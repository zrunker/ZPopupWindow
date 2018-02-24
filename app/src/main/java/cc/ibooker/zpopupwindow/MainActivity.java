package cc.ibooker.zpopupwindow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DiyPopupWindow diyPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 从屏幕顶部弹出
        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diyPopupWindow == null) {
                    diyPopupWindow = new DiyPopupWindow(MainActivity.this);
                }
                diyPopupWindow.setAnimationStyle(cc.ibooker.zpopupwindowlib.R.style.TopPushPopupWindow);
                diyPopupWindow.showTop();
            }
        });

        // 屏幕底部弹出
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diyPopupWindow == null) {
                    diyPopupWindow = new DiyPopupWindow(MainActivity.this);
                }
                diyPopupWindow.setAnimationStyle(cc.ibooker.zpopupwindowlib.R.style.BottomPushPopupWindow);
                diyPopupWindow.showBottom();
            }
        });

        // 当前控件正上方弹出
        findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diyPopupWindow == null) {
                    diyPopupWindow = new DiyPopupWindow(MainActivity.this);
                }
                diyPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);
                diyPopupWindow.showViewTop(findViewById(R.id.tv3), 0);
            }
        });

        // 当前控件正下方弹出
        findViewById(R.id.tv4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diyPopupWindow == null) {
                    diyPopupWindow = new DiyPopupWindow(MainActivity.this);
                }
                diyPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);
                diyPopupWindow.showViewBottom(findViewById(R.id.tv4), 0);
            }
        });
    }
}
