package com.vimalselvam.cucumber.listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.mongodb.MongoClientURI;
import com.vimalselvam.cucumber.listener.state.*;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.api.TestStep;
import cucumber.api.event.*;
import cucumber.api.formatter.Formatter;
import gherkin.ast.*;
import io.atlassian.fugue.Either;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * A cucumber based reporting listener which generates the Extent Report
 */
public class ExtentCucumberFormatter implements Formatter {

    private static ExtentReports extentReports;
    private static KlovReporter klovReporter;
    private static ExtentHtmlReporter htmlReporter;

    @Override
    public void setEventPublisher(final EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
    }

    private void handleTestSourceRead(final TestSourceRead event) {
        ThreadLocalExtentState.modifyState(state -> {
            final TestSourcesModel testSourcesModel = state.getTestSourcesModel();
            testSourcesModel.addTestSourceReadEvent(event.uri, event);
            final Feature feature = testSourcesModel.getFeature(event.uri);
            final ExtentTest test = extentReports.createTest(com.aventstack.extentreports.gherkin.model.Feature.class, escapeHtml4(feature.getName()), escapeHtml4(feature.getDescription()));
            feature.getTags().forEach(tag -> test.assignCategory(tag.getName()));
            final ExtentFeature extentFeature = new ExtentFeature(feature, test);
            state.putFeature(event.uri, extentFeature);

            return state;
        });
    }

    private void handleTestCaseStarted(final TestCaseStarted event) {
        ThreadLocalExtentState.modifyState(state -> {
            final String uri = event.testCase.getUri();
            final ExtentFeature extentFeature = state.getFeature(uri).orElseThrow(() -> new IllegalStateException("Got no feature for test case '" + uri + "'."));
            final TestSourcesModel.AstNode astNode = state.getTestSourcesModel().getAstNode(uri, event.testCase.getLine());
            final ScenarioDefinition scenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
            try {
                final ExtentTest test = extentFeature.getTest().createNode(new GherkinKeyword(scenarioDefinition.getKeyword()), escapeHtml4(scenarioDefinition.getName()), escapeHtml4(scenarioDefinition.getName()));
                Either<Scenario, ScenarioOutline> scenario;
                if (scenarioDefinition instanceof Scenario) {
                    scenario = Either.left((Scenario) scenarioDefinition);
                } else if (scenarioDefinition instanceof ScenarioOutline) {
                    scenario = Either.right((ScenarioOutline) scenarioDefinition);
                } else {
                    throw new IllegalStateException("Unknown test case of type '" + scenarioDefinition.getClass().getCanonicalName() + "'.");
                }
                final List<Tag> tags = scenario.fold(Scenario::getTags, ScenarioOutline::getTags);
                extentFeature.getFeature().getTags().forEach(tag -> test.assignCategory(tag.getName()));
                tags.forEach(tag -> test.assignCategory(tag.getName()));
                final ExtentScenario extentScenario = new ExtentScenario(scenario, test);

                return ExtentState.ExtentStateBuilder
                        .anExtentState(state)
                        .withCurrentFeature(extentFeature)
                        .withCurrentScenario(extentScenario)
                        .build();
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Unknown keyword '" + scenarioDefinition.getKeyword() + "'.");
            }
        });

    }

    private void handleTestStepStarted(final TestStepStarted event) {
        ThreadLocalExtentState.modifyState(state -> {
            final TestStep testStep = event.testStep;
            if (!(testStep instanceof PickleStepTestStep)) {
                return state;
            }
            final PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) testStep;
            final List<Step> steps = state.getCurrentScenario().getScenario().fold(ScenarioDefinition::getSteps, ScenarioDefinition::getSteps);
            final Step foundStep = steps.stream()
                    .filter(step -> step.getLocation().getLine() == pickleStepTestStep.getStepLine())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find step at line " + pickleStepTestStep.getStepLine()));

            try {
                final ExtentTest test = state.getCurrentScenario().getTest().createNode(new GherkinKeyword(foundStep.getKeyword()), escapeHtml4(pickleStepTestStep.getStepText()));
                final ExtentStep currentStep = new ExtentStep(pickleStepTestStep, test);

                return ExtentState.ExtentStateBuilder.anExtentState(state).withCurrentStep(currentStep).build();
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Unknown step keyword '" + foundStep.getKeyword() + "'.");
            }
        });
    }

    private void handleTestStepFinished(final TestStepFinished finished) {
        ThreadLocalExtentState.modifyState(state -> {
            final ExtentStep currentStep = state.getCurrentStep();
            if (currentStep == null) {
                return state;
            }

            final Result.Type status = finished.result.getStatus();
            final ExtentTest test = currentStep.getTest();
            switch (status) {
                case PASSED:
                    test.pass(status.firstLetterCapitalizedName());
                    break;
                case FAILED:
                    final Throwable error = finished.result.getError();
                    final String errorMessage = finished.result.getErrorMessage();
                    if (error != null) {
                        test.error(error);
                    } else if (errorMessage != null) {
                        test.error(errorMessage);
                    } else {
                        test.fail(status.firstLetterCapitalizedName());
                    }
                    break;
                case PENDING:
                case SKIPPED:
                case AMBIGUOUS:
                case UNDEFINED:
                    test.skip(status.firstLetterCapitalizedName());
                    break;
            }

            return ExtentState.ExtentStateBuilder.anExtentState(state).withCurrentStep(null).build();
        });
    }

    private void handleTestRunFinished(final TestRunFinished event) {
        extentReports.flush();
    }

    public ExtentCucumberFormatter(File file) {
        setExtentHtmlReport(file);
        setExtentReport();
        setKlovReport();
    }

    private void setExtentHtmlReport(File file) {
        if (htmlReporter != null) {
            return;
        }
        if (file == null || file.getPath().isEmpty()) {
            file = new File(ExtentProperties.INSTANCE.getReportPath());
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        htmlReporter = new ExtentHtmlReporter(file);
    }

    static ExtentHtmlReporter getExtentHtmlReport() {
        return htmlReporter;
    }

    private void setExtentReport() {
        if (extentReports != null) {
            return;
        }
        extentReports = new ExtentReports();
        ExtentProperties extentProperties = ExtentProperties.INSTANCE;

        // Remove this block in the next release
        if (extentProperties.getExtentXServerUrl() != null) {
            String extentXServerUrl = extentProperties.getExtentXServerUrl();
            try {
                URL url = new URL(extentXServerUrl);
                ExtentXReporter xReporter = new ExtentXReporter(url.getHost());
                xReporter.config().setServerUrl(extentXServerUrl);
                xReporter.config().setProjectName(extentProperties.getProjectName());
                extentReports.attachReporter(htmlReporter, xReporter);
                return;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid ExtentX Server URL", e);
            }
        }
        extentReports.attachReporter(htmlReporter);
    }

    static ExtentReports getExtentReport() {
        return extentReports;
    }

    // /**
    //  * When running cucumber tests in parallel Klov reporter should be attached only once, in order to avoid duplicate builds on klov server.
    //  */
    private synchronized void setKlovReport() {
        if (extentReports == null) {
            //Extent reports object not found. call setExtentReport() first
            return;
        }

        ExtentProperties extentProperties = ExtentProperties.INSTANCE;

        //if reporter is not null that means it is already attached
        if (klovReporter != null) {
            //Already attached, attaching it again will create a new build/klov report
            return;
        }


        if (extentProperties.getKlovServerUrl() != null) {
            String hostname = extentProperties.getMongodbHost();
            int port = extentProperties.getMongodbPort();

            String database = extentProperties.getMongodbDatabase();

            String username = extentProperties.getMongodbUsername();
            String password = extentProperties.getMongodbPassword();

            try {
                //Create a new KlovReporter object
                klovReporter = new KlovReporter();

                if (username != null && password != null) {
                    MongoClientURI uri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + hostname + ":" + port + "/?authSource=" + database);
                    klovReporter.initMongoDbConnection(uri);
                } else {
                    klovReporter.initMongoDbConnection(hostname, port);
                }

                klovReporter.setProjectName(extentProperties.getKlovProjectName());
                klovReporter.setReportName(extentProperties.getKlovReportName());
                klovReporter.setKlovUrl(extentProperties.getKlovServerUrl());

                extentReports.attachReporter(klovReporter);

            } catch (Exception ex) {
                klovReporter = null;
                throw new IllegalArgumentException("Error setting up Klov Reporter", ex);
            }
        }
    }

    static KlovReporter getKlovReport() {
        return klovReporter;
    }
}
