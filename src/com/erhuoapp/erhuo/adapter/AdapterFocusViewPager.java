package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityGoodsFocus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页的商品类别Adapter
 *
 * @author hujiawei
 * @datetime 15/1/6 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterFocusViewPager extends PagerAdapter {

    private DisplayImageOptions displayImageOptions;
    private List<EntityGoodsFocus> list;
    private List<ImageView> imageViews;

    public AdapterFocusViewPager(final Context context, List<EntityGoodsFocus> objects) {
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.goods_default)
                .showImageOnFail(R.drawable.goods_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        this.imageViews = new ArrayList<ImageView>();
        for (final EntityGoodsFocus obj : objects) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//or CENTER_CROP //CENTER_INSIDE x FIT_CENTER x CENTER x
            ImageLoader.getInstance().displayImage(obj.getImage(), imageView, displayImageOptions);
            imageViews.add(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//点击焦点图的事件监听
                    if (listener!=null){
                        listener.onItemClicked(obj);
                    }
                }
            });
        }
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

    // 图片点击事件监听
    public interface ItemClickedListener{
        void onItemClicked(EntityGoodsFocus obj);
    }
    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }
}
