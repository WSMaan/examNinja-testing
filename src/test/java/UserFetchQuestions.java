
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class UserFetchQuestions {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";  // Replace with the actual base URI
        RestAssured.port = 8081;                   // Replace with the actual port if needed
    }

    // Happy Path Test: Valid test_id and page_number
    @Test
    public void testGetQuestionsHappyPath() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/tests/1/questions?page=6")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON).log().all()
                .body("content[0].questionId", equalTo(27))
                .body("content[0].question", equalTo("Explain the Java memory model."))
                .body("content[0].option1", equalTo("Heap & Stack"))
                .body("content[0].option2", equalTo("Code & Data"))
                .body("content[0].option3", equalTo("Heap & CPU"))
                .body("content[0].option4", equalTo("RAM & ROM"))
                .body("content[0].correctAnswer", equalTo("Heap & Stack"))
                .body("content[0].category", equalTo("Programming"))
                .body("content[0].level", equalTo("Hard"))
                .body("pageable.pageNumber", equalTo(6))
                .body("totalElements", equalTo(10))
                .body("totalPages", equalTo(10));
    }

    // Unhappy Path Test: Invalid test_id
    @Test
    public void testGetQuestionsInvalidTestId() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("test_id", 12)
                .queryParam("page", 0)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON).log().all()
                .body("message", equalTo("No questions found for test with Test Id: 12"));
    }
    @Test
    public void testGetQuestionsInvalidPage() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("test_id", 2)
                .queryParam("page", 10)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON).log().all()
                .body("message", equalTo("Requested page is out of bounds. Maximum page number: 9"));
    }

}
