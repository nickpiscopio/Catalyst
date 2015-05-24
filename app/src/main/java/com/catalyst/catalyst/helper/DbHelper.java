package com.catalyst.catalyst.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.catalyst.catalyst.Model.Table;

import java.util.ArrayList;

/**
 * Created by nickpiscopio on 5/24/15.
 */
public class DbHelper extends SQLiteOpenHelper
{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Catalyst.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + InspirationTable.TABLE_NAME + " (" +
            InspirationTable.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY" + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_IMAGE_ID + TEXT_TYPE + COMMA_SEP +
            InspirationTable.COLUMN_NAME_DATE_DISPLAYED + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_HIDDEN + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_AUTHOR + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_CATEGORY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_DATE_TO_DISPLAY + TEXT_TYPE +
            " ); " +
            "CREATE TABLE IF NOT EXISTS " + AppTable.TABLE_NAME + " (" +
            AppTable.COLUMN_NAME_VERSION + INTEGER_TYPE + " PRIMARY KEY" + NOT_NULL +
            " ); ";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InspirationTable.TABLE_NAME + "; " +
                                                     "DROP TABLE IF EXISTS " + AppTable.TABLE_NAME;

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        Log.i("CreateDatabase", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insert(SQLiteDatabase db, ArrayList<Table> list)
    {
        try
        {
            db.beginTransaction();

            for (Table table : list)
            {
                db.insert(table.getTableName(), null, table.getContentValues());
            }

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}