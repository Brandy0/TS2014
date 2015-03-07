package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;

import java.util.List;

/**
 * 商品类别界面中的商品列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/16 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterUserBuyingList extends ArrayAdapter<EntityGoodsBuying> {

    private List<EntityGoodsBuying> list;

    public AdapterUserBuyingList(Context context, int resource, List<EntityGoodsBuying> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_userbuying, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final EntityGoodsBuying obj = list.get(position);

        viewHolder.textViewTitle.setText(obj.getTitle());
        viewHolder.textViewContent.setText(obj.getContent());
        viewHolder.textViewComment.setText(obj.getCommentNum());
        viewHolder.textViewTime.setText(obj.getTime());
        viewHolder.frameLayoutActions.setVisibility(View.INVISIBLE);
        viewHolder.linearLayoutInfo.setVisibility(View.VISIBLE);
        //viewHolder.textViewPrice.setText("￥" + obj.getPrice());

        // 事件监听
        viewHolder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 跳转到对应商品的详情界面
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemClicked(obj, position);
                }
            }
        });

        viewHolder.imageButtonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.frameLayoutActions.setVisibility(View.VISIBLE);
                //viewHolder.linearLayoutInfo.setVisibility(View.INVISIBLE);
            }
        });

        viewHolder.imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.frameLayoutActions.setVisibility(View.INVISIBLE);
                //viewHolder.linearLayoutInfo.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.frameLayoutActions.setVisibility(View.INVISIBLE);
                //viewHolder.linearLayoutInfo.setVisibility(View.VISIBLE);
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemEdit(obj, position);
                }
            }
        });

        viewHolder.textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.frameLayoutActions.setVisibility(View.INVISIBLE);
                //viewHolder.linearLayoutInfo.setVisibility(View.VISIBLE);
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemRemoved(obj, position);
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
        //public final TextView textViewPrice;

        public final ImageButton imageButtonClose;
        public final ImageButton imageButtonMore;
        public final TextView textViewDelete;
        public final TextView textViewEdit;

        public final FrameLayout frameLayoutActions;
        public final LinearLayout linearLayoutInfo;

        public ViewHolder(View view) {
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_title);
            textViewContent = (TextView) view.findViewById(R.id.tv_goods_content);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewDelete = (TextView) view.findViewById(R.id.tv_goods_delete);
            textViewEdit = (TextView) view.findViewById(R.id.tv_goods_edit);
            imageButtonClose = (ImageButton) view.findViewById(R.id.ib_goods_close);
            imageButtonMore = (ImageButton) view.findViewById(R.id.ib_goods_more);
            linearLayoutInfo = (LinearLayout) view.findViewById(R.id.ll_goods_info);
            frameLayoutActions = (FrameLayout) view.findViewById(R.id.fl_goods_actions);
            //textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
        }
    }

    // 点击某个商品的事件监听器
    public interface ItemClickedListener {
        void onItemClicked(EntityGoodsBuying obj, int position);

        void onItemRemoved(EntityGoodsBuying obj, int position);

        void onItemEdit(EntityGoodsBuying obj, int position);
    }

    private ItemClickedListener listener;

    public ItemClickedListener getListener() {
        return listener;
    }

    public void setListener(ItemClickedListener listener) {
        this.listener = listener;
    }

}
