package com.proofpoint.sampleproject.DAO;

import com.google.common.base.Preconditions;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.sampleproject.discovery.factories.AsyncHttpClientFactory;

import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.proofpoint.json.JsonCodec.listJsonCodec;
import static com.proofpoint.json.JsonCodec.mapJsonCodec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public abstract class RequestBuilder
{
    protected BoundRequestBuilder requestBuilder;
    protected AsyncHttpClient client;
    protected Response response;
    protected Map<String, Object> verifications = new HashMap<String, Object>();

    // Jackson JSON Serialization/De-Serialization libraries
    protected static final JsonCodec<List<Object>> listCodec = listJsonCodec(Object.class);
    protected static final JsonCodec<Map<String, Object>> mapCodec = mapJsonCodec(String.class, Object.class);

    protected AsyncHttpClient getClient()
    {
        if (client == null || client.isClosed()) {
            client = AsyncHttpClientFactory.getClient();
        }
        return client;
    }

    protected void close()
    {
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }

    public void addVerification(String type, Object verification)
    {
        verifications.put(type, verification);
    }

    public void verify()
            throws IOException
    {
        close();
        int testCount = 0;
        for (Entry<String, Object> verification : verifications.entrySet()) {

            // Verification of status code
            if (verification.getKey().equals("status")) {
                testCount++;
                assertEquals(response.getStatusCode(), ((Status) verification.getValue()).getStatusCode(), debugMessageWrapper(response, ""));
            }

            // Verification of JSON Hash or List
            else if (verification.getKey().equals("body")) {
                testCount++;
                Preconditions.checkArgument(verification.getValue() instanceof Map || verification.getValue() instanceof List,
                        "Body is neither a List<Object> or a Map<String, Object>");

                if (verification.getValue() instanceof Map) {
                    assertEquals(getMap(), (Map<String, Object>) verification.getValue(), debugMessageWrapper(response, ""));
                }
                else {
                    assertEquals(getList(), (List<Object>) verification.getValue(), debugMessageWrapper(response, ""));
                }
            }
        }

        assertTrue(testCount > 0, String.format("%s verifications performed on previous request!", testCount));
        verifications.clear();
    }

    public void verify(String debugMessage)
            throws IOException
    {
        try {
            verify();
        }
        catch (AssertionError e)
        {
            throw new AssertionError(String.format("%s - %s", debugMessage, e.toString()));
        }
    }

    protected BoundRequestBuilder prepareGet(String url)
    {
        requestBuilder = getClient().prepareGet(url);
        return requestBuilder;
    }

    protected BoundRequestBuilder preparePost(String url)
    {
        requestBuilder = getClient().preparePost(url);
        return requestBuilder;
    }

    protected BoundRequestBuilder preparePut(String url)
    {
        requestBuilder = getClient().preparePut(url);
        return requestBuilder;
    }

    protected BoundRequestBuilder prepareDelete(String url)
    {
        requestBuilder = getClient().prepareDelete(url);
        return requestBuilder;
    }

    public boolean exists()
            throws IOException
    {
        try {
            verify();
        }
        catch (AssertionError e) {
            return false;
        }

        return true;
    }

    public Response getResponse()
    {
        close();
        return response;
    }

    public Map<String, Object> getMap()
            throws IOException
    {
        close();
        return mapCodec.fromJson(response.getResponseBody());
    }

    public List<Object> getList()
            throws IOException
    {
        close();
        return listCodec.fromJson(response.getResponseBody());
    }

    public String getResponseBody()
            throws IOException
    {
        close();
        return response.getResponseBody();
    }

    public String getHeader(String headerName)
            throws IOException
    {
        close();
        return response.getHeader(headerName);
    }

    public abstract URI getURI();

    protected String debugMessageWrapper(Response response, String message)
            throws IOException
    {
        Preconditions.checkNotNull(response, "response must not be null");
        Preconditions.checkNotNull(message, "message must not be null");

        if (requestBuilder != null) {
            Request request = requestBuilder.build();
            return String.format("%s - %s %s\nResponse: %s\nContent:\n---- cut ----\n%s\n--- end cut ----\n",
                    message,
                    request.getMethod(),
                    request.getUrl(),
                    response.getStatusCode(),
                    response.getResponseBody());
        }

        return String.format("%s - Service URI: '%s'\nResponse: %s\nContent:\n---- cut ----\n%s\n--- end cut ----\n",
                message,
                response.getUri(),
                response.getStatusCode(),
                response.getResponseBody());
    }
}
