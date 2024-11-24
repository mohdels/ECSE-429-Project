package test.java.storyTests;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
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
            sleep(750); // to give time for the api to run
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

        String expectedTitle2 = "file paperwork";
        String expectedDoneStatus2 = "false";

        // Send a GET request to check if the todo exists by ID
        int id2 = 2;
        Response response2 = given()
                .pathParam("id", id2)
                .when()
                .get("/todos/{id}");

        // Verify the response status code is 200 (exists)
        assertEquals(200, response2.getStatusCode());

        // Verify that the title and doneStatus match what is expected
        String actualTitle2 = response2.jsonPath().getString("todos[0].title");
        String actualDoneStatus2 = response2.jsonPath().getString("todos[0].doneStatus");

        assertEquals(expectedTitle2, actualTitle2);
        assertEquals(expectedDoneStatus2, actualDoneStatus2);
        assertEquals("", response2.jsonPath().getString("todos[0].description"));
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
    public void iShouldReceiveResponseStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the response should have a todo task with title: {string} and description: {string}")
    public void theResponseShouldHaveTodoWithTitleAndDescription(String expectedTitle, String expectedDescription) {
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

    @Then("the response should have a todo task with title: {string} and empty description")
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

    @Then("the response should contain a todo task with ID {string} and title {string}")
    public void theResponseShouldContainTodoWithTitle(String expectedId, String expectedTitle) {
        String actualId = response.jsonPath().getString("todos[0].id");
        String actualTitle = response.jsonPath().getString("todos[0].title");
        assertEquals(expectedId, actualId);
        assertEquals(expectedTitle, actualTitle);
    }

// -------------------------- UpdateTodo.feature--------------------------
    // ---------------------- Normal Flow ----------------------
    @When("I send a PUT request to {string} using title: {string} and description: {string}")
    public void iSendAPutRequestToUpdateTodo(String endpoint, String title, String description) {
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.put("/" + endpoint);
    }
// -------------------------- DeleteTodo.feature--------------------------
    // ---------------------- Normal Flow ----------------------
    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = RestAssured.given().delete("/" + endpoint);
    }

    // ---------------------- Alternate Flow ----------------------
    @When("I send a POST request to {string} using title: {string} and description: {string} then delete the todo")
    public void iSendAPostRequestToCreateTodoThenDelete(String endpoint, String title, String description) {
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);

        // Assuming the response contains the ID of the created Todo, extract it
        String id = response.jsonPath().getString("id");

        // Now send a DELETE request to delete this newly created Todo
        response = RestAssured.given().delete("/todos/" + id);
    }

    @Then("the todo at {string} should be deleted")
    public void theTodoShouldBeDeleted(String endpoint) {
        response = RestAssured.given().get("/" + endpoint);
        assertEquals(404, response.getStatusCode());
    }

// -------------------------- GetAllTodos.feature--------------------------
    // ---------------------- Alternate Flow ----------------------
    @When("I send a GET request to {string} using filter {string}")
    public void iSendAGetRequestWithFilter(String endpoint, String filter) {
        response = RestAssured.given().get("/" + endpoint + filter);
    }

    @Then("the response should contain a list of todos")
    public void theResponseShouldContainAListOfTodos() {
        List<Map<String, Object>> todos = response.jsonPath().getList("todos");
        assertNotNull(todos);
        assertFalse(todos.isEmpty());
    }

    @Then("the list should include todos with the following details:")
    public void theListShouldIncludeTodosWithDetails(DataTable expectedTodos) {
        List<Map<String, String>> expectedTodosList = expectedTodos.asMaps(String.class, String.class);
        List<Map<String, Object>> actualTodosList = response.jsonPath().getList("todos");

        assertEquals(expectedTodosList.size(), actualTodosList.size());

        if (actualTodosList.get(0).get("id").equals("2")) {
            Collections.swap(actualTodosList, 0, 1);
        }

        for (int x = 0; x < expectedTodosList.size(); x++){
            assertEquals(expectedTodosList.get(x).get("id"), actualTodosList.get(x).get("id"));
            assertEquals(expectedTodosList.get(x).get("title"), actualTodosList.get(x).get("title"));
            assertEquals(expectedTodosList.get(x).get("title"), actualTodosList.get(x).get("title"));
        }
    }
}

