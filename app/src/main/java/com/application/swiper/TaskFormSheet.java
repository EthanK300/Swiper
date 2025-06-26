package com.application.swiper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TaskFormSheet extends DialogFragment {
    private OnFormSubmittedListener listener;
    EditText title;
    EditText description;
    DatePicker datePicker;
    TimePicker timePicker;
    Button submitButton;
    Button cancelButton;
    int pos;
    String iname;
    String idescription;

    public TaskFormSheet(int pos){
        this.pos = pos;
    }

    public TaskFormSheet(int pos, String name, String description){
        this.pos = pos;
        iname = name;
        idescription = description;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.task_form, container, false);
        title = root.findViewById(R.id.title_input);
        description = root.findViewById(R.id.description_input);
        submitButton = root.findViewById(R.id.submit_button);
        datePicker = root.findViewById(R.id.datePicker);
        timePicker = root.findViewById(R.id.timePicker);
        cancelButton = root.findViewById(R.id.cancel_button);

        if(pos == -1){
            // if it is a create form, then use this one and remove the cancel button
            cancelButton.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) submitButton.getLayoutParams();
            params.horizontalBias = 0.5f;
            submitButton.setLayoutParams(params);
        }else{
            submitButton.setText("Confirm");
            title.setText(iname);
            description.setText(idescription);
        }

        submitButton.setOnClickListener(v -> {
            String t = title.getText().toString();
            String d = description.getText().toString();
            long date;

            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
            calendar.set(Calendar.MILLISECOND, 0);

            date = calendar.getTimeInMillis();
            System.out.println("creating task with data: " + t + ", " + d + ", " + date);
            listener.onFormSubmitted(t, d, date, pos);
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        return root;
    }
    public interface OnFormSubmittedListener {
        void onFormSubmitted(String title, String description, long dueDate, int pos);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFormSubmittedListener) context;
    }
}
