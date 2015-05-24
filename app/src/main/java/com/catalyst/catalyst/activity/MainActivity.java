package com.catalyst.catalyst.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.catalyst.catalyst.Model.Table;
import com.catalyst.catalyst.R;
import com.catalyst.catalyst.helper.DbHelper;
import com.catalyst.catalyst.helper.InspirationTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    public static String DEMO_FINISHED = "com.catalyst.catalyst.demo.finished";
    private static String JSON_FILE = "catalyst.json";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getSharedPreferences(DEMO_FINISHED, Context.MODE_PRIVATE);

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
        DbHelper dbHelper = new DbHelper(getApplicationContext());
        //move to async task
//        if(jsonVersion > database or SharedPreferences version)

        try
        {
            String inspirations = "inspirations";
            String id = "id";
            String category = "category";
            String author = "author";
            String dateToDisplay = "dateToDisplay";

            ArrayList<Table> inspirationArrayList = new ArrayList<Table>();
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONArray inspirationArray = obj.getJSONArray(inspirations);
            for (int i = 0; i < inspirationArray.length(); i++)
            {
                JSONObject inspirationObject = inspirationArray.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(InspirationTable.COLUMN_NAME_ID, inspirationObject.getString(id));
                values.put(InspirationTable.COLUMN_NAME_DATE_DISPLAYED, 0);
                values.put(InspirationTable.COLUMN_NAME_HIDDEN, 0);
                values.put(InspirationTable.COLUMN_NAME_CATEGORY, inspirationObject.getString(category));
                values.put(InspirationTable.COLUMN_NAME_AUTHOR, inspirationObject.getString(author));
                values.put(InspirationTable.COLUMN_NAME_DATE_TO_DISPLAY,
                           inspirationObject.has(dateToDisplay) ?
                                   inspirationObject.getString(dateToDisplay) : "");

                inspirationArrayList.add(new Table(InspirationTable.TABLE_NAME, values));
            }

            dbHelper.insert(dbHelper.getWritableDatabase(), inspirationArrayList);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset()
    {
        String json = null;
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