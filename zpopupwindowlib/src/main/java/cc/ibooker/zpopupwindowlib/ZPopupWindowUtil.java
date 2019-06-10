package cc.ibooker.zpopupwindowlib;

import java.util.ArrayList;

/**
 * ZPopupWindow管理类
 * Created by 邹峰立 on 2017/3/24.
 */
public class ZPopupWindowUtil {
    private ArrayList<ZPopupWindow> mDatas = new ArrayList<>();

    private static ZPopupWindowUtil bottomPushPopupWindowUtil;

    public static ZPopupWindowUtil getInstance() {
        if (bottomPushPopupWindowUtil == null)
            bottomPushPopupWindowUtil = new ZPopupWindowUtil();
        return bottomPushPopupWindowUtil;
    }

    public ArrayList<ZPopupWindow> getmDatas() {
        return mDatas;
    }

    public void setmDatas(ArrayList<ZPopupWindow> mDatas) {
        this.mDatas = mDatas;
    }

    // 添加窗体
    public synchronized void addZPopupWindow(ZPopupWindow zPopupWindow) {
        if (mDatas == null)
            mDatas = new ArrayList<>();
        if (zPopupWindow != null && !mDatas.contains(zPopupWindow))
            mDatas.add(zPopupWindow);
    }

    // 移除窗体
    public synchronized void removeZPopupWindow(ZPopupWindow zPopupWindow) {
        if (mDatas != null && zPopupWindow != null) {
            if (zPopupWindow.isShowing())
                zPopupWindow.dismiss();
            mDatas.remove(zPopupWindow);
        }
    }

    // 清空数据
    public synchronized void clearZPopupWindows() {
        if (mDatas != null) {
            for (ZPopupWindow zPopupWindow : mDatas) {
                if (zPopupWindow != null)
                    zPopupWindow.dismiss();
            }
            mDatas.clear();
        }
    }

    // 保留当前PopupWindow，移除其他所有
    public synchronized void clearZPopupWindowsKeepThis(ZPopupWindow zPopupWindow) {
        if (mDatas != null) {
            for (int i = 0; i < mDatas.size(); i++) {
                ZPopupWindow data = mDatas.get(i);
                if (data != null && data != zPopupWindow && mDatas.contains(data)) {
                    data.dismiss();
                    mDatas.remove(data);
                }
            }
        }
    }
}