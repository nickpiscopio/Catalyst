package com.catalyst.catalyst.listener;

/**
 * Public interface to listen for when tasks finish.
 *
 * Created by Nick Piscopio on 6/6/15.
 */
public interface TaskListener
{
    void onFinished(String... result);
}