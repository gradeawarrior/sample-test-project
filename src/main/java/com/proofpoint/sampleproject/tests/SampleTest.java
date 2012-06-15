package com.proofpoint.sampleproject.tests;

import com.proofpoint.http.client.ApacheHttpClient;
import com.proofpoint.http.client.StatusResponseHandler.StatusResponse;
import org.testng.annotations.Test;

import java.net.URI;

import static com.proofpoint.http.client.Request.Builder.prepareGet;
import static com.proofpoint.http.client.StatusResponseHandler.createStatusResponseHandler;
import static org.testng.Assert.assertEquals;

public class SampleTest
{
    ApacheHttpClient client = new ApacheHttpClient();

    @Test
    public void myTest1() {}

    @Test
    public void myTest2() {}

    @Test
    public void myTest3() {}

    @Test
    public void myHTTPTest()
            throws Exception
    {
        StatusResponse response = client.execute(
                prepareGet().setUri(new URI("http://www.google.com")).build(),
                createStatusResponseHandler());

        assertEquals(response.getStatusCode(), 200);
    }
}
