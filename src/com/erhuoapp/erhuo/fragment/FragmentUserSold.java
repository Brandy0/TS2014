package com.erhuoapp.erhuo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityGoodsSellingInfo;
import com.erhuoapp.erhuo.adapter.AdapterUserSoldList;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshBase;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 个人主页中的已售界面
 *
 * NOTE：这个类已经不使用了，用FragmentUserSold代替
 *
 * @author hujiawei
 * @datetime 15/1/10
 */
public class FragmentUserSold extends Fragment implements View.OnClickListener, IFragment, PullToRefreshBase.OnRefreshListener<ListView> {

    private static final String TAG = "FragmentUserSold";

    private Context context;
    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private boolean isDataLoaded = false;//是否加载过数据
    private boolean isFragmentInit = false;//
    private HashMap<String, String> params;
    private String pageIndex = "0", displayNumber = "10";

    private FrameLoading frameLoading;
    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterUserSoldList adapterGoodsList;
    private List<EntityGoodsSelling> goodsList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        context = getActivity();
        View contentView = inflater.inflate(R.layout.fragment_usersold, null);
        frameLoading = new FrameLoading(contentView);

        // 列表组件
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.lv_userinfo_sold);
        refreshListView.setPullLoadEnabled(true);
        refreshListView.setPullRefreshEnabled(true);
        refreshListView.setOnRefreshListener(this);
        listView = refreshListView.getRefreshableView();

        goodsList = new ArrayList<EntityGoodsSelling>();
        adapterGoodsList = new AdapterUserSoldList(context, 0, goodsList);
        listView.setAdapter(adapterGoodsList);//这个时候listview立刻就为空了
        adapterGoodsList.setListener(new AdapterUserSoldList.ItemClickedListener() {
            @Override
            public void onItemClicked(EntityGoodsSelling obj, int position) {
                //EntityGoodsSelling goods = goodsList.get(position);
                Intent intent = new Intent(context, ActivityGoodsSellingInfo.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goods", goodsList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }

            @Override
            public void onItemRemoved(EntityGoodsSelling obj, int position) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", obj.getId());
                new CloudUtil().RemoveSelling(params, new RemoveSoldCallback(obj));
                //注意，这里删除已售调用的其实是删除该商品的API
            }
        });

        // post请求中的参数
        params = new HashMap<String, String>();

        // 关闭加载界面
        frameLoading.hideFrame();

        return contentView;
    }

    @Override
    public void onClick(View v) {

    }

    private void initData() {
        pageIndex = "0"; displayNumber = "10";
        refreshData(true, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        isFragmentInit = true;
    }

    @Override
    public void refresh() {
        Log.e(TAG, "refresh");
        if (isFragmentInit) {
            if (!isDataLoaded) {
                initData();
            }
        }
    }

    @Override
    public void fragmentRefresh() {
        Log.e(TAG, "fragmentRefresh");
        //initData();
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

    // 重新请求获取对应排序方式的数据，调用之前需要设置一些参数，例如kind，auth，pageIndex
    // removeData表示是否要删除以前的列表数据，refreshData表示当前是不是上拉或者下拉刷新
    private void refreshData(boolean removeData, boolean refreshData) {
        this.removeData = removeData;
        this.refreshData = refreshData;

        params.put("pageIndex", pageIndex);
        params.put("displayNumber", displayNumber);
        new CloudUtil().UserInfoSold(params, new UserInfoSoldCallback());
    }

    // 用户收藏加载完毕的回调
    class UserInfoSoldCallback implements CloudUtil.OnPostRequest<ArrayList<EntityGoodsSelling>> {

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
            listView.postInvalidate();

            if (refreshData) {
                refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                refreshListView.onPullDownRefreshComplete();//上拉刷新完成
            }

            // 没有数据 [考虑到可能是上拉或者下拉，所以要设置刷新完成]
            if (goodsList.size() < 1) {
                frameLoading.showMessage("还没有已售的商品\n点击重新加载数据");//点击重试
                frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                    @Override
                    public void onFrameLoadingClicked() {
                        initData();
                    }
                });
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
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    refreshData(removeData, refreshData);
                }
            });
            Log.e(TAG, err);
        }
    }

    /**
     * 删除已售
     */
    class RemoveSoldCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        private EntityGoodsSelling obj;

        public RemoveSoldCallback(EntityGoodsSelling obj) {
            this.obj = obj;
        }

        @Override
        public void onPost() {
        }

        @Override
        public void onPostOk(final EntityHttpResponse result) {
            ToastUtil.showShortToast(context, "删除成功");
            goodsList.remove(obj);
            adapterGoodsList.notifyDataSetChanged();//有效

            // 清空了数据 测试过，没有问题
            if (goodsList.size() < 1) {
                frameLoading.showMessage("还没有已售的商品\n点击重新加载数据");//点击重试
                frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                    @Override
                    public void onFrameLoadingClicked() {
                        initData();
                    }
                });
            }
        }

        @Override
        public void onPostFailure(String err) {
            ToastUtil.showShortToast(context, "删除失败");
            Log.e(TAG, err);
        }
    }
}
