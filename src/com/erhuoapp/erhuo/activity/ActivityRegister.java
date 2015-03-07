package com.erhuoapp.erhuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.fragment.FragmentPhone;
import com.erhuoapp.erhuo.fragment.FragmentRegister;
import com.erhuoapp.erhuo.fragment.FragmentRegisterOK;
import com.erhuoapp.erhuo.util.IConstants;

/**
 * 注册界面
 *
 * @author hujiawei
 * @datetime 2015/1/3
 */
public class ActivityRegister extends FragmentActivity implements FragmentPhone.FragmentPhoneListener, FragmentRegister.FragmentRegisterListener, FragmentRegisterOK.FragmentRegisterOkListener {

    private static final String TAG = "ActivityRegister";

    private FragmentPhone fragmentPhone;
    private FragmentRegister fragmentRegister;
    private FragmentRegisterOK fragmentRegisterOK;
    //private FragmentCard fragmentCard;
    //NOTE: 2015/1/29 之前设计上传学生证是作为一个Fragment以便重用，但是现在将整个上传学生证作为了ActivityAuth，所以改为Activity跳转，而不是Fragment替换

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 进入第一个Fragment
        fragmentPhone = FragmentPhone.newInstance(IConstants.PHONE_USER_REGISTER);//用户注册
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_register, fragmentPhone);
        transaction.commit();
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

    public void keyBack() {//按下返回键 --> 没有result需要设置！
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IConstants.REQUEST_USER_AUTH) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {//不论是成功上传还是跳过都是跳到注册成功界面
                fragmentRegisterOK = new FragmentRegisterOK();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_register, fragmentRegisterOK);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    /**
     * FragmentPhone
     */
    @Override
    public void phoneCancel() {
        setResult(RESULT_CANCELED);//注册取消了
        finish();
    } //取消手机验证

    @Override
    public void phoneOk(String phone, String token) {//手机验证通过
        fragmentRegister = FragmentRegister.newInstance(phone, token);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_register, fragmentRegister);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void phoneToCard() {
        //目前是在注册阶段
        //有些时候手机验证阶段会返回id_card，这个时候要跳转到上传学生证的界面以便身份认证
    }

    /**
     * FragmentRegisterOK
     */
    @Override
    public void finishRegister() {//注册界面4 完成注册操作
        setResult(RESULT_OK);//注册成功
        finish();
    }

    /**
     * FragmentRegister
     */
    @Override
    public void registerCancel() {//注册界面2 返回，进入手机号验证
        fragmentPhone = FragmentPhone.newInstance(IConstants.PHONE_USER_REGISTER);//用户注册
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_register, fragmentPhone);
        transaction.commit();
    }

    @Override
    public void registerOk() {//注册界面2 成功注册，进入扫描学生证
        //new codes 2015/1/29 这里不再是切换到FragmentCard，而是跳转到ActivityAuth进行 身份认证
        Intent intent = new Intent(this, ActivityAuth.class);
        startActivityForResult(intent, IConstants.REQUEST_USER_AUTH);

        //old codes
//        fragmentCard = FragmentCard.newInstance();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container_register, fragmentCard);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    /**
     * FragmentCard  ---> 弃用了
     */
//    @Override
//    public void cardSkip() {//扫描学生证OK -> 其实是跳过扫描
//        fragmentRegisterOK = new FragmentRegisterOK();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container_register, fragmentRegisterOK);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }


}
