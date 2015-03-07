package com.erhuoapp.erhuo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityOther;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.HashMap;
import java.util.List;

/**
 * 商品类别界面中的商品列表Adapter
 *
 * @author hujiawei
 * @datetime 15/1/6 17:04
 */
@SuppressLint("UseSparseArrays")
public class AdapterGoodsSellingList extends ArrayAdapter<EntityGoodsSelling> {

    private final String TAG = "AdapterGoodsSellingList";

    private List<EntityGoodsSelling> list;
    private DisplayImageOptions displayImageOptions;
    private DisplayImageOptions displayImageOptions_header;

    public AdapterGoodsSellingList(Context context, int resource, List<EntityGoodsSelling> objects) {
        super(context, resource, objects);
        this.list = objects;
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.goods_default)
                .showImageOnFail(R.drawable.goods_default)
                .showImageOnLoading(R.drawable.goods_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();
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
        final ViewHolder viewHolder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listview_goodsselling, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // 加载图片
        final EntityGoodsSelling obj = list.get(position);
        ImageLoader.getInstance().displayImage(obj.getImage(), viewHolder.imageViewGoods, displayImageOptions);
        ImageLoader.getInstance().displayImage(obj.getUserHeader(), viewHolder.imageViewHeader, displayImageOptions_header);

        viewHolder.textViewTitle.setText(obj.getTitle());
        viewHolder.textViewCollect.setText(obj.getCollectNum());
        viewHolder.textViewComment.setText(obj.getCommentNum());
        viewHolder.textViewDist.setText(obj.getDistance());
        viewHolder.textViewTime.setText(obj.getTime());
        viewHolder.textViewUserName.setText(obj.getUserNickName());
        viewHolder.textViewSingleName.setText(obj.getUserNickName());
        viewHolder.textViewPrice.setText("￥" + obj.getPrice());
        if (obj.isNew()) {
            viewHolder.imageViewNew.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewNew.setVisibility(View.GONE);//hujiawei
        }
        if (obj.isBargin()){
            viewHolder.imageViewBargin.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imageViewBargin.setVisibility(View.GONE);//
        }
        if (obj.isAuth()) {
            viewHolder.frameLayoutAuth.setVisibility(View.VISIBLE);
            viewHolder.textViewSingleName.setVisibility(View.GONE);
        } else {
            viewHolder.textViewSingleName.setVisibility(View.VISIBLE);
            viewHolder.frameLayoutAuth.setVisibility(View.GONE);
        }
        if (obj.isCollect()) {
            viewHolder.imageButtonCollect.setImageResource(R.drawable.favorite_active);
        } else {
            viewHolder.imageButtonCollect.setImageResource(R.drawable.favorite);
        }
        if (obj.isComment()) {
            viewHolder.imageButtonComment.setImageResource(R.drawable.comment_active);
        } else {
            viewHolder.imageButtonComment.setImageResource(R.drawable.comment);
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

        viewHolder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// 跳转到对应商品的详情界面
                if (listener != null) {//交给监听者来处理这个事件
                    listener.onItemClicked(obj, position);
                }
            }
        });

        viewHolder.imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 用户头像
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

        viewHolder.imageButtonCollect.setOnClickListener(new View.OnClickListener() {//收藏
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", obj.getId());
                if (obj.isCollect()) {
                    new CloudUtil().RemoveCollect(params, new RemoveCollectCallbak(obj,viewHolder));
                } else {
                    new CloudUtil().AddCollect(params, new AddCollectCallback(obj,viewHolder));
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView imageViewGoods;
        public final ImageView imageViewHeader;
        public final ImageView imageViewNew;
        public final ImageView imageViewBargin;
        public final TextView textViewTitle;
        public final TextView textViewUserName;
        public final TextView textViewSingleName;
        public final TextView textViewCollect;
        public final TextView textViewComment;
        public final TextView textViewTime;
        public final TextView textViewDist;
        public final TextView textViewPrice;
        public final ImageButton imageButtonCollect;
        public final ImageButton imageButtonComment;
        public final FrameLayout frameLayoutAuth;

        public ViewHolder(View view) {
            imageViewGoods = (ImageView) view.findViewById(R.id.iv_goods_image);
            textViewTitle = (TextView) view.findViewById(R.id.tv_goods_name);
            imageViewHeader = (ImageView) view.findViewById(R.id.iv_goods_header);
            imageViewNew = (ImageView) view.findViewById(R.id.iv_goods_new);
            imageViewBargin = (ImageView) view.findViewById(R.id.iv_goods_bargin);
            textViewUserName = (TextView) view.findViewById(R.id.tv_goods_username);
            textViewSingleName = (TextView) view.findViewById(R.id.tv_goods_singlename);
            textViewCollect = (TextView) view.findViewById(R.id.tv_goods_collectnum);
            textViewComment = (TextView) view.findViewById(R.id.tv_goods_commentnum);
            textViewTime = (TextView) view.findViewById(R.id.tv_goods_time);
            textViewDist = (TextView) view.findViewById(R.id.tv_goods_dist);
            textViewPrice = (TextView) view.findViewById(R.id.tv_goods_price);
            imageButtonCollect = (ImageButton) view.findViewById(R.id.ib_goods_collect);
            imageButtonComment = (ImageButton) view.findViewById(R.id.ib_goods_comment);
            frameLayoutAuth = (FrameLayout) view.findViewById(R.id.fl_goods_auth);
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

    // 处理收藏
    /**
     * 添加收藏
     */
    class AddCollectCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        private EntityGoodsSelling obj;
        private ViewHolder viewHolder;

        public AddCollectCallback(EntityGoodsSelling obj, ViewHolder viewHolder){
            this.obj = obj;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityHttpResponse result) {
            ToastUtil.showShortToast(getContext(), "收藏成功");
            obj.setCollect(true);
            viewHolder.imageButtonCollect.setImageResource(R.drawable.favorite_active);
            viewHolder.textViewCollect.setText(String.valueOf(Integer.parseInt(viewHolder.textViewCollect.getText().toString()) + 1));
        }

        @Override
        public void onPostFailure(String err) {
            if (null != err && err.equalsIgnoreCase("alreay_collected")) {
                ToastUtil.showShortToast(getContext(), "已经收藏了");
            } else if (null != err && err.equalsIgnoreCase("not_logged")) {
                ToastUtil.showShortToast(getContext(), "请先登录");
            } else {
                ToastUtil.showShortToast(getContext(), "收藏失败");
            }
            Log.e(TAG, err);
        }
    }

    /**
     * 取消收藏
     */
    class RemoveCollectCallbak implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        private EntityGoodsSelling obj;
        private ViewHolder viewHolder;

        public RemoveCollectCallbak(EntityGoodsSelling obj, ViewHolder viewHolder){
            this.obj = obj;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityHttpResponse result) {
            ToastUtil.showShortToast(getContext(), "取消收藏成功");
            obj.setCollect(false);
            viewHolder.imageButtonCollect.setImageResource(R.drawable.favorite);
            viewHolder.textViewCollect.setText(String.valueOf(Integer.parseInt(viewHolder.textViewCollect.getText().toString()) - 1));
        }

        @Override
        public void onPostFailure(String err) {
            if (null != err && err.equalsIgnoreCase("not_collected")) {
                ToastUtil.showShortToast(getContext(), "还未收藏");
            } else if (null != err && err.equalsIgnoreCase("not_logged")) {
                ToastUtil.showShortToast(getContext(), "请先登录");
            } else {
                ToastUtil.showShortToast(getContext(), "取消收藏失败");
            }
            Log.e(TAG, err);
        }
    }

}
