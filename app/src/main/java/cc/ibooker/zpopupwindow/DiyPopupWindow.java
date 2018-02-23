package cc.ibooker.zpopupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cc.ibooker.zpopupwindowlib.ZPopupWindow;

/**
 * 实现PopupWindow
 * Created by 邹峰立 on 2018/2/23.
 */
public class DiyPopupWindow extends ZPopupWindow {

    public DiyPopupWindow(Context context) {
        super(context);
    }

    @Override
    protected View generateCustomView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.layout_popupwindow, null, false);
    }
}
