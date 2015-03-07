package com.erhuoapp.erhuo.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityLogin;
import com.erhuoapp.erhuo.activity.ActivityMain;
import com.erhuoapp.erhuo.activity.ActivitySelling;
import com.erhuoapp.erhuo.adapter.AdapterFragmentViewPager;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.view.MainViewPager;
import com.erhuoapp.erhuo.view.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

/**
 * 主界面，但是不处理首页中的内容，主要处理各个Tab页面的切换
 * 原本它是一个Activity，但是由于实现SlideMenu的缘故它成了一个Fragment
 *
 * @author hujiawei
 * @datetime 15/1/2 20:10
 */
public class FragmentMain extends Fragment implements View.OnClickListener, IConstants {

    private static final String TAG = "FragmentMain";

    private Context context;
    private SlidingMenu slidingMenu;
    private MainViewPager viewPager;
    private ArrayList<TextView> boards_text = new ArrayList<TextView>();
    private ArrayList<LinearLayout> boards_layout = new ArrayList<LinearLayout>();
    private ArrayList<ImageView> boards_images = new ArrayList<ImageView>();
    private static ArrayList<IFragment> fragments = new ArrayList<IFragment>();

    public static int oldIndex = 0;
    public static int nowIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        context = getActivity();
        View contentView = inflater.inflate(R.layout.fragment_main, null);
        contentView.setOnClickListener(this);

        viewPager = (MainViewPager) contentView.findViewById(R.id.viewpager_main);
        //viewPager.setScrollable(true);//不能滑动！界面中会出现轻微的滑动就是和它有关，必须是false
        viewPager.setOffscreenPageLimit(4);//全部初始化，然后不销毁！
        //NOTE: 这里是最大的坑！本以为不会销毁，但貌似还是销毁了fragment中的一些资源！
        //setOffscreenPageLimit和在adapter中屏蔽destroy作用不等价

        // 四个页面
        fragments.add(new FragmentHome());//首页
        fragments.add(new FragmentBuying());//求购
//        fragments.add(new FragmentMessage());//消息
        fragments.add(new ChatAllHistoryFragment());//通话列表
        
        fragments.add(new FragmentUserInfo());//个人信息
        //Log.e(TAG, "getChildFragmentManager() -> " + getChildFragmentManager().hashCode());
        viewPager.setAdapter(new AdapterFragmentViewPager(getChildFragmentManager(), fragments));
        // 底部按钮
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_1));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_2));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_3));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_4));
        // 底部按钮图片
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_1));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_2));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_3));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_4));
        // 底部按钮文字
        boards_text.add((TextView) contentView.findViewById(R.id.textview_main_board_1));
        boards_text.add((TextView) contentView.findViewById(R.id.textview_main_board_2));
        boards_text.add((TextView) contentView.findViewById(R.id.textview_main_board_3));
        boards_text.add((TextView) contentView.findViewById(R.id.textview_main_board_4));

        // 按钮点击事件
        for (int i = 0; i < boards_layout.size(); i++) {
            final int j = i;
            boards_layout.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectButton(j);//这里因为viewpager不是可以滑动的，所以没有pageSelected事件监听，所以这里直接调用selectButton方法
                    nowIndex = j;
                }
            });
        }
        // 中间我要卖按钮
        contentView.findViewById(R.id.linearlayout_main_board_5).setOnClickListener(this);
        // 左侧滑动菜单
        slidingMenu = ((ActivityMain) getActivity()).getSlidingMenu();
        selectButton(0);

        return contentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach " + activity.hashCode());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        MobclickAgent.onPageStart("MainScreen"); //统计页面
    }
    
    @Override
	public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        MobclickAgent.onPageEnd("MainScreen"); 
    }

    // 在账号退出之后重新刷新
    public void refreshAfterUserExit() {
        selectButton(0);//进入第1个界面
    }

    // 在发布或者更新求购之后重新刷新
    public void refreshAfterBuying() {
        fragments.get(1).fragmentRefresh();//内部刷新一下求购界面
    }

    public static void refresh(){
    	if (nowIndex == oldIndex) {
    		if(nowIndex == 2){
    			 fragments.get(nowIndex).fragmentRefresh();//fragment内部进行刷新
    		}
           
        } else {
        	if(nowIndex == 2){
        		 fragments.get(nowIndex).refresh();//fragment之间切换时的刷新
    		}
           
        }
    }
    
    /**
     * 点击底部按钮切换页面
     */
    private void selectButton(int index) {// 用户未登录状态下不能点击后面两个按钮
        // 消息和个人中心都需要用户登录之后才可以查看
        if (index > 1 && !AppUtil.getInstance().checkUserLogin()) {//2或者3 并且没有用户登录数据
            getActivity().startActivityForResult(new Intent(getActivity(), ActivityLogin.class), REQUEST_LOGIN);
            return;
        }
        // 如果有用户登录数据或者点击的是前面两个page
        slidingMenu.closeLeftMenu();//如果当前正打开着菜单就先关闭
        // 当前情况是如果在首页，点击之后无效；但是点击求购会关闭菜单进入求购界面
        if (index != 0) {//只要不是在首页，其他3个Tab界面都不能滑出左侧菜单
            slidingMenu.setCanSlideLeft(false);
        }
        if (index == 0) {
        	//fragments.set(0, new FragmentHome());//原本想直接用一个新的首页替换，但无效
        	fragments.get(0).fragmentRefresh();//后来发现可以调用fragmentRefresh()函数
        }
        viewPager.setCurrentItem(index);// 跳转到页面
        // 四个Fragment在调用完各自的onCreateView和onResume之后，以后每次点击进入的时候都是调用refresh方法
        if (index == oldIndex) {
        	if(nowIndex == 2){
        		fragments.get(index).fragmentRefresh();//fragment内部进行刷新
        	}
            
        } else {
        	if(nowIndex == 2){
        		 fragments.get(index).refresh();//fragment之间切换时的刷新
        	}
           
            oldIndex = index;
        }
        // 切换按钮效果
        for (int i = 0; i < boards_layout.size(); i++) {
            if (index == i) {
                //boards_layout.get(i).setEnabled(false);//不设置因为让用户依然可以点击然后回到首页
                boards_images.get(i).setEnabled(false);//imagebutton如果disable的话颜色会变
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_red));
            } else {
                boards_images.get(i).setEnabled(true);
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_gray));
            }
        }
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.linearlayout_main_board_5) {// 我要卖
            // 判断是否有用户数据存在
            if (!AppUtil.getInstance().checkUserLogin()) {
                getActivity().startActivityForResult(new Intent(getActivity(), ActivityLogin.class), REQUEST_LOGIN);
            } else {
                //ToastUtil.showShortToast(context, "开发中...");
                getActivity().startActivityForResult(new Intent(getActivity(), ActivitySelling.class), REQUEST_LOGIN);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");//接收不到
        super.onActivityResult(requestCode, resultCode, data);//即使加了这个子Fragment的onActivityResult还是不会被调用
        //打算由ActivityMain接收然后调用FragmentMain中的方法来控制 -> 暂时是在ActivityMain中调用FragmentMain中的方法

        //NOTE:2015/1/28 只有当子Fragment中调用getParentFragment().startActivity这里才会经过ActivityMain然后被调用到这里
        //貌似没有办法再从这里将请求发给子Fragment
        if (requestCode == REQUEST_BUYING_PUBLISH) {//发布求购
            if (resultCode == Activity.RESULT_OK) {
                //ToastUtil.showShortToast(context, "发布成功");
                refreshAfterBuying();
            }
        }

    }
}
