package com.proofpoint.sampleproject.tests;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;


public class SampleFailureTest
{

    @Test
    public void failedTest() {
        assertFalse(true, "Example failed test");
    }

    @Test
    public void passedTest() {
        assertFalse(false, "Example failed test");
    }
}
