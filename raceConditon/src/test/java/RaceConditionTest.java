
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;


public class RaceConditionTest {
    public static String body = "{}";

    @Test
    public void raceConditionTest() throws InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(10); // Use 10 threads for concurrency

        Runnable task = () -> {
            long startTimeMillis = System.currentTimeMillis(); // Capture start time in milliseconds
            Response res = given()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(body)
                    .when()
                    .post("baseUrl" + "/")
                    .then()
                    .extract()
                    .response();

            long requestTimeMillis = res.timeIn(TimeUnit.MILLISECONDS); // Request execution time in milliseconds
            long endTimeMillis = startTimeMillis + requestTimeMillis; // Calculate end time in milliseconds

            // Format the start and end times
            String formattedStartTime = new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(new java.util.Date(startTimeMillis));
            String formattedEndTime = new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(new java.util.Date(endTimeMillis));

            // Log the times
            System.out.println("Status Code: " + res.getStatusCode() +
                    ", Response: " + res.asString() +"Time: " + formattedEndTime +
                    ", Request Time: " + formattedStartTime);
        };


        for (int i = 0; i < 50; i++) { // Execute the task 50 times concurrently
            service.submit(task);
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES); // Wait for all tasks to finish
    }
}

