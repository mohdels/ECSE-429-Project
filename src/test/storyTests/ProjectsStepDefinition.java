package test.storyTests;

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

public class ProjectsStepDefinition {

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

    @AfterAll
    public static void shutdownServer() {
        try {
            given().when().get("/shutdown");
        }
        catch (Exception ignored) {}
    }

// -------------------------- CreateProject.feature--------------------------
    // ---------------------- Normal Flow ----------------------



    // ---------------------- Alternate Flow -------------------



    // ---------------------- Error Flow -----------------------



// -------------------------- ViewProjects.feature--------------------------
    // ---------------------- Normal Flow ----------------------



    // ---------------------- Alternate Flow -------------------



    // ---------------------- Error Flow -----------------------



// -------------------------- FilterProjects.feature--------------------------
    // ---------------------- Normal Flow ----------------------



    // ---------------------- Alternate Flow ----------------------



    // ---------------------- Error Flow ----------------------



// -------------------------- UpdateProject.feature--------------------------
    // ---------------------- Normal Flow ----------------------



    // ---------------------- Alternate Flow ----------------------



    // ---------------------- Error Flow ----------------------



// -------------------------- DeleteProject.feature--------------------------
    // ---------------------- Normal Flow ----------------------



    // ---------------------- Alternate Flow ----------------------



    // ---------------------- Error Flow ----------------------



}

