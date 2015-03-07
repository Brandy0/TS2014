package com.erhuoapp.erhuo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.erhuoapp.erhuo.Constant;
import com.erhuoapp.erhuo.ErHuoApplication;
import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.im.db.UserDao;
import com.erhuoapp.erhuo.im.domain.User;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 登录界面
 *
 * @author hujiawei
 * @datetime 2015/1/3
 */
public class ActivityLogin extends FragmentActivity implements View.OnClickListener, IConstants {

    private static final String TAG = "ActivityLogin";

    private EditText textPhone;
    private EditText textPassword;
    private FrameWaiting frameWaiting;

    private String phone;
    private String password;
    private final HashMap<String, String> params = new HashMap<String, String>();

    private final HashMap<String, String> paramsUMQQ = new HashMap<String, String>();
    private final HashMap<String, String> paramsUMWeChat = new HashMap<String, String>();
    private final HashMap<String, String> paramsUMWeiBo = new HashMap<String, String>();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = getLayoutInflater().inflate(R.layout.activity_login, null);
        setContentView(contentView);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        textPhone = (EditText) findViewById(R.id.et_login_name);
        textPassword = (EditText) findViewById(R.id.et_login_password);

        // 事件监听
        findViewById(R.id.btn_login_login).setOnClickListener(this);
        findViewById(R.id.tv_login_register).setOnClickListener(this);
        findViewById(R.id.tv_login_forget).setOnClickListener(this);
        findViewById(R.id.iv_login_qq).setOnClickListener(this);
        findViewById(R.id.iv_login_weibo).setOnClickListener(this);
        findViewById(R.id.iv_login_weixin).setOnClickListener(this);

        // 进入时关闭它
        frameWaiting.hideFrame();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login://登录
                doLogin();
                break;
            case R.id.tv_login_register://注册
                startActivityForResult(new Intent(this, ActivityRegister.class), IConstants.REQUEST_REGISTER);
                break;
            case R.id.tv_login_forget://忘记密码
                startActivity(new Intent(this, ActivityResetPassword.class));
                break;
            case R.id.iv_login_qq://qq登录 TODO
                LoginUMQQ();
                GetUserInfo();
                break;
            case R.id.iv_login_weixin://微信登录 TODO
                LoginUMWeChat();
                GetUserInfo();
                break;
            case R.id.iv_login_weibo://微博登录 TODO
                LoginWeiBo();
                GetUserInfo();
                break;
        }
    }
    
    public void GetUserInfo(){
    	new CloudUtil().UserInfo(new CloudUtil.OnPostRequest<EntityUserInfo>() {
            @Override
            public void onPost() {
            }

            @Override
            public void onPostOk(EntityUserInfo temp) {
                AppUtil.getInstance().saveUserInfo(temp);
            }

            @Override
            public void onPostFailure(String err) {
            }
        });//加载用户信息
    }

    public void LoginUMQQ() {
        String appId = "1104169970";
        String  appKey = "JSVF4URRWRHgsKkJ";

        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ActivityLogin.this, appId,
                appKey);
        qqSsoHandler.addToSocialSDK();

        mController.doOauthVerify(ActivityLogin.this, SHARE_MEDIA.QQ, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权开始", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权错误", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权完成", Toast.LENGTH_SHORT).show();

                StringBuilder sb = new StringBuilder();
                Set<String> keys = value.keySet();
                for(String key : keys){
                    sb.append(key+"="+value.get(key).toString()+"\r\n");
                }
                Log.d("TestData",sb.toString());

                paramsUMQQ.put("openid", value.get("openid").toString());
                paramsUMQQ.put("third_party", "qq");

                /*
                ToastUtil.showLongToast(ActivityLogin.this, sb.toString());

                AlertDialog.Builder alerthelp = new AlertDialog.Builder(ActivityLogin.this);
                alerthelp.setTitle("帮助");
                alerthelp.setMessage(sb.toString());
                alerthelp.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alerthelp.show();
                */

                //获取相关授权信息
                mController.getPlatformInfo(ActivityLogin.this, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(ActivityLogin.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if(status == 200 && info != null){
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for(String key : keys){
                                sb.append(key+"="+info.get(key).toString()+"\r\n");
                            }
                            Log.d("TestData",sb.toString());

                            paramsUMQQ.put("nickname", info.get("screen_name").toString());
                            paramsUMQQ.put("header", info.get("profile_image_url").toString());
                            if(info.get("gender").toString() == "男"){
                                paramsUMQQ.put("sex", "1");
                            }
                            else {
                                paramsUMQQ.put("sex", "0");
                            }

                            new CloudUtil().ThirdRegister(paramsUMQQ, new LoginCallback());

                            StringBuilder sb2 = new StringBuilder();
                            Set<String> keys2 = paramsUMQQ.keySet();
                            for(String key : keys2){
                                sb2.append(key+"="+paramsUMQQ.get(key).toString()+"\r\n");
                            }

                            /*

                            AlertDialog.Builder alerthelp = new AlertDialog.Builder(ActivityLogin.this);
                            alerthelp.setTitle("帮助");
                            alerthelp.setMessage(sb2.toString());
                            alerthelp.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });
                            alerthelp.show();

                            /*

                            /*
                            ToastUtil.showLongToast(ActivityLogin.this, sb.toString());

                            AlertDialog.Builder alerthelp = new AlertDialog.Builder(ActivityLogin.this);
                            alerthelp.setTitle("帮助");
                            alerthelp.setMessage(sb.toString());
                            alerthelp.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });
                            alerthelp.show();
                            */

                        }else{
                            Log.d("TestData","发生错误："+status);
                        }
                    }
                });
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        } );
    }

    private void  LoginUMWeChat() {
        String appId = "wx73fef33b1a8e1e2e";
        String  appSecret = "74eeaf442cbe3658034f1036be1f2ffa";

        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        UMWXHandler wxHandler = new UMWXHandler(ActivityLogin.this,appId,appSecret);
        wxHandler.addToSocialSDK();

        mController.doOauthVerify(ActivityLogin.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权开始", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权错误", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权完成", Toast.LENGTH_SHORT).show();
                //获取相关授权信息
                mController.getPlatformInfo(ActivityLogin.this, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(ActivityLogin.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if(status == 200 && info != null){
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for(String key : keys){
                                sb.append(key+"="+info.get(key).toString()+"\r\n");
                            }
                            Log.d("TestData",sb.toString());

                            paramsUMWeChat.put("openid", info.get("openid").toString());
                            paramsUMWeChat.put("third_party", "wechat");
                            paramsUMWeChat.put("nickname", info.get("nickname").toString());
                            paramsUMWeChat.put("header", info.get("headimgurl").toString());
                            paramsUMWeChat.put("sex", info.get("sex").toString());

                            new CloudUtil().ThirdRegister(paramsUMWeChat, new LoginCallback());

                            /*
                            ToastUtil.showLongToast(ActivityLogin.this, sb.toString());

                            AlertDialog.Builder alerthelp = new AlertDialog.Builder(ActivityLogin.this);
                            alerthelp.setTitle("帮助");
                            alerthelp.setMessage(sb.toString());
                            alerthelp.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });
                            alerthelp.show();
                            */

                        }else{
                            Log.d("TestData","发生错误："+status);
                        }
                    }
                });
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(ActivityLogin.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        } );

    }

    private void  LoginWeiBo() {

        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

        mController.doOauthVerify(ActivityLogin.this, SHARE_MEDIA.SINA,new UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(ActivityLogin.this, "授权成功.",Toast.LENGTH_SHORT).show();
                    mController.getPlatformInfo(ActivityLogin.this, SHARE_MEDIA.SINA, new UMDataListener() {
                        @Override
                        public void onStart() {
                            Toast.makeText(ActivityLogin.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onComplete(int status, Map<String, Object> info) {
                            if(status == 200 && info != null){
                                StringBuilder sb = new StringBuilder();
                                Set<String> keys = info.keySet();
                                for(String key : keys){
                                    sb.append(key+"="+info.get(key).toString()+"\r\n");
                                }
                                Log.d("TestData",sb.toString());

                                paramsUMWeiBo.put("openid", info.get("uid").toString());
                                paramsUMWeiBo.put("third_party", "sina");
                                paramsUMWeiBo.put("nickname", info.get("screen_name").toString());
                                paramsUMWeiBo.put("header", info.get("profile_image_url").toString());
                                paramsUMWeiBo.put("sex", info.get("gender").toString());

                                new CloudUtil().ThirdRegister(paramsUMWeiBo, new LoginCallback());

                                /*
                                ToastUtil.showLongToast(ActivityLogin.this, sb.toString());

                                AlertDialog.Builder alerthelp = new AlertDialog.Builder(ActivityLogin.this);
                                alerthelp.setTitle("帮助");
                                alerthelp.setMessage(sb.toString());
                                alerthelp.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });
                                alerthelp.show();
                                */

                            }else{
                                Log.d("TestData","发生错误："+status);
                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivityLogin.this, "授权失败",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {}
            @Override
            public void onStart(SHARE_MEDIA platform) {}
        });


    }

    private void doLogin() {
        if (validateInput()) {//验证输入
            params.put("phone", phone);
            params.put("pwd", AppUtil.encodeWithMD5(password));
            //Log.e(TAG, "pwd = " + AppUtil.encodeWithMD5(password));
            new CloudUtil().Login(params, new LoginCallback());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IConstants.REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {//注册成功就算是登陆成功
                setResult(RESULT_OK);//本身进入登陆就是要返回结果的
                finish();
            }
        }
    }
    
    // 登录事件的回调
    class LoginCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {

        @Override
        public void onPost() {
            frameWaiting.showMessage("登录中，请等待...");
        }

        @Override
        public void onPostOk(EntityUserInfo temp) {
            final String getInput =  AppUtil.getInstance().getUserId();
            if(getInput != null){

    			// 调用sdk登陆方法登陆聊天服务器
    			EMChatManager.getInstance().login(getInput, getInput, new EMCallBack() {

    				@Override
    				public void onSuccess() {
    					
    					// 登陆成功，保存用户名密码
    					ErHuoApplication.getInstance().setUserName(getInput);
    					ErHuoApplication.getInstance().setPassword(getInput);
    					try {
    						// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
    						// ** manually load all local groups and
    						// conversations in case we are auto login
    						EMGroupManager.getInstance().loadAllGroups();
    						EMChatManager.getInstance().loadAllConversations();
    						//处理好友和群组
    						processContactsAndGroups();
    					} catch (Exception e) {
    						e.printStackTrace();
    						//取好友或者群聊失败，不让进入主页面
    						runOnUiThread(new Runnable() {
                                public void run() {
                                    ErHuoApplication.getInstance().logout(null);
                                    Toast.makeText(getApplicationContext(), R.string.login_failure_failed, 1).show();
                                }
                            });
    						return;
    					}
    					// 进入主页面
    					//frameWaiting.hideFrame();//这里直接可以不退出显示，登录耗时很少，速度很快，使用framewaiting效果不好
    					runOnUiThread(new Runnable() {
                            public void run() {
                            	ToastUtil.showShortToast(ActivityLogin.this, "登录成功");//为了区别注册成功和登陆成功，消息提示放在这里
                            }
                        });
    		            setResult(RESULT_OK);//返回的界面会有信息提示 -> 改了
    		            finish();
    				}

    				@Override
    				public void onProgress(int progress, String status) {
    				}

    				@Override
    				public void onError(final int code, final String message) {
    					runOnUiThread(new Runnable() {
    						public void run() {
    							Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
    						}
    					});
    				}
    			});

            }

        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("cellphone/password_wrong")) {
                ToastUtil.showShortToast(ActivityLogin.this, "手机号或密码错误，请重试");
            } else {
                ToastUtil.showShortToast(ActivityLogin.this, "登录失败，请重试");
            }
            Log.e(TAG, err);
        }
    }

    // 验证输入的内容
    private boolean validateInput() {
        phone = textPhone.getText().toString();
        if (null == phone || "".equalsIgnoreCase(phone)) {
            ToastUtil.showShortToast(this, "请输入用户名");
            return false;
        }
        password = textPassword.getText().toString();
        if (null == password || "".equalsIgnoreCase(password)) {
            ToastUtil.showShortToast(this, "请输入密码");
            return false;
        }
        return true;
    }

    // 处理该界面的返回键或者其他键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                keyBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);//
    }

    public void keyBack() {//按下返回键
        setResult(RESULT_CANCELED);
        finish();
    }

    private void processContactsAndGroups() throws EaseMobException {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        List<String> usernames = EMContactManager.getInstance().getContactUserNames();
        System.out.println("----------------"+usernames.toString());
        EMLog.d("roster", "contacts size: " + usernames.size());
        Map<String, User> userlist = new HashMap<String, User>();
        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            setUserHearder(username, user);
            userlist.put(username, user);
        }
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(R.string.Application_and_notify);
        newFriends.setNick(strChat);
        
        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        ErHuoApplication.getInstance().setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(ActivityLogin.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
        
        //获取黑名单列表
        List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
        //保存黑名单
        EMContactManager.getInstance().saveBlackList(blackList);

        // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
        EMGroupManager.getInstance().getGroupsFromServer();
    }
    
    /**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
