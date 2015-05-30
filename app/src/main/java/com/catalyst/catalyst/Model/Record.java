package com.catalyst.catalyst.Model;

import android.content.ContentValues;

/**
 * Model for inserting a record into the database.
 *
 * Created by Nick Piscopio on 5/24/15.
 */
public class Record
{
    private String tableName;
    private String whereClause;
    private ContentValues contentValues;

    public Record(String tableName, ContentValues contentValues)
    {
        this.tableName = tableName;
        this.contentValues = contentValues;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setWhereClause(String whereClause)
    {
        this.whereClause = whereClause;
    }

    public String getWhereClause()
    {
        return whereClause;
    }

    public ContentValues getContentValues()
    {
        return contentValues;
    }
}