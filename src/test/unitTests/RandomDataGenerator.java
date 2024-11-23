package test.unitTests;
import com.github.javafaker.Faker;
import org.json.JSONObject;

public class RandomDataGenerator {
    private static final Faker faker = new Faker();

    public static JSONObject generateTodo() {
        JSONObject todo = new JSONObject();
        todo.put("title", faker.lorem().sentence());
        todo.put("doneStatus", faker.bool().bool());
        todo.put("description", faker.lorem().paragraph());
        return todo;
    }

    public static JSONObject generateProject() {
        JSONObject project = new JSONObject();
        project.put("title", faker.book().title());
        project.put("completed", faker.bool().bool());
        project.put("active", faker.bool().bool());
        project.put("description", faker.lorem().paragraph());
        return project;
    }
}
