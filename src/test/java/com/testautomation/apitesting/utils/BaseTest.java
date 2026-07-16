package com.testautomation.apitesting.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.restassured.RestAssured;

public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void beforeMethod() {
        //RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
       // RestAssured.requestSpecification = RestAssured.given().log().all(); //log request details only

        //to log all details of req and response use below
//        RestAssured.filters(
//                new RequestLoggingFilter(),
//                new ResponseLoggingFilter()
        //);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE) {
            Throwable t = result.getThrowable();
            StringWriter error = new StringWriter();
            t.printStackTrace(new PrintWriter(error));
            logger.info(error.toString());
        }

    }

}