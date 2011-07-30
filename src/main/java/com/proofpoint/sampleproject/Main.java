package com.proofpoint.sampleproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;


/**
 * TestNG test launcher
 */
public class Main
{

    private final static Class<?>[] TEST_CLASSES = new Class[]{};

    public static void main(String[] args)
            throws IOException
    {
        BasicConfigurator.configure();
        Logger.getLogger("com.ning.http.client.providers.netty.NettyAsyncHttpProvider").setLevel(Level.DEBUG);
        Logger.getLogger("org.slf4j.impl.StaticLoggerBinder").setLevel(Level.DEBUG);

        // look for configuration files in etc/testng*.xml
        List<String> testSuites = new ArrayList<String>();
        File configDir = new File("etc");
        if (configDir.exists() && configDir.isDirectory()) {

            File[] testSuiteFiles = configDir.listFiles();
            for (File file : testSuiteFiles) {
                String fileName = file.getName();
                if (file.isFile() && fileName.startsWith("testng") && fileName.endsWith(".xml")) {
                    testSuites.add(file.getAbsolutePath());
                }
            }
        }

        // kick off this test suite programatically
        TestNG testng = new TestNG();
        testng.setVerbose(10);
        if (testSuites != null && !testSuites.isEmpty()) {
            testng.setTestSuites(testSuites);
        }
        else {
            testng.setDefaultSuiteName("QATests");
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
