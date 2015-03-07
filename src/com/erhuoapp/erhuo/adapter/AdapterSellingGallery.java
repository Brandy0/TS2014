package com.erhuoapp.erhuo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntitySellingImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * 我要卖的界面中图库adapter
 *
 * @author hujiawei
 * @datetime 2015/1/31
 */
public class AdapterSellingGallery extends ArrayAdapter<EntitySellingImage> {

    private List<EntitySellingImage> list;
    private DisplayImageOptions displayImageOptions;

    public AdapterSellingGallery(Context context, int resource, List<EntitySellingImage> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(10))
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).build();
        //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gridview_sellingimage, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 根据手机屏幕的大小适应这里的图片显示的大小 [有效]
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        imageParams.width = dm.widthPixels / 5;//TODO 这里可以考虑使用其他的更加合适的大小
        imageParams.height = imageParams.width;
        viewHolder.imageViewGoods.setLayoutParams(imageParams);

        final EntitySellingImage obj = list.get(position);
        // 根据图片是否是空操作图片来判断点击触发的事件
        if (obj.isStub()) {// 如果是空的操作图片//getCount() < MAX_IMAGE_COUNT && position == getCount()
            viewHolder.imageViewDelete.setVisibility(View.INVISIBLE);
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.selling_image_stub, viewHolder.imageViewGoods, displayImageOptions);
            viewHolder.imageViewGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnStubClicked();
                    }
                }
            });
        } else {// 如果不是空的操作图片
            viewHolder.imageViewDelete.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(obj.getImageUri(), viewHolder.imageViewGoods, displayImageOptions);
            viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemRemoved(obj, position);
                    }
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageViewGoods;
        public final ImageView imageViewDelete;

        public ViewHolder(View view) {
            imageViewGoods = (ImageView) view.findViewById(R.id.iv_image);
            imageViewDelete = (ImageView) view.findViewById(R.id.iv_image_delete);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntitySellingImage obj, int position);

        void onItemRemoved(EntitySellingImage obj, int position);

        void OnStubClicked();
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }
}
