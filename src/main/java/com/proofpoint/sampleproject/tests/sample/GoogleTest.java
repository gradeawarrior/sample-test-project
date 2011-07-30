package com.proofpoint.sampleproject.tests.sample;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.proofpoint.sampleproject.factories.AsyncHttpClientFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GoogleTest
{
    private static final AsyncHttpClient client = AsyncHttpClientFactory.getClient();
    private Response response;

    @Test
    public void testLogPass()
    {
        assertTrue(true, "This should pass");
    }

    @Test
    public void testGoogleGET()
            throws IOException, ExecutionException, InterruptedException
    {
        response = client.prepareGet("http://www.google.com")
                .execute()
                .get();

        assertEquals(response.getStatusCode(), 200);
    }
}
