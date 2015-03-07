package com.erhuoapp.erhuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.erhuoapp.erhuo.DemoHXSDKHelper;
import com.erhuoapp.erhuo.ErHuoApplication;
import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.luminous.pick.MainActivity;

/**
 *
 * 应用启动时的加载界面
 *
 * @author hujiawei
 * @datetime 15/1/1 23:10
 */
public class ActivityLoading extends FragmentActivity {

    private final String TAG = "ActivityLoading";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        // 1.5秒后跳转到主页
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (AppUtil.getInstance().checkUserLogin()){//如果有用户信息存在那么就加载
                	if (DemoHXSDKHelper.getInstance().isLogined()) {
    					// ** 免登陆情况 加载所有本地群和会话
    					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
    					//加上的话保证进了主页面会话和群组都已经load完毕
    					EMGroupManager.getInstance().loadAllGroups();
    					EMChatManager.getInstance().loadAllConversations();
                	}
                    new CloudUtil().UserInfo(new CloudUtil.OnPostRequest<EntityUserInfo>(){
                        @Override
                        public void onPost() {

                        }

                        @Override
                        public void onPostOk(EntityUserInfo temp) {
                            AppUtil.getInstance().saveUserInfo(temp);
                            ErHuoApplication.getInstance().setUserName(temp.getId());
                            ErHuoApplication.getInstance().setPassword(temp.getId());
                        }

                        @Override
                        public void onPostFailure(String err) {
                            Log.e(TAG, err);
                        }
                    });//加载用户信息
                    
                }
                
                finish();
            }
        }, 1500);

    }
    

}
