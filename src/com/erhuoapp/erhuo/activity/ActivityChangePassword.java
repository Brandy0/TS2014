package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;

import java.util.HashMap;

/**
 *
 * 更改密码界面
 *
 * @author hujiawei
 * @datetime 2015/1/27
 */
public class ActivityChangePassword extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "ActivityChangePassword";

    private Context context;
    private EditText textPassword1;
    private EditText textPassword2;
    private EditText textPassword3;
    private FrameWaiting frameWaiting;

    private String password1, password2, password3;
    private HashMap<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        View contentView = getLayoutInflater().inflate(R.layout.activity_change_password, null);
        setContentView(contentView);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        textPassword1 = (EditText) contentView.findViewById(R.id.et_change_old);
        textPassword2 = (EditText) contentView.findViewById(R.id.et_change_new);
        textPassword3 = (EditText) contentView.findViewById(R.id.et_change_new2);

        // 事件监听
        contentView.findViewById(R.id.btn_change_ok).setOnClickListener(this);
        contentView.findViewById(R.id.ib_return).setOnClickListener(this);

        // 初始化数据
        params = new HashMap<String, String>();
        frameWaiting.hideFrame();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_return://返回
                finish();
                break;
            case R.id.btn_change_ok://确定修改
                doChangePassword();
                break;
        }
    }

    // 更改密码
    private void doChangePassword() {
        if (checkUserInput()) {
            params.put("old_pwd", AppUtil.encodeWithMD5(password1));
            params.put("new_pwd", AppUtil.encodeWithMD5(password2));
            //Log.e(TAG, "pass: "+AppUtil.encodeWithMD5(password1) + " " + AppUtil.encodeWithMD5(password2));
            new CloudUtil().ChangePassword(params, new ChangePasswordCallback());
        }
    }

    // 检查用户的输入
    private boolean checkUserInput() {
        password1 = textPassword1.getText().toString();
        if (null == password1 || "".equalsIgnoreCase(password1)) {
            ToastUtil.showShortToast(context, "请输入旧密码");
            return false;
        }
        if (!password1.matches("[a-zA-Z0-9]+") || password1.length() < 6 || password1.length() > 12) {
            ToastUtil.showShortToast(context, "旧密码一定是6-12位字母/数字");
            return false;
        }
        password2 = textPassword2.getText().toString();
        if (null == password2 || "".equalsIgnoreCase(password2)) {
            ToastUtil.showShortToast(context, "请输入新密码");
            return false;
        }
        if (!password2.matches("[a-zA-Z0-9]+") || password2.length() < 6 || password2.length() > 12) {
            ToastUtil.showShortToast(context, "新密码必须是6-12位字母/数字");
            return false;
        }
        password3 = textPassword3.getText().toString();
        if (null == password3 || "".equalsIgnoreCase(password3)) {
            ToastUtil.showShortToast(context, "请再次输入新密码");
            return false;
        }
        if (!password3.equalsIgnoreCase(password2)) {
            ToastUtil.showShortToast(context, "两次输入的密码不相同");
            return false;
        }
        return true;
    }

    // 更改密码的回调
    class ChangePasswordCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("更改中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "密码更改成功");
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("wrong_password")) {
                ToastUtil.showShortToast(context, "密码错误，请重试");
            } else {
                ToastUtil.showShortToast(context, "更改失败，请重试");
            }
            Log.e(TAG, err);
        }
    }
}
