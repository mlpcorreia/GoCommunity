package db;

import com.mycompany.gocommunity.DatabaseHandler;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Carlos
 */
public class SeleniumIT {
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
    driver = new ChromeDriver();
    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testUntitledTestCase() throws Exception {
    String uname;
    String pname;
    
    do { //randomly generate names until available ones are found
        uname = randomName();
        pname = randomName();
    } while (db.apiGetUser(uname)!=null || db.apiGetProject(pname)!=null);

    driver.get("http://localhost:8080/GoCommunity/");
    driver.findElement(By.linkText("Sign Up")).click();
    driver.findElement(By.name("j_idt7:j_idt9")).clear();
    driver.findElement(By.name("j_idt7:j_idt9")).sendKeys("Selenium User");
    driver.findElement(By.name("j_idt7:j_idt11")).clear();
    driver.findElement(By.name("j_idt7:j_idt11")).sendKeys(uname);
    driver.findElement(By.name("j_idt7:j_idt13")).clear();
    driver.findElement(By.name("j_idt7:j_idt13")).sendKeys("password219380");
    driver.findElement(By.name("j_idt7:j_idt15")).click();
    driver.findElement(By.linkText("New Project")).click();
    driver.findElement(By.name("j_idt7:j_idt9")).clear();
    driver.findElement(By.name("j_idt7:j_idt9")).sendKeys(pname);
    driver.findElement(By.name("j_idt7:j_idt11")).clear();
    driver.findElement(By.name("j_idt7:j_idt11")).sendKeys("This is the project's description. It's not very big but it gets the job done.");
    driver.findElement(By.name("j_idt7:j_idt13")).clear();
    driver.findElement(By.name("j_idt7:j_idt13")).sendKeys("3500.75");
    driver.findElement(By.name("j_idt7:j_idt15")).clear();
    driver.findElement(By.name("j_idt7:j_idt15")).sendKeys("2018-12-12");
    driver.findElement(By.name("j_idt7:j_idt18")).click();
    driver.findElement(By.linkText("Search Projects")).click();
    driver.findElement(By.name("j_idt7:j_idt9")).click();
    driver.findElement(By.name("j_idt7:j_idt9")).clear();
    driver.findElement(By.name("j_idt7:j_idt9")).sendKeys(pname);
    driver.findElement(By.name("j_idt7:j_idt11")).click();
    driver.findElement(By.name("j_idt7:j_idt18")).click();
    driver.findElement(By.id("j_idt11:mkey")).clear();
    driver.findElement(By.id("j_idt11:mkey")).sendKeys("100.00");
    driver.findElement(By.id("j_idt11:mtxt")).clear();
    driver.findElement(By.id("j_idt11:mtxt")).sendKeys("A free t-shirt for every backer if we get this far.");
    driver.findElement(By.id("j_idt11:madd")).click();
    driver.findElement(By.id("j_idt11:cmttxt")).clear();
    driver.findElement(By.id("j_idt11:cmttxt")).sendKeys("First comment!");
    driver.findElement(By.id("j_idt11:cmtadd")).click();
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
        StringBuilder sb = new StringBuilder();
        
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

