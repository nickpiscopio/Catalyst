package com.catalyst.catalyst.Model;

import android.content.ContentValues;

/**
 * Created by nickpiscopio on 5/24/15.
 */
public class Table
{
    private String tableName;

    private ContentValues contentValues;

    public Table(String tableName, ContentValues contentValues)
    {
        this.tableName = tableName;
        this.contentValues = contentValues;
    }

    public String getTableName()
    {
        return tableName;
    }

    public ContentValues getContentValues()
    {
        return contentValues;
    }
}