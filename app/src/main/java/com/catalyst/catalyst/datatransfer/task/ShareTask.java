package com.catalyst.catalyst.datatransfer.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.RelativeLayout;

import com.catalyst.catalyst.listener.ShareListener;
import com.catalyst.catalyst.util.ScreenshotUtil;

import java.io.File;

/**
 * This async task to share an inspiration.
 *
 * Created by Nick Piscopio on 8/30/15.
 */
public class ShareTask extends AsyncTask<RelativeLayout, Void, File>
{
    private ShareListener shareListener;

    public ShareTask(ShareListener shareListener)
    {
        this.shareListener = shareListener;
    }

    @Override
    protected File doInBackground(RelativeLayout... params)
    {
        ScreenshotUtil ssUtil = new ScreenshotUtil();

        Bitmap screenshot = ssUtil.takeScreenShot(params[0]);

        return ssUtil.saveImage(screenshot, "catalyst_inspiration");
    }

    @Override
    protected void onPostExecute(File file)
    {
       shareListener.onImageProcessed(file);
    }
}