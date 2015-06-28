package com.catalyst.catalyst.datatransfer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.catalyst.catalyst.entity.Record;
import com.catalyst.catalyst.helper.DbHelper;
import com.catalyst.catalyst.helper.InspirationTable;
import com.catalyst.catalyst.listener.TaskListener;
import com.catalyst.catalyst.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This async task updates inspirations frond the JSON file.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public class UpdateInspirationsTask extends AsyncTask<Void, Void, Void>
{
    private static String JSON_VERSION = "com.catalyst.catalyst.json.version";
    private static String JSON_FILE = "catalyst.json";

    private Context context;

    private TaskListener taskListener;

    private SharedPreferences prefs;

    public UpdateInspirationsTask(Context context, TaskListener taskListener)
    {
        this.context = context;
        this.taskListener = taskListener;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            String jsonVersionString = "version";

            JSONObject obj = new JSONObject(loadJSONFromAsset());

            prefs = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

            int jsonVersion = prefs.getInt(JSON_VERSION, 0);
            int jsonFileVersion = Integer.valueOf(obj.getString(jsonVersionString));

            if (jsonFileVersion > jsonVersion)
            {
                reviewJson(obj);
            }

            prefs.edit().putInt(JSON_VERSION, jsonFileVersion).apply();
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
       taskListener.onFinished();
    }

    /**
     * Reviews the JSON Object for new entries and updates the database accordingly.
     *
     * @param obj   The JSONObject to read.
     *
     * @throws JSONException
     */
    private void reviewJson(JSONObject obj) throws JSONException
    {
        String inspirations = "inspirations";
        String id = "id";
        String category = "category";
        String author = "author";
        String dateToDisplay = "dateToDisplay";

        HashMap<String, Record> inspirationArrayList = new HashMap<>();
        ArrayList<String> inspirationIds = new ArrayList<>();

        JSONArray inspirationArray = obj.getJSONArray(inspirations);
        for (int i = 0; i < inspirationArray.length(); i++)
        {
            JSONObject inspirationObject = inspirationArray.getJSONObject(i);

            String inspirationId = inspirationObject.getString(id);
            inspirationIds.add(inspirationId);

            ContentValues values = new ContentValues();
            values.put(InspirationTable.COLUMN_NAME_ID, inspirationId);
            values.put(InspirationTable.COLUMN_NAME_DATE_DISPLAYED, 0);
            values.put(InspirationTable.COLUMN_NAME_HIDDEN, 0);
            values.put(InspirationTable.COLUMN_NAME_CATEGORY, inspirationObject.getString(category));
            values.put(InspirationTable.COLUMN_NAME_AUTHOR, inspirationObject.getString(author));
            values.put(InspirationTable.COLUMN_NAME_DATE_TO_DISPLAY,
                       inspirationObject.has(dateToDisplay) ?
                               inspirationObject.getString(dateToDisplay) : "");

            inspirationArrayList.put(inspirationId, new Record(InspirationTable.TABLE_NAME, values));
        }

        DbHelper dbHelper = new DbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //get id where id = all ids from inspirationIds
        String query = "SELECT " + InspirationTable.COLUMN_NAME_ID +
                       " FROM " + InspirationTable.TABLE_NAME +
                       " WHERE " + getClauseId(inspirationIds.toArray(new String[inspirationIds.size()]));

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do
            {
                String cursorId = cursor.getString(cursor.getColumnIndex(InspirationTable.COLUMN_NAME_ID));

                Record record = inspirationArrayList.get(cursorId);

                ContentValues cv = record.getContentValues();
                cv.remove(InspirationTable.COLUMN_NAME_ID);
                cv.remove(InspirationTable.COLUMN_NAME_DATE_DISPLAYED);
                cv.remove(InspirationTable.COLUMN_NAME_HIDDEN);

                record.setWhereClause(InspirationTable.COLUMN_NAME_ID + " = '" + cursorId + "'");

                inspirationArrayList.remove(cursorId);
                inspirationArrayList.put(cursorId, record);

            } while(cursor.moveToNext());

            cursor.close();
        }

        dbHelper.editDb(dbHelper.getWritableDatabase(), new ArrayList<>(inspirationArrayList.values()));
    }

    /**
     * Loads the JSON file from the asset folder.
     *
     * @return The JSON String.
     */
    private String loadJSONFromAsset()
    {
        String json;

        try {

            InputStream is = context.getAssets().open(JSON_FILE);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    /**
     * Gets the where clause.
     *
     * @param ids  ID of record to return
     * @return The where clause
     */
    private String getClauseId(String[] ids) {

        int length = ids.length;

        String where = "";

        for (int i = 0; i < length; i++)
        {
            if (i == 0)
            {
                where += InspirationTable.TABLE_NAME + "." + InspirationTable.COLUMN_NAME_ID + " IN (";
            }

            String segment = "'" + ids[i] + "'";

            where += i == length - 1 ? segment + ")" : segment + ", ";
        }

        return where;
    }
}