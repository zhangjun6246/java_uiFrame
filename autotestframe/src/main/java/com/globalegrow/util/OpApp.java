package com.globalegrow.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.mail.search.StringTerm;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;

import com.globalegrow.code.Locator;

import io.appium.java_client.AppiumDriver;

public abstract  class OpApp {
	
	public static WebDriver driver;
	public List<WebElement> webElements;
	public Locator locator;
	public Actions action;
	
	public OpApp(WebDriver driver,Locator locator, String projectName)
	{
		this.driver=driver;
		this.locator=locator;
		this.projectName=projectName;
	}
	

	public long explicitWaitTimeoutLoop = 20;
	private String projectName;
	private String screenShotPath;
	
	/**
	 * 模拟安卓按键动作
	 * @param keyCode 按键码
	 * @throws Exception
	 */
	public abstract void actionPressKeyCode(int keyCode) throws Exception;
	//隐藏键盘
	public abstract void hideKeyboard();
	/**
	 * 模拟按键,输入字符串
	 * @param value 输入的字符串
	 */
	public abstract void sendSetKeyValue(String value);
	//出现java.net.SocketException: Connection reset结束脚本运行
	public abstract void checkConnectionReset(Exception e);
	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public abstract String GetElementText(String elementStr, long Timeout, long explicitWaitTimeout);
	/**
	 * 给页面输入框元素清空后再输入字符串，输入完后有截图，如果元素不存在则不输入
	 * 备注：由loopSendKeysClean改装,适用于AndroidApp
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @author huxuebing,创建时间：2017-04-21,更新时间：
	 */
	public abstract void SendKeysClean(String inputStr, String keys, long explicitWaitTimeout,boolean isHideKeyboard);
	
	
	
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
	 * 查找元素,不抛出异常
	 * @param elementName 元素定位值
	 * @author huxuebing 修改时间2017-07-25
	 */
	public  WebElement waitForElement(String elementName){
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
		    	}
			    if(element!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementName + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn(",未等待到页面元素(" + elementName + ")存在.");
		}
	
		return element;
		
		
	//	return driver.findElement(locator.getBy(elementName));
	}
	/**
	 * 查找元素,不抛出异常
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待时间 
	 * @author huxuebing 修改时间2017-07-25
	 */
	public  WebElement waitForElement(String elementName ,long waitElementTimeout){
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
		}
	
		return element;
	}
	
	
//	/**
//	 * 在一定时间内，等待页面元素存在
//	 * @param elementStr 页面元素
//	 * @param explicitWaitTimeout  超时时间，单位为秒
//	 * @param assertFlag 断言标志，true-则执行断言，false-则不执行断言
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-10-18,更新时间：2017-03-29
//	 */
//	public WebElement waitForElement(String elementStr, long explicitWaitTimeout, boolean assertFlag){
//		boolean res = false;
//		WebElement element = null;
//		try {
//				element = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(appLocator.getBy(elementStr)));
//			res = true;
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			checkConnectionReset(e);
//		}
//		if (res) {
//			Log.logInfo("等待到页面元素(" + elementStr + ")存在.");
//		} else {
//			Log.logWarn(explicitWaitTimeout + "seconds超时 ,未等待到页面元素(" + elementStr + ")存在.");
//			captureScreen(projectName, "MyWebDriverWait(" + elementStr + ")_" + explicitWaitTimeout + "seconds");
//			if (assertFlag) {
//				Assert.fail();
//			}
//		}
//
//		return element;
//	}
//
//	/**
//	 * 在一定时间内，等待页面元素存在，如果等待到，则返回页面元素，否则返回null，不抛出异常
//	 * 备注：与MyWebDriverWait相似，只是不打印多余的log，用于取代WebDriverWait
//	 * @param elementStr 页面元素
//	 * @param explicitWaitTimeout  超时时间，单位为秒
//	 * @param ePrintFlag 查找不到元素时是否打印异常信息的标志，true-打印，false-不打印
//	 * @param printFlag 是否打印查找或未查找到页面元素的信息标志，true-打印，false-不打印
//	 * @author yuyang ,创建时间：2016-11-15,更新时间：2017-03-29
//	 */
//	public WebElement waitForElement(String elementStr, long explicitWaitTimeout, boolean ePrintFlag, boolean printFlag) {
//		boolean res = false;
//		WebElement element = null;
//
//		try {
//				element = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(appLocator.getBy(elementStr)));
//
//			res = true;
//		} catch (Exception e) {
//			checkConnectionReset(e);
//			if (ePrintFlag) {
//				e.printStackTrace();
//			}
//		}
//
//		if (printFlag) {
//			if (res) {
//				Log.logInfo("等待到页面元素(" + elementStr + ")存在.");
//			} else {
//				Log.logWarn(explicitWaitTimeout + "seconds超时 ,未等待到页面元素(" + elementStr + ")存在.");
//				captureScreen(projectName, "MyWebDriverWait(" + elementStr + ")_" + explicitWaitTimeout + "seconds");
//			}
//		}
//
//		return element;
//	}
	
	/**
	 * 截取屏幕图片,只适用于wap/and
	 * @author linchaojiang
	 * @param directory 路径的最后一级文件夹名
	 * @param captureName 除去时间字符串和后缀的截图名
	 * 创建时间：2016-08-23 更新时间:2016-12-07
	 * 更新说明(2016-09-27)：by yuyang,增加重试来解决概率截图失败的问题
	 * 更新说明(2016-11-16):by yuyang,移动到SatartPhoneBrowse里，发现在使用模拟器的时候，无法截图，由于要使用模拟器调试，先把截图步骤屏蔽
	 * 更新说明(2016-11-16):by yuyang,移动到Op里,由静态方法改为成员方法,只适用于wap
	 * 更新说明(2016-12-07):by yuyang,增加截图失败后刷新页面的动作
	 */
//	public String captureScreen(String directory, String captureName) {
//
//		Date currentTime = new Date();
//		SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
//		String picDateSign = formats.format(currentTime);
//		String screenShotFileName = screenShotPath + directory + "/" + picDateSign + "_" + captureName + ".jpg";
//		File screenShotFile = null;
//		int count = 0;
//
//		while (true) {
//			Log.logInfo("screen shot:" + screenShotFileName);
//			try {
//				Thread.sleep(500);
//				screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//				FileUtils.copyFile(screenShotFile, new File(screenShotFileName));
//				break;
//			} catch (Exception e) {
////				 e.printStackTrace();
//				checkConnectionReset(e);
//				count++;
//				Log.logWarn(count + " times,截图失败,接下来会刷新页面.");
//				driver.navigate().refresh();
//				if (count >= 3) {
//					Log.logInfo("Screenshot failed！");
//					break;
//				}
//			}
//		}
//		return screenShotFileName;
//	}
	
	/**
	 * 截图
	 * @param directory
	 * @param captureName
	 * @return
	 */
	public String captureScreen(String directory, String captureName) {
		Date currentTime = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
		String picDateSign = formats.format(currentTime);
		String screenShotFileName = screenShotPath + directory + "/" + picDateSign + "_" + captureName + ".jpg";
		File screenShotFile = null;
		try {
			screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenShotFile, new File(screenShotFileName));
			Log.logInfo("screen shot:" + screenShotFileName);
		} catch (Exception e) {
			Log.logInfo("截图失败");
			e.printStackTrace();
			checkConnectionReset(e);
		}
		return screenShotFileName;
	}
	
	/**
	 * 给页面输入框元素清空后再输入字符串，元素不存在则不输入
	 * 备注：由loopSendKeysClean改装,适用于AndroidApp
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @author huxuebing,创建时间：2017-04-21,更新时间：
	 */
	public void SendKeysClean(String inputStr, String keys, long explicitWaitTimeout){
		
		WebElement sendElement = null;
			try {
				sendElement = waitForElement(inputStr, explicitWaitTimeout);
				if (sendElement == null) {
					Log.logWarn("未查找页面元素(" + inputStr + "),不尝试输入");
				} else {
					sendElement.click();
					sendElement.clear();
					sendElement.sendKeys(keys);
					Log.logWarn("成功给页面元素(" + inputStr + ")清空后再输入字符串.");
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
				checkConnectionReset(e);
				Log.logWarn("操作页面元素(" + inputStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, GetMethodName() + "(" + inputStr + ")_");
			}
	}
	
	public void sendText(String elementName, String content, long explicitWaitTimeout){
		WebElement element = findElementAssert(elementName, explicitWaitTimeout);
		try {
			Log.logInfo("清空["+elementName+"],输入：["+content+"],元素["+elementName+"]");
			element.clear();
			element.sendKeys(content);
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
			Log.logInfo("输入文本发生异常");
		}
	}
	
	
	
	/**
	 * 给页面输入框元素清空后再输入字符串，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
	 * 备注：由loopSendKeysClean改装,适用于AndroidApp
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param checkFlag 是否检查输入内容,true-检查,false-不检查,实际用时发现,登录密码那里不能检查,因为获取不到密码的值
	 * @param checkEmptyFlag 检查输入框是否为空,true-检查,false-不检查,如果设置为true,会在输入字符前检查是否为空,如果为空才输入
	 * @author yuyang,创建时间：2017-03-09,更新时间：2017-03-10
	 *  //暂时未用backFlag 输入完字符串后是否回退,true-回退,false-不回退,为什么需要回退?因为实际用时发现,输入完密码之后,会弹出键盘,需要通过执行回退动作,将键盘隐藏
	 */
	public void SendKeysClean(String inputStr, String keys, int loopTimes, long explicitWaitTimeout, boolean checkFlag,
			boolean checkEmptyFlag){
		boolean res = false;
		int sendTimes;
		WebElement sendElement = null;
		String inputText = "";
		int lenDel = 10;

		for (sendTimes = 1; sendTimes <= loopTimes; sendTimes++) {
			try {
				if (sendTimes > 1) {
					Log.logWarn(sendTimes + " times,尝试给页面元素(" + inputStr + ")清空后再输入字符串(" + keys + ").");
				}

				sendElement = waitForElement(inputStr, explicitWaitTimeout);
				if (sendElement == null) {
					Log.logWarn(sendTimes + " times," + explicitWaitTimeout + "秒超时,准备给页面元素(" + inputStr + ")清空后再输入字符串,但未等待到该元素.");
				} else {
					if (sendTimes > 1) {
						Log.logWarn(sendTimes + " times,等待到页面元素(" + inputStr + "),准备先清空再输入字符串.");
					}

					if (sendTimes == 1 && checkEmptyFlag) {
						inputText = sendElement.getAttribute("text");
						lenDel = inputText.length();
						if (lenDel > 0) {
							Log.logWarn("该页面元素(" + inputStr + ")不为空，不执行输入动作，直接返回.");
							return;
						}
					}

					if (checkFlag) {
						inputText = sendElement.getAttribute("text");
						lenDel = inputText.length();
					}
					sendElement.click();
					sendElement.clear();
					sendElement.sendKeys(keys);
					
					Log.logWarn("成功给页面元素(" + inputStr + ")清空后再输入字符串.");

					if (checkFlag) {
						sendElement = waitForElement(inputStr, explicitWaitTimeout);
						if (sendElement == null) {
							Log.logWarn(sendTimes + " times," + explicitWaitTimeout + "秒超时,给页面元素(" + inputStr + ")清空并再输入字符串后,准备检查该元素的值,但未等待到该元素.");
						} else {
							inputText = sendElement.getAttribute("text");
							if (inputText.equals(keys)) {
								if (sendTimes > 1) {
									Log.logWarn(sendTimes + " times,给页面元素(" + inputStr + ")清空后再输入字符串,且输入字符串内容正确.");
								}
								res = true;
								break;
							} else {
								Log.logWarn(sendTimes + " times,给页面元素(" + inputStr + ")清空后再输入字符串(" + keys + "),输入的字符内容(" + inputText + ")错误.");
							}
						}
					} else {
						res = true;
						break;
					}
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + inputStr + ")时发生异常(" + GetMethodName() + ").");
				checkConnectionReset(e);
				captureScreen(projectName, GetMethodName() + "(" + inputStr + ")_" + sendTimes + "times");
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,失败给页面元素(" + inputStr + ")清空后再输入字符串.(" + keys + ")");
			captureScreen(projectName, GetMethodName() + "(" + inputStr + ")_" + sendTimes + "times");
		}
	}
	
	
	/**
	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常。
	 * 备注:第二次尝试单击时， 如果找不到单击的页面元素，仍然查找新的页面元素
	 * @param clickStr 用来作单击动作的元素
	 * @param FindStr 用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @author yuyang ,创建时间：2016-11-29,更新时间：2016-12-08
	 * 更新说明(2016-12-06)：by yuyang,第二次尝试单击时，如果找不到单击的页面元素，仍然查找新的页面元素
	 */
	public WebElement ClickForFind(String clickStr, String FindStr, int loopTimes, long explicitWaitTimeout){
		boolean res = false;
		int clickTimes;
		WebElement clickElement;
		WebElement findElement=null;

		for (clickTimes = 1; clickTimes <= loopTimes; clickTimes++) {
			try {
				if (clickTimes > 1) {
					Log.logWarn(clickTimes + " times,尝试单击页面元素(" + clickStr + ").");
				}

				clickElement = waitForElement(clickStr, explicitWaitTimeout);
				if (clickElement == null) {
					Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,准备单击页面元素(" + clickStr + "),但未等待到该元素.");
					if (clickTimes > 1) { // 重试时，如果单击页面元素的动作异常，仍然执行查找新的页面元素
						findElement = waitForElement(FindStr, explicitWaitTimeout);
						if (findElement == null) {
							Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,未等待到准备单击的页面元素(" + clickStr + ")后,也未等待到新的页面元素(" + FindStr
									+ ").");
						} else {
							Log.logWarn(clickTimes + " times,未等待到准备单击的页面元素(" + clickStr + ")后,但等待到新的页面元素(" + FindStr + ").");
							res = true;
							break;
						}
					}
				} else {
					if (clickTimes > 1) {
						Log.logWarn(clickTimes + " times,等待到页面元素(" + clickStr + "),准备单击.");
					}
					clickElement.click();
					if (clickTimes > 1) {
						Log.logWarn(clickTimes + " times,成功单击页面元素(" + clickStr + ").");
					}
					findElement = waitForElement(FindStr, explicitWaitTimeout);
					if (findElement == null) {
						Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,查找到并单击页面元素(" + clickStr + ")后,未等待到新的页面元素(" + FindStr + ").");
					} else {
						Log.logWarn("单击页面元素(" + clickStr + ")后,等待到新的页面元素(" + FindStr + ").");
						res = true;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + clickStr + "," + FindStr + ")时发生异常(" + GetMethodName() + ").");
				checkConnectionReset(e);
				captureScreen(projectName, "loopClick(" + clickStr + ")_" + clickTimes + "times");
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,单击页面元素(" + clickStr + ")后再查找新的页面元素(" + FindStr + "),未按预期执行.");
			captureScreen(projectName, "loopClick(" + clickStr + ")_" + clickTimes + "times");
		}
		return findElement;
	}
	
	/**
	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常。
	 * 备注:1.第二次尝试单击时， 如果找不到单击的页面元素，仍然查找新的页面元素
	 * 2.由loopClick而来,与loopClick不同的地方是,单击的元素,不传字符串,直接传入WebElement
	 * 3.由loopClick改装,适用于App
	 * @param clickElement 用来作单击动作的元素,类型为WebElement
	 * @param FindStr 用来查找的新页面元素
	 * @param loopTimes 尝试单击的次数
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @author yuyang 创建时间:2017-03-13 更新时间:2017-03-13
	 */
	public WebElement ClickForFind(WebElement clickElement, String FindStr, int loopTimes, long explicitWaitTimeout){
		boolean res = false;
		int clickTimes;
		WebElement findElement=null;

		for (clickTimes = 1; clickTimes <= loopTimes; clickTimes++) {
			try {
				if (clickTimes > 1) {
					Log.logWarn(clickTimes + " times,尝试单击页面元素(" + clickElement + ").");
				}

				if (clickElement == null) {
					Log.logWarn(clickTimes + " times,准备单击页面元素(" + clickElement + "),但该元素为null.");
				} else {
					if (clickTimes > 1) {
						Log.logWarn(clickTimes + " times,准备单击页面元素(" + clickElement + ").");
					}
					clickElement.click();
					if (clickTimes > 1) {
						Log.logWarn(clickTimes + " times,成功单击页面元素(" + clickElement + ").");
					}
					findElement = waitForElement(FindStr, explicitWaitTimeout);
					if (findElement == null) {
						Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,单击页面元素(" + clickElement + ")后,未等待到新的页面元素(" + FindStr + ").");
					} else {
						Log.logInfo("单击页面元素 ("+clickElement+") 后，等待到新的页面元素(" + FindStr + ").");
						res = true;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + clickElement + "," + FindStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, GetMethodName() + "(WebElement)_" + clickTimes + "times");
				checkConnectionReset(e);
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,单击页面元素(" + clickElement + ")后再查找新的页面元素(" + FindStr + "),未按预期执行.");
			captureScreen(projectName, GetMethodName() + "(WebElement)_" + clickTimes + "times");
		}
		return findElement;
	}
	

	
	/***
	* 上滑1/4屏幕
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	@SuppressWarnings("rawtypes")
	public void slideUP() {
		Log.logInfo("上滑1/4屏幕");
		int x = ((AppiumDriver)driver).manage().window().getSize().width;
		int y = ((AppiumDriver)driver).manage().window().getSize().height;
		((AppiumDriver)driver).swipe(x / 2, y / 3 * 2, x / 2, y / 3 * 1, 0);
		Pub.sleep(1);
	}

	/***
	* 下滑1/4屏幕
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideDown() {
		Log.logInfo("下滑1/4屏幕");
		int x = ((AppiumDriver)driver).manage().window().getSize().width;
		int y = ((AppiumDriver)driver).manage().window().getSize().height;
		((AppiumDriver)driver).swipe(x / 2, y / 3 * 1, x / 2, y / 3 * 2, 0);
		Pub.sleep(1);
	}

	/***
	* 左滑1/2屏幕
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideLeft() {
		Log.logInfo("左滑1/2屏幕");
		int x = ((AppiumDriver)driver).manage().window().getSize().width;
		int y = ((AppiumDriver)driver).manage().window().getSize().height;
		((AppiumDriver)driver).swipe(x / 4 * 3, y / 2, x / 4 * 1, y / 2, 0);
		Pub.sleep(1);
	}

	/***
	* 右滑1/2屏幕
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideRight() {
		Log.logInfo("右滑1/2屏幕");
		int x = ((AppiumDriver)driver).manage().window().getSize().width;
		int y = ((AppiumDriver)driver).manage().window().getSize().height;
		((AppiumDriver)driver).swipe(x / 4 * 1, y / 2, x / 4 * 3, y / 2, 0);
		Pub.sleep(1);
	}

	/***
	* 特殊上滑
	* @param 传入从左到右宽度的百分比(1-99之间)
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideUP(int i) {
		if (i <= 0 || i >= 100) {
			Log.logWarn("上滑宽度传入错误");
		} else {
			int x = ((AppiumDriver)driver).manage().window().getSize().width;
			int y = ((AppiumDriver)driver).manage().window().getSize().height;
			((AppiumDriver)driver).swipe(x / 100 * i, y / 3 * 2, x / 100 * i, y / 3 * 1, 0);
			Pub.sleep(1);
		}
	}

	/***
	* 特殊下滑
	* @param 传入从左到右宽度的百分比(1-99之间)
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideDown(int i) {
		if (i <= 0 || i >= 100) {
			Log.logWarn("下滑宽度传入错误");
		} else {
			int x = ((AppiumDriver)driver).manage().window().getSize().width;
			int y = ((AppiumDriver)driver).manage().window().getSize().height;
			((AppiumDriver)driver).swipe(x / 100 * i, y / 3 * 1, x / 100 * i, y / 3 * 2, 0);
			Pub.sleep(1);
		}
	}

	/***
	* 特殊左滑
	* @param 传入从上到下宽度的百分比(1-99之间)
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideLeft(int i) {
		if (i <= 0 || i >= 100) {
			Log.logWarn("左滑宽度传入错误");
		} else {
			int x = ((AppiumDriver)driver).manage().window().getSize().width;
			int y = ((AppiumDriver)driver).manage().window().getSize().height;
			((AppiumDriver)driver).swipe(x / 4 * 3, y / 100 * i, x / 4 * 2, y / 100 * i, 0);
			Pub.sleep(1);
		}
	}

	/***
	* 特殊右滑
	* @param 传入从上到下宽度的百分比(1-99之间)
	* @author linchaojiang,创建时间：2017-01-12,更新时间：2017-01-12
	*/
	public void slideRight(int i) {
		if (i <= 0 || i >= 100) {
			Log.logWarn("左滑宽度传入错误");
		} else {
			int x = ((AppiumDriver)driver).manage().window().getSize().width;
			int y = ((AppiumDriver)driver).manage().window().getSize().height;
			((AppiumDriver)driver).swipe(x / 4 * 2, y / 100 * i, x / 4 * 3, y / 100 * i, 0);
			Pub.sleep(1);
		}
	}
	
	/**
	 * 获取屏幕宽度
	 * @throws Exception 抛出全部异常
	 * @author yuyang,创建时间：2017-03-10,更新时间：2017-03-10
	 */
	public int GetWidth(){
		boolean res = false;
		boolean printFlag = true;
		int width = 0;

		int loopTimes = 3;
		for (int i = 1; i <= loopTimes; i++) {
			try {
				width = ((AppiumDriver)driver).manage().window().getSize().width;
				res = true;
				break;
			} catch (Exception e) {
				if (printFlag) {
					e.printStackTrace();
					printFlag = false;
				}
				Log.logWarn("获取屏幕宽度时发生异常(" + GetMethodName() + ").");
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,获取屏幕宽度时发生异常.");
		}

		return width;
	}
	
	/**
	 * 获取屏幕高度,只适用于App
	 * @throws Exception 抛出全部异常
	 * @author yuyang,创建时间：2017-03-10,更新时间：2017-03-10
	 */
	public int GetHeight(){
		boolean res = false;
		boolean printFlag = true;
		int height = 0;

		int loopTimes = 3;
		for (int i = 1; i <= loopTimes; i++) {
			try {
				height = ((AppiumDriver)driver).manage().window().getSize().height;
				res = true;
				break;
			} catch (Exception e) {
				if (printFlag) {
					e.printStackTrace();
					printFlag = false;
				}
				Log.logWarn("获取屏幕高度时发生异常(" + GetMethodName() + ").");
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,获取屏幕高度时发生异常.");
		}

		return height;
	}
	
	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * 备注:传入页面元素类型是WebElement
	 * @param element 页面元素对象
	 * @param Timeout 超时时间，单位为秒
	 * @author yuyang,创建时间：2017-03-18,更新时间：2017-03-18
	 */
	public String GetElementText(WebElement element, long Timeout){
		boolean res = false;
		String text = "";
		long tmpTimeout = Timeout * 1000;

		int looptimes = 0;
		long currentTime;
		long startTime = System.currentTimeMillis();
		while (true) {
			try {
				looptimes++;
				currentTime = System.currentTimeMillis();
				if (currentTime - startTime < 0) {
					currentTime += 86400;
				}
				if (currentTime - startTime >= tmpTimeout) {
					break;
				}
				Thread.sleep(200);

				text = element.getText();
				Log.logInfo("获取到元素 "+element+" 的文本值为： "+text);
				if (looptimes > 1) {
					Log.logWarn(looptimes + " times,成功获取页面元素(" + element + ")的文本值.");
				}
				res = true;
				break;
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + element + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, "loopGetElementText(" + element + ")_" + looptimes + "times");
				checkConnectionReset(e);
			}
		}

		if (!res) {
			Log.logWarn(Timeout + "seconds超时 ,失败获取页面元素(" + element + ")的文本值.");
			captureScreen(projectName, "loopGetElementText(" + element + ")_" + looptimes + "times");
		}

		return text;
	}	
	
	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * 备注:传入页面元素类型是WebElement
	 * @param element 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @author yuyang,创建时间：2017-03-18,更新时间：2017-03-18
	 */
	public String getText(String inputStr, long timeout){
		WebElement sendElement = null;
		try {
			sendElement = waitForElement(inputStr, timeout*1000);
			String text = sendElement.getText();
			Log.logInfo("获取["+inputStr+"]值:["+text+"],元素["+inputStr+"]");
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			checkConnectionReset(e);
			Log.logWarn("获取页面元素(" + inputStr + ")时发生异常(" + GetMethodName() + ").");
		}
		return null;
	}	
	
	/**
	 * 模拟安卓滑屏动作,只适用于App
	 * @param startx 屏幕起始x坐标（相对屏幕0-1）
	 * @param starty 屏幕起始y坐标（相对屏幕0-1）
	 * @param endx 屏幕结束x坐标（相对屏幕0-1）
	 * @param endy 屏幕结束y坐标（相对屏幕0-1）
	 * @param duration 滑动时间
	 * @author yuyang,创建时间：2017-03-10,更新时间：2017-03-29
	 */
	public void swipe(double startx, double starty, double endx, double endy, int duration) {
		int x = ((AppiumDriver)driver).manage().window().getSize().width;
		int y = ((AppiumDriver)driver).manage().window().getSize().height;
		
		int startxInt = (int)(startx*x);
		int  startyInt= (int)(starty*y);
		int endxInt = (int)(endx*x);
		int endyInt = (int)(endy*y);
		Log.logInfo("滑动屏幕中，滑动数据："+startxInt+"."+startyInt+"---"+endxInt+"."+endyInt);
		((AppiumDriver)driver).swipe(startxInt, startyInt, endxInt, endyInt, duration);
		Pub.sleep(1);
		
	}
	
	/**
	 * 重写 findElementBySwipe滑动屏幕,检查页面元素是否存在,如果查找到,则返回该元素,否则返回null,不抛出异常，自定义最大滑动次数
	 * @param elementStr 页面元素
	 * @param startx 屏幕起始x坐标,分辨率比率处理后的double类型
	 * @param starty 屏幕起始y坐标,分辨率比率处理后的double类型
	 * @param endx 屏幕结束x坐标,分辨率比率处理后的double类型
	 * @param endy 屏幕结束y坐标,分辨率比率处理后的double类型
	 * @param swipeCount 最大滑动次数
	 * @author yuyang 创建时间:2017-03-15 更新时间:2017-03-15
	 */
	public WebElement findElementBySwipe(String elementStr, double startx, double starty, double endx, double endy,int swipeCount) {
		WebElement element = null;
		//定义一个滑动次数
		int i=1;
		
		String pageSourceBefore = driver.getPageSource();
		String pageSourceAfter = "";
		try {
			while (true) {
				swipe(startx, starty, endx, endy, 1000);
				Pub.sleep(1);
				boolean b=elementExists(5, elementStr);
				if (b) {
					element=waitForElement(elementStr);
					Log.logInfo("滑动屏幕后,查找到页面元素(" + elementStr + ").");
					break;
				}
				pageSourceAfter = driver.getPageSource();
				if (pageSourceBefore.equals(pageSourceAfter)) {
					Log.logWarn("滑动屏幕到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementBySwipe(" + elementStr + ")_" + swipeCount + "times");
					break;
				}
				i=i+1;
				if (i>swipeCount) {
					Log.logWarn("滑动15次到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementBySwipe(" + elementStr + ")_" + swipeCount + "times");
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		return element;
	}
	
	/**
	 * 滑动屏幕,检查页面元素是否存在,如果查找到,则返回该元素,否则返回null,不抛出异常,滑动次数最多15次
	 * @param elementStr 页面元素
	 * @param startx 屏幕起始x坐标,分辨率比率处理后的double类型
	 * @param starty 屏幕起始y坐标,分辨率比率处理后的double类型
	 * @param endx 屏幕结束x坐标,分辨率比率处理后的double类型
	 * @param endy 屏幕结束y坐标,分辨率比率处理后的double类型
	 * @author yuyang 创建时间:2017-03-15 更新时间:2017-04-12 修改者 huxuebing 
	 */
	public WebElement findElementBySwipe(String elementStr, double startx, double starty, double endx, double endy) {
		WebElement element = null;
		//定义一个滑动次数
		int i=1;
		
		String pageSourceBefore = driver.getPageSource();
		String pageSourceAfter = "";
		try {
			while (true) {
				swipe(startx, starty, endx, endy, 1000);
				Pub.sleep(1);
				boolean b=elementExists(5, elementStr);
				if (b) {
					element=waitForElement(elementStr);
					Log.logInfo("滑动屏幕后,查找到页面元素(" + elementStr + ").");
					break;
				}
				pageSourceAfter = driver.getPageSource();
				if (pageSourceBefore.equals(pageSourceAfter)) {
					Log.logWarn("滑动屏幕到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementBySwipe(" + elementStr + ")_" + 15 + "times");
					break;
				}
				i=i+1;
				if (i>15) {
					Log.logWarn("滑动15次到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementBySwipe(" + elementStr + ")_" + 15 + "times");
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return element;
	}
	
	/**
	 * 在制定时间内等待，直到文本出现在页面上
	 * 
	 * @param timeoutInSeconds 设置等待时间，单位：秒
	 * @param TargetText       等待出现的文本，可以设置多个。
	 * @return boolean
	 * @author Huxuebing 创建时间：2017-03-29
	 */
	public boolean waitForText(int timeoutInSeconds,String... TargetText)
	{
		Boolean flag=false;
		String pageSource=null;
		//获取当前系统时间
		long currentTime=System.currentTimeMillis();
		try {
			while(true)
			{
				try {
					Thread.sleep(3*1000);
					if (driver !=null)
					{
						//获取页面资源
						pageSource=driver.getPageSource();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (null != pageSource)
				{
					//遍历页面资源查找需要的文本信息
					for (int i = 0; i < TargetText.length; i++) {
						flag=pageSource.contains(TargetText[i]);
					}
				}
				if (System.currentTimeMillis() -currentTime >=timeoutInSeconds * 1000 ||flag)
				{
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return flag;
	}
	
	/**
	 * 设置对象查找超时时间
	 * @param time 超时时间
	 *  @author Huxuebing 创建时间：2017-03-29
	 */
	public void setElementLocateTimeout(long time)
	{
		driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
	}
	
	/**
	 * 在指定的时间内判断制定的对象是否存在。
	 * 
	 * @param seconds 指定时间
	 * @param elementStr 元素对象
	 * @return boolean
	 * @author Huxuebing 创建时间：2017-03-29
	 */
	public boolean elementExists(long seconds,String elementStr)
	{
		boolean exists=false;
		setElementLocateTimeout(1);
		long start=System.currentTimeMillis();
		while(!exists&&((System.currentTimeMillis()-start)<=seconds *1000))
		{
			try {
				exists = GetElementList(elementStr).size() > 0;
			} catch (NoSuchElementException e) {
				exists=false;
				checkConnectionReset(e);
			}
		}
		setElementLocateTimeout(30);
		return exists;
	}
	
	
	/**
	 * 在一定时间内，检查页面元素是否会消失
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @throws Exception 所有异常抛出
	 * @author yuyang,创建时间：2016-11-22,更新时间：2016-11-22
	 */
	public boolean CheckDisappear(String elementStr, long Timeout) throws Exception {
		boolean res = false;
		long tmpTimeout = Timeout * 1000;

		long currentTime;
		long startTime = System.currentTimeMillis();
		try {
			while (true) {
				currentTime = System.currentTimeMillis();
				if (currentTime - startTime < 0) {
					currentTime += 86400;
				}
				if (currentTime - startTime >= tmpTimeout) {
					break;
				}
				Thread.sleep(200);

				if (waitForElement(elementStr)==null) {
					Log.logInfo("页面元素(" + elementStr + ")消失.");
					res = true;
					break;
				}
				Pub.sleep(1);

			}

			if (!res) {
				captureScreen(projectName, "CheckDisappear(" + elementStr + ")_" + Timeout + "seconds");
				Log.logWarn(Timeout + "seconds超时 ,页面元素(" + elementStr + ")仍显示在页面中.");
				Assert.fail();
			}
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		return res;
	}
	/**
	 * 查找元素
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间
	 */
	public  WebElement findElementAssert(String elementName ,long waitElementTimeout){
		WebElement element = null;
		try {
			element = waitForElement(elementName);
		} catch (Exception e) {
			Log.logError("获取元素["+elementName+"]失败");
			checkConnectionReset(e);
			captureScreen(projectName, "findelement(" + elementName + ")_" + waitElementTimeout + "seconds");
		}
		return element;
	}
	
	/**
	 * 获取元素,没找到元素则断言失败
	 * @param elementName 元素定位值
	 * @param waitElementTimeout 等待元素超时时间
	 */
	public  WebElement findElementNotAssert(String elementName ,long waitElementTimeout){
		WebElement element = null;
		try {
			element = waitForElement(elementName, waitElementTimeout);
		} catch (Exception e) {
			//e.printStackTrace();
			Log.logInfo("元素["+elementName+"]不存在");
			checkConnectionReset(e);
			//captureScreen(projectName, "findelement(" + elementName + ")_" + waitElementTimeout + "seconds");
		}
		return element;
	}
	
	/**
	 * 查找元素
	 * @param elementStr 元素对象
	 * @param waitElementTimeout 超时时间
	 * @return boolean 
	 */
	public boolean isExist(String elementStr, long waitElementTimeout) {
		WebElement element = findElementNotAssert(elementStr, waitElementTimeout);
		try {
			if(element != null){
				Log.logInfo("元素【"+elementStr+"】存在");
				return true;
			}
		} catch (Exception e) {
			checkConnectionReset(e);
		}
		return false;
	}
	
	/**
	 * 查找元素是否可见 
	 * @param elementStr 元素对象
	 * @param waitElementTimeout 超时时间
	 * @return boolean
	 */
	public boolean isVisible(String elementStr, long waitElementTimeout) {
		WebElement element = findElementNotAssert(elementStr, waitElementTimeout);
		try {
			if(element == null){
				Log.logInfo("元素【"+elementStr+"】不可见");
				return false;
			}
			if(element.isDisplayed()){
				Log.logInfo("元素【"+elementStr+"】可见");
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.logInfo("判断元素是否可见发生异常");
			checkConnectionReset(e);
		}
		Log.logInfo("元素【"+elementStr+"】不可见");
		return false;
	}
	
	/**
	 * 等待元素不可见
	 * @param elepath 元素定位值
	 * @param timeout 等待超时时间(秒)
	 */
	public  boolean waitElementInvisibility(String elementName ,long timeout){
		WebElement element = findElementNotAssert(elementName, timeout);
		if(element == null){
			Log.logInfo("元素【"+elementName+"】不可见");
			return true;
		}
		try {
			for (int i = 0; i < timeout*2; i++) {
				if(!element.isDisplayed()){
					Log.logInfo("元素【"+elementName+"】不可见");
					return true;
				}
				Thread.sleep(500);
			}
		} catch (Exception e) {
			//Log.logInfo("元素【"+elementName+"】不可见");
		}
		Log.logInfo("元素【"+elementName+"】可见");
		return false;
	}
	
	/**
	 * 点击页面元素，点击后延迟1S
	 * @param elementStr 页面元素
	 * @param explicitWaitTimeout 单击后的延时时间，单位为秒
	 * @author huxuebing 创建时间:2017-03-16 更新时间:2017-07-25
	 */
	public void click(String elementStr, long explicitWaitTimeout){
		WebElement clickElement = null;
		try {
			clickElement = findElementAssert(elementStr, explicitWaitTimeout);
			clickElement.click();
			Log.logInfo("成功单击页面元素(" + elementStr + ").");
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
			checkConnectionReset(e);
		}
	}
	
	/**
	 * 点击页面元素，有截图，有点击后延迟
	 * @param elementStr 页面元素
	 * @param sleepTime 单击后的延时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间,单位为秒
	 * @author yuyang 创建时间:2017-03-16 更新时间:2017-03-16
	 */
	public void ClickElement(String elementStr, int sleepTime, long explicitWaitTimeout){
		boolean res = false;
		WebElement clickElement = null;

			try {
				clickElement = waitForElement(elementStr, explicitWaitTimeout);
				if (clickElement != null) {
					clickElement.click();
					Pub.sleep(sleepTime);
					Log.logInfo("成功单击页面元素(" + elementStr + ").");
					res = true;
				} 

			} catch (Exception e) {
//				e.printStackTrace();
				Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, "ClickElement(" + elementStr + ")" );
				checkConnectionReset(e);
			}

		if (!res) {
			Log.logWarn(explicitWaitTimeout + "seconds超时 ,失败点击页面元素(" + elementStr + ").");
			captureScreen(projectName, "ClickElement(" + elementStr + ")" );
		}
	}
	
	/**
	 * 点击页面元素，无延迟
	 * @param elementStr 页面元素
	 * @param explicitWaitTimeout 显示等待元素的超时时间 单位为秒
	 * @author yuyang 创建时间:2017-03-16 更新时间:2017-03-16
	 */
	public void ClickElement(String elementStr, long explicitWaitTimeout){
		WebElement clickElement = null;

			try {
				clickElement = waitForElement(elementStr, explicitWaitTimeout);
				if (clickElement != null) {
					clickElement.click();
					Log.logInfo("成功单击页面元素(" + elementStr + ").");
				} 

			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, "ClickElement(" + elementStr + ")" );
				checkConnectionReset(e);
			}
	}
	
	/**
	 * 检查页面元素是否可见，如果存在且可见，则返回1；如果存在且不可见，则返回2；如果不存在或其它异常，则重试直到超时，超时返回-1
	 * 备注：该方法不会截图
	 * @param elementStr 待检查的元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param printFlag 打印标志，为true时则打印log
	 * @return 1-页面元素存在且可见，2-页面元素存在且不可见，-1-超时检查，页面元素不存在或者其它异常
	 * @author yuyang,创建时间：2016-12-14,更新时间：2016-12-14
	 */
	public int CheckElementVisible(String elementStr, long Timeout, long explicitWaitTimeout, boolean printFlag) {
		int res = -1;
		long tmpTimeout = Timeout * 1000;
		boolean diplayFlag = false;
		boolean enableFlag = false;
		WebElement element = null;

		int looptimes = 0;
		long currentTime;
		long startTime = System.currentTimeMillis();
		while (true) {
			try {
				looptimes++;
				currentTime = System.currentTimeMillis();
				if (currentTime - startTime < 0) {
					currentTime += 86400;
				}
				if (currentTime - startTime >= tmpTimeout) {
					break;
				}
				Thread.sleep(200);

				element = waitForElement(elementStr, explicitWaitTimeout);
				if (element == null) {
					if (printFlag) {
						Log.logWarn(looptimes + " times," + explicitWaitTimeout + "秒超时,准备检查页面元素(" + elementStr + ")是否可见,但未等待到该元素.");
					}
				} else {
					if (looptimes > 1) {
						Log.logWarn(looptimes + " times,等待到页面元素(" + elementStr + "),准备检查页面元素是否可见.");
					}
					diplayFlag = element.isDisplayed();
					enableFlag = element.isEnabled();

					if (diplayFlag && enableFlag) {
						if (looptimes > 1) {
							Log.logWarn(looptimes + " times,检查到页面元素(" + elementStr + ")可见.");
						} else {
							Log.logInfo("检查到页面元素(" + elementStr + ")可见.");
						}
						res = 1;
						break;
					} else {
						if (looptimes > 1) {
							Log.logWarn(looptimes + " times,检查到页面元素(" + elementStr + ")不可见(" + diplayFlag + "," + enableFlag + ").");
						} else {
							Log.logInfo("检查到页面元素(" + elementStr + ")不可见(" + diplayFlag + "," + enableFlag + ").");
						}
						res = 2;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, "CheckElementVisible(" + elementStr + ")" );
				checkConnectionReset(e);
			}
		}

		if (res == -1 && printFlag) {
			Log.logWarn(Timeout + "seconds超时 ,失败检查页面元素(" + elementStr + ")的可见状态.");
			captureScreen(projectName, "CheckElementVisible(" + elementStr + ")" );
		}
		return res;
	}
	
	/**
	 * 检查页面元素是否可见，如果存在且可见，则返回1；如果存在且不可见，则返回2；如果不存在或其它异常，则重试直到超时，超时返回-1
	 * 备注：该方法不会截图
	 * @param elementStr 待检查的元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @param printFlag 打印标志，为true时则打印log
	 * @return 1-页面元素存在且可见，2-页面元素存在且不可见，-1-超时检查，页面元素不存在或者其它异常
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-12-14,更新时间：2016-12-14
	 */
	public int CheckElementVisible(String elementStr, long explicitWaitTimeout, boolean printFlag) {
		int res = -1;
		boolean diplayFlag = false;
		boolean enableFlag = false;
		try {
			WebElement element = waitForElement(elementStr, explicitWaitTimeout);
			if (element == null) {
				if (printFlag) {
					Log.logWarn(explicitWaitTimeout + "秒未等待到该元素(" + elementStr + ").");
				}
			} else {
				diplayFlag = element.isDisplayed();
				enableFlag = element.isEnabled();
				if (diplayFlag && enableFlag) {
					Log.logInfo("检查到页面元素(" + elementStr + ")可见.");
					res = 1;
				} else {
					Log.logInfo("检查到页面元素(" + elementStr + ")不可见(" + diplayFlag + "," + enableFlag + ").");
					res = 2;
				}
			}
			if (res == -1 && printFlag) {
				Log.logWarn(explicitWaitTimeout + "seconds超时 ,失败检查页面元素(" + elementStr + ")的可见状态.");
				captureScreen(projectName, "CheckElementVisible(" + elementStr + ")" );
			}
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}

		return res;
	}
	
	
	
	/**
	 * 获取相同属性的对象集合，然后通过索引确认元素
	 * @param elementStr 页面元素
	 * @param index	元素索引
	 * @return WebElement 
	 * @author huxuebing 创建时间：2017.3.31
	 */
	public WebElement GetElementByBy(String elementStr,int index)
	{
		WebElement element=null;
		try {
			List<WebElement> elements=GetElementList(elementStr);
			if(elements.size()>0){
				element=elements.get(index);
			}else{
				Log.logWarn("所传入的属性查询到的元素为空.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return element;
	}
	
	/**
	 * 获取相同属性的对象集合，然后通过元素文本内容确认元素
	 * @param elementStr 页面元素
	 * @param elementText	元素文本
	 * @return WebElement 
	 * @author huxuebing 创建时间：2017.4.6
	 */
	public WebElement GetElementByBy(String elementStr,String elementText)
	{
		WebElement element=null;
		try {
			List<WebElement> elements=GetElementList(elementStr);
			if(elements.size()>0){
				for (WebElement webElement : elements) {
					if(webElement.getText().contains(elementText))
					{
						element=webElement;
					}
				}
			}else{
				Log.logWarn("所传入的属性查询到的元素为空.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return element;
	}
	
	/**
	 * 滑动屏幕,检查页面元素是否存在,如果查找到,则返回该元素,否则返回null,不抛出异常
	 * @param elementStr 页面元素
	 * @param index 元素索引
	 * @param startx 屏幕起始x坐标,分辨率比率处理后的double类型
	 * @param starty 屏幕起始y坐标,分辨率比率处理后的double类型
	 * @param endx 屏幕结束x坐标,分辨率比率处理后的double类型
	 * @param endy 屏幕结束y坐标,分辨率比率处理后的double类型
	 * @param ePrintFlag 查找不到元素时是否打印异常信息的标志，true-打印，false-不打印
	 * @author huxuebing 创建时间:2017-04-05 更新时间:2017-04-05
	 */
	public WebElement findElementByElementIndexSwipe(String elementStr,int index, double startx, double starty, double endx, double endy) {
		WebElement element = null;
		//定义一个滑动次数
		int i=1;
		
		String pageSourceBefore = driver.getPageSource();
		String pageSourceAfter = "";
		try {
			while (true) {
				swipe(startx, starty, endx, endy, 1000);
				Pub.sleep(1);
				boolean b=elementExists(5, elementStr);
				if (b) {
					List<WebElement> elements=GetElementList(elementStr);
					if(elements.size()>0){
						element=elements.get(index);
						Log.logInfo("滑动屏幕后,查找到页面元素(" + elementStr + ").");
					}else{
						Log.logWarn("所传入的属性查询到的元素为空.");
					}
					break;
				}
				pageSourceAfter = driver.getPageSource();
				if (pageSourceBefore.equals(pageSourceAfter)) {
					Log.logWarn("滑动屏幕到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementIndexSwipe(" + elementStr + ")" );
					break;
				}
				i=i+1;
				if (i>15) {
					Log.logWarn("滑动15次到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementIndexSwipe(" + elementStr + ")" );
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return element;
	}
	
	/**
	 * 滑动屏幕,检查页面元素是否存在,如果查找到,则返回该元素,否则返回null,不抛出异常
	 * @param elementStr 页面元素
	 * @param elementText 元素文本值
	 * @param startx 屏幕起始x坐标,分辨率比率处理后的double类型
	 * @param starty 屏幕起始y坐标,分辨率比率处理后的double类型
	 * @param endx 屏幕结束x坐标,分辨率比率处理后的double类型
	 * @param endy 屏幕结束y坐标,分辨率比率处理后的double类型
	 * @author huxuebing 创建时间:2017-04-05 更新时间:2017-04-05
	 */
	public WebElement findElementByElementTextSwipe(String elementStr,String elementText, double startx, double starty, double endx, double endy) {
		WebElement element = null;
		//定义一个滑动次数
		int i=1;
		
		String pageSourceBefore = driver.getPageSource();
		String pageSourceAfter = "";
		try {
			while (true) {
				swipe(startx, starty, endx, endy, 1000);
				Pub.sleep(1);
				boolean b=elementExists(5,elementStr);
				if (b) {
					List<WebElement> elements=GetElementList(elementStr);
					if(elements.size()>0){
						for (WebElement webElement : elements) {
							if(elementText.equals(webElement.getText())){
								Log.logInfo("webElement.getText() ="+webElement.getText());
								element=webElement;
								break;
							}
						}
						if(element!=null)
						{
							Log.logInfo("滑动屏幕后,查找到页面元素(" + elementStr + ").");
							break;
						}
					}else{
						Log.logWarn("所传入的属性查询到的元素为空.");
					}
					break;
				}
				pageSourceAfter = driver.getPageSource();
				if (pageSourceBefore.equals(pageSourceAfter)) {
					Log.logWarn("滑动屏幕到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementTextSwipe(" + elementStr + ")" );
					break;
				}
				i=i+1;
				if (i>15) {
					Log.logWarn("滑动15次到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementTextSwipe(" + elementStr + ")" );
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		
		return element;
	}
	
	/**
	 * 重写 findElementByElementIndexSwipe 滑动屏幕,检查页面元素是否存在,如果查找到,则返回该元素,否则返回null,不抛出异常，自定义滑动最大次数
	 * @param elementStr 页面元素
	 * @param index 元素索引
	 * @param startx 屏幕起始x坐标,分辨率比率处理后的double类型
	 * @param starty 屏幕起始y坐标,分辨率比率处理后的double类型
	 * @param endx 屏幕结束x坐标,分辨率比率处理后的double类型
	 * @param endy 屏幕结束y坐标,分辨率比率处理后的double类型
	 * @param swipeCount 滑动最大次数
	 * @author huxuebing 创建时间:2017-04-12 更新时间:2017-04-12
	 */
	public WebElement findElementByElementIndexSwipe(String elementStr,int index, double startx, double starty, double endx, double endy,int swipeCount) {
		WebElement element = null;
		//定义一个滑动次数
		int i=1;
		
		String pageSourceBefore = driver.getPageSource();
		String pageSourceAfter = "";
		try {
			while (true) {
				swipe(startx, starty, endx, endy, 1000);
				Pub.sleep(1);
				boolean b=elementExists(5,elementStr);
				if (b) {
					List<WebElement> elements=GetElementList(elementStr);
					if(elements.size()>0){
						element=elements.get(index);
						Log.logInfo("滑动屏幕后,查找到页面元素(" + elementStr + ").");
					}else{
						Log.logWarn("所传入的属性查询到的元素为空.");
					}
					break;
				}
				pageSourceAfter = driver.getPageSource();
				if (pageSourceBefore.equals(pageSourceAfter)) {
					Log.logWarn("滑动屏幕到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementIndexSwipe(" + elementStr + ")" );
					break;
				}
				i=i+1;
				if (i>swipeCount) {
					Log.logWarn("滑动15次到底后,仍然没有查找到页面元素(" + elementStr + ").");
					captureScreen(projectName, "findElementByElementIndexSwipe(" + elementStr + ")" );
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			checkConnectionReset(e);
		}
		return element;
	}
	
	/**
	 * 模拟输入，在原生方法执行时添加日志
	 * @param element 页面元素
	 * @param charSequences 输入文本
	 * @author huxuebing 创建时间：2017.3.31
	 */
	public void sendKeys(final WebElement element,CharSequence... inputText)
	{
		Log.logInfo("[输入字符 "+element.toString().substring(element.toString().indexOf("->")));
		element.sendKeys(inputText);
	}
	
	/**
	 * 模拟输入，在原生方法执行时添加日志
	 * @param elementStr 页面元素
	 * @param charSequences 输入文本
	 * @author huxuebing 创建时间：2017.3.31
	 */
	public void sendKeys(final String elementStr,CharSequence... inputText)
	{
		WebElement element=waitForElement(elementStr);
		Log.logInfo("[输入字符 "+element.toString().substring(element.toString().indexOf("->")));
		element.sendKeys(inputText);
	}
	
	/**
	 * 获取相同属性的对象集合
	 * @param elementStr 页面元素
	 * @return List<WebElement> 
	 * @author huxuebing 创建时间：2017.4.6
	 */
	public List<WebElement> GetElementList(String elementStr)
	{
		List<WebElement> elements=null;	
		boolean res = false;
		String type="";
		String value="";

			Map<String,String> map=locator.getMap(elementStr);
			for (Map.Entry<String, String> entry : map.entrySet()) {  
				type=entry.getKey();
	    	    value=entry.getValue();
		    	try{
		    		elements = driver.findElements(locator.getBy(map, type));
		    	}catch(Exception e){
		    		 Log.logInfo("通过方式:"+type+",值："+value+"来查找元素:"+elementStr+",但未查找到，请检查元素对象！");
		    	}
			    if(elementStr!=null){
			    	 res = true;
			    	 break;
			    }

			}  
		
		if (res) {
				Log.logInfo("等待到页面元素(" + elementStr + ")存在,查找方式为:"+type+" ,查找值为:"+value+".");
		} else {
			Log.logWarn("未等待到页面元素(" + elementStr + ")存在.");
		}
	
		return elements;
	}
	
	/**
	 * 点击元素
	 * @param WebElement 页面元素
	 * @param delayTime 点击后的延迟时间 ,单位为秒
	 * @author huxuebing 创建时间：2017.4.8
	 */
	public void clickWebElement(WebElement element,int delayTime)
	{
		element.click();
		Pub.sleep(delayTime);
	}
	
}
