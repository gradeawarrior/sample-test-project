package com.proofpoint.sampleproject.web.security;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 */
public class NaiveTrustManager implements X509TrustManager {
  //  private X509TrustManager standardTrustManager = null;

  /**
   * Constructor for EasyX509TrustManager.
   */
  public NaiveTrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
    super();
    TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    factory.init(keystore);
    TrustManager[] trustmanagers = factory.getTrustManagers();
    if ( trustmanagers.length == 0 ) {
      throw new NoSuchAlgorithmException("no trust manager found");
    }
    //      this.standardTrustManager = (X509TrustManager)trustmanagers[0];
  }

  /**
   * Doesn't throw an exception, so this is how it approves a certificate.
   * 
   * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
   */
  public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
  }

  /**
   * Doesn't throw an exception, so this is how it approves a certificate.
   * 
   * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
   */
  public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
  }

  /**
   * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
   */
  public X509Certificate[] getAcceptedIssuers() {
    return null; // I've seen someone return new X509Certificate[ 0 ];
  }
}