package com.erhuoapp.erhuo.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;

/**
 * 注册界面中的注册成功界面
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class FragmentRegisterOK extends Fragment implements View.OnClickListener{

    private static final String TAG = "FragmentRegisterOK";

    public static FragmentRegisterOK newInstance(){
        Log.e(TAG, "new instance");
        FragmentRegisterOK fragmentRegisterOK = new FragmentRegisterOK();
        return fragmentRegisterOK;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_register_ok, container, false);

        // 事件监听
        contentView.findViewById(R.id.btn_registerok_ok).setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach");
        listener = (FragmentRegisterOkListener) activity;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_registerok_ok) {//返回（应该会回到登录界面）
        	GetUserInfo();
            if (null!=listener){
                listener.finishRegister();
            }
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

    // 注册成功的监听器
    public interface FragmentRegisterOkListener {
        public void finishRegister();
    }

    private FragmentRegisterOkListener listener;
}
