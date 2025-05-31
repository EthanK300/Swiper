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
        String userType = getArguments().getString("type");
        LinearLayout container = view.findViewById(R.id.itemContainer);

        if(userType.equals("newGuest") || userType.equals("newUser")){
            TextView tv = new TextView(getContext());
            tv.setText("No tasks yet. Click the + to add some!");
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            container.addView(tv);
        }else if(userType.equals("guest")){

        }else{
            // call web api to update task list
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_fragment, container, false);
    }

}
