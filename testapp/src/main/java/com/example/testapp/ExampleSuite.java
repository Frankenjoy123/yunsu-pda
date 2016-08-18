package com.example.testapp;

import junit.framework.TestSuite;

public class ExampleSuite extends TestSuite {
    public ExampleSuite() {
        addTestSuite( MathTest.class );
    }
}
