package com.vimalselvam.cucumber.listener.state;

import com.aventstack.extentreports.ExtentTest;
import cucumber.api.PickleStepTestStep;

public class ExtentStep {
    private PickleStepTestStep step;
    private ExtentTest test;

    public ExtentStep(final PickleStepTestStep step, final ExtentTest test) {
        this.step = step;
        this.test = test;
    }

    public PickleStepTestStep getStep() {
        return step;
    }

    public ExtentTest getTest() {
        return test;
    }
}
