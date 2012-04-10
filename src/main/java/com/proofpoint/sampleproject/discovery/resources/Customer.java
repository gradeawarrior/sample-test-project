package com.proofpoint.sampleproject.discovery.resources;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.proofpoint.sampleproject.discovery.Resource;
import com.proofpoint.discovery.client.HttpServiceSelector;
import com.proofpoint.discovery.client.ServiceType;
import com.proofpoint.log.Logger;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

public class Customer implements Resource
{
    public static final String SERVICE_NAME = "customer";

    private final Logger log = Logger.get(Customer.class);
    private final HttpServiceSelector serviceSelector;

    @Inject
    public Customer(@ServiceType(SERVICE_NAME) HttpServiceSelector serviceSelector)
    {
        Preconditions.checkNotNull(serviceSelector, "Customer serviceSelector must not be null");
        this.serviceSelector = serviceSelector;
    }

    @Override
    public List<URI> getServices()
    {
        return serviceSelector.selectHttpService();
    }

    @Override
    public URI getService()
            throws NoSuchElementException
    {
        List<URI> services = getServices();

        if (services.size() > 0) {
            return services.get(0);
        }
        throw new NoSuchElementException(SERVICE_NAME + " Service not found");
    }
}
