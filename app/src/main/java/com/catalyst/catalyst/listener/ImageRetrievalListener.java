package com.catalyst.catalyst.listener;

import android.graphics.Bitmap;

/**
 * Public interface to listen for when an image is retrieved.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public interface ImageRetrievalListener
{
    void onImageRetrieved(Bitmap image);
}