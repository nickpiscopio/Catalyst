package com.catalyst.catalyst.util;

import android.graphics.Bitmap;
import android.view.View;

/**
 *  Utility class for screenshots.
 *
 * Created by Nick Piscopio on 6/26/15.
 */
public class ScreenshotUtil
{
    /**
     * Takes a screenshot of a specified view.
     *
     * @param view  The view to take a screenshot.
     *
     * @return  A bitmap of the view.
     */
    public Bitmap takeScreenShot(View view)
    {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap b1 = view.getDrawingCache();

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();

        return b;
    }
}