package com.catalyst.catalyst.datatransfer;

import android.os.AsyncTask;
import android.util.Log;

import com.catalyst.catalyst.listener.ServiceListener;
import com.catalyst.catalyst.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This async task updates inspirations frond the JSON file.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public class JsonAccessor extends AsyncTask<String, Void, JSONObject>
{
    private ServiceListener serviceListener;

    private boolean success;

    public JsonAccessor(ServiceListener serviceListener)
    {
        this.serviceListener = serviceListener;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        String text = "";
        BufferedReader reader = null;

        try
        {
            // Defined URL  where to send data
            URL url = new URL(params[0]);

            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write( data );
//            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }

            text = sb.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }

            catch(Exception ex) {}
        }

        JSONObject jsonObject = null;

        //Parse the String to JSONObject
        try
        {
//            jsonObject = new JSONObject(text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1));

            jsonObject = new JSONObject(text);

            success = true;
        }
        catch(JSONException e)
        {
            Log.e(Constant.TAG, "Error parsing data " + e.toString());

            success = false;
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject json)
    {
        if (success)
        {
            serviceListener.onRetrievalSuccessfully(json);
        }
        else
        {
            serviceListener.onRetrievalFailed();
        }

    }
}