package com.getappinfo.app;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

public class MyPagerAdapter   extends PagerAdapter {
    private static final String TAG = "MyPagerAdapter";
    private List<View> mViewList;
    private List<String> mTitleList;

    public MyPagerAdapter(List<View> viewlist, List<String> titlelist) {
        this.mViewList = viewlist;
        this.mTitleList = titlelist;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view==obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        Log.d(TAG,"instantiateItem,"+"position:"+position);
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
        Log.d(TAG,"destroyItem,"+"position:"+position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }


}