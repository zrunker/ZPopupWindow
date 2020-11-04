package cc.ibooker.zpopupwindowlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 自定义弹出的PopupWindow，半透明。
 * 使用方法:
 * 继承BottomPushPopupWindow，编写generateCustomView添加自定义的view，调用show方法显示。
 *
 * @author 邹峰立
 */
public abstract class ZPopupWindow2 extends PopupWindow {
    private Context context;
    private boolean isOpenManager;// 是否打开PopupWindow管理，默认打开
    private boolean isOpenMutex;// 是否清空已有PopupWindow，互斥，默认开启
    private boolean isOpenRegReceiver;// 是否开启注册广播，默认开启
    private int maskViewBackColor;// 默认背景颜色 - 0x9f000000
    private float alpha = 0.5f;// 默认透明度

    public ZPopupWindow2 setOpenMutex(boolean openMutex) {
        isOpenMutex = openMutex;
        return this;
    }

    public ZPopupWindow2 setOpenManager(boolean openManager) {
        isOpenManager = openManager;
        return this;
    }

    public ZPopupWindow2 setOpenRegReceiver(boolean openRegReceiver) {
        isOpenRegReceiver = openRegReceiver;
        if (!isOpenRegReceiver) {
            unRegReceiver();
        }
        return this;
    }

    public ZPopupWindow2(@NonNull Context context) {
        this(context, true);
    }

    public ZPopupWindow2(@NonNull Context context, boolean isOpenManager) {
        this(context, isOpenManager, true, false);
    }

    public ZPopupWindow2(@NonNull Context context, boolean isOpenManager, boolean isOpenMutex, boolean isOpenRegReceiver) {
        this(context, isOpenManager, isOpenMutex, isOpenRegReceiver, 0x000000);
    }

    public ZPopupWindow2(@NonNull Context context, boolean isOpenManager, boolean isOpenMutex, boolean isOpenRegReceiver, int maskViewBackColor) {
        super(context);
        this.context = context;
        this.isOpenManager = isOpenManager;
        this.isOpenMutex = isOpenMutex;
        this.isOpenRegReceiver = isOpenRegReceiver;
        this.maskViewBackColor = maskViewBackColor;
        setContentView(generateCustomView(context));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setClippingEnabled(false);
        setAnimationStyle(android.R.style.Animation_Toast);
        // PopupWindow管理
        if (isOpenManager && isOpenMutex)
            ZPopupWindowUtil.getInstance().clearZPopupWindowsKeepThis(this);
        // 添加到管理类
        if (isOpenManager)
            ZPopupWindowUtil.getInstance().addZPopupWindow(this);
        // 注册相应广播
        unRegReceiver();
        if (isOpenRegReceiver)
            regReceiver();
    }

    // 抽象方法
    protected abstract View generateCustomView(Context context);

    // 设置PopupWindow的背景透明度
    public ZPopupWindow2 setBackgroundAlpha(float alpha) {
        if (context != null) {
            Window window = ((Activity) context).getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = alpha;
                if (alpha == 1) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                } else {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                window.setAttributes(lp);
            }
        }
        return this;
    }

    // 设置遮罩层颜色
    public ZPopupWindow2 setMaskViewBackColor(int color) {
        this.maskViewBackColor = color;
        setBackgroundDrawable(new ColorDrawable(maskViewBackColor));
        return this;
    }

    // 设置透明度
    public ZPopupWindow2 setAlpha(float alpha) {
        this.alpha = alpha;
        setBackgroundAlpha(alpha);
        return this;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        showBefore();
        super.showAtLocation(parent, gravity, x, y);
        showAfter();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showBefore();
        super.showAsDropDown(anchor, xoff, yoff);
        showAfter();
    }

    @Override
    public void showAsDropDown(View anchor) {
        showBefore();
        super.showAsDropDown(anchor);
        showAfter();
    }

    // 展示之前
    private void showBefore() {
        setBackgroundDrawable(new ColorDrawable(maskViewBackColor));
        setBackgroundAlpha(alpha);
    }

    // 展示之后
    private void showAfter() {
        if (isOpenManager) {
            ZPopupWindowUtil.getInstance().addZPopupWindow(this);
            if (isOpenMutex)
                ZPopupWindowUtil.getInstance().clearZPopupWindowsKeepThis(this);
        }
        // 注册广播
        if (isOpenRegReceiver)
            regReceiver();
    }

    @Override
    public void dismiss() {
        setBackgroundAlpha(1f);
        try {
            if (receiver != null && context != null) {// 注销广播
                context.unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dismiss();
        if (isOpenManager)
            ZPopupWindowUtil.getInstance().removeZPopupWindow(this);
    }

    /**
     * 显示在界面的底部
     */
    public void showBottom() {
        if (context != null && !isShowing()) {
            setAnimationStyle(R.style.ZPopupWindow_BottomPushPopupWindow);
            showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    /**
     * 显示在界面的顶部
     */
    public void showTop() {
        if (context != null && !isShowing()) {
            setAnimationStyle(R.style.ZPopupWindow_TopPushPopupWindow);
            showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    /**
     * 显示在指定View的正上方
     *
     * @param view    指定的View
     * @param yOffset Y轴偏移量
     */
    public void showViewTop(View view, int yOffset) {
        if (context != null && !isShowing()) {
            // 获取需要在其上方显示的控件的位置信息
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            // 获取自身的长宽高
            this.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = this.getContentView().getMeasuredHeight();
//            int popupWidth = this.getContentView().getMeasuredWidth();
            //在控件上方显示
            showAtLocation(view, Gravity.NO_GRAVITY, 0, location[1] - popupHeight - yOffset);
        }
    }

    /**
     * 显示在指定View的正下方
     *
     * @param view    指定的View
     * @param yOffset Y轴偏移量
     */
    public void showViewBottom(View view, int yOffset) {
//        this.maskHeight = getScreenH(context) - getStatusHeight((Activity) context) - view.getHeight() - yOffset;
//        // 获取需要在其上方显示的控件的位置信息
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        // 获取自身的长宽高
//        this.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
////        int popupHeight = this.getContentView().getMeasuredHeight();
//        int popupWidth = this.getContentView().getMeasuredWidth();
//        // 在控件上方显示
//        maskGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        showAtLocation(view, Gravity.NO_GRAVITY, (location[0]) + popupWidth / 2, location[1] + view.getHeight() + yOffset);
        if (context != null && !isShowing()) {
            showAsDropDown(view, 0, yOffset);
        }
    }

    // 注册广播接收器，接收暗屏广播，锁屏广播
    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(receiver, filter);
    }

    // 反注册广播
    public void unRegReceiver() {
        if (receiver != null && context != null)
            try {
                context.unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // 定义一个广播接收器，帮助关闭PopupWindow
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
                dismiss();
            }
        }
    };

    // 设置点击外侧事件
    public ZPopupWindow2 setOutsideTouch(boolean able) {
        setOutsideTouchable(able);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isOutsideTouchable()) {
                    View mView = getContentView();
                    if (null != mView)
                        mView.dispatchTouchEvent(event);
                }
                return isFocusable() && !isOutsideTouchable();
            }
        });
        return this;
    }

}