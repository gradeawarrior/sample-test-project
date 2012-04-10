package com.proofpoint.sampleproject.discovery;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.proofpoint.sampleproject.discovery.resources.*;
import com.proofpoint.configuration.ConfigurationFactory;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.discovery.client.DiscoveryModule;
import com.proofpoint.jmx.JmxModule;
import com.proofpoint.json.JsonModule;
import com.proofpoint.log.Logger;
import com.proofpoint.node.NodeModule;
import org.weakref.jmx.guice.MBeanModule;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.proofpoint.discovery.client.DiscoveryBinder.discoveryBinder;

public class ServiceFactory implements Module
{
    private static final Logger LOGGER = Logger.get(ServiceFactory.class);
    public static final String CONFIG_PATH = "etc/config.properties";

    public static URI shareServiceURI = null;
    public static URI messageStoreURI = null;

    public static Injector serviceInjector;

    static {
        try {
            // Pull in config.properties
            Properties properties = new Properties();
            properties.load(new FileInputStream(ServiceFactory.CONFIG_PATH));

            serviceInjector = Guice.createInjector(
                    new JsonModule(),
                    new JmxModule(),
                    new MBeanModule(),
                    new NodeModule(),

                    // Discovery service module
                    new DiscoveryModule(),
                    new ServiceFactory(),

                    // Configs
                    new ConfigurationModule(new ConfigurationFactory(new HashMap<String, String>((Map) properties))));
        }
        catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    @Override
    public void configure(Binder binder)
    {
        /**
         * ====================== Modify below this line ======================
         */

        binder.bind(User.class).in(Scopes.SINGLETON);
        discoveryBinder(binder).bindHttpSelector(User.SERVICE_NAME);

        binder.bind(Customer.class).in(Scopes.SINGLETON);
        discoveryBinder(binder).bindHttpSelector(Customer.SERVICE_NAME);

        binder.bind(Share.class).in(Scopes.SINGLETON);
        discoveryBinder(binder).bindHttpSelector(Share.SERVICE_NAME);

        binder.bind(MessageStore.class).in(Scopes.SINGLETON);
        discoveryBinder(binder).bindHttpSelector(MessageStore.SERVICE_NAME);

        /**
         * ====================== Modify above this line ======================
         */
    }

    public static synchronized List<URI> getServices(Class selector)
    {
        return ((Resource) serviceInjector.getInstance(selector)).getServices();
    }

    public static synchronized URI getService(Class selector)
    {
        if (selector.equals(Share.class)) {
            if (shareServiceURI != null) {
                return shareServiceURI;
            }
            else {
                shareServiceURI = ((Resource) serviceInjector.getInstance(selector)).getService();
                return shareServiceURI;
            }
        }
        else if (selector.equals(MessageStore.class)) {
            if (messageStoreURI != null) {
                return messageStoreURI;
            }
            else {
                messageStoreURI = ((Resource) serviceInjector.getInstance(selector)).getService();
                return messageStoreURI;
            }
        }
        else {
            return ((Resource) serviceInjector.getInstance(selector)).getService();
        }
    }
}