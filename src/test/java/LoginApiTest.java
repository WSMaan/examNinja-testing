import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordUpdateTest {

    // Base URI for the BBC website
    private static final String BASE_URL = "https://www.bbc.com";

    @Test
    public void testHomepageStatus() {
        // Send a GET request to the BBC homepage
        Response response = RestAssured.get(BASE_URL);

        // Assert that the status code is 200 (OK)
        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        // Assert that the response content type is HTML
        Assert.assertEquals(response.getContentType(), "text/html; charset=utf-8", "Content type is not HTML");
    }

    @Test
    public void testHomepageTitle() {
        // Send a GET request to the BBC homepage
        Response response = RestAssured.get(BASE_URL);

        // Extract the HTML body
        String htmlBody = response.getBody().asString();

        // Check if the title tag exists and contains expected text
        Assert.assertTrue(htmlBody.contains("<title>BBC - Home</title>"), "Homepage title is not as expected");
    }

    @Test
    public void testHomepageLinks() {
        // Send a GET request to the BBC homepage
        Response response = RestAssured.get(BASE_URL);

        // Extract the HTML body
        String htmlBody = response.getBody().asString();

        // Check for specific links in the HTML
        Assert.assertTrue(htmlBody.contains("href=\"/news\""), "Link to News is missing");
        Assert.assertTrue(htmlBody.contains("href=\"/sport\""), "Link to Sport is missing");
        Assert.assertTrue(htmlBody.contains("href=\"/weather\""), "Link to Weather is missing");
    }
}
