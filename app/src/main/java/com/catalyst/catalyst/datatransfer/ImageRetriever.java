package com.catalyst.catalyst.datatransfer;

import android.graphics.Bitmap;
import android.util.Log;

import com.catalyst.catalyst.listener.ImageRetrievalListener;
import com.catalyst.catalyst.listener.ServiceListener;
import com.catalyst.catalyst.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by nickpiscopio on 8/18/15.
 */
public class ImageRetriever implements ServiceListener, ImageRetrievalListener
{
    ImageRetrievalListener imageRetrievalListener;

    public ImageRetriever(ImageRetrievalListener imageRetrievalListener)
    {
        this.imageRetrievalListener = imageRetrievalListener;

        new FlickrTask(this).execute("https://api.flickr.com/services/feeds/photos_public.gne?tags=scenery&format=json");

//        new FlickrTask(this).execute("http://www.splashbase.co/api/v1/images/random");
    }

    @Override
    public void onJSONRetreived(JSONObject json)
    {
        Log.i(Constant.TAG, "Found image");

        try
        {
            JSONArray jsonArray = json.getJSONArray("items");

            int jsonLength = jsonArray.length();
            int randomImage = new Random().nextInt(jsonLength + 1);

            JSONObject childJSONObject = jsonArray.getJSONObject(randomImage);
            JSONObject mediaObject = childJSONObject.getJSONObject("media");

            String link = mediaObject.getString("m").replace("_m", "_b");


//            String link = json.getString("url");

            new ImageAccessor(this).execute(link);
        }
        catch(JSONException exception)
        {
            Log.e(Constant.TAG, "Could not find link: " + exception.toString());
        }
    }

    @Override
    public void onImageRetrieved(Bitmap image)
    {
        imageRetrievalListener.onImageRetrieved(image);
    }
}
