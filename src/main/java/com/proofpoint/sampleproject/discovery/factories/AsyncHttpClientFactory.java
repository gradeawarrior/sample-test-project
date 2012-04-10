package com.proofpoint.sampleproject.discovery.factories;

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
        return new AsyncHttpClient(getBuilder(120000).build());
    }

    public static AsyncHttpClient getClient(String user, String password)
    {
        Realm realm = new RealmBuilder()
                .setPrincipal(user)
                .setPassword(password)
                .setUsePreemptiveAuth(true)
                .setScheme(AuthScheme.BASIC)
                .build();

        Builder builder = getBuilder(12000)
                .setSSLContext(MySSLSocketFactory.createEasySSLContext())
                .setRealm(realm);

        return new AsyncHttpClient(builder.build());
    }

    protected static Builder getBuilder(int requestTimeoutInMs)
    {
        return new AsyncHttpClientConfig.Builder().setRequestTimeoutInMs(requestTimeoutInMs);
    }

    public static AsyncHttpClient getClient(int requestTimeoutInMs)
    {
        return new AsyncHttpClient(getBuilder(requestTimeoutInMs).build());
    }
}
