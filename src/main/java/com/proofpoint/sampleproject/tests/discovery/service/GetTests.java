package com.proofpoint.sampleproject.tests.discovery.service;

import com.proofpoint.sampleproject.discovery.resources.Customer;
import com.proofpoint.sampleproject.discovery.resources.MessageStore;
import com.proofpoint.sampleproject.discovery.resources.User;
import com.proofpoint.sampleproject.tests.ServiceDriver;
import com.proofpoint.log.Logger;
import com.proofpoint.sampleproject.discovery.resources.Share;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetTests extends ServiceDriver
{
    private final Logger log = Logger.get(GetTests.class);
    private static final String ENDPOINT = "/v1/service";
    private static final String SERVICES_ATTRIBUTE = "services";
    private static final String TYPE_ATTRIBUTE = "type";

    private final String url;
    private List<Object> services;

    public GetTests(URI serviceURI)
    {
        super(serviceURI);
        url = serviceURI.toString() + ENDPOINT;
    }

    @Test(description = "Test that there is at least 1 service registered in discovery", groups = "services-exist")
    public void testGetAllServices()
            throws Exception
    {
        response = client.prepareGet(url).execute().get();

        services = (List) mapCodec.fromJson(response.getResponseBody()).get(SERVICES_ATTRIBUTE);

        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertTrue(services.size() > 0, "There are services registered");
    }

    @Test(dependsOnGroups = "services-exist")
    public void testShareServiceIsRegistered()
            throws Exception
    {
        testServiceFound(Share.SERVICE_NAME);
    }

    @Test(dependsOnGroups = "services-exist")
    public void testMessageStoreServiceIsRegistered()
            throws Exception
    {
        testServiceFound(MessageStore.SERVICE_NAME);
    }

    @Test(dependsOnGroups = "services-exist")
    public void testUserStoreServiceIsRegistered()
            throws Exception
    {
        testServiceFound(User.SERVICE_NAME);
    }

    @Test(dependsOnGroups = "services-exist")
    public void testCustomerStoreServiceIsRegistered()
            throws Exception
    {
        testServiceFound(Customer.SERVICE_NAME);
    }

    public void testServiceFound(String serviceName)
    {
        boolean found = false;
        int count = 0;
        for (Object serviceObject : services) {
            Map<String, Object> service = (Map<String, Object>) serviceObject;
            if (service.get(TYPE_ATTRIBUTE).equals(serviceName)) {
                found = true;
                count++;
            }
        }

        assertEquals(found, true, String.format("There are %1$d services of type '%2$s'", count, serviceName));
    }
}
