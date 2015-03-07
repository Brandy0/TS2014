package com.erhuoapp.erhuo.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterFragmentViewPager;
import com.erhuoapp.erhuo.fragment.FragmentOtherBuying;
import com.erhuoapp.erhuo.fragment.FragmentOtherSelling;
import com.erhuoapp.erhuo.fragment.FragmentOtherSold;
import com.erhuoapp.erhuo.fragment.IFragment;
import com.erhuoapp.erhuo.im.db.HeadAndName;
import com.erhuoapp.erhuo.im.domain.UserHeadAndName;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;


/**
 * 他人的个人主页界面
 *
 * TODO:如果进入的时候加载用户信息出错的话，三个列表旁边的数字就会不再显示了
 *
 * @author hujiawei
 * @datetime 2015/1/20
 */
public class ActivityOther extends FragmentActivity implements IConstants, View.OnClickListener {

    private static final String TAG = "ActivityOther";

    private EntityUserInfo user;
    private int currentIndex = 0;//当前选中的选项
    private ViewPager viewPager;
    private TextView textViewName;
    private TextView textViewSchool;
    private TextView textViewGrade;
    private ImageView imageViewHeader;
    private ImageView imageViewAuth;
    private TextView textViewSoldNum;
    private TextView textViewBuyingNum;
    private TextView textViewSellingNum;

    private ArrayList<TextView> boards_text = new ArrayList<TextView>();
    private ArrayList<TextView> boards_num_text = new ArrayList<TextView>();
    private ArrayList<IFragment> fragments = new ArrayList<IFragment>();
    private DisplayImageOptions displayImageOptions_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");
        View contentView = getLayoutInflater().inflate(R.layout.activity_other, null);
        setContentView(contentView);

        // 界面组件
        textViewName = (TextView) contentView.findViewById(R.id.tv_other_name);
        textViewSchool = (TextView) contentView.findViewById(R.id.tv_other_school);
        textViewGrade = (TextView) contentView.findViewById(R.id.tv_other_grade);
        imageViewHeader = (ImageView) contentView.findViewById(R.id.iv_other_header);
        imageViewAuth = (ImageView) contentView.findViewById(R.id.iv_other_auth);
        textViewSellingNum = (TextView) contentView.findViewById(R.id.tv_other_sellingnum);
        textViewBuyingNum = (TextView) contentView.findViewById(R.id.tv_other_buyingnum);
        textViewSoldNum = (TextView) contentView.findViewById(R.id.tv_other_soldnum);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_other_list);
        
        //聊天触发
        contentView.findViewById(R.id.ib_other_chat).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!AppUtil.getInstance().checkUserLogin()) {//没有用户登录数据
					ActivityOther.this.startActivityForResult(new Intent(ActivityOther.this, ActivityLogin.class), REQUEST_LOGIN);
		            return;
		        }
				else {
					String uid = user.getId();
					startActivity(new Intent(ActivityOther.this, ChatActivity.class)
					.putExtra("userId", uid)
					);
				}
			}
		});
        viewPager.setOffscreenPageLimit(3);

        // 获取用户 对于Fragment的初始化其实也可以在AdapterOtherFragmentViewPager中做
        // getItem()初始化Fragment，instantiateItem()中给Fragment传递参数
        user = (EntityUserInfo) getIntent().getSerializableExtra("user");
        Bundle bundle = new Bundle();
        bundle.putString("uid", user.getId());
        FragmentOtherSelling fragmentOtherSelling = new FragmentOtherSelling();
        fragmentOtherSelling.setArguments(bundle);
        FragmentOtherBuying fragmentOtherBuying = new FragmentOtherBuying();
        fragmentOtherBuying.setArguments(bundle);
        FragmentOtherSold fragmentOtherSold = new FragmentOtherSold();
        fragmentOtherSold.setArguments(bundle);

        fragments.add(fragmentOtherSelling);//出售
        fragments.add(fragmentOtherBuying);//求购
        fragments.add(fragmentOtherSold);//已售
        viewPager.setAdapter(new AdapterFragmentViewPager(getSupportFragmentManager(), fragments));
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

        // 按钮组
        boards_text.add((TextView) contentView.findViewById(R.id.tv_other_selling));
        boards_text.add((TextView) contentView.findViewById(R.id.tv_other_buying));
        boards_text.add((TextView) contentView.findViewById(R.id.tv_other_sold));
        boards_num_text.add(textViewSellingNum);
        boards_num_text.add(textViewBuyingNum);
        boards_num_text.add(textViewSoldNum);

        // 按钮点击事件
        for (int i = 0; i < boards_text.size(); i++) {
            final int j = i;
            boards_text.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到页面  ---> 这里viewpager就会更新显示的fragment
                    viewPager.setCurrentItem(j);//让它去触发viewpager的onPageSelected，这样就可以避免两次refresh方法调用了
                }
            });
            boards_num_text.get(i).setVisibility(View.INVISIBLE);
        }

        // 事件监听
        contentView.findViewById(R.id.ib_other_return).setOnClickListener(this);

        // 头像
        displayImageOptions_header = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.header_default)
                .showImageOnFail(R.drawable.header_default)
                .showImageOnLoading(R.drawable.header_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100, 3)).build();

        // 初始化数据
        selectButton(0);
        showBasicUserInfo(user);
        String header = user.getHeader();
        if (!header.equalsIgnoreCase("")) {//否则显示默认的头像
            ImageLoader.getInstance().displayImage(header, imageViewHeader, displayImageOptions_header);
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", user.getId());
        new CloudUtil().OtherUserInfo(params, new OtherUserInfoCallback());//加载用户信息
        
        HeadAndName mHeadAndName = new HeadAndName(ActivityOther.this);
        UserHeadAndName use = new UserHeadAndName();
        use.setHead(user.getHeader());
        use.setNick(user.getNickName());
        use.setUserId(user.getId());
		mHeadAndName.saveContact(use );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        MobclickAgent.onResume(this);//友盟统计onResume
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        MobclickAgent.onPause(this);//友盟统计onPause
        
    }

    /**
     * 点击顶部按钮切换页面
     */
    private void selectButton(int index) {//
        currentIndex = index;
        fragments.get(index).refresh();//fragment之间切换时的刷新  <---fix:这里有个bug会导致refresh方法调用两次

        // 切换按钮效果
        for (int i = 0; i < boards_text.size(); i++) {
            if (index == i) {
                boards_text.get(i).setEnabled(false);//
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_white));
                boards_num_text.get(i).setTextColor(getResources().getColor(R.color.main_white));
            } else {
                boards_text.get(i).setEnabled(true);
                boards_text.get(i).setTextColor(getResources().getColor(R.color.main_brightgray));
                boards_num_text.get(i).setTextColor(getResources().getColor(R.color.main_brightgray));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_other_return:
                finish();
                break;
        }
    }

    // 设置个人主页中的用户基本信息
    private void showBasicUserInfo(EntityUserInfo userInfo) {
        if (!"".equalsIgnoreCase(userInfo.getNickName())) {
            textViewName.setText(userInfo.getNickName());
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

        if (!userInfo.getIsAuth()) {
            imageViewAuth.setVisibility(View.GONE);//
        } else {
            imageViewAuth.setVisibility(View.VISIBLE);
        }

        String sellingNum = userInfo.getSellingnum();
        if (sellingNum.equalsIgnoreCase("")) {
            textViewSellingNum.setVisibility(View.GONE);
        } else {
            textViewSellingNum.setVisibility(View.VISIBLE);
            textViewSellingNum.setText(sellingNum);
        }

        String buyingNum = userInfo.getBuyingnum();
        if (buyingNum.equalsIgnoreCase("")) {
            textViewBuyingNum.setVisibility(View.GONE);
        } else {
            textViewBuyingNum.setVisibility(View.VISIBLE);
            textViewBuyingNum.setText(buyingNum);
        }

        String soldNum = userInfo.getSoldnum();
        if (soldNum.equalsIgnoreCase("")) {
            textViewSoldNum.setVisibility(View.GONE);
        } else {
            textViewSoldNum.setVisibility(View.VISIBLE);
            textViewSoldNum.setText(soldNum);
        }
    }

    /**
     * 用户数据
     */
    class OtherUserInfoCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityUserInfo temp) {
            showBasicUserInfo(temp);
        }

        @Override
        public void onPostFailure(String err) {
            ToastUtil.showShortToast(ActivityOther.this, "加载用户信息失败");
            Log.e(TAG, err);
        }
    }

}
