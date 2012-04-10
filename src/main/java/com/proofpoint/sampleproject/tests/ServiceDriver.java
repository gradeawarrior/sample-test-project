package com.proofpoint.sampleproject.tests;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.sampleproject.discovery.factories.AsyncHttpClientFactory;
import com.proofpoint.log.Logger;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.proofpoint.json.JsonCodec.listJsonCodec;
import static com.proofpoint.json.JsonCodec.mapJsonCodec;

public class ServiceDriver
{
    private static final Logger log = Logger.get(ServiceDriver.class);

    protected AsyncHttpClient client = AsyncHttpClientFactory.getClient(120000);
    protected Response response;
    protected URI serviceURI;

    protected static final JsonCodec<List<Object>> listCodec = listJsonCodec(Object.class);
    protected static final JsonCodec<Map<String, Object>> mapCodec = mapJsonCodec(String.class, Object.class);

    public ServiceDriver()
    {
        this.serviceURI = null;
    }

    public ServiceDriver(URI serviceURI)
    {
        this.serviceURI = serviceURI;
    }

    public String toString()
    {
        return String.format("%s(%s)", this.getClass(), serviceURI.toString());
    }
}
