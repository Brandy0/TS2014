package com.erhuoapp.erhuo.view;

/**
 * @author hujiawei
 * @datatime 15/1/2 22:54
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 主界面对应的ViewPager，屏蔽了它的滑动效果
 *
 * @author hujiawei
 * @datetime 15/1/1 23:10
 */
public class MainViewPager extends ViewPager {

    private float x, moveX;
    private boolean isScrollable = false;

    public MainViewPager(Context context) {
        super(context);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isScrollable) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScrollable) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }
}
