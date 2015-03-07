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
import com.erhuoapp.erhuo.model.EntityComment;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
* 商品详情界面中的评论列表Adapter
*
* @author hujiawei
* @datetime 15/1/25
*/
@SuppressLint("UseSparseArrays")
public class AdapterGoodsCommentList extends ArrayAdapter<EntityComment> {

    private final String TAG = "AdapterGoodsCommentList";

    private List<EntityComment> list;
    private DisplayImageOptions displayImageOptions_header;

    public AdapterGoodsCommentList(Context context, int resource, List<EntityComment> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions_header = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.header_default)
                .showImageOnFail(R.drawable.header_default)
                .showImageOnLoading(R.drawable.header_default)
                .cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100, 3)).build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_comment, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        final EntityComment obj = list.get(position);
        ImageLoader.getInstance().displayImage(obj.getUserHeader(), viewHolder.imageViewHeader, displayImageOptions_header);

        viewHolder.textViewName.setText(obj.getUserName());
        viewHolder.textViewContent.setText(obj.getContent());
        viewHolder.textViewTime.setText(obj.getTime());

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageViewHeader;
        public final TextView textViewName;
        public final TextView textViewTime;
        public final TextView textViewContent;

        public ViewHolder(View view) {
            imageViewHeader = (ImageView) view.findViewById(R.id.iv_comment_header);
            textViewName = (TextView) view.findViewById(R.id.tv_comment_name);
            textViewTime = (TextView) view.findViewById(R.id.tv_comment_time);
            textViewContent = (TextView) view.findViewById(R.id.tv_comment_content);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntityGoodsSelling obj, int position);
        void onItemRemoved(EntityGoodsSelling obj, int position);
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }

    // 数据列表
    public List<EntityComment> getList() {
        return list;
    }

    public void setList(List<EntityComment> list) {
        this.list = list;
    }
}
