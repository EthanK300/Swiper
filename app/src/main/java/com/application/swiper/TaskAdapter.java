package com.application.swiper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskView>{
    private List<Task> taskList;
    private MainActivity mainActivityReference;
    public TaskAdapter(MainActivity ma, List<Task> list){
        this.mainActivityReference = ma;
        this.taskList = list;
    }

    @NonNull
    @Override
    public TaskView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DraggableTask v = (DraggableTask)LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskView holder, int pos) {
        holder.draggable.setTaskActionListener(new TaskAction() {
            @Override
            public void delayTask(int pos) {
                mainActivityReference.delayTask(pos);
            }

            @Override
            public void completeTask(int pos) {
                mainActivityReference.completeTask(pos);
            }
        });

        holder.edit.setOnClickListener(v -> {
            mainActivityReference.editTask(pos);
        });

        holder.delete.setOnClickListener(v -> {
            mainActivityReference.deleteTask(pos);
        });

        holder.data = taskList.get(pos);
        holder.bindData();

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
