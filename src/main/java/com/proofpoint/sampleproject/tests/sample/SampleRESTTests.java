package com.proofpoint.sampleproject.tests.sample;

import com.google.common.collect.ImmutableMap;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.proofpoint.sampleproject.factories.AsyncHttpClientFactory;
import com.proofpoint.sampleproject.json.JsonCodec;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.proofpoint.sampleproject.json.JsonCodec.listJsonCodec;
import static com.proofpoint.sampleproject.json.JsonCodec.mapJsonCodec;

public class SampleRESTTests
{
    private static final String CUSTOMER_FACET = "foobar";
    private static final String CUSTOMER_ID = "Proofpoint";

    private static final Map<String, Object> customer = new ImmutableMap.Builder<String, Object>()
            .put("email", "superdupertango@proofpoint.com")
            .put("cooless_factor", "7")
            .put("name", "supertango")
            .put("is_valid", "true")
            .build();

    private static final JsonCodec<List<Object>> listCodec = listJsonCodec(Object.class);
    private static final JsonCodec<Map<String, Object>> mapCodec = mapJsonCodec(String.class, Object.class);

    private final AsyncHttpClient client = AsyncHttpClientFactory.getClient();
    private final String url = "http://www.example.com:8080/v1/customer";
    private String customer_url;

    private Response response;

    @BeforeClass
    public void classSetup()
    {
        customer_url = url + "/" + CUSTOMER_ID;
    }

    @Test
    public void testDeleteFacetWithValidURL()
            throws IOException, ExecutionException, InterruptedException
    {
        response = client.prepareDelete(url + "/" + CUSTOMER_FACET)
                .execute()
                .get();

        Assert.assertEquals(response.getStatusCode(), 204);
        Assert.assertEquals(response.getStatusText(), "No Content");

        response = client.prepareGet(customer_url + "/" + CUSTOMER_FACET)
                .execute()
                .get();

        Assert.assertEquals(response.getStatusCode(), 404);
    }

    @Test
    public void testPutRequestValidURL()
            throws IOException, ExecutionException, InterruptedException
    {

        response = client.preparePut(url + "/" + CUSTOMER_FACET)
                .setBody(mapCodec.toJson(customer))
                .setHeader("Content-Type", "application/json")
                .execute()
                .get();

        Assert.assertEquals(response.getStatusCode(), 204);
        Assert.assertEquals(response.getStatusText(), "No Content");

        response = client.prepareGet(url + "/" + CUSTOMER_FACET)
                .execute()
                .get();

        Map actual = mapCodec.fromJson(response.getResponseBody());
        Map<String, Object> expected = customer;

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(actual, expected);
    }
}
