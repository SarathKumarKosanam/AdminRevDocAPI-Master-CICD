package com.rev.admin.utils;
import com.rev.admin.endpoints.AdminEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


/**
 * Manages the multi-step process of acquiring the final St-Access-Token for authenticated API calls.
 */
public class TokenManager {
    // --- Config ---
    private static final String ADMIN_EMAIL = ConfigReader.get("adminEmail");
    private static final String ADMIN_PASSWORD = ConfigReader.get("adminPassword");
    private static final String ADMIN_PHONE = ConfigReader.get("adminPhone"); 
    private static final String TEMP_OTP = ConfigReader.get("tempOtp"); 
    private static final String TEMP_DEVICE_ID = "test-automation-device-id-123"; 

    // --- State Variables ---
    private static String finalSAccessToken;
    // Holds the Front-Token header required to authenticate Steps 2 & 3
    private static String sessionFrontToken; 
    public static String sessionCookie;

    /**
     * Public method to get the final St-Access-Token, initiating the full flow if necessary.
     */


    // Step 1: Login with Email and Password
    public static String[] loginWithEmailAndPassword(String baseUrl) {
        String loginBody = ConfigReader.get("LoginBody");

        System.out.println("Initiating login (Step 1)...");
        Response response = RestAssured.given()
            .baseUri(baseUrl)
            .basePath(AdminEndpoints.LOGIN)
            .header("Accept", "application/json")
            .contentType(ContentType.JSON)
            .body(loginBody)
            .log().all()
            .post()
            .then()
            .log().all()
            .statusCode(200)
            .extract().response();

        // FIX: Extract the Front-Token header for the session flow
        
        if(response.header("Front-Token").isEmpty()) {
        	System.out.print("Front-Token Not Found");
        	
        }
        else
        sessionFrontToken = response.header("Front-Token");
        
        if(response.header("St-Access-Token").isEmpty())
        	System.out.print("Cookie Not Found");
        else
        sessionCookie = response.header("St-Access-Token");
        
        //System.out.println("Token Generated succesfully : " + sessionCookie);
        
        return new String [] {sessionFrontToken,sessionCookie};
    }
    
}
