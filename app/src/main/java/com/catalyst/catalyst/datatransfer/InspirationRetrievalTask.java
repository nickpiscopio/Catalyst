package com.catalyst.catalyst.datatransfer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.catalyst.catalyst.entity.Record;
import com.catalyst.catalyst.helper.DbHelper;
import com.catalyst.catalyst.helper.InspirationTable;
import com.catalyst.catalyst.listener.TaskListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This async task updates inspirations frond the JSON file.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public class InspirationRetrievalTask extends AsyncTask<Void, Void, Void>
{
    private Context context;

    private TaskListener taskListener;

    private SharedPreferences prefs;

    private String inspirationId = "";
    private String author = "";

    public InspirationRetrievalTask(Context context, TaskListener taskListener)
    {
        this.context = context;
        this.taskListener = taskListener;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            getInspiration();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void param)
    {
       taskListener.onFinished(inspirationId, author);
    }

    /**
     * Gets an inspiration from teh database.
     *
     * @throws JSONException
     */
    private void getInspiration() throws JSONException
    {
        DbHelper dbHelper = new DbHelper(context);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(dbHelper.GET_INSPIRATION, null);

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            ArrayList<Record> records = new ArrayList<>();

            do
            {
                inspirationId = cursor.getString(cursor.getColumnIndex(InspirationTable.COLUMN_NAME_ID));
                author = cursor.getString(cursor.getColumnIndex(InspirationTable.COLUMN_NAME_AUTHOR));

                ContentValues values = new ContentValues();
                values.put(InspirationTable.COLUMN_NAME_DATE_DISPLAYED,
                           Calendar.getInstance().getTimeInMillis());

                Record record = new Record(InspirationTable.TABLE_NAME, values);
                record.setWhereClause(InspirationTable.COLUMN_NAME_ID + " = '" + inspirationId + "'");

                records.add(record);

            } while(cursor.moveToNext());

            cursor.close();

            dbHelper.editDb(dbHelper.getWritableDatabase(), records);
        }
    }
}