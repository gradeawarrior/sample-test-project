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

    public static String getRandomAlphaNumericString(int length)
    {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rand.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String generateRandomEmail(String domain)
    {
        return String.format("a%s-%s@%s", getRandomAlphaNumericString(5), getRandomAlphaNumericString(5), domain);
    }
}
