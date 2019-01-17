package com.simp.kurisanic.mylibrary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simp.kurisanic.mylibrary.model.Item;

import java.util.ArrayList;

/**
 *
 * Chauke Kurisani
 */

public class ItemDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;

    public ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    String TAG = "ItemDatabaseHelper";
    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + ItemTable.ItemEntry.TABLE + " ( " +
                ItemTable.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemTable.ItemEntry.COLUMN_ITEM_TITLE + " TEXT NOT NULL, " +
                ItemTable.ItemEntry.COLUMN_ITEMS_DESC + " TEXT NOT NULL, " +
                ItemTable.ItemEntry.COLUMN_ITEM_STATUS + " TEXT NOT NULL, " +
                ItemTable.ItemEntry.COLUMN_DUE_DATE + " INTEGER);";

        sqLiteDatabase.execSQL(createTable);
    }
    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldDatabaseVersion, int newDatabaseVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemTable.ItemEntry.TABLE);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Item> getAllData() {
        ArrayList<Item> itemlist = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ ItemTable.ItemEntry.TABLE,null);

        while(res.moveToNext()) {
            int id = res.getInt(0);   //0 is the number of id column in your database table, it could be different in your database table
            String title = res.getString(1);
            String description = res.getString(2);
            String status = res.getString(3);
            String due_date = res.getString(4);

            Item newItem = new Item(title,  description, due_date, id,status);
            itemlist.add(newItem);
        }
        return itemlist;
    }
}
