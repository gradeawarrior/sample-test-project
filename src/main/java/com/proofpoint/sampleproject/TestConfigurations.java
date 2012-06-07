package com.proofpoint.sampleproject;

import com.proofpoint.configuration.Config;

import javax.validation.constraints.NotNull;

public class TestConfigurations
{
    private String testngConfig = null;
    private boolean testDaemon = false;

    @NotNull
    public String getTestngConfig()
    {
        return testngConfig;
    }

    @Config("testng.config")
    public TestConfigurations setTestngConfig(String testngConfig)
    {
        this.testngConfig = testngConfig;
        return this;
    }

    public boolean getTestDaemon()
    {
        return testDaemon;
    }

    @Config("test.daemon")
    public TestConfigurations setTestDaemon(boolean testDaemon)
    {
        this.testDaemon = testDaemon;
        return this;
    }
}
