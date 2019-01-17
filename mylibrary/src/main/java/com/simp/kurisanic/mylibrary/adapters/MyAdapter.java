package com.simp.kurisanic.mylibrary.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.simp.kurisanic.mylibrary.R;
import com.simp.kurisanic.mylibrary.activities.ListActivity;
import com.simp.kurisanic.mylibrary.activities.UpdateActivity;
import com.simp.kurisanic.mylibrary.database.ItemDatabaseHelper;
import com.simp.kurisanic.mylibrary.database.ItemTable;
import com.simp.kurisanic.mylibrary.model.Item;

import java.text.ParseException;
import java.util.ArrayList;

/**
 *
 * Chauke Kurisani
 */
public class MyAdapter extends ArrayAdapter<Item> {

    private final String            TAG = "MyAdapter";
    private final Context           context;
    private final ArrayList<Item>    itemsArrayList;
    private       Activity           ListRef;
    private ItemDatabaseHelper mHelper;
    View.OnClickListener checkBoxListener;

    public MyAdapter(Context context, ArrayList<Item> itemsArrayList) {
        super(context, R.layout.item, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        mHelper = new ItemDatabaseHelper(this.context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item, parent, false);

        final TextView titleView = (TextView) itemView.findViewById(R.id.item_title);
        final TextView descView = (TextView) itemView.findViewById(R.id.item_desc);
        final TextView duetimeView = (TextView) itemView.findViewById(R.id.item_duetime);
        final TextView idView = (TextView) itemView.findViewById(R.id.item_id);
        final TextView statusView = (TextView) itemView.findViewById(R.id.item_status);
        final TextView timestampView = (TextView) itemView.findViewById(R.id.item_timestamp);
        final CheckBox checkbox = (CheckBox) itemView.findViewById(R.id.checkBox1);

        titleView.setText(itemsArrayList.get(position).getTitle());
        descView.setText(itemsArrayList.get(position).getDescription());
        duetimeView.setText(itemsArrayList.get(position).getDue_date());
        idView.setText(Integer.toString(itemsArrayList.get(position).getId()));
        statusView.setText(itemsArrayList.get(position).getStatus());
        timestampView.setText(itemsArrayList.get(position).getTimestamp_dt());

        int priorityColor = itemsArrayList.get(position).getPriorityColor();

        if (priorityColor >= 0 && priorityColor <= 2)
            duetimeView.setTextColor(itemsArrayList.get(position).getColorCodeById(priorityColor));

        if (statusView.getText().equals("completed")) {
            titleView.setPaintFlags((titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG));
            checkbox.setVisibility(View.GONE);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                Log.d(TAG, "OnClick." +
                                "\ntitle: " + String.valueOf(titleView.getText()) +
                                "\ndesc: " + String.valueOf(descView.getText()) +
                                "\nid: " + String.valueOf(idView.getText()));
                intent.putExtra("title", String.valueOf(titleView.getText()));
                intent.putExtra("desc", String.valueOf(descView.getText()));
                intent.putExtra("id", String.valueOf(idView.getText()));
                intent.putExtra("due_time", String.valueOf(timestampView.getText()));

                context.startActivity(intent);
            }
        });

        checkBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                int i = v.getId();
                if (i == R.id.checkBox1) {
                    if (checked) {
                        // Do your coding
                        Toast.makeText(context,
                                "Task, Completed :)", Toast.LENGTH_LONG).show();

                        SQLiteDatabase db = mHelper.getReadableDatabase();
                        ContentValues values = new ContentValues();

                        Log.d(TAG, "Status: " + statusView.getText()
                                + " | Title: " + titleView.getText()
                                + " | ID: " + idView.getText()
                                + " | desc: " + descView.getText());

                        if (statusView.getText().equals("progress")) {
                            values.put(ItemTable.ItemEntry.COLUMN_ITEM_STATUS, "completed");
                            titleView.setPaintFlags((titleView.getPaintFlags() | (~Paint.STRIKE_THRU_TEXT_FLAG)));
                        } else {
                            values.put(ItemTable.ItemEntry.COLUMN_ITEM_STATUS, "progress");
                            //  titleView.setPaintFlags((titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        db.update(ItemTable.ItemEntry.TABLE, values, "_id=" + idView.getText(), null);
                        db.close();
                        try {
                            ((ListActivity) context).updateList();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        checkbox.setChecked(false);
                        // Do your coding
                        Toast.makeText(context,
                                "Task, on Progress :)", Toast.LENGTH_LONG).show();
                    }

                }
            }
        };

        checkbox.setOnClickListener(checkBoxListener);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                SQLiteDatabase db = mHelper.getReadableDatabase();
                ContentValues values = new ContentValues();

                Log.d(TAG, "Status: "+statusView.getText()
                        + " | Title: " + titleView.getText()
                        + " | ID: " + idView.getText()
                        + " | desc: " + descView.getText());

                if (statusView.getText().equals("progress")) {
                    values.put(ItemTable.ItemEntry.COLUMN_ITEM_STATUS, "completed");
                    titleView.setPaintFlags((titleView.getPaintFlags() | (~ Paint.STRIKE_THRU_TEXT_FLAG)));
                } else {
                    values.put(ItemTable.ItemEntry.COLUMN_ITEM_STATUS, "progress");
                }
                db.update(ItemTable.ItemEntry.TABLE, values, "_id="+ idView.getText(), null);
                db.close();
                try {
                    ((ListActivity) context).updateList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        return itemView;
    }
}