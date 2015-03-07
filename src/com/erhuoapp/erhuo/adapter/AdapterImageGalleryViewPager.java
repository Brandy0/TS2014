package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityGoodsSellingInfo;
import com.erhuoapp.erhuo.activity.ActivityImageGallery;
import com.erhuoapp.erhuo.model.EntityImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情中的图片点击之后进入的图片列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/13
 */
@SuppressLint("UseSparseArrays")
public class AdapterImageGalleryViewPager extends PagerAdapter {

    private DisplayImageOptions displayImageOptions;
    private ArrayList<EntityImage> list;
    private List<ImageView> imageViews;

    public AdapterImageGalleryViewPager(final Context context, ArrayList<EntityImage> objects) {
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.goods_default)
                .showImageOnFail(R.drawable.goods_default)
                .showImageOnLoading(R.drawable.goods_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        this.imageViews = new ArrayList<ImageView>();
        for (final EntityImage obj : objects) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageLoader.getInstance().displayImage(obj.getImage(), imageView, displayImageOptions);
            imageViews.add(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//点击焦点图的事件监听
                	ActivityImageGallery temp_A =  ( ActivityImageGallery )context;
                	temp_A.keyBack();
                }
            });
        }
        //添加点击图片返回监听
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imageViews.get(position));
        return imageViews.get(position);
    }

}
