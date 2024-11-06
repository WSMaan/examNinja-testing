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

public class TestRegistrationPage extends BaseSetUp {

    private Logger log = Logger.getLogger(TestRegistrationPage.class.getName());
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
        driver.get(p.getProperty("url"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        log.info("Navigated to registration page: " + p.getProperty("url"));
    }

    @Test
    public void testSuccessfulRegistration(){
        // Happy path:Successful registration
        //Fill all the field with valid data and Click the registration button
        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email= driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwd = driver.findElement(By.xpath(p.get("password").toString()));
        pwd.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        assertTrue("Successful registration is not display", regButton.isDisplayed());
    }

    @Test
    public void testEmptyFirstNameField(){
        //Unhappy path: Empty first name field
        // Fill the other fields with valid data and Click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        WebElement lastName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lastName.sendKeys(p.get("lName_val").toString());
        WebElement email= driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement password = driver.findElement(By.xpath(p.get("password").toString()));
        password.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());

        WebElement fNameError = driver.findElement(By.xpath("//p[@id=':r1:-helper-text']"));
        assertEquals("First Name is required", fNameError.getText());
    }

    @Test
    public void testEmptyLastNameField(){
        //Unhappy path: Empty last name field
        // Fill the other fields with valid data and Click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        WebElement firstName = driver.findElement(By.xpath(p.get("name").toString()));
        firstName.sendKeys(p.get("name_val").toString());
        WebElement email= driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement password = driver.findElement(By.xpath(p.get("password").toString()));
        password.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());

        WebElement lNameError = driver.findElement(By.xpath("//p[@id=':r3:-helper-text']"));
        assertEquals("Last Name is required", lNameError.getText());
    }

    @Test
    public void testEmptyEmailFields() {
        // Unhappy Path: Empty Email Fields Scenario
        // Fill the other fields with valid data and Click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        WebElement firstName = driver.findElement(By.xpath(p.get("name").toString()));
        firstName.sendKeys(p.get("name_val").toString());
        WebElement lastName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lastName.sendKeys(p.get("lName_val").toString());
        WebElement password = driver.findElement(By.xpath(p.get("password").toString()));
        password.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());

        WebElement emailError = driver.findElement(By.xpath("//p[@id=':r5:-helper-text']"));
        assertEquals("Email is required",emailError.getText());
    }

    @Test
    public void testEmptyPasswordFields() {
        // Unhappy Path: Empty Password Fields Scenario
        // Fill the other fields with valid data and Click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        WebElement firstName = driver.findElement(By.xpath(p.get("name").toString()));
        firstName.sendKeys(p.get("name_val").toString());
        WebElement lastName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lastName.sendKeys(p.get("lName_val").toString());
        WebElement email= driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());

        WebElement passwordError = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        assertEquals("Password is required", passwordError.getText());
    }

    @Test
    public void testEmptyConfirmPasswordField(){
        //Unhappy path: Confirm password field empty
        // Fill the other fields with valid data and Click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();
        WebElement firstName = driver.findElement(By.xpath(p.get("name").toString()));
        firstName.sendKeys(p.get("name_val").toString());
        WebElement lastName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lastName.sendKeys(p.get("lName_val").toString());
        WebElement email= driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement password = driver.findElement(By.xpath(p.get("password").toString()));
        password.sendKeys(p.get("pass_val").toString());

        WebElement confirmPwdError= driver.findElement(By.xpath("//p[@id='confirmP-helper-text']"));
        assertEquals( "Confirm Password is required",confirmPwdError.getText());

    }
    @Test
    public void testAllEmptyFields(){
        // Unhappy Path: All empty Fields Scenario
        // Leave all fields empty and click the registration button
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Verify that error messages are shown for all required fields (first name, last name, email, password)
        WebElement fNameError = driver.findElement(By.xpath("//p[@id=':r1:-helper-text']"));
        WebElement lNameError = driver.findElement(By.xpath("//p[@id=':r3:-helper-text']"));
        WebElement emailError = driver.findElement(By.xpath("//p[@id=':r5:-helper-text']"));
        WebElement passwordError = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        WebElement cPassword= driver.findElement(By.xpath("//p[@id='confirmP-helper-text']"));

        assertEquals("First Name is required", fNameError.getText());
        assertEquals("Last Name is required", lNameError.getText());
        assertEquals("Email is required",emailError.getText());
        assertEquals("Password is required", passwordError.getText());
        assertEquals("Confirm Password is required", cPassword.getText());
    }

    @Test
    public void testEmptyFirstAndLastNameFields() {
        // Unhappy path: Empty first name & last name
        // Fill the other fields with valid data and Click the registration button
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement password = driver.findElement(By.xpath(p.get("password").toString()));
        password.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Verify that an error message is shown for both first name and last name
        WebElement fNameError = driver.findElement(By.xpath("//p[@id=':r1:-helper-text']"));
        WebElement lNameError = driver.findElement(By.xpath("//p[@id=':r3:-helper-text']"));

        assertEquals("First Name is required", fNameError.getText());
        assertEquals("Last Name is required", lNameError.getText());
    }


    @Test
    public void testInvalidEmail() {
        // Unhappy Path: Invalid Email Scenario

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys("invalid12mail.com");
        WebElement pwd = driver.findElement(By.xpath(p.get("password").toString()));
        pwd.sendKeys(p.get("pass_val").toString());
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys(p.get("cPass_val").toString());
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Validate error message for invalid email
        WebElement emailError = driver.findElement(By.xpath("//p[@id=':r5:-helper-text']"));
        assertEquals( "Enter a valid email address",emailError.getText());
    }

    @Test
    public void testWeakPassword() {
        // Unhappy Path: Weak Password Scenario

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwd = driver.findElement(By.xpath(p.get("password").toString()));
        pwd.sendKeys("123"); // Weak password
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys("123");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Validate error message for weak password
        WebElement passwordError = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        // assertTrue( "Weak password error message not displayed",passwordError.isDisplayed());
        assertEquals("Password must be between 8 and 15 characters", passwordError.getText());
    }

    @Test
    public void testPasswordTooLong() {
        // Unhappy Path: Long Password Scenario

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwd = driver.findElement(By.xpath(p.get("password").toString()));
        pwd.sendKeys("Abc@1230000000000"); // long password
        WebElement cPwd= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        cPwd.sendKeys("Abc@1230000000000");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Validate error message for weak password
        WebElement passwordError = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        // assertTrue( "Weak password error message not displayed",passwordError.isDisplayed());
        assertEquals("Password must be between 8 and 15 characters", passwordError.getText());
    }
    @Test
    public void testPasswordAndConfirmPasswordMatch(){
       // Unhappy path: Enter mismatch password

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwdField = driver.findElement(By.xpath(p.get("password").toString()));
        WebElement cPwdField= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        pwdField.sendKeys("Abc*1234");
        cPwdField.sendKeys("Acb@1234");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        // Verify error message or validation for mismatched passwords
        WebElement errorMessage = driver.findElement(By.xpath("//p[@id='confirmP-helper-text']"));
        assertEquals("Passwords must match", errorMessage.getText());
    }

    @Test
    public void testPasswordMustHaveUpperLetter(){
        // Unhappy path: Enter password without Uppercase letter

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwdField = driver.findElement(By.xpath(p.get("password").toString()));
        WebElement cPwdField= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        pwdField.sendKeys("abcd12345");
        cPwdField.sendKeys("abcd12345");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        WebElement errorMessage = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        assertEquals("Password must contain at least one uppercase letter", errorMessage.getText());
    }

    @Test
    public void testPasswordMustHaveSpecialCharacter(){
        //Unhappy path: Enter password without special character

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwdField = driver.findElement(By.xpath(p.get("password").toString()));
        WebElement cPwdField= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        pwdField.sendKeys("Abcd12345");
        cPwdField.sendKeys("Abcd12345");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        WebElement errorMessage = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        assertEquals("Password must contain at least one special character", errorMessage.getText());
    }

    @Test
    public void testPasswordMustHaveNumber(){
        // Unhappy path: Enter password without Number

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwdField = driver.findElement(By.xpath(p.get("password").toString()));
        WebElement cPwdField= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        pwdField.sendKeys("abcd@Def");
        cPwdField.sendKeys("abcd@Def");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        WebElement errorMessage = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        assertEquals("Password must contain at least one number", errorMessage.getText());
    }

    @Test
    public void testPasswordFieldIsMasked(){

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement passwordField = driver.findElement(By.xpath(p.get("password").toString()));
        String fieldType= passwordField.getAttribute("type");
        assertEquals( "password", fieldType);

    }

    @Test
    public void testPasswordDoesNotAcceptSpace(){
        //Unhappy path: Enter space in password field

        WebElement fName = driver.findElement(By.xpath(p.get("name").toString()));
        fName.sendKeys(p.get("name_val").toString());
        WebElement lName = driver.findElement(By.xpath(p.get("lastName").toString()));
        lName.sendKeys(p.get("lName_val").toString());
        WebElement email = driver.findElement(By.xpath(p.get("email").toString()));
        email.sendKeys(p.get("email_val").toString());
        WebElement pwdField = driver.findElement(By.xpath(p.get("password").toString()));
       // WebElement cPwdField= driver.findElement(By.xpath(p.get("confirmPass").toString()));
        pwdField.sendKeys("         ");
      //  cPwdField.sendKeys( "         ");
        WebElement regButton = driver.findElement(By.xpath(p.get("reg_btn").toString()));
        regButton.click();

        WebElement errorField = driver.findElement(By.xpath("//p[@id='pwd-helper-text']"));
        assertEquals("Password cannot contain only spaces", errorField.getText());
    }
}


