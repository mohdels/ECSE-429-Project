package test.unitTests;

import io.restassured.RestAssured;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.BeforeAll;

import java.net.HttpURLConnection;
import java.net.URL;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ProjectUnitTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Test that the server is up and ready for testing
    @BeforeAll
    public static void startServer(){
        int serverResponse = 502;
        try{
            URL serverUrl = new URL("http://localhost:4567");
            HttpURLConnection serverConnection = (HttpURLConnection) serverUrl.openConnection();

            serverConnection.setRequestMethod("GET");
            serverResponse = serverConnection.getResponseCode();

            assertEquals(200, serverResponse);

        } catch(Exception e) {
            e.printStackTrace();
            assertEquals(200, serverResponse);
        }
    }

    @AfterAll
    public static void shutdownServer() {

    }
}