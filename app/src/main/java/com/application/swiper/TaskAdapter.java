package com.application.swiper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskView>{
    private List<Task> taskList;

    public TaskAdapter(List<Task> list){
        this.taskList = list;
    }

    @NonNull
    @Override
    public TaskView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskView holder, int pos) {
        holder.data = taskList.get(pos);
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
