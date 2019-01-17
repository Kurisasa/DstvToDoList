package com.simp.kurisanic.mylibrary.database;

import android.provider.BaseColumns;

/**
 *
 * Chauke Kurisani
 */

public class ItemTable {

    public class ItemEntry implements BaseColumns {
        public static final String TABLE= "item";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ITEMS_DESC = "itemsDescription";
        public static final String COLUMN_DUE_DATE= "due_date";
        public static final String COLUMN_ITEM_TITLE = "itemTitle";
        public static final String COLUMN_ITEM_STATUS = "status";
    }

}
