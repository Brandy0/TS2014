package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 发布/编辑求购界面
 *
 * @author hujiawei
 * @datetime 2015/1/17
 */
public class ActivityBuying extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "ActivityBuying";

    private Context context;
    private EntityGoodsBuying goods;

    private FrameWaiting frameWaiting;
    private EditText editTextTitle;
    private EditText editTextPrice;
    private EditText editTextContent;
    private TextView textViewPublish;
    private TextView textViewTitle;//标题部分
    private CheckBox checkBoxLocation;
    private LinearLayout linearLayoutLocation;

    private String title;
    private String price;
    private String content;
    private HashMap<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        View contentView = getLayoutInflater().inflate(R.layout.activity_buying, null);
        setContentView(contentView);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        editTextTitle = (EditText) findViewById(R.id.et_buying_title);
        editTextPrice = (EditText) findViewById(R.id.et_buying_price);
        editTextContent = (EditText) findViewById(R.id.et_buying_content);
        textViewPublish = (TextView) findViewById(R.id.tv_buying_publish);
        textViewTitle = (TextView) findViewById(R.id.tv_buying_title);
        checkBoxLocation = (CheckBox) findViewById(R.id.cb_buying_location);
        linearLayoutLocation = (LinearLayout) findViewById(R.id.ll_buying_location);

        // 事件监听
        findViewById(R.id.ib_buying_return).setOnClickListener(this);
        textViewPublish.setOnClickListener(this);

        // 如果有数据的话表示是编辑模式
        goods = (EntityGoodsBuying) getIntent().getSerializableExtra("goods");
        params = new HashMap<String, String>();

        // 进入时关闭它
        frameWaiting.hideFrame();
        linearLayoutLocation.setVisibility(View.GONE);//不要占位置

        initData();
    }

    // 初始化数据，还是不要放在onResume中，不然在编辑模式下容易出现修改被重置
    private void initData() {
        if (null != goods) {//编辑模式
            editTextTitle.setText(goods.getTitle());
            editTextContent.setText(goods.getContent());
            editTextPrice.setText(goods.getPrice());

            textViewTitle.setText("编辑求购");
            textViewPublish.setText("完成");
            linearLayoutLocation.setVisibility(View.VISIBLE);//编辑模式下才显示
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_buying_return:
                finish();
                break;
            case R.id.tv_buying_publish:
                doPublish();
                break;
        }
    }

    // 发布求购
    private void doPublish() {
        if (checkUserInput()) {//验证通过
            params.put("title", title);
            params.put("price", price);
            params.put("content", content);
            params.put("lati", "0");//TODO：这里需要获取用户的地理位置
            params.put("longi", "0");
            if (null!=goods){//编辑模式
                params.put("id", goods.getId());
                if (checkBoxLocation.isChecked()){
                    params.put("location", "1");//1表示更新地理位置信息
                }else{
                    params.put("location", "0");
                }
                new CloudUtil().UpdateBuying(params, new UpdateBuyingCallback());
            }else{//添加模式
                new CloudUtil().AddBuying(params, new AddBuyingCallback());
            }
        }
    }

    // 发布事件的回调
    class AddBuyingCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {
            //closeInputMethod();//先要关闭输入键盘
            frameWaiting.showMessage("发布中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            //frameWaiting.hideFrame();//用不着关闭
            ToastUtil.showShortToast(context, "发布成功");
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "发布失败，请重试");
            Log.e(TAG, err);
        }
    }

    // 更新事件的回调
    class UpdateBuyingCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {
            //closeInputMethod();//先要关闭输入键盘
            frameWaiting.showMessage("更新中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            //frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "更新成功");
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "更新失败，请重试");
            Log.e(TAG, err);
        }
    }

    // 验证输入的内容
    private boolean checkUserInput() {
        title = editTextTitle.getText().toString();
        if (null == title || "".equalsIgnoreCase(title)) {
            ToastUtil.showShortToast(this, "请输入物品标题");
            return false;
        }
        price = editTextPrice.getText().toString();
        if (null == price || "".equalsIgnoreCase(price)) {
            ToastUtil.showShortToast(this, "请输入物品价位");
            return false;
        }
        if (!price.matches("[0-9]+")) {
            ToastUtil.showShortToast(this, "物品价位必须是数字");
            return false;
        }
        content = editTextContent.getText().toString();
        if (null == content || "".equalsIgnoreCase(content)) {
            ToastUtil.showShortToast(this, "请描述下求购物品吧");
            return false;
        }
        return true;
    }

    // 关闭输入法 [有问题]
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//显示了就关闭
        }
        //imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
