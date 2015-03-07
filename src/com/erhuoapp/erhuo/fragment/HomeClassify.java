package com.erhuoapp.erhuo.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityGoodsSellingInfo;
import com.erhuoapp.erhuo.activity.ActivitySearch;
import com.erhuoapp.erhuo.adapter.AdapterGoodsSellingList;
import com.erhuoapp.erhuo.model.EntityGoodsClassify;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshBase;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 首页中的商品分类列表界面
 *
 * @author hujiawei
 * @datetime 15/1/6 15:10
 */
public class HomeClassify implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {

    private static final String TAG = "HomeClassify";

    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private int kind = 1, auth = 0;//1 时间排序 2 距离排序 3 价格升序 4 价格降序
    private final String ORDER_DEFAULT = "", ORDER_PRICE_UP = "asc", ORDER_PRICE_DOWN = "desc", ORDER_DISTANCE = "dist";
    private String pageIndex = "0", displayNumber = "10";
    private double lati = 0d, longi = 0d;

    private EntityGoodsClassify classify;

    private Context context;
    private View contentView;
    private TextView textViewName;
    private FrameLoading frameLoading;

    private Button searchTime;
    private Button searchDistance;
    private TextView searchPrice;
    private Button searchAuth;
    private ImageView imageViewPrice;

    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterGoodsSellingList adapterGoodsList;
    private HashMap<String, String> params;
    private List<EntityGoodsSelling> goodsList;

    public HomeClassify(Context context1, View view) {
        Log.e(TAG, "HomeClassify onCreate");
        this.context = context1;
        contentView = view.findViewById(R.id.ll_home_classify);

        frameLoading = new FrameLoading(contentView);//

        // 界面组件
        textViewName = (TextView) contentView.findViewById(R.id.tv_classify_name);
        searchPrice = (TextView) contentView.findViewById(R.id.tv_classify_price);
        searchTime = (Button) contentView.findViewById(R.id.btn_classify_time);
        searchDistance = (Button) contentView.findViewById(R.id.btn_classify_distance);
        searchAuth = (Button) contentView.findViewById(R.id.btn_classify_auth);
        imageViewPrice = (ImageView) contentView.findViewById(R.id.iv_classify_price);

        // 监听事件
        searchTime.setOnClickListener(this);
        searchDistance.setOnClickListener(this);
        searchPrice.setOnClickListener(this);
        searchAuth.setOnClickListener(this);
        contentView.findViewById(R.id.ib_classify_search).setOnClickListener(this);

        // 列表组件
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.lv_refresh_classify_special);
        refreshListView.setPullLoadEnabled(true);
        refreshListView.setPullRefreshEnabled(true);
        refreshListView.setOnRefreshListener(this);
        listView = refreshListView.getRefreshableView();

        goodsList = new ArrayList<EntityGoodsSelling>();
        adapterGoodsList = new AdapterGoodsSellingList(context, 0, goodsList);
        listView.setAdapter(adapterGoodsList);
        adapterGoodsList.setListener(new AdapterGoodsSellingList.ItemClickedListener() {
            @Override
            public void onItemClicked(EntityGoodsSelling obj, int position) {
                Intent intent = new Intent(context, ActivityGoodsSellingInfo.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goods", goodsList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        // post请求中的参数
        params = new HashMap<String, String>();//不需要添加order参数，updateOrder方法中会设置的

        // 关闭加载界面
        frameLoading.hideFrame();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_classify_search:// 搜索
                context.startActivity(new Intent(context, ActivitySearch.class));
                break;
            case R.id.btn_classify_time:// 时间排序
                if (kind != 1) {//
                    kind = 1; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_classify_distance:// 距离排序
                if (kind != 2) {//
                    kind = 2; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.tv_classify_price:// 价格排序
                if (kind == 3) {
                    kind = 4; pageIndex = "0";
                    refreshData(true, false);
                } else {
                    kind = 3; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_classify_auth:// 增加限制：认证用户
                auth = 1 - auth;
                pageIndex = "0";
                refreshData(true, false);
                break;
        }
    }

    // 更新排序部分的组件
    private void updateOrder() {
        searchTime.setTextColor(context.getResources().getColor(R.color.font_gray_red));
        searchDistance.setTextColor(context.getResources().getColor(R.color.font_gray_red));
        searchPrice.setTextColor(context.getResources().getColor(R.color.font_gray_red));

        switch (kind) {
            case 1:// 时间排序
                params.put("order", ORDER_DEFAULT);
                searchTime.setTextColor(context.getResources().getColor(R.color.font_red_gray));
                imageViewPrice.setImageResource(R.drawable.arrow_normal);
                break;
            case 2:// 距离排序
                params.put("order", ORDER_DISTANCE);
                searchDistance.setTextColor(context.getResources().getColor(R.color.font_red_gray));
                imageViewPrice.setImageResource(R.drawable.arrow_normal);
                break;
            case 3:// 价格排序，从低到高，上粉下灰
                params.put("order", ORDER_PRICE_UP);
                searchPrice.setTextColor(context.getResources().getColor(R.color.font_red_gray));
                imageViewPrice.setImageResource(R.drawable.arrow_up);
                break;
            case 4:// 价格排序,从高到低，上灰下粉
                params.put("order", ORDER_PRICE_DOWN);
                searchPrice.setTextColor(context.getResources().getColor(R.color.font_red_gray));
                imageViewPrice.setImageResource(R.drawable.arrow_down);
                break;
        }

        updateAuth();
    }

    // 更新认证用户部分的显示样式
    private void updateAuth() {
        if (auth == 1) {
            //searchAuth.setBackground(context.getDrawable(R.drawable.credit_user_red));//这种方式需要高版本的Android才支持
            searchAuth.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.credit_user_red));
        } else {
            searchAuth.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.credit_user_gray));
        }
    }

    // OnRefreshListener --- start

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageIndex = "0";
        refreshData(true, true);//kind保持不变
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        int index = Integer.parseInt(pageIndex) + 1;//下一页
        pageIndex = String.valueOf(index);
        refreshData(false, true);//不删除原有数据
    }

    // OnRefreshListener --- end

    // 显示类别下的商品列表
    public void show(EntityGoodsClassify classify) {
        this.classify = classify;
        contentView.setVisibility(View.VISIBLE);//在FragmentHome中被调用时表示要显示这个分类

        textViewName.setText(classify.getName());
        initData();
    }

    private void initData() {
        kind = 1; pageIndex = "0"; auth = 0;//默认是全部用户
        refreshData(true, false);
    }

    // 重新请求获取对应排序方式的数据，调用之前需要设置一些参数，例如kind，auth，pageIndex
    // removeData表示是否要删除以前的列表数据，refreshData表示当前是不是上拉或者下拉刷新
    private void refreshData(boolean removeData, boolean refreshData) {
        this.removeData = removeData;
        this.refreshData = refreshData;

        updateOrder();//kind 这个参数由这个方法里面设置
        params.put("cid", classify.getId());
        params.put("auth", String.valueOf(auth));
        params.put("pageIndex", pageIndex);
        params.put("displayNumber", displayNumber);
        params.put("lati", String.valueOf(lati));//TODO:需要重新获取经纬度
        params.put("longi", String.valueOf(longi));

        new CloudUtil().GoodsSelling(params, new GoodsSellingCallback());
    }

    public void hide() {
        contentView.setVisibility(View.INVISIBLE);
    }

    /**
     * 商品列表监听
     */
    class GoodsSellingCallback implements CloudUtil.OnPostRequest<ArrayList<EntityGoodsSelling>> {

        @Override
        public void onPost() {
            if (!refreshData) {//如果是上拉或者下拉刷新时不要显示加载界面
                //frameLoading.setMessage("数据加载中...");
                frameLoading.showFrame();//开始显示动画
            }

            //如果存在数据并且需要清空的话先清空数据
            if (removeData && goodsList != null && goodsList.size() > 0) {
                goodsList.clear();
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPostOk(ArrayList<EntityGoodsSelling> temp) {
            Log.e(TAG, "data size = " + temp.size());
            frameLoading.hideFrame();

            // 判断是add还是setter
            int oldSize = goodsList.size();
            goodsList.addAll(temp);//直接对adapter使用addAll方法需要高版本的Android支持

            adapterGoodsList.notifyDataSetChanged();
            if (refreshData){
                refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                refreshListView.onPullDownRefreshComplete();//上拉刷新完成
            }
            // 没有数据 [考虑到可能是上拉或者下拉，所以要设置刷新完成]
            if (goodsList.size() < 1) {
                frameLoading.showMessage("该分类还没有商品\n点击重新加载数据");//点击重试
                frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                    @Override
                    public void onFrameLoadingClicked() {
                        initData();
                    }
                });
                if (refreshData){
                    refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                    refreshListView.onPullDownRefreshComplete();//上拉刷新完成
                }
                return;
            }

            if (!removeData) {//解决上拉刷新的时候重新回到列表第一个元素的问题
                if (oldSize > 1) {
                    listView.setSelection(oldSize - 1);//将原来数据列表中的最后一个显示在新的数据中的第一个
                } else {
                    listView.setSelection(oldSize);//最新的数据的第一个会出现在显示出来的数据中的第一个
                }
            } else {
                listView.setSelection(0);//在类别之间切换时要重新回到0
            }
        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    //new CloudUtil().GoodsSelling(params, new GoodsSelling());
                    refreshData(removeData, refreshData);
                }
            });
            Log.e(TAG, err);
        }
    }

}
