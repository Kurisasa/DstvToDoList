package com.simp.kurisanic.mylibrary.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
public class CreateActivity extends AppCompatActivity {

    private static final String TAG = "CreateActivity";
    private EditText            _title;
    private EditText            _desc;
    private Button              cancel_button;
    private Button              create_button;
    private Button              date_button;
    private ItemDatabaseHelper  mHelper;
    private String              date_time;
    private int                 mYear;
    private int                 mMonth;
    private int                 mDay;
    private int                 mHour;
    private int                 mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mHelper = new ItemDatabaseHelper(this);
        TimeZone.setDefault(TimeZone.getTimeZone("Universal"));

        _title = (EditText) findViewById(R.id.fieldTitle);
        _desc = (EditText) findViewById(R.id.fieldDesc);

        cancel_button = (Button) findViewById(R.id.cancelCreateButton);
        create_button = (Button) findViewById(R.id.createButton);
        date_button = (Button) findViewById(R.id.dateButton);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Universal"));
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Init button at current time()
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        date_button.setText(sdf.format(new Date()));

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

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Generate and execute query to add an item in database
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                String textTitle = String.valueOf(_title.getText());
                String textDesc = String.valueOf(_desc.getText());

                values.put(ItemTable.ItemEntry.COLUMN_ITEM_TITLE, textTitle);
                values.put(ItemTable.ItemEntry.COLUMN_ITEMS_DESC, textDesc);
                values.put(ItemTable.ItemEntry.COLUMN_ITEM_STATUS, "progress");

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm");
                try {
                    Date date = (Date)formatter.parse(date_time);
                    values.put(ItemTable.ItemEntry.COLUMN_DUE_DATE, date.getTime() / 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                db.insertWithOnConflict(ItemTable.ItemEntry.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                db.close();
                finish();
            }
        });

        updateCreateButtonState();
        updateDateButton();
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
                updateCreateButtonState();
            }
        };

        _title.addTextChangedListener(tw);
        _desc.addTextChangedListener(tw);
    }

    // Enable or disable the create button if title is empty
    void updateCreateButtonState() {
        create_button.setEnabled(!TextUtils.isEmpty(_title.getText()));
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
