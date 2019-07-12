//package com.globalegrow.util;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.Inet4Address;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//
//import org.htmlparser.util.ParserException;
//import org.openqa.selenium.Alert;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Cookie;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.NoAlertPresentException;
//import org.openqa.selenium.NoSuchElementException;
//import org.openqa.selenium.TimeoutException;
//import org.openqa.selenium.UnhandledAlertException;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.Assert;
//
//import com.globalegrow.base.StartPhoneBrowser;
//import com.globalegrow.base.Startbrowser;
//import com.globalegrow.code.Locator;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//
//import io.appium.java_client.android.AndroidDriver;
//import jxl.Cell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.read.biff.BiffException;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;
//
//public class PublicFun {
//
//	static int contentLengthAllow = -1; // 返回报文长度限制, 为-1时不限制长度
//	static int defaultConnectTimeOut = 30000; // 默认连接超时,毫秒
//	static int defaultReadTimeOut = 30000; // 默认读取超时,毫秒
//
////	static String proxyHost = "10.37.1.248"; // 代理Host
////	static String proxyPort = "8080"; // 代理端口
//	static String projectPath = System.getProperty("user.dir");
//
//	public static final int[] successCode = { 200, 201, 202 }; // 请求成功返回码
//
//	/**
//	 * 生成指定长度的随机字符串
//	 * @param length 字符串长度
//	 * @return 生成的随机字符串
//	 * @author yuyang 创建时间:2016-08-29更新时间:2016-08-29
//	 */
//	public static String getRandomString(int length) {
//		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//		Random random = new Random();
//		StringBuffer randomstr = new StringBuffer();
//
//		for (int i = 0; i < length; ++i) {
//			int number = random.nextInt(62);// [0,62)
//			randomstr.append(str.charAt(number));
//		}
//		return randomstr.toString();
//	}
//
//	/**
//	 * 检查字符串是否有长度
//	 * @param str 待检查长度的字符串
//	 * @return true或false,表示字符串变量中是否包含非空的字符
//	 * @author yuyang 创建时间:2016-08-31更新时间:2016-08-31
//	 */
//	public static boolean hasLength(String str) {
//		return str != null && !"".equals(str.trim());
//	}
//
//	/**
//	 * 切换浏览器窗口到指定标题的窗口
//	 * @param driver 浏览器驱动
//	 * @param windowTitle 待切换到窗口的标题
//	 * @param sleepMillisecondBefore 切换之前延迟的时间，单位为毫秒
//	 * @return true或false,表示是否切换窗口成功
//	 * @throws InterruptedException 
//	 * @author yuyang 创建时间:2016-09-1更新时间:2016-09-1
//	 */
//	public static boolean switchToWindow(WebDriver driver, String windowTitle, long sleepMillisecondBefore) throws InterruptedException {
//		boolean flag = false;
//		try {
//			if (sleepMillisecondBefore > 0) {
//				Thread.sleep(sleepMillisecondBefore);
//			}
//			String currentHandle = driver.getWindowHandle();
//			Set<String> handles = driver.getWindowHandles();
//			for (String s : handles) {
//				if (s.equals(currentHandle))
//					continue;
//				else {
//					driver.switchTo().window(s);
//					if (driver.getTitle().contains(windowTitle)) {
//						flag = true;
//						// System.out.println("Switch to window: " + windowTitle
//						// + " successfully!");
//						break;
//					} else
//						continue;
//				}
//			}
//			if (!flag) {
//				driver.switchTo().window(currentHandle);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Window: " + windowTitle + " cound not found!(switchToWindow)");
//		}
//		return flag;
//	}
//
//	/**
//	 * 读取excle文件，返回map对象
//	 * @author linchaojiang
//	 * 创建时间:2016-08-30
//	 * 更新时间:2016-08-30
//	 */
//	@SuppressWarnings("rawtypes")
//	public static Map<String, List> readTestCase(String className, String projectName, String xlsFileName) {
//
//		Map<String, List> map = new HashMap<String, List>();
//
//		List<String[]> list = new ArrayList<String[]>();
//
//		Workbook rwb = null;
//		Cell cell = null;
//
//		InputStream stream;
//		try {
//			stream = new FileInputStream(projectPath + "/testCase/" + projectName + "/" + xlsFileName + ".xls");
//			rwb = Workbook.getWorkbook(stream);
//		} catch (FileNotFoundException e) {
//			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
//			e.printStackTrace();
//		} catch (BiffException e) {
//			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
//			e.printStackTrace();
//		} catch (IOException e) {
//			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
//			e.printStackTrace();
//		}
//
//		Sheet sheet = rwb.getSheet(className);
//		int rows = sheet.getRows();
//		int coumn = sheet.getColumns();
//
//		String[] strkey = new String[rows - 1];// 存取testCase的值
//
//		for (int i = 1; i < rows; i++) {
//			String[] strValue = new String[coumn - 1];// 存取每一行的数据
//			strkey[i - 1] = sheet.getCell(0, i).getContents();
//			for (int j = 1; j < coumn; j++) {
//				strValue[j - 1] = sheet.getCell(j, i).getContents();
//			}
//			list.add(strValue);
//		}
//
//		// 把行的数据加入map中
//		for (int i = 0; i < strkey.length; i++) {
//			map.put(strkey[i], Arrays.asList(list.get(i)));
//		}
//
//		return map;
//	}
//
//	/**
//	 * 向指定URL发送GET方法的请求
//	 * @param url 发送请求的URL
//	 * @return Result 所代表远程资源的响应,头信息
//	 * @author linchaojiang
//	 * 创建时间：2016-09-01
//	 * 更新时间：2016-09-01
//	 * 
//	 */
//	public static Map<String, String> get(String url) {
//
//		Map<String, String> result = new HashMap<String, String>();
//		BufferedReader in = null;
//
//		try {
//			// 打开和URL之间的连接
//			URLConnection connection = new URL(url).openConnection();
//			// 此处的URLConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection
//			// 故此处最好将其转化为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API.
//			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
//
//			// 设置通用的请求属性
//			httpURLConnection.setRequestProperty("accept", "*/*");
//			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
//			httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			httpURLConnection.setConnectTimeout(defaultConnectTimeOut);
//			httpURLConnection.setReadTimeout(defaultReadTimeOut);
//
//			// // Fidder监听请求
//			// if ((!proxyHost.equals("") && !proxyPort.equals(""))) {
//			// System.setProperty("http.proxyHost", proxyHost);
//			// System.setProperty("http.proxyPort", proxyPort);
//			// }
//
//			// 建立连接
//			httpURLConnection.connect();
//			result = getResponse(httpURLConnection, in, result);
//
//
//
//		} catch (Exception requestException) {
//			System.err.println("发送GET请求出现异常!" + requestException);
//			// requestException.printStackTrace();
//		}
//		// 关闭输入流
//		finally {
//			try {
//				if (in != null) {
//					in.close();
//				}
//			} catch (Exception closeException) {
//				closeException.printStackTrace();
//			}
//		}
//		return result;
//	}
//
//	// 公用方法，根据返回码处理返回值
//	public static Map<String, String> getResponse(HttpURLConnection httpURLConnection, BufferedReader in, Map<String, String> result)
//			throws UnsupportedEncodingException, IOException {
//		boolean flag = false;
//		for (int i = 0; i < successCode.length; i++) {
//			if (successCode[i] == httpURLConnection.getResponseCode()) {
//				flag = true;
//				break;
//			}
//		}
//
//		// 返回码非“successCode”时，response为返回message
//		if (flag) {
//			// 定义 BufferedReader输入流来读取URL的响应
//			in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//			String line;
//
//			// 获取所有响应头字段
//			Map<String, List<String>> Hearder = httpURLConnection.getHeaderFields();
//			for (String key : Hearder.keySet()) {
//				result.put(key, Hearder.get(key).toString());
//			}
//
//			// responseList.clear();
//			String responseStr = "";
//			while ((line = in.readLine()) != null) {
//				responseStr += line;
//			}
//
//			// Content长度限制
//			if (responseStr.length() > contentLengthAllow && contentLengthAllow > 0) {
//				responseStr = responseStr.substring(0, contentLengthAllow);
//			}
//
//			result.put("Message", httpURLConnection.getResponseMessage());
//			result.put("Code", String.valueOf(httpURLConnection.getResponseCode()));
//			result.put("Response", responseStr);
//		} else {
//			result.put("Message", httpURLConnection.getResponseMessage());
//			result.put("Code", String.valueOf(httpURLConnection.getResponseCode()));
//			//
//			result.put("Response", httpURLConnection.getResponseMessage());
//			// 获取所有响应头字段
//			Map<String, List<String>> Hearder = httpURLConnection.getHeaderFields();
//			for (String key : Hearder.keySet()) {
//				result.put(key, Hearder.get(key).toString());
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * 将16位byte[] 转换为32位String,用于GetMD5String()
//	 * @param buffer 16位的byte[]
//	 * @return 32位的字符串
//	 * @author yuyang
//	 * 创建时间：2016-09-02
//	 * 更新时间：2016-09-02
//	 */
//	private static String toHex(byte buffer[]) {
//		StringBuffer sb = new StringBuffer(buffer.length * 2);
//		for (int i = 0; i < buffer.length; i++) {
//			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
//			sb.append(Character.forDigit(buffer[i] & 15, 16));
//		}
//
//		return sb.toString();
//	}
//
//	/**
//	 * 给字符串生成MD5码
//	 * @param str,输入字符串
//	 * @return res,生成的MD5码字符串
//	 * @author yuyang
//	 * 创建时间：2016-09-02
//	 * 更新时间：2016-09-02
//	 */
//	public static String GetMD5String(String str) {
//		String res = null;
//		try {
//			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//			messageDigest.update(str.getBytes());
//			res = toHex(messageDigest.digest());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//	/**
//	 * 接口：删除用户的邮箱帐户。如果刪除失败，会抛出异常
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @author yuyang
//	 * 创建时间：2016-09-02
//	 * 更新时间：2016-09-02
//	 */
//	public static void IF_DelUserAccount(String DomainName, String UserEmailAccount) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?act=del&email=" + UserEmailAccount + "&salt=" + GetMD5String(UserEmailAccount);
//		System.out.println(URLCommand);
//		Map<String, String> res = json2map(get(URLCommand).get("Response"));
//		if (!"1".equals(res.get("status"))) {
//			throw new Exception("Delete user account(" + UserEmailAccount + ") failed!(" + res + ")(" + DomainName + ")");
//		}
//		Log.logInfo("删除邮箱帐户(" + UserEmailAccount + "),域名(" + DomainName + ").");
//	}
//
//	/**
//	 * 
//	* @测试点: IF_DelUserAccount2 
//	* @验证点: 接口删除
//	* @使用环境： @param DomainName
//	* @使用环境： @param UserEmailAccount
//	* @使用环境： @throws Exception    测试环境
//	* @备注： void    
//	* @author zhangjun 
//	* @date 2016年12月5日
//	 */
//	public static void IF_DelUserAccount2(String DomainName, String UserEmailAccount) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?act=del_auto_account&mod=user&email=" + UserEmailAccount;
//		System.out.println(URLCommand);
//		PublicFun.get(URLCommand);
//
//	}
//
//	/**
//	 * 删除己注册的邮箱帐户，如果删除后帐户仍然是己注册状态，则重试;如果删除前，查询到用户是未注册状态，则不执行删除动作
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param loopTimes  尝试删除的次数
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-06  更新时间：2016-10-06
//	 */
//	public static void loopDelUserAccount(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
//		boolean eflag = true;
//		int ret = loopQueryUserRegisterStatus(DomainName, UserEmailAccount, 3);
//		if (ret == 1) {
//
//			boolean res = false;
//			for (int i = 1; i <= loopTimes; i++) {
//				if (i > 1) {
//					Log.logWarn(i + " times,尝试删除邮箱帐户(" + UserEmailAccount + ").");
//				}
//
//				try {
//					PublicFun.IF_DelUserAccount(DomainName, UserEmailAccount);
//					ret = loopQueryUserRegisterStatus(DomainName, UserEmailAccount, 3);
//					if (ret == 1) {
//						Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
//					} else if (ret == 0) {
//						if (i > 1) {
//							Log.logWarn(i + " times,删除邮箱帐户成功(" + UserEmailAccount + ").");
//						}
//						res = true;
//						break;
//					}
//				} catch (Exception e) {
//					if (eflag) {
//						e.printStackTrace();
//						eflag = false;
//					}
//					Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
//				}
//			}
//
//			if (!res) {
//				Log.logWarn("连续 " + loopTimes + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
//				throw new RetryException();
//			}
//
//		} else if (ret == 0) {
//			Log.logInfo("Test account is not registered(" + UserEmailAccount + "),不需要执行删除操作.");
//		}
//	}
//
//	/**
//	 * 接口：查询用户邮箱帐号是否己注册
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐号
//	 * @return 1-用户邮箱帐号己注册;0-未注册
//	 * @author yuyang
//	 * 创建时间：2016-09-02
//	 * 更新时间：2016-09-02
//	 */
//	public static int IF_QueryUserRegisterStatus(String DomainName, String UserEmailAccount) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?act=check_email&email=" + UserEmailAccount;
//		int ret = Integer.valueOf((json2map(get(URLCommand).get("Response"))).get("status")).intValue();
//		if (ret != 0 && ret != 1)
//			throw new Exception(
//					"Query user register status failed,The user account(" + UserEmailAccount + ") is abnormal!(" + ret + ")(" + DomainName + ")");
//		return ret;
//	}
//
//	/**
//	 * 查询用户邮箱帐号是否己注册，如果查询失败，则重试
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐号
//	 * @param loopTimes 重试次数
//	 * @return 1-用户邮箱帐号己注册;0-未注册
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 */
//	public static int loopQueryUserRegisterStatus(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
//		boolean eflag = true;
//
//		int res = -1;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态.");
//			}
//
//			try {
//				res = IF_QueryUserRegisterStatus(DomainName, UserEmailAccount);
//				if (i > 1) {
//					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态成功.");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
//			}
//		}
//
//		if (res == -1) {
//			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 接口：查询用户是否己作邮箱验证
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐号
//	 * @return 1-邮箱己验证;0-邮箱未验证;-1-邮箱未注册
//	 * @author yuyang 创建时间：2016-09-02 更新时间：2016-09-02
//	 */
//	public static int IF_QueryUserVerifyEmailStatus(String DomainName, String UserEmailAccount) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?act=get_user_info&email=" + UserEmailAccount;
//		Map<String, String> res = json2map(get(URLCommand).get("Response"));
//		if (res != null) {
//			int ret = Integer.valueOf(res.get("is_validated")).intValue();
//			if (ret != 0 && ret != 1)
//				throw new Exception("Query user verify email status failed,The email verification status(" + UserEmailAccount + ") is abnormal!("
//						+ ret + ")(" + DomainName + ")");
//			return ret;
//		} else {
//			return -1;
//		}
//	}
//
//	/**
//	 * 查询用户是否己作邮箱验证，如果查询失败，则重试
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐号
//	 * @param loopTimes 重试次数
//	 * @return 1-邮箱己验证;0-邮箱未验证;-1-邮箱未注册
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 */
//	public static int loopQueryUserVerifyEmailStatus(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
//		boolean eflag = true;
//
//		int res = -2;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态.");
//			}
//
//			try {
//				res = IF_QueryUserVerifyEmailStatus(DomainName, UserEmailAccount);
//				if (i > 1) {
//					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态成功.");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
//			}
//		}
//
//		if (res == -2) {
//			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 把json字符串转换成Map
//	 * @param str_json 待转换的json字符串
//	 * @return 转换的结果Map
//	 * @author yuyang
//	 * 创建时间：2016-09-02
//	 * 更新时间：2016-09-02
//	 */
//	private static Map<String, String> json2map(String str_json) {
//		Map<String, String> res = null;
//		try {
//			Gson gson = new Gson();
//			res = gson.fromJson(str_json, new TypeToken<Map<String, String>>() {
//			}.getType());
//		} catch (JsonSyntaxException e) {
//			// e.printStackTrace();
//		}
//		return res;
//	}
//
//	/**
//	 * 根据tag，匹配并返回全部包含tag的连接
//	 * @param str_html_content html内容
//	 * @param tag 提取link的tag，即匹配字符串，一般填写域名
//	 * @return 匹配到的全部link数组
//	 * @author yuyang
//	 * 创建时间：2016-09-03
//	 * 更新时间：2016-09-03
//	 */
//	public static ArrayList<String> getLinkOfTag(String str_html_content, String tag) {
//		ArrayList<String> match_links = new ArrayList<String>();
//		try {
//			ArrayList<String> links = HtmlParse.getAllALink(str_html_content);
//			for (String link : links) {
//				if (link.contains(tag)) {
//					match_links.add(link);
//				}
//			}
//		} catch (ParserException e) {
//			e.printStackTrace();
//		}
//		return match_links;
//	}
//
//	/**
//	 * 根据excel中提供的json数据的key值获取value值
//	 * @param name :key值
//	 * @param testCase :测试方法名
//	 * @return String
//	 * @author linchaojiang ,创建时间：2016-09-06,更新时间：2016-09-06
//	 */
//	public static String getJsonData(String name, String testCase) {
//		String value = null;
//		String json = Startbrowser.testCasemap.get(testCase).get(4).toString();// 读取excel中的数据，并把中文的单引号和冒号转换为英文
//		String json2 = json.replace("’", "'").replace("‘", "'").replace("，", ",").replace("：", ":");
//		try {
//			JSONObject jsonObject = JSONObject.fromObject(json2);
//			value = jsonObject.getString(name);
//		} catch (JSONException e) {
//			Log.logInfo(e.getMessage() + "\n读取excel中的json数据出错!!!");
//			e.printStackTrace();
//		}
//		return value;
//	}
//
//	/**
//	 * 获取运行所使用的浏览器信息
//	 * @param jse JavascriptExecutor对象
//	 * @return 浏览器信息，如版本等
//	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
//	 */
//	public static String getBrowserInfo(JavascriptExecutor jse) {
//		String JS_getBrowserInfo = "var browser_info = window.navigator.userAgent;return browser_info;";
//		String res = "";
//		try {
//			res = (String) (jse).executeScript(JS_getBrowserInfo);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//	/**
//	 * 获取本地的IP和域名
//	 * @return [] [0]是IP地址，[1]是域名
//	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
//	 */
//	public static String[] getIPv4() {
//		ArrayList<String> ipv4 = new ArrayList<String>();
//		ArrayList<String> ipv4host = new ArrayList<String>();
//		InetAddress ip;
//		String[] ipv4res = new String[2];
//
//		try {
//			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
//			while (allNetInterfaces.hasMoreElements()) {
//				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
//				while (addresses.hasMoreElements()) {
//					ip = (InetAddress) addresses.nextElement();
//					if (ip != null && ip instanceof Inet4Address) {
//						ipv4.add(ip.getHostAddress());
//						ipv4host.add(ip.getCanonicalHostName());
//					}
//				}
//			}
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//
//		int ipv4Size = ipv4.size();
//		String[] ipv4Array = (String[]) ipv4.toArray(new String[ipv4Size]);
//		String[] ipv4hostArray = (String[]) ipv4host.toArray(new String[ipv4Size]);
//		StringBuilder ipv4sb = new StringBuilder(ipv4Size);
//		StringBuilder ipv4hostsb = new StringBuilder(ipv4Size);
//		for (int i = 0; i < ipv4Size; i++) {
//			if (!"127.0.0.1".equals(ipv4Array[i])) {
//				ipv4sb.append(ipv4Array[i]);
//				ipv4hostsb.append(ipv4hostArray[i]);
//				if (i < ipv4Size - 1) {
//					ipv4sb.append(", ");
//					ipv4hostsb.append(", ");
//				}
//			}
//		}
//
//		ipv4res[0] = ipv4sb.toString();
//		ipv4res[1] = ipv4hostsb.toString();
//		return ipv4res;
//	}
//
//	/**
//	 * 将字符串转为boolean,转换的规则是0-false,其它值-true
//	 * @param str 待转换的字符串
//	 * @return false或true
//	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
//	 */
//	public static boolean string2boolean(String str) {
//		boolean res = false;
//		try {
//			res = !(Integer.valueOf(str.trim()).intValue() == 0);
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//	/**
//	 * 输出测试时长格式化后的字符串
//	 * @param Seconds 运行时期长，单位是秒
//	 * @return 测试时长字符串，格式是 天 时 分 秒
//	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
//	 */
//	public static String getRunTimeStr(int Seconds) {
//		String res = null;
//
//		int days = Seconds / 86400;
//		int hours = (Seconds % 86400) / 3600;
//		int minutes = ((Seconds % 86400) % 3600) / 60;
//		int seconds = ((Seconds % 86400) % 3600) % 60;
//		try {
//
//			res = ((days > 0) ? Integer.toString(days) + "d " : "") + ((hours > 0) ? Integer.toString(hours) + "h:" : "")
//					+ ((minutes > 0) ? Integer.toString(minutes) + "m:" : "") + ((seconds >= 0) ? Integer.toString(seconds) + "s" : "");
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//
//	/**
//	 * 获取测试用例的验证点信息，从xls文件中
//	 * @param testcasename 测试用例(或者叫方法)的名称
//	 * @return 验证点信息
//	 * @author yuyang ,创建时间：2016-10-06,更新时间：2016-11-16
//	 * 更新说明(2016-10-19)：增加测试描述信息，验证点和测试描述信息由局部变量改为成员变量
//	 * 更新说明(2016-11-16)：由使用Startbrowser.testCasemap的方式，改为参数传递的方式，参数是testCaseMap
//	 * 更新说明(2016-11-16)：由使用Startbrowser.testCasemap的方式，改为参数传递的方式，参数是testCaseMap
//	 */
//	public static void getTestCaseInfo(Map<String, List> testCaseMap, String testcasename, testInfo info, boolean startMethodFlag) {
//		Startbrowser.testDescription = "";
//		Startbrowser.testCheckpoint = "";
//
//		String Description = "";
//		try {
//			Description = testCaseMap.get(testcasename).get(2).toString();
//			int tmpidx = Description.indexOf(".");
//			if (tmpidx >= 0) {
//				Description = Description.substring(tmpidx + 1);
//			}
//			if (Description.length() > 0) {
//				String lastchar = Description.substring(0, Description.length() - 1);
//				if (!lastchar.equals(".")) {
//					Description += ".";
//				}
//			}
//			Description = "测试描述:" + Description;
//			info.testDescription = Description;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的测试描述信息.");
//		}
//		String Checkpoint = "";
//		try {
//			Checkpoint = testCaseMap.get(testcasename).get(3).toString();
//			int tmpidx = Checkpoint.indexOf(".");
//			if (tmpidx >= 0) {
//				Checkpoint = Checkpoint.substring(tmpidx + 1);
//			}
//			if (Checkpoint.length() > 0) {
//				String lastchar = Checkpoint.substring(0, Checkpoint.length() - 1);
//				if (!lastchar.equals(".")) {
//					Checkpoint += ".";
//				}
//			}
//			Checkpoint = "验证点  :" + Checkpoint;
//			info.testCheckpoint = Checkpoint;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的验证点信息.");
//		}
//		String Correlation = "";
//		try {
//			Correlation = testCaseMap.get(testcasename).get(5).toString();
//			int tmpidx = Correlation.indexOf(".");
//			if (tmpidx >= 0) {
//				Correlation = Correlation.substring(tmpidx + 1);
//			}
//			if (Correlation.length() > 0) {
//				String lastchar = Correlation.substring(0, Correlation.length() - 1);
//				if (!lastchar.equals(".")) {
//					Correlation += ".";
//				}
//			}
//			Correlation = "相关性说明 :" + Correlation;
//			info.testCorrelation = Correlation;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的相关性说明信息.");
//		}
//
//		Startbrowser.testDescription = Description;
//		Startbrowser.testCheckpoint = Checkpoint;
//
//		info.testExceptionNote = "执行流程异常" + "(" + info.testCheckpoint + ").";
//		info.methodName = testcasename;
//		info.methodNameFull = info.moduleName + "." + info.methodName;
//
//		if (startMethodFlag) {
//			Log.logInfo("(" + info.methodNameFull + ")...test method start...");
//			if (Startbrowser.correFail) {
//				Log.logWarn("测试脚本关联失败.");
//				Assert.fail();
//			}
//		}
//	}
//
//	/**
//	 * 获取测试用例的验证点信息，从xls文件中
//	 * @param testcasename 测试用例(或者叫方法)的名称
//	 * @return 验证点信息
//	 * @author yuyang ,创建时间：2016-11-23,更新时间：2016-11-23
//	 * 备注：从getTestCaseInfo修改而来，因为Startbrowser有两个文件，为了适应这种情况，使关联标志正确
//	 */
//	public static void getTestCaseInfoWap(Map<String, List> testCaseMap, String testcasename, testInfo info, boolean startMethodFlag) {
//		String Description = "";
//		try {
//			Description = testCaseMap.get(testcasename).get(2).toString();
//			int tmpidx = Description.indexOf(".");
//			if (tmpidx >= 0) {
//				Description = Description.substring(tmpidx + 1);
//			}
//			if (Description.length() > 0) {
//				String lastchar = Description.substring(0, Description.length() - 1);
//				if (!lastchar.equals(".")) {
//					Description += ".";
//				}
//			}
//			Description = "测试描述:" + Description;
//			info.testDescription = Description;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的测试描述信息.");
//		}
//		String Checkpoint = "";
//		try {
//			Checkpoint = testCaseMap.get(testcasename).get(3).toString();
//			int tmpidx = Checkpoint.indexOf(".");
//			if (tmpidx >= 0) {
//				Checkpoint = Checkpoint.substring(tmpidx + 1);
//			}
//			if (Checkpoint.length() > 0) {
//				String lastchar = Checkpoint.substring(0, Checkpoint.length() - 1);
//				if (!lastchar.equals(".")) {
//					Checkpoint += ".";
//				}
//			}
//			Checkpoint = "验证点  :" + Checkpoint;
//			info.testCheckpoint = Checkpoint;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的验证点信息.");
//		}
//		String Correlation = "";
//		try {
//			Correlation = testCaseMap.get(testcasename).get(5).toString();
//			int tmpidx = Correlation.indexOf(".");
//			if (tmpidx >= 0) {
//				Correlation = Correlation.substring(tmpidx + 1);
//			}
//			if (Correlation.length() > 0) {
//				String lastchar = Correlation.substring(0, Correlation.length() - 1);
//				if (!lastchar.equals(".")) {
//					Correlation += ".";
//				}
//			}
//			Correlation = "相关性说明 :" + Correlation;
//			info.testCorrelation = Correlation;
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("未获取到测试用例(" + testcasename + ")的相关性说明信息.");
//		}
//
//		info.testExceptionNote = "执行流程异常" + "(" + info.testCheckpoint + ").";
//		info.methodName = testcasename;
//		info.methodNameFull = info.moduleName + "." + info.methodName;
//
//		if (startMethodFlag) {
//			Log.logInfo("(" + info.methodNameFull + ")...test method start...");
//			if (StartPhoneBrowser.correFail) {
//				Log.logWarn("测试脚本关联失败.");
//				Assert.fail();
//			}
//		}
//	}
//
//	/**
//	 * 打印当前TestCase的信息
//	 * @author yuyang ,创建时间：2016-10-19,更新时间：2016-11-16
//	 * 备注：需要与getTestCaseInfo(testCasemap, methodName)配对使用
//	 * 更新说明(2016-11-16)：由使用Startbrowser.testDescription的方式，改为参数传递的方式
//	 */
//	public static void printTestCaseInfo(testInfo info) {
//		Log.logInfo(info.testDescription);
//		Log.logInfo(info.testCheckpoint);
//	}
//
//	/**
//	 * 测试方法执行异常时，打印当前TestCase的信息，包括异常提示和相关性说明
//	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-11-17
//	 * 备注：需要与getTestCaseInfo(testCasemap, methodName)配对使用
//	 * 更新说明(2016-11-16)：由使用Startbrowser.testDescription的方式，改为参数传递的方式
//	 */
//	public static void printTestCaseExceptionInfo(testInfo info) {
//		Log.logWarn(info.testExceptionNote);
//		Log.logWarn(info.testCorrelation);
//	}
//
//	/**
//	 * 延时若干秒
//	 * @param seconds 延时秒数,最小为1
//	 * @author yuyang ,创建时间：2016-10-07,更新时间：2016-10-07
//	 */
//	public static void sleep(int seconds) {
//		try {
//			if (seconds > 0) {
//				Thread.sleep(seconds * 1000);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("Error happened while delaying for " + seconds + "seconds.");
//		}
//	}
//
//	/**
//	 * 清理价格字符前的美元符串，获取价格字符串，如果价格前后有空格会一并删除掉
//	 * @param 包含美元符号或其它符串的价格字符串,如,$2.59
//	 * @author yuyang ,创建时间：2016-10-07,更新时间：2016-10-07
//	 */
//	public static String getPriceStr(String priceStr) {
//		String res = "";
//		char c = 0;
//		try {
//			if (hasLength(priceStr)) {
//				priceStr = priceStr.trim();
//				for (int i = 0; i < priceStr.length(); i++) {
//					c = priceStr.charAt(i);
//					if (c >= '0' && c <= '9') {
//						res = priceStr.substring(i);
//						break;
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("Error happened while getting price string.");
//		}
//		return res;
//	}
//
//	/**
//	 * 数字字符串数组比较排序，返回排序后的数字字符串数组，排序是先把字符串转成Integer后，再按从小到大排
//	 * @param list 字符串数组
//	 * @return 排序后的字符串数组
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static List<String> myListSort(List<String> list) {
//		Collections.sort(list, new Comparator() {
//			@Override
//			public int compare(Object o1, Object o2) {
//				return new Integer((String) o1).compareTo(new Integer((String) o2));
//			}
//		});
//		return list;
//	}
//
//	/**
//	 * 获取方法名
//	 * @return
//	 */
//	public static String GetMethodName() {
//		String methodnamestr = Thread.currentThread().getStackTrace()[2].getMethodName();
//		return methodnamestr;
//	}
//
//	/**
//	 * 删除URL字符串中的全部参数
//	 * @param URLStr URL字符串
//	 * @return 删除全部参数后的URL字符串
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-10-12
//	 */
//	public static String removeURLParameter(String URLStr) throws Exception {
//		if (hasLength(URLStr)) {
//			URLStr = URLStr.trim();
//			int idx = URLStr.indexOf("?");
//			if (idx >= 0)
//				URLStr = URLStr.substring(0, idx);
//		}
//		return URLStr;
//	}
//
//	/**
//	 * 清除URL参数后，再修改URL的后缀为.html，适用于web
//	 * @param URLStr URL字符串
//	 * @return 修改后的URL
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-10-12
//	 */
//	public static String modifyURLSuffix(String URLStr) throws Exception {
//		URLStr = removeURLParameter(URLStr);
//		if (hasLength(URLStr)) {
//			URLStr = URLStr.trim();
//			if (!URLStr.substring(URLStr.length() - 5).equals(".html")) {
//				int idx = URLStr.lastIndexOf('.');
//				if (idx >= 0)
//					URLStr = URLStr.substring(0, idx) + ".html";
//			}
//		}
//		return URLStr;
//	}
//
//	/**
//	 * 清除URL参数后，再修改URL的后缀为.html，替换商品URL为wap端的URL,适用于wap
//	 * @param URLStr URL字符串
//	 * @return 修改后的URL
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang ,创建时间：2016-11-18,更新时间：2016-11-18
//	 */
//	public static String modifyURLSuffixWap(String DomainName, String URLStr) throws Exception {
//		URLStr = removeURLParameter(URLStr);
//		URLStr = modifyURLSuffix(URLStr);
//		if (hasLength(URLStr)) {
//			URLStr = URLStr.trim();
//			if (DomainName.contains("a.s1.egomsl.com") || DomainName.contains("trunk.s1.egomsl.com")) {
//				URLStr = URLStr.replaceFirst("sammydress", "wap-sammydress");
//			} else if (DomainName.equals("www.sammydress.com")) {
//				URLStr = URLStr.replaceFirst("www", "m");
//			}
//		}
//		return URLStr;
//	}
//
//	/**
//	 * 删除URL字符串中的http头
//	 * @param URLStr URL字符串
//	 * @return 删除http头后的URL字符串
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang ,创建时间：2016-11-24,更新时间：2016-11-24
//	 */
//	public static String removeURLHttp(String URLStr) throws Exception {
//		if (hasLength(URLStr)) {
//			URLStr = URLStr.trim();
//			if (URLStr.substring(0, 7).equals("http://")) {
//				URLStr = URLStr.substring(7);
//			}
//			if (URLStr.substring(0, 8).equals("https://")) {
//				URLStr = URLStr.substring(8);
//			}
//		}
//		return URLStr;
//	}
//
//	/**
//	 * 打印断言信息
//	 * @param assertStr 断言命令字符串
//	 * @param msgType 断言类型
//	 * @param msg 自定义的信息
//	 * @author yuyang ,创建时间：2016-10-13,更新时间：2016-10-13
//	 */
//	public static void assertInfo(String assertStr, String msgType, String msg) {
//		if (assertStr.equals("assertTrue")) {
//			if (msgType.equals("contains")) {
//				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 包含.");
//			} else if (msgType.equals("greater")) {
//				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 大于.");
//			} else if (msgType.equals("less")) {
//				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 小于.");
//			}
//		} else if (assertStr.equals("assertEquals")) {
//			Log.logInfo(assertStr + ":目标值和期望值之间的关系是 等于.");
//		} else if (assertStr.equals("assertNotEquals")) {
//			Log.logInfo(assertStr + ":目标值和期望值之间的关系是 不等于.");
//		} else {
//			Log.logInfo(assertStr + ":" + msg);
//		}
//	}
//
//
//	
//
//	/**
//	 * 从产品页面的URL获取产品ID，或者叫商品ID
//	 * @param URLStr
//	 * @return 商品ID
//	 * @author yuyang ,创建时间：2016-10-14,更新时间：2016-10-14
//	 */
//	public static String getProductID(String URLStr) {
//		String IDStr = "";
//		if (URLStr.substring(0, 4).equals("http")) {
//			int idx = URLStr.indexOf("product");
//			int idx2 = URLStr.lastIndexOf('.');
//			if (idx >= 0 && idx2 > idx) {
//				IDStr = URLStr.substring(idx + "product".length(), idx2);
//			}
//		}
//		if (!hasLength(IDStr)) {
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "getProductID(" + URLStr + ")");
//			Log.logWarn("失败获取商品ID(" + URLStr + "),测试中止.");
//			Assert.fail();
//		}
//		return IDStr;
//	}
//
//	/**
//	 * 
//	* @测试点: IF_Verification 
//	* @验证点:通过cookies 获得验证码
//	* @param driver  
//	*  @param domainName   测试路径
//	*  @param requestId  请求的id（每次请求保持唯一性）
//	*  @param privatekey 测试的key，需分区正式和测试
//	*  @param ftc   验证码类型，登录=login，注册=regist，忘记密码ftc=''
//	*  @return    测试环境
//	* @备注： String    
//	* @author zhangjun 
//	* @date 2016年12月2日
//	 */
//	public static String IF_Verification(WebDriver driver, String domainName, String requestId, String privatekey,
//            String ftc) {
//        String getdata = null;
//        String getsign;
//        String getResponse;
//        String getverification;
//        Set<Cookie> cookies = driver.manage().getCookies();
//        for (Cookie cookie : cookies) {
//            if (domainName.contains("rosegal")) {
//                if (cookie.getName().contains("RG_SESSIONID")) { // 取出cookie值
//                    getdata = "a:1:{s:10:\"session_id\";s:26:\"" + cookie.getValue() + "\";}";// 生成虚序列化data
//                }
//            } else {
//                if (cookie.getName().contains("PHPSESSID")) { // 取出cookie值
//                    getdata = "a:1:{s:10:\"session_id\";s:26:\"" + cookie.getValue() + "\";}";// 生成虚序列化data
//                }
//            }
//        }
//        getsign = privatekey + requestId + getdata;// 获得私钥`
//        System.out.println(requestId);
//        String sign = PublicFun.GetMD5String(getsign);// 生成MD5
//        String URLCommand = "http://" + domainName + "/auto/api.php?mod=common&act=get_user_sign&requestId=" + requestId
//                + "&sign=" + sign + "&data=" + getdata + "&ftc=" + ftc;
//        System.out.println(URLCommand);
//        Map<String, String> res = PublicFun.get(URLCommand);
//        getResponse = res.get("Response");
//        if (domainName.contains("rosegal")) {
//            getverification    =getResponse.split("verify")[1].split("s:4")[1].split("\"")[1].replace("\\", "");
//        }else {
//            int beginIndex = getResponse.lastIndexOf("s:6");// jsoin解析不出来，暂用字符串形式截取验证码
//            getverification = getResponse.substring(beginIndex + 6, beginIndex + 12); // 取得字符串
//
//        }
//        System.out.println(getverification);
//        return getverification;
//    }
//
//	/**
//	 * 接口：清空用户购物车(或收藏夹,或save for later)中的商品
//	 * @param DomainName 域名
//	 * @param Type 支持3种类型,分别是:Cart,Collect,SaveForLater
//	 * @param UserEmailAccount 用户的邮箱帐号
//	 * @return 1-清除成功;0-清除失败
//	 * @throws Exception
//	 * @author yuyang 创建时间：2016-10-14  更新时间：2016-10-14
//	 */
//	public static int IF_ClearProducts(String DomainName, String Type, String UserEmailAccount) throws Exception {
//		String actStr = "";
//		if (Type.equals("Cart")) {
//			actStr = "clear_cart";
//		} else if (Type.equals("Collect")) {
//			actStr = "clear_user_collect";
//		} else if (Type.equals("SaveForLater")) {
//			actStr = "clear_save_for_later";
//		} else {
//			Log.logWarn("接口命令无效,中止测试(" + GetMethodName() + ",Type=" + Type + ").");
//			Assert.fail();
//		}
//
//		String URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=" + actStr + "&email=" + UserEmailAccount;
//		Map<String, String> res = json2map(get(URLCommand).get("Response"));
//		if (res != null) {
//			String ret = res.get("_resultcode");
//			if (!ret.equals("200"))
//				throw new Exception("Clearing all products in cart of user(" + UserEmailAccount + ") is abnormal!(" + ret + ")(" + DomainName + ")");
//			return 1;
//		}
//		return 0;
//	}
//
//	/**
//	 * 清空用户购物车(或收藏夹,或save for later)中的商品，如果清除失败，则重试多次
//	 * @param DomainName 域名
//	 * @param Type 支持3种类型,分别是:Cart,Collect,SaveForLater
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param loopTimes  尝试清空的次数
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-14  更新时间：2016-10-14
//	 */
//	public static void loopClearProducts(String DomainName, String Type, String UserEmailAccount, int loopTimes) throws Exception {
//		boolean eflag = true;
//
//		String logStr = "";
//		if (Type.equals("Cart")) {
//			logStr = "购物车";
//		} else if (Type.equals("Collect")) {
//			logStr = "收藏夹";
//		} else if (Type.equals("SaveForLater")) {
//			logStr = "Save for later";
//		} else {
//			Log.logWarn("接口命令无效,中止测试(" + GetMethodName() + ",Type=" + Type + ").");
//			Assert.fail();
//		}
//		boolean res = false;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试清空用户" + logStr + "(" + UserEmailAccount + ").");
//			}
//
//			try {
//				int ret = PublicFun.IF_ClearProducts(DomainName, Type, UserEmailAccount);
//				if (ret == 0) {
//					Log.logWarn(i + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
//					continue;
//				}
//
//				if (i > 1) {
//					Log.logWarn(i + " times,清空用户" + logStr + "成功(" + UserEmailAccount + ").");
//				}
//				res = true;
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
//			}
//		}
//
//		if (!res) {
//			Log.logWarn("连续 " + loopTimes + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
//			throw new RetryException();
//		}
//	}
//
//	/**
//	 * 清空用户购物车,收藏夹和save for later中的商品，如果清除失败，则重试多次
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param loopTimes  尝试清空的次数
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-14  更新时间：2016-10-14
//	 */
//	public static void loopClearProductsAllTypes(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
//		loopClearProducts(DomainName, "Cart", UserEmailAccount, loopTimes);
//		loopClearProducts(DomainName, "Collect", UserEmailAccount, loopTimes);
//		loopClearProducts(DomainName, "SaveForLater", UserEmailAccount, loopTimes);
//	}
//
//	/**
//	 * 给页面输入框元素输入回车，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param inputStr 输入框元素
//	 * @param loopTimes 尝试单击的次数
//	 * @param explicitWaitTimeout 显示等待元素的超时时间
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-10-17,更新时间：2016-10-17
//	 */
//	public static void loopSendKeysEnter(WebDriver driver, Locator locator, String inputStr, int loopTimes, long explicitWaitTimeout)
//			throws Exception {
//		boolean res = false;
//		int sendTimes;
//		WebElement sendElement = null;
//		boolean eflag = true;
//
//		for (sendTimes = 1; sendTimes <= loopTimes; sendTimes++) {
//			if (sendTimes > 1) {
//				Log.logWarn(sendTimes + " times,尝试给页面元素(" + inputStr + ")输入回车.");
//			}
//
//			try {
//				sendElement = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(inputStr)));
//				sendElement.sendKeys(Keys.chord(Keys.ENTER));
//				Thread.sleep(100);
//				res = true;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//			}
//			if (res) {
//				if (sendTimes > 1) {
//					Log.logWarn(sendTimes + " times,成功给页面元素(" + inputStr + ")输入回车.");
//				}
//				break;
//			} else {
//				Log.logWarn(sendTimes + " times,失败给页面元素(" + inputStr + ")输入回车.");
//			}
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopSendKeysEnter(" + inputStr + ")_" + sendTimes + "times");
//		}
//		if (!res) {
//			Log.logWarn("连续 " + loopTimes + " times,失败给页面元素(" + inputStr + ")输入回车.");
//			throw new RetryException();
//		}
//	}
//
//	/**
//	 * 判断对象是否存在
//	 * @param by 待检查的元素,如id,xpath等
//	 * @return boolean,true-查找到，false-未查找到
//	 * @author yuyang 创建时间:2016-10-17 更新时间:2016-10-17
//	 * 备注：从StartBrowser里面拷备出来的，因为要调用这个方法
//	 */
//	public static boolean isElementPresent(WebDriver driver, By by) {
//		try {
//			driver.findElement(by);
//			return true;
//		} catch (NoSuchElementException e) {
//			return false;
//		}
//	}
//
//	/**
//	 * 使用AndroidDriver判断对象是否存在
//	 * @param by 待检查的元素,如id,xpath等
//	 * @return boolean,true-查找到，false-未查找到
//	 * @author yuyang 创建时间:2016-11-22 更新时间:2016-11-22
//	 */
//	public static boolean isElementPresent(AndroidDriver driver, By by) {
//		try {
//			driver.findElement(by);
//			return true;
//		} catch (NoSuchElementException e) {
//			return false;
//		}
//	}
//
//	/**
//	 * 在一定时间内，检查页面元素是否会消失
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param elementStr 页面元素
//	 * @param Timeout 超时时间，单位为秒
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-10-17,更新时间：2016-10-17
//	 */
//	public static boolean loopCheckDisappear(WebDriver driver, Locator locator, String elementStr, long Timeout) throws Exception {
//		boolean res = false;
//		long tmpTimeout = Timeout * 1000;
//
//		long currentTime;
//		long startTime = System.currentTimeMillis();
//		while (true) {
//			currentTime = System.currentTimeMillis();
//			if (currentTime - startTime < 0) {
//				currentTime += 86400;
//			}
//			if (currentTime - startTime >= tmpTimeout) {
//				break;
//			}
//			Thread.sleep(200);
//
//			if (!isElementPresent(driver, locator.getBy(elementStr))) {
//				Log.logInfo("页面元素(" + elementStr + ")消失.");
//				res = true;
//				break;
//			}
//		}
//
//		if (!res) {
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopCheckElementDisappear(" + elementStr + ")_" + Timeout + "seconds");
//			Log.logWarn(Timeout + "seconds超时 ,页面元素(" + elementStr + ")仍显示在页面中.");
//			Assert.fail();
//		}
//		return res;
//	}
//
//	/**
//	 * 在一定时间内，等待页面元素
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param elementStr 页面元素
//	 * @param explicitWaitTimeout  超时时间，单位为秒
//	 * @param assertFlag 断言标志，true-则执行断言，false-则不执行断言
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-10-18,更新时间：2016-10-18
//	 */
//	public static WebElement MyWebDriverWait(WebDriver driver, Locator locator, String elementStr, long explicitWaitTimeout, boolean assertFlag)
//			throws Exception {
//		boolean res = false;
//		WebElement element = null;
//
//		try {
//			element = (new WebDriverWait(driver, explicitWaitTimeout)).until(ExpectedConditions.presenceOfElementLocated(locator.getBy(elementStr)));
//			res = true;
//		} catch (Exception e) {
//			if (assertFlag) {
//				e.printStackTrace();
//			}
//		}
//
//		if (res) {
//			Log.logInfo("等待到页面元素(" + elementStr + ").");
//		} else {
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "MyWebDriverWait(" + elementStr + ")_" + explicitWaitTimeout + "seconds");
//			Log.logWarn(explicitWaitTimeout + "seconds超时 ,未等待到页面元素(" + elementStr + ").");
//			if (assertFlag) {
//				Assert.fail();
//			}
//		}
//
//		return element;
//	}
//
//	/**
//	 * 给非空的页面输入框元素输入字符串，即输入前先检查该输入框是否为空，如果不为空，则输入，输入后检查是否输入成功，如果不成功则重试，如果多次重试后仍然无法输入字符串，则抛出自定义异常
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param inputStr 输入框元素
//	 * @param keys 待输入的字符串
//	 * @param loopTimes 尝试单击的次数
//	 * @param explicitWaitTimeout 显示等待元素的超时时间
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-10-20,更新时间：2016-10-20
//	 */
//	public static void loopCheckSendKeys(WebDriver driver, Locator locator, String inputStr, String keys, int loopTimes, long explicitWaitTimeout)
//			throws Exception {
//		boolean res = false;
//		int sendTimes;
//		WebElement sendElement = null;
//		String inputText;
//		boolean eflag = true;
//
//		inputText = (new WebDriverWait(driver, explicitWaitTimeout)).until(ExpectedConditions.presenceOfElementLocated(locator.getBy(inputStr)))
//				.getAttribute("value");
//		if (hasLength(inputText)) {
//			return;
//		}
//
//		for (sendTimes = 1; sendTimes <= loopTimes; sendTimes++) {
//			if (sendTimes > 1) {
//				Log.logWarn(sendTimes + " times,尝试给页面元素(" + inputStr + ")输入字符串(" + keys + ").");
//			}
//
//			inputText = "";
//			try {
//				sendElement = null;
//				sendElement = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(inputStr)));
//				sendElement.sendKeys(keys);
//				Thread.sleep(100);
//				inputText = sendElement.getAttribute("value");
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//
//			}
//			if (inputText.equals(keys)) {
//				if (sendTimes > 1) {
//					Log.logWarn(sendTimes + " times,成功给页面元素(" + inputStr + ")输入字符串.");
//				}
//				res = true;
//				break;
//			} else {
//				Log.logWarn(sendTimes + " times,失败给页面元素(" + inputStr + ")输入字符串(" + keys + ").");
//			}
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopCheckSendKeys(" + inputStr + ")_" + sendTimes + "times");
//		}
//
//		if (!res) {
//			Log.logWarn("连续 " + loopTimes + " times,失败给页面元素(" + inputStr + ")输入字符串.(" + keys + ")");
//			throw new RetryException();
//		}
//	}
//
//	/**
//	 * 使用鼠标滑动到页面元素上的 (x,y)位置，单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素，则抛出自定义异常
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param clickStr 用来作单击动作的元素
//	 * @param x 滑动到页面元素上的x位置
//	 * @param y 滑动到页面元素上的y位置
//	 * @param FindStr 用来查找的新页面元素
//	 * @param loopTimes 尝试单击的次数
//	 * @param explicitWaitTimeout 显示等待元素的超时时间
//	 * @throws Exception 除超时查找元素的异常之外的其它异常抛出
//	 * @author yuyang ,创建时间：2016-10-24,更新时间：2016-10-24
//	 * 注意：用来作单击动作的元素，可是等待出现
//	 */
//	public static void loopClickMoveTo(WebDriver driver, Locator locator, String clickStr, int x, int y, String FindStr, int loopTimes,
//			long explicitWaitTimeout) throws Exception {
//		boolean res = false;
//		int clickTimes;
//		WebElement clickElement = null;
//		WebElement findElement = null;
//		boolean eflag = true;
//		boolean e1flag = true;
//
//		for (clickTimes = 1; clickTimes <= loopTimes; clickTimes++) {
//			if (clickTimes > 1) {
//				Log.logWarn(clickTimes + " times,尝试单击页面元素(" + clickStr + ").");
//			}
//
//			try {
//				clickElement = null;
//				clickElement = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(clickStr)));
//			} catch (TimeoutException e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,准备单击页面元素(" + clickStr + "),但未等待到该元素.");
//			}
//			if (clickElement != null) {
//
//				try {
//					Actions action = new Actions(driver);
//					action.moveToElement(clickElement, x, y).perform();
//					Thread.sleep(500);
//					action.click().perform();
//					action.release();
//
//					findElement = (new WebDriverWait(driver, explicitWaitTimeout))
//							.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(FindStr)));
//				} catch (TimeoutException e1) {
//					if (e1flag) {
//						e1.printStackTrace();
//						e1flag = false;
//					}
//					Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,单击页面元素后,未等待到新的页面元素(" + FindStr + "),或者单击操作异常.");
//				}
//
//				if (findElement != null) {
//					if (clickTimes > 1) {
//						Log.logWarn(clickTimes + " times,单击页面元素(" + clickStr + ")后,等待到新的页面元素(" + FindStr + ").");
//					}
//					res = true;
//					break;
//				} else {
//					Log.logWarn(clickTimes + " times,单击页面元素(" + clickStr + ")后,未等待到新的页面元素(" + FindStr + ").");
//				}
//			}
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopClick(" + clickStr + ")_" + clickTimes + "times");
//		}
//
//		if (!res) {
//			Log.logWarn("连续 " + loopTimes + " times,单击页面元素(" + clickStr + "),未按预期执行.");
//			throw new RetryException();
//		}
//	}
//
//	/**
//	 * 接口：查询并获取商品列表数据，数据结果是一个JSONObject，目前是获取20个商品
//	 * @param DomainName 域名
//	 * @param typeProduct 产品类型，1-普通商品，2-促销商品，3-清仓商品， 4-普通商品且2<库存<100的
//	 * @param page 搜索的页数
//	 * @param freeShippingFlag 免邮标志，true-免邮，false-不免邮
//	 * @author yuyang ,创建时间：2016-10-25,更新时间：2016-10-25
//	 */
//	public static JSONObject IF_GetProductList(String DomainName, int typeProduct, int page, boolean freeShippingFlag) throws Exception {
//		String URLCommand = "";
//		String freeShippingStr = "";
//		String maxPriceStr = "";
//		if (freeShippingFlag) {
//			freeShippingStr = "&is_freeshipping=y";
//			maxPriceStr = "5";
//		} else { // 测试网不开启免邮，因为测试商品数据比较少，支付也可以随便付
//			maxPriceStr = "500";
//		}
//		if (typeProduct == 2) {
//			URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price=" + maxPriceStr + "&page=" + page
//					+ "&page_size=20&is_promote=y" + freeShippingStr;
//		} else if (typeProduct == 3) {
//			URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price=" + maxPriceStr + "&page=" + page
//					+ "&page_size=20&is_clearance=y" + freeShippingStr;
//		} else if (typeProduct == 4) {
//			URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price=" + maxPriceStr + "&page=" + page
//					+ "&page_size=20&goods_number=100" + freeShippingStr;
//		} else {
//			URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price=" + maxPriceStr + "&page=" + page
//					+ "&page_size=20" + freeShippingStr;
//		}
//		String resstr = get(URLCommand).get("Response");
//		JSONObject res = JSONObject.fromObject(get(URLCommand).get("Response"));
//		if (!"200".equals(res.get("_resultcode").toString())) {
//			throw new Exception("Get product list failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
//		}
//		return res;
//	}
//
//	/**
//	 * 查询并获取商品列表数据，如果获取失败，则重试
//	 * @param DomainName 域名
//	 * @param typeProduct 产品类型，1-普通商品，2-促销商品，3-清仓商品
//	 * @param page 搜索的页数
//	 * @param loopTimes 尝试删除的次数
//	 * @param freeShippingFlag 免邮标志，true-免邮，false-不免邮
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-25  更新时间：2016-10-25
//	 */
//	public static JSONObject loopGetProductList(String DomainName, int typeProduct, int page, int loopTimes, boolean freeShippingFlag)
//			throws Exception {
//		boolean eflag = true;
//
//		String productstr = "普通";
//		if (typeProduct == 1) {
//			productstr = "普通";
//		} else if (typeProduct == 2) {
//			productstr = "促销";
//		} else if (typeProduct == 3) {
//			productstr = "清仓";
//		}
//
//		JSONObject res = null;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试查询并获取" + productstr + "商品列表数据(" + DomainName + ").");
//			}
//
//			try {
//				res = IF_GetProductList(DomainName, typeProduct, page, freeShippingFlag);
//				if (i > 1) {
//					Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
//				} else {
//					Log.logInfo("查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据失败.");
//			}
//		}
//
//		if (res == null) {
//			Log.logWarn("连续 " + loopTimes + " times,查询并获取" + productstr + "商品列表数据失败(" + DomainName + ").");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 通过价格条件获取指定类型的商品,如果获取的是普通商品，会返回库存充足的普通商品
//	 * @param DomainName 域名
//	 * @param typeProduct 产品类型，1-普通商品，2-促销商品，3-清仓商品 ，4-普通商品且2<库存<100的
//	 * @param typePrice 指定类型，1-价格最低,2-价格大于2且小于30的，3-价格不限，4-价格不限且不检查库存
//	 * @param firstProductFlag 1-如果符合typePrice的商品不存在，则返回第一件商品，0-...不返回第一件商品
//	 * @param page 搜索的页数
//	 * @param freeShippingFlag 免邮标志，true-免邮，false-不免邮
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-25  更新时间：2016-10-25
//	 */
//	public static Product GetProduct(String DomainName, int typeProduct, int typePrice, int firstProductFlag, int page, boolean freeShippingFlag)
//			throws Exception {
//		Product res = new Product();
//		JSONObject jsonData = loopGetProductList(DomainName, typeProduct, page, 3, freeShippingFlag);
//		JSONObject jsonGoodsList = jsonData.getJSONObject("goods_list");
//		Iterator it = jsonGoodsList.keys();
//
//		String findkey = "";
//		String firstkey = "";
//		if (typePrice == 1) {
//			double minPrice = 9999;
//			JSONObject jsonGoods;
//			String shopPrice;
//			while (it.hasNext()) {
//				String key = it.next().toString();
//				jsonGoods = jsonGoodsList.getJSONObject(key);
//				shopPrice = jsonGoods.getString("shop_price");
//
//				if (!hasLength(firstkey))
//					firstkey = key;
//
//				if (typeProduct == 1) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
//						continue;
//					}
//				} else if (typeProduct == 2) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
//						continue;
//					}
//				} else if (typeProduct == 3) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
//						continue;
//					}
//				}
//
//				if (hasLength(shopPrice)) {
//					if (Double.valueOf(shopPrice) < minPrice) {
//						minPrice = Double.valueOf(shopPrice);
//						findkey = key;
//					}
//				}
//			}
//		} else if (typePrice == 2) {
//			double maxPrice = 30;
//			double minPrice = 2;
//			JSONObject jsonGoods;
//			String shopPrice;
//			while (it.hasNext()) {
//				String key = it.next().toString();
//				jsonGoods = jsonGoodsList.getJSONObject(key);
//				shopPrice = jsonGoods.getString("shop_price");
//
//				if (!hasLength(firstkey))
//					firstkey = key;
//
//				if (typeProduct == 1) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
//						continue;
//					}
//				} else if (typeProduct == 2) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
//						continue;
//					}
//				} else if (typeProduct == 3) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
//						continue;
//					}
//				}
//
//				if (hasLength(shopPrice)) {
//					if (Double.valueOf(shopPrice) >= minPrice && Double.valueOf(shopPrice) <= maxPrice) {
//						findkey = key;
//						break;
//					}
//				}
//			}
//		} else if (typePrice == 3) {
//			JSONObject jsonGoods;
//			String shopPrice;
//			while (it.hasNext()) {
//				String key = it.next().toString();
//				jsonGoods = jsonGoodsList.getJSONObject(key);
//				shopPrice = jsonGoods.getString("shop_price");
//
//				if (!hasLength(firstkey))
//					firstkey = key;
//
//				if (typeProduct == 1) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
//						continue;
//					}
//				} else if (typeProduct == 2) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
//						continue;
//					}
//				} else if (typeProduct == 3) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
//						continue;
//					}
//				} else if (typeProduct == 4) {
//					String stockNum = jsonGoods.getString("goods_number");
//					if ((Integer.valueOf(stockNum) <= 2) || (Integer.valueOf(stockNum) >= 100)) { // 普通商品的库存需要>2且<100
//						continue;
//					}
//				}
//
//				if (hasLength(shopPrice)) {
//					findkey = key;
//					break;
//				}
//			}
//		} else if (typePrice == 4) {
//			JSONObject jsonGoods;
//			String shopPrice;
//			while (it.hasNext()) {
//				String key = it.next().toString();
//				jsonGoods = jsonGoodsList.getJSONObject(key);
//				shopPrice = jsonGoods.getString("shop_price");
//
//				if (!hasLength(firstkey))
//					firstkey = key;
//
//				if (typeProduct == 1) {
//				} else if (typeProduct == 2) {
//				} else if (typeProduct == 3) {
//				} else if (typeProduct == 4) {
//				}
//
//				if (hasLength(shopPrice)) {
//					findkey = key;
//					break;
//				}
//			}
//		}
//
//		if (firstProductFlag == 1 && !hasLength(res.id)) {
//			findkey = firstkey;
//		}
//
//		if (hasLength(findkey)) {
//			res.id = findkey;
//			res.URL = jsonGoodsList.getJSONObject(findkey).getString("url_title");
//			res.shopPrice = jsonGoodsList.getJSONObject(findkey).getString("shop_price");
//			res.sku = jsonGoodsList.getJSONObject(findkey).getString("goods_sn");
//			res.stockNum = str2intpositive(jsonGoodsList.getJSONObject(findkey).getString("goods_number"));
//			res.type = typeProduct;
//		} else {
//			res = null;
//		}
//
//		return res;
//	}
//
//	/**
//	 * 通过索引获取指定类型的商品,只获取第一页的
//	 * @param DomainName 域名
//	 * @param typeProduct 产品类型，1-普通商品，2-促销商品，3-清仓商品
//	 * @param index 返回产品的索引，小于0时按1处理，大于当前商品数量时，返回空
//	 * @param freeShippingFlag 免邮标志，true-免邮，false-不免邮
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-25  更新时间：2016-10-25
//	 * 备注：这个方法是不会判断库存数量的
//	 */
//	public static Product GetProductByIndex(String DomainName, int typeProduct, int index, boolean freeShippingFlag) throws Exception {
//		if (index <= 0) {
//			index = 1;
//		}
//		Product res = new Product();
//		JSONObject jsonData = loopGetProductList(DomainName, typeProduct, 1, 3, freeShippingFlag);
//		JSONObject jsonGoodsList = jsonData.getJSONObject("goods_list");
//		Iterator it = jsonGoodsList.keys();
//
//		String findkey = "";
//		int tmpidx = 0;
//		while (it.hasNext()) {
//			String key = it.next().toString();
//			if (hasLength(key)) {
//				tmpidx++;
//				if (index == tmpidx) {
//					findkey = key;
//					break;
//				}
//			}
//		}
//
//		if (hasLength(findkey)) {
//			res.id = findkey;
//			res.URL = jsonGoodsList.getJSONObject(findkey).getString("url_title");
//			res.shopPrice = jsonGoodsList.getJSONObject(findkey).getString("shop_price");
//			res.sku = jsonGoodsList.getJSONObject(findkey).getString("goods_sn");
//			res.stockNum = str2intpositive(jsonGoodsList.getJSONObject(findkey).getString("goods_number"));
//			res.type = typeProduct;
//		} else {
//			Log.logWarn("获取商品数据失败,中止测试.");
//			Assert.fail();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 接口：获取商品的详细信息，数据结果是一个JSONObject
//	 * @param DomainName 域名
//	 * @param productSku 商品的sku
//	 * @author yuyang ,创建时间：2016-10-25,更新时间：2016-10-25
//	 */
//	public static JSONObject IF_GetProductInfo(String DomainName, String productSku) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=get_goods_info&goods_sn=" + productSku + "&email=532688164@qq.com";
//
//		JSONObject res = JSONObject.fromObject(get(URLCommand).get("Response"));
//		if (!"200".equals(res.get("_resultcode").toString())) {
//			throw new Exception("Get product(" + productSku + ") info failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
//		}
//		return res;
//	}
//
//	/**
//	 * 获取商品的详细信息，如果获取失败，则重试
//	 * @param DomainName 域名
//	 * @param productSku 商品的sku
//	 * @param loopTimes 尝试删除的次数
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-25  更新时间：2016-10-25
//	 */
//	public static JSONObject loopGetProductInfo(String DomainName, String productSku, int loopTimes) throws Exception {
//		boolean eflag = true;
//
//		JSONObject res = null;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试获取商品(" + productSku + ")的详细信息.");
//			}
//
//			try {
//				res = IF_GetProductInfo(DomainName, productSku);
//				if (i > 1) {
//					Log.logWarn(i + " times,获取商品(" + productSku + ")的详细信息成功.");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,获取商品(" + productSku + ")的详细信息失败.");
//			}
//		}
//
//		if (res == null) {
//			Log.logWarn("连续 " + loopTimes + " times,获取商品(" + productSku + ")的详细信息失败.");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 获取商品详细数据，从中得到商品的阶梯价格，返回商品
//	 * @param DomainName 域名
//	 * @param productSku 商品的sku
//	 * @param product 商品数据
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang 创建时间：2016-10-25  更新时间：2016-10-25
//	 */
//	public static Product GetProductStepPrice(String DomainName, String productSku, Product product) throws Exception {
//		JSONObject jsonData = loopGetProductInfo(DomainName, productSku, 3);
//		JSONArray arrayProdcutStepPrice = jsonData.getJSONArray("volume_price_list");
//
//		product.stepNum = arrayProdcutStepPrice.size();
//		for (int i = 0; i < product.stepNum; i++) {
//			product.stepName.add("priceSection" + i);
//		}
//		for (int i = 0; i < product.stepNum; i++) {
//			JSONObject jsonProdcutStepPrice = (JSONObject) arrayProdcutStepPrice.get(i);
//			String stepNum = jsonProdcutStepPrice.getString("number");
//			String stepPrice = jsonProdcutStepPrice.getString("format_price");
//			product.stepPriceMap.put(product.stepName.get(i), stepPrice);
//
//			List<String> numlist = new ArrayList<String>();
//			String[] numarray = stepNum.split("-| ");
//			for (int j = 0; j < numarray.length; j++) {
//				if (hasLength(numarray[j]) && !numarray[j].contains("max")) {
//					numlist.add(numarray[j]);
//				}
//			}
//			if (numlist.size() == 1) {
//				numlist.add(numlist.get(0));
//			}
//			product.stepSectionMap.put(product.stepName.get(i), numlist);
//		}
//
//		return product;
//	}
//
//	/**
//	 * 接口：获取用户的订单信息，主要包括订单状态和编号
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param start_time 开始的时间戳
//	 * @param end_time 结束的时间戳
//	 * @author yuyang ,创建时间：2016-10-26,更新时间：2016-10-26
//	 */
//	public static JSONObject IF_GetOrderInfo(String DomainName, String UserEmailAccount, long start_time, long end_time) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=query_order&email=" + UserEmailAccount + "&start_time=" + start_time
//				+ "&end_time=" + end_time;
////		System.out.println(URLCommand);
//		JSONObject res = JSONObject.fromObject(get(URLCommand).get("Response"));
//		String rescode = res.get("_resultcode").toString();
//		if (!"200".equals(rescode) && !"1001".equals(rescode)) {
//			throw new Exception(
//					"Get recent order info of user(" + UserEmailAccount + ") failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
//		}
//		return res;
//	}
//
//	/**
//	 * 获取用户的订单信息，如果获取失败，则重试
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param start_time 开始的时间戳
//	 * @param end_time 结束的时间戳
//	 * @param loopTimes 重试次数
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 * @throws RetryException 
//	 */
//	public static JSONObject loopGetOrderInfo(String DomainName, String UserEmailAccount, long start_time, long end_time, int loopTimes)
//			throws Exception {
//		boolean eflag = true;
//
//		JSONObject res = null;
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试获取用户(" + UserEmailAccount + ")的订单信息.");
//			}
//
//			try {
//				res = IF_GetOrderInfo(DomainName, UserEmailAccount, start_time, end_time);
//				if (i > 1) {
//					Log.logWarn(i + " times,获取用户(" + UserEmailAccount + ")的订单信息成功.");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,获取用户(" + UserEmailAccount + ")的订单信息失败.");
//			}
//		}
//
//		if (res == null) {
//			Log.logWarn("连续 " + loopTimes + " times,获取用户(" + UserEmailAccount + ")的订单信息失败.");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 将正整数字符串转换为正整数，如果字符串为非数字，则返回-1
//	 * @param str 待转换的数字字符串，或非数字字符串
//	 * @return 转换后的数字，或-1，-1表示抛出异常
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 */
//	public static int str2intpositive(String str) {
//		int res = -1;
//
//		try {
//			res = Integer.valueOf(str);
//		} catch (NumberFormatException e) {
////			e.printStackTrace();
//		}
//		return res;
//	}
//
//	/**
//	 * 获取商品详细数据，从中得到商品的阶梯价格，返回商品
//	 * @param DomainName 域名
//	 * @param UserEmailAccount 用户的邮箱帐户
//	 * @param start_time 开始的时间戳
//	 * @param end_time 结束的时间戳
//	 * @throws Exception 
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 */
//	public static List<String> GetOrderNumberArray(String DomainName, String UserEmailAccount, long start_time, long end_time) throws Exception {
//		List<String> OrderNumberArray = new ArrayList<String>();
//		JSONObject jsonData = loopGetOrderInfo(DomainName, UserEmailAccount, start_time, end_time, 3);
//
//		Iterator it = jsonData.keys();
//		while (it.hasNext()) {
//			String key = it.next().toString();
//			if (str2intpositive(key) >= 0) {
//				JSONObject jsonOrder = jsonData.getJSONObject(key);
//				OrderNumberArray.add(jsonOrder.getString("order_sn"));
//			}
//		}
//
//		return OrderNumberArray;
//	}
//
//	/**
//	 * 接口：获取服务器时间
//	 * @param DomainName 域名
//	 * @author yuyang ,创建时间：2016-10-26,更新时间：2016-10-26
//	 */
//	public static String IF_GetServerTime(String DomainName) throws Exception {
//		String URLCommand = "http://" + DomainName + "/auto/api.php?act=get_unix_time&mod=common";
//
//		JSONObject res = JSONObject.fromObject(get(URLCommand).get("Response"));
//		String rescode = res.get("_resultcode").toString();
//		if (!"200".equals(rescode)) {
//			throw new Exception("Get unix time failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
//		}
//
//		String restime = res.get("unixtime").toString();
//		return restime;
//	}
//
//	/**
//	 * 获取服务器时间，如果获取失败，则重试
//	 * @param DomainName 域名
//	 * @param loopTimes 重试次数
//	 * @author yuyang 创建时间：2016-10-26  更新时间：2016-10-26
//	 * @throws Exception 
//	 */
//	public static String loopGetServerTime(String DomainName, int loopTimes) throws Exception {
//		boolean eflag = true;
//
//		String res = "";
//		for (int i = 1; i <= loopTimes; i++) {
//			if (i > 1) {
//				Log.logWarn(i + " times,尝试获取服务器时间(" + DomainName + ").");
//			}
//
//			try {
//				res = IF_GetServerTime(DomainName);
//				if (i > 1) {
//					Log.logWarn(i + " times,获取服务器时间(" + DomainName + ")成功.");
//				}
//				break;
//			} catch (Exception e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(i + " times,获取服务器时间(" + DomainName + ")失败.");
//			}
//		}
//
//		if (!hasLength(res)) {
//			Log.logWarn("连续 " + loopTimes + " times,获取服务器时间(" + DomainName + ")失败.");
//			throw new RetryException();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 单击页面元素，单击后在一定时间内查找新的页面元素，如果查找到则返回，如果查找不到，则尝试查找第二个新的页面元素，如果查找到则返回，否则尝试再次单击，如果多次重试后仍然无法单击并查找到新的页面元素们，则抛出自定义异常
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param clickStr 用来作单击动作的元素
//	 * @param FindStr 用来查找的新页面元素
//	 * @param FindStr2  未查找到FindStr时，用来查找的新页面元素
//	 * @param loopTimes 尝试单击的次数
//	 * @param explicitWaitTimeout 显示等待元素的超时时间
//	 * @throws Exception 除超时查找元素的异常之外的其它异常抛出
//	 * @author yuyang ,创建时间：2016-10-27,更新时间：2016-10-27
//	 */
//	public static void loopClick2(WebDriver driver, Locator locator, String clickStr, String FindStr, String FindStr2, int loopTimes,
//			long explicitWaitTimeout) throws Exception {
//		boolean res = false;
//		int clickTimes;
//		WebElement clickElement = null;
//		WebElement findElement = null;
//		WebElement findElement2 = null;
//		boolean eflag = true;
//		boolean e1flag = true;
//		boolean findstrflag = true;
//		boolean onceflag = true;
//
//		for (clickTimes = 1; clickTimes <= loopTimes; clickTimes++) {
//			if (clickTimes > 1) {
//				Log.logWarn(clickTimes + " times,尝试单击页面元素(" + clickStr + ").");
//			}
//
//			try {
//				clickElement = null;
//				clickElement = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(clickStr)));
//			} catch (TimeoutException e) {
//				if (eflag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,准备单击页面元素(" + clickStr + "),但未等待到该元素.");
//			}
//			if (clickElement != null) {
//
//				try {
//					clickElement.click();
//					findElement = (new WebDriverWait(driver, explicitWaitTimeout))
//							.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(FindStr)));
//				} catch (TimeoutException e1) {
//					if (e1flag) {
//						e1.printStackTrace();
//						e1flag = false;
//						findstrflag = false;
//					}
//					Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,单击页面元素后,未等待到新的页面元素(" + FindStr + "),或者单击操作异常.");
//				}
//
//				if (findElement != null) {
//					if (clickTimes > 1) {
//						Log.logWarn(clickTimes + " times,单击页面元素(" + clickStr + ")后,等待到新的页面元素(" + FindStr + ").");
//					}
//					res = true;
//					break;
//				} else {
//					Log.logWarn(clickTimes + " times,单击页面元素(" + clickStr + ")后,未等待到新的页面元素(" + FindStr + ").");
//				}
//
//				if (!findstrflag && onceflag) {
//					onceflag = false;
//					try {
//						findElement2 = (new WebDriverWait(driver, explicitWaitTimeout))
//								.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(FindStr2)));
//						Log.logWarn(clickTimes + " times,单击页面元素(" + clickStr + ")后,未等待到新的页面元素,但等待到了新的页面元素2(" + FindStr2 + "),测试中止.");
//						Assert.fail();
//					} catch (TimeoutException e2) {
////							e2.printStackTrace();
//						Log.logWarn(clickTimes + " times," + explicitWaitTimeout + "秒超时,单击页面元素后,也未等待到新的页面元素2(" + FindStr2 + "),或者单击操作异常.");
//					}
//				}
//			}
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopClick(" + clickStr + ")_" + clickTimes + "times");
//		}
//
//		if (!res) {
//			Log.logWarn("连续 " + loopTimes + " times,单击页面元素(" + clickStr + "),未按预期执行.");
//			throw new RetryException();
//		}
//	}
//
//	/**
//	 * 获取当前URL，是否包含指定字符串，如果包含，则返回true
//	 * @param driver 浏览器driver
//	 * @param URLStr URL地址字符串
//	 * @param Timeout 超时时间，单位为秒
//	 * @throws Exception 除超时查找元素的异常之外的其它异常抛出
//	 * @author yuyang ,创建时间：2016-11-02,更新时间：2016-11-02
//	 */
//	public static boolean CheckURL(WebDriver driver, String URLStr, long Timeout, int mode) throws Exception {
//		boolean res = false;
//		long tmpTimeout = Timeout * 1000;
//
//		String currentURL;
//		long currentTime;
//		long startTime = System.currentTimeMillis();
//		while (true) {
//			currentTime = System.currentTimeMillis();
//			if (currentTime - startTime < 0) {
//				currentTime += 86400;
//			}
//			if (currentTime - startTime >= tmpTimeout) {
//				break;
//			}
//			Thread.sleep(200);
//
//			currentURL = driver.getCurrentUrl();
//			if (mode == 1) {
//				if (currentURL.contains(URLStr)) {
//					res = true;
//					break;
//				}
//			} else if (mode == 2) {
//				if (currentURL.equals(URLStr)) {
//					res = true;
//					break;
//				}
//			}
//		}
//
//		return res;
//	}
//
//	/**
//	 * 检查页面元素是否可见，则返回；如果不是，则循环检查，直到超时
//	 * @param driver 浏览器driver
//	 * @param locator 当前测试类的locator
//	 * @param elementStr 待检查的元素
//	 * @param Timeout 超时时间，单位为秒
//	 * @param explicitWaitTimeout 显示等待元素的超时时间
//	 * @param printFlag 是否打印，第一次查找到元素失败的标志，true-打印，false-不打印
//	 * @throws Exception 所有异常抛出
//	 * @author yuyang ,创建时间：2016-11-8,更新时间：2016-11-8
//	 */
//	public static boolean loopCheckElementVisible(WebDriver driver, Locator locator, String elementStr, long Timeout, long explicitWaitTimeout,
//			boolean printFlag) throws Exception {
//		boolean res = false;
//		long tmpTimeout = Timeout * 1000;
//		boolean diplayFlag = false;
//		boolean enableFlag = false;
//		boolean eflag = true;
//
//		long currentTime;
//		long startTime = System.currentTimeMillis();
//		while (true) {
//			currentTime = System.currentTimeMillis();
//			if (currentTime - startTime < 0) {
//				currentTime += 86400;
//			}
//			if (currentTime - startTime >= tmpTimeout) {
//				break;
//			}
//			Thread.sleep(200);
//
//			try {
//				diplayFlag = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(elementStr))).isDisplayed();
//				enableFlag = (new WebDriverWait(driver, explicitWaitTimeout))
//						.until(ExpectedConditions.presenceOfElementLocated(locator.getBy(elementStr))).isEnabled();
//				if (diplayFlag && enableFlag) {
//					res = true;
//					break;
//				}
//			} catch (Exception e) {
//				if (eflag && printFlag) {
//					e.printStackTrace();
//					eflag = false;
//				}
//				Log.logWarn("操作页面元素(" + elementStr + ")时发生异常(" + GetMethodName() + ").");
//			}
//		}
//
//		if (!res) {
//			Startbrowser.captureScreenAll(Startbrowser.projectName, "loopCheckElementVisible(" + elementStr + ")_" + Timeout + "seconds");
//			Log.logWarn(Timeout + "seconds超时 ,失败检查页面元素(" + elementStr + ")是否可见,期望值为(true,true),当前值为(" + diplayFlag + "," + enableFlag + ").");
////			throw new RetryException();
//		}
//		return res;
//	}
//
//	/**
//	 * 如果当前为重试执行，则刷新页面
//	 * @param driver 浏览器driver
//	 * @param lastMethodName 上一次执行的方法名
//	 * @param currentMethodName 当前执行的方法名
//	 * @author yuyang ,创建时间：2016-11-12,更新时间：2016-11-12
//	 * 更新说明(2016-11-30)：by yuyang,增加了navigate刷新页面失败时，使用get方法的再打开一次
//	 * 备注：需要传入正确的上一次执行的方法名
//	 */
//	public static void retryRefresh(WebDriver driver, String lastMethodName, String currentMethodName) {
//		try {
//			if (RetryFlag(lastMethodName, currentMethodName)) {
//				String currentURL = driver.getCurrentUrl();
//				Log.logInfo("Refresh web page(" + currentURL + ")");
//				try {
//					driver.navigate().refresh();
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//					Log.logWarn("重试时刷新当前页面失败,接下来使用get方法重新获取当前页面.");
//				}
//				try {
//					driver.get(currentURL);
//				} catch (Exception e) {
//					e.printStackTrace();
//					Log.logWarn("重试时get当前页面失败,测试中止.");
//					Assert.fail();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.logWarn("重试时重新获取当前页面失败,测试中止.");
//			Assert.fail();
//		}
//	}
//
//	/**
//	 * 获取当前测试方法的重试标志
//	 * @param lastMethodName 上一次执行的方法名
//	 * @param currentMethodName 当前执行的方法名
//	 * @author yuyang ,创建时间：2016-11-12,更新时间：2016-11-12
//	 * 备注：需要传入正确的上一次执行的方法名
//	 */
//	public static boolean RetryFlag(String lastMethodName, String currentMethodName) {
//		boolean res = false;
//
//		try {
//			if (currentMethodName.equals(lastMethodName)) {
//				res = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return res;
//	}
//
//	/**
//	 * 获取当前页面的URL
//	 * @param driver WebDriver
//	 * @author yuyang ,创建时间：2016-11-23,更新时间：2016-11-23
//	 * 备注：主要为了规避UnhandledAlertException弹窗异常，才封装了这个方法
//	 */
//	public static String MyGetCurrentUrl(WebDriver driver) {
//		String currentURL = "";
//
//		int looptimes = 0;
//		while (true) {
//			try {
//				looptimes++;
//				Thread.sleep(200);
//				try { // for alert window
//					currentURL = driver.getCurrentUrl();
//				} catch (UnhandledAlertException f) {
//					try {
//						Alert alert = driver.switchTo().alert();
//						String alertText = alert.getText();
//						Log.logInfo(looptimes + " times,Alert data: " + alertText);
//						alert.accept();
//					} catch (NoAlertPresentException e) {
//						e.printStackTrace();
//					}
//				}
//				if (hasLength(currentURL)) {
//					break;
//				}
//				Log.logWarn(looptimes + " times,失败获取current URL(" + GetMethodName() + ").");
//				if (looptimes >= 10) {
//					break;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.logWarn("获取current URL时发生异常(" + GetMethodName() + ").");
//			}
//		}
//		return currentURL;
//	}
//
//	/**
//	 * 获取当前页面的URL
//	 * @param driver AndroidDriver
//	 * @author yuyang ,创建时间：2016-11-23,更新时间：2016-11-23
//	 * 备注：从MyGetCurrentUrl(WebDriver driver)修改而来
//	 */
//	public static String MyGetCurrentUrl(AndroidDriver driver) {
//		String currentURL = "";
//
//		int looptimes = 0;
//		while (true) {
//			try {
//				looptimes++;
//				Thread.sleep(200);
//				try { // for alert window
//					currentURL = driver.getCurrentUrl();
//				} catch (UnhandledAlertException f) {
//					try {
//						Alert alert = driver.switchTo().alert();
//						String alertText = alert.getText();
//						Log.logInfo(looptimes + " times,Alert data: " + alertText);
//						alert.accept();
//					} catch (NoAlertPresentException e) {
//						e.printStackTrace();
//					}
//				}
//				if (hasLength(currentURL)) {
//					break;
//				}
//				Log.logWarn(looptimes + " times,失败获取current URL(" + GetMethodName() + ").");
//				if (looptimes >= 10) {
//					break;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.logWarn("获取current URL时发生异常(" + GetMethodName() + ").");
//			}
//		}
//		return currentURL;
//	}
//
//	/**
//	 * 获取商品详细数据，从中得到商品的各仓库的库存
//	 * @param DomainName 域名
//	 * @param productSku 商品的sku
//	 * @param product 商品数据
//	 * @throws Exception 抛出全部异常
//	 * @author yuyang 创建时间：2016-12-17  更新时间：2016-12-17
//	 */
//	public static Product GetProductWid(String DomainName, String productSku, Product product) throws Exception {
//		JSONObject jsonData = loopGetProductInfo(DomainName, productSku, 3);
//
//		Iterator it = jsonData.keys();
//		JSONObject jsonGoods;
//		while (it.hasNext()) {
//			String key = it.next().toString();
//			try {
//				String wid;
//				String goods_number;
//				jsonGoods = jsonData.getJSONObject(key);
//				wid = jsonGoods.getString("wid");
//				goods_number = jsonGoods.getString("goods_number");
//				product.widMap.put(wid, goods_number);
//			} catch (JSONException e) {
////				e.printStackTrace();
//			}
//
//		}
//		
//		for (String wid : product.widMap.keySet()) 
//		{ 
//			if(Integer.valueOf(product.widMap.get(wid)) >=2){
//				product.widMapValid.put(wid, product.widMap.get(wid));
//			}
//		}
//		
//		return product;
//	}
//
//	public static void testcookie(WebDriver driver) throws Exception {
//		Cookie cookie = new Cookie("java", "eclipse", "/", null);
//		driver.manage().addCookie(cookie);
//
//		// 输出现有cookie
//		Set<Cookie> cks0 = driver.manage().getCookies();
//		System.out.println("cookie总数为" + cks0.size());
//		for (Cookie ck : cks0)
//			System.out.println(
//					"作用域：" + ck.getDomain() + ", 名称：" + ck.getName() + ", 值：" + ck.getValue() + ", 范围：" + ck.getPath() + ", 过期时间" + ck.getExpiry());
//		System.out.println("/////////////////////////////////////////");
//
//		// 删除名称为value的cookie
//		driver.manage().deleteCookieNamed("value");
//		// 删除刚才新增的cookie java
//		driver.manage().deleteCookie(cookie);
//
//		// 输出现有cookie
//		Set<Cookie> cks = driver.manage().getCookies();
//		System.out.println("cookie总数为" + cks.size());
//		for (Cookie ck : cks)
//			System.out.println(
//					"作用域：" + ck.getDomain() + ", 名称：" + ck.getName() + ", 值：" + ck.getValue() + ", 范围：" + ck.getPath() + ", 过期时间" + ck.getExpiry());
//
//		System.out.println("/////////////////////////////////////////");
//		// 删除全部cookie
//		driver.manage().deleteAllCookies();
//		Set<Cookie> c = driver.manage().getCookies();
//		System.out.println("cookie总数为" + c.size());
//		for (Cookie ck : c)
//			System.out.println(
//					"作用域：" + ck.getDomain() + ", 名称：" + ck.getName() + ", 值：" + ck.getValue() + ", 范围：" + ck.getPath() + ", 过期时间" + ck.getExpiry());
//	}
//
//	public static void main(String[] args) throws Exception {
//
//		String DomainName = "sammydress.com.a.s1.egomsl.com";
////		IF_ClearProducts(DomainName,"Cart", "yuyang01@globalegrow.com");
////		loopClearProducts(DomainName,"Cart", "yuyang01@globalegrow.com",  3);
////		IF_ClearProducts(DomainName,"Collect", "yuyang01@globalegrow.com", );
////		loopClearProducts(DomainName,"Collect", "yuyang01@globalegrow.com",  3);
////		IF_ClearProducts(DomainName,"SaveForLater", "yuyang01@globalegrow.com", );
////		loopClearProducts(DomainName,"SaveForLater", "yuyang01@globalegrow.com", 3);
//
//	}
////
//}
//
//class RetryException extends Exception {
//	private static final long serialVersionUID = 1L;
//
//	public RetryException() {
//		Log.logInfo("Exception occured while retrying.");
//	}
//}
