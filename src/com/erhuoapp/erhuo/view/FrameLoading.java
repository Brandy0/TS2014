package com.erhuoapp.erhuo.view;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;

/**
 * 自定义的表示正在加载信息的View
 * <p/>
 * （为了代码的可读性，将其单独提出来作为一个类，其实非常类似ViewHolder的机制）
 *
 * @author hujiawei
 * @datetime 15/1/5 16:53
 */
public class FrameLoading {

    private TextView textView;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    private LinearLayout linearLayoutBackground;

    // 创建加载界面
    public FrameLoading(View contentView) {
        imageView = (ImageView) contentView.findViewById(R.id.imageview_frame_loading);
        textView = (TextView) contentView.findViewById(R.id.textview_frame_loading);
        linearLayoutBackground = (LinearLayout) contentView.findViewById(R.id.linearlayout_frame_loading);
        animationDrawable = (AnimationDrawable) imageView.getBackground();// 获取imageview的动画

        linearLayoutBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onFrameLoadingClicked();
                }
            }
        });
    }

    // 创建加载界面
    public FrameLoading(Activity context) {
        this(context.getLayoutInflater().inflate(R.layout.frame_loading, null));
    }

    // 设置信息
    public void setMessage(String str) {
        textView.setText(str);
    }

    /**
     * 下面的每个方法尽量考虑完全，四个对象的状态最好都要有 *
     */

    // 显示动画
    public void showFrame() {
        textView.setText("数据加载中...\n");
        imageView.setVisibility(View.VISIBLE);
        linearLayoutBackground.setVisibility(View.VISIBLE);
        imageView.setBackgroundDrawable(animationDrawable);
        animationDrawable.start();
    }

    // 隐藏动画
    public void hideFrame() {
        linearLayoutBackground.setVisibility(View.INVISIBLE);
        //animationDrawable.stop();
    }

    // 显示信息
    public void showMessage(String msg) {
        linearLayoutBackground.setVisibility(View.VISIBLE);
        textView.setText(msg);
        imageView.setVisibility(View.GONE);//发生错误就不显示图片了
        //animationDrawable.stop();//图片一直显示，只是动画在发生错误的时候不展示
    }

    // 显示没有搜索结果
    public void showNoSearchResult() {
        imageView.setVisibility(View.VISIBLE);
        linearLayoutBackground.setVisibility(View.VISIBLE);
        textView.setText("没有搜到这类物品哦~");
        imageView.setBackgroundResource(R.drawable.search_noresult);
    }

    // 显示没有留言
    public void showNoComment() {
        imageView.setVisibility(View.VISIBLE);
        linearLayoutBackground.setVisibility(View.VISIBLE);
        textView.setText("还没有留言哦~");
        imageView.setBackgroundResource(R.drawable.comment_noresult);
    }

    // 加载界面点击之后触发的事件监听
    public interface FrameLoadingListener {
        void onFrameLoadingClicked();
    }

    // 加载界面点击之后
    private FrameLoadingListener listener;

    public FrameLoadingListener getListener() {
        return listener;
    }

    public void setListener(FrameLoadingListener listener) {
        this.listener = listener;
    }
}
