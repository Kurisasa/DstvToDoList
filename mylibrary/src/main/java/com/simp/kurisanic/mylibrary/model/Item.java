package com.simp.kurisanic.mylibrary.model;

import android.graphics.Color;

import static android.graphics.Color.rgb;

/**
 *
 * Chauke Kurisani
 */

public class Item {

    private String title;
    private String description;
    private String due_date;
    private String status;
    private String timestamp_dt;
    private int    id;
    private int    priorityColor;

    public static final int     PRIO_DATE = 0;
    public static final int     PRIO_HOUR = 1;
    public static final int     PRIO_TOOLATE = 2;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp_dt() {
        return timestamp_dt;
    }

    public void setTimestamp_dt(String timestamp_dt) {
        this.timestamp_dt = timestamp_dt;
    }

    public int getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(int priorityColor) {
        this.priorityColor = priorityColor;
    }

    public static int[] getColorCode() {
        return colorCode;
    }

    public static int   getColorCodeById(int id) {
        return colorCode[id];
    }


    public static final int[]   colorCode = { rgb(34,139,34), rgb(255,140,0), Color.RED};

    public Item(String title, String description, String due_date, int id, String status) {
        super();
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.id = id;
        this.status = status;
        this.timestamp_dt = due_date;
        this.priorityColor = 0;
    }
}
