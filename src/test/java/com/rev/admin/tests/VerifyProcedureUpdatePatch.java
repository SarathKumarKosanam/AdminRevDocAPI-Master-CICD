package com.rev.admin.tests;



import com.rev.admin.base.BaseTest;

import com.rev.admin.endpoints.AdminEndpoints;

import com.rev.admin.utils.ApiUtils;

import com.rev.admin.reports.ExtentTestManager; // New Import

import com.aventstack.extentreports.ExtentTest; // New Import

import io.restassured.response.Response;

import org.testng.Assert;

import org.testng.annotations.Test;

import com.rev.admin.utils.ConfigReader;



public class VerifyProcedureUpdatePatch extends BaseTest{


@Test

public void procedureUpdatePatch() {

  ExtentTest test = ExtentTestManager.getTest();

  

  

  test.info("Verify the procedure update via patch ");

  Response ProcedureUpdateResponse = ApiUtils.sendPatch(AdminEndpoints.Procedure_Update,ConfigReader.get("procedureUpdatebody"));




        try {

            Assert.assertEquals(ProcedureUpdateResponse.getStatusCode(), 200, "Procedure patch failed (Status Code).");

            test.pass("ProcedureUpdatePatch Passed and data updated successfully. Status Code: 200.");

        } catch (AssertionError e) {

            test.fail("Step 3 Failed. Expected Status 200, but got " + ProcedureUpdateResponse.getStatusCode() + ". Error: " + e.getMessage());

            throw e;

        }


}


    // New method for partial update
    @Test
    public void procedurePartialUpdatePatch() {
        ExtentTest test = ExtentTestManager.getTest();

        // 1. Log the test information
        test.info("Verify the procedure partial update via PATCH method.");

        // 2. Send the PATCH request with a body containing only the fields to be updated
        Response partialUpdateResponse = ApiUtils.sendPatch(
                AdminEndpoints.Procedure_Update, 
                ConfigReader.get("partialPatchupdate") // Body for partial update
        );

        // 3. Assert the status code
        try {
            Assert.assertEquals(partialUpdateResponse.getStatusCode(), 200, "Procedure partial update failed (Status Code).");
            test.pass("ProcedurePartialUpdatePatch Passed. Status Code: 200.");
        } catch (AssertionError e) {
            test.fail("ProcedurePartialUpdatePatch Failed. Expected Status 200, but got " + partialUpdateResponse.getStatusCode() + ". Error: " + e.getMessage());
            throw e;
        }

        // Optional: Add more assertions to verify the fields were actually updated
        // For example, by sending a GET request after the PATCH.
        // test.info("Verifying the updated resource...");
        // Response getResponse = ApiUtils.sendGet(AdminEndpoints.Procedure_Get_By_Id);
        // Assert.assertTrue(getResponse.getBody().asString().contains("New partial value"), "Partial update not reflected in GET response.");
    }
}