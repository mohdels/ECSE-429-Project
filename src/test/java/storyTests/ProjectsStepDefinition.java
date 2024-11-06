package test.java.storyTests;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectsStepDefinition {

    private static Process process;
    private Response response;
    private RequestSpecification request;

    @Given("the following projects exist in the system:")
    public void theFollowingTodosExistInTheSystem(io.cucumber.datatable.DataTable dataTable) {
        String expectedTitle = "Office Work";
        String expectedActiveStatus = "false";

        // Send a GET request to check if the todo exists by ID
        int id = 1;
        Response response = given()
                .pathParam("id", id)
                .when()
                .get("/projects/{id}");

        // Verify the response status code is 200 (exists)
        assertEquals(200, response.getStatusCode());

        // Verify that the title and doneStatus match what is expected
        String actualTitle = response.jsonPath().getString("projects[0].title");
        String actualActiveStatus = response.jsonPath().getString("projects[0].active");

        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedActiveStatus, actualActiveStatus);
        assertEquals("", response.jsonPath().getString("projects[0].description"));
    }

    @AfterAll
    public static void shutdownServer() {
        try {
            given().when().get("/shutdown");
        }
        catch (Exception ignored) {}
    }

// -------------------------- CreateProject.feature--------------------------
    // -------------------------- Normal Flow --------------------------

    @When("I send a POST request to {string} using title: {string} and active status: {string} to create a project")
    public void iSendAPostRequestToCreateProjectWithTitleAndActiveStatus(String endpoint, String title, String active) {
        // Build the request body with the provided title and active status
        String body = String.format("{\"title\":\"%s\", \"active\":%s}", title, active);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post(endpoint);
    }

    @Then("We should receive a response status code of {int}")
    public void iShouldReceiveResponseStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the response should contain a project with title: {string} and active status: {string}")
    public void theResponseShouldContainProjectWithTitleAndActiveStatus(String expectedTitle, String expectedActive) {
        String actualTitle = response.jsonPath().getString("title");
        String actualActiveStatus = response.jsonPath().getString("active");
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedActive, actualActiveStatus);
    }

    // -------------------------- Alternate Flow --------------------------

    @When("I send a POST request to {string} with no fields in the body")
    public void iSendAPostRequestToCreateProjectWithNoFieldsInTheBody(String endpoint) {
        // Send an empty request body
        request = RestAssured.given().header("Content-Type", "application/json").body("{}");
        response = request.post(endpoint);
    }

    @Then("the response should contain a project with default values for all fields")
    public void theResponseShouldContainProjectWithDefaultValuesForAllFields() {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");
        String actualActiveStatus = response.jsonPath().getString("active");

        // Assuming default values for a project are an empty title, an empty description, and `active` set to false
        assertEquals("", actualTitle);  // Assuming the default title is empty
        assertEquals("", actualDescription);  // Assuming the default description is empty
        assertEquals("false", actualActiveStatus);  // Assuming the default active status is false
    }

    // -------------------------- Error Flow --------------------------

    @When("I send a POST request to {string} using an active status: {string}")
    public void iSendAPostRequestToCreateProjectWithInvalidActiveStatus(String endpoint, String activeStatus) {
        // Build the request body with an invalid active status
        String body = String.format("{\"title\":\"Invalid Project\", \"active\":\"%s\"}", activeStatus);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post(endpoint);
    }

    @Then("the response should contain the following error message: {string}")
    public void theResponseShouldContainErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = response.jsonPath().getString("errorMessages[0]");
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

// -------------------------- GetAllProjects.feature--------------------------
    // ---------------------- Normal Flow ----------------------
    @When("We send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        response = RestAssured.given().get("/" + endpoint);
    }

    @Then("the response should contain a list of projects")
    public void theResponseShouldContainAListOfProjects() {
        List<Map<String, Object>> projects = response.jsonPath().getList("projects");
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
    }

    @Then("the list should include projects with the following details:")
    public void theListShouldIncludeProjectsWithDetails(DataTable expectedProjects) {
        List<Map<String, String>> expectedProjectsList = expectedProjects.asMaps(String.class, String.class);
        List<Map<String, Object>> actualProjectsList = response.jsonPath().getList("projects");

        assertEquals(expectedProjectsList.size(), actualProjectsList.size(), "Project list size does not match");

        if (actualProjectsList.get(0).get("id").equals("2")) {
            Collections.swap(actualProjectsList, 0, 1);
        }

        for (int x = 0; x < expectedProjectsList.size(); x++) {
            assertEquals(expectedProjectsList.get(x).get("id"), actualProjectsList.get(x).get("id").toString(), "Project ID does not match");
            assertEquals(expectedProjectsList.get(x).get("title"), actualProjectsList.get(x).get("title"), "Project title does not match");
            assertEquals(expectedProjectsList.get(x).get("active"), actualProjectsList.get(x).get("active").toString(), "Project active status does not match");
        }
    }

    // ---------------------- Alternate Flow ----------------------
    @When("I send a GET request to {string} using filter {string} to get projects")

    public void iSendAGetRequestWithFilter(String endpoint, String filter) {
        response = RestAssured.given().get("/" + endpoint + filter);
    }

// -------------------------- GetProject.feature--------------------------
    // -------------------------- Alternate Flow --------------------------

    @Then("the response should contain a project with ID {string} and title {string}")
    public void theResponseShouldContainProjectWithIdAndTitle(String expectedId, String expectedTitle) {
        String actualId = response.jsonPath().getString("projects[0].id");
        String actualTitle = response.jsonPath().getString("projects[0].title");

        assertEquals(expectedId, actualId, "Project ID does not match");
        assertEquals(expectedTitle, actualTitle, "Project title does not match");
    }

    // -------------------------- Alternate Flow --------------------------

    @When("We send a GET request to {string} with title parameter {string} to get a project")
    public void iSendAGetRequestWithTitleParameter(String endpoint, String title) {
        response = given()
                .queryParam("title", title)
                .when()
                .get("/" + endpoint);
    }

// -------------------------- UpdateProject.feature--------------------------
    // -------------------------- Normal Flow --------------------------

    @When("I send a PUT request to {string} using title: {string} and description: {string} to update project")
    public void iSendAPutRequestToUpdateProject(String endpoint, String title, String description) {
        // Create the request body with the provided title and description
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.put("/" + endpoint);
    }

    @Then("the response should have a project with title: {string} and description: {string}")
    public void theResponseShouldHaveProjectWithTitleAndDescription(String expectedTitle, String expectedDescription) {
        String actualTitle = response.jsonPath().getString("title");
        String actualDescription = response.jsonPath().getString("description");

        assertEquals(expectedTitle, actualTitle, "Title does not match");
        assertEquals(expectedDescription, actualDescription, "description does not match");
    }

    // -------------------------- Alternate Flow --------------------------

    @When("I send a POST request to {string} using title: {string} and description: {string} to update project")
    public void iSendAPostRequestToUpdateProject(String endpoint, String title, String description) {
        // Create the request body with the provided title and description
        String body = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);
    }

// -------------------------- DeleteProject.feature--------------------------
    // -------------------------- Normal Flow --------------------------

    @Then("the project at {string} should be deleted")
    public void theProjectAtEndpointShouldBeDeleted(String endpoint) {
        // Verify that the project no longer exists by sending a GET request
        response = given().when().get(endpoint);
        assertEquals(404, response.getStatusCode(), "Project at " + endpoint + " should have been deleted.");
    }

    // -------------------------- Alternate Flow --------------------------

    @When("I send a POST request to {string} using title: {string} and active status: {string} then delete the project")
    public void iSendAPostRequestToCreateProjectWithTitleAndActiveStatusThenDelete(String endpoint, String title, String activeStatus) {
        // Build the request body with the provided title and active status
        String body = String.format("{\"title\":\"%s\", \"active\":%s}", title, activeStatus);
        request = RestAssured.given().header("Content-Type", "application/json").body(body);
        response = request.post("/" + endpoint);

        String newProjectId = response.jsonPath().getString("id");
        String deleteEndpoint = "/" + endpoint + "/" + newProjectId;
        response = RestAssured.given().delete(deleteEndpoint);
    }

    // -------------------------- Error Flow --------------------------

    @Given("a project with ID {int} has already been deleted")
    public void aProjectWithIDHasAlreadyBeenDeleted(int deletedId) {
        // Simulate that the project has already been deleted by sending a DELETE request
        String deleteEndpoint = "/projects/" + deletedId;
        given().header("Content-Type", "application/json").delete(deleteEndpoint);
    }
}

