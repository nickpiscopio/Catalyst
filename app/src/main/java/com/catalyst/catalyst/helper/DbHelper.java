package com.catalyst.catalyst.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.catalyst.catalyst.entity.Record;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Database helper file.
 *
 * Created by Nick Piscopio on 5/24/15.
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

    private static final long ELIGIBLE_INSPIRATION_DATE =  Calendar.getInstance().getTimeInMillis() - 604800000;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + InspirationTable.TABLE_NAME + " (" +
            InspirationTable.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY" + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_IMAGE_ID + TEXT_TYPE + COMMA_SEP +
            InspirationTable.COLUMN_NAME_DATE_DISPLAYED + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_HIDDEN + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_AUTHOR + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_CATEGORY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            InspirationTable.COLUMN_NAME_DATE_TO_DISPLAY + TEXT_TYPE +
            " );";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InspirationTable.TABLE_NAME + ";";

    public static final String GET_INSPIRATION = "SELECT " +
                                                 InspirationTable.COLUMN_NAME_ID + COMMA_SEP +
                                                 InspirationTable.COLUMN_NAME_AUTHOR +
                                                 " FROM " + InspirationTable.TABLE_NAME +
                                                 " WHERE " + InspirationTable.COLUMN_NAME_HIDDEN + " = 0 AND " +
                                                 InspirationTable.COLUMN_NAME_DATE_DISPLAYED + " < " +
                                                 String.valueOf(ELIGIBLE_INSPIRATION_DATE) + " ORDER BY RANDOM() LIMIT 1";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
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

    /**
     * Edits the contents of the database.
     *
     * @param db    The database to edit.
     * @param list  The list of parameters to either insert or update.
     */
    public void editDb(SQLiteDatabase db, ArrayList<Record> list)
    {
        try
        {
            db.beginTransaction();

            for (Record table : list)
            {
                if (table.getWhereClause() != null && table.getWhereClause().length() > 0)
                {
                    db.update(table.getTableName(), table.getContentValues(),
                              table.getWhereClause(), null);
                }
                else
                {
                    db.insert(table.getTableName(), null, table.getContentValues());
                }

            }

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}