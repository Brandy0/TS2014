package com.erhuoapp.erhuo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.erhuoapp.erhuo.fragment.IFragment;

import java.util.ArrayList;

/**
 * 这个类是作为ViewPager的Adapter，它继承自FragmentPagerAdapter
 * <p/>
 * 内部是封装了一些Fragment对象（一般都是FragmentImage对象）
 *
 * NOTE: 2015-1-26 所有的Fragment viewpager的adapter都设置为该类了
 *
 * @author hujiawei
 * @datetime 15/1/2 21:08
 */
public class AdapterFragmentViewPager extends FragmentPagerAdapter {

    private final String TAG = "AdapterFragmentViewPager";

    private ArrayList<IFragment> fragments = new ArrayList<IFragment>();

    public AdapterFragmentViewPager(FragmentManager fragmentManager, ArrayList<IFragment> Fragments) {
        super(fragmentManager);
        this.fragments = Fragments;
    }

    @Override
    public Fragment getItem(int position) {
        //Log.e(TAG, "getItem " + position);
        return (Fragment) fragments.get(position);//这里要保证返回一个Fragment
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //Log.e(TAG, "no destroyItem " + position);//hujiawie 会销毁的！
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Log.e(TAG, "instantiateItem " + position);
        return super.instantiateItem(container, position);
    }

}
