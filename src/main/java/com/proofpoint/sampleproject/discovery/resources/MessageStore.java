package com.proofpoint.sampleproject.discovery.resources;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.proofpoint.discovery.client.HttpServiceSelector;
import com.proofpoint.discovery.client.ServiceType;
import com.proofpoint.log.Logger;
import com.proofpoint.sampleproject.discovery.Resource;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

public class MessageStore implements Resource
{
    public static final String SERVICE_NAME = "MESSAGE_STORE";

    private final Logger log = Logger.get(MessageStore.class);
    private final HttpServiceSelector serviceSelector;

    @Inject
    public MessageStore(@ServiceType(SERVICE_NAME) HttpServiceSelector serviceSelector)
    {
        Preconditions.checkNotNull(serviceSelector, "Customer serviceSelector must not be null");
        this.serviceSelector = serviceSelector;
    }

    @Override
    public List<URI> getServices()
    {
        try {
            // TODO: FIXME - This is a hack to get consistent results from discovery
            Thread.sleep(500);
            return serviceSelector.selectHttpService();
        }
        catch (InterruptedException e) {
            throw new NoSuchElementException(SERVICE_NAME + " Service not found - " + serviceSelector.toString());
        }
    }

    @Override
    public URI getService()
            throws NoSuchElementException
    {
        List<URI> services = getServices();

        if (services.size() > 0) {
            return services.get(0);
        }
        throw new NoSuchElementException(SERVICE_NAME + " Service not found - " + serviceSelector.toString());
    }
}
