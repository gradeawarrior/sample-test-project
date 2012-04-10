DESCRIPTION
===========

    This is a REST Integration Test application that is to be injected into the
    environment to functionally verify that the environment/service is working.

INSTRUCTIONS
============

    1. Extract the tarball

    2. create the etc directory

    3. Create etc/config.properties and add the following:

            discovery.uri = http://<DISCOVERY_HOST_AND_DOMAIN>:<PORT>
            node.environment = <YOUR_ENVIRONMENT>

        DISCOVERY_HOST_AND_DOMAIN   -   The host where discovery is running (e.g. discovery.lab.proofpoint.com:8080)
        YOUR_ENVIRONMENT            -   The node environment for discovery (e.g. legalholdci)

    4. touch the etc/jvm.config

    5. Create etc/testng.xml. See below for an example test config file. Default is to run all tests.

TEST CONFIG
===========

    This is a standard testng.xml file that can be used to enable/disable the execution of the tests:

<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Share API Integration Tests" verbose="1" parallel="classes" thread-count="1">

    <test name="Discovery">
        <classes>
            <class name="com.proofpoint.sampleproject.discovery.factories.DiscoveryTestFactory"/>
        </classes>
    </test>

    <test name="Sample">
        <classes>
            <class name="com.proofpoint.sampleproject.tests.SampleTest"/>
        </classes>
    </test>

</suite>