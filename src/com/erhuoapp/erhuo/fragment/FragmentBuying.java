package com.erhuoapp.erhuo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityBuying;
import com.erhuoapp.erhuo.activity.ActivityGoodsBuyingInfo;
import com.erhuoapp.erhuo.activity.ActivityLogin;
import com.erhuoapp.erhuo.adapter.AdapterGoodsBuyingList;
import com.erhuoapp.erhuo.im.db.HeadAndName;
import com.erhuoapp.erhuo.im.domain.UserHeadAndName;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ErUse;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshBase;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 求购界面
 * <p/>
 * NOTE:所有出现有可刷新列表的地方都以这个类的实现作为参考 (由于项目前期代码的缘故，本来是可以不用goodsList，直接使用adapterGoodsList)
 *
 * @author hujiawei
 * @datetime 15/1/2 21:10
 */
public class FragmentBuying extends Fragment implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener, IFragment, IConstants {

    private static final String TAG = "FragmentBuying";

    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private boolean isDataLoaded = false;//数据是否已经加载过
    private int kind = 1, auth = 0;//1 时间排序 2 距离排序 3 价格升序 4 价格降序
    private final String ORDER_DEFAULT = "", ORDER_PRICE_UP = "asc", ORDER_PRICE_DOWN = "desc", ORDER_DISTANCE = "dist";
    private String pageIndex = "0", displayNumber = "10";
    private double lati = 0d, longi = 0d;

    private Context context;
    private View contentView;
    private FrameLoading frameLoading;

    private Button searchTime;
    private Button searchDistance;
    private TextView searchPrice;
    private Button searchAuth;
    private ImageView imageViewPrice;

    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterGoodsBuyingList adapterGoodsList;
    private HashMap<String, String> params;
    private List<EntityGoodsBuying> goodsList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");

        context = getActivity();
        contentView = inflater.inflate(R.layout.fragment_buying, null);
        frameLoading = new FrameLoading(contentView);//

        // 界面组件
        searchPrice = (TextView) contentView.findViewById(R.id.tv_buying_price);
        searchTime = (Button) contentView.findViewById(R.id.btn_buying_time);
        searchDistance = (Button) contentView.findViewById(R.id.btn_buying_distance);
        searchAuth = (Button) contentView.findViewById(R.id.btn_buying_auth);
        imageViewPrice = (ImageView) contentView.findViewById(R.id.iv_buying_price);

        // 监听事件
        searchTime.setOnClickListener(this);
        searchDistance.setOnClickListener(this);
        searchPrice.setOnClickListener(this);
        searchAuth.setOnClickListener(this);
        contentView.findViewById(R.id.ib_buying_add).setOnClickListener(this);

        // 列表组件
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.lv_refresh_buying);
        refreshListView.setPullLoadEnabled(true);
        refreshListView.setPullRefreshEnabled(true);
        refreshListView.setOnRefreshListener(this);
        listView = refreshListView.getRefreshableView();

        goodsList = new ArrayList<EntityGoodsBuying>();
        adapterGoodsList = new AdapterGoodsBuyingList(context, 0, goodsList);
        listView.setAdapter(adapterGoodsList);
        adapterGoodsList.setListener(new AdapterGoodsBuyingList.ItemClickedListener() {
            @Override
            public void onItemClicked(EntityGoodsBuying obj, int position) {

            	checkPosition = position;//获取被点击的View的位置
            	
                Intent intent = new Intent(context, ActivityGoodsBuyingInfo.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goods", goodsList.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_CHECK_BUYING);
            }
        });

        // post请求中的参数
        params = new HashMap<String, String>();//不需要添加order参数，updateOrder方法中会设置的

        // 关闭显示
        frameLoading.hideFrame();

        return contentView;
    }

    private void initData() {
        kind = 1; pageIndex = "0"; auth = 0;//默认是全部用户
        refreshData(true, false);
        Log.e(TAG, "initData");
    }

    private static int checkPosition = 0;
    
    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面
        
        //initData();//会跳转到最顶部，故本地处理
        //listView.getItemAtPosition(checkPosition);
        //goodsList.get(checkPosition).setCommentNum(""+7);
        
        // 这样做的原因是为了防止用户点击了某项进入详情之后再回来的话能够回到刚才的列表位置，而不需要重新加载
        if (!isDataLoaded) {//如果没有加载过数据的话那么就设置默认参数并加载
            initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        MobclickAgent.onPageEnd("MainScreen");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestrory");
    }

    @Override
    public void refresh() {
        Log.e(TAG, "refresh");
    }

    @Override
    public void fragmentRefresh() {
        Log.e(TAG, "fragmentRefresh");
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_buying_add://发布求购
                if (AppUtil.getInstance().checkUserLogin()) {
                    //context.startActivity(new Intent(context, ActivityBuying.class));
                    //加不加 ((ActivityMain) context). 是有区别的 http://www.kcsjok.com/?p=22
                    //Fragment嵌套的情况下也不同，如果希望父Fragment被调用的话需要使用getParentFragment()
                    //下面的代码会让FragmentMain中的onActivityResult被调用
                    getParentFragment().startActivityForResult(new Intent(context, ActivityBuying.class), REQUEST_BUYING_PUBLISH);
                } else {//还没有登录的话先登录
                    //context.startActivity(new Intent(context, ActivityLogin.class));
                    //不用加上((ActivityMain) context). 登录这个请求我希望是ActivityMain获取返回值
                    startActivityForResult(new Intent(context, ActivityLogin.class), REQUEST_LOGIN);
                }
                break;
            case R.id.btn_buying_time:// 时间排序
                if (kind != 1) {//
                    kind = 1; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_buying_distance:// 距离排序
                if (kind != 2) {//
                    kind = 2; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.tv_buying_price:// 价格排序
                if (kind == 3) {
                    kind = 4; pageIndex = "0";
                    refreshData(true, false);
                } else {
                    kind = 3; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_buying_auth:// 增加限制：认证用户
                auth = 1 - auth;
                pageIndex = "0";
                refreshData(true, false);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");//接收不到
        //下面代码没有用，接收不到返回数据，不会被调用
//        if (requestCode == REQUEST_BUYING_PUBLISH) {//发布求购
//            if (resultCode == Activity.RESULT_OK) {
//                ToastUtil.showShortToast(context, "发布成功");
//                initData();
//            }
//        }
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

    // 更新认证用户部分的显示样式

    private void updateAuth() {
        if (auth == 1) {
            //searchAuth.setBackground(context.getDrawable(R.drawable.credit_user_red));//这种方式需要高版本的Android才支持
            searchAuth.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.credit_user_red));
        } else {
            searchAuth.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.credit_user_gray));
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

    // 重新请求获取对应排序方式的数据，调用之前需要设置一些参数，例如kind，auth，pageIndex
    // removeData表示是否要删除以前的列表数据，refreshData表示当前是不是上拉或者下拉刷新
    private void refreshData(boolean removeData, boolean refreshData) {
        this.removeData = removeData;
        this.refreshData = refreshData;

        updateOrder();//kind 这个参数由这个方法里面设置
        params.put("auth", String.valueOf(auth));
        params.put("pageIndex", pageIndex);
        params.put("displayNumber", displayNumber);
        params.put("lati", String.valueOf(lati));//TODO:可能需要重新获取经纬度
        params.put("longi", String.valueOf(longi));

        new CloudUtil().GoodsBuying(params, new GoodsBuyingCallback());
        
        Log.e(TAG, "refreshData");
    }

    /**
     * 商品列表监听
     */
    class GoodsBuyingCallback implements CloudUtil.OnPostRequest<ArrayList<EntityGoodsBuying>> {

        @Override
        public void onPost() {
            if (!refreshData) {//如果不是上拉或者下拉刷新时就要显示加载界面
                frameLoading.showFrame();//开始显示动画
            }

            //如果存在数据并且需要清空的话先清空数据
            if (removeData && goodsList != null && goodsList.size() > 0) {
                goodsList.clear();
            }
        }

        @Override
        public void onPostOk(ArrayList<EntityGoodsBuying> temp) {
            Log.e(TAG, "data size = " + temp.size());
            isDataLoaded = true;
            frameLoading.hideFrame();

            // 判断是add还是setter
            int oldSize = goodsList.size();
            goodsList.addAll(temp);//直接对adapter使用addAll方法需要高版本的Android支持

            HeadAndName mHeadAndName = new HeadAndName(getActivity());
        	List<UserHeadAndName> contactList = new ArrayList<UserHeadAndName>();
            for(int i=0;i<goodsList.size();i++){
            	UserHeadAndName mUserHeadAndName = new UserHeadAndName();
            	mUserHeadAndName.head = goodsList.get(i).getUserHeader();
            	mUserHeadAndName.nick = goodsList.get(i).getUserNickName();
            	mUserHeadAndName.userid = goodsList.get(i).getUserId();
            	contactList.add(mUserHeadAndName);
            }
            mHeadAndName.saveContactList(contactList);
            
            adapterGoodsList.notifyDataSetChanged();
            if (refreshData) {
                refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                refreshListView.onPullDownRefreshComplete();//上拉刷新完成
            }
            // 没有数据 [考虑到可能是上拉或者下拉，所以要设置刷新完成]
            if (goodsList.size() < 1) {
                frameLoading.showMessage("还没有求购的商品\n点击重新加载数据");//点击重试
                frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                    @Override
                    public void onFrameLoadingClicked() {
                        initData();
                    }
                });
                return;
            }

            // 解决上拉刷新的显示问题
            if (!removeData) {//只有上拉刷新不删除原来的数据，解决上拉刷新的时候重新回到列表第一个元素的问题
                if (oldSize > 1) {
                    listView.setSelection(oldSize - 1);//将原来数据列表中的最后一个显示在新的数据中的第一个
                } else {
                    listView.setSelection(oldSize);//最新的数据的第一个会出现在显示出来的数据中的第一个
                }
            } else {
                listView.setSelection(0);
            }
        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    refreshData(removeData, refreshData);
                }
            });
            Log.e(TAG, err);
        }
    }

}
