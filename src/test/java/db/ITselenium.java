package db;

import com.mycompany.gocommunity.DatabaseHandler;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Carlos
 */
public class ITselenium {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private Random random = new Random();
  private final DatabaseHandler db = new DatabaseHandler("go.odb");
  EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/go.odb");
        EntityManager em = emf.createEntityManager();

  @Before
  public void setUp() throws Exception {
    //System.setProperty("webdriver.chrome.driver", "/home/chff/chromedriver");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    driver = new ChromeDriver(options);
    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void seleniumTest() throws Exception {
    System.out.println("Selenium Test");
    
    String uname;
    String pname;
    
    do { //randomly generate names until available ones are found
        uname = randomName();
        pname = randomName();
    } while (db.apiGetUser(uname)!=null || db.apiGetProject(pname)!=null);

    driver.get("http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT");
    driver.findElement(By.linkText("Sign Up")).click();
    driver.findElement(By.id("a:n")).clear();
    driver.findElement(By.id("a:n")).sendKeys("Selenium User");
    driver.findElement(By.id("a:un")).clear();
    driver.findElement(By.id("a:un")).sendKeys(uname);
    driver.findElement(By.id("a:p")).clear();
    driver.findElement(By.id("a:p")).sendKeys("password219380");
    driver.findElement(By.id("a:b")).click();
    driver.findElement(By.linkText("New Project")).click();
    driver.findElement(By.id("p:n")).clear();
    driver.findElement(By.id("p:n")).sendKeys(pname);
    driver.findElement(By.id("p:d")).clear();
    driver.findElement(By.id("p:d")).sendKeys("This is the project's description. It's not very big but it gets the job done.");
    driver.findElement(By.id("p:g")).clear();
    driver.findElement(By.id("p:g")).sendKeys("3500.75");
    driver.findElement(By.id("p:t")).clear();
    int nextyear = Calendar.getInstance().get(Calendar.YEAR) + 1;
    driver.findElement(By.id("p:t")).sendKeys(nextyear+"-06-06");
    driver.findElement(By.id("p:b")).click();
    driver.findElement(By.linkText("Search Projects")).click();
    driver.findElement(By.id("s:se")).click();
    driver.findElement(By.id("s:se")).clear();
    driver.findElement(By.id("s:se")).sendKeys(pname);
    driver.findElement(By.id("s:b")).click();
    driver.findElement(By.id("s:pr")).click();
    driver.findElement(By.id("p:mkey")).clear();
    driver.findElement(By.id("p:mkey")).sendKeys("100.00");
    driver.findElement(By.id("p:mtxt")).clear();
    driver.findElement(By.id("p:mtxt")).sendKeys("A free t-shirt for every backer if we get this far.");
    driver.findElement(By.id("p:madd")).click();
    driver.findElement(By.id("p:cmttxt")).clear();
    driver.findElement(By.id("p:cmttxt")).sendKeys("First comment!");
    driver.findElement(By.id("p:cmtadd")).click();

  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }
  
  private int random(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }
    
    private String randomName() {
        int type;
        StringBuilder sb = new StringBuilder("Selenium-");
        
        for (int i=0;i<20;i++) {
            type = random.nextInt(3);
            switch (type) {
                case 0:
                    sb.append(random(0,10));
                    break;
                case 1:
                    sb.append((char) random(65,90));
                    break;
                default:
                    sb.append((char) random(97,122));
                    break;
            }
        }
        return sb.toString();    
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
  }
}

