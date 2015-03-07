package com.erhuoapp.erhuo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.erhuoapp.erhuo.R;

/**
 * 首页左边的滑动菜单
 *
 * @author hujiawei
 * @datetime 15/1/2 20:10
 */
public class FragmentMenu extends Fragment {

    private final String TAG = "FragmentMenu";

    private ListView listViewMenu;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_menu, null);
        listViewMenu = (ListView) contentView.findViewById(R.id.lv_menu_classify);
        return contentView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ListView getListViewMenu() {
        return listViewMenu;
    }

    public void setListViewMenu(ListView listViewMenu) {
        this.listViewMenu = listViewMenu;
    }
}
