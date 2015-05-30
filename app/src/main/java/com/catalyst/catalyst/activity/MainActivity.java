package com.catalyst.catalyst.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.catalyst.catalyst.Model.Record;
import com.catalyst.catalyst.R;
import com.catalyst.catalyst.helper.DbHelper;
import com.catalyst.catalyst.helper.InspirationTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main view of Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class MainActivity extends AppCompatActivity
{
    public static String SHARED_PREFERENCES = "catalyst.shared.preferences";
    public static String DEMO_FINISHED = "com.catalyst.catalyst.demo.finished";
    public static String JSON_VERSION = "com.catalyst.catalyst.json.version";

    private static String JSON_FILE = "catalyst.json";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        boolean isDemoFinished = prefs.getBoolean(DEMO_FINISHED, false);

        if (!isDemoFinished)
        {
            showDemo();
        }
        else
        {
            runCatalyst();
        }
    }

    /**
     * Shows the demo screens.
     */
    private void showDemo()
    {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Runs the normal functionality of Catalyst.
     */
    private void runCatalyst()
    {
        //move to async task
        try
        {
            String jsonVersionString = "version";

            JSONObject obj = new JSONObject(loadJSONFromAsset());

            int jsonVersion = prefs.getInt(JSON_VERSION, 0);
            int jsonFileVersion = Integer.valueOf(obj.getString(jsonVersionString));

            if (jsonFileVersion > jsonVersion)
            {
                reviewJson(obj);
            }

            prefs.edit().putInt(JSON_VERSION, jsonFileVersion).commit();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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

        DbHelper dbHelper = new DbHelper(getApplicationContext());

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //get id where id = all ids from inspirationIds
        String query = "SELECT " + InspirationTable.COLUMN_NAME_ID +
                       " FROM " + InspirationTable.TABLE_NAME +
                       " WHERE " + getClauseId(inspirationIds.toArray(new String[inspirationIds.size()]));
        database.rawQuery(query, null);

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

            InputStream is = getAssets().open(JSON_FILE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = null;

        switch (item.getItemId())
        {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            default:
                break;
        }

        if (intent != null)
        {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}