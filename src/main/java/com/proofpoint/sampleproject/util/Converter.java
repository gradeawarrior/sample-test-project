package com.proofpoint.sampleproject.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public abstract class Converter
{
    public static List<Object> convert(List<?> list)
    {
        ImmutableList.Builder<Object> result = new ImmutableList.Builder<Object>();
        for (Object item : list) {
            result.add(item);
        }
        return result.build();
    }
}
