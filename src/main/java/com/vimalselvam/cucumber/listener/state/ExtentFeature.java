package com.vimalselvam.cucumber.listener.state;

import com.aventstack.extentreports.ExtentTest;
import gherkin.ast.Feature;

public class ExtentFeature {
    private Feature feature;
    private ExtentTest test;

    public ExtentFeature(final Feature feature, final ExtentTest test) {
        this.feature = feature;
        this.test = test;
    }

    public Feature getFeature() {
        return feature;
    }

    public ExtentTest getTest() {
        return test;
    }
}
