package com.catalyst.catalyst.listener;

import com.catalyst.catalyst.entity.CatalystBitmap;

/**
 * Public interface to listen for when an image is retrieved.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public interface ImageAccessorListener
{
    void onImageRetrieved(CatalystBitmap catalystBitmap);
}