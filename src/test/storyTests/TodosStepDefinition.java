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
import java.util.List;
import java.util.Map;

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

    @Given("the following todos exist in the system:")
    public void theFollowingTodosExistInTheSystem(io.cucumber.datatable.DataTable dataTable) {
        String expectedTitle1 = "scan paperwork";
        String expectedDoneStatus1 = "false";

        // Send a GET request to check if the todo exists by ID
        int id = 1;
        Response response = given()
                .pathParam("id", id)
                .when()
                .get("/todos/{id}");

        // Verify the response status code is 200 (exists)
        assertEquals(200, response.getStatusCode());

        // Verify that the title and doneStatus match what is expected
        String actualTitle = response.jsonPath().getString("todos[0].title");
        String actualDoneStatus = response.jsonPath().getString("todos[0].doneStatus");

        assertEquals(expectedTitle1, actualTitle);
        assertEquals(expectedDoneStatus1, actualDoneStatus);
        assertEquals("", response.jsonPath().getString("todos[0].description"));
    }

    @AfterAll
    public static void shutdownServer() {
        try {
            given().when().get("/shutdown");
        }
        catch (Exception ignored) {}
    }

// -------------------------- CreateTodo.feature--------------------------
    // ---------------------- Normal Flow ----------------------

    @When("I send a POST request to {string} using title: {string} and description: {string}")
    public void iSendAPostRequestWithTitleAndDescription(String endpoint, String title, String description) {
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

    @Then("I should receive a response status code of {int}")
    public void iShouldReceiveResponseStatusCodeForNormalFlow(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("the response should have a todo task with title: {string} and description: {string}")
    public void theResponseShouldHaveTodoWithTitleAndDescriptionForNormalFlow(String expectedTitle, String expectedDescription) {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedDescription, actualDescription);
    }

    // ---------------------- Alternate Flow ----------------------

    @When("I send a POST request to {string} using title: {string} and empty description")
    public void iSendAPostRequestWithTitleOnly(String endpoint, String title) {
        String body = String.format("{\"title\":\"%s\", \"description\":null}", title);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

    @And("the response should have a todo task with title: {string} and empty description")
    public void theResponseShouldHaveTodoWithTitleAndEmptyDescriptionForAlternateFlow(String expectedTitle) {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");
        assertEquals(expectedTitle, actualTitle);
        assertNull(actualDescription);
    }

    // ---------------------- Error Flow ----------------------
    @Then("the response should contain the error message {string}")
    public void theResponseShouldContainErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = response.jsonPath().getString("errorMessages");
        assertEquals(expectedErrorMessage, actualErrorMessage);
        //throw new io.cucumber.java.PendingException();
    }

// -------------------------- GetTodo.feature--------------------------
    // ---------------------- Normal Flow ----------------------
    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = RestAssured.given().get("/" + endpoint);
    }
    // ---------------------- Alternate Flow ----------------------
    @When("I send a GET request to {string} with title parameter {string}")
    public void iSendAGetRequestWithTitleParameter(String endpoint, String title) {
        response = RestAssured.given().queryParam("title", title).get("/" + endpoint);
    }

    @And("the response should contain a todo task with ID {string} and title {string}")
    public void theResponseShouldContainTodoWithTitle(String expectedId, String expectedTitle) {
        String actualId = response.jsonPath().getString("todos[0].id");
        String actualTitle = response.jsonPath().getString("todos[0].title");
        assertEquals(expectedId, actualId);
        assertEquals(expectedTitle, actualTitle);
    }
}

