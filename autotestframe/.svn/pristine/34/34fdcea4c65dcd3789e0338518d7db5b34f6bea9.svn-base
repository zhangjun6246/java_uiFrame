package com.globalegrow.code;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.globalegrow.util.Log;;

public class Page {
	
	static Actions action = null;
	static Robot robot = null;

	
	/**
	 * 模拟右击，在执行点击操作
	 * @param driver
	 * @param locator
	 * @author linchaojiang， 创建时间：2016-09-03，更新时间：2016-09-03
	 */
	public static void RightClick(WebDriver driver,By locator){
		 try {//模拟右键操作需要用到
			 robot = new Robot();
		 } catch (AWTException e) {
			 e.printStackTrace();
		 }
		action = new Actions(driver);
		WebElement button = driver.findElement(locator);//查找对象
		pause(1);
		action.contextClick(button).perform();//对该元素进行右击操作	
		pause(3);
		robot.keyPress(KeyEvent.VK_ESCAPE);
		pause(3);
		button.click();
	}
	
	/**
	 * 模拟单击，首先把焦点移动到对象上，在进行单击
	 * @param driver
	 * @param locator
	 * @author linchaojiang，创建时间：2016-09-06，更新时间：2016-09-06
	 */
	public static void Singleclick(WebDriver driver,By locator){
		WebElement element = driver.findElement(locator);//查找对象
		Actions action = new Actions(driver);
		pause(1);
		action.moveToElement(element).click().perform();
	}
	
	/**
	 * 线程休眠
	 * @param second
	 * @author linchaojiang
	 */
	public static void pause(int second){
		
		try {
			Thread.sleep(second*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.logInfo("线程休眠出错！！！！");
		}
	}
	
	/**
	 * 处理非alert弹框
	 * @param driver
	 * @return boolean
	 * @throws InterruptedException
	 * @author linchaojiang
	 * 创建时间：2016-08-31
	 * 更新时间：2016-08-31
	 */
	public static boolean manyNewWindows(WebDriver driver,String currentWindow){
		//得到所有窗口的句柄
		Set<String> handles = driver.getWindowHandles();
		//不包括当前窗口
		handles.remove(currentWindow);
		
		int timeout = 0;//等待十秒
		//存在窗口
		while (true) {			
			if(timeout==10){//如果十秒还没找到，退出
				Log.logInfo("没有找到弹出窗口！！！");
				break;
			}
			try {
				if(handles.size() > 0){
					driver.switchTo().window(handles.iterator().next());
			        return true;
				}else {
					pause(1);
					timeout++;
					continue;
				}
			} catch (Exception e) {
				// TODO: handle exception
				 Log.logInfo(e.getMessage());
			     return false;
			}						
		}
		Log.logInfo("Did not find window");
		return false;
		}
	
	/**
	 * 关闭其他的窗口
	 * @author linchaojiang
	 * @param currentWindow
	 * @return boolean
	 * 创建时间：2016-09-01
	 * 更新时间：2016-09-01
	 */
	public static boolean closeOtherWindow(WebDriver driver,String currentWindow){
		
		Set<String> handles = driver.getWindowHandles();
		handles.remove(currentWindow);		
		/*把Set集合转换成Iterator*/
        Iterator<String> it = handles.iterator();
        String otherWindowTitle = null;
        
		try {
			while (it.hasNext()) {				
				 driver.switchTo().window(it.next());
				 otherWindowTitle=driver.getTitle();
				 driver.close();
				 Log.logInfo("成功关闭窗口:"+otherWindowTitle);
			}
		} catch (Exception e) {
			 Log.logError("关闭窗口失败:"+otherWindowTitle);
			 return false;			 
		}
		driver.switchTo().window(currentWindow);//切换为默认窗口	
		return true;		
	}
			

	/**
	 * 处理单个非alert弹窗（模态窗口）
	 * @param driver
	 * @return boolean
	 * @author linchaojiang
	 * 创建时间：2016-09-01
	 * 更新时间：2016-09-01
	 * 
	 */
	public static boolean oneNewWindow(WebDriver driver){
	    try{
	        //定位窗口
	        driver.switchTo().window(driver.getWindowHandles().iterator().next());
	        Log.logInfo("窗口定位成功！！！");
	        return true;
	    }catch(Exception e){
	        Log.logError(e.getMessage());
	        return false;
	    }
	}
	
	/**
	 * 根据页面的title切换窗口
	 * @param driver
	 * @param windowTitle
	 * @return boolean
	 * @author linchaojiang
	 * 创建时间：2016-09-03
	 */
	public static boolean switchToWindow(WebDriver driver,String windowTitle){  
	    boolean flag = false;  
	    try {  
	        String currentHandle = driver.getWindowHandle();  
	        Set<String> handles = driver.getWindowHandles();  
	        for (String s : handles) {  
	            if (s.equals(currentHandle))  
	                continue;  
	            else {  
	                driver.switchTo().window(s);  
	                if (driver.getTitle().contains(windowTitle)) {  
	                    flag = true;  
	                    Log.logInfo("Switch to window: "  
	                            + windowTitle + " successfully!");  
	                    break;  
	                } else  
	                    continue;  
	            }  
	        }  
	    } catch (NoSuchWindowException e) {  
	    	Log.logError("Window: " + windowTitle+ " cound not found!"+e.fillInStackTrace());  
	        flag = false;  
	    }  
	    return flag; 
	} 
	
	/**
	 * 根据标题关闭窗体
	 * @param driver
	 * @param windowTitle
	 * @param currentWindow
	 * @author linchaojiang
	 * 创建时间：2016-09-03
	 */
	public static void closeWithTitle(WebDriver driver,String windowTitle,String currentWindow){ 
	    try {  
	        String currentHandle = driver.getWindowHandle();  
	        Set<String> handles = driver.getWindowHandles();  
	        for (String s : handles) {  
	            if (s.equals(currentHandle))  
	                continue;  
	            else {  
	                driver.switchTo().window(s); 
	                if (driver.getTitle().contains(windowTitle)) {  
	                	driver.close(); 
	                    Log.logInfo("close window: "  
	                            + windowTitle + " successfully!");  
	                    break; 
	                } else  
	                    continue; 	                
	            }  
	        }
	        driver.switchTo().window(currentWindow);
	    } catch (NoSuchWindowException e) {  
	        Log.logError("close Window: " +e.fillInStackTrace());  
	    }  
	}
	
	/**
	 * 判断cookie是否存在及值是否为空
	 * @param webDriver
	 * @param name
	 * @return boolean
	 * @author linchaojiang,创建时间：2016-09-06,更新时间：2016-09-06
	 */
	public static boolean cookieIsNotExist(WebDriver webDriver,String name){
		boolean flag = false;
		Set<Cookie> cookies = webDriver.manage().getCookies();		
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(name)&&(!cookie.getValue().equals(null))){
				Log.logInfo("cookie找到"+name+"且值不为空");
				flag = true;
			}
			
		}
		if(!flag){
			Log.logInfo("cookie中不包含"+name);
		}
		return flag;
	}
	
	/**
	 * 判断cookie是否存在及值是否正确
	 * @param webDriver
	 * @param name
	 * @return boolean
	 * @author linchaojiang,创建时间：2016-09-06,更新时间：2016-09-06
	 */
	public static boolean cookieIsNotExist(WebDriver webDriver,String name,String value){
		boolean flag = false;
		Set<Cookie> cookies = webDriver.manage().getCookies();		
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(name)&&(!cookie.getValue().equals(value))){
				Log.logInfo("cookie找到"+name+"且值等于"+value);
				flag = true;
			}			
		}
		if(!flag){
			Log.logInfo("cookie中不包含"+name);
		}
		return flag;
	}
		
}


