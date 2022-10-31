package com.bonlala.fitalent.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bonlala.fitalent.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Admin
 * Date 2022/9/8
 */
class T extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dashboard,container,false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
