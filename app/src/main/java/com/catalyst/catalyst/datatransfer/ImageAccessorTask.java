package com.catalyst.catalyst.datatransfer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.catalyst.catalyst.listener.ImageAccessorListener;
import com.catalyst.catalyst.util.Constant;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by nickpiscopio on 8/18/15.
 */
public class ImageAccessorTask extends AsyncTask<String, Void, Bitmap>
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
    protected Bitmap doInBackground(String... params)
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

        return image;
    }

    /**
     * Called after the image has been retrieved from the URL.
     *
     * @param image	The image that is being set in the ImageView.
     */
    @Override
    protected void onPostExecute(Bitmap image)
    {
        super.onPostExecute(image);

        imageAccessorListener.onImageRetrieved(image);
    }
}
