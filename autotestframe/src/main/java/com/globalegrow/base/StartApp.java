package com.globalegrow.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.globalegrow.code.Locator;
import com.globalegrow.util.Log;
import com.globalegrow.util.OpAndroid;
import com.globalegrow.util.OpApp;
import com.globalegrow.util.OpIos;
import com.globalegrow.util.PropUtil;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class StartApp {

	
	protected static WebDriver driver;
	protected Locator locator;
	public static OpApp opApp;
	public static String platforName;

	protected static String projectName = "";
	public static Map<String, List> testCasemap = new HashMap<String, List>();// 存放excel的数据
	public static String screenShotPath = "test-output/html-report/screenShot/";// 截图路径
	public String moduleName = "";// 模块名称
	protected int pageLoadTimeoutMax = 65;
	
	public static String testDescription = ""; //测试描述
	public static String testCheckpoint = ""; //验证点
	public static boolean correFail = false; //关联失败标志
	
	public static WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	public static OpApp getOp() {
		return opApp;
	}

	public static void setOp(OpApp opApp) {
		StartApp.opApp = opApp;
	}
	
	public Locator getLocator() {
		return locator;
	}

	public void setLocator(Locator locator) {
		this.locator = locator;
	}

	public void start() {
		//获取driver
		if(TestngListener2.projectCode.contains("android"))
		{
			InitAndriodTest.StartAndroidApp();
			platforName="android";
			driver = InitAndriodTest.getDriver();
			Log.logInfo("初始化项目名称··············");
			projectName = TestngListener2.getProjectName();
			Log.logInfo("初始化项目成功··············");
			moduleName = GetModulemName();
			Log.logInfo("初始化locator对象··············");
			locator = new Locator(TestngListener2.projectCode);// 初始化对象
			Log.logInfo("初始化locator对象成功··············");
			opApp=new OpAndroid((AndroidDriver)driver, locator,projectName);
			//判断屏幕是否锁屏
			if(((AndroidDriver)driver).isLocked()){
				((AndroidDriver)driver).unlockDevice();
			}
		}else{
			Log.logInfo("当前为ios");
			InitIosTest.StartIosApp();
			platforName="ios";
			driver = InitIosTest.getDriver();
			Log.logInfo("初始化项目名称··············");
			projectName = TestngListener2.getProjectName();
			Log.logInfo("初始化项目成功··············");
			moduleName = GetModulemName();
			Log.logInfo("projectName："+projectName);
			Log.logInfo("初始化locator对象··············");
			locator = new Locator(TestngListener2.projectCode);// 初始化对象
			Log.logInfo("初始化locator对象成功··············");
			opApp=new OpIos((IOSDriver)driver, locator, projectName);
		}
		Log.logInfo("platforName:"+platforName);
	}
	
	/**
	 * 获取当前测试类的项目名
	 * @return 以字符串形式，返回当前的测试类的项目名
	 * @author yuyang 创建时间:2016-09-20 更新时间:2016-09-20
	 */
	protected String GetModulemName() {
		String[] tmpName = GetClassName().split("\\.");
		String modulemName = "";
		if (tmpName.length > 1) {
			modulemName = tmpName[tmpName.length - 1];
		}
		return modulemName;
	}
	
	/**
	 * 初始化
	 * @param myProjectName
	 * @param configName
	 */
	public void init(String myProjectName, String configName) {
		
		PropUtil.GetProperties(myProjectName, configName);
		projectName = myProjectName;

	}
	
	/**
	 * 获取当前类名
	 * @return 以字符串形式，返回当前的类名
	 * @author yuyang 创建时间:2016-08-31 更新时间:2016-08-31
	 */
	protected String GetClassName() {
		String classnamestr = this.getClass().getName();
		return classnamestr;
	}
	
	/**
	 * 获取上一级的方法名
	 * @return 以字符串形式，返回上一级的方法名
	 * @author yuyang 创建时间:2016-08-31 更新时间:2016-08-31
	 */
	protected String GetMethodName() {
		String methodnamestr = Thread.currentThread().getStackTrace()[2].getMethodName();
		return methodnamestr;
	}
	
}
