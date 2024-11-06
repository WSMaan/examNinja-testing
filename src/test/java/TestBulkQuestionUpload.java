import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class TestBulkQuestionUpload {

    @BeforeClass
    public static void setup() {
        // Base URI
        RestAssured.baseURI = "http://localhost:9000"; // Update with your actual base URL
    }

    @Test
    public void testBulkUpload_SuccessfulUpload() {
        File successUpload = new File("src/test/resources/Sample success1.csv");
        given()
                .multiPart("file", successUpload, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testBulkUpload_NoFileAttached() {
        given()
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testBulkUpload_InvalidFileFormat() {
        File invalidFormat = new File("src/test/resources/invalidFileformant.txt");
        given()
                .multiPart("file", invalidFormat, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testEmptyFile() {
        File emptyFile = new File("src/test/resources/emptyfile - Copy.csv");
        given()
                .multiPart("file", emptyFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testMissingMandatoryField() {
        File missingQuestionFile = new File("src/test/resources/Missing Mandatory Fields.csv");
        given()
                .multiPart("file", missingQuestionFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testMissingQuestionType() {
        File missingQTypeFile = new File("src/test/resources/invalid Qtype.csv");
        given()
                .multiPart("file", missingQTypeFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testDuplicateQuestions() {
        File duplicateQuestionsFile = new File("src/test/resources/duplicate Qs.csv");
        given()
                .multiPart("file", duplicateQuestionsFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testMissingQuestion() {
        File missingQuestion = new File("src/test/resources/missingQS.csv");
        given()
                .multiPart("file", missingQuestion, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }
}

