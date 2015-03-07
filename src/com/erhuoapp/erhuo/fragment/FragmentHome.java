package com.erhuoapp.erhuo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityBrowser;
import com.erhuoapp.erhuo.activity.ActivityGoodsSellingInfo;
import com.erhuoapp.erhuo.activity.ActivityLogin;
import com.erhuoapp.erhuo.activity.ActivityMain;
import com.erhuoapp.erhuo.activity.ActivitySearch;
import com.erhuoapp.erhuo.activity.ActivitySetting;
import com.erhuoapp.erhuo.adapter.AdapterFocusViewPager;
import com.erhuoapp.erhuo.adapter.AdapterGridClassify;
import com.erhuoapp.erhuo.adapter.AdapterMenuClassify;
import com.erhuoapp.erhuo.model.EntityGoodsClassify;
import com.erhuoapp.erhuo.model.EntityGoodsFocus;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.CloudUtil.OnPostRequest;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * <p/>
 *
 * @author hujiawei
 * @datetime 15/1/2 21:10
 */
public class FragmentHome extends Fragment implements IConstants, View.OnClickListener, IFragment {

    private static final String TAG = "FragmentHome";

    private boolean isHomeDataLoaded = false;//首页的数据是否已经加载过
    private boolean isInClassify = false;//不在商品类别列表界面

    private Context context;
    private LinearLayout homeContent;
    private HomeClassify homeClassify;
    private FrameLoading frameLoading;

    private GridView gridView;
    private LinearLayout focusPoints;
    private ImageView charLogoErhuo;
    private EditText editTextSearch;
    private ViewPager viewPager;
    private TextView textViewTitle, textViewMoney;

    private SlidingMenu slidingMenu;
    private FragmentMenu fragmentMenu;
    private ArrayList<ImageView> points = new ArrayList<ImageView>();
    private AdapterMenuClassify adapterMenuClassify;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        context = getActivity();
        View contentView = inflater.inflate(R.layout.fragment_home, null);

        // 界面组件
        charLogoErhuo = (ImageView)	contentView.findViewById(R.id.char_logo_erhuo);
        editTextSearch = (EditText) contentView.findViewById(R.id.et_home_search);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_home_focus);
        gridView = (GridView) contentView.findViewById(R.id.gridview_home_classify);
        focusPoints = (LinearLayout) contentView.findViewById(R.id.ll_focus_points);
        textViewTitle = (TextView) contentView.findViewById(R.id.tv_focus_title);
        textViewMoney = (TextView) contentView.findViewById(R.id.tv_focus_price);
        homeContent = (LinearLayout) contentView.findViewById(R.id.ll_home_content);

        // 首页有加载等待界面，homeClassify内部也有一个加载等待界面
        frameLoading = new FrameLoading(contentView);//
        homeClassify = new HomeClassify(getActivity(), contentView);

        // 事件监听
        editTextSearch.setOnClickListener(this);
        contentView.findViewById(R.id.char_logo_erhuo).setOnClickListener(this); 
        contentView.findViewById(R.id.ib_home_search).setOnClickListener(this);
        contentView.findViewById(R.id.ib_classify_menu).setOnClickListener(this);

        // 左侧滑动菜单
        slidingMenu = ((ActivityMain) getActivity()).getSlidingMenu();
        fragmentMenu = ((ActivityMain) getActivity()).getFragmentMenu();

        return contentView;
    }

    //如果进入某个商品详情之后返回的话，ActivityMain -> FragmentMain -> FragmentHome 的onResume方法依次会被调用
    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面

        slidingMenu.closeLeftMenu();//如果菜单开着，先关闭它

        // 这三行很重要，默认进来的时候不是在列表界面所以会进入首页加载数据
        if (!isInClassify) {//如果本来是在列表界面，那就显示原来的列表就行
            gotoHomePage();//如果不是那就显示首页
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach " + activity.hashCode());
    }

    @Override
    public void refresh() {
        Log.e(TAG, "refresh");

        if (isInClassify) {//如果是从其他fragment切换重新进入的话，如果原来是在商品列表中的话是可以滑出左侧菜单
            slidingMenu.setCanSlideLeft(true);
        }
    }

    @Override
    public void fragmentRefresh() {
        Log.e(TAG, "fragmentRefresh");

        if (isInClassify) {
            gotoHomePage();//首页内部如果在列表页点击了首页按钮要能够回到首页，如果本来就是首页那就不执行任何操作
        }
    }

    // 进入首页中的商品类别界面
    private void gotoClassify(EntityGoodsClassify classify, int position) {
        adapterMenuClassify.setCurrentPosition(position);//告诉菜单栏选中的类别发生了变化
        fragmentMenu.getListViewMenu().invalidateViews();//不是使用invalidate()!!!

        homeContent.setVisibility(View.INVISIBLE);
        homeClassify.show(classify);

        isInClassify = true;
        slidingMenu.setCanSlideLeft(true);
        slidingMenu.closeLeftMenu();
    }

    // 进入首页原始的内容
    private void gotoHomePage() {
        if (!isHomeDataLoaded) {//为了防止又重新加载数据
            homeClassify.hide();
            frameLoading.showFrame();
            homeContent.setVisibility(View.INVISIBLE);
            new CloudUtil().GoodsClassify(new GoodsClassifyCallback());// 分类列表
            new CloudUtil().GoodsFocus(new GoodsFocusCallback());// 焦点图
        } else {
            homeClassify.hide();
            frameLoading.hideFrame();
            homeContent.setVisibility(View.VISIBLE);
        }

        isInClassify = false;
        slidingMenu.setCanSlideLeft(false);
        slidingMenu.closeLeftMenu();
    }

    /**
     * 焦点图监听事件
     */
    class GoodsFocusCallback implements OnPostRequest<ArrayList<EntityGoodsFocus>> {

        @Override
        public void onPost() {
            frameLoading.setMessage("数据加载中...");
        }

        @Override
        public void onPostOk(final ArrayList<EntityGoodsFocus> temp) {
            frameLoading.hideFrame();// 关闭动画
            focusPoints.removeAllViews();//防止多次加载出现数量的叠加
            points = new ArrayList<ImageView>();

            //添加相应数目个的小圆点
            for (int i = 0; i < temp.size(); i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setPadding(0, 0, 10, 0);
                points.add(imageView);
                focusPoints.addView(imageView);//多次加载可能会重复叠加
            }
            updatePoints(0, temp);
            // NOTE：因为首页的数据在应用启动之后一般不会重复加载，所以这里加载成功之后new adapter，而不是一开始就定义adapter
            AdapterFocusViewPager focusViewPager = new AdapterFocusViewPager(getActivity(), temp);
            focusViewPager.setListener(new AdapterFocusViewPager.ItemClickedListener() {
                @Override
                public void onItemClicked(EntityGoodsFocus obj) {
                    if (obj.getTarget() == 0) {//进入商品详情
                        EntityGoodsSelling goods = new EntityGoodsSelling();
                        goods.setId(obj.getId());
                        Intent intent = new Intent(context, ActivityGoodsSellingInfo.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods", goods);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else if (obj.getTarget() == 1) {//打开指定的网址
                        Intent intent = new Intent(context, ActivityBrowser.class);
                        intent.putExtra("title", obj.getTitle());
                        intent.putExtra("url", obj.getUrl());
                        context.startActivity(intent);
                    } else if (obj.getTarget() == 2) {
                        //ToastUtil.showShortToast(context, obj.getTargetCid() + " " + obj.getTargetName());
                        EntityGoodsClassify entityGoodsClassify = new EntityGoodsClassify();
                        entityGoodsClassify.setId(obj.getTargetCid());
                        entityGoodsClassify.setName(obj.getTargetName());
                        gotoClassify(entityGoodsClassify, -1);//-1表示一个没有的position，不会有问题
                    }
                }
            });
            viewPager.setAdapter(focusViewPager);
            viewPager.setCurrentItem(0);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    updatePoints(position, temp);
                }

                public void onPageScrollStateChanged(int state) {
                }
            });

        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    gotoHomePage();
                }
            });
            Log.e(TAG, err);
        }
    }

    // 更新小圆点
    private void updatePoints(int position, List<EntityGoodsFocus> list) {
        if (points.size() < 1) return;
        for (int i = 0; i < points.size(); i++) {
            if (position == i) {
                points.get(i).setImageResource(R.drawable.round_point);//chosen
            } else {
                points.get(i).setImageResource(R.drawable.round_point_normal);
            }
        }
        textViewTitle.setText(list.get(position).getTitle());
        textViewMoney.setText(" ￥ " + list.get(position).getPrice());
    }

    /**
     * 分类列表监听
     */
    class GoodsClassifyCallback implements OnPostRequest<ArrayList<EntityGoodsClassify>> {

        @Override
        public void onPost() {
            frameLoading.showFrame();
        }

        @Override
        public void onPostOk(final ArrayList<EntityGoodsClassify> list) {
            isHomeDataLoaded = true;//加载过数据了

            frameLoading.hideFrame();// 关闭动画
            homeContent.setVisibility(View.VISIBLE);
            //设置显示在左侧菜单栏的类别列表
            adapterMenuClassify = new AdapterMenuClassify(getActivity(), 0, list);
            fragmentMenu.getListViewMenu().setAdapter(adapterMenuClassify);
            fragmentMenu.getListViewMenu().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gotoClassify(list.get(position), position);
                }
            });

            // 设置显示在首页的类别列表
            AdapterGridClassify gridClassify = new AdapterGridClassify(getActivity(), 0, list);
            gridView.setAdapter(gridClassify);//中间值为resource，给0即可
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gotoClassify(list.get(position), position);
                }
            });
        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    gotoHomePage();
                }
            });
            Log.e(TAG, err);
        }
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick");
        switch (v.getId()) {
            case R.id.ib_home_search://搜索
            case R.id.et_home_search:
                startActivity(new Intent(getActivity(), ActivitySearch.class));
                break;
            case R.id.ib_classify_menu://菜单
                slidingMenu.showLeftView();//两次点击能够收回左侧菜单
                break;
            case R.id.char_logo_erhuo:
            	if (!AppUtil.getInstance().checkUserLogin()) {//没有用户登录数据
                    getActivity().startActivityForResult(new Intent(getActivity(), ActivityLogin.class), REQUEST_LOGIN);
                    return;
                }
            	startActivity(new Intent(getActivity(), ActivitySetting.class));
            	break;
        }
    }

}
