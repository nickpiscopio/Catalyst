package com.catalyst.catalyst.datatransfer;

import android.graphics.Bitmap;
import android.util.Log;

import com.catalyst.catalyst.listener.ImageAccessorListener;
import com.catalyst.catalyst.listener.ServiceListener;
import com.catalyst.catalyst.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Parses the response from the API Endpoint.
 *
 * Created by Nick Piscopio on 8/18/15.
 */
public class ImageAccessor implements ServiceListener, ImageAccessorListener
{
    private ImageAccessorListener imageAccessorListener;

    private static final String API_ENDPOINT = "https://pixabay.com/api/?username=thyleft&key=82883695662c8ce96614&safesearch=true&editors_choice=true&response_group=high_resolution&image_type=photo&per_page=200";

    public enum ImageAccessorState
    {
        PROCESSING,
        FINISHED
    }

    public ImageAccessor(ImageAccessorListener imageAccessorListener)
    {
        this.imageAccessorListener = imageAccessorListener;

        callApiEndpoint();
    }

    @Override
    public void onRetrievalSuccessfully(JSONObject json)
    {
        try
        {
            JSONArray jsonArray = json.getJSONArray("hits");

            int jsonLength = jsonArray.length();
            int randomImage = new Random().nextInt(jsonLength);

            JSONObject childJSONObject = jsonArray.getJSONObject(randomImage);
            String link = childJSONObject.getString("fullHDURL");

            new ImageAccessorTask(this).execute(link);
        }
        catch(JSONException exception)
        {
            Log.e(Constant.TAG, "Could not find link: " + exception.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        callApiEndpoint();
    }

    @Override
    public void onImageRetrieved(Bitmap image)
    {
        imageAccessorListener.onImageRetrieved(image);
    }

    /**
     * Calls the API endpoint to get new images.
     */
    private void callApiEndpoint()
    {
        new JsonAccessor(this).execute(API_ENDPOINT);
    }
}
