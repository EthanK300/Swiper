package com.application.swiper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskView>{
    private ArrayList<Task> taskList;
    private MainActivity mainActivityReference;
    public TaskAdapter(MainActivity ma, ArrayList<Task> list){
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
            Task t = mainActivityReference.tasksList.get(pos);
            TaskFormSheet editable = new TaskFormSheet(pos, t.title, t.description);
            editable.show(mainActivityReference.getSupportFragmentManager(), "editDialog");
        });

        holder.delete.setOnClickListener(v -> {
            DeleteDialog d = new DeleteDialog(mainActivityReference, pos);
            d.show(mainActivityReference.getSupportFragmentManager(), "deleteDialog");
        });

        holder.data = taskList.get(pos);
        holder.bindData();

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
