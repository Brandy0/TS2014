package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterGoodsSellingList;
import com.erhuoapp.erhuo.adapter.AdapterGridHotSearch;
import com.erhuoapp.erhuo.adapter.AdapterSearchHistory;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ErUse;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshBase;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 搜索界面
 *
 * @author hujiawei
 * @datetime 2015/1/4
 */
public class ActivitySearch extends FragmentActivity implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {

    private static final String TAG = "ActivitySearch";

    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private boolean isDataLoaded = false;//是否加载过数据
    private int kind = 1, auth = 0;//1 时间排序 2 距离排序 3 价格升序 4 价格降序
    private final String ORDER_DEFAULT = "", ORDER_PRICE_UP = "asc", ORDER_PRICE_DOWN = "desc", ORDER_DISTANCE = "dist";
    private String pageIndex = "0", displayNumber = "10";
    private double lati = 0d, longi = 0d;
    private int hotnum = 6;

    private HashMap<String, String> params;
    private Context context;
    private View contentView;
    private FrameLoading frameLoading;

    private GridView gridViewHot;
    private ListView listViewHistory;
    private AdapterSearchHistory adapterSearchHistory;

    private EditText editTextSearch;
    private LinearLayout linearLayoutTip;
    private LinearLayout linearLayoutResult;
    private ImageButton imageButtonSearch;

    private Button searchTime;
    private Button searchDistance;
    private TextView searchPrice;
    private Button searchAuth;
    private ImageView imageViewPrice;

    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterGoodsSellingList adapterGoodsList;
    private List<EntityGoodsSelling> goodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        contentView = getLayoutInflater().inflate(R.layout.activity_search, null);
        setContentView(contentView);

        frameLoading = new FrameLoading(contentView);//

        // 界面组件
        searchPrice = (TextView) contentView.findViewById(R.id.tv_search_price);
        searchTime = (Button) contentView.findViewById(R.id.btn_search_time);
        searchDistance = (Button) contentView.findViewById(R.id.btn_search_distance);
        searchAuth = (Button) contentView.findViewById(R.id.btn_search_auth);
        imageViewPrice = (ImageView) contentView.findViewById(R.id.iv_search_price);
        linearLayoutTip = (LinearLayout) contentView.findViewById(R.id.ll_search_tip);
        linearLayoutResult = (LinearLayout) contentView.findViewById(R.id.ll_search_result);
        editTextSearch = (EditText) contentView.findViewById(R.id.et_search_box);
        listViewHistory = (ListView) contentView.findViewById(R.id.lv_search_history);

        // 监听事件
        findViewById(R.id.ib_search_return).setOnClickListener(this);
        findViewById(R.id.ib_search_search).setOnClickListener(this);
        searchTime.setOnClickListener(this);
        searchDistance.setOnClickListener(this);
        searchPrice.setOnClickListener(this);
        searchAuth.setOnClickListener(this);

        //大神方法，亮瞎了
        //监听editTextSearch中的回车输入，进入搜索
        editTextSearch.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					// TODO 自动生成的方法存根
					doSearch(editTextSearch.getText().toString());
					return true;
				}
        	 });
        

        // 在xml中已经取消了edittext自动获得焦点的事件
        // 一下监听貌似没用，故取消
        /*
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    gotoTip();
                } else {
                    gotoResult();
                }
            }
        });
        */
        /*
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTip();
            }
        });
        */

        // 列表组件
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.lv_refresh_search);
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

        // 热门搜索部分
        gridViewHot = (GridView) contentView.findViewById(R.id.gridview_search_hot);
        HashMap<String, String> hotParams = new HashMap<String, String>();
        hotParams.put("num", hotnum + "");
        new CloudUtil().HotSearch(hotParams, new HotSearchCallback());

        // post请求中的参数
        params = new HashMap<String, String>();
        
        //显示输入法
        ErUse.toggleInputMethod(editTextSearch,200);
    }

    @Override
    protected void onResume() {
        super.onResume();//一定要先调用父类的onResume方法
        Log.e(TAG, "onResume");
        MobclickAgent.onResume(this);//友盟统计onResume
        if (!isDataLoaded) {//没有加载过数据的话显示搜索提示信息
            gotoTip();
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.e(TAG, "onPause");
    	MobclickAgent.onPause(this);//友盟统计onPause
    }

    // 显示提示内容
    private void gotoTip() {
        // 搜索历史
        adapterSearchHistory = new AdapterSearchHistory(context, 0, AppUtil.getInstance().getSearchHistory());
        listViewHistory.setAdapter(adapterSearchHistory);
        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTextSearch.setText(adapterSearchHistory.getItem(position));
                doSearch(adapterSearchHistory.getItem(position));
            }
        });

        linearLayoutTip.setVisibility(View.VISIBLE);
        linearLayoutResult.setVisibility(View.INVISIBLE);
    }

    // 显示搜索结果
    private void gotoResult() {
        linearLayoutTip.setVisibility(View.INVISIBLE);
        linearLayoutResult.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_search_return:// 返回
                finish();
                break;
            case R.id.btn_search_time:// 时间排序
                if (kind != 1) {//
                    kind = 1; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_search_distance:// 距离排序
                if (kind != 2) {//
                    kind = 2; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.tv_search_price:// 价格排序
                if (kind == 3) {
                    kind = 4; pageIndex = "0";
                    refreshData(true, false);
                } else {
                    kind = 3; pageIndex = "0";
                    refreshData(true, false);
                }
                break;
            case R.id.btn_search_auth:// 增加限制：认证用户
                auth = 1 - auth;
                pageIndex = "0";
                refreshData(true, false);
                break;
            case R.id.ib_search_search://搜索操作
                doSearch(editTextSearch.getText().toString());
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

    // 进行搜索操作，显示对应关键字搜索出来的商品列表
    private void doSearch(String keyword) {
        if (keyword.equalsIgnoreCase("")) {
            ToastUtil.showShortToast(context, "请输入搜索内容");
            return;
        }

        // 进行搜索
        gotoResult();
        kind = 1; pageIndex = "0"; auth = 0;//默认是全部用户
        params.put("keyword", keyword);
        refreshData(true, false);

        //添加到搜索历史
        ArrayList<String> historyList = AppUtil.getInstance().getSearchHistory();
        if (null == historyList) {//搜索历史为空
            historyList = new ArrayList<String>();
            historyList.add(keyword);
            AppUtil.getInstance().saveSearchHistory(historyList);
        } else {//有搜索历史
            if (!historyList.contains(keyword)) {//判断是否已经存在这个搜索字符串，有的话就不用处理了
                historyList.add(0, keyword);//每次都往前插入，这样的话后面的就可能在保存的时候去掉了
                AppUtil.getInstance().saveSearchHistory(historyList);
            }
        }
        
      //收起键盘
      ErUse.hideInputMethod(editTextSearch,0);
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

        new CloudUtil().GoodsSearch(params, new GoodsSearchCallback());
    }

    /**
     * 搜索商品列表
     */
    class GoodsSearchCallback implements CloudUtil.OnPostRequest<ArrayList<EntityGoodsSelling>> {

        @Override
        public void onPost() {
            if (!refreshData) {//如果是上拉或者下拉刷新时不要显示加载界面
                frameLoading.showFrame();//开始显示动画
            }

            //如果存在数据并且需要清空的话先清空数据
            if (removeData && goodsList != null && goodsList.size() > 0) {
                goodsList.clear();
            }
        }

        @Override
        public void onPostOk(ArrayList<EntityGoodsSelling> temp) {
            Log.e(TAG, "data size = " + temp.size());
            isDataLoaded = true;
            frameLoading.hideFrame();

            int oldSize = goodsList.size();
            goodsList.addAll(temp);//直接对adapter使用addAll方法需要高版本的Android支持

            adapterGoodsList.notifyDataSetChanged();
            if (refreshData) {
                refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                refreshListView.onPullDownRefreshComplete();//上拉刷新完成
            }
            // 没有数据 [考虑到可能是上拉或者下拉，所以要设置刷新完成]
            if (goodsList.size() < 1) {
                frameLoading.showNoSearchResult();
                return;
            }

            if (!removeData) {//解决上拉刷新的时候重新回到列表第一个元素的问题
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
            Log.e(TAG, err);
        }
    }

    // 热门搜索回调事件
    class HotSearchCallback implements CloudUtil.OnPostRequest<ArrayList<String>> {
        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final ArrayList<String> list) {
            AdapterGridHotSearch adapterGridHotSearch = new AdapterGridHotSearch(ActivitySearch.this, 0, list);
            gridViewHot.setAdapter(adapterGridHotSearch);
            gridViewHot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editTextSearch.setText(list.get(position));
                    doSearch(list.get(position));
                }
            });
        }

        @Override
        public void onPostFailure(String err) {

        }
    }
    
}
