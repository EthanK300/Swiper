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
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class CreateFormSheet extends DialogFragment {
    private OnFormSubmittedListener listener;
    EditText title;
    EditText description;
    DatePicker datePicker;
    TimePicker timePicker;
    Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.create_form, container, false);
        title = root.findViewById(R.id.title_input);
        description = root.findViewById(R.id.description_input);
        submitButton = root.findViewById(R.id.submit_button);
        datePicker = root.findViewById(R.id.datePicker);
        timePicker = root.findViewById(R.id.timePicker);

        submitButton.setOnClickListener(v -> {
            String t = title.getText().toString();
            String d = description.getText().toString();
            long date;

            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
            calendar.set(Calendar.MILLISECOND, 0);

            date = calendar.getTimeInMillis();
            System.out.println("creating task with data: " + t + ", " + d + ", " + date);
            listener.onFormSubmitted(t, d, date);
            dismiss();
        });

        return root;
    }
    public interface OnFormSubmittedListener {
        void onFormSubmitted(String title, String description, long dueDate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFormSubmittedListener) context;
    }
}
