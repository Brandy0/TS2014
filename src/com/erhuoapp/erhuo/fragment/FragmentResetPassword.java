package com.erhuoapp.erhuo.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;

import java.util.HashMap;

/**
 * 重置密码中的添加新密码界面
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class FragmentResetPassword extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentResetPassword";

    private Context context;
    private EditText textPassword1;
    private EditText textPassword2;
    private FrameWaiting frameWaiting;

    private String password1, password2;
    private String phone, token;
    private HashMap<String, String> params;

    public static FragmentResetPassword newInstance(String phone, String token) {
        Log.e(TAG, "new Instance");
        FragmentResetPassword fragmentResetPassword = new FragmentResetPassword();
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putString("token", token);
        fragmentResetPassword.setArguments(bundle);
        return fragmentResetPassword;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        phone = getArguments() != null ? getArguments().getString("phone") : "";
        token = getArguments() != null ? getArguments().getString("token") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        context = getActivity();
        View contentView = inflater.inflate(R.layout.fragment_reset_password, null);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        textPassword1 = (EditText) contentView.findViewById(R.id.et_reset_pwd);
        textPassword2 = (EditText) contentView.findViewById(R.id.et_reset_pwd2);

        // 事件监听
        contentView.findViewById(R.id.btn_reset_ok).setOnClickListener(this);
        contentView.findViewById(R.id.ib_return).setOnClickListener(this);

        // 初始化数据
        params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("token", token);

        frameWaiting.hideFrame();

        return contentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_return://返回
                if (listener != null) {
                    listener.passwordCancel();
                }
                break;
            case R.id.btn_reset_ok://确定修改
                doResetPassword();
                break;
        }
    }

    // 修改密码
    private void doResetPassword() {
        if (checkUserInput()) {
            params.put("pwd", AppUtil.encodeWithMD5(password1));
            new CloudUtil().ResetPassword(params, new ResetPasswordCallback());
        }
    }

    // 检查用户的输入
    private boolean checkUserInput() {
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
        return true;
    }

    // 重置密码的回调
    class ResetPasswordCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("验证中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "密码重置成功");
            if (listener!=null){
                listener.passwordOk();
            }
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "重置失败，请重试");
            Log.e(TAG, err);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (FragmentResetPasswordListener) activity;
    }

    // 监听器，确定修改
    public interface FragmentResetPasswordListener {
        public void passwordCancel();

        public void passwordOk();
    }

    private FragmentResetPasswordListener listener;

    public FragmentResetPasswordListener getListener() {
        return listener;
    }

    public void setListener(FragmentResetPasswordListener listener) {
        this.listener = listener;
    }

}
