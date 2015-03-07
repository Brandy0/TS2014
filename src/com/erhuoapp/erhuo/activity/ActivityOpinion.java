package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ToastUtil;

import java.util.HashMap;

/**
 * 意见反馈界面
 *
 * @author hujiawei
 * @datetime 2015/1/20
 */
public class ActivityOpinion extends FragmentActivity {

    private final String TAG = "ActivityOpinion";

    private EditText textContent;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        // 界面组件
        textContent = (EditText) findViewById(R.id.et_opinion_content);

        // 事件监听
        findViewById(R.id.ib_opinion_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tv_opinion_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = textContent.getText().toString();
                if (null == content || "".equalsIgnoreCase(content)) {
                    ToastUtil.showShortToast(ActivityOpinion.this, "请输入意见内容");
                    return;
                }
                //closeInputMethod();//
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content", content);
                new CloudUtil().PostAdvice(params, null);
                ToastUtil.showShortToast(getApplicationContext(), "感谢您提的意见");
                finish();
            }
        });
    }

    // 关闭输入法 [没有问题]
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//显示了就关闭
        }
        //imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
