package com.proofpoint.sampleproject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.proofpoint.log.Logger;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TestNG test launcher
 */
public class TestManager
{

    private Set<TestStatus> testHistory = Collections.synchronizedSet(new HashSet<TestStatus>());
    private final Logger logger = Logger.get(TestManager.class);

    @Inject
    public TestManager()
    {
        //TODO: Will need other stuff like stats and events
    }

    // TODO: Run asynchronously

    public synchronized boolean run(InputStream xmlSuite)
            throws IOException, SAXException, ParserConfigurationException
    {
        Collection<XmlSuite> allSuites = new Parser(xmlSuite).parse();

        TestStatus testStatus = new TestStatus();

        // kick off this test suite programatically
        TestNG testng = new TestNG();
        testng.setVerbose(10);
        testng.setCommandLineSuite(allSuites.iterator().next());

        // configurable via testng.xml or always hardcode?
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.setOutputDirectory("target" + File.separatorChar + "test-reports");
        testng.run();
        testStatus.finished(!testng.hasFailure());
        testHistory.add(testStatus);
        return true;
    }

    public void run(File testngFile)
            throws IOException, SAXException, ParserConfigurationException
    {
        Preconditions.checkNotNull(testngFile, "testngFile must not be null");
        Preconditions.checkArgument(testngFile.exists(), String.format("testngFile %s must exist", testngFile.getCanonicalPath()));
        Preconditions.checkArgument(testngFile.isFile(), String.format("testngFile %s must be a file", testngFile.getCanonicalPath()));

        List<String> testSuites = new ArrayList<String>();
        testSuites.add(testngFile.getAbsolutePath());

        TestNG testng = new TestNG();
        testng.setVerbose(10);
        if (testSuites != null && !testSuites.isEmpty()) {
            testng.setTestSuites(testSuites);
        }
        else {
            logger.info("No tests detected in %s", testngFile.getCanonicalPath());
            return;
        }

        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.setOutputDirectory("target" + File.separatorChar + "test-reports");
        testng.run();

        System.out.flush();
        System.err.flush();
        System.exit(testng.getStatus());
    }

    public synchronized Set<TestStatus> status()
    {
        return ImmutableSet.copyOf(testHistory);
    }

    public Set<Map<String, Object>> toWire()
    {
        ImmutableSet.Builder<Map<String, Object>> resultsBuilder = ImmutableSet.builder();
        for (TestStatus testStatus : status()) {
            resultsBuilder.add(testStatus.toWire());
        }
        return resultsBuilder.build();
    }
}
