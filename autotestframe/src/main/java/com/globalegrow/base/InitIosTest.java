package com.globalegrow.base;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.globalegrow.code.Page;
import com.globalegrow.util.Log;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

@SuppressWarnings("rawtypes")
public class InitIosTest extends Init{
		
	protected static IOSDriver<?> driver;
	public static String pappPackage;
	public static String pappActivity;
	public static String pappName;
	public static String pinstallApp;
	public static String platformName;
	
	public static IOSDriver getDriver() {
		return driver;
	}
	public static void setDriver(IOSDriver driver) {
		InitIosTest.driver = driver;
	}	
	@Parameters({ "appName","installApp","project_code","tastjob","devices"})
	@BeforeSuite
	public void beforeSuite(String appName,String installApp,
			String project_code, String tastjob, String devices){
		initTest(project_code, tastjob, devices);
		pappName = appName ;
		if(installApp.equals("false")){
			pinstallApp = "True";
		}else{
			pinstallApp = "False";
		}
	}
	
	/**
	 * 初始化session
	 * @param appPackage
	 * @param appActivity
	 * @param appName
	 * @author linchaojiang
	 * 创建时间:2017-01-13,最后更新时间:2017-01-13
	 * @throws MalformedURLException 
	 */
	public static void StartIosApp(){
		
		Log.logInfo("开始读取设备的配置信息······");
		platformName = devicesMap.get("platformName");
		File classpathRoot = new File(System.getProperty("user.dir"));  
	    File appDir = new File(classpathRoot, "apps");  
	    File app = new File(appDir, pappName);// 测试apk
        DesiredCapabilities capabilities = new DesiredCapabilities();  
        capabilities.setCapability("platformName", devicesMap.get("platformName"));  
        capabilities.setCapability("udid",devicesMap.get("devices_name"));//真机
        capabilities.setCapability("deviceName",devicesMap.get("devices_name"));//真机
        capabilities.setCapability("platformVersion", devicesMap.get("platformVersion"));// 版本
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability("app", app.getAbsolutePath()); 
        capabilities.setCapability("newCommandTimeout", devicesMap.get("newCommandTimeout"));
        capabilities.setCapability("webDriverAgentUrl", "http://localhost:8001");
      capabilities.setCapability("noReset", pinstallApp); //重置app状态
        capabilities.setCapability("autoAcceptAlerts", true);//自动接受权限弹出框
        Log.logInfo("capabilities信息:"+capabilities.toString());
        try {
        	Log.logInfo("开始启动session··············");
     //   	appiumServerUrl="http://localhost:4728/wd/hub";
        	driver = new IOSDriver(new URL(appiumServerUrl),  capabilities);  
        	Log.logInfo("启动session成功··············");
        } catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("启动session异常:"+e.getMessage());
		}
        Page.pause(4);
	}
			
	/**
	 * 停止appium和adb链接
	 * @author linchaojiang
	 * 创建时间:2017-01-13,最后更新时间:2017-01-13
	 */
	@AfterSuite
	public void StopServer(){
		if(driver != null){
			Log.logInfo("driver还存在");
		//	driver.removeApp("com.globalegrow.gearbest");
			driver.quit();
		}
		Page.pause(3);
		stopServer();		
	}
}
