package com.application.swiper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TaskView extends RecyclerView.ViewHolder{
    DraggableTask draggable;
    ImageButton edit;
    ImageButton delete;
    Task data;
    TextView t;
    int pos;
    public TaskView(View v){
        super(v);
        t = v.findViewById(R.id.card_text);
        draggable = (DraggableTask)v;
        edit = v.findViewById(R.id.edit);
        delete = v.findViewById(R.id.delete);
    }

    public void bindData(){
        // TODO: take data from task object and stick it into textviews for user
        String text = data.title + " : " + data.description;
        pos = this.getAdapterPosition();
        t.setText(text);
    }
}
