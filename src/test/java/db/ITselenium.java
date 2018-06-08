package db;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import static org.junit.Assert.*;

public class ITselenium {
  /*private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    //System.setProperty("webdriver.gecko.driver", "/home/miguel/geckodriver.exe");
    driver = new FirefoxDriver();
    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateAccount() throws Exception {
    driver.get("http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/");
    driver.findElement(By.linkText("Sign Up")).click();
    driver.findElement(By.id("a:n")).click();
    driver.findElement(By.id("a:n")).clear();
    driver.findElement(By.id("a:n")).sendKeys("Selenium test");
    driver.findElement(By.id("a:un")).click();
    driver.findElement(By.id("a:un")).clear();
    driver.findElement(By.id("a:un")).sendKeys("seleniumtest");
    driver.findElement(By.id("a:p")).click();
    driver.findElement(By.id("a:p")).clear();
    driver.findElement(By.id("a:p")).sendKeys("test123");
    driver.findElement(By.id("a:b")).click();
    driver.findElement(By.linkText("Logout")).click();
  }
  
  @Test
  public void testFollowProject() throws Exception {
    driver.get("http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/login.xhtml");
    driver.findElement(By.name("j_idt7:j_idt9")).clear();
    driver.findElement(By.name("j_idt7:j_idt9")).sendKeys("seleniumtest");
    driver.findElement(By.name("j_idt7:j_idt11")).clear();
    driver.findElement(By.name("j_idt7:j_idt11")).sendKeys("test123");
    driver.findElement(By.name("j_idt7:j_idt13")).click();
    driver.findElement(By.name("j_idt7:j_idt13")).click();
    driver.findElement(By.name("j_idt7:j_idt9")).click();
    driver.findElement(By.linkText("New Project")).click();
    driver.findElement(By.id("p:n")).click();
    driver.findElement(By.id("p:n")).clear();
    driver.findElement(By.id("p:n")).sendKeys("Selenium Test Project");
    driver.findElement(By.id("p:d")).click();
    driver.findElement(By.id("p:d")).clear();
    driver.findElement(By.id("p:d")).sendKeys("Selenium Test Project");
    driver.findElement(By.id("p:g")).click();
    driver.findElement(By.id("p:g")).clear();
    driver.findElement(By.id("p:g")).sendKeys("9000");
    driver.findElement(By.id("p:t")).click();
    driver.findElement(By.id("p:t")).clear();
    driver.findElement(By.id("p:t")).sendKeys("2018-09-01");
    driver.findElement(By.id("p:b")).click();
    driver.findElement(By.linkText("Logout")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }*/
}
