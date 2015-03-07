package com.erhuoapp.erhuo.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;

/**
 * 自定义的表示正在等待服务器返回数据的View
 *
 * 类似FrameLoading，只是两个界面略有不同，放在不同的加载等待场合中使用
 *
 * @author hujiawei
 * @datetime 15/1/14 16:53
 */
public class FrameWaiting {

    private TextView textView;
    private LinearLayout linearLayoutBackground;

    // 创建加载界面
    public FrameWaiting(View contentView) {
        textView = (TextView) contentView.findViewById(R.id.textview_frame_waiting);
        linearLayoutBackground = (LinearLayout) contentView.findViewById(R.id.linearlayout_frame_waiting);
    }

    /**
     * 隐藏动画
     */
    public void hideFrame() {
        linearLayoutBackground.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示信息
     */
    public void showMessage(String msg) {
        textView.setText(msg);
        linearLayoutBackground.setVisibility(View.VISIBLE);
    }
}
