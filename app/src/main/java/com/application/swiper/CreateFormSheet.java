package com.application.swiper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateFormSheet extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_form, container, false);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        View view = getView();
//        if (view != null) {
//            View parent = (View) view.getParent();
//            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
//            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            behavior.setSkipCollapsed(true);
//        }
//    }
}
