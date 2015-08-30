package com.catalyst.catalyst.listener;

import java.io.File;

/**
 * Public interface to listen for when the share finishes processing the image.
 *
 * Created by Nick Piscopio on 8/30/15.
 */
public interface ShareListener
{
    void onImageProcessed(File file);
}