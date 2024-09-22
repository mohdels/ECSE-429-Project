package test.unittests;

import io.restassured.RestAssured;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.BeforeAll;

import java.net.HttpURLConnection;
import java.net.URL;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ToDoUnitTest {
    private boolean deleted = false;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Test that the server is up
    @Test
    void testServer(){
        deleted = false;
        try{
            URL serverUrl = new URL("http://localhost:4567");
            HttpURLConnection serverConnection = (HttpURLConnection) serverUrl.openConnection();

            serverConnection.setRequestMethod("GET");
            int serverResponse = serverConnection.getResponseCode();

            assertEquals(200, serverResponse);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}