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
    public void addZPopupWindow(ZPopupWindow data) {
        if (mDatas == null)
            mDatas = new ArrayList<>();
        mDatas.add(data);
    }

    // 移除窗体
    public void removeZPopupWindow(ZPopupWindow data) {
        if (mDatas != null) {
            mDatas.remove(data);
            mDatas.clear();
        }
    }

    // 清空数据
    public void clearZPopupWindow() {
        if (mDatas != null) {
            for (ZPopupWindow zPopupWindow : mDatas) {
                if (zPopupWindow != null)
                    zPopupWindow.dismiss();
            }
            mDatas.clear();
        }
    }
}
