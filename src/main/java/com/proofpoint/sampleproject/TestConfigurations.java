package com.proofpoint.sampleproject;

import com.proofpoint.configuration.Config;

import javax.validation.constraints.NotNull;

public class TestConfigurations
{
    private String testngConfig = null;

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
}
