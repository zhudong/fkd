package com.fuexpress.kr.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuexpress.kr.R;

/**
 * Created by Administrator on 2017-7-17.
 */

public class CartItemDetailsFragment extends MyBottomSheetDialogFragment {
    public static final String REMIXER_TAG = "Remixer";

    private View rootView;
    private boolean isAddingFragment = false;
    private final Object syncLock = new Object();

    public static CartItemDetailsFragment newInstance() {
        return new CartItemDetailsFragment();
    }


    public void attachToButton(final FragmentActivity activity, View button) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showRemixer(activity.getSupportFragmentManager(), REMIXER_TAG);
            }
        });
    }

    public void showRemixer(FragmentManager manager, String tag) {
        synchronized (syncLock) {
            if (!isAddingFragment && !isAdded()) {
                isAddingFragment = true;
                show(manager, tag);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cart_item_details, container, false);
        return rootView;
    }
}
