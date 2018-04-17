package com.cucumber.listener;

import java.io.File;

/**
 * An enum which holds the properties to be set for extent reporter
 */
public enum ExtentProperties {
    INSTANCE;
    private String reportPath;
    private String extentXServerUrl;
    private String projectName;

    //Klov properties
    private String klovServerUrl;
    private String klovProjectName;
    private String klovReportName;
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbDatabase;
    private String mongodbUsername;
    private String mongodbPassword;

    ExtentProperties() {
        this.reportPath = "output" + File.separator + "Run_" + System.currentTimeMillis() + File.separator
                + "report.html";
        this.projectName = "default";
    }

    /**
     * Gets the report path
     * @return The report path
     */
    public String getReportPath() {
        return reportPath;
    }

    /**
     * Sets the report path
     * @param reportPath The report path value
     */
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * Gets the ExtentX server URL
     * @return The ExtentX server URL
     */
    public String getExtentXServerUrl() {
        return extentXServerUrl;
    }

    /**
     * Sets the ExtentX server URL
     * @param extentXServerUrl The ExtentX server URL
     */
    public void setExtentXServerUrl(String extentXServerUrl) {
        this.extentXServerUrl = extentXServerUrl;
    }

    /**
     * Gets the project name
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the project name
     * @param projectName The project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the Klov server URL
     * @return The Klov server URL
     */
    public String getKlovServerUrl() {
        return klovServerUrl;
    }

    /**
     * Sets the Klov server URL
     * @param klovServerUrl The Klov server URL
     */
    public void setKlovServerUrl(String klovServerUrl) {
        this.klovServerUrl = klovServerUrl;
    }

    /**
     * Gets the Klov project name
     * @return The Klov project name
     */
    public String getKlovProjectName() {
        return klovProjectName;
    }

    /**
     * Sets the Klov project name
     * @param klovProjectName The Klov project name
     */
    public void setKlovProjectName(String klovProjectName) {
        this.klovProjectName = klovProjectName;
    }

    /**
     * Gets the Klov report name
     * @return The Klov report name
     */
    public String getKlovReportName() {
        return klovReportName;
    }

    /**
     * Sets the Klov report name
     * @param klovReportName The Klov report name
     */
    public void setKlovReportName(String klovReportName) {
        this.klovReportName = klovReportName;
    }

    /**
     * Gets if all the results are added to single klov build.
     * @return true if all tests to be added to single klov build and false if one build per thread will be created
     */
    public String getMongodbHost() {
        return mongodbHost;
    }

    /**
     * Sets the mongoDB server hostname
     * @param mongodbHost The mongoDB server hostname
     */
    public void setMongodbHost(String mongodbHost) {
        this.mongodbHost = mongodbHost;
    }

    /**
     * Gets the mongoDB server port number
     * @return The mongoDB server port number
     */
    public int getMongodbPort() {
        return mongodbPort;
    }

    /**
     * Sets the mongoDB server port number
     * @param mongodbPort The mongoDB server port number
     */
    public void setMongodbPort(int mongodbPort) {
        this.mongodbPort = mongodbPort;
    }

    /**
     * Gets the mongoDB database name to store Klov reports
     * @return The mongoDB database name to store Klov reports
     */
    public String getMongodbDatabase() {
        return mongodbDatabase;
    }

    /**
     * Sets the mongoDB database name to store Klov reports
     * @param mongodbDatabase The mongoDB database name to store Klov reports
     */
    public void setMongodbDatabase(String mongodbDatabase) {
        this.mongodbDatabase = mongodbDatabase;
    }

    /**
     * Gets the mongoDB username
     * @return The mongoDB username
     */
    public String getMongodbUsername() {
        return mongodbUsername;
    }

    /**
     * Sets the mongoDB username.
     * No need to set username if mongoDB is not running in --auth mode
     * @param mongodbUsername The mongoDB username
     */
    public void setMongodbUsername(String mongodbUsername) {
        this.mongodbUsername = mongodbUsername;
    }

    /**
     * Gets the mongoDB user password
     * @return The mongoDB user password
     */
    public String getMongodbPassword() {
        return mongodbPassword;
    }

    /**
     * Sets the mongoDB user password
     * No need to set password if mongoDB is not running in --auth mode
     * @param mongodbPassword The mongoDB user password
     */
    public void setMongodbPassword(String mongodbPassword) {
        this.mongodbPassword = mongodbPassword;
    }
}
