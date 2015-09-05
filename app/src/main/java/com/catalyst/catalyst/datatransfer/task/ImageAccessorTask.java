package com.catalyst.catalyst.datatransfer.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.catalyst.catalyst.entity.CatalystBitmap;
import com.catalyst.catalyst.listener.ImageAccessorListener;
import com.catalyst.catalyst.util.Constant;

import java.io.InputStream;
import java.net.URL;

/**
 * Gets an image from a URL and creates a CatalystBitmap from it.
 *
 * Created by Nick Piscopio on 8/18/15.
 */
public class ImageAccessorTask extends AsyncTask<String, Void, CatalystBitmap>
{
    ImageAccessorListener imageAccessorListener;

    public ImageAccessorTask(ImageAccessorListener imageAccessorListener)
    {
        this.imageAccessorListener = imageAccessorListener;
    }

    /**
     * Creates a Bitmap of the image from the URL being sent in
     *
     * @param params	The URL String.
     */
    @Override
    protected CatalystBitmap doInBackground(String... params)
    {
        // //Create an Image from the link provided.
        Bitmap image = null;

        try
        {
            // Get the URL from the params
            InputStream inputStream = new URL(params[0]).openStream();
            image = BitmapFactory.decodeStream(inputStream);
        }
        catch(Exception exception)
        {
            Log.e(Constant.TAG, exception.toString());
        }

        return new CatalystBitmap(image);
    }

    /**
     * Called after the image has been retrieved from the URL.
     *
     * @param image	    The CatalystBitmap that is being set to the listener.
     */
    @Override
    protected void onPostExecute(CatalystBitmap image)
    {
        super.onPostExecute(image);

        imageAccessorListener.onImageRetrievedSuccessfully(image);
    }
}
