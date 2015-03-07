package com.erhuoapp.erhuo.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.fragment.FragmentEmpty;
import com.erhuoapp.erhuo.fragment.FragmentMain;
import com.erhuoapp.erhuo.fragment.FragmentMenu;
import com.erhuoapp.erhuo.im.db.HeadAndName;
import com.erhuoapp.erhuo.im.db.InviteMessgeDao;
import com.erhuoapp.erhuo.im.db.UserDao;
import com.erhuoapp.erhuo.im.domain.UserHeadAndName;
import com.erhuoapp.erhuo.im.utils.CommonUtils;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.SlidingMenu;
import com.luminous.pick.MainActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 主界面
 *
 * @author hujiawei
 * @datetime 15/1/1 23:10
 */
public class ActivityMain extends FragmentActivity implements IConstants {

    private static final String TAG = "ActivityMain";

    private boolean isExit = false;//当连续在2秒内点击了返回键则为true

    private SlidingMenu slidingMenu;
    private FragmentMenu fragmentMenu;
    private FragmentMain fragmentMain;
    private FragmentEmpty fragmentEmpty;
    private NewMessageBroadcastReceiver msgReceiver;
    private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            finish();
            startActivity(new Intent(this, ActivityMain.class));
            return;
        }
        // 判断是否需要进入引导页
        SharedPreferences sharedPreferences = AppUtil.getInstance().getSharedPreferences();
        if (sharedPreferences.getBoolean(AppUtil.PREF_KEY_FIRST, true)) {
            startActivityForResult(new Intent(ActivityMain.this, ActivityGuide.class), REQUEST_GUIDE);
        } else {
            //这个时候其实ActivityMain还没有显示出来，所以可以先跳到loading，1.5秒后loading销毁，显示主界面
            startActivity(new Intent(ActivityMain.this, ActivityLoading.class));
        }

        // 这次进入应用是否有用户的数据在
        Log.e(TAG, "onCreate user login " + AppUtil.getInstance().getUserId() + " " + AppUtil.getInstance().getUserName());

        // 滑动菜单
        setContentView(R.layout.activity_main_slidingmenu);
        slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
        slidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.frame_left, null));
        slidingMenu.setRightView(getLayoutInflater().inflate(R.layout.frame_right, null));
        slidingMenu.setCenterView(getLayoutInflater().inflate(R.layout.frame_center, null));
        slidingMenu.setCanSliding(false, false);//进入的时候首先设置为左右都不能滑动，后面会设置左边能滑动
        //slidingMenu.setCanSlideRight(false);//永远不能进入右侧的滑动菜单

        // 左中右Fragments
        //Log.e(TAG, "getSupportFragmentManager() -> " + getSupportFragmentManager().hashCode());
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        fragmentMenu = new FragmentMenu();
        t.replace(R.id.left_frame, fragmentMenu);
        fragmentEmpty = new FragmentEmpty();
        t.replace(R.id.right_frame, fragmentEmpty);
        fragmentMain = new FragmentMain();
        t.replace(R.id.center_frame, fragmentMain);
        t.commit();
        
        
        inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
		
		//注册一个透传消息的BroadcastReceiver
		IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
		cmdMessageIntentFilter.setPriority(3);
		registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
		
		
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        MobclickAgent.onResume(this);//友盟统计onResume

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        MobclickAgent.onPause(this);//友盟统计onPause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {//request,result
        Log.e(TAG, "onActivityResult");//不论Fragment中是直接start还是getActivity然后start这里都会被调用
        super.onActivityResult(requestCode, resultCode, intent);//这里这句不能使得FragmentMain中的result方法被调用
        if (requestCode == REQUEST_GUIDE) {//在引导界面就取消返回了的话就退出
            if (resultCode == RESULT_CANCELED) {
                finish();
                System.exit(0);//应用退出
            }
        } else if (requestCode == REQUEST_LOGIN) {//处理登录
            if (resultCode == Activity.RESULT_OK) {//有可能是注册成功了
                // 登录成功之后就可以加载用户数据了，这样当用户切换到个人主页的时候就有数据了
                if (AppUtil.getInstance().checkUserLogin()) {//如果有用户信息存在那么就加载
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
            }
        } else if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == RESULT_OK) {
                ToastUtil.showShortToast(this, "当前账号已退出");
                fragmentMain.refreshAfterUserExit();
            }
        } else if (requestCode == REQUEST_BUYING_UPDATE) {//更新求购
            if (resultCode == RESULT_OK) {
                //ToastUtil.showShortToast(this, "更新成功");//
            }
        }

        //发布求购成功移到FragmentMain中了
    }

    // 处理该界面的返回键或者其他键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                keyBack();
                return true;
            case KeyEvent.KEYCODE_MENU:
                keyMenu();
                return true;
        }
        return super.onKeyDown(keyCode, event);//
    }

    // 按下返回键
    public void keyBack() {
        Timer timer = null;
        if (!isExit) {//点击两次返回退出程序
            isExit = true;
            ToastUtil.showShortToast(this, "再次点击退出程序");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    // 按下菜单键
    public void keyMenu() {
        if (slidingMenu.isCanSlideLeft()) {
            slidingMenu.showLeftView();//如果当前menu显示了的话，调用之后会隐藏menu
        }
    }

    public SlidingMenu getSlidingMenu() {
        return slidingMenu;
    }

    public FragmentMenu getFragmentMenu() {
        return fragmentMenu;
    }

    



	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			
			String getMessageHead = message.getStringAttribute("fromheader",null);
			String getMessageName = message.getStringAttribute("from",null);
			String getMessageId = message.getStringAttribute("fromid",null);
			
		    HeadAndName mHeadAndName = new HeadAndName(ActivityMain.this);
            UserHeadAndName user = new UserHeadAndName();
            user.setHead(getMessageHead);
            user.setNick(getMessageName);
            user.setUserId(getMessageId);
			mHeadAndName.saveContact(user );
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				}
			}
			
			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();
			notifyNewMessage(message);  

			// 刷新bottom bar
			FragmentMain.refresh();

		}
	}
	 protected NotificationManager notificationManager;
	 private static final int notifiId = 11;
	/**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(this)){
            return;
        }
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);
        
        String ticker = CommonUtils.getMessageDigest(message, this);
        String st = getResources().getString(R.string.expression);
        if(message.getType() == Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
        //设置状态栏提示
        mBuilder.setTicker(message.getFrom()+": " + ticker);
        
        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }


	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");

			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);

				if (msg != null) {

					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
					if (ChatActivity.activityInstance != null) {
						if (msg.getChatType() == ChatType.Chat) {
							if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
								return;
						}
					}

					msg.isAcked = true;
				}
			}
			
		}
	};
	
	
	
	/**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			EMLog.d(TAG, "收到透传消息");
			//获取cmd message对象
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			//获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String action = cmdMsgBody.action;//获取自定义action
			
			//获取扩展属性 此处省略
//			message.getStringAttribute("");
			EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
		}
	};






	
	
}
