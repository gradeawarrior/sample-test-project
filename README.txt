This is a simple maven project to be used for building a test. There is a single Main class that controls the
TestNG tests. Using your IDE, run this Main class. The tests can be controlled via the etc/testng.xml file.
To disable a test, you can comment out that test in the etc/testng.xml. Only classes/packages/groups specified
in the testng.xml will be run.

For instructions on how to configure the testng.xml, see:

    http://testng.org/doc/documentation-main.html

If running via terminal, you can optionally run the tests via:

    mvn clean test
