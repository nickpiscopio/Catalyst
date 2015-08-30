package com.catalyst.catalyst.entity;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Model for a bitmap.
 *
 * Created by Nick Piscopio on 8/30/15.
 */
public class CatalystBitmap
{
    private Bitmap bitmap;

    private byte[] encodedBitmap;

    private String author;

    public CatalystBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        encodedBitmap = baos.toByteArray();
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public byte[] getEncodedBitmap()
    {
        return encodedBitmap;
    }

    public void setEncodedBitmap(byte[] encodedBitmap)
    {
        this.encodedBitmap = encodedBitmap;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }
}