package com.catalyst.catalyst.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

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

    /**
     * Saves an image to the public picture directory in android.
     *
     * @param bitmap    The image to save.
     *
     * @param name      The name to save it. This will be saved as a PNG.
     *
     * @return  File that was saved.
     */
    public File saveImage(Bitmap bitmap, String name)
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + Constant.TAG);

        if(!dir.exists())
        {
            dir.mkdirs();
        }

        String fileName = name + ".png";

        File file = new File(dir, fileName);
        if (file.exists())
        {
            file.delete();
        }

        try
        {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return file;
    }
}