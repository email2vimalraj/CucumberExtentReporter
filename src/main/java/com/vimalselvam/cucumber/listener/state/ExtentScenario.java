package com.vimalselvam.cucumber.listener.state;

import com.aventstack.extentreports.ExtentTest;
import gherkin.ast.Scenario;
import gherkin.ast.ScenarioOutline;
import io.atlassian.fugue.Either;

public class ExtentScenario {
    private Either<Scenario, ScenarioOutline> scenario;
    private ExtentTest test;

    public ExtentScenario(final Either<Scenario, ScenarioOutline> scenario, final ExtentTest test) {
        this.scenario = scenario;
        this.test = test;
    }

    public Either<Scenario, ScenarioOutline> getScenario() {
        return scenario;
    }

    public ExtentTest getTest() {
        return test;
    }
}
