package com.example.healthy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.healthy.Databases.ReminderDatabase;
import com.example.healthy.R;
import com.example.healthy.Services.AlarmReceiver;
import com.example.healthy.Services.NotificationReceiver;
import com.example.healthy.UI.Activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReminderFragment extends BaseFragment implements View.OnClickListener{

    private SQLiteDatabase sqLite;
    private EditText edtTitle,edtText;
    private TextView selectedTime;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Button buttonInitializeTimePicker;
    private RadioButton radioButtonAlarm,radioButtonNotify;
    private ViewGroup transitionsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reminder_item_detail, container, false);
        if(getActivity()!=null) {
            ((MainActivity) getActivity()).updateToolbarTitle(getString(R.string.title_reminder));
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ReminderDatabase dbHelper = new ReminderDatabase(getContext());
        Button buttonSubmit = view.findViewById(R.id.buttonReminderSubmit);
        buttonSubmit.setOnClickListener(this);
        sqLite = dbHelper.getWritableDatabase();
        edtTitle = view.findViewById(R.id.editTextReminderTitle);
        edtText = view.findViewById(R.id.editTextReminderText);
        timePicker = view.findViewById(R.id.timePickerReminder);
        datePicker = view.findViewById(R.id.datePickerReminder);
        selectedTime = view.findViewById(R.id.textViewSelectedTime);
        radioButtonAlarm = view.findViewById(R.id.radioButtonAlarm);
        radioButtonNotify = view.findViewById(R.id.radioButtonNotify);
        transitionsContainer = view.findViewById(R.id.viewGroupDateTimePicker);
        buttonInitializeTimePicker = view.findViewById(R.id.btnInitializeTimePicker);
        buttonInitializeTimePicker.setOnClickListener(this);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                notifyTimeChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonReminderSubmit:
                setReminderVariables();
                break;
            case R.id.btnInitializeTimePicker:
                initializeTimePicker();
                break;
        }
    }

    private void setReminderVariables() {
        if(getActivity() != null) {
            initializeCalendar();
            String title = edtTitle.getText().toString();
            String detail = edtText.getText().toString();
            ContentValues cv = new ContentValues();

            cv.put(ReminderDatabase.TITLE, title);
            cv.put(ReminderDatabase.DETAIL, detail);
            cv.put(ReminderDatabase.TIME, getString(R.string.not_set));

            if (radioButtonAlarm.isChecked()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(getString(R.string.time_format));
                String timeString = format.format(new Date(initializeCalendar().getTimeInMillis()));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
                String dateString = dateFormat.format(new Date(initializeCalendar().getTimeInMillis()));

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                String alertTitle = edtText.getText().toString();
                intent.putExtra("title", alertTitle);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, initializeCalendar().getTimeInMillis(), pendingIntent);
                cv.put(ReminderDatabase.TIME, timeString);
                cv.put(ReminderDatabase.DATE, dateString);
                cv.put(ReminderDatabase.TYPE, getString(R.string.alert));

            } else if (radioButtonNotify.isChecked()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(getString(R.string.time_format));
                String timeString = format.format(new Date(initializeCalendar().getTimeInMillis()));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = dateFormat.format(new Date(initializeCalendar().getTimeInMillis()));

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), NotificationReceiver.class);
                String alertTitle = edtTitle.getText().toString();
                String alertContent = edtText.getText().toString();

                intent.putExtra("title", alertTitle);
                intent.putExtra("alert_content", alertContent);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, initializeCalendar().getTimeInMillis(), pendingIntent);
                cv.put(ReminderDatabase.TIME, timeString);
                cv.put(ReminderDatabase.DATE, dateString);
                cv.put(ReminderDatabase.TYPE, getString(R.string.notification));
            }
            sqLite.insert(ReminderDatabase.TABLE_NAME, null, cv);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ReminderFragment reminderFragment = new ReminderFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, reminderFragment)
                    .addToBackStack(null).commit();
            Toast.makeText(getContext(),(getString(R.string.alarm_has_been_set)),Toast.LENGTH_LONG).show();
        }
    }

    private Calendar initializeCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR,datePicker.getYear());
        calendar.set(Calendar.MONTH,datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR,timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND,0);
        return calendar;
    }

    private void notifyTimeChanged(){
        int year,month,day,hour,minute;
        year = datePicker.getYear();
        month = datePicker.getMonth()+1;
        day = datePicker.getDayOfMonth();
        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();

        StringBuilder valueToPut = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).append(" ").append(hour).append(":").append(minute);
        if(minute < 10){
            StringBuilder newStringBuilder = new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year).append(" ").append(hour).append(":").append("0").append(minute);
            selectedTime.setText(newStringBuilder);
        }else
            selectedTime.setText(valueToPut);
    }

    private void initializeTimePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(transitionsContainer,new AutoTransition());
        }
            timePicker.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.GONE);
            buttonInitializeTimePicker.setVisibility(View.GONE);
    }
}
