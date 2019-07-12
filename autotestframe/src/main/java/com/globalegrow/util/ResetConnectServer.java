/**
 * 
 */
package com.globalegrow.util;

import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;


/**
 * 模块描述：  通过修改cookie修改连接的服务器
 * @author Lujun
 * @date 2017年5月27日 上午11:13:19
 */
public class ResetConnectServer {
	
	/**
	 * 通过修改修改连接的服务器
	 * @param driver WebDriver
	 * @param cookiekey  要修改cookie中的字段
	 * @param cookieValue  要修改cookie中字段的值
	 */
	public static void resetServer(WebDriver driver,String cookiekey,String cookieValue){
		
		Set<Cookie> cookies = driver.manage().getCookies();
		if(cookieValue.equals("${ORIGINDC}") || cookieValue.equals("0") ){
			Log.logInfo("不需要指定服务器访问");
			}else {
				if(cookies.toString().contains(cookiekey)){
					for(Cookie cookie:cookies){
						if(cookie.getName().equals(cookiekey)){
							Log.logInfo("当前 "+cookie.getName()+" 的值为："+cookie.getValue());
							Log.logInfo("将 "+cookie.getName()+" 的值修改为："+cookieValue);
							Log.logInfo("cookie域名为： "+cookie.getDomain());
							if(cookie.getValue().equals(cookieValue)){
								Log.logInfo("无需修改连接的服务器，当前连接就是要修改的服务器");
							}else {
								driver.manage().deleteCookieNamed(cookiekey);
								driver.manage().addCookie(new Cookie(cookiekey,cookieValue,cookie.getDomain(),"/",null));
								Set<Cookie> resetCookies = driver.manage().getCookies();
								for(Cookie reCookie:resetCookies){
									if(reCookie.getName().equals(cookiekey)){
										if(reCookie.getValue().equals(cookieValue)){
											Log.logInfo("通过修改cookie改变连接服务器成功");
											Assert.assertTrue(true);
										}else {
											Log.logInfo("通过修改cookie改变连接服务器失败");
											Assert.fail();
											}
										break;
										}
									}
								}
							break;
							}
						}
					}else {
						Log.logInfo("cookie中不存在 "+cookiekey+" 字段");
					}
				}
		}
	}
