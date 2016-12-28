package com.ildar.attainment.dateBase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;

import com.ildar.attainment.model.ModelTask;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ildar on 21.07.2016.
 */
public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager(SQLiteDatabase database){
        this.database = database;
    }

    public ModelTask getTask(long timeStamp){
        ModelTask modelTask = null;
        Cursor c = database.query(DBHelper.TASKS_TABLE, null, DBHelper.SELECTION_TIME_STAMP,
                new String[]{Long.toString(timeStamp)}, null, null, null);
        if (c.moveToFirst()){
            String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));

            modelTask = new ModelTask(title, date, priority, status, timeStamp);
        }
        c.close();

        return modelTask;
    }

    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy){
        List<ModelTask> tasks = new ArrayList<>();

        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        if (c.moveToFirst()){
            do{
                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIME_STAMP_COLUMN));
                ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp);
                tasks.add(modelTask);
            } while (c.moveToNext());
        }

        c.close();

        return tasks;
    }
}
