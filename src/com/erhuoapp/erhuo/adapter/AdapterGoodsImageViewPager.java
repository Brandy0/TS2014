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
import com.erhuoapp.erhuo.activity.ActivityImageGallery;
import com.erhuoapp.erhuo.model.EntityImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情中的图片列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/8
 */
@SuppressLint("UseSparseArrays")
public class AdapterGoodsImageViewPager extends PagerAdapter {

    private Context context;
    private DisplayImageOptions displayImageOptions;
    private ArrayList<EntityImage> list;
    private List<ImageView> imageViews;

    public AdapterGoodsImageViewPager(final Context context, final ArrayList<EntityImage> objects) {
        this.context = context;
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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(obj.getImage(), imageView, displayImageOptions);
            imageViews.add(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//点击焦点图的事件监听
                    Intent intent = new Intent(context, ActivityImageGallery.class);
                    intent.putExtra("index", objects.indexOf(obj));
                    intent.putExtra("entityImages", objects);//ArrayList，而不能是List
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }
    
    //获取图片Bitmap
    public Bitmap getBtmItem(int arg0) {
    	Bitmap btm_temp;
    	ImageView iv_temp = imageViews.get(arg0);
    	iv_temp.setDrawingCacheEnabled(true);
    	btm_temp = iv_temp.getDrawingCache();
    	iv_temp.setDrawingCacheEnabled(false);
    	return btm_temp;
    }
    
  //获取图片url
    public String getStrItem(int arg0){
    	return list.get(arg0).getImage();
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
