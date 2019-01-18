package com.simp.kurisanic.mylibrary.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simp.kurisanic.mylibrary.R;
import com.simp.kurisanic.mylibrary.adapters.MyAdapter;
import com.simp.kurisanic.mylibrary.database.ItemDatabaseHelper;
import com.simp.kurisanic.mylibrary.database.ItemTable;
import com.simp.kurisanic.mylibrary.model.Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * Chauke Kurisani
 */
public class ListActivity extends AppCompatActivity {
    private static final String     TAG = "ListActivity";
    private ItemDatabaseHelper mHelper;
    private ListView                mTaskListView;
    private MyAdapter mAdapter;
    private boolean                 isAlphabetical;
    private boolean                 isFiltered;
    private String                  searchText;
    private EditText                searchBar;
    private View                    focus;
    private FloatingActionButton     fab;
    private SeekBar                seekBar;
    private TextView                percentage;
    private  ArrayList<Item> allItems = null;
    String totals;
    double total;
    int workProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        focus = (View) findViewById(R.id.focus_point);
        focus.clearFocus();
        isAlphabetical = true;
        isFiltered = false;
        searchText = "";

        TimeZone.setDefault(TimeZone.getTimeZone("Universal"));
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Universal"));

        mHelper = new ItemDatabaseHelper(this);
        mTaskListView = (ListView) findViewById(R.id.task_list);
        searchBar = (EditText) findViewById(R.id.search_bar);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        percentage = (TextView) findViewById(R.id.percentage);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(ListActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the textview with '0'.
        //percentage.setText("Done: " + seekBar.getProgress() + "%" );

//        seekBar.setProgress(0);
//        seekBar.setMax(100);
//        seekBar.setProgress(workProgress);

        seekBar.post(new Runnable() {
            @Override
            public void run() {

                if(workProgress > 0){
                    seekBar.setProgress(workProgress);
                    seekBar.postDelayed(mProgressRunner, 1000);
                }else{
                    seekBar.setProgress(0);
                }


            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                if(workProgress > 0){
                    seekBar.setProgress(workProgress);
                }else{
                    seekBar.setProgress(0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                if(workProgress > 0){
//                    seekBar.setProgress(workProgress);
//                }else{
//                    seekBar.setProgress(0);
//                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(workProgress > 0){
                    seekBar.setProgress(workProgress);
                }else{
                    seekBar.setProgress(0);
                }
            }

        });

        try {
            updateList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
}
    Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (seekBar != null) {
                seekBar.setProgress(workProgress); // update seekbar

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateList();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                isFiltered = (searchBar.getText().length() > 0) ? true : false;
                searchText = searchBar.getText().toString();

                if (isFiltered == false)
                    focus.clearFocus();

                try {
                    updateList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        searchBar.addTextChangedListener(tw);
    }

    private ArrayList<Item> generateData() throws ParseException { ArrayList<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String query = "SELECT * FROM " + ItemTable.ItemEntry.TABLE;

        if (isFiltered) {
            query += " WHERE itemTitle LIKE '%" + searchText + "%'";
        }

        query += " ORDER BY status DESC,";

        if (isAlphabetical) {
            query +=  " itemTitle COLLATE NOCASE";
        } else {
            query +=  " due_date";
        }

        Log.d(TAG, "Query: " +query);

        Cursor cursor = db.rawQuery(query, null);
        renderList(cursor, items);

        cursor.close();
        db.close();
        return items;
    }

    void renderList(Cursor cursor, ArrayList<Item> items) throws ParseException {

        while(cursor.moveToNext()) {
            Item _item = new Item(cursor.getString(cursor.getColumnIndex(ItemTable.ItemEntry.COLUMN_ITEM_TITLE)),
                    cursor.getString(cursor.getColumnIndex(ItemTable.ItemEntry.COLUMN_ITEMS_DESC)),
                    Integer.toString(cursor.getInt(cursor.getColumnIndex(
                            ItemTable.ItemEntry.COLUMN_DUE_DATE))),
                    cursor.getInt(cursor.getColumnIndex(ItemTable.ItemEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(ItemTable.ItemEntry.COLUMN_ITEM_STATUS)));

            renderDateField(cursor, _item);

            _item.setTimestamp_dt(Integer.toString(cursor.getInt(cursor.getColumnIndex(
                    ItemTable.ItemEntry.COLUMN_ITEM_STATUS))));

            items.add(_item);
        }

        allItems = items;
    }

    void renderDateField(Cursor cursor, Item _item) throws ParseException {

        // Format and fill date from Unix timeStamp
        DateFormat sdfInit;
        DateFormat sdfRender;

        long timeStamp = Long.parseLong(cursor.getString(cursor.getColumnIndex(ItemTable.ItemEntry.COLUMN_DUE_DATE)));
        Date newDate = new Date(timeStamp*1000L);

        long realtimeTimeStamp = System.currentTimeMillis() / 1000L;
        Date realtimeDate = new Date(realtimeTimeStamp*1000L);

        sdfInit = new SimpleDateFormat("dd/MM/yyyy");
        String initFormat = sdfInit.format(realtimeDate);
        String renderFormat;

        if (realtimeTimeStamp > timeStamp) {
            renderFormat = "Over Due";
            _item.setPriorityColor(Item.PRIO_TOOLATE);

        } else if (timeStamp > getStartOfDayInMillis(initFormat) && timeStamp < getEndOfDayInMillis(initFormat)) {
            sdfRender = new SimpleDateFormat("H:mm");
            renderFormat = sdfRender.format(newDate);
            _item.setPriorityColor(Item.PRIO_HOUR);

        } else {
            sdfRender = new SimpleDateFormat("dd/MM/yyyy");
            renderFormat = sdfRender.format(newDate);
            _item.setPriorityColor(Item.PRIO_DATE);
        }

        _item.setDue_date(renderFormat);
    }

    public void updateList() throws ParseException {
        mAdapter = new MyAdapter(this, generateData());
        mTaskListView.setAdapter(mAdapter);

        if(allItems != null) {
            ArrayList<Item> selectedItem = new ArrayList<>();
            for (int i = 0; i < allItems.size(); i++) {
                Item item = allItems.get(i);
                if (item.getStatus().equals("completed"))
                    selectedItem.add(item);
                }
                final double value = allItems.size();
                final double value1 = selectedItem.size();
                total = ((value1) / value) * 100;
                workProgress = (int) total;
                totals = Integer.toString(workProgress);


                percentage.setText(totals + "%");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_add_task) {
            Intent intent = new Intent(ListActivity.this, CreateActivity.class);
            startActivity(intent);
            return true;

        }else if(i == R.id.menu_search)
        {
            Toast.makeText(getApplicationContext(), "Not Implemented yet", Toast.LENGTH_SHORT).show();
           return true;
        }
        else if (i == R.id.action_swap_order) {
            isAlphabetical = !isAlphabetical;
            try {
                updateList();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    public long getStartOfDayInMillis(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }
    
    public long getEndOfDayInMillis(String date) throws ParseException {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis(date) + (24 * 60 * 60);
    }

}


