package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityGoodsClassify;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 首页的商品类别Adapter
 *
 * @author hujiawei
 * @datetime 15/1/5 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterSellingClassify extends ArrayAdapter<EntityGoodsClassify> {

    private DisplayImageOptions displayImageOptions;
    private List<EntityGoodsClassify> list;
    private int currentPosition = -1;

    public AdapterSellingClassify(Context context, int resource, List<EntityGoodsClassify> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.selling_classify_other)
                .showImageOnFail(R.drawable.selling_classify_other)
                .cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gridview_sellingclassify, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder.textView.setText(list.get(position).getName());
        setViewHolderEffect(position, viewHolder);

        return convertView;
    }

    private void setViewHolderEffect(int position, ViewHolder viewHolder) {
        if (currentPosition != position) {
            ImageLoader.getInstance().displayImage(list.get(position).getPostIcon(), viewHolder.imageView, displayImageOptions);
            //viewHolder.imageView.setAlpha(80);//setImageAlpha需要较高的Android版本支持
            //viewHolder.textView.setTextColor(getContext().getResources().getColorStateList(R.color.font_white_alpha));
        } else {//当前选中的菜单项
            ImageLoader.getInstance().displayImage(list.get(position).getPostIconActive(), viewHolder.imageView, displayImageOptions);
            //viewHolder.imageView.setAlpha(255);//0-255
            //viewHolder.textView.setTextColor(getContext().getResources().getColorStateList(R.color.font_alpha_white));
        }
    }

    private static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageview_classify);
            textView = (TextView) view.findViewById(R.id.textview_classify);
        }
    }

    // currentPosition
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

}
