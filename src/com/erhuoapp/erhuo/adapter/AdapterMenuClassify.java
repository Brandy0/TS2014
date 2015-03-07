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
 * 左侧菜单栏的商品类别列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/6 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterMenuClassify extends ArrayAdapter<EntityGoodsClassify> {

    private DisplayImageOptions displayImageOptions;
    private List<EntityGoodsClassify> list;

    private int currentPosition = 0;

    public AdapterMenuClassify(Context context, int resource, List<EntityGoodsClassify> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.category_others)
                .showImageOnFail(R.drawable.category_others)
                .cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //因为列表项并不多，所以不用考虑复用，但是因为这些view经常性invalid，所以还是复用的比较好
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_classify, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        ImageLoader.getInstance().displayImage(list.get(position).getSmallIcon(), viewHolder.imageView, displayImageOptions);
        viewHolder.textView.setText(list.get(position).getName());

        setViewHolderEffect(position, viewHolder);

        return convertView;
    }

    // 更新类别列表中的各项的显示效果
    // fix：暂时没能搞定点击时改变文本的颜色的效果，待定做何种效果
    private void setViewHolderEffect(int position, ViewHolder viewHolder) {
        if (currentPosition != position) {
            viewHolder.imageView.setAlpha(80);//setImageAlpha需要较高的Android版本支持
            viewHolder.textView.setTextColor(getContext().getResources().getColorStateList(R.color.font_white_alpha));
        } else {//当前选中的菜单项
            viewHolder.imageView.setAlpha(255);//0-255
            viewHolder.textView.setTextColor(getContext().getResources().getColorStateList(R.color.font_alpha_white));
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
