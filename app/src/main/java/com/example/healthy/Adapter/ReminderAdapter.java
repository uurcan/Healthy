package com.example.healthy.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthy.Databases.ReminderDatabase;
import com.example.healthy.R;
import com.example.healthy.Utils.ApplicationConstants;

public class ReminderAdapter extends CursorAdapter {
    private LayoutInflater layoutInflater;

    public ReminderAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle =  view.findViewById(R.id.textReminderItemTitle);
        TextView textViewText =  view.findViewById(R.id.textReminderItemText);
        TextView textViewDate =  view.findViewById(R.id.textReminderItemDate);
        TextView textViewTime =  view.findViewById(R.id.textReminderItemTime);
        TextView textViewType =  view.findViewById(R.id.textReminderItemType);
        ImageView imageViewTypeLogo = view.findViewById(R.id.reminderNotificationTypeLogo);
        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.TITLE)));
        textViewText.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.DETAIL)));
        textViewDate.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.DATE)));
        textViewTime.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.TIME)));
        textViewType.setText(cursor.getString(cursor.getColumnIndex(ReminderDatabase.TYPE)));
        if (cursor.getString(cursor.getColumnIndex(ReminderDatabase.TYPE)) != null) {
            if (cursor.getString(cursor.getColumnIndex(ReminderDatabase.TYPE)).equals(ApplicationConstants.CONSTANT_NOTIFICATION_ALARM))
                imageViewTypeLogo.setImageResource(R.drawable.ico_reminder_cell_alarm);
            else if (cursor.getString(cursor.getColumnIndex(ReminderDatabase.TYPE)).equals(ApplicationConstants.CONSTANT_NOTIFICATION_NOTIFICATION))
                imageViewTypeLogo.setImageResource(R.drawable.ico_reminder_cell_notify);
        }else
            imageViewTypeLogo.setImageResource(R.drawable.app_logo);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.reminder_list_item,parent,false);
    }

}
