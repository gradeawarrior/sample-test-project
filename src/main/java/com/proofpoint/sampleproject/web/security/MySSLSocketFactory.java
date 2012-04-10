package com.proofpoint.sampleproject.web.security;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class MySSLSocketFactory extends SSLSocketFactory {

  /**
   * Log object for this class.
   */
  private static final Log LOG = LogFactory.getLog(MySSLSocketFactory.class);

  /**
   * Constructor for MySSLSocketFactory.
   */
  public MySSLSocketFactory() {
    super(createEasySSLContext());
    setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
  }

  public static SSLContext createEasySSLContext() {
    try {
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, new TrustManager[] { new NaiveTrustManager(null) }, null);
      return context;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      throw new HttpClientError(e.toString());
    }
  }

}