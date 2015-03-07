package com.erhuoapp.erhuo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erhuoapp.erhuo.R;

/**
 *
 * 消息列表
 *
 * @author hujiawei
 * @datetime 15/1/2 21:11
 */
public class FragmentMessage extends Fragment implements View.OnClickListener, IFragment {

    private static final String TAG = "FragmentMessage";

    private View contentView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        contentView = inflater.inflate(R.layout.fragment_message, null);
        return contentView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestrory");
    }

    @Override
    public void refresh() {
        Log.e(TAG, "refresh");
    }

    @Override
    public void fragmentRefresh() {
        Log.e(TAG, "fragmentRefresh");

    }
}
