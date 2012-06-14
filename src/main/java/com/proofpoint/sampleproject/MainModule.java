package com.proofpoint.sampleproject;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.event.client.HttpEventClientConfig;

import static com.proofpoint.discovery.client.DiscoveryBinder.discoveryBinder;

public class MainModule implements Module
{
    public static final String SAMPLE_PROJECT_TEST_NAME = "sample-test-project";

    @Override
    public void configure(Binder binder)
    {
        binder.bind(TestManager.class).in(Scopes.SINGLETON);
        binder.bind(TestResource.class).in(Scopes.SINGLETON);

        // Configurations
        ConfigurationModule.bindConfig(binder).to(TestConfigurations.class);
        ConfigurationModule.bindConfig(binder).to(HttpEventClientConfig.class);

        // Discovery Announcement
        discoveryBinder(binder).bindHttpAnnouncement(SAMPLE_PROJECT_TEST_NAME).build();
    }
}