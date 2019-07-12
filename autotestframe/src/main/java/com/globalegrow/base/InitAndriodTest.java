package com.globalegrow.base;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.globalegrow.code.Page;
import com.globalegrow.util.Cmd;
import com.globalegrow.util.Log;
import com.globalegrow.util.PropUtil;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;

@SuppressWarnings("rawtypes")
public class InitAndriodTest extends Init{
	
	protected static AndroidDriver<?> driver;
	public static String pappPackage;
	public static String pappActivity;
	public static String pappName;
	public static String pinstallApp;
	public static String pautomation_name;
	public static String platformName;
	
	public static AndroidDriver getDriver() {
		return driver;
	}
	public static void setDriver(AndroidDriver driver) {
		InitAndriodTest.driver = driver;
	}	

	@Parameters({ "appPackage","appActivity","appName","installApp","project_code","tastjob","devices","automation_name"})
	@BeforeSuite
	public void beforeSuite(String appPackage,String appActivity,String appName,String installApp,
			String project_code, String tastjob, String devices,String automation_name){
		initTest(project_code, tastjob, devices);
		pappPackage = appPackage;
		pappActivity= appActivity;
		pappName = appName ;
		pautomation_name = automation_name;
		pinstallApp = installApp;

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
	public static void StartAndroidApp(){
		Log.logInfo("开始读取设备的配置信息······");
		platformName = devicesMap.get("platformName");
        File classpathRoot = new File(System.getProperty("user.dir"));  
        File appDir = new File(classpathRoot, "apps");  
        File app = new File(appDir, pappName);// 测试apk
        DesiredCapabilities capabilities = new DesiredCapabilities();  
        capabilities.setCapability("platformName", devicesMap.get("platformName"));  
        capabilities.setCapability("udid",freeDeviceName);//真机
        capabilities.setCapability("deviceName",freeDeviceName);//真机
        capabilities.setCapability("platformVersion", devicesMap.get("platformVersion"));// 安卓版本
        if(pautomation_name.equals("UIAUTOMATOR2")){
        	capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        }
        capabilities.setCapability("app", app.getAbsolutePath()); 
        capabilities.setCapability("unicodeKeyboard", "True");
        capabilities.setCapability("resetKeyboard", "True");
        capabilities.setCapability("deviceReadyTimeout", devicesMap.get("deviceReadyTimeout"));
        capabilities.setCapability("newCommandTimeout", devicesMap.get("newCommandTimeout"));
        capabilities.setCapability("appPackage", pappPackage);// app的packageName
        capabilities.setCapability("appActivity", pappActivity); //启动activity  
        capabilities.setCapability("noReset", pinstallApp); //重置app状态
//        capabilities.setCapability("fullReset", pinstallApp); //不重新安装App
        Log.logInfo("capabilities信息:"+capabilities.toString());
         try {
            Log.logInfo("开始启动session··············");
            driver = new AndroidDriver(new URL(appiumServerUrl),  capabilities);  
            Log.logInfo("启动session成功··············");
         } catch (Exception e) {
     		//	e.printStackTrace();
     		Log.logInfo("启动session异常,脚本停止运行--:"+e.getMessage());
    		if(e.getMessage().contains("Connection reset"))
    		{
    			Log.logInfo("出现 Connection reset，脚本停止运行");
    			if(driver != null){
    				driver.quit();
    			}
    			Properties props=System.getProperties();
    			String osName = props.getProperty("os.name");
    			if(osName.contains("Windows")){
    				Cmd.StopServer(Init.freePort, Init.freeDeviceName);
    			}else if (osName.contains("Linux")) {
    				Cmd.disconnectDevices(osName,Init.freeDeviceName);//断开设备链接
    			}
    		}
    		System.exit(0);
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
			((AndroidDriver)driver).lockDevice();//锁定屏幕
			driver.quit();
		}
		Page.pause(3);
		stopServer();	
	}
	
}
