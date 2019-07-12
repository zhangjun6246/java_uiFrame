package com.globalegrow.base;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.globalegrow.util.Log;

import io.appium.java_client.android.AndroidDriver;

/**
 * 初始化appium、adb链接及停止appium和adb链接
 * @author linchaojiang
 * 创建时间:2017-01-13,最后更新时间:2017-02-07
 */
public class InitWapTest extends Init{
	
	@SuppressWarnings("rawtypes")
	protected static AndroidDriver driver;
	
	@SuppressWarnings("rawtypes")
	public static AndroidDriver getDriver() {
		return driver;
	}

	@SuppressWarnings("rawtypes")
	public static void setDriver(AndroidDriver driver) {
		InitWapTest.driver = driver;
	}

	@Parameters({ "project_code","tastjob","devices"})
	@BeforeSuite
	public void beforeSuite(String project_code, String tastjob, String devices){	
		initTest(project_code, tastjob, devices);
		Log.logInfo("开始读取设备的配置信息······");		
	}
		
	/**
	 * 停止appium和adb链接
	 * @author linchaojiang
	 * 创建时间:2017-01-13,最后更新时间:2017-01-13
	 */
	@AfterSuite
	public void AfterSuite(){
		try {
			driver.quit();
		} catch (Exception e) {
			Log.logInfo("浏览器已关闭或未启动······");
		}
		stopServer();
	}
	
	/**
	 * 根据传入的参数，启动浏览器
	 * @param browserName
	 * @param deviceName
	 * @param platformVersion
	 * @param url
	 */
	@SuppressWarnings("rawtypes")
	public static void StartBrowser() {

		Log.logInfo("开始读取设备的配置信息······");	
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", devicesMap.get("platformName"));// 系统类型：安卓，IOS
		capabilities.setCapability("deviceName", freeDeviceName);// 设备名称
		capabilities.setCapability("udid", freeDeviceName);// 设备udid
		capabilities.setCapability("platformVersion", devicesMap.get("platformVersion"));// 系统的版本
		capabilities.setCapability("browserName", "Chrome");// 启动浏览器类型			
		capabilities.setCapability("newCommandTimeout", devicesMap.get("newCommandTimeout"));//second		
		try {
			Log.logInfo("开始启动session··············");
			Log.logInfo("url:"+appiumServerUrl);
			Log.logInfo("capabilities:"+capabilities.toString());
			driver = new AndroidDriver(new URL(appiumServerUrl),capabilities);		
			Log.logInfo("启动session成功··············");
		} catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("StartBrowser error:"+e.getMessage());
		}
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}
}

