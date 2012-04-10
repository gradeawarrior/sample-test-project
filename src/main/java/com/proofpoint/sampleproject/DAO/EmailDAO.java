package com.proofpoint.sampleproject.DAO;

import com.google.common.collect.ImmutableMap;
import com.proofpoint.log.Logger;
import com.sun.mail.imap.protocol.FLAGS;
import org.apache.commons.lang.StringUtils;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EmailDAO
{
    private static final Logger log = Logger.get(EmailDAO.class);
    private static final Map<Boolean, String> SSL_CONNECTION = new ImmutableMap.Builder<Boolean, String>()
            .put(true, "imaps")
            .put(false, "imap")
            .build();

    // Libraries
    private Store store;
    private Folder folder;
    private Message message;
    private final Session session;
    Properties props = System.getProperties();

    // Connnection Properties
    private final String host;
    private final String user;
    private final String password;
    private final Boolean sslEnabled;

    private Boolean debug = false;                  // used for debugging IMAP connection
    private Boolean match = true;                   // used for search

    private String folderName = "INBOX";            // Default is inbox
    private Integer connectionTimeout = 20000;      // Default is 20 Seconds
    private Integer searchTimeout = 60000;          // Default is 60 Seconds

    // Search Builder
    private String sender;
    private String subject;
    private List<String> recipients = new ArrayList<String>();
    private List<String> body = new ArrayList<String>();
    private Date date;

    public EmailDAO(String host, Integer port, Boolean sslEnabled, String user, String password)
    {
        this.host = host;
        this.user = user;
        this.password = password;
        this.sslEnabled = sslEnabled;

        setProperties(port, connectionTimeout);
        session = Session.getDefaultInstance(props);
    }

    public EmailDAO(String host, String user, String password)
    {
        this.host = host;
        this.user = user;
        this.password = password;
        this.sslEnabled = false;

        setProperties(143, connectionTimeout);
        session = Session.getDefaultInstance(props);
    }

    protected void setProperties(Integer port, Integer connectionTimeout)
    {
        // Disable TLS
        props.setProperty("mail.imap.starttls.enable", "false");

        // Use SSL
//        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.setProperty("mail.imap.socketFactory.fallback", "false");

//        props.setProperty("mail.store.protocol", "imap");

        // Update Port
        props.setProperty("mail.imap.port", port.toString());
        props.setProperty("mail.imap.socketFactory.port", port.toString());

        // Set Timeout
        props.setProperty("mail.imap.connectiontimeout", connectionTimeout.toString());
        props.setProperty("mail.imap.timeout", connectionTimeout.toString());

        // Enable Debug
        props.setProperty("mail.debug", debug.toString());
    }

    public EmailDAO setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public EmailDAO connect()
            throws MessagingException
    {
        if (store == null || !store.isConnected()) {
            log.debug(String.format("Connecting to IMAP\n\t- host:%s\n\t- port:%s\n\t- user:%s\n\t- password:%s\n\t- timeout:%s",
                    host,
                    props.get("mail.imap.port"),
                    user,
                    password,
                    connectionTimeout));

            // Connect to the server
            session.setDebug(debug);
            store = session.getStore(SSL_CONNECTION.get(sslEnabled));
            store.connect(host, user, password);
            log.info("Successfully connected to IMAP");
        }
        openFolder(folderName);

        return this;
    }

    public EmailDAO disconnect()
            throws MessagingException
    {
        closeFolder();
        if (store != null || store.isConnected()) {
            log.debug("Closing IMAP connection");
            store.close();
        }
        else {
            log.warn("Store is not connected");
        }
        return this;
    }

    protected EmailDAO openFolder(String folderName)
            throws MessagingException
    {
        if (folder == null || !folder.isOpen()) {
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            log.debug("Successfully Opened Folder: " + folderName);
        }
        return this;
    }

    protected EmailDAO closeFolder()
            throws MessagingException
    {
        if (folder != null && folder.isOpen()) {
            folder.close(true);
        }
        return this;
    }

    public EmailDAO resetSearch()
    {
        this.sender = null;
        this.subject = null;
        this.recipients = new ArrayList<String>();
        this.body = new ArrayList<String>();
        this.date = null;
        return this;
    }

    public EmailDAO setSearchTimeout(Integer searchTimeout)
    {
        this.searchTimeout = searchTimeout;
        return this;
    }

    public EmailDAO addSender(String sender)
    {
        this.sender = sender;
        return this;
    }

    public EmailDAO addSubject(String subject)
    {
        this.subject = subject;
        return this;
    }

    public EmailDAO addRecipient(String recipient)
    {
        recipients.add(recipient);
        return this;
    }

    public EmailDAO addRecipients(List<String> recipients)
    {
        this.recipients = recipients;
        return this;
    }

    public EmailDAO addSearchContent(String string)
    {
        body.add(string);
        return this;
    }

    public EmailDAO addSearchContents(List<String> strings)
    {
        this.body = strings;
        return this;
    }

    public EmailDAO setDate()
    {
        date = new Date();
        return this;
    }

    public EmailDAO setDate(Date date)
    {
        this.date = date;
        return this;
    }

    protected void logSearchingFor()
    {
        if (debug) {
            if (date != null) {
                log.debug(String.format(" - Searching for received after: %s", date));
            }
            if (sender != null) {
                log.debug(String.format(" - Searching for sender: %s", sender));
            }
            if (recipients != null && recipients.size() > 0) {
                for (String recipient : recipients) {
                    log.debug(String.format(" - Searching for recipient: %s", recipient));
                }
            }
            if (subject != null) {
                log.debug(String.format(" - Searching for subject: %s", subject));
            }
            if (body != null && body.size() > 0) {
                for (String content : body) {
                    log.debug(String.format(" - Searching for content in body: %s", content));
                }
            }
        }
    }

    public EmailDAO executeSearch()
            throws IOException, MessagingException, InterruptedException
    {
        return executeSearch(searchTimeout);
    }

    public EmailDAO executeSearch(int searchTimeout)
            throws IOException, MessagingException, InterruptedException
    {
        connect();

        long currentTime = System.currentTimeMillis();
        long endTimeMillis = currentTime + searchTimeout;
        while (!search() && currentTime <= endTimeMillis) {
            closeFolder();
            Thread.sleep(3000);
            currentTime = System.currentTimeMillis();
            log.debug(String.format("Continuing search for mail... %s milliseconds to go before search timeout", endTimeMillis - currentTime));
        }

        return this;
    }

    public Message getMessage()
    {
        return message;
    }

    protected boolean search()
            throws MessagingException, IOException
    {
        connect();

        this.message = null;
        List<Boolean> matches = new ArrayList<Boolean>();

        // Get the list of messages
        Message[] messages = folder.getMessages();
        log.debug(String.format("Found %s message(s) in %s", messages.length, folderName));
        logSearchingFor();

        for (int i = messages.length - 1; i >= 0; i--) {
            // Clear Search criteria check
            match = true;
            matches.clear();

            Message message = messages[i];

            String subject = message.getSubject();
            Address[] from = message.getFrom();
            List<Address> rcptList = Arrays.asList(message.getAllRecipients());
            Date receivedDate = message.getReceivedDate();

            MimeMultipart multipart = (MimeMultipart) message.getContent();
            String content = multipart.getBodyPart("<html>").getContent().toString();

            // Print last message received
            if (i == messages.length - 1 && debug) {
                log.debug(String.format("The latest email is - from:%s - to:%s - Subject:'%s'", from[0], rcptList.get(0).toString(), subject));
            }

            if (this.date != null && receivedDate.before(date)) {
                match = false;
                return match;
            }

            if (this.subject != null && subject != null && subject.equals(this.subject)) {
                matches.add(true);
            }
            else if (this.subject != null) {
                matches.add(false);
            }

            if (this.sender != null && from != null && from.length > 0 && from[0].toString().equals(this.sender)) {
                matches.add(true);
            }
            else if (this.sender != null) {
                matches.add(false);
            }

            if (recipients != null && recipients.size() > 0) {
                for (String recipient : recipients) {
                    if (rcptList.contains(new InternetAddress(recipient))) {
                        matches.add(true);
                    }
                    else {
                        matches.add(false);
                        break;
                    }
                }
            }
            else {
                matches.add(false);
            }

            if (this.body != null && this.body.size() > 0) {
                for (String contentSearch : this.body) {
                    if (content.contains(contentSearch)) {
                        matches.add(true);
                    }
                    else {
                        matches.add(false);
                        break;
                    }
                }
            }

            // Verify if criteria was met
            match = !matches.contains(false);

            if (match) {
                this.message = message;
                log.info(String.format("Found Message: %s", message.getSubject()));
                return match;
            }
        }

        return false;
    }


    public void deleteAllMessages()
            throws MessagingException
    {
        connect();

        // get a list of java mail messages as an array of messages
        Message[] messages = folder.getMessages();

        //delete all the messages
        for (Message message : messages) {
            message.setFlag(FLAGS.Flag.DELETED, true);
        }
    }

    public int getMessageCount(String recipient)
            throws MessagingException
    {
        connect();

        // get a list of messages
        Message[] messages = folder.getMessages();
        return messages.length;
    }

    public void printMessages()
            throws MessagingException
    {
        connect();

        log.debug("IMAP Default Folder Name: " + folder.getFullName());

        // get a list of messages
        Message[] messages = folder.getMessages();
        for (Message message : messages) {
            log.debug(String.format("Subject:'%s'", message.getSubject()));
        }
    }

    public EmailDAO verify()
    {
        if (message == null) {
            throw new AssertionError(String.format("Message was not found after %s milliseconds for the following search criteria:\n%s", getDebugSearchCriteria(), searchTimeout));
        }
        return this;
    }

    protected String getDebugSearchCriteria()
    {
        StringBuffer buff = new StringBuffer();

        if (sender != null) {
            buff.append(String.format("sender: %s\n", sender));
        }
        if (subject != null) {
            buff.append(String.format("subject: %s\n", subject));
        }
        if (recipients.size() > 0) {
            buff.append(String.format("recipients: %s\n", StringUtils.join(recipients, ",")));
        }
        if (body.size() > 0) {
            buff.append("Body contents:\n");
            for (String content : body) {
                buff.append(String.format("    - %s\n", content));
            }
        }
        if (date != null) {
            DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            buff.append(String.format("Sent after date: %s\n", dateFormatter.format(date)));
        }

        return buff.toString();
    }
}
