package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
public class AdapterUserSellingList extends ArrayAdapter<EntityGoodsSelling> {

    private List<EntityGoodsSelling> list;
    private DisplayImageOptions displayImageOptions;

    public AdapterUserSellingList(Context context, int resource, List<EntityGoodsSelling> objects) {
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
        final ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_userselling, parent, false);
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
        viewHolder.layoutActions.setVisibility(View.INVISIBLE);

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

        viewHolder.imageButtonMore.setOnClickListener(new View.OnClickListener() {//更多操作
            @Override
            public void onClick(View v) {//交给监听者来处理这个事件
                viewHolder.layoutActions.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.layoutActions.setVisibility(View.INVISIBLE);
            }
        });

        viewHolder.textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.layoutActions.setVisibility(View.INVISIBLE);
                if (listener!=null){
                    listener.onItemRemoved(obj, position);
                }
            }
        });

        viewHolder.textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.layoutActions.setVisibility(View.INVISIBLE);
                if (listener!=null){
                    listener.onItemEdit(obj, position);
                }
            }
        });

        viewHolder.textViewSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.layoutActions.setVisibility(View.INVISIBLE);
                if (listener!=null){
                    listener.onItemSold(obj, position);
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
        public final ImageButton imageButtonMore;
        public final ImageButton imageButtonClose;
        public final FrameLayout layoutActions;
        public final TextView textViewDelete;
        public final TextView textViewEdit;
        public final TextView textViewSold;

        public ViewHolder(View view) {
            imageViewGoods = (ImageView) view.findViewById(R.id.iv_goods_image);
            imageViewNew = (ImageView) view.findViewById(R.id.iv_goods_new);
            imageButtonMore = (ImageButton) view.findViewById(R.id.ib_goods_more);
            imageViewBargin = (ImageView) view.findViewById(R.id.iv_goods_bargin);
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_title);
            textViewContent = (TextView) view.findViewById(R.id.tv_goods_content);
            textViewCollect = (TextView) view.findViewById(R.id.tv_goods_collectnum);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
            imageButtonClose = (ImageButton) view.findViewById(R.id.ib_goods_close);
            layoutActions = (FrameLayout) view.findViewById(R.id.fl_goods_actions);
            textViewDelete = (TextView) view.findViewById(R.id.tv_goods_delete);
            textViewEdit = (TextView) view.findViewById(R.id.tv_goods_edit);
            textViewSold = (TextView) view.findViewById(R.id.tv_goods_sold);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntityGoodsSelling obj, int position);
        void onItemRemoved(EntityGoodsSelling obj, int position);
        void onItemEdit(EntityGoodsSelling obj, int position);
        void onItemSold(EntityGoodsSelling obj, int position);
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }

}
