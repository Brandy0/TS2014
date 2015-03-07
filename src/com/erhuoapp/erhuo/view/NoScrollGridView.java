package com.erhuoapp.erhuo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 *
 * 自定义的不能滚动的Gridview
 * 当gridview嵌套在scrollview中的时候gridview会显示不全
 * http://blog.csdn.net/zshshuai/article/details/7345317
 *
 * @author hujiawei
 * @datetime 2015/1/28
 */
public class NoScrollGridView extends GridView {


    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }  


    public NoScrollGridView(Context context) {
        super(context);  
    }  


    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);  
    }  


    @Override  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
}  