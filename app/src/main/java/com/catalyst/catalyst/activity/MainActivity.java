package com.catalyst.catalyst.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.alarm.CatalystAlarm;
import com.catalyst.catalyst.datatransfer.InspirationRetrievalTask;
import com.catalyst.catalyst.datatransfer.UpdateInspirationsTask;
import com.catalyst.catalyst.listener.TaskListener;
import com.catalyst.catalyst.util.Constant;

/**
 * Main view of Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class MainActivity extends AppCompatActivity implements TaskListener
{
    public static final String NEW_INSPIRATION = "new.inspiration";
    private static String INSPIRATION_ID = "com.catalyst.catalyst.inspiration.id";
    private static String INSPIRATION_AUTHOR = "com.catalyst.catalyst.inspiration.author";

    private SharedPreferences prefs;

    private Context context;

    private TextView inspiration;
    private TextView author;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        boolean isDemoFinished = prefs.getBoolean(Constant.DEMO_FINISHED, false);

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
        context = getApplicationContext();

        new UpdateInspirationsTask(context, this).execute();
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

    /**
     * Listener called when the DatabaseTask finishes processing.
     */
    @Override
    public void onFinished(String... result)
    {
        // Creates the alarm to start sending notifications to the user.
        CatalystAlarm.getInstance(context);

        inspiration = (TextView) findViewById(R.id.text_inspiration);
        author = (TextView) findViewById(R.id.text_author);

        //If get new inspiration
        if (getIntent().getBooleanExtra(NEW_INSPIRATION, false))
        {
            if (result.length == 0)
            {
                //Get an inspiration
                new InspirationRetrievalTask(context, this).execute();
            }
            else
            {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MainActivity.INSPIRATION_ID, result[0]);
                editor.putString(MainActivity.INSPIRATION_AUTHOR, result[1]);
                editor.commit();
            }
        }

        String storedId = prefs.getString(MainActivity.INSPIRATION_ID, "");
        String storedAuthor = "- " + prefs.getString(MainActivity.INSPIRATION_AUTHOR, "");

        int id = storedId.length() > 0 ? getResources().getIdentifier(storedId, "string", getPackageName()) : R.string.positivity_conquer;

        inspiration.setText(id);
        author.setText(storedAuthor);
    }
}