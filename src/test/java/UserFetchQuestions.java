import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import util.BaseTestClass;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class UserFetchQuestions extends BaseTestClass {

    // Happy Path Test: Valid test_id and page_number
    @Test
    public void testGetQuestionsHappyPath() {
        // Generate random test_id and page number using Random class
        Random random = new Random();
        int randomTestId = random.nextInt(3) + 1;  // Generates a value between 1 and 3
        int page = random.nextInt(10);  // Random page between 0 and 9

        // Step 1: Fetch the question response and extract it
        Response questionResponse = given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .when()
                .pathParam("test_id", randomTestId)
                .queryParam("page", page)
                .get(questionsEndpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .log().ifError()
                .extract().response();

        // Step 2: Extract option values dynamically from the response
        String option1 = questionResponse.jsonPath().getString("questions[0].option1");
        String option2 = questionResponse.jsonPath().getString("questions[0].option2");
        String option3 = questionResponse.jsonPath().getString("questions[0].option3");
        String option4 = questionResponse.jsonPath().getString("questions[0].option4");

        // Step 3: Perform assertions
        questionResponse.then()
                .body("testName", notNullValue())
                .body("questionNumber", matchesRegex("\\d+ of \\d+"))
                .body("questions[0].questionId", notNullValue())
                .body("questions[0].question", notNullValue())
                .body("questions[0].option1", notNullValue())
                .body("questions[0].option2", notNullValue())
                .body("questions[0].option3", notNullValue())
                .body("questions[0].option4", notNullValue())
                .body("questions[0].correctAnswer", notNullValue())
                .body("questions[0].answerDescription", notNullValue())
                .body("questions[0].category", notNullValue())
                .body("questions[0].level", notNullValue())
                .body("questions[0].questionType", notNullValue())
                .body("questions[0].testId", equalTo(questionResponse.jsonPath().getInt("questions[0].testId")))
                // Updated selectedOption validation with dynamic options
                .body("questions[0].selectedOption", anyOf(
                        nullValue(),
                        equalTo(option1),
                        equalTo(option2),
                        equalTo(option3),
                        equalTo(option4)
                ))
                .body("pageDetails.pageNumber", equalTo(questionResponse.jsonPath().getInt("pageDetails.pageNumber")))
                .body("pageDetails.pageSize", equalTo(questionResponse.jsonPath().getInt("pageDetails.pageSize")))
                .body("pageDetails.totalPages", equalTo(questionResponse.jsonPath().getInt("pageDetails.totalPages")))
                .body("pageDetails.totalElements", equalTo(questionResponse.jsonPath().getInt("pageDetails.totalElements")))
                .body("pageDetails.lastPage", equalTo(questionResponse.jsonPath().getBoolean("pageDetails.lastPage")));

        int testId = questionResponse.jsonPath().getInt("questions[0].testId");
        int questionId = questionResponse.jsonPath().getInt("questions[0].questionId");
        int pageNumber=questionResponse.jsonPath().getInt("pageDetails.pageNumber");

        String[] options = {
                option1,option2,option3,option4
        };

        String currentSelectedOption = questionResponse.jsonPath().getString("questions[0].selectedOption");

        if (currentSelectedOption == null) {
            String selectedOption = options[new Random().nextInt(options.length)];

            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .body("{ \"questionId\": " + questionId + ", \"testId\": " + testId + ", \"selectedOption\": \"" + selectedOption + "\" }")
                    .when()
                    .post(saveAnswerEndpoint)
                    .then()
                    .statusCode(200)
                    .body("status", equalTo("success"))
                    .body("message", equalTo("Answer saved successfully!"));

            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .pathParam("test_id", testId)
                    .queryParam("page", pageNumber)
                    .when()
                    .get(questionsEndpoint)
                    .then()
                    .statusCode(200)
                    .body("questions[0].questionId", equalTo(questionId))
                    .body("questions[0].selectedOption", equalTo(selectedOption));

        } else {
            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .pathParam("test_id", testId)
                    .queryParam("page", pageNumber)
                    .when()
                    .get(questionsEndpoint)
                    .then()
                    .statusCode(200)
                    .body("questions[0].selectedOption", equalTo(currentSelectedOption));
        }
    }

    // Unhappy Path Test: Invalid test_id
    @Test
    public void testGetQuestionsInvalidTestId(){
        given()
                .header("Authorization", authToken)  // Use the JWT token here too
                .contentType(ContentType.JSON)
                .pathParam("test_id", 10) // Invalid test_id
                .queryParam("page", 0)
                .when()
                .get(questionsEndpoint)
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("No questions found for test with Test Id:"));
    }

    // Unhappy Path Test: Invalid page number
    @Test
    public void testGetQuestionsInvalidPage(){
        given()
                .header("Authorization", authToken)  // Use the JWT token here too
                .contentType(ContentType.JSON)
                .pathParam("test_id", 1)
                .queryParam("page", 66) // Page number out of bounds
                .when()
                .get(questionsEndpoint)
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("Requested page is out of bounds. Maximum page number:"));
    }

}