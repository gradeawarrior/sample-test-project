package com.proofpoint.sampleproject.discovery.factories;

import com.proofpoint.sampleproject.discovery.ServiceFactory;
import com.proofpoint.log.Logger;
import org.testng.internal.ObjectFactoryImpl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class TestFactory
{
    private static final Logger logger = Logger.get(TestFactory.class);
    protected static final ObjectFactoryImpl OBJECT_FACTORY = new ObjectFactoryImpl();

    public static Object[] configureNodes(List<Class> testClassList, Class providerClass)
            throws IOException, NoSuchMethodException
    {
        // Grab the list of User services found in Discovery
        List<URI> serviceList = ServiceFactory.getServices(providerClass);
        logger.debug("service list size " + serviceList.size());
        int testCount = serviceList.size() * testClassList.size();
        logger.debug("Instantiating " + testCount + " test(s)");
        List<Object> testInstances = new ArrayList();

        // Factories logic for TestNG - http://testng.org/doc/documentation-main.html#factories
        for (Class testClass : testClassList) {
            for (URI serviceURI : serviceList) {
                logger.debug(testClass.toString() + "(" + serviceURI.toString() + ")");

                try {
                    testInstances.add(OBJECT_FACTORY.newInstance(testClass.getConstructor(URI.class), serviceURI));
                }
                catch (Exception e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            }
        }

        return testInstances.toArray();
    }

    public Object[] configureNodes(List<Class> testClassList, List<URI> serviceList)
            throws IOException, NoSuchMethodException
    {
        logger.debug("service list size " + serviceList.size());
        int testCount = serviceList.size() * testClassList.size();
        logger.debug("Instantiating " + testCount + " test(s)");
        List<Object> testInstances = new ArrayList();

        // Factories logic for TestNG - http://testng.org/doc/documentation-main.html#factories
        for (Class testClass : testClassList) {
            for (URI serviceURI : serviceList) {
                logger.debug(testClass.toString() + "(" + serviceURI.toString() + ")");

                try {
                    testInstances.add(OBJECT_FACTORY.newInstance(testClass.getConstructor(URI.class), serviceURI));//serviceURI));
                }
                catch (Exception e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            }
        }

        return testInstances.toArray();
    }
}
