package com.erhuoapp.erhuo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erhuoapp.erhuo.R;

/**
 * 右边的滑动菜单，实际为空，使用它的原因是为了不改动自定义的SlidingMenu组件
 *
 * @author hujiawei
 * @datetime 15/1/2 20:10
 */
public class FragmentEmpty extends Fragment {

    private final String TAG = "FragmentMenu";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
		return inflater.inflate(R.layout.fragment_empty, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
