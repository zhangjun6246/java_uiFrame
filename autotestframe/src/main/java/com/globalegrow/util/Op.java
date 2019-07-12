package com.globalegrow.util;

import java.io.File;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.globalegrow.code.Locator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import test.testng285.Derived;


/**
 * pc和wap的常用操作类
 */
public class Op {
	public static WebDriver driver ;
	public Locator locator;
	public long defaultPageLoadTimeoutMax = 60;

	public Actions action;
	public JavascriptExecutor jse;

	public long explicitWaitTimeoutLoop = 20;
	private String projectName;
	private String screenShotPath;
	Exception exception=new Exception();

	public String CHECK_LOG_FORMAT = "yyyyMMdd";
	public String TEN_PAY_DATE_STRING_FORMAT = "yyMMdd";
	public String REPORT_CSV_FORMAT = "yyyyMMdd_HHmm";
	public String ZH_DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
	public String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

	private  SimpleDateFormat simpleDateFormat;

	public  String format(String type) {
		simpleDateFormat = new SimpleDateFormat(type);
		return simpleDateFormat.format(new Date());
	}
	

	public static WebDriver getDriver() {
		return driver;
	}

	public static void setDriver(WebDriver driver) {
		Op.driver = driver;
	}

	/**
	 * wap测试构造函数
	 * @param driver
	 * @param locator
	 * @param projectName
	 * @param string
	 */
	public Op(WebDriver driver, Locator locator, String projectName) {
		this.driver = driver;
		this.locator = locator;
		this.projectName = projectName;
		this.action = new Actions(driver);
		this.jse = (JavascriptExecutor) driver;
	}

	/**
	 * 设置截图路径
	 * @param screenShotPath
	 * @author yuyang ,创建时间：2016-10-18,更新时间：2016-10-18
	 */
	public void setScreenShotPath(String screenShotPath) {
		this.screenShotPath = screenShotPath;
	}

	public long getExplicitWaitTimeoutLoop() {
		return explicitWaitTimeoutLoop;
	}

	public void setExplicitWaitTimeoutLoop(long explicitWaitTimeoutLoop) {
		this.explicitWaitTimeoutLoop = explicitWaitTimeoutLoop;
	}

	/**
	 * 获取方法名
	 * @return 方法名
	 */
	public static String GetMethodName() {
		String methodnamestr = Thread.currentThread().getStackTrace()[2].getMethodName();
		return methodnamestr;
	}

	/**
	 * 在一定时间内，等待页面元素存在，如果等待到，则返回页面元素，否则返回null，不抛出异常
	 * 备注：与MyWebDriverWait相似，只是不打印多余的log，用于取代WebDriverWait
	 * @param elementStr 页面元素
	 * @param explicitWaitTimeout  超时时间，单位为秒
	 * @param ePrintFlag 查找不到元素时是否打印异常信息的标志，true-打印，false-不打印
	 * @param printFlag 是否打印查找或未查找到页面元素的信息标志，true-打印，false-不打印
	 * @author yuyang ,创建时间：2016-11-15,更新时间：2016-11-15
	 */
	public WebElement MyWebDriverWait2(String elementStr, long explicitWaitTimeout, boolean ePrintFlag){
		WebElement element=null;
		try {
			 element=findElementAssert(elementStr, explicitWaitTimeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(ePrintFlag){
				e.printStackTrace();
			}
			
		}
		return element;
	}
	
	public WebElement MyWebDriverWait2(String elementStr, long explicitWaitTimeout) throws Exception{
		return findElementAssert(elementStr, explicitWaitTimeout);
	}

	/**
	 * 截取屏幕图片,只适用于wap
	 * @author linchaojiang
	 * @param directory 路径的最后一级文件夹名
	 * @param captureName 除去时间字符串和后缀的截图名
	 * 创建时间：2016-08-23 更新时间:2016-12-07
	 * 更新说明(2016-09-27)：by yuyang,增加重试来解决概率截图失败的问题
	 * 更新说明(2016-11-16):by yuyang,移动到SatartPhoneBrowse里，发现在使用模拟器的时候，无法截图，由于要使用模拟器调试，先把截图步骤屏蔽
	 * 更新说明(2016-11-16):by yuyang,移动到Op里,由静态方法改为成员方法,只适用于wap
	 * 更新说明(2016-12-07):by yuyang,增加截图失败后刷新页面的动作
	 */
	public String captureScreen(String directory, String captureName) throws Exception{
		Date currentTime = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
		String picDateSign = formats.format(currentTime);
		String screenShotFileName = screenShotPath + directory + "/" + picDateSign + "_" + captureName + ".jpg";
		File screenShotFile = null;
		screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenShotFile, new File(screenShotFileName));
		Log.logWarn("截图成功，图片路径:"+screenShotFileName);
		return screenShotFileName;
	}

	/**
	 * 截取屏幕图片,浏览器多个窗口的,只适用于web
	 * @author yuyang
	 * @param directory 路径的最后一级文件夹名
	 * @param captureName 除去时间字符串和后缀的截图名
	 * 创建时间：2016-09-29 更新时间:2016-12-07
	 * 更新说明(2016-12-07):by yuyang,移动到Op里,由静态方法改为成员方法,只适用于web
	 */
	public String captureScreenAll(String directory, String captureName) {
		Date currentTime = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
		String picDateSign = formats.format(currentTime);
		String screenShotFileNameFirst = "";
		String screenShotFileName = "";
		File screenShotFile = null;
		int count = 0;
		int picNum = 0;

		screenShotFileNameFirst = screenShotPath + directory + "/" + picDateSign + "_" + captureName + "_1.jpg";
		String currentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		for (String s : handles) {
			picNum++;
			screenShotFileName = screenShotPath + directory + "/" + picDateSign + "_" + captureName + "_" + picNum + ".jpg";
			driver.switchTo().window(s);
			while (true) {
				Log.logInfo("screen shot:" + screenShotFileName);
				try {
					Thread.sleep(500);
					screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(screenShotFile, new File(screenShotFileName));
					break;
				} catch (Exception e) {
					e.printStackTrace();
					count++;
					Log.logWarn(count + " times,截图失败,接下来会刷新页面.");
					driver.navigate().refresh();
					if (count >= 3) {
						Log.logInfo("Screenshot failed！");
						break;
					}
				}
			}

		}
		driver.switchTo().window(currentHandle);
		return screenShotFileNameFirst;
	}

	/**
	 * 截取屏幕图片,兼容web和wap
	 * @author yuyang
	 * @param directory 路径的最后一级文件夹名
	 * @param captureName 除去时间字符串和后缀的截图名
	 * 创建时间：2016-12-07 更新时间:2016-12-07
	 */
	public String captureScreen2(String directory, String captureName){
		String screenShotFileName = "";
		try {
			screenShotFileName = captureScreen(directory, captureName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenShotFileName;
	}

	/**
	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常。
	 * 备注:第二次尝试单击时， 如果找不到单击的页面元素，仍然查找新的页面元素
	 * @param clickStr 用来作单击动作的元素
	 * @param FindStr 用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-11-29,更新时间：2016-12-08
	 * 更新说明(2016-12-06)：by yuyang,第二次尝试单击时，如果找不到单击的页面元素，仍然查找新的页面元素
	 */
	public void loopClick(String clickStr, String FindStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		for (int i = 0; i < loopTimes; i++) {
			click(clickStr, explicitWaitTimeout);
			if(findElementNotAssert(FindStr, explicitWaitTimeout) != null){
				break;
			}
		}
	}

	/**
	 * 给页面输入框元素输入字符串，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-11-16,更新时间：2016-11-16
	 */
	public void loopSendKeys(String inputStr, String keys, int loopTimes, long explicitWaitTimeout) throws Exception {
		for (int i = 0; i < loopTimes; i++) {
			sendText(inputStr, explicitWaitTimeout, keys);
			if(getAttributeValue(inputStr, explicitWaitTimeout, "value").equals(keys)){
				break;
			}
		}
	}

	/**
	 * 点击页面元素，如果点击成功，则返回；否则循环点击，直到超时
	 * @param elementStr 页面元素
	 * @param SleepTime 点击后的休息时间
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public void loopClickElement(String elementStr,long SleepTime, long Timeout, long explicitWaitTimeout) throws Exception {
		loopClick(elementStr, explicitWaitTimeout, Timeout);
		Thread.sleep(SleepTime);
	}

	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public String loopGetElementText(String elementStr, long Timeout, long explicitWaitTimeout) throws Exception {
		findElementAssert(elementStr,explicitWaitTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				String text = findElementNotAssert(elementStr, explicitWaitTimeout).getText();
				if(text.trim().length()>0){
					return text;
				}
			} catch (Exception e) {
			}
			Thread.sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return findElementNotAssert(elementStr, explicitWaitTimeout).getText();
	}

	/**
	 * 检查页面元素直到可见，如果可见，则返回true；如果元素不存在或者不可见，则循环检查，直到超时，超时仍不可见返回false
	 * 备注：1.如果元素可见是期望结果，则调用这个方法，如果单纯要判断元素是否可见(或超时不存在)，那么调用loopCheckElementVisible2
	 * 2.会在一段时间内判断元素是否可见，如果超时后，元素仍不可见(或不存在)，则根据assertFlag来执行断言失败，或者返回false
	 * @param elementStr 待检查的元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param assertFlag 断言标志，为true时则执行断言
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public boolean loopCheckElementVisible(String elementStr, long Timeout, boolean assertFlag) {
		if(assertFlag){
			if(!waitElementVisible(elementStr, Timeout)){
				Assert.fail();
			}
		}
		return waitElementVisible(elementStr, Timeout);
	}

	/**
	 * 检查页面元素是否可见，如果存在且可见，则返回1；如果存在且不可见，则返回2；如果不存在或其它异常，则重试直到超时，超时返回-1
	 * 备注：该方法不会截图
	 * @param elementStr 待检查的元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @return 1-页面元素存在且可见，2-页面元素存在且不可见，-1-超时检查，页面元素不存在或者其它异常
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-12-14,更新时间：2016-12-14
	 */
	public int loopCheckElementVisible2(String elementStr, long Timeout, long explicitWaitTimeout) {
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				element = findElementNotAssert(elementStr, explicitWaitTimeout);
				if(element != null){
					if(element.isDisplayed()){
						Log.logInfo("元素["+elementStr+"]可见");
						return 1;
					}else {
						Log.logInfo("元素["+elementStr+"]存在，但不可见");
						return 2;
					}
				}
				Thread.sleep(500);
				interval = System.currentTimeMillis()-starttime;
			} catch (Exception e) {
			}
		}
		Log.logInfo("元素["+elementStr+"]不可见");
		return -1;
	}

	/**
	 * 检查页面元素是否存在不可见，或者不存在，如果是，则返回true；如果元素存在且可见，则返回false
	 * @param elementStr 待检查的元素
	 * @param assertFlag 断言标志，为true时则执行断言
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-12-14,更新时间：2016-12-14
	 */
	public boolean loopCheckElementInvisibleNoExist(String elementStr, boolean assertFlag){
		if(assertFlag){
			if(isVisible(elementStr)){
				Assert.fail();
			}
		}
		return !isVisible(elementStr);
	}

	/**
	 * 获取页面元素的属性值，如果获取到，则返回；否则循环获取，直到超时
	 * @param elementStr 页面元素
	 * @param attributeStr 属性名
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-11-18,更新时间：2016-11-18
	 */
	public String loopGetElementValue(String elementStr, String attributeStr, long explicitWaitTimeout) throws Exception {
		return getAttributeValue(elementStr, explicitWaitTimeout, attributeStr);
	}

	/**
	 * 在一定时间内，检查页面元素是否会消失
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @throws Exception 所有异常抛出
	 * @author yuyang,创建时间：2016-11-22,更新时间：2016-11-22
	 */
	public boolean loopCheckDisappear(String elementStr, long Timeout) throws Exception{
		return waitElementInvisibility(elementStr, Timeout);
	}

	/**
	 * 给页面输入框元素清空后再输入字符串，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-11-24,更新时间：2016-11-24
	 */
	public void loopSendKeysClean(String inputStr, String keys, long explicitWaitTimeout) throws Exception{
		sendText(inputStr, explicitWaitTimeout, keys);
	}

	/**
	 * 给页面输入框元素清空，如果清空失败，则会重试，如果多次重试后仍然无法清空，则抛出自定义异常
	 * @param inputStr 输入框元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-12-16,更新时间：2017-02-20
	 * @更新说明(2017-02-20)：by yuyang,增加断言标志,来选择是否需要抛出异常
	 */
	public void loopSendClean(String inputStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(inputStr, explicitWaitTimeout);
		for (int i = 0; i < loopTimes; i++) {
			try {
				element.clear();
				Log.logInfo("成功清空"+inputStr);
				break;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模拟单击，把焦点移动到页面元素上，进行单击
	 * @param elementStr 元素字符串
	 * @throws Exception 抛出全部异常
	 * @author yuyang,创建时间：2016-11-24,更新时间：2017-01-24
	 * 更新说明(2017-01-24)：by yuyang,增加重试来解决模拟鼠标操作会抛出异常的问题,默认重试3次
	 */
	public void actionSingleClick(String elementStr) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeoutLoop);
		for (int i = 1; i <= 3; i++) {
			try {
				action.moveToElement(element).click().perform();
				break;
			} catch (Exception e) {
				Log.logWarn("模拟鼠标操作时发生异常(" + GetMethodName() + ").");
			}
		}
	}

	/**
	 * 模拟单击，把焦点移动到页面元素(x,y)上，进行单击
	 * @param elementStr 元素字符串
	 * @param x 滑动到页面元素上的x位置
	 * @param y 滑动到页面元素上的y位置
	 * @throws Exception 抛出全部异常
	 * @author yuyang，创建时间：2016-12-13,更新时间：2017-01-24
	 * 更新说明(2017-01-24)：by yuyang,增加重试来解决模拟鼠标操作会抛出异常的问题,默认重试3次
	 */
	public void actionSingleClickXY(String elementStr, int x, int y) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeoutLoop);
		for (int i = 1; i <= 3; i++) {
			try {
				action.moveToElement(element, x, y).perform();
				Thread.sleep(500);
				action.click().perform();
				Thread.sleep(1000);
				break;
			} catch (Exception e) {
				Log.logWarn("模拟鼠标操作时发生异常(" + GetMethodName() + ").");
			}
		}
	}

	/**
	 * 模拟右击
	 * @param elementStr 元素字符串
	 * @throws Exception 抛出全部异常
	 * @author yuyang, 创建时间：2016-12-08,更新时间：2017-01-24
	 * 更新说明(2017-01-24)：by yuyang,增加重试来解决模拟鼠标操作会抛出异常的问题,默认重试3次
	 */
	public void actionRightClick(String elementStr) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeoutLoop);
		for (int i = 1; i <= 3; i++) {
			try {
				action.contextClick(element).perform();
				Thread.sleep(1000);
				break;
			} catch (Exception e) {
				Log.logWarn("模拟鼠标操作时发生异常(" + GetMethodName() + ").");
			}
		}
	}

	/**
	 * 模拟移动，把焦点移动到页面元素上
	 * @param elementStr 元素字符串
	 * @throws Exception 抛出全部异常
	 * @author yuyang，创建时间：2016-12-23,更新时间：2017-01-24
	 * 更新说明(2017-01-24)：by yuyang,增加重试来解决模拟鼠标操作会抛出异常的问题,默认重试3次
	 */
	public void actionMoveTo(String elementStr) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeoutLoop);
		for (int i = 1; i <= 3; i++) {
			try {
				action.moveToElement(element).perform();
				Thread.sleep(1000);
				break;
			} catch (Exception e) {
				Log.logWarn("模拟鼠标操作时发生异常(" + GetMethodName() + ").");
			}
		}
	}

	/**
	 * get url,如果加载url失败，则重试
	 * @param url 页面地址
	 * @param timeout 超时时间
	 * @param loopTimes 重试次数
	 * @param timeoutDefault 默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang，创建时间：2016-11-28，更新时间：2016-11-28
	 */
	public void loopGet(String url, int timeout, int loopTimes, int timeoutDefault){
		get(url, timeout, loopTimes);
	}
	
	/**
	 * get url,如果加载url失败，则重试,用于navigate().to(url)方法
	 * @param url 页面地址
	 * @param timeout 超时时间
	 * @param loopTimes 重试次数
	 * @param timeoutDefault 默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author zhangjun
	 * 	 */
	
	public void navigateUrl(String url, int timeout, int loopTimes, int timeoutDefault){
		loopGet(url, timeout, loopTimes, timeoutDefault);
	}
	
	/**
	 * back url,如果返回到前一个页面失败，则重试,用于navigate().back()方法
	 * @param timeoutDefault 默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author zhangjun
	 * 	 */
	
	public void navigateBack( int timeoutDefault){
		browserBack(timeoutDefault);
	}
	
	/**
	 * 前进 url,如果返回到前一个页面失败，则重试,用于navigate().forward()方法
	 * @param timeout 超时时间
	 * @param timeoutDefault 默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author zhangjun
	 * 	 */
	
	public void navigateForward( int timeout, int timeoutDefault){
		driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);
		try {
			driver.navigate().forward();
		} catch (Exception e) { 
		}	
		driver.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
	}
	
	/**
	 *刷新 url,如果返回到前一个页面失败，则重试,用于navigate().refresh()方法
	 * @param timeout 超时时间
	 * @param loopTimes 重试次数
	 * @param timeoutDefault 默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author zhangjun
	 * 	 */
	
	public void navigateRefresh( int timeout, int loopTimes, int timeoutDefault){
		driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);
		for (int i = 0; i < loopTimes; i++) {
			try {
				driver.navigate().refresh();
				Log.logInfo("即将进行页面刷新完成");
				break;
			} catch (Exception e) { 
			}	
		}
		driver.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
	}
	
	
	/**
	 * 点击页面元素，如果点击成功，则返回；否则循环点击，直到超时,并且控制页面的超时时间
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param pageTime 页面加载耗时
	 * @param timeoutDefault  默认加载页面的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author zhangjun,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public void loopClickGoPageURL(String elementStr, long Timeout, long explicitWaitTimeout,int pageTime, int timeoutDefault) throws Exception {
		driver.manage().timeouts().pageLoadTimeout(pageTime, TimeUnit.SECONDS);
		loopClick(elementStr, explicitWaitTimeout, Timeout);
		driver.manage().timeouts().pageLoadTimeout(timeoutDefault, TimeUnit.SECONDS);
	}
	
	

	/**
	 * 模拟移动到元素，再单击元素，单击的可以另一个元素，通常用于滑动显示的菜单项的单击
	 * @param moveElementStr 移动到的元素
	 * @parma clickElementStr 单击的元素
	 * @throws Exception 抛出全部异常
	 * @author yuyang，创建时间：2016-11-28,更新时间：2017-01-24
	 * 更新说明(2017-01-24)：by yuyang,增加重试来解决模拟鼠标操作会抛出异常的问题,默认重试3次
	 */
	public void actionMoveThenClick(String moveElementStr, String clickElementStr) throws Exception {
		WebElement element = findElementAssert(moveElementStr, explicitWaitTimeoutLoop);
		for (int i = 1; i <= 3; i++) {
			try {
				action.moveToElement(element).perform();
				Thread.sleep(500);
				WebElement clickElement = findElementNotAssert(clickElementStr, explicitWaitTimeoutLoop);
				action.moveToElement(clickElement).perform();
				action.click().perform();
				Thread.sleep(1000);
				break;
			} catch (Exception e) {
				Log.logWarn("模拟鼠标操作时发生异常(" + GetMethodName() + ").");
			}
		}
	}

	/**
	 * 切换浏览器窗口到指定标题的窗口,只适用于web
	 * @param driver 浏览器驱动
	 * @param windowTitle 待切换到窗口的标题
	 * @return true或false,表示是否切换窗口成功
	 * @throws InterruptedException 
	 * @author yuyang 创建时间:2016-09-1更新时间:2016-12-06
	 * 更新说明(2016-12-06)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public boolean switchToWindow(String windowTitle) throws Exception {
		boolean flag = false;
		try {
			String currentHandle = driver.getWindowHandle();
			if(windowTitle.equals(currentHandle)){
				return true;
			}
			Set<String> handles = driver.getWindowHandles();
			for (String string : handles) {
				if(driver.switchTo().window(string).getTitle().contains(windowTitle)){
					//driver.switchTo().window(string);
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
		}
		if(!flag){
			Log.logInfo("Window: " + windowTitle + " cound not found!(switchToWindow)");
		}
		return flag;
	}

	/**
	 * 获取运行所使用的浏览器信息,只适用于web
	 * @return 浏览器信息，如版本等
	 * @author yuyang,创建时间：2016-09-07 更新时间:2016-12-07
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public String getBrowserInfo() {
		String JS_getBrowserInfo = "var browser_info = window.navigator.userAgent;return browser_info;";
		String res = "";
		try {
			res = (String) (jse).executeScript(JS_getBrowserInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 单击页面元素，会打开一个新页面，如果未打开，则尝试再次单击，如果多次重试后仍然无法单击并打开一个新页面，则抛出自定义异常,只适用于web
	 * @param clickStr 用来作单击动作的元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-04 更新时间:2016-12-07
	 * 更新说明(2016-10-12)：用来作单击动作的元素，由 必须存在 改为 等待出现
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public void loopClickPage(String clickStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(clickStr, explicitWaitTimeout);
		int initWindowCount = driver.getWindowHandles().size();
		for (int i = 0; i < loopTimes; i++) {
			element.click();
			if(driver.getWindowHandles().size()>initWindowCount){
				break;
			}
			Thread.sleep(1000);
		}
	}

	/**
	 * 检查页面输入框元素的属性value的值是否为期望值，如果是，则返回；如果不是，则循环检查，直到超时
	 * @param inputStr 输入框元素
	 * @param expectedValueStr 期望值
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param printFlag 是否打印，第一次查找到元素失败的标志，true-打印，false-不打印
	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-12-07
	 * 注意：用于购物车页面的获取商品数量
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op
	 */
	public boolean loopCheckInputValue(String inputStr, String expectedValueStr, long Timeout, long explicitWaitTimeout, boolean printFlag) throws Exception{
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				if(findElementAssert(inputStr).getAttribute("value").equals(expectedValueStr)){
					return true;
				}
			} catch (Exception e) {
			}
			Thread.sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}

	/**
	 * 检查页面元素的值是否为期望值，如果是，则返回；如果不是，则循环检查，直到超时
	 * @param priceStr 输入框元素
	 * @param expectedPriceStr 期望值
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-12-07
	 * 注意：主要用于购物车页面的获取价格变化后的值
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op
	 */
	public String loopCheckPriceValue(String priceStr, String expectedPriceStr, long Timeout, long explicitWaitTimeout) throws Exception {
		if(waitTextToBe(priceStr, expectedPriceStr, Timeout)){
			return expectedPriceStr;
		}
		return "";
	}

	/**
	 * 页面元素执行js脚本，如果执行成功，则返回；否则循环执行，直到超时;无返回字符串值,只适用于web
	 * @param elementStr 页面元素
	 * @param jsStr js脚本字符串
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout  显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-13,更新时间：2016-12-07
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public void loopExecuteScript(String elementStr, String jsStr, long Timeout, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				jse.executeScript(jsStr, element);
				break;
			} catch (Exception e) {
			}
			Thread.sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
	}

	/**
	 * 页面元素执行js脚本，并获取返回字符串，如果执行成功，则返回；否则循环执行，直到超时,只适用于web
	 * @param elementStr 页面元素
	 * @param jsStr js脚本字符串
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout  显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-14,更新时间：2016-12-07
	 * 备注：如果执行js后返回的字符串如果为空，认为获取失败
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public String loopExecuteScriptStr(String elementStr, String jsStr, long Timeout, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(elementStr, explicitWaitTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				return jse.executeScript(jsStr, element).toString();
			} catch (Exception e) {
				Thread.sleep(500);
				interval = System.currentTimeMillis()-starttime;
			}
		}
		return "";
	}

	/**
	 * 给页面输入框元素输入回车，如果不成功则重试，直到超时
	 * @param inputStr 输入框元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-17,更新时间：2016-12-07
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op
	 */
	public void loopSendKeysEnter(String inputStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(inputStr, explicitWaitTimeout);
		for (int i = 0; i < loopTimes; i++) {
			try {
				element.sendKeys(Keys.chord(Keys.ENTER));
				break;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 判断对象是否存在
	 * @param by 待检查的元素,如id,xpath等
	 * @return boolean,true-查找到，false-未查找到
	 * @author yuyang 创建时间:2016-10-17,更新时间：2016-12-07
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op,可兼容web和wap
	 */
	public boolean isElementPresent(String elementStr){
		return isExist(elementStr);
	}
	
	/**
	 * 判断对象是否存在，增加显示等待元素的时间，用元素显示等待时间判断元素是否存在
	 * @param 通过显示等待元素，判断元素是否显示
	 * @return boolean,true-查找到，false-未查找到
	 * @author zhangjun 
	 *
	 */
	public boolean isElementPresent(String elementStr,long explicitWaitTimeout) {
		return isExist(elementStr, explicitWaitTimeout);
	}
	

	/**
	 * 给非空的页面输入框元素输入字符串，即输入前先检查该输入框是否为空，如果不为空，则输入，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-20,更新时间：2016-12-07
	 * 更新说明(2016-12-07)：by yuyang,由PublicFun移动到Op
	 */
	public void loopCheckSendKeys(String inputStr, String keys, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(inputStr, explicitWaitTimeout);
		if(element.getAttribute("value").trim().length() > 0){
			return;
		}else {
			for (int i = 0; i < loopTimes; i++) {
				try {
					element.sendKeys(keys);
					if(getAttributeValue(inputStr, explicitWaitTimeout, "value").equals(keys)){
						break;
					}
				} catch (Exception e) {
				}
				Thread.sleep(100);
			}
		}
	}
	/**
	 * 使用鼠标滑动到页面元素上的 (x,y)位置，单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常,只适用于web
	 * @param clickStr 用来作单击动作的元素
	 * @param x 滑动到页面元素上的x位置
	 * @param y 滑动到页面元素上的y位置
	 * @param FindStr 用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-10-24,更新时间：2016-12-08
	 * 注意：用来作单击动作的元素，可是等待出现
	 * 更新说明(2016-12-08)：by yuyang,由PublicFun移动到Op,只适用于web
	 */
	public void loopClickMoveTo(String clickStr, int x, int y, String FindStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(clickStr, explicitWaitTimeout);
		for (int i = 0; i <= loopTimes; i++) {
			try {
				action.moveToElement(element, x, y).perform();
				Thread.sleep(500);
				action.click().perform();
				if(findElementNotAssert(FindStr, explicitWaitTimeout)!=null){
					Log.logInfo("点击["+clickStr+"]后，找到了元素["+FindStr+"]");
					return;
				}
			} catch (Exception e) {
			}
			Thread.sleep(500);
		}
		Log.logInfo("点击["+clickStr+"]后，没有找到元素["+FindStr+"]");
	}

	/**
	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，如果查找不到，则尝试查找第二个新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素们，则抛出自定义异常
	 * @param clickStr 用来作单击动作的元素
	 * @param FindStr 用来查找的新页面元素
	 * @param FindStr2  未查找到FindStr时，用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-10-27,更新时间：2016-12-08
	 * 更新说明(2016-12-08)：by yuyang,由PublicFun移动到Op
	 */
	public void loopClick2(String clickStr, String FindStr, String FindStr2, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(clickStr, explicitWaitTimeout);
		for (int i = 0; i <= loopTimes; i++) {
			try {
				element.click();
				if(findElementNotAssert(FindStr, explicitWaitTimeout)!=null){
					Log.logInfo("点击["+clickStr+"]后，找到了元素["+FindStr+"]");
					return;
				}
				if(findElementNotAssert(FindStr2, explicitWaitTimeout)!=null){
					Log.logInfo("点击["+clickStr+"]后，找到了元素["+FindStr2+"]");
					return;
				}
			} catch (Exception e) {
			}
			Thread.sleep(500);
		}
		Log.logInfo("点击["+clickStr+"]后，没有找到元素["+FindStr+"]和["+FindStr2+"]");
	}

	/**
	 * 获取当前URL，是否包含或等于指定字符串，如果包含或等于，则返回true
	 * @param URLStr URL地址字符串
	 * @param Timeout 超时时间，单位为秒
	 * @param mode 1-包含,2-等于
	 * @throws Exception 除超时查找元素的异常之外的其它异常抛出
	 * @author yuyang ,创建时间：2016-11-02,更新时间：2016-12-08
	 * 更新说明(2016-12-08)：by yuyang,由PublicFun移动到Op
	 */
	public boolean CheckURL(String URLStr, long Timeout, int mode) throws Exception {
		if(mode==1){
			return urlToContains(URLStr, Timeout, false);
		}else if (mode==2) {
			return urlToBe(URLStr, Timeout, false);
		}
		return false;
	}

	/**
	 * 如果当前为重试执行，则刷新页面
	 * @param lastMethodName 上一次执行的方法名
	 * @param currentMethodName 当前执行的方法名
	 * @author yuyang ,创建时间：2016-11-12,更新时间：2016-12-08
	 * 备注：需要传入正确的上一次执行的方法名
	 * 更新说明(2016-11-30)：by yuyang,增加了navigate刷新页面失败时，使用get方法的再打开一次
	 * 更新说明(2016-12-08)：by yuyang,由PublicFun移动到Op
	 */
	public void retryRefresh(String lastMethodName, String currentMethodName) {
		try {
			if (Pub.RetryFlag(lastMethodName, currentMethodName)) {
				driver.navigate().refresh();
			}
		} catch (Exception e) {
			Log.logWarn("重试时重新获取当前页面失败,测试中止.");
		}
	}

	/**
	 * 获取当前页面的URL
	 * @param assertFlag 断言标志，true-则执行断言，false-则不执行断言
	 * @author yuyang ,创建时间：2016-11-23,更新时间：2016-12-08
	 * 备注：主要为了规避UnhandledAlertException弹窗异常，才封装了这个方法
	 * 更新说明(2016-12-08)：by yuyang,由PublicFun移动到Op
	 */
	public String MyGetCurrentUrl(boolean assertFlag){
		String currentURL = "";
		for (int i = 0; i <= 10; i++) {
			try {
				currentURL = driver.getCurrentUrl();
				return currentURL;
			} catch (UnhandledAlertException e) {
				driver.switchTo().alert().accept();
			}
		}
		if(assertFlag){
			if(currentURL.length()<0){
				Assert.fail();
			}
		}
		return currentURL;
	}

	/**
	 * 在一定时间内，等待页面元素，且该页面元素可用和可被单击，如果等待到，则返回页面元素，否则返回null，不抛出异常
	 * @param elementStr 页面元素
	 * @param explicitWaitTimeout  超时时间，单位为秒
	 * @author yuyang,创建时间：2016-12-08,更新时间：2016-12-08
	 * 备注：与MyWebDriverWait2相似，返回的元素是可用和可被单击的
	 */
	public WebElement MyWebDriverWaitClickable(String elementStr, long explicitWaitTimeout) throws Exception{
		boolean res = false;
		WebElement element = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementStr);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		element = (new WebDriverWait(driver, explicitWaitTimeout))
							.until(ExpectedConditions.elementToBeClickable(locator.getBy(map, type)));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementStr+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
		    	explicitWaitTimeout=1;
			    if(element!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementStr + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn(explicitWaitTimeout + "seconds超时 ,未等待到页面元素(" + elementStr + ")存在.");
			throw exception;
		}
	
		return element;
		
		//return new WebDriverWait(driver, explicitWaitTimeout).until(ExpectedConditions.elementToBeClickable(locator.getBy(elementStr)));
	}

	/**
	 * 在一定时间内，等待页面元素，且该页面元素可用和可被单击，如果等待到，则返回true,否则返回false
	 * @param elementStr 页面元素
	 * @param explicitWaitTimeout  超时时间，单位为秒
	 * @param ePrintFlag 查找不到元素时是否打印异常信息的标志，true-打印，false-不打印
	 * @param printFlag 是否打印查找或未查找到页面元素的信息标志，true-打印，false-不打印
	 * @author yuyang,创建时间：2016-12-08,更新时间：2016-12-08
	 */
	public boolean isElementClickable(String elementStr, long explicitWaitTimeout) {
		try {
			if (MyWebDriverWaitClickable(elementStr, explicitWaitTimeout) != null) {
				return true;
			}
		} catch (Exception e) {
			Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
		}
		return false;
	}

	/**
	 * 获取下拉选择对象
	 * @param elementStr select页面元素
	 * @author linchaojiang,创建时间：xxxx-xx-xx,更新时间：2016-12-08
	 * 更新说明(2016-12-08)：by yuyang,由Locator移动到Op
	 */
	public Select getSelect(String elementStr, long explicitWaitTimeout) throws Exception{
		WebElement element = findElementAssert(elementStr, explicitWaitTimeout);
		Select sel = null;
		try {
			sel = new Select(element);
		} catch (Exception e) {
			Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
		}
		return sel;
	}

	/**
	 * 获取相同属性的所有对象
	 * @param elementsStr 页面元素
	 * @return 页面元素列表
	 * @author linchaojiang,创建时间：2016-12-08,更新时间：2016-12-08
	 */
	public List<WebElement> getElements(String elementsStr) throws Exception{
		return findElementsAssert(elementsStr);
	}

	/**
	 * 获取所有cookie的集合
	 * @return cookie集合
	 * @author yuyang,创建时间：2016-12-15,更新时间：2016-12-15
	 */
	public Set<Cookie> getCookies() {
		return driver.manage().getCookies();
	}

	/**
	 * 打印cookies信息
	 * @author yuyang,创建时间：2016-12-15,更新时间：2016-12-15
	 */
	public void printCookies(Set<Cookie> cks) {
		System.out.println("cookie总数为" + cks.size());
		int i = 0;
		for (Cookie ck : cks) {
			i++;
			System.out.println(i + ",作用域：" + ck.getDomain());
			System.out.println(i + ",名称：" + ck.getName());
			System.out.println(i + ",值：" + ck.getValue());
			System.out.println(i + ",范围：" + ck.getPath());
			System.out.println(i + ",过期时间" + ck.getExpiry());
		}
	}

	/**
	 * 获取当前窗口数量
	 * @return 当前的窗口数量
	 * @author yuyang,创建时间：2016-12-15,更新时间：2016-12-15
	 */
	public int getWindowNum() {
		return driver.getWindowHandles().size();
	}

	/**
	 * 慢速给页面输入框元素清空后再输入字符串，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
	 * 备注：与loopSendKeysClean的区别是，慢速输入，慢速输入是指，在输入字符串之后，延时1秒后再检查字符串是否输入成功,主要用在GB购物车页面的数量输入框输入数量
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2016-12-22,更新时间：2016-12-22
	 */
	public void loopSendKeysCleanSlow(String inputStr, String keys, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(inputStr, explicitWaitTimeout);
		for (int i = 0; i < loopTimes; i++) {
			try {
				element.sendKeys(Keys.chord(Keys.CONTROL, "a"), keys);
				Thread.sleep(1000);
				if(getAttributeValue(inputStr, explicitWaitTimeout, "value").equals(keys)){
					break;
				}
			} catch (Exception e) {
			}
			Thread.sleep(500);
		}
	}

	/**
	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常。
	 * 第二次尝试单击时， 如果找不到单击的页面元素，仍然查找新的页面元素，并且在查找新元素前会执行刷新页面的动作
	 * 备注:与loopClick不同的地方是，第二次尝试单击时， 如果找不到单击的页面元素，在查找新元素前会执行刷新页面的动作
	 * @param clickStr 用来作单击动作的元素
	 * @param FindStr 用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang ,创建时间：2017-01-19,更新时间：2017-01-19
	 */
	public void loopClickRefresh(String clickStr, String FindStr, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(clickStr, explicitWaitTimeout);
		for (int i = 0; i < loopTimes; i++) {
			try {
				element.click();
				if(findElementNotAssert(FindStr, explicitWaitTimeout)!=null){
					return;
				}
			} catch (Exception e) {
			}
		}
		Thread.sleep(500);
	}

	/**
	 * 给页面输入框元素输入字符串，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，也不抛出自定义异常,直接返回true或false
	 * 备注:与loopSendKeys的区别是，不会抛出异常，成功输入字符串会返回true，失败输入字符串返回false
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @return 成功输入字符串会返回true，失败输入字符串返回false
	 * @author yuyang,创建时间：2017-01-24,更新时间：2017-01-24
	 */
	public boolean loopSendKeysCheck(String inputStr, String keys, int loopTimes, long explicitWaitTimeout) throws Exception {
		WebElement element = findElementAssert(inputStr, explicitWaitTimeout);
		for (int i = 0; i <= loopTimes; i++) {
			try {
				if (i > 1) {
					element.sendKeys(Keys.chord(Keys.CONTROL, "a"), keys);
				} else {
					element.sendKeys(keys);
				}
				Thread.sleep(100);
				if(getAttributeValue(inputStr, explicitWaitTimeout, "value").equals(keys)){
					return true;
				}
			} catch (Exception e) {
			}
		}
		Log.logInfo("输入字符串失败");
		return false;
	}

	/**
	 * 检查页面元素直到可见(可显示或可使用)，如果可见(可显示或可使用)，则返回true；如果元素不存在或者不可见(可显示或可使用)，则循环检查，直到超时，超时仍不可见返回false
	 * 备注：1.如果元素可见(可显示或可使用)是期望结果，则调用这个方法，如果单纯要判断元素是否可见(或超时不存在)，那么调用loopCheckElementVisible2
	 * 2.会在一段时间内判断元素是否可见(可显示或可使用)，如果超时后，元素仍不可见(或不存在)，则根据assertFlag来执行断言失败，或者返回false
	 * @param elementStr 待检查的元素
	 * @param Timeout 超时时间，单位为秒
	 * @param diplayCheck 检查是否可显示的标志
	 * @param enableCheck 检查是否可使用的标志
	 * @param assertFlag 断言标志，为true时则执行断言
	 * @author yuyang,创建时间：2017-01-24,更新时间：2017-01-24
	 */
	public boolean loopCheckElementVisible(String elementStr, long Timeout, boolean diplayCheck, boolean enableCheck,
			boolean assertFlag) {
		boolean resDisplay = true,resEnable = true,res = false;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				if(diplayCheck){
					resDisplay = isVisible(elementStr);
				}
				if(enableCheck){
					resEnable = isVisible(elementStr);
				}
				if(resDisplay && resEnable){
					res = true;
					break;
				}
				
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		if(assertFlag){
			if(!res){
				Assert.fail();
			}
		}
		return res;
	}



	/**
	 * 页面元素有多个时,点击指定文本的页面元素,如果点击成功,则返回;否则循环点击,直到超时
	 * @param elementStr 页面元素
	 * @param elementText 指定的页面元素的文本值
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang 创建时间:2017-03-16 更新时间:2017-03-16
	 */
	public void loopClickElement(String elementStr, String elementText, long Timeout, long explicitWaitTimeout)
			throws Exception {
		List<WebElement> list = findElementsAssert(elementStr, explicitWaitTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		for (WebElement element : list) {
			if(element.getText().equals(elementText)){
				while (interval<Timeout*1000) {
					try {
						element.click();
						Log.logInfo("成功点击["+elementStr+"]");
						return;
					} catch (Exception e) {
					}
					Thread.sleep(500);
					interval = System.currentTimeMillis()-starttime;
				}
			}else {
				Log.logInfo("没有找到对应文本的元素对象");
			}
		}
	}

	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * 备注:传入页面元素类型是WebElement
	 * @param element 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2017-03-18,更新时间：2017-03-18
	 */
	public String loopGetElementText(WebElement element, long Timeout) throws Exception {
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<Timeout*1000) {
			try {
				element.getText();
				Log.logInfo("成功获取元素文本值["+element.getText()+"]");
				return element.getText();
			} catch (Exception e) {
			}
			Thread.sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		Log.logInfo("成功获取元素文本值超时");
		return "";
	}



	/**
	 * 获取父元素下的相同属性的所有对象
	 * 备注：重写getElements()方法,会先查找父元素,再查找子元素列表
	 * @param elementsStr 页面元素
	 * @return 获取子页面元素列表
	 * @author yuyang,创建时间：2017-04-02,更新时间：2017-04-02
	 */
	public List<WebElement> getElements(String ParentStr, String elementsStr) throws Exception{
		WebElement parentElement = MyWebDriverWait2(ParentStr, explicitWaitTimeoutLoop, false);
		List<WebElement> elementList = new ArrayList<WebElement>();
//		elementList = parentElement.findElements(locator.getBy(elementsStr));
//		return elementList;
		
		boolean res = false;
		WebElement element = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementsStr);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		elementList =  parentElement.findElements(locator.getBy(map, type));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementsStr+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
			    if(element!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementsStr + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn( "未等待到页面元素(" + elementsStr + ")存在.");
			throw exception;
		}
	
		return elementList;
	}
	
	/**
	 * 获取父元素下的相同属性的所有对象
	 * 备注：重写getElements()方法,会先查找父元素,再查找子元素列表
	 * @param elementsStr 页面元素
	 * @return 获取子页面元素列表
	 * @author yuyang,创建时间：2017-04-02,更新时间：2017-04-02
	 */
	public List<WebElement> getElements(WebElement parentElement, String elementsStr) throws Exception{
		boolean res = false;
		List<WebElement> element = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementsStr);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		element =  parentElement.findElements(locator.getBy(map, type));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementsStr+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
			    if(element!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementsStr + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn( "未等待到页面元素(" + elementsStr + ")存在.");
			throw exception;
		}
	
		return element;
	}
	
	/**
	 * 获取父元素下的相同属性的所有对象
	 * 备注：重写getElements()方法,会先查找父元素,再查找子元素列表
	 * @param elementsStr 页面元素
	 * @return 获取子页面元素列表
	 * @author yuyang,创建时间：2017-04-02,更新时间：2017-04-02
	 */
	public WebElement getElement(WebElement parentElement, String elementsStr) throws Exception{
		boolean res = false;
		WebElement element = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementsStr);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		element =  parentElement.findElement(locator.getBy(map, type));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementsStr+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
			    if(element!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementsStr + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn( "未等待到页面元素(" + elementsStr + ")存在.");
			throw exception;
		}
	
		return element;
	}
	
	/**
	 * 访问url，且设置页面加载时间
	 * @param url url链接
	 * @param pageLoadTimeout 页面加载超时时间,单位秒
	 * @author jasonwang
	 */
	public void get(String url, long pageLoadTimeout){
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
		try {
			driver.get(url);
			Log.logInfo("成功访问["+url+"]");
		} catch (Exception e) {
			Log.logInfo("访问【"+url+"】超时！");
		}	
		driver.manage().timeouts().pageLoadTimeout(defaultPageLoadTimeoutMax, TimeUnit.SECONDS);
	}
	
	/**
	 * 访问url，且设置页面加载时间,设置重试次数
	 * @param url url链接
	 * @param pageLoadTimeout 页面加载超时时间,单位秒
	 * @param loopTimes 重试次数
	 * @author jasonwang
	 */
	public  void get(String url, long pageLoadTimeout, int loopTimes){
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
		for (int i = 1; i <= loopTimes; i++) {
			try {
				driver.get(url);
				if(driver.getCurrentUrl().equals(url)){
					Log.logInfo("成功访问【"+url+"】");
					break;
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				Log.logInfo("访问【"+url+"】超时！");
			}
		}
		driver.manage().timeouts().pageLoadTimeout(defaultPageLoadTimeoutMax, TimeUnit.SECONDS);
	}

	/**
	 * 页面刷新
	 * @param pageLoadTime 等待元素超时时间,单位秒
	 * @author jasonwang
	 */
	public  void pageRefresh(long pageLoadTime){
		driver.manage().timeouts().pageLoadTimeout(pageLoadTime, TimeUnit.SECONDS);
		try {
			driver.navigate().refresh();
			Log.logInfo("成功刷新页面");
		} catch (Exception e) {
			Log.logInfo("页面加载超时，继续往下执行");
		}	
		driver.manage().timeouts().pageLoadTimeout(defaultPageLoadTimeoutMax, TimeUnit.SECONDS);
	}
	
	/**
	 * 查找元素
	 * @param elementName 元素定位值
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  WebElement findElementAssert(String elementName) throws Exception{
		boolean res = false;
		WebElement element = null;
		String type="";
		String value="";
		Map<String,String> map=locator.getMap(elementName);
		for (Map.Entry<String, String> entry : map.entrySet()) {  
			type=entry.getKey();
    	    value=entry.getValue();
	    	try{
	    		element = driver.findElement(locator.getBy(map, type));
	    	}catch(Exception e){
	    		Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementName+",但未查找到，请检查元素对象！");
	    		 exception=e;
	    	}
		    if(element!=null){
		    	 res = true;
		    	 break;
		    }

		}  
		if (res) {
				Log.logInfo("等待到页面元素(" + elementName + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn("未等待到页面元素(" + elementName + ")存在.");
			throw exception;
		}
		return element;
	//	return driver.findElement(locator.getBy(elementName));
	}
	/**
	 * 查找元素，设置智能等待元素时间
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间,单位秒
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  WebElement findElementAssert(String elementName ,long waitElementTimeout)throws Exception{
		boolean res = false;
		WebElement element = null;
		String type="";
		String value="";
		Map<String,String> map=locator.getMap(elementName);
		for (Map.Entry<String, String> entry : map.entrySet()) {  
			type=entry.getKey();
    	    value=entry.getValue();
	    	try{
	    		element = (new WebDriverWait(driver, waitElementTimeout))
						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(map, type)));
	    	}catch(Exception e){
	    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementName+",但未查找到，请检查元素对象！");
	    		 exception=e;
	    	}
	    	waitElementTimeout=1;
		    if(element!=null){
		    	 res = true;
		    	 break;
		    }
		}  
		if (res) {
				Log.logInfo("等待到页面元素(" + elementName + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn(waitElementTimeout + "seconds超时 ,未等待到页面元素(" + elementName + ")存在.");
			throw exception;
		}
		return element;
	}
	
	/**
	 * 查找元素
	 * @param elementName 元素定位值
	 * @return 元素存在则返回元素对象，否则返回null
	 * @author jasonwang
	 */
	public  WebElement findElementNotAssert(String elementName){
		WebElement element = null;
		try {
			element = findElementAssert(elementName);
		} catch (Exception e) {
			Log.logInfo("元素["+elementName+"]不存在");
		}
		return element;
	}
	/**
	 * 查找元素，设置智能等待元素超时时间
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 元素存在则返回元素对象，否则返回null
	 * @author jasonwang
	 */
	public  WebElement findElementNotAssert(String elementName ,long waitElementTimeout){
		WebElement element = null;
		try {
			element = findElementAssert(elementName, waitElementTimeout);
		} catch (Exception e) {
			Log.logInfo("元素["+elementName+"]不存在");
		}
		return element;
	}
	
	/**
	 * 查找一组元素
	 * @param elementName 元素定位值
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  List<WebElement> findElementsAssert(String elementName) throws Exception{
		boolean res = false;
		List<WebElement> list = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementName);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		list = driver.findElements(locator.getBy(map, type));
		    	}catch(Exception e){
		    		// Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementName+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
			    if(list!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementName + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn("未等待到页面元素(" + elementName + ")存在.");
			throw exception;
		}
	
		return list;
		
		
	//	return driver.findElements(locator.getBy(elementName));
	}
	/**
	 * 查找一组元素，且设置智能等待元素超时时间
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  List<WebElement> findElementsAssert(String elementName ,long waitElementTimeout)throws Exception{
		WebDriverWait wait = new WebDriverWait(driver, waitElementTimeout);
		boolean res = false;
		List<WebElement> list = null;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementName);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.getBy(map, type)));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementName+",但未查找到，请检查元素对象！");
		    		 exception=e;
		    	}
		    	waitElementTimeout=1;
			    if(list!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementName + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn(waitElementTimeout+"秒后，未等待到页面元素(" + elementName + ")存在.");
			throw exception;
		}
	
		return list;
		
		
//		List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator.getBy(elementName)));
//		return elements;
	}
	/**
	 * 查找一组元素
	 * @param elementName 元素定位值
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  List<WebElement> findElementsNotAssert(String elementName) {
		List<WebElement> elements = new ArrayList<>();
		try {
			elements =findElementsAssert(elementName);
		} catch (Exception e) {
			Log.logInfo("查找元素出现异常");
		}
		return elements;
	}
	
	public  List<WebElement> findElementsNotAssert(String elementName ,long waitElementTimeout){
		List<WebElement> elements = new ArrayList<>();
		try {
			elements = findElementsAssert(elementName, waitElementTimeout);
		} catch (Exception e) {
		}
		return elements;
	}
	
	
	/**
	 * 根据查找元素的方式和值获取By对象
	 * @param type 元素定位方式
	 * @param value 元素定位值
	 * @return by对象
	 * @author jasonwang
	 */
	private static By getBy(String type,String value){
		By by = null;
		try {
			if (type.equals("id")) {
				by = By.id(value);
			} else if (type.equals("name")) {
				by = By.name(value);
			} else if (type.equals("xpath")) {
				by = By.xpath(value);
			} else if (type.equals("className")) {
				by = By.className(value);
			} else if (type.equals("linkText")) {
				by = By.linkText(value);
			} else if (type.equals("css")) {
				by = By.cssSelector(value);
			} else {
				Log.logInfo("返回by对象出错！！！");
			}
		} catch (Exception e) {
		}
		return by;
	}
	
	/**
	 * 根据查找元素的方式和值获取元素，设置查找元素智能等待时间
	 * @param type 元素定位方式
	 * @param value 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 元素存在则返回元素对象，否则抛出异常
	 * @author jasonwang
	 */
	public  WebElement findElementByItemsAssert(String type,String value,long waitElementTimeout) throws Exception{
		WebElement element = null;
		WebDriverWait wait = new WebDriverWait(driver, waitElementTimeout);
		element = wait.until(ExpectedConditions.presenceOfElementLocated(getBy(type,value)));
		return element;
    }
	
	/**
	 * 根据查找元素的方式和值获取元素，设置查找元素智能等待时间
	 * @param type 元素定位方式
	 * @param value 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 元素存在返回元素对象，否则返回null
	 * @author jasonwang
	 */
	public  WebElement findElementByItemsNotAssert(String type,String value,long waitElementTimeout){
		WebElement element = null;
		WebDriverWait wait = new WebDriverWait(driver, waitElementTimeout);
		try {
			element = wait.until(ExpectedConditions.presenceOfElementLocated(getBy(type,value)));
		} catch (Exception e) {
			Log.logInfo("获取元素["+value+"]失败");
		}
		return element;
    }
	
	/**
	 * 根据查找元素的方式和值获取一组元素，设置查找元素智能等待时间
	 * @param type 元素定位方式
	 * @param value 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 返回元素列表，出现异常抛出
	 * @author jasonwang
	 */
	public  List<WebElement> findElementsByItemAssert(String type,String value,long waitElementTimeout) throws Exception{
		List<WebElement> elements = null;
		WebDriverWait wait = new WebDriverWait(driver, waitElementTimeout);
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getBy(type,value)));
		return elements;
    }
	/**
	 * 根据查找元素的方式和值获取一组元素，设置查找元素智能等待时间
	 * @param type 元素定位方式
	 * @param value 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 返回元素列表
	 * @author jasonwang
	 */
	public  List<WebElement> findElementsByItemNotAssert(String type,String value,long waitElementTimeout){
		List<WebElement> elements = null;
		WebDriverWait wait = new WebDriverWait(driver, waitElementTimeout);
		try {
			elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getBy(type,value)));
		} catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("获取元素["+value+"]组出现异常");
		}
		return elements;
    }
	
	/**
	 * 检查勾选框是否被选中
	 * @param elepath 元素定位值
	 * @return 如果元素存在且已勾选返回true，存在未勾选返回false，不存在则抛出异常
	 * @author jasonwang
	 */
	public  boolean isSelected(String elementName) throws Exception{
		WebElement element = findElementAssert(elementName);
		try {
			if(element.isSelected()){
				Log.logInfo("元素【"+elementName+"】已勾选");
				return true;
			}else{
				Log.logInfo("元素【"+elementName+"】未勾选");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 检查勾选框是否被选中,查找元素时进行智能等待
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间(秒)
	 * @return 如果元素存在且已勾选返回true，存在未勾选返回false，不存在则抛出异常
	 * @author jasonwang
	 */
	public  boolean isSelected(String elementName ,long waitElementTimeout) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		try {
			if(element.isSelected()){
				Log.logInfo("元素【"+elementName+"】已勾选");
				return true;
			}else{
				Log.logInfo("元素【"+elementName+"】未勾选");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 等待勾选框被选中
	 * @param elepath 元素定位值
	 * @param timeout 等待超时时间,单位秒
	 * @return 如果在指定时间内元素已被勾选上返回true,超时返回false
	 * @author jasonwang
	 */
	public  boolean waitElementSelected(String elementName ,long timeout){
		WebElement element = null;
		boolean res = false;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).isSelected()){
					Log.logInfo("元素【"+elementName+"】已勾选");
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		Log.logInfo("等待元素["+elementName+"]勾选上超时");
		return res;
	}
	
	/**
	 * 检查元素是否存在
	 * @param elepath 元素定位值
	 * @return 如果元素存在返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean isExist(String elementName) {
		WebElement element = null;
		try {
			element = findElementAssert(elementName);
			Log.logInfo("元素["+elementName+"]存在");
			return true;
		} catch (Exception e) {
		}
		Log.logInfo("元素["+elementName+"]不存在");
		return false;
	}
	
	/**
	 * 检查元素是否存在
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待超时时间，单位秒
	 * @return 如果在指定时间内元素存在返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean isExist(String elementName ,long waitElementTimeout){
		try {
			WebElement element = findElementAssert(elementName, waitElementTimeout);
			if(element!=null){
				Log.logInfo("元素["+elementName+"]存在");
				return true;
			}
		} catch (Exception e) {
		}
		srceenShots(elementName);
		Log.logInfo("元素["+elementName+"]不存在dom中");
		return false;
	}
	/**
	 * 检查元素是否可见
	 * @param elepath 元素定位值
	 * @return 如果元素可见返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean isVisible(String elementName){
		WebElement element = null;
		try {
			element = findElementAssert(elementName);
			if(element.isDisplayed()){
				Log.logInfo("元素【"+elementName+"】可见");
				return true;
			}
		} catch (Exception e) {
		}
		Log.logInfo("元素【"+elementName+"】不可见");
		return false;
	}
	/**
	 * 检查元素是否可见
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待超时时间，单位秒
	 * @return 如果在指定时间内元素可见返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean isVisible(String elementName ,long waitElementTimeout){
		WebElement element = findElementNotAssert(elementName, waitElementTimeout);
		try {
			if(element == null){
				Log.logInfo("元素【"+elementName+"】不可见");
				return false;
			}
			if(element.isDisplayed()){
				Log.logInfo("元素【"+elementName+"】可见");
				return true;
			}
		} catch (Exception e) {
		}
		Log.logInfo("元素【"+elementName+"】不可见");
		return false;
	}
	
	/**
	 * 等待元素可见
	 * @param elepath 元素定位值
	 * @param timeout 等待超时时间，单位秒
	 * @return 如果在指定时间内元素可见返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean waitElementVisible(String elementName ,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).isDisplayed()){
					Log.logInfo("元素["+elementName+"]显示");
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		//srceenShots(elementName);
		Log.logInfo("等待元素["+elementName+"]显示超时");
		return false;
	}
	
	/**
	 * 睡眠一段时间
	 * @param sleeptime 睡眠时间(毫秒)
	 */
	private void sleep(int sleeptime) {
		try {
			Thread.sleep(sleeptime);
		} catch (Exception e) {
		}
	}
	/**
	 * 等待元素消失
	 * @param elementName 元素定位值
	 * @param timeout 等待超时时间，单位秒
	 * @return 如果在指定时间内元素消失返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean waitElementInvisibility(String elementName ,long timeout){
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(!isExist(elementName)){
					Log.logInfo("元素["+elementName+"]消失");
					return true;
				}else {
					if(!isVisible(elementName)){
						Log.logInfo("元素["+elementName+"]存在，但是不可见");
						return true;
					}
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		//srceenShots(elementName);
		Log.logInfo("等待元素["+elementName+"]消失超时");
		return false;
	}
	
	/**
	 * 检查元素是否可操作
	 * @param elementName 元素定位值
	 * @return 如果元素存在且可操作返回true，元素存在不可操作返回false，不存在抛出异常
	 * @author jasonwang
	 */
	public  boolean isEnabled(String elementName) throws Exception{
		WebElement element = findElementAssert(elementName);
		if(element.isEnabled()){
			Log.logInfo("元素【"+elementName+"】可操作");
			return true;
		}else{
			Log.logInfo("元素【"+elementName+"】不可操作");
		}
		return false;
	}
	
	/**
	 * 检查元素是否可操作，设置智能等待元素超时时间
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间(秒)
	 * @return 如果元素存在且可操作返回true，元素存在不可操作返回false，不存在抛出异常
	 * @author jasonwang
	 */
	public  boolean isEnabled(String elementName ,long waitElementTimeout)throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		if(element.isEnabled()){
			Log.logInfo("元素【"+elementName+"】可操作");
			return true;
		}else{
			Log.logInfo("元素【"+elementName+"】不可操作");
		}
		return false;
	}
	
	/**
	 * 等待元素可操作
	 * @param elementName 元素定位值
	 * @param timeout 等待超时时间(秒)
	 * @return 如果在指定时间元素可操作返回true,否则返回false
	 * @author jasonwang
	 */
	public  boolean waitElementEnabled(String elementName ,long timeout){
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).isEnabled()){
					Log.logInfo("元素["+elementName+"]可操作");
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		//srceenShots(elementName);
		Log.logInfo("等待元素["+elementName+"]可操作超时");
		return false;
	}
	
	/**
	 * 点击元素，设置等待元素超时时间,如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间,单位秒
	 * @author jasonwang
	 */
	public  void click(String elementName ,long waitElementTimeout) throws Exception{
		findElementAssert(elementName, waitElementTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<waitElementTimeout*1000) {
			try {
				findElementAssert(elementName, waitElementTimeout).click();
				Log.logInfo("成功点击["+elementName+"]");
				return;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
			interval = System.currentTimeMillis()-starttime;
		}
		Log.logInfo("点击["+elementName+"]出现异常");
	}
	/**
	 * 点击元素，设置等待元素超时时间且打印操作说明,如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间,单位秒
	 * @param 操作说明
	 * @author jasonwang
	 */
	public  void click(String elementName ,long waitElementTimeout,String oprationNote) throws Exception{
		findElementAssert(elementName, waitElementTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<waitElementTimeout*1000) {
			try {
				findElementAssert(elementName, waitElementTimeout).click();
				Log.logInfo("成功点击["+elementName+"],"+oprationNote);
				return;
			} catch (Exception e) {
				//e.printStackTrace();
			}
			Thread.sleep(1000);
			interval = System.currentTimeMillis()-starttime;
		}
		Log.logInfo("点击["+elementName+"]失败,"+oprationNote);
	}
	
	/**
	 * 点击元素，设置等待元素超时时间且设置点击后界面最大加载时间,如果元素不存在抛出异常
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param pageLoadTime 页面加载时间
	 */
	public  void click(String elementName ,long waitElementTimeout,long pageLoadTime) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		driver.manage().timeouts().pageLoadTimeout(pageLoadTime, TimeUnit.SECONDS);
		try {
			waitElementEnabled(elementName,waitElementTimeout);
			element.click();
			Log.logInfo("成功点击了["+elementName+"]");
		} catch (Exception e) {
			Log.logInfo("点击["+elementName+"],发生异常");
		}
		driver.manage().timeouts().pageLoadTimeout(defaultPageLoadTimeoutMax, TimeUnit.SECONDS);
	}
	
	/**
	 * 循环点击元素，设置等待元素超时时间和循环点击的超时时间
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param timeout 循环点击超时时间
	 * @author jasonwang
	 */
	public  void loopClick(String elementName ,long waitElementTimeout,Long timeout) throws Exception{
		findElementAssert(elementName, waitElementTimeout);
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				findElementAssert(elementName, waitElementTimeout).click();
				Log.logInfo("成功点击了["+elementName+"]");
				return;
			} catch (Exception e) {
				Thread.sleep(500);
				interval = System.currentTimeMillis()-starttime;
			}
		}
		Log.logInfo("点击["+elementName+"]出现异常，重试超时");
	}
	
	/**
	 * 清空文本框，输入字符串，如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param content 输入文本值
	 */
	public  void sendText(String elementName ,long waitElementTimeout,String content) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		try {
			for (int i = 0; i < 5; i++) {
				element.clear();
				element.sendKeys(content);
				if(getAttributeValue(elementName, waitElementTimeout, "value").equals(content)){
					break;
				}
				Thread.sleep(500);
			}
			Log.logInfo("对["+elementName+"],输入：["+content+"]");
		} catch (Exception e) {
			Log.logInfo("输入文本发生异常");
		}
	}
	/**
	 * 清空文本框，输入字符串打印操作说明，如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param content 输入文本值
	 * @author jasonwang
	 */
	public  void sendText(String elementName ,long waitElementTimeout,String content,String oprationNote) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		element.clear();
		for (int i = 0; i < 10; i++) {
			try {
				findElementAssert(elementName, waitElementTimeout).sendKeys(content);
				Log.logInfo("对["+oprationNote+"],输入：["+content+"]");
				break;
			} catch (Exception e) {
				//e.printStackTrace();
				Thread.sleep(1000);
			}
		}
	}
	
	
	/**
	 * 清空文本框，将文本字符串拆分成一个个字符输入到文本框中，此方法意在解决数量文本框输入相同数字（999,888）时，被js清空的情况
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param content 输入文本值
	 * @param interval 输入一个字符后暂停时间
	 * @author jasonwang
	 */
	public  void loopSendText(String elementName ,long waitElementTimeout,String content,int interval) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		try {
			Log.logInfo("清空["+elementName+"],输入：["+content+"],元素["+elementName+"]");
			element.clear();
			char[] ch=content.toCharArray();
			for(int i=0;i<ch.length;i++){
				element = findElementAssert(elementName, waitElementTimeout);
				element.sendKeys(String.valueOf(ch[i]));
				Pub.sleep(interval);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.logInfo("输入文本发生异常");
		}
	}
	/**
	 * 不清空文本框，输入字符串打印操作说明，如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param content 输入文本值
	 * @author jasonwang
	 */
	public  void sendTextNotClear(String elementName ,long waitElementTimeout,String content) throws Exception{
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		try {
			element.sendKeys(content);
			Log.logInfo("对["+elementName+"],输入：["+content+"]");
		} catch (Exception e) {
			Log.logInfo("输入文本发生异常");
		}
	}
	/**
	 * 获取元素属性值,如果元素不存在抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param attributeName 元素属性的名称
	 * @author jasonwang
	 */
	public  String getAttributeValue(String elementName ,long waitElementTimeout,String attributeName)throws Exception {
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		String text = null;
		try {
			text = element.getAttribute(attributeName);
			Log.logInfo("获取["+elementName+"]的["+attributeName+"]属性值:["+text+"]");
		} catch (Exception e) {
			Log.logInfo("获取"+elementName+"]的["+attributeName+"属性值发生异常");
		}
		return text;
	}
	
	/**
	 * 获取元素文本值，设置智能等待元素超时时间，如果元素不存在抛出异常
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @author jasonwang
	 */
	public  String getText(String elementName ,long waitElementTimeout)throws Exception {
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		String text = null;
		try {
			text = element.getText();
			Log.logInfo("获取["+elementName+"]值:["+text+"]");
		} catch (Exception e) {
			Log.logInfo("获取["+elementName+"]值发生异常");
		}
		return text;
	}
	/**
	 * 获取元素文本值，设置智能等待元素超时时间，如果元素不存在抛出异常
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @param oprationNote 操作说明
	 * @author jasonwang
	 */
	public  String getText(String elementName ,long waitElementTimeout,String oprationNote)throws Exception {
		WebElement element = findElementAssert(elementName, waitElementTimeout);
		String text = null;
		try {
			text = element.getText();
			Log.logInfo("获取["+oprationNote+"]值:["+text+"]");
		} catch (Exception e) {
			Log.logInfo("获取["+oprationNote+"]值发生异常");
		}
		return text;
	}
	
	
	/**
	 * 获取一组元素的文本值，如果元素不存在抛出异常
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @author jasonwang
	 */
	public  List<String> getTexts(String elementName ,long waitElementTimeout)throws Exception {
		List<String> texts =new ArrayList<String>();
		List<WebElement> elements = findElementsAssert(elementName, waitElementTimeout);
		try {
			for(int i=0;i<elements.size();i++){
				String text = elements.get(i).getText();
				texts.add(text);
			}
			Log.logInfo("获取["+elementName+"]值:"+texts+"");
		} catch (Exception e) {
		}
		return texts;
	}
	
	/**
	 * 获取一组元素的属性值，如果元素不存在抛出异常
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @author jasonwang
	 */
	public  List<String> getAttributeValues(String elementName ,long waitElementTimeout,String attributeName)throws Exception {
		List<String> texts =new ArrayList<String>();
		List<WebElement> elements = findElementsAssert(elementName, waitElementTimeout);
		try {
			for(int i=0;i<elements.size();i++){
				String text = elements.get(i).getAttribute(attributeName);
				texts.add(text);
			}
			Log.logInfo("获取["+elementName+"]值:"+texts+"");
		} catch (Exception e) {
		}
		return texts;
	}
	
	/**
	 * 获取相同元素个数
	 * @param elepath 元素定位值
	 * @param waitElementTimeout 等待元素超时时间，单位秒
	 * @return 相同元素个数
	 * @author jasonwang
	 */
	public  int getElementCount(String elementName ,long waitElementTimeout) {
		return findElementsNotAssert(elementName, waitElementTimeout).size();
	}
	
	/**
	 * 等待元素文本内容变为指定的文本内容
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容变为指定的文本内容则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitTextToBe(String elementName,String text,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).getText().equals(text)){
					Log.logInfo("元素["+elementName+"]的值为："+text);
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	/**
	 * 等待元素文本内容发生变化，与初始值不相等
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容发生变化与初始值不相等则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitTextNotToBe(String elementName,String text,long timeout){
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(!findElementAssert(elementName).getText().equals(text)){
					Log.logInfo("元素["+elementName+"]的值为："+findElementAssert(elementName).getText());
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	
	/**
	 * 等待元素文本内容发生变化，不再包含初始值
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容发生变化与初始值不相等则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitTextNotContains(String elementName,String text,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(!findElementAssert(elementName).getText().contains(text)){
					Log.logInfo("元素["+elementName+"]的值为："+text);
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	
	
	
	/**
	 * 等待元素文本内容包含指定的文本内容
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容包含指定的文本内容则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitTextContains(String elementName,String text,long timeout){
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).getText().contains(text)){
					Log.logInfo("元素["+elementName+"]的值为："+findElementAssert(elementName).getText());
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	/**
	 * 等待元素文本内容不为空
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容不为空则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitTextNotEmpty(String elementName,String text,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).getText().trim().length()>0){
					Log.logInfo("元素["+elementName+"]的值为："+element.getText());
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	
	/**
	 * 等待元素value值内容变为指定的内容
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容变为指定的文本内容则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitAttrValueToBe(String elementName,String text,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).getAttribute("value").equals(text)){
					Log.logInfo("元素["+elementName+"]的值为："+text);
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	
	/**
	 * 等待元素value值内容包含指定的内容
	 * @param elementName 元素定位值
	 * @param text 指定的文本内容
	 * @param timeout 超时时间(s)
	 * @return 如果在指定时间内元素文本内容变为指定的文本内容则返回true，否则返回false；
	 * @author jasonwang
	 */
	public  boolean  waitAttrValueContains(String elementName,String text,long timeout){
		WebElement element = null;
		Long starttime = System.currentTimeMillis();
		Long interval = 0L;
		while (interval<timeout*1000) {
			try {
				if(findElementAssert(elementName).getAttribute("value").contains(text)){
					Log.logInfo("元素["+elementName+"]的值为："+element.getAttribute("value"));
					return true;
				}
			} catch (Exception e) {
			}
			sleep(500);
			interval = System.currentTimeMillis()-starttime;
		}
		return false;
	}
	
	
	/**
	 * 浏览器后退，设置页面加载时间
	 * @param pageLoadTimeout 页面加载时间
	 * @author jasonwang
	 */
	public  void browserBack(long pageLoadTimeout ){
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
		try {
			driver.navigate().back();
		} catch (Exception e) {
			Log.logInfo("页面后退刷新超时");
		}
		driver.manage().timeouts().pageLoadTimeout(defaultPageLoadTimeoutMax, TimeUnit.SECONDS);
	}
	
	/**
	 * 等待当前页面的url变为指定url，如果进行断言那么返回值为true则断言成功，否则断言失败
	 * @param url 元素定位值
	 * @param timeout 等待元素超时时间，单位秒
	 * @param isAssert 断言标识
	 * @return 如果在指定时间内当前页面的url变为指定的url则返回true，否则返回false
	 * @author jasonwang
	 */
	public  boolean urlToBe(String url ,long timeout,boolean isAssert){
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		boolean res = false;
		try {
			res = wait.until(ExpectedConditions.urlToBe(url));
		} catch (Exception e) {
		}
		if(isAssert){
			if(!res){
				Log.logError("actualUrl:["+driver.getCurrentUrl()+"],expectUrl:["+url+"],页面跳转错误");
			} 
		}
		Log.logInfo("actualUrl:["+driver.getCurrentUrl()+"],expectUrl:["+url+"]");
		return res;
	}
	/**
	 * 等待当前页面的url包含指定url，如果进行断言那么返回值为true则断言成功，否则断言失败
	 * @param url 元素定位值
	 * @param timeout 等待元素超时时间，单位秒
	 * @param isAssert 断言标识
	 * @return 如果在指定时间内当前页面的url变为指定的url则返回true，否则返回false
	 * @author jasonwang
	 */
	public  boolean urlToContains(String url ,long timeout,boolean isAssert){
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		boolean res = false;
		try {
			res = wait.until(ExpectedConditions.urlContains(url));
		} catch (Exception e) {
		}
		if(isAssert){
			if(!res){
				Log.logError("actualUrl:["+driver.getCurrentUrl()+"],expectUrl:["+url+"],页面跳转错误");
			} 
		}
		Log.logInfo("actualUrl:["+driver.getCurrentUrl()+"],expectUrl:["+url+"]");
		return res;
	}
	/**
	 * 截屏
	 * @param eleName 元素定位值
	 * @author jasonwang
	 */
	public  void srceenShots(String eleName) {
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			//FileUtil.createFile(System.getProperty("user.dir")+"/test-output/elementInvisible/",Tool.format(Tool.CHECK_LOG_FORMAT));
			FileUtils.copyFile(file, new File(System.getProperty("user.dir")+"/test-output/html-report/screenShot/"+projectName+format(REPORT_CSV_FORMAT)+"_"+eleName+".jpg"));
			Log.logInfo("未找到的元素截图地址：["+System.getProperty("user.dir").replace("\\", "/")+"/test-output/html-report/screenShot/"+projectName+format(REPORT_CSV_FORMAT)+"_"+eleName+".jpg]");
		} catch (Exception e) {
			Log.logInfo("截图失败");
		}
	}
	
	
	/**
	 * 获取**域名下cookie中某个key的值
	 * 注意事项： 1. 此方法会重新get一遍页面，如自动化过程中前后逻辑关联比较紧密的请谨慎选择
	 * @param domain  域名，例： www.baidu.com
	 * @param key cookie字段名，例：要获取cookie的userid对应的值，传入 userid
	 * @return 返回 ""时,cookie中key不存在  返回不为空时,key的值正常获取到
	 */
	public String getCookieKeyValue(String domain,String key){
		String value = "";
		driver.get(domain);
		Set<Cookie> cookies = driver.manage().getCookies();
		for(Cookie cookie:cookies){
			if(cookie.getName().contains(key)){
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}
	
	/**
	 * 获取指定域名下cookie
	 * 注意事项： 1. 此方法会重新get一遍页面，如自动化过程中前后逻辑关联比较紧密的请谨慎选择
	 * @param domain 域名，例： www.baidu.com
	 * @return Set<Cookie>  返回null时，没有获取到cookie 
	 */
	public Set<Cookie> getCookie(String domain){
		driver.get(domain);
		Set<Cookie> cookies = driver.manage().getCookies();
		return cookies;
	}
	
	
	/**
	 * 获取url参数
	 * @param url  完整的链接，例： www.baiud.com/?a=b&b=c
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getUrlParams(String url) { 
		try {
			String aString = url.substring(10).split("\\|\\|")[0];
			url = URLDecoder.decode(aString,"utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
	    Map<String, Object> map = new HashMap<String, Object>();  
	    if (StringUtils.isBlank(url)) { 
	        return map;  
	    }  
	    String[] params = url.split("&");  
	    for (int i = 0; i < params.length; i++) {
	    	map.put(params[i].substring(0, params[i].indexOf("=")), params[i].substring(params[i].indexOf("=")+1));
	    }  
	    return map;  
	} 

	/**
	 * 对比两个map是否一致，可精确到map长度一致，需按要求指定 size参数
	 * @param size 是否判断map的长度一致参数，  size = 1 需判断长度，size = 0或其他,无需判断
	 * @param expectMap 预期map
	 * @param actualMap 实际map
	 * @return boolean
	 */
	public boolean jugeMap(int size,Map<String, Object> expectMap ,Map<String, Object> actualMap){
		boolean result = true;
		Set<String> expectkeySet = expectMap.keySet();
		Set<String> actualkeySet = actualMap.keySet();
		
		if(size == 1){
			if(expectkeySet.size() != actualkeySet.size()){
				Log.logInfo("两个map的长度不一致");
				result = false;
			}
		}
		if(result == false){
			return result;
		}else {
			for(String key:expectkeySet){
				if(actualMap.containsKey(key)){
					if(expectMap.get(key).equals(actualMap.get(key))){
						result = true;
					}else {
						Log.logInfo("actualMap和expectMap中 "+key+" 的值不一致");
						result = false;
						break;
					}
				}else {
					Log.logInfo("actualMap 中不存在key： "+key);
					result = false;
					break;
				}
			}
			return result;
		}
		
	}
	
	/**
	 * 1. 清除冗余的数据，数据上报中，获取得数据不止一条，在做测试之前需要先清除冗余的数据，此方法根据关键字模糊匹配
	 * 2. 注意事项： 此方法仅提供关键字模糊匹配的方式，没有提供key value方式精确匹配，请谨慎选择
	 * @param reprotData 抓取到的数据，list
	 * @param clearKey 需要清除的
	 * @return List<String>
	 */
	public List<String> clearRedundant(List<String> reprotData,List<String> clearKey){
		List<String> resultList = new ArrayList<>();
		for(String clearkey:clearKey){
			for(int i=0;i<reprotData.size();i++){
				if(!reprotData.get(i).contains(clearkey)){
					resultList.add(reprotData.get(i));
				}
			}
		}
		return resultList;
	}
	
	
	/**
	 * 判断map中是否存在关键字 key 和 key 对应的value 例：  数据上报上报时有很多数据，可以通过key和key对应的value精准挑出某条数据
	 * @param key  
	 * @param value
	 * @param reprotMap
	 * @return boolean
	 */
	public boolean IFExistKey(String key,String value,Map<String, String> reprotMap){
		boolean result = false;
		if(reprotMap.containsKey(key)){
			if(reprotMap.get(key).equals(value)){
				result = true;
			}else {
				result = false;
			}
		}else {
			result = false;
		}
		return result;
	}

	
	/**
	 * 1. 将json字符串处理成 map，支持 JSONObject和JSONArray 格式
	 * 2. JSONArray 格式中，如果key不唯一，数据会有误，请谨慎选择
	 * @param jsonStr  json格式字符串
	 * @param Map<String, Object> Firmap  Map对象，用于递归存储数据
	 * @return Map<String, Object> Firmap
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> jsStrToMap(String jsonStr,Map<String,Object> Firmap){
		Object jsStrFir = null;
		try {
			jsStrFir = JSONObject.fromObject(jsonStr);
//			jsStrTwo = JSONObject.fromObject(twoString);
		} catch (Exception e) {
			// TODO: handle exception
			jsStrFir = JSONArray.fromObject(jsonStr);
		}
		
//		Map<String,Object> Firmap = new HashMap<>();
		if(jsStrFir instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)jsStrFir;
			Iterator it = jsonObj.keys(); 
			String key = "";
			Object value = null;
			while(it.hasNext()){
				key = (String)it.next();
				value = jsonObj.get(key); 
				if(value instanceof JSONObject){
					jsStrToMap(value.toString(),Firmap);
				}else if (value instanceof JSONArray) {
					jsStrToMap(value.toString(),Firmap);
				}else {
					Firmap.put(key, value);
				}
			}
		}
		
		if(jsStrFir instanceof JSONArray){
			JSONArray jsonArr = (JSONArray)jsStrFir;
			for(int i=0;i<jsonArr.size();i++){
				 JSONObject jsonObj = jsonArr.getJSONObject(i);
				 Iterator it = jsonObj.keys(); 
				 while(it.hasNext()){
					 String jsonkey = (String)it.next();
					 Object value = jsonObj.get(jsonkey);  
					 Firmap.put(jsonkey, value);
				 }
			}
		}
		return Firmap;
	}
	
	/**
	 * 1. 判断jsonArray对象中是否存在某个key,value
	 * 2. 穿参中 key和value必须对应，也就是必须是一个键值对
	 * @param JSonArrayStr  jsonArray 字符串
	 * @param key 
	 * @param value
	 * @return boolean
	 */
	@SuppressWarnings("rawtypes")
	public boolean iFJsonListExistKey(String JSonArrayStr,String key,String value){
		boolean result = false;
		try {
			Object jsStrFir = JSONArray.fromObject(JSonArrayStr);
			JSONArray jsonArr = (JSONArray)jsStrFir;
			for(int i=0;i<jsonArr.size();i++){
				 JSONObject jsonObj = jsonArr.getJSONObject(i);
				 Iterator it = jsonObj.keys(); 
				 while(it.hasNext()){
					 String jsonkey = (String)it.next();
					 Object jsonvalue = jsonObj.get(key);  
					 if(jsonkey.equals(key)&& jsonvalue.equals(value)){
						 result = true;
						 break;
					 }
				 }
				 if(result){
					 break;
				 }
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.logInfo("json 字符串不是 JsonArray格式");
			e.printStackTrace();
		}
		return result;
	}
	

}
