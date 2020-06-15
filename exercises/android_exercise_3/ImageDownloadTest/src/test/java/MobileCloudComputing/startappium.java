package MobileCloudComputing;

import java.io.IOException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class startappium {
	
	@BeforeSuite
	public void appium() throws IOException, InterruptedException {
//		Runtime.getRuntime().exec("/home/manoj/Android/Sdk/tools/emulator -avd Nexus_6_API_24 -netdelay none -netspeed full");
//		Thread.sleep(5000L);
		//Runtime.getRuntime().exec("/bin/sh -c appium");
		//Thread.sleep(7000L);
	}
	
	@AfterSuite
	public void killappium() throws IOException {
		
//		Runtime.getRuntime().exec("/bin/sh -c killall node");
//		Runtime.getRuntime().exec("adb -s emulator-5554 emu kill");
	}
	
}
