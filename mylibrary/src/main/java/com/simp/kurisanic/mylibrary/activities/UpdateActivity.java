package com.simp.kurisanic.mylibrary.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.simp.kurisanic.mylibrary.R;
import com.simp.kurisanic.mylibrary.database.ItemDatabaseHelper;
import com.simp.kurisanic.mylibrary.database.ItemTable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * Chauke Kurisani
 */

public class UpdateActivity extends AppCompatActivity {

    private static final String TAG = "UpdateActivity";
    private EditText            _title;
    private EditText            _desc;
    private int                 _id;
    private Button              cancel_button;
    private Button              update_button;
    private Button              delete_button;
    private Button              date_button;
    private ItemDatabaseHelper mHelper;
    private String  date_time;
    private int     mYear;
    private int     mMonth;
    private int     mDay;
    private int     mHour;
    private int     mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mHelper = new ItemDatabaseHelper(this);

        _title = (EditText) findViewById(R.id.fieldTitle);
        _desc = (EditText) findViewById(R.id.fieldDesc);

        Intent intent = getIntent();
        _title.setText(intent.getStringExtra("title"));
        _desc.setText(intent.getStringExtra("desc"));
        _id = Integer.parseInt(intent.getStringExtra("id"));
        date_time = (intent.getStringExtra("due_time"));

        cancel_button = (Button) findViewById(R.id.cancelUpdateButton);
        update_button = (Button) findViewById(R.id.updateButton);
        delete_button = (Button) findViewById(R.id.deleteButton);
        date_button = (Button) findViewById(R.id.dateButton);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Universal"));
        Date defaultDate = new Date();
        defaultDate.setTime(Long.parseLong(date_time) * 1000);
        c.setTime(defaultDate);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Init button at current time()
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        date_button.setText(sdf.format(defaultDate));

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(ItemTable.ItemEntry.COLUMN_ITEM_TITLE, String.valueOf(_title.getText()));
                values.put(ItemTable.ItemEntry.COLUMN_ITEMS_DESC, String.valueOf(_desc.getText()));

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm");
                try {
                    Date date = (Date)formatter.parse(date_time);
                    values.put(ItemTable.ItemEntry.COLUMN_DUE_DATE, date.getTime() / 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                db.update(ItemTable.ItemEntry.TABLE, values, "_id="+ _id, null);
                db.close();
                finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                db.delete(ItemTable.ItemEntry.TABLE, "_id="+ _id, null);
                db.close();
                finish();
            }
        });
        updateUpdateButtonState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                updateUpdateButtonState();
            }
        };

        _title.addTextChangedListener(tw);
        _desc.addTextChangedListener(tw);
    }

    // Enable or disable the update button if title is empty
    void updateUpdateButtonState() {
        update_button.setEnabled(!TextUtils.isEmpty(_title.getText()));
    }

    void updateDateButton() {

        // Build date string from Custom dialog
        date_time =  mDay + "/" +(mMonth + 1)  + "/" + mYear + " "
                + mHour + ":" + mMinute;

        String toUpdate;
        toUpdate = CustomDateFactory("dd/MM/yyyy H:mm",
                "EEE, d MMM yyyy HH:mm",
                date_time);

        date_button.setText(toUpdate);
    }

    String CustomDateFactory(String expectedPattern, String outputPattern, String input) {

        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(outputPattern);
            Date date = formatter.parse(input);
            String output = sdf.format(date);
            return (output);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return (null);
    }

    private void datePicker(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (view.isShown()) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;

                            updateDateButton();
                            timePicker();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select a day");
        datePickerDialog.show();
    }

    private void timePicker(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (view.isShown()) {
                            mHour = hourOfDay;
                            mMinute = minute;

                            updateDateButton();
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.setTitle("Select an hour");
        timePickerDialog.show();
    }
}
