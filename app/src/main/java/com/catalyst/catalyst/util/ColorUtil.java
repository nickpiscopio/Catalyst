package com.catalyst.catalyst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.catalyst.catalyst.R;

import java.util.Random;

/**
 * Utility class for colors.
 *
 * Created by Nick Piscopio on 6/21/15.
 */
public class ColorUtil
{
    /**
     * Generates a random number.
     *
     * @param min   The minimum number to generate.
     * @param max   The maximum number to generate.
     *
     * @return  The generated number.
     */
    public static int getRandomNumber(int min, int max)
    {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static int getStoredColor(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constant.SHARED_PREFERENCES,
                                                               Context.MODE_PRIVATE);

        int storedColorResource;

        //Get the stored color from the shared preferences.
        switch (prefs.getInt(Constant.INSPIRATION_COLOR, Constant.INSPIRATION_COLOR_MIN))
        {
            case 1:
                storedColorResource = context.getResources().getColor(R.color.green);
                break;
            case 2:
                storedColorResource = context.getResources().getColor(R.color.light_blue);
                break;
            case 3:
                storedColorResource = context.getResources().getColor(R.color.purple);
                break;
            case 0:
            default:
                storedColorResource = context.getResources().getColor(R.color.dark_blue);
                break;
        }

        return storedColorResource;
    }
}