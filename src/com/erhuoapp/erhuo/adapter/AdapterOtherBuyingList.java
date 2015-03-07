package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;

import java.util.List;

/**
 * 商品类别界面中的商品列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/20
 */
@SuppressLint("UseSparseArrays")
public class AdapterOtherBuyingList extends ArrayAdapter<EntityGoodsBuying> {

    private List<EntityGoodsBuying> list;

    public AdapterOtherBuyingList(Context context, int resource, List<EntityGoodsBuying> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_otherbuying, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final EntityGoodsBuying obj = list.get(position);

        viewHolder.textViewTitle.setText(obj.getTitle());
        viewHolder.textViewContent.setText(obj.getContent());
        viewHolder.textViewComment.setText(obj.getCommentNum());
        viewHolder.textViewTime.setText(obj.getTime());
        viewHolder.textViewPrice.setText("￥" + obj.getPrice());

        // 事件监听
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
        public final TextView textViewTitle;
        public final TextView textViewContent;
        public final TextView textViewComment;
        public final TextView textViewTime;
        public final TextView textViewPrice;

        public ViewHolder(View view) {
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_title);
            textViewContent = (TextView) view.findViewById(R.id.tv_goods_content);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntityGoodsBuying obj, int position);
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }

}
