# Cucumber Extent Reporter

This tool helps you to generate the custom cucumber-jvm report using ExtentReports plugin.

The [ExtentReports](http://extentreports.relevantcodes.com/) plugin is developed by Anshoo Arora. This is one of the best reporting plugin available for testing world. This plugin can be used with any Test Apis.

## Build Status
[![Build Status](https://travis-ci.org/email2vimalraj/CucumberExtentReporter.svg?branch=master)](https://travis-ci.org/email2vimalraj/CucumberExtentReporter)

## Usage
If you are using a maven based project, you can directly add this library as a dependency:

```
<dependency>
    <groupId>com.vimalselvam</groupId>
    <artifactId>cucumber-extentsreport</artifactId>
    <version>2.0.0</version>
</dependency>
```

Please note that adding the dependency of ExtentReport v3.0.2+ is mandatory.

If not, download the jar from [here](http://search.maven.org/#search%7Cga%7C1%7Ccucumber-extentsreport).

## Release Notes
For more details, look at [Changelog](Changelog.md).

## Getting Started

### Runner Class example:
Create a runner class and add the `com.cucumber.listener.ExtentCucumberFormatter` as a plugin followed by the report file as input.

A sample example is show below:

```java
package com.cucumber.runner;

import com.cucumber.listener.Reporter;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * A sample test to demonstrate
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"src/test/resources/features"},
    glue = {"com.cucumber.stepdefinitions"},
    plugin = {"com.cucumber.listener.ExtentCucumberFormatter:output/report.html"}
)
public class RunCukesTest {
    @AfterClass
    public static void setup() {
        Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
        Reporter.setSystemInfo("os", "Mac OSX");
        Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}

```

The above example shows a JUnit runner. However, you can use the TestNG runner too.
Also make sure the `loadXMLConfig`, `setSystemInfo` and `setTestRunnerOutput` methods should be in your `@AfterClass` method.

### Logging
User can add logs at any step and those logs will be captured and attached to the corresponding step. The log should be added as follows:

```java
Reporter.addStepLog("Step Log message goes here");
```

In case any log to be added at the scenario level, the following can be done:

```java
Reporter.addScenarioLog("Scenario Log message goes here");
```

### Adding screenshot / screen cast
The screenshot or screen cast can be added from any of the step as follows:

```java
Reporter.addScreenCaptureFromPath("absolute screenshot path");
Reporter.addScreenCastFromPath("absolute screen cast path");
```

## Demo
[Report](report.html)

Fore more details, kindly visit [Cucumber Extent Reporter](http://www.vimalselvam.com/cucumber-extent-reporter/).
