package com.app.markeet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.markeet.R;

public class FragmentUmkm extends Fragment {
    private View root_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root_view = inflater.inflate(R.layout.fragment_umkm,null);
       return root_view;
    }

}
