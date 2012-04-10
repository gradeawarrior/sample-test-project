package com.proofpoint.sampleproject.discovery.factories;

import com.google.common.collect.ImmutableList;
import com.proofpoint.sampleproject.discovery.ServiceFactory;
import com.proofpoint.sampleproject.tests.discovery.service.GetTests;
import com.proofpoint.log.Logger;
import org.testng.annotations.Factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class DiscoveryTestFactory extends TestFactory
{
    private static final Logger logger = Logger.get(DiscoveryTestFactory.class);

    protected final ImmutableList<Class> testClassList = new ImmutableList.Builder<Class>()
            .add(GetTests.class)
            .build();

    @Factory
    public Object[] configureNodes()
            throws IOException, NoSuchMethodException, URISyntaxException
    {
        // Pull in config.properties
        Properties properties = new Properties();
        properties.load(new FileInputStream(ServiceFactory.CONFIG_PATH));
        URI serviceURI = new URI((String) properties.get("discovery.uri"));

        int testIndex = 0;
        int testCount = testClassList.size();
        logger.debug("Instantiating " + testCount + " test(s)");
        Object[] result = new Object[testCount];

        // Factories logic for TestNG - http://testng.org/doc/documentation-main.html#factories
        for (Class testClass : testClassList) {
            logger.debug("test[" + testIndex + "] = " + testClass.toString() + "(" + serviceURI.toString() + ")");
            result[testIndex++] = OBJECT_FACTORY.newInstance(testClass.getConstructor(URI.class), serviceURI);
        }

        return result;
    }
}
