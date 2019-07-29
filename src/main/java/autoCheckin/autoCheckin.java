package autoCheckin;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class autoCheckin {
    public static void main(String[] args) throws FileNotFoundException {
        new autoCheckin().execute();
    }

    public void execute() throws FileNotFoundException {

        FileReader reader = null;
        InputStream inputStream = this.getClass().getResourceAsStream("/login.properties");

        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(this::run1);

        if(!p.getProperty("companionConfirmation").equals("")) {
            executorService.submit(this::run2);
        }

        // close executorService
        executorService.shutdown();
    }

    public  void run1() {

        try {
            checkIn(null,"confirmation", "firstName", "lastName", "mainScreenshot");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void run2() {

        try {
            checkIn(null,"companionConfirmation", "companionFirstName", "companionLastName", "companionScreenshot");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void checkIn(ChromeDriver driver, String confirmationProp, String firstNameProp, String lastNameProp, String ssName) throws InterruptedException {
        InputStream inputStream = this.getClass().getResourceAsStream("/login.properties");


        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(URLDecoder.decode(ClassLoader.getSystemResource("chromedriver.exe").getPath()));
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1200x2400");

        driver = new ChromeDriver(options);

        driver.get("https://www.southwest.com/?clk=GNAVHOMELOGO");
        //driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);

        WebElement checkIn = driver.findElement(By.xpath("//*[@id=\"TabbedArea_4-tab-4\"]/span/span[2]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(checkIn).click().build().perform();
        driver.manage().timeouts().implicitlyWait(3000, TimeUnit.SECONDS);

        Thread.sleep(2000);

        WebElement confirmation = driver.findElement(By.xpath("//*[@id=\"LandingPageAirReservationForm_confirmationNumber_check-in\"]"));
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.SECONDS);
        confirmation.sendKeys(p.getProperty(confirmationProp));

        Thread.sleep(500);

        WebElement firstName = driver.findElement(By.xpath("//*[@id=\"LandingPageAirReservationForm_passengerFirstName_check-in\"]"));
        driver.manage().timeouts().implicitlyWait(3000, TimeUnit.SECONDS);
        firstName.sendKeys(p.getProperty(firstNameProp));

        Thread.sleep(500);

        WebElement lastName = driver.findElement(By.xpath("//*[@id=\"LandingPageAirReservationForm_passengerLastName_check-in\"]"));
        driver.manage().timeouts().implicitlyWait(150, TimeUnit.SECONDS);
        lastName.sendKeys(p.getProperty(lastNameProp));

        Thread.sleep(500);

        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.SECONDS);
        WebElement checkinButton = driver.findElement(By.xpath("//*[@id=\"LandingPageAirReservationForm_submit-button_check-in\"]"));
        checkinButton.click();

        for(int i = 0; i < 15; i++) {
            driver.manage().timeouts().implicitlyWait(500, TimeUnit.SECONDS);
            WebElement checkinButton2 = driver.findElement(By.xpath(" //*[@id=\"form-mixin--submit-button\"]"));
            checkinButton2.click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Take screenshot and store as a file format
        File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            // now copy the  screenshot to desired location using copyFile //method
            FileUtils.copyFile(src, new File("C:\\Users\\TSO9362\\Pictures\\Selenium\\CheckIn Attempts\\" + ssName));
        }

        catch (IOException e)
        {
            System.out.println(e.getMessage());

        }
    }
}
