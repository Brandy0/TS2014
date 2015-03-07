package com.erhuoapp.erhuo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterImageGalleryViewPager;
import com.erhuoapp.erhuo.model.EntityImage;

import java.util.ArrayList;

/**
 * 图片列表放大界面
 *
 * @author hujiawei
 * @datetime 2015/1/13
 */
public class ActivityImageGallery extends FragmentActivity {

    private int index = 0;
    private ViewPager viewPager;
    private ArrayList<EntityImage> entityImages;

    private LinearLayout focusPoints;
    private ArrayList<ImageView> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        // 实例化控件
        viewPager = (ViewPager) findViewById(R.id.viewpager_image_gallery);
        focusPoints = (LinearLayout) findViewById(R.id.ll_goods_points);

        // 得到传递过来的数据
        index = getIntent().getIntExtra("index", 0);
        entityImages = (ArrayList<EntityImage>) getIntent().getSerializableExtra("entityImages");

        // 遍历列表
        points = new ArrayList<ImageView>();

        //添加相应数目个的小圆点
        for (int i = 0; i < entityImages.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setPadding(0, 0, 10, 0);
            points.add(imageView);
            focusPoints.addView(imageView);//多次加载可能会重复叠加
        }
        //处理viewpager
        updatePoints(index);
        AdapterImageGalleryViewPager adapterGoodsImageViewPager = new AdapterImageGalleryViewPager(this, entityImages);
        viewPager.setAdapter(adapterGoodsImageViewPager);
        viewPager.setCurrentItem(index);// 设置当前图片所在位置
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                updatePoints(position);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

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

    // 按下返回键
    public void keyBack() {
        finish();
    }

}
