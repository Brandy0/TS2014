package com.erhuoapp.erhuo.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.IConstants;

import java.util.ArrayList;

/**
 * 引导界面
 *
 * @author hujiawei
 * @datetime 15/1/1 23:10
 */
public class ActivityGuide extends FragmentActivity implements ViewPager.OnPageChangeListener, IConstants {

    private ArrayList<View> views = new ArrayList<View>();
    //private int[] images = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private int[] images = {R.drawable.newguide1, R.drawable.newguide2, R.drawable.newguide3};

    private LinearLayout focusPoints;
    private ArrayList<ImageView> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 分界面对应的图片
        focusPoints = (LinearLayout) findViewById(R.id.ll_guide_points);
        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(image);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            views.add(imageView);
        }

        // 引导页的最后一页
        View lastView  = getLayoutInflater().inflate(R.layout.layout_guide_last, null);
        lastView.findViewById(R.id.ib_guide_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.getInstance().saveKeyValue(AppUtil.PREF_KEY_FIRST, false);//标识为不再是第一次进入了
                AppUtil.getInstance().saveKeyValue(AppUtil.PREF_KEY_LOGIN, false);//第一次进入，还没有登录信息
                setResult(RESULT_OK);
                finish();
            }
        });
        views.add(lastView);

        // 添加相应数目个的小圆点
        points = new ArrayList<ImageView>();
        for (int i = 0; i < views.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setPadding(0, 0, 10, 0);
            points.add(imageView);
            focusPoints.addView(imageView);//多次加载可能会重复叠加
        }
        updatePoints(0);//默认进入的时候选择第一个
        // viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_guide);
        viewPager.setAdapter(new GuidePagerAdapter());
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * ViewPager适配器
     */
    public class GuidePagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    ///// OnPageChangeListener /////

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int id) {
        updatePoints(id);
    }

    ///// OnPageChangeListener /////

    // 更新小圆点
    private void updatePoints(int position) {
        for (int i = 0; i < points.size(); i++) {
            if (position == i) {
                points.get(i).setImageResource(R.drawable.round_point);//chosen
            } else {
                points.get(i).setImageResource(R.drawable.round_point_normal);
            }
        }
    }

    // 处理该界面的返回键或者其他键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                keyBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);//
    }

    public void keyBack() {//按下返回键
        setResult(RESULT_CANCELED);//在引导界面就取消返回了的话就退出
        finish();
    }


}
