package com.proofpoint.sampleproject.util;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ImapConnector {
  Folder folder;
  Store store;
 
  public Store getStore(String imapHost, String imapPort, String timeout, String username, String password) {

    Properties props = new Properties();
    String provider = "imap";
    props.setProperty("mail.store.protocol", "imaps");
    props.setProperty("mail.imap.host", imapHost);
    props.setProperty("mail.imap.port", imapPort);
    props.setProperty("mail.imap.connectiontimeout", timeout);
    props.setProperty("mail.imap.timeout", timeout);
    Store store = null;
    try {
      // Connect to the server
      Session session = Session.getDefaultInstance(props, null);
      store = session.getStore(provider);
      store.connect(imapHost, username, password);
    } catch (NoSuchProviderException nspe) {
      System.err.println("invalid provider name");
    } catch (MessagingException me) {
      System.err.println("messaging exception");
      me.printStackTrace();
    }
    return store;
  }
 
  public Message[] getMessages(String imapHost, String imapPort, String timeout, String username, String password,String folderName) throws MessagingException{
      store = getStore(imapHost, imapPort, timeout, username, password);

    // open the inbox folder
    
   
      folder = store.getFolder(folderName);
      folder.open(Folder.READ_WRITE);
      
   // get a list of java mail messages as an array of messages
      Message[] messages = folder.getMessages();
      
      return messages;
  }
 
  public void messageTearDown() throws MessagingException{
    folder.close(true);
    store.close();
  }    

}

