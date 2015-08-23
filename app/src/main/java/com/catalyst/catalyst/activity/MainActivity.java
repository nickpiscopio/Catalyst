package com.catalyst.catalyst.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.alarm.CatalystAlarm;
import com.catalyst.catalyst.datatransfer.ImageAccessor;
import com.catalyst.catalyst.datatransfer.InspirationRetrievalTask;
import com.catalyst.catalyst.datatransfer.UpdateInspirationsTask;
import com.catalyst.catalyst.listener.ImageAccessorListener;
import com.catalyst.catalyst.listener.TaskListener;
import com.catalyst.catalyst.util.ColorUtil;
import com.catalyst.catalyst.util.Constant;
import com.catalyst.catalyst.util.ScreenshotUtil;

/**
 * Main view of Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class MainActivity extends AppCompatActivity implements TaskListener, ImageAccessorListener
{
    public static final String NEW_INSPIRATION = "new.inspiration";
    private static final String INSPIRATION_ID = "com.catalyst.catalyst.inspiration.id";
    private static final String INSPIRATION_AUTHOR = "com.catalyst.catalyst.inspiration.author";

    private SharedPreferences prefs;

    private Resources res;
    private Context context;

    private ImageView inspirationImage;

    private RelativeLayout layoutInspiration;

    private TextView inspiration;
    private TextView author;

    private Uri screenshotUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        res = getResources();

        boolean isDemoFinished = prefs.getBoolean(Constant.DEMO_FINISHED, false);

        context = getApplicationContext();

        inspirationImage = (ImageView) findViewById(R.id.image_inspiration);

        layoutInspiration = (RelativeLayout) findViewById(R.id.layout_inspiration);

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
        inspiration = (TextView) findViewById(R.id.text_inspiration);
        author = (TextView) findViewById(R.id.text_author);

        setActivityColor(false);

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

        int result = 0;

        switch (item.getItemId())
        {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.action_share:
                ScreenshotUtil ssUtil = new ScreenshotUtil();
                Bitmap screenshot = ssUtil.takeScreenShot(layoutInspiration);

                String path = MediaStore.Images.Media
                        .insertImage(getContentResolver(), screenshot, "title", null);
                screenshotUri = Uri.parse(path);

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);

                result = Constant.ACTIVITY_RESULT_SHARE_INSPIRATION;
                break;
            default:
                break;
        }

        if (intent != null)
        {
            startActivityForResult(intent, result);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.ACTIVITY_RESULT_SHARE_INSPIRATION)
        {
            // Delete the image from the device after sharing.
            getContentResolver().delete(screenshotUri, null, null);
        }
    }

    /**
     * Listener called when the DatabaseTask finishes processing.
     */
    @Override
    public void onFinished(String... result)
    {
        // Creates the alarm to start sending notifications to the user.
        CatalystAlarm.getInstance(context);

        Intent intent = getIntent();

        //If get new inspiration
        if (intent.getBooleanExtra(NEW_INSPIRATION, false))
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
                editor.putInt(Constant.INSPIRATION_COLOR, ColorUtil.getRandomNumber(Constant.INSPIRATION_COLOR_MIN, Constant.INSPIRATION_COLOR_MAX));
                editor.apply();

                setActivityColor(true);

                intent.putExtra(NEW_INSPIRATION, false);
            }
        }

        String storedId = prefs.getString(MainActivity.INSPIRATION_ID, "");
        String storedAuthor = "—" + prefs.getString(MainActivity.INSPIRATION_AUTHOR, "");

        int id = storedId.length() > 0 ? res.getIdentifier(storedId, "string", getPackageName()) : R.string.positivity_conquer;

        inspiration.setText(id);
        author.setText(storedAuthor);
    }

    /**
     * Sets the background color of the activity.
     *
     * @param transition    Boolean value of whether to use a transition or not.
     */
    private void setActivityColor(boolean transition)
    {
        new ImageAccessor(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

//        View actionBarBackground = findViewById(R.id.view_action_bar_background);

        int storedColorResource = ColorUtil.getStoredColor(context);

        if (transition)
        {
            int fadeDuration = 1700;
            int color = Color.TRANSPARENT;
//            Drawable background = actionBarBackground.getBackground();
//            if (background instanceof ColorDrawable)
//            {
//                color = ((ColorDrawable)background).getColor();
//            }
//
//            ObjectAnimator colorFade = ObjectAnimator.ofObject(actionBarBackground, "backgroundColor", new ArgbEvaluator(),
//                                                               color, storedColorResource);

            ObjectAnimator colorFade2 = ObjectAnimator.ofObject(layoutInspiration, "backgroundColor", new ArgbEvaluator(),
                                                               color, storedColorResource);
//            colorFade.setDuration(fadeDuration);
            colorFade2.setDuration(fadeDuration);

//            colorFade.start();
            colorFade2.start();
        }
        else
        {
//            actionBarBackground.setBackgroundColor(storedColorResource);
            layoutInspiration.setBackgroundColor(storedColorResource);
        }
    }

    @Override
    public void onImageRetrieved(Bitmap image)
    {
//        BitmapDrawable backgroundDrawable = new BitmapDrawable(getResources(), image);

        inspirationImage.setImageBitmap(image);
        inspirationImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //        layoutInspiration.setBackground(backgroundDrawable);
//        backgroundDrawable.setGravity(Gravity.FILL | Gravity.CLIP_HORIZONTAL);

    }
}