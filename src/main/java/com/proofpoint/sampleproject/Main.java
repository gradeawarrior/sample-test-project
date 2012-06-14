package com.proofpoint.sampleproject;

import java.io.File;
import java.net.URI;

import com.google.inject.Injector;
import com.proofpoint.bootstrap.Bootstrap;
import com.proofpoint.discovery.client.Announcer;
import com.proofpoint.discovery.client.DiscoveryClientConfig;
import com.proofpoint.discovery.client.DiscoveryModule;
import com.proofpoint.event.client.HttpEventModule;
import com.proofpoint.http.server.HttpServerModule;
import com.proofpoint.jaxrs.JaxrsModule;
import com.proofpoint.jmx.JmxHttpModule;
import com.proofpoint.jmx.JmxModule;
import com.proofpoint.json.JsonModule;
import com.proofpoint.log.LogJmxModule;
import com.proofpoint.log.Logger;
import com.proofpoint.node.NodeConfig;
import com.proofpoint.node.NodeModule;
import com.proofpoint.tracetoken.TraceTokenModule;
import org.weakref.jmx.guice.MBeanModule;

/**
 * TestNG test launcher
 */
public class Main
{
    private static final Logger log = Logger.get(Main.class);
    private static final int EXIT_FAIL = 70;

    public static Injector injector;
    public static URI discoveryUri;
    public static String environment;
    public static TestConfigurations testConfigurations;

    public static void main(String[] args)
            throws Exception
    {
        initialize();
    }

    private static synchronized void initialize()
            throws InterruptedException
    {
        Bootstrap app = new Bootstrap(
                new NodeModule(),
                new HttpServerModule(),
                new JsonModule(),
                new JaxrsModule(),
                new DiscoveryModule(),
                new MBeanModule(),
                new JmxModule(),
                new JmxHttpModule(),
                new LogJmxModule(),
                new HttpEventModule(),
                new TraceTokenModule(),
                new MainModule()
        );

        try {
            injector = app.initialize();
            DiscoveryClientConfig discoveryClientConfig = injector.getInstance(DiscoveryClientConfig.class);
            NodeConfig nodeConfig = injector.getInstance(NodeConfig.class);
            testConfigurations = injector.getInstance(TestConfigurations.class);

            discoveryUri = discoveryClientConfig.getDiscoveryServiceURI();
            environment = nodeConfig.getEnvironment();
            File testngFile = new File(testConfigurations.getTestngConfig());

            if (testConfigurations.getTestDaemon()) {
                injector.getInstance(Announcer.class).start();
            }
            else {
                injector.getInstance(TestManager.class).run(testngFile);
            }
        }
        catch (ThreadDeath expected) {
            System.exit(0);
        }
        catch (Exception e) {
            Logger.get(Main.class).error(e);
            System.err.flush();
            System.out.flush();
            Thread.sleep(1000L);
            System.exit(EXIT_FAIL);
        }
        catch (Throwable t) {
            Logger.get(Main.class).error(t);
            System.exit(EXIT_FAIL);
        }
    }
}