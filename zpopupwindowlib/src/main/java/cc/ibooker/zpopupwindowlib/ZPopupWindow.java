package cc.ibooker.zpopupwindowlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 自定义弹出的PopupWindow，增加半透明蒙层。
 * 实现原理：
 * 在弹出自定义的PopupWindow时，增加一个半透明蒙层view到窗口，并置于PopupWindow下方。
 * 使用方法
 * 继承BottomPushPopupWindow，编写generateCustomView添加自定义的view，调用show方法显示。
 *
 * @author 邹峰立
 */
public abstract class ZPopupWindow extends PopupWindow {
    private Context context;
    private WindowManager wm;
    private View maskView;
    private int maskHeight;
    private int maskGravity = Gravity.CENTER | Gravity.TOP;
    private boolean isOpenManager;// 是否打开PopupWindow管理，默认打开
    private boolean isOpenMutex;// 是否清空已有PopupWindow，互斥，默认开启
    private boolean isOpenRegReceiver;// 是否开启注册广播，默认开启
    private int maskViewBackColor = 0x9f000000;

    public ZPopupWindow setOpenMutex(boolean openMutex) {
        isOpenMutex = openMutex;
        return this;
    }

    public ZPopupWindow setOpenManager(boolean openManager) {
        isOpenManager = openManager;
        return this;
    }

    public ZPopupWindow setOpenRegReceiver(boolean openRegReceiver) {
        isOpenRegReceiver = openRegReceiver;
        if (!isOpenRegReceiver) {
            unRegReceiver();
        }
        return this;
    }

    public ZPopupWindow(Context context) {
        this(context, true);
    }

    public ZPopupWindow(Context context, boolean isOpenManager) {
        this(context, isOpenManager, true, false);
    }

    public ZPopupWindow(Context context, boolean isOpenManager, boolean isOpenMutex, boolean isOpenRegReceiver, int maskViewBackColor) {
        this(context, isOpenManager, isOpenMutex, isOpenRegReceiver);
        this.maskViewBackColor = maskViewBackColor;
    }

    public ZPopupWindow(Context context, boolean isOpenManager, boolean isOpenMutex, boolean isOpenRegReceiver) {
        super(context);
        this.context = context;
        this.maskHeight = getScreenH(context);
        this.wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setContentView(generateCustomView(context));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
//        setClippingEnabled(false);
        setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        setAnimationStyle(R.style.ZPopupWindow_BottomPushPopupWindow);
        // 关闭所有ZPopupWindow
        this.isOpenManager = isOpenManager;
        this.isOpenMutex = isOpenMutex;
        this.isOpenRegReceiver = isOpenRegReceiver;
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

    // 设置遮罩层颜色
    public ZPopupWindow setMaskViewBackColor(int color) {
        this.maskViewBackColor = color;
        return this;
    }

    // 抽象方法
    protected abstract View generateCustomView(Context context);

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        addMaskView(parent.getWindowToken(), maskHeight, maskGravity);
        super.showAtLocation(parent, gravity, x, y);
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
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        addMaskView(anchor.getWindowToken(), maskHeight, maskGravity);
        super.showAsDropDown(anchor, xoff, yoff);
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
    public void showAsDropDown(View anchor) {
        addMaskView(anchor.getWindowToken(), maskHeight, maskGravity);
        super.showAsDropDown(anchor);
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
    public void setAnimationStyle(int animationStyle) {
        super.setAnimationStyle(animationStyle);
    }

    @Override
    public void dismiss() {
        try {
            if (receiver != null && context != null) {// 注销广播
                context.unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 刷新界面
        removeMaskView();
        super.dismiss();
        if (isOpenManager)
            ZPopupWindowUtil.getInstance().removeZPopupWindow(this);
    }

    /**
     * 显示在界面的底部
     */
    public void showBottom() {
        if (context != null && !isShowing())
            showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示在界面的顶部
     */
    public void showTop() {
        if (context != null && !isShowing())
            showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示在指定View的正上方
     *
     * @param view    指定的View
     * @param yOffset Y轴偏移量
     */
    public void showViewTop(View view, int yOffset) {
        if (context != null && !isShowing()) {
            this.maskHeight = getScreenH(context) - view.getHeight() - yOffset;
            // 获取需要在其上方显示的控件的位置信息
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            // 获取自身的长宽高
            this.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = this.getContentView().getMeasuredHeight();
            int popupWidth = this.getContentView().getMeasuredWidth();
            //在控件上方显示
            maskGravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + popupWidth / 2, location[1] - popupHeight - yOffset);
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
            this.maskHeight = getScreenH(context) - getStatusHeight((Activity) context) - view.getHeight() - yOffset;
            maskGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
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

    // 添加遮罩层
    private void addMaskView(IBinder token, int maskHeight, int gravity) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = maskHeight;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        params.token = token;
//        params.windowAnimations = android.R.style.Animation_Toast;
        params.gravity = gravity;
        params.x = 0;
        params.y = 0;
        maskView = new View(context);
        maskView.setBackgroundColor(maskViewBackColor);
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    if (onBackPressListener != null)
                        onBackPressListener.onBackPress();
                    else
                        removeMaskView();
                    return true;
                }
                return false;
            }
        });
        wm.addView(maskView, params);
    }

    // 移除遮罩层
    private void removeMaskView() {
        if (maskView != null && maskView.getWindowToken() != null && wm != null) {
            wm.removeViewImmediate(maskView);
            maskView = null;
        }
    }

    // 设置点击外侧事件
    public ZPopupWindow setOutsideTouch(boolean able) {
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

    /**
     * 获取屏幕高度（包括状态栏的高度） - px
     */
    private int getScreenH(Context aty) {
        WindowManager wm = (WindowManager) aty.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.heightPixels;
    }

    /**
     * 获取状态栏高度
     */
    private int getStatusHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public interface OnBackPressListener {
        void onBackPress();
    }

    private OnBackPressListener onBackPressListener;

    public void setOnBackPressListener(OnBackPressListener onBackPressListener) {
        this.onBackPressListener = onBackPressListener;
    }
}