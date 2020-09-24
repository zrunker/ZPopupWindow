package cc.ibooker.zpopupwindowlib;

import android.widget.PopupWindow;

import java.util.HashSet;

/**
 * ZPopupWindow管理类
 * Created by 邹峰立 on 2017/3/24.
 */
public class ZPopupWindowUtil {
    private HashSet<PopupWindow> mList = new HashSet<>();

    private static ZPopupWindowUtil bottomPushPopupWindowUtil;

    public static ZPopupWindowUtil getInstance() {
        if (bottomPushPopupWindowUtil == null)
            bottomPushPopupWindowUtil = new ZPopupWindowUtil();
        return bottomPushPopupWindowUtil;
    }

    public HashSet<PopupWindow> getList() {
        return mList;
    }

    public void setList(HashSet<PopupWindow> mList) {
        this.mList = mList;
    }

    // 添加窗体
    public synchronized void addZPopupWindow(PopupWindow zPopupWindow) {
        if (mList == null)
            mList = new HashSet<>();
        if (zPopupWindow != null)
            mList.add(zPopupWindow);
    }

    // 移除窗体
    public synchronized void removeZPopupWindow(PopupWindow zPopupWindow) {
        if (mList != null && zPopupWindow != null) {
            if (zPopupWindow.isShowing())
                zPopupWindow.dismiss();
            mList.remove(zPopupWindow);
        }
    }

    // 清空数据
    public synchronized void clearZPopupWindows() {
        if (mList != null) {
            for (PopupWindow zPopupWindow : mList) {
                if (zPopupWindow != null)
                    zPopupWindow.dismiss();
            }
            mList.clear();
        }
    }

    // 保留当前PopupWindow，移除其他所有
    public synchronized void clearZPopupWindowsKeepThis(PopupWindow zPopupWindow) {
        if (mList != null) {
            for (PopupWindow data : mList) {
                if (data != null && data != zPopupWindow
                        && !data.equals(zPopupWindow) && mList.contains(data)) {
                    data.dismiss();
                    mList.remove(data);
                }
            }
        }
    }
}