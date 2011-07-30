package com.proofpoint.sampleproject.common.templates;

import com.google.common.base.Preconditions;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.proofpoint.sampleproject.factories.AsyncHttpClientFactory;
import com.proofpoint.sampleproject.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.proofpoint.sampleproject.json.JsonCodec.listJsonCodec;
import static com.proofpoint.sampleproject.json.JsonCodec.mapJsonCodec;

public class TestDriverSetup {
    private static final Logger log = Logger.getLogger(TestDriverSetup.class);

    protected static final AsyncHttpClient client = AsyncHttpClientFactory.getClient();
    protected Response response;
    protected String url;

    protected static final JsonCodec<List<Object>> listCodec = listJsonCodec(Object.class);
    protected static final JsonCodec<Map<String, Object>> mapCodec = mapJsonCodec(String.class, Object.class);

      public void setup(String protocol, String host, Integer port)
            throws IOException, ExecutionException, InterruptedException
    {
        Preconditions.checkNotNull(protocol, "protocol must not be null");
        Preconditions.checkNotNull(host, "host must not be null");
        Preconditions.checkNotNull(port, "port must not be null");

        this.url = protocol + "://" + host + ":" + port;

    }
}

