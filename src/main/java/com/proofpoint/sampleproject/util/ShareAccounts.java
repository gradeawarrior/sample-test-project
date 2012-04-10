package com.proofpoint.sampleproject.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public abstract class ShareAccounts
{
    public static final String VALID_DOMAIN = "matlock.lab.proofpoint.com";

    // Templates
    public static final String SHARE_RECIPIENT_TEMPLATE = "sampleproject-recipient-%s@%s";

    public static final String DEFAULT_SHARE_NO_REPLY_EMAIL = "no-reply@proofpoint.com";
    public static final String DEFAULT_SHARE_SENDER = String.format("sampleproject-sender@%s", VALID_DOMAIN);

    // Email Templates
    public static final String DEFAULT_SHARE_RECEIVED_FILES_SUBJECT = "Received your files.";
    public static final String DEFAULT_SHARE_RECEIVED_FILES_CONTENT = "downloaded the files you sent";

    public static String generateValidRecipient()
    {
        return String.format(SHARE_RECIPIENT_TEMPLATE, Random.getRandomAlphaNumericString(5), VALID_DOMAIN);
    }

    public static List<String> generateValidRecipients(int size)
    {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<String>();
        for (int count = 0; count < size; count++) {
            builder.add(generateValidRecipient());
        }
        return builder.build();
    }
}
