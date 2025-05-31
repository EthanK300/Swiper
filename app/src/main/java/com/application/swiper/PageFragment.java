package com.application.swiper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class PageFragment extends Fragment {
    public PageFragment(){

    }

    public static PageFragment newInstance(String which) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString("type", which);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Safely get the argument
        String fragmentType = getArguments().getString("type");
        System.out.println(fragmentType);

        if(fragmentType.equals("DEFAULT")){
            TextView tv = new TextView(getContext());
            tv.setText("No tasks created yet. Click the + to create them!");
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            ((LinearLayout)view.findViewById(R.id.itemContainer)).addView(tv);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_fragment, container, false);
    }

}
