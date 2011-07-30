package com.proofpoint.sampleproject.common.templates;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public abstract class CustomerServiceDriver extends TestDriverSetup
{
    public static final Logger log = Logger.getLogger(CustomerServiceDriver.class);

    protected static final String ENDPOINT = "/v1/sample";
    protected static final String CUSTOMER_FACET = "foobar";
    protected static final String CUSTOMER_DOMAIN = "proofpoint.com";

    protected static final Map<String, Object> BASIC_JSON_OBJECT = new ImmutableMap.Builder<String, Object>()
            .put("legalhold", new ImmutableMap.Builder<String, Object>()
                    .build())
            .build();

    @BeforeClass
    @Parameters({"customer.protocol", "customer.host", "customer.port"})
    public void setup(@Optional("http") String protocol, @Optional("localhost") String host, @Optional("8080") Integer port)
            throws IOException, ExecutionException, InterruptedException
    {
        super.setup(protocol, host, port);

        url = protocol + "://" + host + ":" + port + ENDPOINT;
    }

}