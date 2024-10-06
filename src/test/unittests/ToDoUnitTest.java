package test.unittests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.Random;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ToDoUnitTest {
    private int testId;
    private final String taskTitle = "Title Todo";
    private final String taskDescription = "Description of todo";
    private final Boolean doneStatus = false;

    @BeforeAll
    public static void setup() throws Exception {
        // To run the api
        ProcessBuilder processBuilder;
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c", "java -jar ..\\..\\..\\runTodoManagerRestAPI-1.5.5.jar");
        }
        else {
            processBuilder = new ProcessBuilder(
                    "sh", "-c", "java -jar ../../../runTodoManagerRestAPI-1.5.5.jar");
        }

        try {
            processBuilder.start();
            Thread.sleep(1000);
        } catch (IOException e) {
            System.out.println("Server ain't running duh");
        }

        RestAssured.baseURI = "http://localhost:4567";

        // To test that the api is up and ready for testing
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
        JSONObject object = new JSONObject();
        object.put("title", taskTitle);
        object.put("doneStatus", doneStatus);
        object.put("description", taskDescription);
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());
        assertEquals(taskTitle, response.jsonPath().getString("title"));
        assertEquals(taskDescription, response.jsonPath().getString("description"));
        assertEquals("false", response.jsonPath().getString("doneStatus"));

        testId = response.jsonPath().getInt("id"); // get the id of the newly created task
    }

    // deletes the created todo after each test terminates
    @AfterEach
    public void deleteToDo(){
        Response response = given()
                .pathParam("id", testId)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, response.getStatusCode());
    }

    @AfterAll
    public static void shutdownServer() {
        try {
            given().when().get("/shutdown");
        }
        catch (Exception ignored) {}
    }

    @Test
    public void testGetDocs(){
        Response response = given()
                .when()
                .get("/docs");

        String title = response.xmlPath().getString("html.head.title");
        assertEquals(200, response.getStatusCode());
        assertTrue(title.contains("API Documentation"));
    }

    @Test
    public void testGetAllTodosJson() {
        Response response = given()
                .when()
                .get("/todos");

        List<Map<String, Object>> todos = response.jsonPath().getList("todos");
        boolean found = false;
        for (Map<String, Object> todo : todos) {
            if (taskTitle.equals(todo.get("title"))) {
                found = true;
                break;
            }
        }
        assertEquals(200, response.getStatusCode());
        assertFalse(todos.isEmpty());
        assertTrue(found);
    }

    @Test
    public void testGetAllTodosXml(){
        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .when()
                .get("/todos");

        List<String> titles = response.xmlPath().getList("todos.todo.title");
        boolean found = titles.contains(taskTitle);

        assertEquals(200, response.getStatusCode());
        assertNotNull(titles);
        assertFalse(titles.isEmpty());
        assertTrue(found);
    }

    @Test
    public void testGetAllTodosHeaders() {
        Response response = given()
                .when()
                .head("/todos");

        assertEquals(200, response.getStatusCode());
        assertNotEquals(0, response.getHeaders().size());
        assertEquals("application/json", response.getHeaders().get("Content-Type").getValue());
        assertEquals("chunked", response.getHeaders().get("Transfer-Encoding").getValue());
    }

    @Test
    public void testGetAllTodosOptions() {
        Response response = given()
                .when()
                .options("/todos");

        assertEquals(200, response.getStatusCode());
        assertEquals("OPTIONS, GET, HEAD, POST", response.getHeaders().get("Allow").getValue());
    }

    @Test
    public void testPostEmptyTodo() {
        JSONObject object = new JSONObject();
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
        assertEquals("[title : field is mandatory]", response.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testPostNoTitle() {
        JSONObject object = new JSONObject();
        object.put("doneStatus", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
        assertEquals("[title : field is mandatory]", response.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testPostTodoJson() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("doneStatus", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("false", response.jsonPath().getString("doneStatus"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created todo
        int testId = response.jsonPath().getInt("id");
        Response response2 = given().pathParam("id", testId).when().delete("/todos/{id}");
        assertEquals(200, response2.getStatusCode());
    }

    @Test
    public void testPostTodoXml() {
        String xmlBody = "<todo>" +
                "<title>test title</title>" +
                "<doneStatus>false</doneStatus>" +
                "<description>test description</description>" +
                "</todo>";

        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .body(xmlBody)
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.xmlPath().getString("todo.title"));
        assertEquals("false", response.xmlPath().getString("todo.doneStatus"));
        assertEquals("test description", response.xmlPath().getString("todo.description"));

        // Delete the newly created todo
        int testId = response.xmlPath().getInt("todo.id");
        Response responsePost = given()
                .pathParam("id", testId)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, responsePost.getStatusCode());
    }

    @Test
    public void testGetTodoWithId() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .get("/todos/{id}");

        assertEquals(200, response.getStatusCode());
        assertEquals(taskTitle, response.jsonPath().getString("todos[0].title"));
        assertEquals(taskDescription, response.jsonPath().getString("todos[0].description"));
        assertEquals("false", response.jsonPath().getString("todos[0].doneStatus"));
    }

    @Test
    public void testGetTodoInvalidId() {
        int invalidId = -1;
        Response response = given()
                .pathParam("id", invalidId)
                .when()
                .get("/todos/{id}");

        assertEquals(404, response.getStatusCode());
        String expectedMessage = "[Could not find an instance with todos/" + invalidId + "]";
        assertEquals(expectedMessage, response.jsonPath().getString("errorMessages"));

    }

    @Test
    public void testGetTodoWithTitle() {
        Response response = given()
                .pathParam("taskTitle", taskTitle)
                .when()
                .get("/todos?title={taskTitle}");

        assertEquals(200, response.getStatusCode());
        assertEquals(taskTitle, response.jsonPath().getString("todos[0].title"));
        assertEquals(taskDescription, response.jsonPath().getString("todos[0].description"));
        assertEquals("false", response.jsonPath().getString("todos[0].doneStatus"));
    }

    @Test
    public void testGetTodoWithTitleAndDescription() {
        Response response = given()
                .pathParam("taskTitle", taskTitle)
                .pathParam("taskDescription", taskDescription)
                .when()
                .get("/todos?title={taskTitle}&description={taskDescription}");

        assertEquals(200, response.getStatusCode());
        assertEquals(taskTitle, response.jsonPath().getString("todos[0].title"));
        assertEquals(taskDescription, response.jsonPath().getString("todos[0].description"));
        assertEquals("false", response.jsonPath().getString("todos[0].doneStatus"));
    }

    @Test
    public void testGetSpecificTodoHeaders() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .head("/todos/{id}");

        assertEquals(200, response.getStatusCode());
        assertNotEquals(0, response.getHeaders().size());
        assertEquals("application/json", response.getHeaders().get("Content-Type").getValue());
        assertEquals("chunked", response.getHeaders().get("Transfer-Encoding").getValue());

    }

    @Test
    public void testGetSpecificTodoOptions() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .options("/todos/{id}");

        assertEquals(200, response.getStatusCode());
        assertEquals("OPTIONS, GET, HEAD, POST, PUT, DELETE", response.getHeaders().get("Allow").getValue());

    }

    @Test
    public void testAmendTodoPost() {
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - post");

        Response responsePost = given()
                .body(updatedObject.toString())
                .when()
                .post("/todos/" + testId);

        assertEquals(200, responsePost.getStatusCode());
        assertEquals("updated test title - post", responsePost.jsonPath().getString("title"));
        assertEquals(doneStatus.toString(), responsePost.jsonPath().getString("doneStatus"));
        assertEquals(taskDescription, responsePost.jsonPath().getString("description"));
    }

    @Test
    public void testAmendTodoPostInvalidId() {
        int invalidId = -1;
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - post");

        Response responsePost = given()
                .body(updatedObject.toString())
                .when()
                .post("/todos/" + invalidId);

        assertEquals(404, responsePost.getStatusCode());
        String expectedMessage = "[No such todo entity instance with GUID or ID " + invalidId + " found]";
        assertEquals(expectedMessage, responsePost.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testAmendTodoPut() {
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - put");

        Response responsePut = given()
                .body(updatedObject.toString())
                .when()
                .put("/todos/" + testId);

        assertEquals(200, responsePut.getStatusCode());
        assertEquals("updated test title - put", responsePut.jsonPath().getString("title"));
        assertEquals(doneStatus.toString(), responsePut.jsonPath().getString("doneStatus"));
        assertEquals(taskDescription, responsePut.jsonPath().getString("description"));
    }

    @Test
    public void testAmendTodoPutInvalidId() {
        int invalidId = -1;
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - put");

        Response responsePut = given()
                .body(updatedObject.toString())
                .when()
                .post("/todos/" + invalidId);

        assertEquals(404, responsePut.getStatusCode());
        String expectedMessage = "[No such todo entity instance with GUID or ID " + invalidId + " found]";
        assertEquals(expectedMessage, responsePut.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testCreateTodoMalformedPayloadJson() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("done", false);  // should be doneStatus
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
        assertEquals("[Could not find field: done]", response.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testCreateTodoMalformedPayloadXml() {
        String xmlBody = "<todo>" +
                "<title>test title</title>" +
                "<done>false</done>" +
                "<description>test description</description>" +
                "</todo>";

        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .body(xmlBody)
                .when()
                .post("/todos");

        assertEquals(400, response.getStatusCode());
        assertEquals("Could not find field: done", response.xmlPath().getString("errorMessages"));
    }

    @Test
    public void testDeleteAlreadyDeletedTodo() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("doneStatus", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/todos");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("false", response.jsonPath().getString("doneStatus"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created todo
        int testId = response.jsonPath().getInt("id");
        Response responseDelete = given()
                .pathParam("id", testId)
                .when()
                .delete("/todos/{id}");

        assertEquals(200, responseDelete.getStatusCode());

        // try deleting the same todo again
        Response responseDeleteAgain = given()
                .pathParam("id", testId)
                .when()
                .delete("/todos/{id}");

        assertEquals(404, responseDeleteAgain.getStatusCode());
        String expectedMessage = "[Could not find any instances with todos/" + testId + "]";
        assertEquals(expectedMessage, responseDeleteAgain.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testDeleteTodoInvalidId() {
        int invalidId = -1;
        Response response = given()
                .pathParam("id", invalidId)
                .when()
                .delete("/todos/{id}");

        assertEquals(404, response.getStatusCode());
        String expectedMessage = "[Could not find any instances with todos/" + invalidId + "]";
        assertEquals(expectedMessage, response.jsonPath().getString("errorMessages"));

    }

}