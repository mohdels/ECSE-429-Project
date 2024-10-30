package test.storyTests;

import static java.lang.Thread.sleep;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class storyTest {

    private Response response;
    private RequestSpecification request;
    private static Process process;

    // Not sure this is how to check if the service is running, I just did the same as unit tests
    // Will look into it later
    @Given("the service is running")
    public void theServiceIsRunning() {
        // To run the api
        try {
            process = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            sleep(500); // to give time for the api to run
        } catch (Exception e) {
            e.printStackTrace();
        }
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Normal Flow
    @When("I execute a normal flow")
    public void normalFlowRequest() {
    }

    @Then("I should receive a normal flow response")
    public void normalFlowResponse() {
    }

    // Alternate Flow
    @When("I execute an alternate flow")
    public void alternateFlowRequest() {
    }

    @Then("I should receive an alternate flow response")
    public void alternateFlowResponse() {
    }

    // Error Flow
    @When("I execute an error flow")
    public void errorFlowRequest() {
    }

    @Then("I should receive an error flow response")
    public void errorFlowResponse() {
    }
}
