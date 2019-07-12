package com.globalegrow.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.globalegrow.code.Locator;
import com.globalegrow.util.Log;
import com.globalegrow.util.Op;
import com.globalegrow.util.PropUtil;
import com.globalegrow.util.Pub;
import com.google.gson.Gson;

@SuppressWarnings("rawtypes")
public class StartPhoneBrowser {

	protected static WebDriver driver;
//	protected PhoneLocator locator;
	protected Locator locator;
	public static Op op=null;
	protected static String projectName = "";
	public static Map<String, List> testCasemap = new HashMap<String, List>();// 存放excel的数据
	public static String screenShotPath = "test-output/html-report/screenShot/";// 截图路径
	public String moduleName = "";// 模块名称
	protected int pageLoadTimeoutMax = 65;

	public static boolean correFail = false; // 关联失败标志

	public Locator getLocator() {
		return locator;
	}

	public void setLocator(Locator locator) {
		this.locator = locator;
	}

	public static WebDriver getDriver() {
		return driver;
	}

	public static void setDriver(WebDriver driver) {
		StartPhoneBrowser.driver = driver;
	}

	
	public void start(String devicesName) {

		initdriver();//初始化driver
		Log.logInfo("初始化项目名称··············");
		projectName = TestngListener2.getProjectName();
		Log.logInfo("初始化项目成功··············");
		Log.logInfo("初始化locator对象··············");
		locator = new Locator(TestngListener2.getProjectCode());// 初始化对象
		Log.logInfo("初始化locator对象成功··············");
		op = new Op(driver, locator, projectName);
		Log.logInfo("初始化op成功··············");
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

		
	public void init(String myProjectName, String configName) {
		PropUtil.GetProperties(myProjectName, configName);
		projectName = myProjectName;

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

	/**
	 * 判断对象是否存在
	 * @param by 待检查的元素,如id,xpath等
	 * @return boolean,true-查找到，false-未查找到
	 * @author yuyang 创建时间:2016-09-05 更新时间:2016-09-05
	 */
	public static boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	/**
	 * 使用谷歌浏览器模拟手机浏览器
	 * @param devicesName
	 * @author linchaojiang
	 * 创建时间:2017-06-15,更新时间:2017-06-15
	 * 备注
	 */
	public void initdriver(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-infobars");
		Map<String, Object> prefs = new HashMap<String, Object>();
		// 是否加载图片
		// prefs.put("profile.managed_default_content_settings.images", 2);
		options.setExperimentalOption("prefs", prefs);
		Map<String, Object> deviceMetrics = new HashMap<String, Object>();
		deviceMetrics.put("width", 360);
		deviceMetrics.put("height", 640);
		deviceMetrics.put("pixelRatio", 3.0);
		Map<String, Object> mobileEmulation = new HashMap<String, Object>();
		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent",
				"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
		options.setExperimentalOption("mobileEmulation", mobileEmulation);
		System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
		if (TestngListener2.getBrowserName().contains("platform")) {// 这里表示使用的平台，为了不影响以前框架使用
			System.out.println("使用的平台进行启动的浏览器");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			try {
				// 让主节点去自己分发数据
				driver = new RemoteWebDriver(new URL("http://10.40.6.24:4444/wd/hub"), capabilities);
				// 获取driver的session
				String getSession = driver.toString().substring(driver.toString().indexOf("(") + 1,
						driver.toString().indexOf(")"));
				// get请求，获取在hub上分发到某台电脑运行
				Map<String, String> getIP = Pub
						.get("http://10.40.6.24:4444/grid/api/testsession?session=" + getSession);
				// 从返回内容中获取到需要的内容
				Gson gson = new Gson();
				Map<String, Object> map = new HashMap<String, Object>();
				map = gson.fromJson(getIP.get("Response"), map.getClass());
				String proxyId = (String) map.get("proxyId");
				System.out.println("服务器运行的ip为:" + proxyId);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			driver = new ChromeDriver(options);
		}

	}

}
