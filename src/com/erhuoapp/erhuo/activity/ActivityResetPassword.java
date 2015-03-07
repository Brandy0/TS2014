package com.erhuoapp.erhuo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.fragment.FragmentPhone;
import com.erhuoapp.erhuo.fragment.FragmentResetPassword;
import com.erhuoapp.erhuo.util.IConstants;

/**
 * 重置密码界面
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class ActivityResetPassword extends FragmentActivity implements FragmentPhone.FragmentPhoneListener, FragmentResetPassword.FragmentResetPasswordListener {

    private static final String TAG = "ActivityResetPassword";

    private FragmentPhone fragmentPhone;
    private FragmentResetPassword fragmentResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        fragmentPhone = FragmentPhone.newInstance(IConstants.PHONE_PASSWORD_RESET);// 第一步验证手机号界面
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_reset, fragmentPhone);
        transaction.commit();
    }

    ///// FragmentPhone验证手机号的回调
    @Override
    public void phoneCancel() {//
        finish();//取消重置密码
    }

    @Override
    public void phoneOk(String phone, String token) {
        fragmentResetPassword = FragmentResetPassword.newInstance(phone, token);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_reset, fragmentResetPassword);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void phoneToCard() {
        //重置密码的时候不会发生这种情况，所以忽略
        Log.e(TAG, "phoneToCard");
    }

    ///// FragmentResetPassword 回调
    @Override
    public void passwordCancel() {
        finish();//取消重置密码
    }

    @Override
    public void passwordOk() {
        finish();
    }
}
