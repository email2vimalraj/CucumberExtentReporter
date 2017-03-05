package com.cucumber.listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * A cucumber based reporting listener which generates the Extent Report
 */
public class ExtentCucumberFormatter implements Reporter, Formatter {
    private static ThreadLocal<ExtentReports> reportsThreadLocal = new ThreadLocal<ExtentReports>();
    private static ThreadLocal<ExtentHtmlReporter> htmlReporterThreadLocal =
        new ThreadLocal<ExtentHtmlReporter>();
    private static ThreadLocal<ExtentTest> featureTestThreadLocal = new ThreadLocal<ExtentTest>();
    private static ThreadLocal<ExtentTest> scenarioOutlineThreadLocal =
        new ThreadLocal<ExtentTest>();
    static ThreadLocal<ExtentTest> scenarioThreadLocal = new ThreadLocal<ExtentTest>();
    private static ThreadLocal<LinkedList<Step>> stepListThreadLocal =
        new ThreadLocal<LinkedList<Step>>();
    static ThreadLocal<ExtentTest> stepTestThreadLocal = new ThreadLocal<ExtentTest>();
    private boolean scenarioOutlineFlag;

    public ExtentCucumberFormatter(File file) {
        setExtentHtmlReport(new ExtentHtmlReporter(file));
        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(getExtentHtmlReport());
        setExtentReport(extentReports);
        stepListThreadLocal.set(new LinkedList<Step>());
        scenarioOutlineFlag = false;
    }

    private static void setExtentHtmlReport(ExtentHtmlReporter htmlReport) {
        htmlReporterThreadLocal.set(htmlReport);
    }

    static ExtentHtmlReporter getExtentHtmlReport() {
        return htmlReporterThreadLocal.get();
    }

    private static void setExtentReport(ExtentReports extentReports) {
        reportsThreadLocal.set(extentReports);
    }

    static ExtentReports getExtentReport() {
        return reportsThreadLocal.get();
    }

    public void syntaxError(String state, String event, List<String> legalEvents, String uri,
        Integer line) {

    }

    public void uri(String uri) {

    }

    public void feature(Feature feature) {
        featureTestThreadLocal.set(getExtentReport().createTest(feature.getName()));
        ExtentTest test = featureTestThreadLocal.get();

        for (Tag tag : feature.getTags()) {
            test.assignCategory(tag.getName());
        }
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        scenarioOutlineFlag = true;
        ExtentTest node = featureTestThreadLocal.get()
            .createNode(scenarioOutline.getKeyword() + ": " + scenarioOutline.getName());
        scenarioOutlineThreadLocal.set(node);
    }

    public void examples(Examples examples) {
        ExtentTest test = scenarioOutlineThreadLocal.get();

        String[][] data = null;
        List<ExamplesTableRow> rows = examples.getRows();
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            ExamplesTableRow examplesTableRow = rows.get(i);
            List<String> cells = examplesTableRow.getCells();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j);
            }
        }
        test.info(MarkupHelper.createTable(data));
    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
        if (scenarioOutlineFlag) {
            scenarioOutlineFlag = false;
        }

//        if (scenario.getKeyword().trim().equalsIgnoreCase("Scenario")) {
//            scenarioOutlineThreadLocal.set(null);
//        }

        ExtentTest scenarioNode;
        if (scenarioOutlineThreadLocal.get() != null && scenario.getKeyword().trim()
            .equalsIgnoreCase("Scenario Outline")) {
            scenarioNode = scenarioOutlineThreadLocal.get()
                .createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,
                    scenario.getName());
        } else {
            scenarioNode = featureTestThreadLocal.get()
                .createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,
                    scenario.getName());
        }

        for (Tag tag : scenario.getTags()) {
            scenarioNode.assignCategory(tag.getName());
        }
        scenarioThreadLocal.set(scenarioNode);
    }

    public void background(Background background) {

    }

    public void scenario(Scenario scenario) {

    }

    public void step(Step step) {
        if (scenarioOutlineFlag) {
            return;
        }
        stepListThreadLocal.get().add(step);
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {

    }

    public void done() {
        getExtentReport().flush();
    }

    public void close() {

    }

    public void eof() {

    }

    public void before(Match match, Result result) {

    }

    public void result(Result result) {
        if (scenarioOutlineFlag) {
            return;
        }

        if (Result.PASSED.equals(result.getStatus())) {
            stepTestThreadLocal.get().pass(Result.PASSED);
        } else if (Result.FAILED.equals(result.getStatus())) {
            stepTestThreadLocal.get().fail(result.getError());
        } else if (Result.SKIPPED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.SKIPPED.getStatus());
        } else if (Result.UNDEFINED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.UNDEFINED.getStatus());
        }
    }

    public void after(Match match, Result result) {

    }

    public void match(Match match) {
        Step step = stepListThreadLocal.get().poll();
        ExtentTest scenarioTest = scenarioThreadLocal.get();
        GherkinKeyword keyword = null;
        try {
            keyword = new GherkinKeyword(step.getKeyword().trim());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ExtentTest stepTest = scenarioTest.createNode(keyword, step.getName());
        stepTestThreadLocal.set(stepTest);
    }

    public void embedding(String mimeType, byte[] data) {

    }

    public void write(String text) {

    }
}
