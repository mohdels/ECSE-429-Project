package test.unittests;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.Random;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ToDoUnitTest {
    private int testId;
    private final String taskTitle = "Title Todo";
    private final String taskDescription = "Description of todo";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }

    // Test that the server is up and ready for testing
    @BeforeAll
    public static void testServer(){
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

    // Create a todo before each test runs
    @BeforeEach
    public void createToDo() {
        Map<String, String> testParams = new HashMap<>();
        testParams.put("title", taskTitle);
        testParams.put("description", taskDescription);
        Response response = given().contentType("application/json").body(testParams).when().post("/todos");
        String title = response.jsonPath().getString("title");
        String doneStatus = response.jsonPath().getString("doneStatus");
        String description = response.jsonPath().getString("description");
        assertEquals(201, response.getStatusCode());
        assertEquals(taskTitle, title);
        assertEquals(taskDescription, description);
        assertEquals("false", doneStatus);

        testId = response.jsonPath().getInt("id"); // get the id of the newly created task
    }

    // deletes the created todo after each test terminates
    @AfterEach
    public void deleteToDo(){
        Response response = given().pathParam("id", testId).when().delete("/todos/{id}");
        assertEquals(200, response.getStatusCode());
    }
    @Test
    public void testGetDocs(){

    }

    @Test
    public void testGetAllTodosJson() {
        Response response = given().when().get("/todos");
        List<Map<String, Object>> todos = response.jsonPath().getList("todos");
        boolean found = false;
        for (Map<String, Object> todo : todos) {
            if (taskTitle.equals(todo.get("title"))) {
                found = true;
                break;
            }
        }
        assertEquals(200, response.getStatusCode());
        assertNotEquals(0, todos.size());
        assertTrue(found);
    }

    @Test
    public void testGetAllTodosXml(){

    }

    @Test
    public void testGetAllTodosHeaders() {
    }

    @Test
    public void testGetAllTodosOptions(){
        Response response = given().when().options("/todos");
        Header header = response.getHeaders().get("Allow");
        assertEquals(200, response.getStatusCode());
        assertEquals("OPTIONS, GET, HEAD, POST", header.getValue());
    }

    @Test
    public void testPostNoTitle() {

    }

    @Test
    public void testPostTodoWithTitle() {

    }

    @Test
    public void testGetTodoWithId() {

    }

    @Test
    public void testGetTodoWithTitle() {

    }

    @Test
    public void testPostTodoWithTitleAndDescription() {

    }

    @Test
    public void testGetTodoWithTitleAndDescription() {

    }

    @Test
    public void testGetSpecificTodoHeaders() {

    }

    @Test
    public void testGetSpecificTodoOptions() {

    }

    @Test
    public void testAmendTodoPut() {

    }

    @Test
    public void testAmendTodoPost() {

    }

    @Test
    public void testCreateToDoMalformedPayloadJson() {

    }

    @Test
    public void testCreateToDoMalformedPayloadXml() {

    }

    @Test
    public void testDeleteAlreadyDeletedTod() {

    }

}