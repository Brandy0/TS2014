package com.erhuoapp.erhuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.fragment.FragmentPhone;

/**
 * 绑定手机号码界面
 *
 * @author hujiawei
 * @datetime 2015/1/26
 */
public class ActivityPhone extends FragmentActivity implements FragmentPhone.FragmentPhoneListener {

    //操作的类型，有四种：绑定手机号和更改手机号在账号管理界面，注册时有，重置密码时有
    private String type;
    private FragmentPhone fragmentPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        type = getIntent().getStringExtra("type");

        fragmentPhone = FragmentPhone.newInstance(type);//用户注册
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_phone, fragmentPhone);
        transaction.commit();
    }

    ///// FragmentPhone的三个回调方法

    @Override
    public void phoneOk(String phone, String token) {//手机号验证ok
        Intent intent = new Intent();
        intent.putExtra("phone", phone);
        intent.putExtra("token", token);
        setResult(RESULT_OK, intent);//把验证成功的手机号返回
        finish();
    }

    @Override
    public void phoneToCard() {//验证ok，但是需要进入扫描学生证进一步验证身份
        Intent intent = new Intent(this, ActivityAuth.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void phoneCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
