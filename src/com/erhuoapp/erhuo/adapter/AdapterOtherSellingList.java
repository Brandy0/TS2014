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
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * 个人主页中的出售商品列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/17
 */
@SuppressLint("UseSparseArrays")
public class AdapterOtherSellingList extends ArrayAdapter<EntityGoodsSelling> {

    private List<EntityGoodsSelling> list;
    private DisplayImageOptions displayImageOptions;

    public AdapterOtherSellingList(Context context, int resource, List<EntityGoodsSelling> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.goods_default)
                .showImageOnFail(R.drawable.goods_default)
                .showImageOnLoading(R.drawable.goods_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_otherselling, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        final EntityGoodsSelling obj = list.get(position);
        ImageLoader.getInstance().displayImage(obj.getImage(), viewHolder.imageViewGoods, displayImageOptions);

        viewHolder.textViewTitle.setText(obj.getTitle());
        viewHolder.textViewContent.setText(obj.getContent());
        viewHolder.textViewCollect.setText(obj.getCollectNum());
        viewHolder.textViewComment.setText(obj.getCommentNum());
        viewHolder.textViewTime.setText(obj.getTime());
        viewHolder.textViewPrice.setText("￥" + obj.getPrice());

        if (obj.isNew()){
            viewHolder.imageViewNew.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imageViewNew.setVisibility(View.GONE);
        }
        if (obj.isBargin()){
            viewHolder.imageViewBargin.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imageViewBargin.setVisibility(View.GONE);
        }

        // 事件监听
        viewHolder.imageViewGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 跳转到对应商品的详情界面
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemClicked(obj, position);
                }
            }
        });

        viewHolder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 跳转到对应商品的详情界面
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemClicked(obj, position);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageViewGoods;
        public final ImageView imageViewNew;
        public final ImageView imageViewBargin;
        public final TextView textViewTitle;
        public final TextView textViewContent;
        public final TextView textViewCollect;
        public final TextView textViewComment;
        public final TextView textViewTime;
        public final TextView textViewPrice;

        public ViewHolder(View view) {
            imageViewGoods = (ImageView) view.findViewById(R.id.iv_goods_image);
            imageViewNew = (ImageView) view.findViewById(R.id.iv_goods_new);
            imageViewBargin = (ImageView) view.findViewById(R.id.iv_goods_bargin);
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_title);
            textViewContent = (TextView) view.findViewById(R.id.tv_goods_content);
            textViewCollect = (TextView) view.findViewById(R.id.tv_goods_collectnum);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntityGoodsSelling obj, int position);
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }

}
