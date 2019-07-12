package com.globalegrow.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.WebElement;

import com.globalegrow.base.Init;
import com.globalegrow.base.InitAndriodTest;
import com.globalegrow.code.Locator;
import com.globalegrow.code.Page;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.android.AndroidKeyMetastate;

public class OpAndroid extends OpApp{
	private static AndroidDriver driver;
	private String projectName;
	private Locator locator;
	
	public OpAndroid(AndroidDriver driver,Locator locator, String projectName) {
		super(driver,locator,projectName);
		// TODO Auto-generated constructor stub
		this.driver=driver;
		this.locator=locator;
		this.projectName=projectName;
	}

	/**
	 * 模拟安卓按键动作,只适用于AndroidApp
	 * @param keyCode 安卓按键
	 * @throws Exception 抛出全部异常
	 * @author yuyang,创建时间：2017-03-10,更新时间：2017-03-10
	 */
	public void actionPressKeyCode(int keyCode) throws Exception {
		boolean res = false;
		boolean printFlag = true;

		int loopTimes = 3;
		for (int i = 1; i <= loopTimes; i++) {
			try {
				driver.pressKeyCode(keyCode);
				res = true;
				break;
			} catch (Exception e) {
				if (printFlag) {
					e.printStackTrace();
					printFlag = false;
				}
				Log.logWarn("模拟安卓按键(" + keyCode + ")时发生异常(" + GetMethodName() + ").");
			}
			Thread.sleep(1000);
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,模拟安卓按键(" + keyCode + ")时发生异常.");
			throw new Exception();
		}
	}
	
	public static final Map<String, Integer> keycode = new HashMap<String, Integer>();
	static {
		keycode.put("0", 7);
		keycode.put("1", 8);
		keycode.put("2", 9);
		keycode.put("3", 10);
		keycode.put("4", 11);
		keycode.put("5", 12);
		keycode.put("6", 13);
		keycode.put("7", 14);
		keycode.put("8", 15);
		keycode.put("9", 16);
		keycode.put("a", 29);
		keycode.put("b", 30);
		keycode.put("c", 31);
		keycode.put("d", 32);
		keycode.put("e", 33);
		keycode.put("f", 34);
		keycode.put("g", 35);
		keycode.put("h", 36);
		keycode.put("i", 37);
		keycode.put("j", 38);
		keycode.put("k", 39);
		keycode.put("l", 40);
		keycode.put("m", 41);
		keycode.put("n", 42);
		keycode.put("o", 43);
		keycode.put("p", 44);
		keycode.put("q", 45);
		keycode.put("r", 46);
		keycode.put("s", 47);
		keycode.put("t", 48);
		keycode.put("u", 49);
		keycode.put("v", 50);
		keycode.put("w", 51);
		keycode.put("x", 52);
		keycode.put("y", 53);
		keycode.put("z", 54);
		keycode.put("z", 54);
		keycode.put("z", 54);
	}
	
	/**
	 * 模拟按键,输入字符串
	 * @param value 待输入的字符串
	 * @author lichaojiang 创建时间:2017-03-14 更新时间:2017-03-14
	 */
	public void sendSetKeyValue(String value) {
		char[] inputString = value.toCharArray();
		for (int i = 0; i < inputString.length; i++) {
			if (inputString[i] >= 'A' && inputString[i] <= 'Z') {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_CAPS_LOCK);
			} else if (inputString[i] >= 'a' && inputString[i] <= 'z') {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_NUM_LOCK);
			}
			if (String.valueOf(inputString[i]).equals("@")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_AT);
			} else if (String.valueOf(inputString[i]).equals(".")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_PERIOD);
			} else if (String.valueOf(inputString[i]).equals("_")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_MINUS, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("!")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_1, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("#")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_3, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("$")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_4, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("%")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_5, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("^")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_6, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("&")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_7, AndroidKeyMetastate.META_SHIFT_ON);
			}else if (String.valueOf(inputString[i]).equals("*")) {
				driver.pressKeyCode(AndroidKeyCode.KEYCODE_8, AndroidKeyMetastate.META_SHIFT_ON);
			}else {
//				Log.logInfo("inputString[" + i + "]:" + inputString[i] + "-" + keycode.get(String.valueOf(inputString[i])));
				driver.pressKeyCode(keycode.get(String.valueOf(inputString[i])));
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void hideKeyboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkConnectionReset(Exception e) {
		// TODO Auto-generated method stub
		if(e.getMessage().contains("Connection reset"))
		{
			Log.logInfo("出现 Connection reset，脚本停止运行");
			if(driver != null){
				driver.quit();
			}
			//执行完毕后删除APP 已保证不会出现在不同的环境覆盖安装
			Cmd.runCommand("cmd /c adb uninstall "+InitAndriodTest.pappPackage);
			Page.pause(3);
			Properties props=System.getProperties();
			String osName = props.getProperty("os.name");
			if(osName.contains("Windows")){
				Cmd.StopServer(Init.freePort, Init.freeDeviceName);
			}else if (osName.contains("Linux")) {
				Cmd.disconnectDevices(osName,Init.freeDeviceName);//断开设备链接
			}
			System.exit(0);
		}
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
					text = element.getText();
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
					
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
				Log.logWarn("操作页面元素(" + inputStr + ")时发生异常(" + GetMethodName() + ").");
				captureScreen(projectName, GetMethodName() + "(" + inputStr + ")_");
			}
	}
}
