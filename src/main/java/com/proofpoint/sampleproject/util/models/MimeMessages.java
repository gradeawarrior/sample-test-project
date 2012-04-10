package com.proofpoint.sampleproject.util.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.proofpoint.log.Logger;
import com.proofpoint.sampleproject.util.Random;
import com.proofpoint.sampleproject.util.ResourceList;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.DataProvider;

import javax.mail.internet.InternetAddress;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class MimeMessages
{
    private static final Logger log = Logger.get(MimeMessages.class);

    public static final Builder GENERIC_RFC822_MIMEBUILDER = new MimeMessages.Builder()
            .setSender(Random.generateRandomEmail("example.com"))
            .addRecipient(Random.generateRandomEmail("example.com"))
            .setSubject("Hi there")
            .setBody("Hi Joe, see these files being shared with you.");

    public static final String GENERIC_RFC822_MIME = GENERIC_RFC822_MIMEBUILDER.build();

    public static final List<String> INVALID_RECIPIENTS = new ImmutableList.Builder<String>()
            .add("")
            .add("foobar")
            .add("@example.com")
            .add("foobar@@example.com")
            .build();

    @DataProvider(name = "rfc822-messages")
    public static Object[][] get()
    {
        Map<String, InputStream> files = getHamMessages();
        Object[][] objects = new Object[files.size()][1];

        int index = 0;
        for (Entry<String, InputStream> file : files.entrySet()) {
            objects[index++] = (new Object[]{file.getKey(), file.getValue()});
        }

        return objects;
    }

    @DataProvider(name = "rfc822-non-working-messages")
    public static Object[][] getNonWorking()
    {
        Map<String, InputStream> files = getNonWorkingMessages();
        Object[][] objects = new Object[files.size()][1];

        int index = 0;
        for (Entry<String, InputStream> file : files.entrySet()) {
            objects[index++] = (new Object[]{file.getKey(), file.getValue()});
        }

        return objects;
    }

    public static Map<String, InputStream> getHamMessages()
    {
        String filenames = ".*ham-non-spam.*";
        return ResourceList.getResources(Pattern.compile(filenames));
    }

    public static Map<String, InputStream> getNonWorkingMessages()
    {
        String filenames = ".*non-working.*";
        return ResourceList.getResources(Pattern.compile(filenames));
    }

    public static final class Builder
    {
        private Object sender;
        private List<Object> recipients = new ArrayList<Object>();
        private String subject = "";
        private String body = "";

        public Builder()
        {
        }

        public Builder setSender(String sender)
        {
            Preconditions.checkNotNull(sender, "sender must not be null");
            this.sender = sender;
            return this;
        }

        public Builder setSender(InternetAddress sender)
        {
            Preconditions.checkNotNull(sender, "sender must not be null");
            this.sender = sender;
            return this;
        }

        public Builder addRecipients(Collection<Object> recipients)
        {
            Preconditions.checkNotNull(recipients, "recipients must not be null");

            this.recipients = (List) recipients;
            return this;
        }

        public Builder addRecipient(String recipient)
        {
            Preconditions.checkNotNull(recipient, "recipient must not be null");
            recipients.add(recipient);
            return this;
        }

        public Builder addRecipient(InternetAddress recipient)
        {
            Preconditions.checkNotNull(recipient, "recipient must not be null");
            recipients.add(recipient);
            return this;
        }

        public Builder setSubject(String subject)
        {
            Preconditions.checkNotNull(subject, "subject must not be null");
            this.subject = subject;
            return this;
        }

        public Builder setBody(String body)
        {
            Preconditions.checkNotNull(body, "body must not be null");
            this.body = body;
            return this;
        }

        public String build()
        {
            Preconditions.checkNotNull(recipients, "recipients must not be null");
            Preconditions.checkNotNull(subject, "subject must not be null");
            Preconditions.checkNotNull(body, "body must not be null");

            // Headers
            StringBuffer buff = new StringBuffer();

            if (sender != null) {
                buff.append(String.format("From: %1$s\r\n", sender));
            }
            buff.append(String.format("To: %1$s\r\n", StringUtils.join(recipients, ",")));
            buff.append(String.format("Subject: %1$s\r\n", subject));
            buff.append("Content-Type: multipart/mixed; boundary=\"__ABCDEF\"");

            // Body
            buff.append("\r\n\r\n\r\n--__ABCDEF\r\n");
            buff.append("Content-Type: text/plain\r\n\r\n");

            buff.append(body);
            buff.append("\r\n\r\n\r\n--__ABCDEF--\r\n");

            log.debug(buff.toString());
            return buff.toString();
        }
    }
}
