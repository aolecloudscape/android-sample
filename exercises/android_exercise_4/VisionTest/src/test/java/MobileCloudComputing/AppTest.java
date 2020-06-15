package MobileCloudComputing;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterSuite;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;

public class AppTest
{

   private final String PickPhotoButtonId = "btnPickPhoto";

   private final String TextViewPersonsId = "txtNumPeople";

   private final String TextViewBarcodeId = "txtBarcode";

   private final String screenshotDir = System.getenv("SCREENSHOT_DIR");

   AndroidDriver driver;

   TouchAction t;

    
    public void installAPK() throws IOException {
    	File f = new File("/");
		File fs = new File(f, "/submission/user/application.apk");
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("deviceName", "Android");
		cap.setCapability("newCommandTimeout", 100); // if appium receives no commands in this time, app is terminated, in s
		cap.setCapability("appWaitDuration", 60000 ); // maximum time to wait for app to initialize, in ms
		cap.setCapability("deviceReadyTimeout", 60); // timeout to wait for device to be ready, in s
		cap.setCapability("androidDeviceReadyTimeout", 60); // timeout to wait for device to be ready after booting, in s
		cap.setCapability("androidInstallTimeout", 120000); // timeout to wait for app to install, in ms
      //cap.setCapability("autoWebviewTimeout", 10000); // time to wait for Webview context to become active, in ms
		cap.setCapability(MobileCapabilityType.DEVICE_NAME,"emulator-5554");
		cap.setCapability(MobileCapabilityType.APP, fs.getAbsolutePath());
		driver = new AndroidDriver(new URL(System.getenv("APPIUM_SERVER")+"/wd/hub"),cap);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

      t = new TouchAction(driver);   
    }


    @Test(description="Selfie photo",priority=1)
    public void SelfieTest() throws IOException, InterruptedException {
      runTest("fi.aalto.mcc.customgallery:id/button2", "1", "No", 20000L);
    }
    
    @Test(description="QR Code photo", priority=2)
    public void BarcodeTest() throws IOException, InterruptedException {
      runTest("fi.aalto.mcc.customgallery:id/button1", "0", "Yes", 20000L);
    }

    @Test(description="Landscape photo")
    public void LandscapeTest() throws IOException, InterruptedException {
      runTest("fi.aalto.mcc.customgallery:id/button3", "0", "No", 20000L);
    }

    @Test(description="Giant (50mb) landscape photo. Are you handling well the memory?")
    public void LargeLandscapeTest() throws IOException, InterruptedException {  
      runTest("fi.aalto.mcc.customgallery:id/button4", "0", "No", 20000L);
    }

    private static int testIndex = 0;
    
   public void runTest(String testCaseButton, String persons, String barcode, long processing) {

      String currId = null;

      testIndex++;

      String screenshotPath = screenshotDir+"/"+testIndex+".png";
      try {
         installAPK();      
         
         // press button
         currId = PickPhotoButtonId;
         takeScreenshot(screenshotPath);
	      WebElement myDynamicElement = (new WebDriverWait(driver, 10))
              .until(ExpectedConditions.presenceOfElementLocated(By.id(PickPhotoButtonId)));

	      (new TouchAction(driver)).tap(driver.findElement(By.id(PickPhotoButtonId))).perform();
      
         try {
            myDynamicElement = (new WebDriverWait(driver, 10))
                 .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@text='Test Cases']")));
         } catch (TimeoutException e) {
            //click hamburger
            (new TouchAction(driver)).tap(
		         driver.findElementByAndroidUIAutomator("new UiSelector().description(\"Show roots\")")).perform();
   	      
            Thread.sleep(1000L);
         }

         // open test case app
         try {
            takeScreenshot(screenshotPath);
            t.tap(driver.findElementByXPath("//*[@text='Test Cases']")).perform();
	         Thread.sleep(1000L); 

	         takeScreenshot(screenshotPath);
	         (new TouchAction(driver)).tap(
		         driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\""+testCaseButton+"\")")).perform();
	     
         } catch (NoSuchElementException e) {
            throw new Exception("Internal Error. Could not load test case.");
         } catch (TimeoutException e) {
            throw new Exception("Internal Error. Could not load test case.");
         }

         // allow time for photo processing
     	   Thread.sleep(processing);

         // check output
	      takeScreenshot(screenshotPath);
         currId = TextViewPersonsId;
         String foundPersons = driver.findElement(By.id(TextViewPersonsId)).getText();
         
         currId = TextViewBarcodeId;
         String foundBarcode = driver.findElement(By.id(TextViewBarcodeId)).getText();
   
	      Assert.assertEquals(foundPersons, persons, "Wrong number of persons:");

	      Assert.assertEquals(foundBarcode, barcode, "Wrong detection of barcode:");

      } catch (NoSuchElementException e) {
         Assert.fail("Could not find element with id '"+currId+"'. Please check the assignment instruction and use the provided View ids.");
      } catch (TimeoutException e) { 
         Assert.fail("Could not find element with id '"+currId+"'. Please check the assignment instruction and use the provided View ids.");
      } catch (Exception e) {
         Assert.fail(e.toString());
      } finally {
         driver.quit();
      }
   }  

   private void takeScreenshot(String filePath) throws IOException, InterruptedException {
      Process p = Runtime.getRuntime().exec("adb shell screencap -p /sdcard/screen.png");         
      p.waitFor();

      p = Runtime.getRuntime().exec("adb pull /sdcard/screen.png "+filePath);
      p.waitFor();
   }
}
