package test.unitTests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.Random;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

// Run unit tests in a random order
@TestMethodOrder(Random.class)
public class ProjectUnitTest {
    private int testId;
    private JSONObject randomProject;
    private final String projectTitle = "Introduction to Software Validation";
    private final String partialProjectDescription = "Beginner";
    private static Process process;

    @BeforeAll
    public static void setup() throws Exception {
        String csvFile = "project_performance_results.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("operation,numObjects,duration,cpuUsage,memoryUsage\n"); // Write the header line
        } catch (IOException e) {
            e.printStackTrace();
        }
        // To run the api
        try {
            process = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            sleep(500); // to give time for the api to run
        } catch (Exception e) {
            e.printStackTrace();
        }

        // sets the base URI for all HTTP requests
        RestAssured.baseURI = "http://localhost:4567";

        // To test that the api is up and ready for testing
        int serverResponse = 404; // to indicate that the api is not running
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

    // Create a project before each test runs
    @BeforeEach
    public void createProject() {
        long startTime = System.currentTimeMillis();
        randomProject = RandomDataGenerator.generateProject();
        Response response = given()
                .body(randomProject.toString())
                .when()
                .post("/projects");
        long endTime = System.currentTimeMillis();
        System.out.println("createProject duration: " + (endTime - startTime) + "ms");

        assertEquals(201, response.getStatusCode());
        assertEquals(randomProject.getString("title"), response.jsonPath().getString("title"));
        assertEquals(randomProject.getString("description"), response.jsonPath().getString("description"));
        assertEquals(String.valueOf(randomProject.getBoolean("completed")), response.jsonPath().getString("completed"));
        assertEquals(String.valueOf(randomProject.getBoolean("active")), response.jsonPath().getString("active"));

        testId = response.jsonPath().getInt("id"); // get the ID of the newly created project
    }

    // deletes the created project after each test terminates
    @AfterEach
    public void deleteProject() {
        long startTime = System.currentTimeMillis();
        Response response = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");
        long endTime = System.currentTimeMillis();
        System.out.println("deleteProject duration: " + (endTime - startTime) + "ms");

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
    public void testGetAllProjectsJson() {
        Response response = given()
                .when()
                .get("/projects");

        List<Map<String, Object>> projects = response.jsonPath().getList("projects");
        boolean found = false;
        for (Map<String, Object> project : projects) {
            if (randomProject.getString("title").equals(project.get("title"))) {
                found = true;
                break;
            }
        }
        assertEquals(200, response.getStatusCode());
        assertFalse(projects.isEmpty());
        assertTrue(found);
    }

    @Test
    public void testGetAllProjectsXml(){
        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .when()
                .get("/projects");

        List<String> titles = response.xmlPath().getList("projects.project.title");
        boolean found = titles.contains(randomProject.getString("title"));

        assertEquals(200, response.getStatusCode());
        assertNotNull(titles);
        assertFalse(titles.isEmpty());
        assertTrue(found);
    }

    @Test
    public void testGetAllProjectsHeaders() {
        Response response = given()
                .when()
                .head("/projects");

        assertEquals(200, response.getStatusCode());
        assertNotEquals(0, response.getHeaders().size());
        assertEquals("application/json", response.getHeaders().get("Content-Type").getValue());
        assertEquals("chunked", response.getHeaders().get("Transfer-Encoding").getValue());
    }

    @Test
    public void testGetAllProjectsOptions() {
        Response response = given()
                .when()
                .options("/projects");

        assertEquals(200, response.getStatusCode());
        assertEquals("OPTIONS, GET, HEAD, POST", response.getHeaders().get("Allow").getValue());
    }

    @Test
    public void testPostEmptyProjectExpected() {
        JSONObject object = new JSONObject();
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testPostEmptyProjectActual() {
        JSONObject object = new JSONObject();
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void testPostProjectJson() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("completed", false);
        object.put("active", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("false", response.jsonPath().getString("completed"));
        assertEquals("false", response.jsonPath().getString("active"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created project
        int testId = response.jsonPath().getInt("id");
        Response responsePost = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost.getStatusCode());
    }

    @Test
    public void testPostProjectXml() {
        String xmlBody = "<project>" +
                "<title>test title</title>" +
                "<completed>false</completed>" +
                "<active>false</active>" +
                "<description>test description</description>" +
                "</project>";

        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .body(xmlBody)
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.xmlPath().getString("project.title"));
        assertEquals("false", response.xmlPath().getString("project.completed"));
        assertEquals("false", response.xmlPath().getString("project.active"));
        assertEquals("test description", response.xmlPath().getString("project.description"));

        // Delete the newly created project
        int testId = response.xmlPath().getInt("project.id");
        Response responsePost = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost.getStatusCode());
    }

    @Test
    public void testPostProjectsWithSameNameAndDescription() {
        JSONObject object1 = new JSONObject();
        object1.put("title", "test title");
        object1.put("completed", false);
        object1.put("active", false);
        object1.put("description", "test description");
        Response response1 = given()
                .body(object1.toString())
                .when()
                .post("/projects");

        assertEquals(201, response1.getStatusCode());
        assertEquals("test title", response1.jsonPath().getString("title"));
        assertEquals("false", response1.jsonPath().getString("completed"));
        assertEquals("false", response1.jsonPath().getString("active"));
        assertEquals("test description", response1.jsonPath().getString("description"));

        JSONObject object2 = new JSONObject();
        object2.put("title", "test title");
        object2.put("completed", false);
        object2.put("active", false);
        object2.put("description", "test description");
        Response response2 = given()
                .body(object2.toString())
                .when()
                .post("/projects");

        assertEquals(201, response2.getStatusCode());
        assertEquals("test title", response2.jsonPath().getString("title"));
        assertEquals("false", response2.jsonPath().getString("completed"));
        assertEquals("false", response2.jsonPath().getString("active"));
        assertEquals("test description", response2.jsonPath().getString("description"));

        int testId1 = response1.jsonPath().getInt("id");
        Response responsePost1 = given()
                .pathParam("id", testId1)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost1.getStatusCode());

        int testId2 = response2.jsonPath().getInt("id");
        Response responsePost2 = given()
                .pathParam("id", testId2)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost2.getStatusCode());
    }

    @Test
    public void testPostCompletedProject() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("completed", true);
        object.put("active", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("true", response.jsonPath().getString("completed"));
        assertEquals("false", response.jsonPath().getString("active"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created project
        int testId = response.jsonPath().getInt("id");
        Response responsePost = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost.getStatusCode());
    }

    @Test
    public void testPostCompletedAndActiveProject() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("completed", true);
        object.put("active", true);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("true", response.jsonPath().getString("completed"));
        assertEquals("true", response.jsonPath().getString("active"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created project
        int testId = response.jsonPath().getInt("id");
        Response responsePost = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responsePost.getStatusCode());

    }

    @Test
    public void testGetProjectWithId() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .get("/projects/{id}");

        assertEquals(200, response.getStatusCode());
        assertEquals(randomProject.getString("title"), response.jsonPath().getString("projects[0].title"));
        assertEquals(randomProject.getString("description"), response.jsonPath().getString("projects[0].description"));
        assertEquals(String.valueOf(randomProject.getBoolean("completed")), response.jsonPath().getString("projects[0].completed"));
        assertEquals(String.valueOf(randomProject.getBoolean("active")), response.jsonPath().getString("projects[0].active"));
    }

    @Test
    public void testGetProjectInvalidId() {
        int invalidId = -1;
        Response response = given()
                .pathParam("id", invalidId)
                .when()
                .get("/projects/{id}");

        assertEquals(404, response.getStatusCode());
        String expectedMessage = "[Could not find an instance with projects/" + invalidId + "]";
        assertEquals(expectedMessage, response.jsonPath().getString("errorMessages"));

    }

    @Test
    public void testGetProjectWithTitle() {
        Response response = given()
                .pathParam("projectTitle", randomProject.getString("title"))
                .when()
                .get("/projects?title={projectTitle}");

        assertEquals(200, response.getStatusCode());
        assertEquals(randomProject.getString("title"), response.jsonPath().getString("projects[0].title"));
        assertEquals(randomProject.getString("description"), response.jsonPath().getString("projects[0].description"));
        assertEquals(String.valueOf(randomProject.getBoolean("completed")), response.jsonPath().getString("projects[0].completed"));
        assertEquals(String.valueOf(randomProject.getBoolean("active")), response.jsonPath().getString("projects[0].active"));

    }

    @Test
    public void testGetProjectWithTitleAndPartialDescription() {
        Response response = given()
                .pathParam("projectTitle", projectTitle)
                .pathParam("partialProjectDescription", partialProjectDescription)
                .when()
                .get("/projects?title={projectTitle}&description={partialProjectDescription}");

        assertEquals(200, response.getStatusCode());
        assertEquals(projectTitle, response.jsonPath().getString("projects[0].title"));
        assertEquals(partialProjectDescription, response.jsonPath().getString("projects[0].description"));
        assertEquals("false", response.jsonPath().getString("projects[0].completed"));
        assertEquals("false", response.jsonPath().getString("projects[0].active"));
    }



    @Test
    public void testGetSpecificProjectHeaders() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .head("/projects/{id}");

        assertEquals(200, response.getStatusCode());
        assertNotEquals(0, response.getHeaders().size());
        assertEquals("application/json", response.getHeaders().get("Content-Type").getValue());
        assertEquals("chunked", response.getHeaders().get("Transfer-Encoding").getValue());

    }

    @Test
    public void testGetSpecificProjectOptions() {
        Response response = given()
                .pathParam("id", testId)
                .when()
                .options("/projects/{id}");

        assertEquals(200, response.getStatusCode());
        assertEquals("OPTIONS, GET, HEAD, POST, PUT, DELETE", response.getHeaders().get("Allow").getValue());

    }

    @Test
    public void testAmendProjectPost() {
        int[] objectCounts = {1, 10, 100, 1000};
        String csvFile = "project_performance_results.csv";

        for (int numObjects : objectCounts) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 1000; // 1-second sampling duration
            double initialCpuUsage = PerformanceUtils.getAverageCpuUsage(startTime, endTime);
            long initialMemoryUsage = PerformanceUtils.getMemoryUsage();

            for (int i = 0; i < numObjects; i++) {
                JSONObject updatedProject = RandomDataGenerator.generateProject();
                Response responsePost = given()
                        .body(updatedProject.toString())
                        .when()
                        .post("/projects/" + testId);
                assertEquals(200, responsePost.getStatusCode());
            }

            long operationEndTime = System.currentTimeMillis();
            long finalSamplingEndTime = operationEndTime + 1000; // 1-second sampling duration after operation
            double finalCpuUsage = PerformanceUtils.getAverageCpuUsage(operationEndTime, finalSamplingEndTime);
            long finalMemoryUsage = PerformanceUtils.getMemoryUsage();

            CsvWriter.writeResults(
                    csvFile,
                    "amendProjectPost",
                    numObjects,
                    (operationEndTime - startTime),
                    Math.max(0, finalCpuUsage - initialCpuUsage),
                    Math.max(0, finalMemoryUsage - initialMemoryUsage)
            );
        }
    }


    @Test
    public void testAmendProjectPostInvalidId() {
        int invalidId = -1;
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - post");

        Response responsePost = given()
                .body(updatedObject.toString())
                .when()
                .post("/projects/" + invalidId);

        assertEquals(404, responsePost.getStatusCode());
        String expectedMessage = "[No such project entity instance with GUID or ID " + invalidId + " found]";
        assertEquals(expectedMessage, responsePost.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testAmendProjectPut() {
        int[] objectCounts = {1, 10, 100, 1000};
        String csvFile = "project_performance_results.csv";

        for (int numObjects : objectCounts) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 1000; // 1-second sampling duration
            double initialCpuUsage = PerformanceUtils.getAverageCpuUsage(startTime, endTime);
            long initialMemoryUsage = PerformanceUtils.getMemoryUsage();

            for (int i = 0; i < numObjects; i++) {
                JSONObject updatedProject = RandomDataGenerator.generateProject();
                Response responsePut = given()
                        .body(updatedProject.toString())
                        .when()
                        .put("/projects/" + testId);
                assertEquals(200, responsePut.getStatusCode());
            }

            long operationEndTime = System.currentTimeMillis();
            long finalSamplingEndTime = operationEndTime + 1000; // 1-second sampling duration after operation
            double finalCpuUsage = PerformanceUtils.getAverageCpuUsage(operationEndTime, finalSamplingEndTime);
            long finalMemoryUsage = PerformanceUtils.getMemoryUsage();

            CsvWriter.writeResults(
                    csvFile,
                    "amendProjectPut",
                    numObjects,
                    (operationEndTime - startTime),
                    Math.max(0, finalCpuUsage - initialCpuUsage),
                    Math.max(0, finalMemoryUsage - initialMemoryUsage)
            );
        }
    }

    @Test
    public void testAmendProjectPutInvalidId() {
        int invalidId = -1;
        JSONObject updatedObject = new JSONObject();
        updatedObject.put("title", "updated test title - put");

        Response responsePut = given()
                .body(updatedObject.toString())
                .when()
                .post("/projects/" + invalidId);

        assertEquals(404, responsePut.getStatusCode());
        String expectedMessage = "[No such project entity instance with GUID or ID " + invalidId + " found]";
        assertEquals(expectedMessage, responsePut.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testCreateProjectMalformedPayloadJson() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("complete", false);  // should be completed, not complete
        object.put("active", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(400, response.getStatusCode());
        assertEquals("[Could not find field: complete]", response.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testCreateProjectMalformedPayloadXml() {
        String xmlBody = "<project>" +
                "<title>test title</title>" +
                "<complete>false</complete>" +  // should be completed, not complete
                "<active>false</active>" +
                "<description>test description</description>" +
                "</project>";

        Response response = given()
                .header("Accept", ContentType.XML)
                .contentType(ContentType.XML)
                .body(xmlBody)
                .when()
                .post("/projects");

        assertEquals(400, response.getStatusCode());
        assertEquals("Could not find field: complete", response.xmlPath().getString("errorMessages"));
    }

    @Test
    public void testDeleteAlreadyDeletedProject() {
        JSONObject object = new JSONObject();
        object.put("title", "test title");
        object.put("completed", false);
        object.put("active", false);
        object.put("description", "test description");
        Response response = given()
                .body(object.toString())
                .when()
                .post("/projects");

        assertEquals(201, response.getStatusCode());
        assertEquals("test title", response.jsonPath().getString("title"));
        assertEquals("false", response.jsonPath().getString("completed"));
        assertEquals("false", response.jsonPath().getString("active"));
        assertEquals("test description", response.jsonPath().getString("description"));

        // delete the newly created project
        int testId = response.jsonPath().getInt("id");
        Response responseDelete = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(200, responseDelete.getStatusCode());

        // try deleting the same project again
        Response responseDeleteAgain = given()
                .pathParam("id", testId)
                .when()
                .delete("/projects/{id}");

        assertEquals(404, responseDeleteAgain.getStatusCode());
        String expectedMessage = "[Could not find any instances with projects/" + testId + "]";
        assertEquals(expectedMessage, responseDeleteAgain.jsonPath().getString("errorMessages"));
    }

    @Test
    public void testDeleteProjectInvalidId() {
        int invalidId = -1;
        Response response = given()
                .pathParam("id", invalidId)
                .when()
                .delete("/projects/{id}");

        assertEquals(404, response.getStatusCode());
        String expectedMessage = "[Could not find any instances with projects/" + invalidId + "]";
        assertEquals(expectedMessage, response.jsonPath().getString("errorMessages"));

    }

    @Test
    public void testCreateMultipleProjects() {
        int[] objectCounts = {1, 10, 100, 1000};
        String csvFile = "project_performance_results.csv";

        for (int numObjects : objectCounts) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 1000; // 1-second sampling duration
            double initialCpuUsage = PerformanceUtils.getAverageCpuUsage(startTime, endTime);
            long initialMemoryUsage = PerformanceUtils.getMemoryUsage();

            for (int i = 0; i < numObjects; i++) {
                JSONObject project = RandomDataGenerator.generateProject();
                Response response = given()
                        .body(project.toString())
                        .when()
                        .post("/projects");
                assertEquals(201, response.getStatusCode());
            }

            long operationEndTime = System.currentTimeMillis();
            long finalSamplingEndTime = operationEndTime + 1000; // 1-second sampling duration after operation
            double finalCpuUsage = PerformanceUtils.getAverageCpuUsage(operationEndTime, finalSamplingEndTime);
            long finalMemoryUsage = PerformanceUtils.getMemoryUsage();

            CsvWriter.writeResults(
                    csvFile,
                    "createMultipleProjects",
                    numObjects,
                    (operationEndTime - startTime),
                    Math.max(0, finalCpuUsage - initialCpuUsage),
                    Math.max(0, finalMemoryUsage - initialMemoryUsage)
            );
        }
    }

    @Test
    public void testDeleteMultipleProjects() {
        int[] objectCounts = {1, 10, 100, 1000};
        String csvFile = "project_performance_results.csv";

        for (int numObjects : objectCounts) {
            int[] createdIds = new int[numObjects];

            // Create multiple projects
            for (int i = 0; i < numObjects; i++) {
                JSONObject project = RandomDataGenerator.generateProject();
                Response response = given()
                        .body(project.toString())
                        .when()
                        .post("/projects");
                assertEquals(201, response.getStatusCode());
                createdIds[i] = response.jsonPath().getInt("id");
            }

            long startTime = System.currentTimeMillis();
            long endTime = startTime + 1000; // 1-second sampling duration
            double initialCpuUsage = PerformanceUtils.getAverageCpuUsage(startTime, endTime);
            long initialMemoryUsage = PerformanceUtils.getMemoryUsage();

            // Delete all created projects
            for (int id : createdIds) {
                Response response = given()
                        .pathParam("id", id)
                        .when()
                        .delete("/projects/{id}");
                assertEquals(200, response.getStatusCode());
            }

            long operationEndTime = System.currentTimeMillis();
            long finalSamplingEndTime = operationEndTime + 1000; // 1-second sampling duration after operation
            double finalCpuUsage = PerformanceUtils.getAverageCpuUsage(operationEndTime, finalSamplingEndTime);
            long finalMemoryUsage = PerformanceUtils.getMemoryUsage();

            CsvWriter.writeResults(
                    csvFile,
                    "deleteMultipleProjects",
                    numObjects,
                    (operationEndTime - startTime),
                    Math.max(0, finalCpuUsage - initialCpuUsage),
                    Math.max(0, finalMemoryUsage - initialMemoryUsage)
            );
        }
    }

}
