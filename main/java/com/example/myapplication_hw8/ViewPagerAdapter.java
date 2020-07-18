package com.example.myapplication_hw8;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"PRODUCT", "SELLER INFO", "SHIPPING"};

    private List<Fragment> mFragment;

    private String data;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mFragment = new ArrayList<>();
        mFragment.add(new ProductFramgent());
        mFragment.add(new SellerFragment());
        mFragment.add(new ShippingFragment());
    }

    @Override
    public Fragment getItem(int position) {
        //return TabFragment.getInstance(position);
        Fragment re = mFragment.get(position);

        return re;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    public static class ProductFramgent extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.product, container, false);
            return view;
        }
    }

    public static class ShippingFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.shipping, container, false);
            return view;
        }
    }

    public static class SellerFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.seller, container, false);
            return view;
        }
    }
}