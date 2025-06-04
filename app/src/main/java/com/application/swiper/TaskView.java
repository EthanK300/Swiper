package com.application.swiper;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TaskView extends RecyclerView.ViewHolder{
    Task data;
    TextView t;
    public TaskView(View v){
        super(v);
        t = v.findViewById(R.id.card_text);
    }

    public void bindData(){
        // TODO: take data from task object and stick it into textviews for user
        t.setText(data.title + " : " + data.description);
    }
}
