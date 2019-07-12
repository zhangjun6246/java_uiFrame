package com.globalegrow.util;

import org.openqa.selenium.WebElement;

import com.globalegrow.code.Locator;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

public class OpIos extends OpApp{
	private static IOSDriver driver;
	private String projectName;
	private Locator locator;

	public OpIos(IOSDriver driver,Locator locator, String projectName) {
		super(driver, locator,projectName);
		// TODO Auto-generated constructor stub
		this.driver=driver;
		this.locator=locator;
		this.projectName=projectName;
	}

	@Override
	public void actionPressKeyCode(int keyCode) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideKeyboard() {
		// TODO Auto-generated method stub
		((AppiumDriver) driver).findElementById("Done").click();
	}

	@Override
	public void sendSetKeyValue(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkConnectionReset(Exception e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 获取页面元素的文本值，如果获取到，则返回；否则循环获取，直到超时
	 * @param elementStr 页面元素
	 * @param Timeout 超时时间，单位为秒
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @throws RetryException 抛出重试异常
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
	 */
	public String GetElementText(String elementStr, long Timeout, long explicitWaitTimeout){
		boolean res = false;
		String text = "";
		long tmpTimeout = Timeout * 1000;
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
					Log.logWarn(looptimes + " times," + explicitWaitTimeout + "秒超时,准备获取页面元素(" + elementStr + ")的文本值,但未等待到该元素.");
				} else {
					if (looptimes > 1) {
						Log.logWarn(looptimes + " times,等待到页面元素(" + elementStr + "),准备获取文本值.");
					}
					text = element.getAttribute("name");
					Log.logInfo("获取到元素 "+elementStr+" 的文本值为： "+text);
					if (looptimes > 1) {
						Log.logWarn(looptimes + " times,成功获取页面元素(" + elementStr + ")的文本值.");
					}
					res = true;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, "loopGetElementText(" + elementStr + ")_" + looptimes + "times");
			}
		}

		if (!res) {
			Log.logWarn(Timeout + "seconds超时 ,失败获取页面元素(" + elementStr + ")的文本值.");
			captureScreen(projectName, "loopGetElementText(" + elementStr + ")_" + looptimes + "times");
		}

		return text;
	}
	
	/**
	 * 给页面输入框元素清空后再输入字符串，输入完后有截图，如果元素不存在则不输入
	 * 备注：由loopSendKeysClean改装,适用于AndroidApp
	 * @param inputStr 输入框元素
	 * @param keys 待输入的字符串
	 * @param explicitWaitTimeout 显示等待元素的超时时间
	 * @author huxuebing,创建时间：2017-04-21,更新时间：
	 */
	public void SendKeysClean(String inputStr, String keys, long explicitWaitTimeout,boolean isHideKeyboard){
		
		WebElement sendElement = null;
			try {
				sendElement = waitForElement(inputStr, explicitWaitTimeout);
				if (sendElement == null) {
					Log.logWarn("未查找页面元素(" + inputStr + "),不尝试输入");
				} else {
			//		sendElement.click();
					sendElement.clear();
					sendElement.sendKeys(keys);
					Log.logWarn("成功给页面元素(" + inputStr + ")清空后再输入字符串.");
					if(isHideKeyboard){
						hideKeyboard();
					}
					
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + inputStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, GetMethodName() + "(" + inputStr + ")_");
			}
	}

}