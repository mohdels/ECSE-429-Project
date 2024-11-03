package test.storyTests;

import io.cucumber.java.AfterAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class TodosStepDefinition {

    private static Process process;
    private Response response;
    private RequestSpecification request;

    @Given("the service is running")
    public void serviceRunning() {
        RestAssured.baseURI = "http://localhost:4567";
        if (isServiceRunning()) {
            try {
                given().when().get("/shutdown");
            } catch (Exception e) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startService();
        } else {
            startService();
        }
    }

    private boolean isServiceRunning() {
        try {
            URL serviceUrl = new URL("http://localhost:4567");
            HttpURLConnection serviceConnection = (HttpURLConnection) serviceUrl.openConnection();
            serviceConnection.setRequestMethod("GET");
            serviceConnection.connect();
            int serviceResponseCode = serviceConnection.getResponseCode();
            return serviceResponseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void startService() {
        try {
            process = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            sleep(500); // to give time for the api to run
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @AfterAll
//    public static void shutdownServer() {
//        try {
//            given().when().get("/shutdown");
//        }
//        catch (Exception ignored) {}
//    }

    // ---------------------- Normal Flow ----------------------

    // Normal Flow: Sending a POST request with title and description
    @When("I send a POST request to {string} using title: {string} and description: {string}")
    public void iSendAPostRequestWithTitleAndDescription(String endpoint, String title, String description) {
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

    // Normal Flow: Verify response status code for successful creation
    @Then("I should receive a response status code of {int} for normal flow")
    public void iShouldReceiveResponseStatusCodeForNormalFlow(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // Normal Flow: Verify response title and description for created todo
    @And("the response should have a todo task with title: {string} and description: {string} for normal flow")
    public void theResponseShouldHaveTodoWithTitleAndDescriptionForNormalFlow(String expectedTitle, String expectedDescription) {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");
        assertEquals("Title does not match", expectedTitle, actualTitle);
        assertEquals("Description does not match", expectedDescription, actualDescription);
    }

    // ---------------------- Alternate Flow ----------------------

    // Alternate Flow: Sending a POST request with title only (description is empty)
    @When("I send a POST request to {string} using title: {string} and empty description")
    public void iSendAPostRequestWithTitleOnly(String endpoint, String title) {
        String body = String.format("{\"title\":\"%s\", \"description\":null}", title);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

    // Alternate Flow: Verify response status code for successful creation
    @Then("I should receive a response status code of {int} for alternate flow")
    public void iShouldReceiveResponseStatusCodeForAlternateFlow(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // Alternate Flow: Verify response title and description (null or empty) for created todo
    @And("the response should have a todo task with title: {string} and empty description for alternate flow")
    public void theResponseShouldHaveTodoWithTitleAndEmptyDescriptionForAlternateFlow(String expectedTitle) {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");
        assertEquals("Title does not match", expectedTitle, actualTitle);
        assertNull("Description should be null or empty", actualDescription);
    }

    // ---------------------- Error Flow ----------------------

    // Error Flow: Send POST request without a title to trigger validation error
    @When("I send a POST request to {string} using title: \"\" and description: {string}")
    public void iSendAPostRequestWithoutTitle(String endpoint, String description) {
        String body = String.format("{\"title\":\"\", \"description\":\"%s\"}", description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

    // Error Flow: Verify response status code for validation error
    @Then("I should receive a response status code of {int} for error flow")
    public void iShouldReceiveResponseStatusCodeForErrorFlow(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // Error Flow: Validation for error message in the response
    @Then("the response should contain the error message {string}")
    public void theResponseShouldContainErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = response.jsonPath().getString("error.message");
        assertEquals("Error message does not match", expectedErrorMessage, actualErrorMessage);
    }
}

