import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestAdminLoginPage extends BaseSetUp{
    private Logger log = Logger.getLogger(TestAdminLoginPage.class.getName());
    private WebDriver driver;
    private Properties p;

    @BeforeClass
    public void loadProperties() throws IOException {
        // Load properties from application.properties file
        p = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/application.properties");
        p.load(fis);
    }

    @BeforeMethod
    public void setUp() {
        driver = getWebDriver();
        // Open the registration page
        driver.get(p.getProperty("url1"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        log.info("Navigated to registration page: " + p.getProperty("url"));
    }

    @Test
    public void testSuccessfulLogin(){
        // Happy path: Successful login

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys(p.get("pwd_val").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        assertTrue("Login was not successful", l_button.isDisplayed());
    }

    @Test
    public void testInvalidPassword(){
        // Unhappy path: Unsuccessful login
        //Enter valid username and invalid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("drs@12Sol");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("Incorrect password", pwdError.getText());
    }

    @Test
    public void testInvalidUsername(){
        // Unhappy path: Unsuccessful login
        //Enter invalid username and valid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys("userName");
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys(p.get("pwd_val").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("User not found", pwdError.getText());
    }

    @Test
    public void testInvalidUsernameAndPassword(){
        // Unhappy path: Unsuccessful login
        //Enter invalid username and invalid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys("userName");
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("abcdef#12");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("User not found", pwdError.getText());
    }

    @Test
    public void testEmptyField(){
        // Unhappy path: Unsuccessful login
        //Passing empty username and password

        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("Validation failed", pwdError.getText());
    }

    @Test
    public void testEmptyPasswordField(){
        // Unhappy path: Unsuccessful login
        //Enter valid username and empty password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("Validation failed", pwdError.getText());
    }

    @Test
    public void testEmptyUsernameField(){
        // Unhappy path: Unsuccessful login
        //Enter valid password and empty username

        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys(p.get("pwd_val").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lcmie7-MuiTypography-root']"));
        assertEquals("Validation failed", pwdError.getText());
    }

}
