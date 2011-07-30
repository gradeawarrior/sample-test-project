package com.proofpoint.sampleproject.factories;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;
import com.ning.http.client.Realm.RealmBuilder;
import com.proofpoint.sampleproject.web.security.MySSLSocketFactory;

public class AsyncHttpClientFactory
{
    public static AsyncHttpClient getClient()
    {
        Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setSSLContext(MySSLSocketFactory.createEasySSLContext());
        return new AsyncHttpClient(builder.build());
    }

    public static AsyncHttpClient getClient(String user, String password)
    {
        Builder builder = new AsyncHttpClientConfig.Builder();
        Realm realm = new RealmBuilder()
                .setPrincipal(user)
                .setPassword(password)
                .setUsePreemptiveAuth(true)
                .setScheme(AuthScheme.BASIC)
                .build();

        builder.setSSLContext(MySSLSocketFactory.createEasySSLContext())
                .setRealm(realm);

        return new AsyncHttpClient(builder.build());
    }
}
