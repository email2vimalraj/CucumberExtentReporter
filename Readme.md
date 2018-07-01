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
    <version>3.1.0</version>
</dependency>
```

Please note that **Java 8+** and adding the dependency of **ExtentReport v3.1.1+** is mandatory.

I've been receiving queries often that the extent report class not found. As I mentioned in my last statement, you will have to explicitly add ExtentReport as a maven dependency. To do that, paste the following in your pom.xml:

```
<dependency>
    <groupId>com.aventstack</groupId>
    <artifactId>extentreports</artifactId>
    <version>3.1.1</version>
</dependency>
```

If you are not using maven, download the jar from [here](http://search.maven.org/#search%7Cga%7C1%7Ccucumber-extentsreport).

## Release Notes
For more details, look at [Changelog](Changelog.md).

## Getting Started

### Runner Class example:
Create a runner class and add the `com.cucumber.listener.ExtentCucumberFormatter:output/report.html` as a plugin followed by the report file as input (**optional parameter**).

A sample example is shown below:

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
    public static void teardown() {
        Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
        Reporter.setSystemInfo("os", "Mac OSX");
        Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}

```

The above setup will generate the report in `output` directory with the name of `report.html`. There are 3 ways to configure the report location:

- As shown above, pass the report file path along with `com.cucumber.listener.ExtentCucumberFormatter:`
- If in case you want a dynamic location, you can leave the file path parameter empty while configuring the plugin. For example:
    `plugin = {"com.cucumber.listener.ExtentCucumberFormatter:"}`
    This will generate the report file in the location `output/Run_<Current Time Stamp>/report.html`.
- You can also configure the report location by using `ExtentProperties` enum as follows. Leave the plugin configuration empty, and configure the report location in your `@BeforeClass` method:
    
    ```java
    plugin = {"com.cucumber.listener.ExtentCucumberFormatter:"}
    ......
    ......
    @BeforeClass
    public static void setup() {
        ExtentProperties extentProperties = ExtentProperties.INSTANCE;
        extentProperties.setReportPath("output/myreport.html");
    }
    ```

The above example shows a JUnit runner. However, you can use the TestNG runner too. Refer more examples [here](https://github.com/email2vimalraj/CucumberExtentReporter/tree/master/src/test/java/com/cucumber/runner). 
Also make sure the `loadXMLConfig`, `setSystemInfo` and `setTestRunnerOutput` methods should be in your `@AfterClass` method.

### Setting up ExtentX
Since ExtentReport does not support ExtentX anymore, we have deprecated the ExtentX and added support for [Klov](#setting-up-klov). Thanks to (@msingh3)[https://github.com/msingh3] for the contribution.

### Setting up Klov
The current release added a support for `Klov`. The results of your execution can be sent to `Klov` dashboard.

The `Klov` configurations can be set up using `ExtentProperties` enum as follows:

```java
plugin = {"com.cucumber.listener.ExtentCucumberFormatter:"}
......
......
@BeforeClass
public static void setup() {
    ExtentProperties extentProperties = ExtentProperties.INSTANCE;
    extentProperties.setKlovServerUrl("http://localhost");
    // specify project
    // ! you must specify a project, other a "Default project will be used"
    extentProperties.setKlovProjectName("MyProject");
    // you must specify a reportName otherwise a default timestamp will be used
    extentProperties.setKlovReportName("TestReport");
    
    // Mongo DB Configuration
    extentProperties.setMongodbHost("localhost");
    extentProperties.setMongodbPort("27017");
    extentProperties.setMongodbDatabase("klov");
    
    // If mongo Db is running in Authentication mode provide username and password 
    extentProperties.setMongodbUsername("username");
    extentProperties.setMongodbPassword("password");
}
```

You can set up `Klov` on local using: [Klov Demo](http://extentreports.com/docs/klov/)

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
The screenshot or screen cast can be added from any of the step as follows. Please note that the plugin will not take the screenshot, instead it helps you to attach the screenshot file which should be already available in the mentioned path. If you are looking for on how to take the screenshot using Selenium, please refer [this](http://www.seleniumeasy.com/selenium-tutorials/take-screenshot-with-selenium-webdriver):

```java
Reporter.addScreenCaptureFromPath("absolute screenshot path");
Reporter.addScreenCastFromPath("absolute screen cast path");
```

### Assigning Authors to the Scenario
You can assign authors to the scenario using `Reporter.assignAuthor("author1", "author2", ...);`. 

Ideally this should be added at the hooks level. For example, the following sample shows how to assign author to a particular scenario using `Before` hook:

```java
@Before
public void beforeScenario(Scenario scenario) {
    if (scenario.getName().equals("Some scenario name")) {
        Reporter.assignAuthor("Author1");
    }
}
```

## Demo
[Report](http://vimalselvam.com/cucumberextentreport/report.html)

Fore more details, kindly visit [http://www.vimalselvam.com/cucumber-extent-reporter/](http://www.vimalselvam.com/cucumber-extent-reporter/).
