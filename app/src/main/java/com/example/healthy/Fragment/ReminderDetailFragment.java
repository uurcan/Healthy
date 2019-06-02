package com.example.healthy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.healthy.Databases.ReminderDatabase;
import com.example.healthy.R;
import com.example.healthy.UI.Activity.MainActivity;

public class ReminderDetailFragment extends BaseFragment implements View.OnClickListener {

    private SQLiteDatabase sqLiteDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reminder_detail, container, false);
        if (getActivity() != null)
            ((MainActivity) getActivity()).updateToolbarTitle(getString(R.string.reminder_fragment));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getIntentExtras(view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnReminderDelete:
                deleteSelectedRowDialog();
                break;
            case R.id.btnReminderEdit:
                switchToEditActivity();
                break;
        }
    }

    private void getIntentExtras(View v) {
        if (getActivity() != null) {
            if (getActivity().getIntent().getExtras() != null) {
                long rowId = getActivity().getIntent().getExtras().getLong("rowId");
                ReminderDatabase reminderDatabase = new ReminderDatabase(getContext());
                sqLiteDatabase = reminderDatabase.getWritableDatabase();
                @SuppressLint("Recycle")
                Cursor cursor = sqLiteDatabase.rawQuery
                        ("select * from " + ReminderDatabase.TABLE_NAME + " where " + ReminderDatabase.COLUMN_ID + "=" + rowId, null);
                Button deleteReminder = v.findViewById(R.id.btnReminderDelete);
                deleteReminder.setOnClickListener(this);
                Button editReminder = v.findViewById(R.id.btnReminderEdit);
                editReminder.setOnClickListener(this);
                TextView title = v.findViewById(R.id.textReminderDetailTitle);
                TextView text = v.findViewById(R.id.textReminderDetailText);
                TextView dateTime = v.findViewById(R.id.textReminderDetailTime);
                TextView type = v.findViewById(R.id.textReminderDetailType);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String date = cursor.getString(cursor.getColumnIndex(ReminderDatabase.DATE));
                        String time = cursor.getString(cursor.getColumnIndex(ReminderDatabase.TIME));
                        title.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.TITLE)));
                        text.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.DETAIL)));
                        type.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.TYPE)));
                        String dateAndTime = date + " " + time;
                        dateTime.setText(dateAndTime);
                    }
                    cursor.close();
                }
            }
        }
    }

    private void deleteSelectedRowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.are_you_sure_want_to_delete))
                .setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.ic_warning)
                .setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedVariable();
                    }
                }).show();
    }

    private void deleteSelectedVariable() {
        if (getActivity() != null) {
            if (getActivity().getIntent().getExtras() != null) {
                long id = getActivity().getIntent().getExtras().getLong("rowId");
                sqLiteDatabase.delete(ReminderDatabase.TABLE_NAME, ReminderDatabase.COLUMN_ID + "=" + id, null);
                sqLiteDatabase.close();
                Toast.makeText(getContext(), getString(R.string.selected_value_removed), Toast.LENGTH_LONG).show();
                switchPreviousPage();
            }
        }
    }

    private void switchPreviousPage(){
        if(getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ReminderFragment reminderFragment = new ReminderFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, reminderFragment).commit();
        }
    }

    private void switchToEditActivity() {
        if (getActivity() != null) {
            if (getActivity().getIntent().getExtras() != null) {
                long id = getActivity().getIntent().getExtras().getLong("rowId");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new EditReminderFragment();
                getActivity().getIntent().putExtra("rowId", id);
                fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
            }
        }
    }
}
