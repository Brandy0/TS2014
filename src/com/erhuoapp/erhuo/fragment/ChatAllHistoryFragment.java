package com.erhuoapp.erhuo.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.erhuoapp.erhuo.ErHuoApplication;
import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.activity.ChatActivity;
import com.erhuoapp.erhuo.adapter.ChatAllHistoryAdapter;
import com.erhuoapp.erhuo.im.db.InviteMessgeDao;
import com.umeng.analytics.MobclickAgent;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 * 
 */
public class ChatAllHistoryFragment extends Fragment implements IFragment{

	private InputMethodManager inputMethodManager;
	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_conversation_history, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		
		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) getView().findViewById(R.id.list);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);
				
		
		final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(ErHuoApplication.getInstance().getUserName()))
					Toast.makeText(getActivity(), st2, 0).show();
				else {
				    // 进入聊天页面
				    Intent intent = new Intent(getActivity(), ChatActivity.class);
				    if(conversation.isGroup()){
				        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
				    }else{
				        // it is single chat
                        intent.putExtra("userId", username);
				    }
				    startActivity(intent);
				}
			}
		});
		// 注册上下文菜单
		registerForContextMenu(listView);

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}

		});

	}

	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			adapter.remove(tobeDeleteCons);
			adapter.notifyDataSetChanged();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if(adapter != null)
		    adapter.notifyDataSetChanged();
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0)
				list.add(conversation);
		}
		// 排序
		sortConversationByLastChatTime(list);
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1, final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); //统计页面
		
		if (!hidden ) {
			refresh();
		}
	}
	
	@Override
	public void onPause() {
        super.onPause();
        //Log.e(TAG, "onPause");
        MobclickAgent.onPageEnd("MainScreen"); 
    }

	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
    }

	@Override
	public void fragmentRefresh() {
		// TODO Auto-generated method stub
		refresh();
	}

}
