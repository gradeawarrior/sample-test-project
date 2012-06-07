package com.proofpoint.sampleproject;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/*
* NOTE: All setters must fail if endTime has been set.
*
*/
public class TestStatus
{
    private final long startTime;
    private long endTime;
    private boolean succeeded = false;

    public TestStatus()
    {
        startTime = System.nanoTime();
    }

    public synchronized boolean finished(boolean succeeded)
    {
        if (endTime > 0) {
            return false;
        }
        endTime = System.nanoTime();
        this.succeeded = succeeded;
        return true;
    }

    public Map<String, Object> toWire()
    {
        ImmutableMap.Builder<String, Object> wireBuilder = ImmutableMap.builder();

        wireBuilder.put("startTime", startTime);
        wireBuilder.put("endTime", endTime);
        wireBuilder.put("succeeded", succeeded);

        return wireBuilder.build();
    }
}