package com.application.swiper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Pair;

import androidx.fragment.app.DialogFragment;

public class DeleteDialog extends DialogFragment {
    private MainActivity ma;
    private int pos;

    public DeleteDialog(MainActivity ma, int pos){
        this.ma = ma;
        this.pos = pos;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ma.deleteTask(pos);
                        // request to delete the task
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("cancel : id : " + id);
                        // cancelled the delete, do nothing
                    }
                });
        return builder.create();
    }
}