package com.catalyst.catalyst.listener;

import org.json.JSONObject;

/**
 * Public interface to listen for when tasks finish.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public interface ServiceListener
{
    void onJSONRetreived(JSONObject json);
}