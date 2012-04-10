package com.proofpoint.sampleproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;
import com.proofpoint.bootstrap.Bootstrap;
import com.proofpoint.discovery.client.DiscoveryClientConfig;
import com.proofpoint.discovery.client.DiscoveryModule;
import com.proofpoint.event.client.HttpEventModule;
import com.proofpoint.experimental.jmx.JmxHttpModule;
import com.proofpoint.http.server.HttpServerModule;
import com.proofpoint.jaxrs.JaxrsModule;
import com.proofpoint.jmx.JmxModule;
import com.proofpoint.json.JsonModule;
import com.proofpoint.log.Logger;
import com.proofpoint.node.NodeConfig;
import com.proofpoint.node.NodeModule;
import com.proofpoint.sampleproject.discovery.factories.DiscoveryTestFactory;
import com.proofpoint.sampleproject.tests.SampleTest;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.weakref.jmx.guice.MBeanModule;

/**
 * TestNG test launcher
 */
public class Main
{
    private static final Logger log = Logger.get(Main.class);

    private final static Class<?>[] TEST_CLASSES =
            new Class[]{
                    DiscoveryTestFactory.class,
                    SampleTest.class
            };

    public static Injector injector;
    public static TestConfigurations configurations;
    public static URI discoveryUri;
    public static String environment;

    public static void main(String[] args)
            throws Exception
    {
        Bootstrap app = new Bootstrap(
                new NodeModule(),
//                new HttpEventModule(),
                new JsonModule(),
                new JaxrsModule(),
                new HttpServerModule(),
                new DiscoveryModule(),
                new MBeanModule(),
                new JmxModule(),
                new JmxHttpModule(),
                new HttpEventModule(),
                new MainModule()
        );

        injector = app.strictConfig().initialize();
        configurations = injector.getInstance(TestConfigurations.class);
        DiscoveryClientConfig discoveryClientConfig = injector.getInstance(DiscoveryClientConfig.class);
        NodeConfig nodeConfig = injector.getInstance(NodeConfig.class);

        // Discovery Configs
        discoveryUri = discoveryClientConfig.getDiscoveryServiceURI();
        environment = nodeConfig.getEnvironment();

        // look for configuration files in etc/testng*.xml
        File testngFile = new File(configurations.getTestngConfig());
        List<String> testSuites = new ArrayList<String>();

        if (testngFile.exists() && testngFile.isFile()) {
            testSuites.add(testngFile.getAbsolutePath());
        }
        else {
            FileNotFoundException exception = new FileNotFoundException(configurations.getTestngConfig() + " was not found");
            log.error(exception);
            throw exception;
        }

        // kick off this test suite programatically
        TestNG testng = new TestNG();
        testng.setVerbose(10);
        if (testSuites != null && !testSuites.isEmpty()) {
            testng.setTestSuites(testSuites);
        }
        else {
            testng.setDefaultSuiteName("SeleniumTests");
            testng.setTestClasses(TEST_CLASSES);
        }
        // configurable via testng.xml or always hardcode?
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.setOutputDirectory("target" + File.separatorChar + "test-reports");
        testng.run();

        // Proofpoint logger doesn't autoflush.
        System.out.flush();
        System.err.flush();
        System.exit(testng.getStatus());
    }
}