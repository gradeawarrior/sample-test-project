DESCRIPTION
===========

    This is a REST Integration Test application that is to be deployed into the
    environment to functionally verify that the environment/service(s) is working.

INSTRUCTIONS
============

1. Extract the tarball

3. Add etc/ directory alongside bin/ and lib/ directories, with required configuration (see below) included.

4. To operate the service, use bin/launcher [start|stop|status]

5. To install as a service, which should be run at startup, symlink bin/init.redhat into /etc/init.d and enable in chkconfig.


Required Configuration
======================

** etc/config.properties (KEY=VALUE format)

discovery.uri               The url to the discovery service
node.environment            The discovery environment name
http-server.http.port       The port to listen on, defaults to 8080 (use 0 to bind to a random port)
log.levels-file             The path to log.properties file
test.daemon                 Configures whether the script should run as RESTful service or not (default to false)
testng.config               The location of the testng.xml configuration file. Suggested etc/testng.xml

** etc/jvm.config

(this file must exist, but may be empty - contains Java JVM configuration)

** etc/log.properties

com.proofpoint 		   The root logger level configuration (suggested value = INFO)

** etc/testng.xml

The configured set of tests to run against the environment. See below for an example testng.xml file.


TEST CONFIG
===========

    This is a standard testng.xml file that can be used to enable/disable the execution of the tests:

<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >

<suite name="Share API Integration Tests" verbose="1" parallel="classes" thread-count="1">

    <test name="Sample">
        <classes>
            <class name="com.proofpoint.sampleproject.tests.SampleTest"/>
        </classes>
    </test>

    <test name="FailureSample">
        <classes>
            <class name="com.proofpoint.sampleproject.tests.SampleFailureTest"/>
        </classes>
    </test>

</suite>


Sample Project REST API
=======================

If the server is configured to run in server mode (test.daemon = true),
The test application will run as a RESTful service that accepts test requests.

POST /v1/test
    - Schedules a new test
    - Request Body:
        A valid testng.xml to execute existing programmed tests on the server
    - Response Codes:
        200     Test was successfully scheduled

GET /v1/test
    - Returns past test results in JSON

