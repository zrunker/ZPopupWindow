package cc.ibooker.zpopupwindowlib;

import java.util.ArrayList;

/**
 * ZPopupWindow管理类
 * Created by 邹峰立 on 2017/3/24.
 */
public class ZPopupWindowUtil {
    private ArrayList<ZPopupWindow> mList = new ArrayList<>();

    private static ZPopupWindowUtil bottomPushPopupWindowUtil;

    public static ZPopupWindowUtil getInstance() {
        if (bottomPushPopupWindowUtil == null)
            bottomPushPopupWindowUtil = new ZPopupWindowUtil();
        return bottomPushPopupWindowUtil;
    }

    public ArrayList<ZPopupWindow> getmList() {
        return mList;
    }

    public void setmList(ArrayList<ZPopupWindow> mList) {
        this.mList = mList;
    }

    // 添加窗体
    public synchronized void addZPopupWindow(ZPopupWindow zPopupWindow) {
        if (mList == null)
            mList = new ArrayList<>();
        if (zPopupWindow != null && !mList.contains(zPopupWindow))
            mList.add(zPopupWindow);
    }

    // 移除窗体
    public synchronized void removeZPopupWindow(ZPopupWindow zPopupWindow) {
        if (mList != null && zPopupWindow != null) {
            if (zPopupWindow.isShowing())
                zPopupWindow.dismiss();
            mList.remove(zPopupWindow);
        }
    }

    // 清空数据
    public synchronized void clearZPopupWindows() {
        if (mList != null) {
            for (ZPopupWindow zPopupWindow : mList) {
                if (zPopupWindow != null)
                    zPopupWindow.dismiss();
            }
            mList.clear();
        }
    }

    // 保留当前PopupWindow，移除其他所有
    public synchronized void clearZPopupWindowsKeepThis(ZPopupWindow zPopupWindow) {
        if (mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                ZPopupWindow data = mList.get(i);
                if (data != null && data != zPopupWindow && mList.contains(data)) {
                    data.dismiss();
                    mList.remove(data);
                }
            }
        }
    }
}