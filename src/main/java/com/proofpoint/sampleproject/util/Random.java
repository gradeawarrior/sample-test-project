package com.proofpoint.sampleproject.util;

import java.util.Date;

public class Random
{
    private static java.util.Random rand = new java.util.Random((new Date()).getTime());

    public static byte[] getRandomByteArray(int length)
    {
        byte[] byte_array = new byte[length];
        rand.nextBytes(byte_array);
        return byte_array;
    }

    public static int nextInt(int ceiling)
    {
        return rand.nextInt(ceiling);
    }
}
