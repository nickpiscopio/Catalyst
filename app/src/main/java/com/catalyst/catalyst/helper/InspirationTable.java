package com.catalyst.catalyst.helper;

import android.provider.BaseColumns;

/**
 * Inspiration table in the database.
 *
 * Created by Nick Piscopio on 5/24/15.
 */
public class InspirationTable implements BaseColumns
{
    public static final String TABLE_NAME = "Inspiration";

    public static final String COLUMN_NAME_ID = "InspirationId";
    public static final String COLUMN_NAME_IMAGE_ID = "ImageId";
    public static final String COLUMN_NAME_DATE_DISPLAYED = "DateDisplayed";
    public static final String COLUMN_NAME_HIDDEN = "Hidden";
    public static final String COLUMN_NAME_AUTHOR = "Author";
    public static final String COLUMN_NAME_CATEGORY = "Category";
    public static final String COLUMN_NAME_DATE_TO_DISPLAY = "DateToDisplay";
}
