package com.erhuoapp.erhuo.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.erhuoapp.erhuo.activity.ActivityLogin;
import com.erhuoapp.erhuo.im.db.UserDao;
import com.erhuoapp.erhuo.im.domain.User;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;

/**
 * 注册界面的第二步
 * <p/>
 * 输入昵称和密码
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class FragmentRegister extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentRegister";

    private Context context;
    private FrameWaiting frameWaiting;

    private EditText textName;
    private EditText textPassword1;
    private EditText textPassword2;
    private CheckBox checkBoxRule;
    private Button buttonNext;

    private String name, password1, password2;
    private String phone, token;
    private HashMap<String, String> params;

    public static FragmentRegister newInstance(String phone, String token) {
        Log.e(TAG, "new Instance");
        FragmentRegister fragmentRegister = new FragmentRegister();
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putString("token", token);
        fragmentRegister.setArguments(bundle);
        return fragmentRegister;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        phone = getArguments().getString("phone");
        token = getArguments().getString("token");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_register, container, false);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        textName = (EditText) contentView.findViewById(R.id.et_register_name);
        textPassword1 = (EditText) contentView.findViewById(R.id.et_register_password1);
        textPassword2 = (EditText) contentView.findViewById(R.id.et_register_password2);
        checkBoxRule = (CheckBox) contentView.findViewById(R.id.cb_register_rule);
        buttonNext = (Button) contentView.findViewById(R.id.btn_register_next);

        // 事件监听
        buttonNext.setOnClickListener(this);
        contentView.findViewById(R.id.ib_register_return).setOnClickListener(this);

        // 进入时关闭它
        frameWaiting.hideFrame();

        // 初始化数据
        params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("token", token);

        return contentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        listener = (FragmentRegisterListener) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_register_return:// 返回，表示用户退出注册
                if (null != listener) {
                    listener.registerCancel();
                }
                break;
            case R.id.btn_register_next://点击下一步
                doRegister();
                break;
        }
    }

    private void doRegister() {
        if (validateInput()) {// 输入验证，通过之后进入下一步
            params.put("userName", name);
            params.put("userPassword", AppUtil.encodeWithMD5(password1));//发送加密的密码
            new CloudUtil().Register(params, new RegisterCallback());
        }
    }

    // 注册事件的回调
    class RegisterCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {

        @Override
        public void onPost() {
            frameWaiting.showMessage("注册中，请等待...");
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
    						getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    ErHuoApplication.getInstance().logout(null);
//                                    Toast.makeText(getActivity(), R.string.login_failure_failed, 1).show();
                                }
                            });
    						return;
    					}
    					
    					
    		           
    		            getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                            	 frameWaiting.hideFrame();
                            	 ToastUtil.showShortToast(context, "恭喜你，注册成功");
                            }
                        });
    		           
    		            if (null != listener) {
    		                listener.registerOk();
    		            }
    				}

    				@Override
    				public void onProgress(int progress, String status) {
    				}

    				@Override
    				public void onError(final int code, final String message) {
    					getActivity().runOnUiThread(new Runnable() {
    						public void run() {
//    							Toast.makeText(getActivity(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
    						}
    					});
    				}
    			});

            }

        
        	
        	
        	
        	
        	
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("nickname_exist")) {
                ToastUtil.showShortToast(context, "用户名已经存在了");
            } else if (err.equalsIgnoreCase("wrong_token")) {
                ToastUtil.showShortToast(context, "请重新验证手机号");
            } else {
                ToastUtil.showShortToast(context, "注册失败，请重试");
            }
            Log.e(TAG, err);
        }
    }

    // 验证输入的内容
    private boolean validateInput() {
        name = textName.getText().toString();
        if (null == name || "".equalsIgnoreCase(name)) {
            ToastUtil.showShortToast(context, "请输入用户名");
            return false;
        }
        //if (!name.matches("[a-zA-Z0-9]+") || name.length() < 4 || name.length() > 15) {
        if ( name.length() < 4 || name.length() > 15) {
            ToastUtil.showShortToast(context, "用户名必须为4-15位字符");
            return false;
        }
        password1 = textPassword1.getText().toString();
        if (null == password1 || "".equalsIgnoreCase(password1)) {
            ToastUtil.showShortToast(context, "请输入密码");
            return false;
        }
        if (!password1.matches("[a-zA-Z0-9]+") || password1.length() < 6 || password1.length() > 12) {
            ToastUtil.showShortToast(context, "密码必须为6-12位字母/数字");
            return false;
        }
        password2 = textPassword2.getText().toString();
        if (null == password2 || "".equalsIgnoreCase(password2)) {
            ToastUtil.showShortToast(context, "请再次输入密码");
            return false;
        }
        if (!password1.equalsIgnoreCase(password2)) {
            ToastUtil.showShortToast(context, "两次输入的密码不相同");
            return false;
        }
        if (!checkBoxRule.isChecked()) {
            ToastUtil.showShortToast(context, "您需要同意贰货APP用户注册和使用协议");
            return false;
        }
        return true;
    }

    // 注册事件的监听器
    public interface FragmentRegisterListener {
        public void registerCancel();

        public void registerOk();
    }

    private FragmentRegisterListener listener;
    
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
        UserDao dao = new UserDao(getActivity());
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
