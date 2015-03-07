package com.erhuoapp.erhuo.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ActivityInfodata;
import com.erhuoapp.erhuo.activity.ActivitySetting;
import com.erhuoapp.erhuo.adapter.AdapterFragmentViewPager;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 个人信息
 * <p/>
 * NOTE:2015-1-22 这个类已经不使用了，用FragmentUserInfo代替
 * NOTE:2015-1-23 重新启用这个类，因为我发现了之前使用fragments产生bug的原因所在
 *
 * @author hujiawei
 * @datetime 15/1/2 21:11
 */
public class FragmentUserInfo extends Fragment implements View.OnClickListener, IFragment {

    private static final String TAG = "FragmentUserInfo";

    private int currentIndex = 0;//当前选中的选项
    private EntityUserInfo userInfo;

    private ViewPager viewPager;
    private TextView textViewName;
    private TextView textViewSchool;
    private TextView textViewGrade;
    private ImageView imageViewHeader;
    private ImageView imageViewAuth;

    private ArrayList<TextView> boards_text = new ArrayList<TextView>();
    private ArrayList<LinearLayout> boards_layout = new ArrayList<LinearLayout>();
    private ArrayList<ImageView> boards_images = new ArrayList<ImageView>();
    private ArrayList<IFragment> fragments = new ArrayList<IFragment>();

    private DisplayImageOptions displayImageOptions_header;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_userinfo, null);

        // 界面组件
        textViewName = (TextView) contentView.findViewById(R.id.tv_userinfo_name);
        textViewSchool = (TextView) contentView.findViewById(R.id.tv_userinfo_school);
        textViewGrade = (TextView) contentView.findViewById(R.id.tv_userinfo_grade);
        imageViewHeader = (ImageView) contentView.findViewById(R.id.iv_userinfo_header);
        imageViewAuth = (ImageView) contentView.findViewById(R.id.iv_userinfo_auth);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_userinfo_list);
        viewPager.setOffscreenPageLimit(4);
        
        //为头像添加跳转到个人信息设置的响应函数
        imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), ActivityInfodata.class), IConstants.REQUEST_SETTINGS);
                //startActivity(new Intent(this, ActivityInfodata.class));
            }
        });
        
        fragments.add(new FragmentUserSelling());//出售
        fragments.add(new FragmentUserBuying());//求购
        fragments.add(new FragmentUserFavorite());//收藏
        fragments.add(new FragmentUserSold());//已售
        //Log.e(TAG, "getChildFragmentManager() -> " + getChildFragmentManager().hashCode());
        viewPager.setAdapter(new AdapterFragmentViewPager(getChildFragmentManager(), fragments));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 顶部按钮
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_1));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_2));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_3));
        boards_layout.add((LinearLayout) contentView.findViewById(R.id.linearlayout_main_board_4));
        // 顶部按钮图片
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_1));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_2));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_3));
        boards_images.add((ImageView) contentView.findViewById(R.id.imageview_main_board_4));
        // 顶部按钮文字
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
                    // 跳转到页面  ---> 这里viewpager就会更新显示的fragment
                    viewPager.setCurrentItem(j);//让它去触发viewpager的onPageSelected，这样就可以避免两次refresh方法调用了
                }
            });
        }

        // 事件监听
        contentView.findViewById(R.id.ib_userinfo_settings).setOnClickListener(this);

        // 头像 image loader
        displayImageOptions_header = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.header_default)
                .showImageOnFail(R.drawable.header_default)
                .showImageOnLoading(R.drawable.header_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100, 3)).build();

        //selectButton(0);// FragmentUserInfo﹕ onCreateView -> FragmentUserXXX﹕ refresh 空指针异常
        //viewPager.setCurrentItem(0);//
        selectButton(0);//先选择0，这个时候FragmentUserSelling的refresh方法会被调用，但是不会发生任何响应

        // 暂时用应用内保存的用户数据显示这个界面
        userInfo = AppUtil.getInstance().getBasicUserInfo();
        showBasicUserInfo(userInfo);

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        MobclickAgent.onPageStart("MainScreen"); //统计页面
        
        // 用户可能通过其他的客户端或者途径修改了自己的个人信息，所以每次都需要重新加载
        new CloudUtil().UserInfo(new UserInfoCallback());//加载用户信息
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        MobclickAgent.onPageEnd("MainScreen"); 
    }

    @Override
    public void refresh() {
        Log.e(TAG, "refresh");
    }

    @Override
    public void fragmentRefresh() {
        Log.e(TAG, "fragmentRefresh");
    }

    /**
     * 点击顶部按钮切换页面
     */
    private void selectButton(int index) {//
        currentIndex = index;
        fragments.get(index).refresh();//fragment之间切换时的刷新  <---fix:这里有个bug会导致refresh方法调用两次

        // 切换按钮效果
        for (int i = 0; i < boards_layout.size(); i++) {
            if (index == i) {
                boards_layout.get(i).setEnabled(false);//
                boards_images.get(i).setEnabled(false);//imagebutton如果disable的话颜色会变
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_red));
            } else {
                boards_layout.get(i).setEnabled(true);
                boards_images.get(i).setEnabled(true);
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_gray));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_userinfo_settings://设置
                getActivity().startActivityForResult(new Intent(getActivity(), ActivitySetting.class), IConstants.REQUEST_SETTINGS);
                break;
        }
    }

    // 设置个人主页中的用户基本信息
    private void showBasicUserInfo(EntityUserInfo userInfo) {
        if (!"".equalsIgnoreCase(userInfo.getNickName())) {
            textViewName.setText(userInfo.getNickName());
        } else {
            textViewName.setText(userInfo.getName());
        }

        String school = userInfo.getSchool();
        if (school.equalsIgnoreCase("")) {
            textViewSchool.setVisibility(View.GONE);
        } else {
            textViewSchool.setVisibility(View.VISIBLE);
            textViewSchool.setText(school);
        }

        String grade = userInfo.getGrade();
        if (grade.equalsIgnoreCase("")) {
            textViewGrade.setVisibility(View.GONE);
        } else {
            textViewGrade.setVisibility(View.VISIBLE);
            textViewGrade.setText(grade);
        }

        String header = userInfo.getHeader();
        if (!header.equalsIgnoreCase("")) {//否则显示默认的头像
            ImageLoader.getInstance().displayImage(header, imageViewHeader, displayImageOptions_header);
        }

        if (!userInfo.getIsAuth()) {
            imageViewAuth.setVisibility(View.GONE);
        } else {
            imageViewAuth.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用户数据
     */
    class UserInfoCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityUserInfo temp) {
            userInfo = temp;
            showBasicUserInfo(temp);
            AppUtil.getInstance().saveUserInfo(temp);
        }

        @Override
        public void onPostFailure(String err) {
            //ToastUtil.showShortToast(getActivity(), "加载用户信息失败");
            Log.e(TAG, err);
        }
    }
}
