package com.rev.admin.base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.ITestResult;
import java.lang.reflect.Method;



import com.rev.admin.utils.ConfigReader;
import com.rev.admin.utils.TokenManager;
import com.rev.admin.reports.ExtentManager;

import com.rev.admin.reports.ExtentTestManager;


import io.restassured.RestAssured;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import com.aventstack.extentreports.Status;
import org.testng.annotations.Test; // Added Test annotation import

public class BaseTest {
	
    
    // CRITICAL: Reads the environment variable set by the GitHub workflow
    // This allows the CI pipeline to inject the target URL (e.g., https://api.stage.us-east-2.api.revdoc.link)
    protected static final String API_BASE_URL = System.getenv("API_BASE_URL") != null ? 
                                               System.getenv("API_BASE_URL") : 
                                               null; // Keep null here to enforce fallback to ConfigReader
	
	public String FrontToken;
	public static String Cookie;

protected static ExtentReports extent;
// Removed: protected static ExtentTest test; // ExtentTest is now managed by ExtentTestManager

// 🔹 Extent Report initialization (runs once per suite)
@BeforeSuite
public void initReport() {
ConfigReader.init();	
extent = ExtentManager.createInstance();
System.out.println("✅ Extent Report initialized successfully");
}
    
    // 🔹 Extent Test setup (runs before every test method)
    @BeforeMethod
    public void startExtentTest(Method method) {
        String testName = method.getName();
        // Extracting description from the @Test annotation if available
        String description = method.getAnnotation(Test.class) != null ? method.getAnnotation(Test.class).description() : testName;
        
        ExtentTestManager.startTest(testName, description);
    }

// 🔹 RestAssured setup (runs before every test class)
 @BeforeSuite
public void setUpFrontTokeAndCookie() {
	    String baseUrl;
	    
	    // 1. Set Base URI dynamically: Use CI/CD variable if present, otherwise fall back to ConfigReader
	    if (API_BASE_URL != null) {
	        baseUrl = API_BASE_URL;
	        System.out.println("Using CI/CD Environment Variable Base URL: " + baseUrl);
	    } else {
	        // Fallback for local execution, reads from config.properties
	        baseUrl = ConfigReader.get("baseUrl"); 
	        System.out.println("Using Local ConfigReader Base URL: " + baseUrl);
	    }
	    RestAssured.baseURI = baseUrl;

// 2. RUN FULL AUTH FLOW AND GET FINAL COOKIE/TOKEN
// This single call triggers the 3-step login if not yet done.
String[] Tokens = TokenManager.loginWithEmailAndPassword(RestAssured.baseURI);

this.FrontToken = Tokens[0];
Cookie = "sAccessToken="+Tokens[1];


}

    // 🔹 Extent Test finalizer (runs after every test method)
    @AfterMethod
    public void endExtentTest(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped: " + result.getSkipCausedBy());
        } else {
            test.log(Status.PASS, "Test Passed");
        }
        // Flushing is handled in @AfterSuite
    }
    
    

// 🔹 Flush report after suite execution
@AfterSuite
public void tearDownReport() {
if (extent != null) {
extent.flush();
System.out.println("📊 Extent Report generated successfully");}
}}