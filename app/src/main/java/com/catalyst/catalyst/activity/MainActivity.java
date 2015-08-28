package com.catalyst.catalyst.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.catalyst.catalyst.util.Constant;
import com.catalyst.catalyst.util.ScreenshotUtil;

import java.io.ByteArrayOutputStream;

/**
 * Main view of Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class MainActivity extends AppCompatActivity implements TaskListener, ImageAccessorListener
{
    public static final String NEW_INSPIRATION = "new.inspiration";
    private final String INSPIRATION_ID = "com.catalyst.catalyst.inspiration.id";
    private final String INSPIRATION_AUTHOR = "com.catalyst.catalyst.inspiration.author";
    private final String IMAGE_ACCESSOR_STATE = "com.catalyst.catalyst.image.accessor.state";

    private SharedPreferences prefs;

    private Resources res;
    private Context context;

    private ImageView inspirationImage;

    private RelativeLayout layoutInspiration;

    private TextView inspiration;
    private TextView author;

    private Uri screenshotUri;

    private ImageAccessor.ImageAccessorState imageAccessorState;

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

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        if (imageAccessorState != null)
        {
            savedInstanceState.putInt(IMAGE_ACCESSOR_STATE, imageAccessorState.ordinal());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        int imageState = savedInstanceState.getInt(IMAGE_ACCESSOR_STATE, Constant.PROCESS_FAILED);

        if (imageState > Constant.PROCESS_FAILED)
        {
            switch (imageState)
            {
                case 0:
                    imageAccessorState = ImageAccessor.ImageAccessorState.PROCESSING;
                    break;
                case 1:
                    imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED;
                    break;
                default:
                    break;
            }
        }
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

                imageAccessorState = ImageAccessor.ImageAccessorState.PROCESSING;

                //Get a background image
                new ImageAccessor(this);
            }
            else
            {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(INSPIRATION_ID, result[0]);
                editor.putString(INSPIRATION_AUTHOR, result[1]);
                editor.apply();

                intent.putExtra(NEW_INSPIRATION, false);
            }
        }

        getStoredImage();

        String storedId = prefs.getString(INSPIRATION_ID, "");
        String storedAuthor = "—" + prefs.getString(INSPIRATION_AUTHOR, "");

        int id = storedId.length() > 0 ? res.getIdentifier(storedId, "string", getPackageName()) : R.string.positivity_conquer;

        inspiration.setText(id);
        author.setText(storedAuthor);
    }

    @Override
    public void onImageRetrieved(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constant.INSPIRATION_BACKGROUND_IMAGE,
                         Base64.encodeToString(b, Base64.DEFAULT));
        editor.apply();

        imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED;

        setBackgroundImage(image);
    }

    /**
     * Gets the stored image in the shared preferences.
     */
    private void getStoredImage()
    {
        String previouslyEncodedImage = prefs.getString(Constant.INSPIRATION_BACKGROUND_IMAGE, "");

        if (imageAccessorState == ImageAccessor.ImageAccessorState.FINISHED || !previouslyEncodedImage.equalsIgnoreCase(""))
        {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
            setBackgroundImage(image);
        }
    }

    /**
     * Sets that background image from a bitmap and scales it properly.
     *
     * @param image     The bitmap to use for the background.
     */
    private void setBackgroundImage(Bitmap image)
    {
        inspirationImage.setImageBitmap(image);
        inspirationImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
}