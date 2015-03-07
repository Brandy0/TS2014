package com.erhuoapp.erhuo.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送手机验证码界面
 * <p/>
 * 注册的第一个界面 / 重置密码的第一个界面
 *
 * @author hujiawei
 * @datetime 2015/1/14
 */
public class FragmentPhone extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentPhone";

    private Button buttonSendCode;
    private Button buttonValidateCode;
    private TextView textViewError;
    private EditText editTextPhone;
    private EditText editTextCode;
    private TextView textViewTitle;
    private FrameWaiting frameWaiting;
    private RelativeLayout layoutPwdForm;
    private TextView textViewAccount;
    private EditText editTextPwd;
    private Button buttonPwd;

    private Activity context;
    private String oldPhone = "";
    private String pwd;//密码
    private String token = "";//更改手机号验证密码之后得到的token
    private String type;//验证手机号码的作用类型
    private String phone;//手机号码
    private String code;//验证码
    private HashMap<String, String> params;
    private HashMap<String, String> codeParams;
    private HashMap<String, String> pwdParams;
    private final int TIMER_COUNT = 60;

    private Timer timer;//倒计时需要的计时器
    private int count = TIMER_COUNT;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            context.runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    count--;
                    buttonSendCode.setText("已发送(" + count + "s)");
                    if (count < 0) {
                        timer.cancel();
                        buttonSendCode.setText("重新获取");
                        buttonSendCode.setEnabled(true);
                    }
                }
            });
        }
    };

    // 执行顺序是 newInstance -> onCreate -> onCreateView

    public static FragmentPhone newInstance(String type) {
        Log.e(TAG, "new Instance");
        FragmentPhone fragmentPhone = new FragmentPhone();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragmentPhone.setArguments(bundle);
        return fragmentPhone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        type = getArguments() != null ? getArguments().getString("type") : IConstants.PHONE_ADD;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_phone, null);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);//
        buttonSendCode = (Button) contentView.findViewById(R.id.btn_phone_sendcode);
        buttonValidateCode = (Button) contentView.findViewById(R.id.btn_phone_validate);
        textViewError = (TextView) contentView.findViewById(R.id.tv_phone_error);
        editTextPhone = (EditText) contentView.findViewById(R.id.et_phone);
        editTextCode = (EditText) contentView.findViewById(R.id.et_code);
        textViewTitle = (TextView) contentView.findViewById(R.id.tv_title);
        layoutPwdForm = (RelativeLayout) contentView.findViewById(R.id.rl_pwd_form);
        textViewAccount = (TextView) contentView.findViewById(R.id.tv_pwd_info);
        editTextPwd = (EditText) contentView.findViewById(R.id.et_pwd_pwd);
        buttonPwd = (Button) contentView.findViewById(R.id.btn_pwd_ok);

        // 事件监听
        buttonPwd.setOnClickListener(this);
        buttonSendCode.setOnClickListener(this);
        buttonValidateCode.setOnClickListener(this);
        contentView.findViewById(R.id.ib_return).setOnClickListener(this);
        contentView.findViewById(R.id.ib_pwd_return).setOnClickListener(this);

        // init ui and data
        init();
        return contentView;
    }

    // 初始化界面
    private void init() {
        // data
        oldPhone = AppUtil.getInstance().getUserPhone();
        params = new HashMap<String, String>();
        params.put("type", type);
        codeParams = new HashMap<String, String>();
        codeParams.put("type", type);
        pwdParams = new HashMap<String, String>();

        // 标题
        if (type.equalsIgnoreCase(IConstants.PHONE_USER_REGISTER)) {
            textViewTitle.setText("注 册");
        } else if (type.equalsIgnoreCase(IConstants.PHONE_ADD)) {
            textViewTitle.setText("绑定手机号");
        } else if (type.equalsIgnoreCase(IConstants.PHONE_CHANGE)) {
            textViewTitle.setText("更改手机号");
        } else if (type.equalsIgnoreCase(IConstants.PHONE_PASSWORD_RESET)) {
            textViewTitle.setText("重置密码");
        }

        textViewError.setVisibility(View.GONE);//暂时不可见
        buttonValidateCode.setEnabled(false);//不能验证
        frameWaiting.hideFrame();
        layoutPwdForm.setVisibility(View.GONE);//一般都是隐藏着

        // 更改手机号的话还需要先输入密码
        if (type.equalsIgnoreCase(IConstants.PHONE_CHANGE)) {
            textViewAccount.setText("请输入账号\"" + AppUtil.getInstance().getUserName() + "\"的密码");
            layoutPwdForm.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        listener = (FragmentPhoneListener) activity;//对应的activity就是监听者
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_pwd_return://更改手机号输入密码左上角返回
                if (listener != null) {
                    listener.phoneCancel();
                }
                break;
            case R.id.ib_return://返回
                if (listener != null) {
                    listener.phoneCancel();
                }
                break;
            case R.id.btn_pwd_ok://密码确定
                confirmPwd();
                break;
            case R.id.btn_phone_validate://验证
                validateCode();
                break;
            case R.id.btn_phone_sendcode://发送验证码
                requestCode();
                break;
        }
    }

    // 确定了密码
    private void confirmPwd() {
        if (checkPassword()) {
            pwdParams.put("phone", oldPhone);//得到用户原来的手机号，肯定能返回值
            pwdParams.put("pwd", AppUtil.encodeWithMD5(pwd));
            new CloudUtil().ChangePhone(pwdParams, new ChangePhoneCallback());
        }
    }

    // 检查密码是否输入了
    private boolean checkPassword() {
        pwd = editTextPwd.getText().toString();
        if (pwd == null || "".equalsIgnoreCase(pwd)) {
            ToastUtil.showShortToast(getActivity(), "请输入密码");
            return false;
        }
        return true;
    }

    // 请求验证码
    private void requestCode() {
        if (checkPhoneNumber()) {
            //Log.e(TAG, "change phone token = " + token);//token不一定有，可能为空
            params.put("old_phone", oldPhone);
            params.put("token", token);
            params.put("phone", phone);
            new CloudUtil().UserRequestCode(params, new UserRequestCodeCallback());
        }
    }

    // 验证手机号码
    private boolean checkPhoneNumber() {
        phone = editTextPhone.getText().toString();
        if (phone == null || "".equalsIgnoreCase(phone)) {
            ToastUtil.showShortToast(getActivity(), "请输入手机号码");
            return false;
        }
        Pattern pattern = Pattern.compile("^(13|14|15|17|18)\\d{9}$");
        Matcher matcher = pattern.matcher(phone);//验证是否是合法的手机号码
        if (!matcher.matches()) {
            ToastUtil.showShortToast(getActivity(), "请输入正确的手机号码");
            return false;
        }
        return true;
    }


    // 请求验证验证码
    private void validateCode() {
        if (checkCodeNumer()) {
            codeParams.put("phone", phone);
            codeParams.put("code", code);
            new CloudUtil().UserValidateCode(codeParams, new UserValidateCodeCallback());
        }
    }

    // 检查验证码的输入
    private boolean checkCodeNumer() {
        code = editTextCode.getText().toString();
        if (code == null || "".equalsIgnoreCase(code)) {
            ToastUtil.showShortToast(getActivity(), "请输入验证码");
            return false;
        }
        return true;
    }

    // 用户请求发送验证码的回调
    class UserRequestCodeCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            buttonSendCode.setEnabled(false);
            buttonSendCode.setText("发送中...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            // 发送验证码成功，但是还没有验证
            buttonValidateCode.setEnabled(true);//可以进行验证了
            buttonSendCode.setEnabled(false);//此时依然不可以点击
            timer = new Timer();//每次都需要重新new一个计时器
            timer.schedule(task, 1000, 1000);//开始倒计时，每隔1s中执行一次
        }

        @Override
        public void onPostFailure(String err) {
            if (err.equalsIgnoreCase("cellphone_already_recorded")) {
                ToastUtil.showShortToast(context, "该手机号已经被绑定了");
            } else if (err.equalsIgnoreCase("cellphone_not_recorded")) {
                ToastUtil.showShortToast(context, "该手机号没有被绑定过");
            } else if (err.equalsIgnoreCase("wrong_token")) {
                ToastUtil.showShortToast(context, "发送失败");
            } else {//未知的其他错误
                ToastUtil.showShortToast(context, "发送失败，请重试");
            }
            buttonSendCode.setEnabled(true);
            buttonSendCode.setText("重新获取");
            buttonValidateCode.setEnabled(false);
            Log.e(TAG, err);
        }
    }

    // 用户请求验证验证码的回调
    class UserValidateCodeCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("验证中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {// {"error":"0","token":"JJIP4AK2THXZ7HP"}
            frameWaiting.hideFrame();
            timer.cancel();
            if (temp.getDescription().equalsIgnoreCase("idcard_auth")) {
                //ToastUtil.showShortToast(context, "进入扫描学生证");
                if (listener != null) {
                    listener.phoneToCard();
                }
            } else {
                if (listener != null) {
                    listener.phoneOk(phone, temp.getToken());//当前成功验证的手机号码和它对应的token
                }
            }
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("wrong_code")) {
                ToastUtil.showShortToast(context, "验证码错误");
            } else if (err.equalsIgnoreCase("cellphone_not_recorded")) {
                ToastUtil.showShortToast(context, "该手机号没有被绑定过");
            } else {//未知的其他错误
                ToastUtil.showShortToast(context, "验证失败，请重试");
            }
            buttonSendCode.setEnabled(true);
            buttonValidateCode.setEnabled(true);
            Log.e(TAG, err);
        }
    }

    // 更改手机号的回调
    class ChangePhoneCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("验证中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {// {"error":"0","token":"JJIP4AK2THXZ7HP"}
            frameWaiting.hideFrame();
            layoutPwdForm.setVisibility(View.GONE);
            token = temp.getToken();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("user_not_match")) {
                ToastUtil.showShortToast(context, "用户不匹配");
            } else if (err.equalsIgnoreCase("cellphone/password_wrong")) {
                ToastUtil.showShortToast(context, "密码错误");
            } else {//未知的其他错误
                ToastUtil.showShortToast(context, "验证失败，请重试");
            }
            Log.e(TAG, err);
        }
    }

    // 监听器
    public interface FragmentPhoneListener {
        public void phoneCancel();

        public void phoneOk(String phone, String token);

        public void phoneToCard();
    }

    private FragmentPhoneListener listener;

    public FragmentPhoneListener getListener() {
        return listener;
    }

    public void setListener(FragmentPhoneListener listener) {
        this.listener = listener;
    }

}
