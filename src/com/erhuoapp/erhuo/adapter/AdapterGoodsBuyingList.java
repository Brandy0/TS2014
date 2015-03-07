package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityOther;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * 求购界面中的商品列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/10
 */
@SuppressLint("UseSparseArrays")
public class AdapterGoodsBuyingList extends ArrayAdapter<EntityGoodsBuying> {

    private List<EntityGoodsBuying> list;
    private DisplayImageOptions displayImageOptions_header;

    public AdapterGoodsBuyingList(Context context, int resource, List<EntityGoodsBuying> objects) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_goodsbuying, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        final EntityGoodsBuying obj = list.get(position);
        ImageLoader.getInstance().displayImage(obj.getUserHeader(), viewHolder.imageViewHeader, displayImageOptions_header);

        viewHolder.textViewTitle.setText(obj.getTitle());
        viewHolder.textViewContent.setText(obj.getContent());
        viewHolder.textViewComment.setText(obj.getCommentNum());
        viewHolder.textViewDist.setText(obj.getDistance());
        viewHolder.textViewTime.setText(obj.getTime());
        viewHolder.textViewUserName.setText(obj.getUserNickName());
        viewHolder.textViewNameAuth.setText(obj.getUserNickName());
        viewHolder.textViewDistAuth.setText(obj.getDistance());
        viewHolder.textViewTimeAuth.setText(obj.getTime());
        viewHolder.textViewPrice.setText("￥" + obj.getPrice());
        if (obj.isComment()){
            viewHolder.imageButtonComment.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.comment_active));
        }else{
            viewHolder.imageButtonComment.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.comment));
        }
        if (obj.isAuth()){
            viewHolder.layoutAuthinfo.setVisibility(View.VISIBLE);
            viewHolder.layoutNoauthinfo.setVisibility(View.GONE);
        }else{
            viewHolder.layoutAuthinfo.setVisibility(View.GONE);
            viewHolder.layoutNoauthinfo.setVisibility(View.VISIBLE);
        }
        // 事件监听
        viewHolder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 跳转到对应商品的详情界面
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemClicked(obj, position);
                }
            }
        });

        viewHolder.imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityOther.class);
                Bundle bundle = new Bundle();
                EntityUserInfo user = new EntityUserInfo();
                user.setId(obj.getUserId());
                user.setHeader(obj.getUserHeader());
                user.setNickName(obj.getUserNickName());
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageViewHeader;
        public final TextView textViewTitle;
        public final TextView textViewContent;
        public final TextView textViewUserName;
        public final TextView textViewComment;
        public final TextView textViewTime;
        public final TextView textViewDist;
        public final TextView textViewPrice;
        public final ImageButton imageButtonComment;
        public final FrameLayout layoutAuthinfo;
        public final LinearLayout layoutNoauthinfo;
        public final TextView textViewTimeAuth;
        public final TextView textViewDistAuth;
        public final TextView textViewNameAuth;

        public ViewHolder(View view) {
            imageViewHeader = (ImageView) view.findViewById(R.id.iv_goods_header);
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_name);
            textViewContent = (TextView) view.findViewById(R.id.tv_goods_content);
            textViewUserName = (TextView) view.findViewById(R.id.tv_goods_username);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewDist = (TextView) view.findViewById(R.id.tv_goods_dist);
            textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
            imageButtonComment = (ImageButton) view.findViewById(R.id.ib_goods_comment);
            layoutAuthinfo = (FrameLayout) view.findViewById(R.id.ll_goods_authinfo);
            layoutNoauthinfo = (LinearLayout) view.findViewById(R.id.ll_goods_noauthinfo);
            textViewTimeAuth = (TextView) view.findViewById(R.id.tv_goods_auth_time);
            textViewDistAuth = (TextView) view.findViewById(R.id.tv_goods_auth_dist);
            textViewNameAuth = (TextView) view.findViewById(R.id.tv_goods_auth_username);
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
