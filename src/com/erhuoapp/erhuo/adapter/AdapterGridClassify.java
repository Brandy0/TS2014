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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

/**
 * 首页的商品类别Adapter
 *
 * @author hujiawei
 * @datetime 15/1/5 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterGridClassify extends ArrayAdapter<EntityGoodsClassify> {

    private DisplayImageOptions displayImageOptions;
    private List<EntityGoodsClassify> list;

    public AdapterGridClassify(Context context, int resource, List<EntityGoodsClassify> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.classify_other)
                .showImageOnFail(R.drawable.classify_other)
                .cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gridview_classify, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        ImageLoader.getInstance().displayImage(list.get(position).getIcon(), viewHolder.imageView, displayImageOptions);
        viewHolder.textView.setText(list.get(position).getName());

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageview_classify);
            textView = (TextView) view.findViewById(R.id.textview_classify);
        }
    }

}
