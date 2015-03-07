package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityUpdate;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 设置界面
 *
 * @author hujiawei
 * @datetime 2015/1/20
 */
public class ActivitySetting extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "ActivitySetting";

    private Context context;
    private EntityUserInfo userInfo;
    private TextView textViewVersion;
    private RelativeLayout layoutExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;

        // 界面组件
        layoutExit = (RelativeLayout) findViewById(R.id.rl_settings_exit);
        textViewVersion = (TextView) findViewById(R.id.tv_settings_version);

        // 事件监听
        findViewById(R.id.ib_settings_return).setOnClickListener(this);
        findViewById(R.id.fl_settings_opinion).setOnClickListener(this);
        findViewById(R.id.fl_settings_account).setOnClickListener(this);
        findViewById(R.id.fl_settings_pwd).setOnClickListener(this);
        findViewById(R.id.fl_settings_about).setOnClickListener(this);
        findViewById(R.id.fl_settings_message).setOnClickListener(this);
        findViewById(R.id.fl_settings_recommend).setOnClickListener(this);
        findViewById(R.id.fl_settings_update).setOnClickListener(this);
        findViewById(R.id.tv_settings_exit).setOnClickListener(this);
        findViewById(R.id.btn_settings_back).setOnClickListener(this);
        findViewById(R.id.tv_settings_confirm).setOnClickListener(this);

        // 初始化
        layoutExit.setVisibility(View.INVISIBLE);
        textViewVersion.setText(IConstants.VERSION);

        // 数据 暂时没用
        userInfo = (EntityUserInfo) getIntent().getSerializableExtra("user");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_settings_return://返回
                finish();
                break;
            case R.id.tv_settings_exit://退出
                layoutExit.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_settings_confirm://退出
                exitUser();
                break;
            case R.id.btn_settings_back://乖乖返回
                layoutExit.setVisibility(View.INVISIBLE);
                break;
            case R.id.fl_settings_about://关于我们
                Intent intent = new Intent(this, ActivityBrowser.class);
                intent.putExtra("title", "关于我们");
                intent.putExtra("url", IConstants.ABOUT_US_URL);
                startActivity(intent);
                break;
            case R.id.fl_settings_account://账号信息
                startActivity(new Intent(this, ActivityInfodata.class));
                break;
            case R.id.fl_settings_pwd://修改密码
                startActivity(new Intent(this, ActivityChangePassword.class));
                break;
            case R.id.fl_settings_recommend://推荐给朋友
                //ToastUtil.showShortToast(this, "暂时木有");
                //startActivity(new Intent(this, ActivityLogin.class));
                UM_Share();
                break;
            case R.id.fl_settings_opinion://意见反馈
                startActivity(new Intent(this, ActivityOpinion.class));
                break;
            case R.id.fl_settings_update://检查更新
                checkUpdate();
                break;
        }
    }

    //友盟分享
    public void UM_Share(){
    	
    	//友盟分享
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        UMImage temp_share_image = new UMImage(this, R.drawable.icon512);
        
        
    	// 设置分享面板上显示的平台 
    	//mController.getConfig().setPlatforms(SHARE_MEDIA.RENREN);
    	//mController.getConfig().setPlatforms(SHARE_MEDIA.DOUBAN);
    	mController.getConfig().removePlatform( SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
    	mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
    	
    	// 微信平台
    	UMWXHandler wxHandler = new UMWXHandler(this,"wx73fef33b1a8e1e2e",
    			"74eeaf442cbe3658034f1036be1f2ffa");
    	wxHandler.addToSocialSDK();
    	
    	// 微信朋友圈
    	UMWXHandler wxCircleHandler = new UMWXHandler(this,"wx73fef33b1a8e1e2e",
    			"74eeaf442cbe3658034f1036be1f2ffa");
    	wxCircleHandler.setToCircle(true);
    	wxCircleHandler.addToSocialSDK();
    	
    	// QQ平台
    	UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104169970",
                "JSVF4URRWRHgsKkJ");
    	qqSsoHandler.addToSocialSDK();  
    	
    	// QQ空间
    	QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "1104169970",
                "JSVF4URRWRHgsKkJ");
    	qZoneSsoHandler.addToSocialSDK();
    	
    	
    	//设置微信好友分享内容
    	WeiXinShareContent weixinContent = new WeiXinShareContent();
    	//设置分享文字
    	weixinContent.setShareContent("贰货，让你的闲置物品转起来");
    	//设置title
    	weixinContent.setTitle("贰货——大学生闲置物品交易平台");
    	//设置分享内容跳转URL
    	weixinContent.setTargetUrl("http://www.erhuoapp.com");
    	//设置分享图片
    	weixinContent.setShareImage(temp_share_image);
    	mController.setShareMedia(weixinContent);
    	
    	//设置微信朋友圈分享内容
    	CircleShareContent circleMedia = new CircleShareContent();
    	circleMedia.setShareContent("贰货，让你的闲置物品转起来");
    	circleMedia.setTitle("贰货——大学生闲置物品交易平台");
    	circleMedia.setShareImage(temp_share_image);
    	circleMedia.setTargetUrl("http://www.erhuoapp.com");
    	mController.setShareMedia(circleMedia);
    	
    	//设置QQ分享内容
    	QQShareContent qqShareContent = new QQShareContent();
    	qqShareContent.setShareContent("贰货，让你的闲置物品转起来");
    	qqShareContent.setTitle("贰货——大学生闲置物品交易平台");
    	qqShareContent.setShareImage(temp_share_image);
    	qqShareContent.setTargetUrl("http://www.erhuoapp.com");
    	mController.setShareMedia(qqShareContent);
    	
    	// 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("贰货，让你的闲置物品转起来");
        qzone.setTargetUrl("http://www.erhuoapp.com");
        qzone.setTitle("贰货——大学生闲置物品交易平台");
        qzone.setShareImage(temp_share_image);
        mController.setShareMedia(qzone);
        
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent("贰货——大学生闲置物品交易平台\n贰货，让你的闲置物品转起来~\nhttp://www.erhuoapp.com");
        mController.setShareMedia(sinaContent);
    	
        // 分享触发
    	mController.openShare(this, false);
    }
    
    
    // 检查更新
    private void checkUpdate() {
        new CloudUtil().CheckUpdate(new CloudUtil.OnPostRequest<EntityUpdate>() {
            @Override
            public void onPost() {
                ToastUtil.showShortToast(context, "版本检查中...");
            }

            @Override
            public void onPostOk(EntityUpdate temp) {
                //TODO 比较版本号
                ToastUtil.showShortToast(context, "最新版本是" + temp.getVersion());
            }

            @Override
            public void onPostFailure(String err) {
                if (err.equalsIgnoreCase("no_package_available")) {
                    ToastUtil.showShortToast(context, "没有最新的安装包");
                } else {
                    ToastUtil.showShortToast(context, "检查更新失败");
                }
                Log.e(TAG, err);
            }
        });
    }

    // 用户退出当前账号
    public void exitUser() {
        AppUtil.getInstance().removeUserData();
        setResult(RESULT_OK);//只有这个时候返回OK
        finish();
    }

    // 处理该界面的返回键或者其他键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://返回键
                //如果是在提示中那么就退出提示
                if (layoutExit.getVisibility() == View.VISIBLE) {
                    layoutExit.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    super.onKeyDown(keyCode, event);
                }
        }
        return super.onKeyDown(keyCode, event);//
    }

}
