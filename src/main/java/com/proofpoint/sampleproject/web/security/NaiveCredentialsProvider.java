package com.proofpoint.sampleproject.web.security;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class NaiveCredentialsProvider extends BasicCredentialsProvider {

  public NaiveCredentialsProvider(Credentials credentials) {
    super();
    setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), credentials);
  }
}
