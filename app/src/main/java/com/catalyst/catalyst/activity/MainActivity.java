package com.catalyst.catalyst.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.alarm.CatalystAlarm;
import com.catalyst.catalyst.alarm.CatalystNotification;
import com.catalyst.catalyst.datatransfer.ImageAccessor;
import com.catalyst.catalyst.datatransfer.task.InspirationRetrievalTask;
import com.catalyst.catalyst.datatransfer.task.ShareTask;
import com.catalyst.catalyst.datatransfer.task.UpdateInspirationsTask;
import com.catalyst.catalyst.entity.CatalystBitmap;
import com.catalyst.catalyst.listener.ImageAccessorListener;
import com.catalyst.catalyst.listener.ShareListener;
import com.catalyst.catalyst.listener.TaskListener;
import com.catalyst.catalyst.util.Constant;

import java.io.File;

/**
 * Main view of Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class MainActivity extends AppCompatActivity implements TaskListener, ImageAccessorListener,
                                                               ShareListener
{
    private final String INSPIRATION_ID = "com.catalyst.catalyst.inspiration.id";
    private final String INSPIRATION_AUTHOR = "com.catalyst.catalyst.inspiration.author";

    private SharedPreferences prefs;

    private Resources res;
    private Context context;

    private ImageView inspirationImage;

    private RelativeLayout layoutInspiration;

    private RelativeLayout layoutInspirationText;

    private TextView inspiration;
    private TextView author;
    private TextView imageAuthor;

    private ImageAccessor.ImageAccessorState imageAccessorState;

    private ProgressDialog progressDialog;

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
        layoutInspiration = (RelativeLayout) findViewById(R.id.layout_inspiration);
        layoutInspirationText = (RelativeLayout) findViewById(R.id.layout_inspiration_text);

        inspirationImage = (ImageView) findViewById(R.id.image_inspiration);

        inspiration = (TextView) findViewById(R.id.text_inspiration);
        author = (TextView) findViewById(R.id.text_author);
        imageAuthor = (TextView) findViewById(R.id.text_image_author);

        getImageAccessorState();

        setLoading(true);

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

        switch (item.getItemId())
        {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.action_share:
                showProgressDialog(res.getString(R.string.share_processing));
                new ShareTask(this).execute(layoutInspiration);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        if (imageAccessorState != null)
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constant.INSPIRATION_BACKGROUND_IMAGE_STATE, imageAccessorState.ordinal());
            editor.apply();
        }
    }

    /**
     * Loads the imageAccessorState from the shared preferences.
     */
    private void getImageAccessorState()
    {
        int imageState = prefs.getInt(Constant.INSPIRATION_BACKGROUND_IMAGE_STATE,
                                         Constant.PROCESS_FAILED);

        if (imageState > Constant.PROCESS_FAILED)
        {
            switch (imageState)
            {
                case 0:
                    imageAccessorState = ImageAccessor.ImageAccessorState.PROCESSING;
                    break;
                case 1:
                    imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED_SUCCESSFULLY;
                    break;
                case 2:
                    imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED_UNSUCCESSFULLY;
                    break;
                default:
                    break;
            }
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

        // If get new inspiration
        if (prefs.getBoolean(Constant.NEW_INSPIRATION, false))
        {
            // Cancels the notification if the user opens up the app without clicking on the notification.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(CatalystNotification.NOTIFICATION_ID);

            SharedPreferences.Editor editor = prefs.edit();

            if (result.length == 0)
            {
                editor.putString(Constant.INSPIRATION_BACKGROUND_IMAGE, "");
                editor.putString(Constant.INSPIRATION_BACKGROUND_IMAGE_AUTHOR, "");
                editor.apply();

                //Get an inspiration
                new InspirationRetrievalTask(context, this).execute();

                imageAccessorState = ImageAccessor.ImageAccessorState.PROCESSING;

                //Get a background image
                new ImageAccessor(this);
            }
            else
            {
                editor.putString(INSPIRATION_ID, result[0]);
                editor.putString(INSPIRATION_AUTHOR, result[1]);
                editor.apply();
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
    public void onImageRetrievedSuccessfully(CatalystBitmap catalystBitmap)
    {
        String author = catalystBitmap.getAuthor();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constant.INSPIRATION_BACKGROUND_IMAGE, Base64.encodeToString(catalystBitmap.getEncodedBitmap(), Base64.DEFAULT));
        editor.putString(Constant.INSPIRATION_BACKGROUND_IMAGE_AUTHOR, author);
        editor.putBoolean(Constant.NEW_INSPIRATION, false);
        editor.apply();

        imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED_SUCCESSFULLY;

        setBackgroundImage(catalystBitmap.getBitmap(), author);
    }

    @Override
    public void onImageRetrievalFailure()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constant.NEW_INSPIRATION, false);
        editor.apply();

        imageAccessorState = ImageAccessor.ImageAccessorState.FINISHED_UNSUCCESSFULLY;

        setImageFailureMessage();
    }

    @Override
    public void onImageProcessed(File path)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(Constant.IMAGE_TYPE);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(path));

        startActivity(intent);

        progressDialog.dismiss();
    }

    /**
     * Gets the stored image in the shared preferences.
     */
    private void getStoredImage()
    {
        String encodedImage = prefs.getString(Constant.INSPIRATION_BACKGROUND_IMAGE, "");
        String imageAuthor = prefs.getString(Constant.INSPIRATION_BACKGROUND_IMAGE_AUTHOR, "");

        if (imageAccessorState == ImageAccessor.ImageAccessorState.FINISHED_SUCCESSFULLY || !encodedImage.equalsIgnoreCase(""))
        {
            byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
            setBackgroundImage(image, imageAuthor);
        }
        else if (imageAccessorState == ImageAccessor.ImageAccessorState.FINISHED_UNSUCCESSFULLY)
        {
            setImageFailureMessage();
        }
        else
        {
            setLoading(true);
        }
    }

    /**
     * Sets that background image from a bitmap and scales it properly.
     *
     * @param image     The bitmap to use for the background.
     */
    private void setBackgroundImage(Bitmap image, String author)
    {
        inspirationImage.setImageBitmap(image);
        inspirationImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageAuthor.setText(
                author.length() > 0 ? res.getString(R.string.image_description, author) :
                        res.getString(R.string.image_description_no_author));

        setLoading(false);
    }

    /**
     * Displays a progress dialog to the user.
     *
     * @param message   The message to display.
     */
    private void showProgressDialog(String message)
    {
        progressDialog = ProgressDialog.show(this, null, message, false);
    }

    /**
     * Shows a message to the user that the image retrieval failed.
     */
    private void setImageFailureMessage()
    {
        imageAuthor.setText(getResources().getString(R.string.failure_description));

        setLoading(false);
    }

    /**
     * Sets the UI according to whether it is loading or not.
     *
     * @param isLoading     Boolean value of which layout to show.
     */
    private void setLoading(boolean isLoading)
    {
        if (isLoading)
        {
            layoutInspirationText.setVisibility(View.GONE);

            if (progressDialog == null)
            {
                showProgressDialog(res.getString(R.string.inspiration_loading));
            }
        }
        else
        {
            layoutInspirationText.setVisibility(View.VISIBLE);

            if (progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
}