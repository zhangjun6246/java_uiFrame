package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.htmlparser.util.ParserException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.globalegrow.base.StartApp;
import com.globalegrow.base.StartPhoneBrowser;
import com.globalegrow.base.Startbrowser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Pub {
	static String projectPath = System.getProperty("user.dir");
	public static final int[] successCode = { 200, 201, 202 }; // 请求成功返回码

	public static Cookie staging = null;
	public static Cookie ORIGINDC = null;

	/**
	 * 获取方法名
	 * 
	 * @return 方法名
	 */
	public static String GetMethodName() {
		String methodnamestr = Thread.currentThread().getStackTrace()[2].getMethodName();
		return methodnamestr;
	}

	/**
	 * 生成指定长度的随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return 生成的随机字符串
	 * @author yuyang 创建时间:2016-08-29更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer randomstr = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(62);// [0,62)
			randomstr.append(str.charAt(number));
		}
		return randomstr.toString();
	}

	/**
	 * 检查字符串是否有长度
	 * 
	 * @param str
	 *            待检查长度的字符串
	 * @return true或false,表示字符串变量中是否包含非空的字符
	 * @author yuyang 创建时间:2016-08-31 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static boolean hasLength(String str) {
		return str != null && !"".equals(str.trim());
	}

	/**
	 * 读取excle文件，返回map对象
	 * 
	 * @author linchaojiang 创建时间:2016-08-30 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Map<String, List> readTestCase(String className, String projectName, String xlsFileName) {

		Map<String, List> map = new HashMap<String, List>();

		List<String[]> list = new ArrayList<String[]>();

		Workbook rwb = null;
		Cell cell = null;

		InputStream stream;
		try {
			stream = new FileInputStream(projectPath + "/testCase/" + projectName + "/" + xlsFileName + ".xls");
			rwb = Workbook.getWorkbook(stream);
		} catch (FileNotFoundException e) {
			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
			e.printStackTrace();
		} catch (BiffException e) {
			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
			e.printStackTrace();
		} catch (IOException e) {
			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
			e.printStackTrace();
		}

		Sheet sheet = rwb.getSheet(className);
		int rows = sheet.getRows();
		int coumn = sheet.getColumns();

		String[] strkey = new String[rows - 1];// 存取testCase的值

		for (int i = 1; i < rows; i++) {
			String[] strValue = new String[coumn - 1];// 存取每一行的数据
			strkey[i - 1] = sheet.getCell(0, i).getContents();
			for (int j = 1; j < coumn; j++) {
				strValue[j - 1] = sheet.getCell(j, i).getContents();
			}
			list.add(strValue);
		}

		// 把行的数据加入map中
		for (int i = 0; i < strkey.length; i++) {
			map.put(strkey[i], Arrays.asList(list.get(i)));
		}

		return map;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @return Result 所代表远程资源的响应,头信息
	 * @author linchaojiang 创建时间：2016-09-01 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 * 
	 */
	public static Map<String, String> get(String url) {
		int defaultConnectTimeOut = 30000; // 默认连接超时,毫秒
		int defaultReadTimeOut = 30000; // 默认读取超时,毫秒

		Map<String, String> result = new HashMap<String, String>();
		BufferedReader in = null;

		try {
			Log.logInfo("通过java请求访问：[" + url + "]");
			// 打开和URL之间的连接
			URLConnection connection = new URL(url).openConnection();
			// 此处的URLConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection
			// 故此处最好将其转化为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API.
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			httpURLConnection.setConnectTimeout(defaultConnectTimeOut);
			httpURLConnection.setReadTimeout(defaultReadTimeOut);
			if (staging != null) {
				httpURLConnection.setRequestProperty("Cookie", staging.toString());
			}
			if (ORIGINDC != null) {
				httpURLConnection.setRequestProperty("Cookie", ORIGINDC.toString());
				ORIGINDC = null;
			}

			// // Fidder监听请求
			// if ((!proxyHost.equals("") && !proxyPort.equals(""))) {
			// System.setProperty("http.proxyHost", proxyHost);
			// System.setProperty("http.proxyPort", proxyPort);
			// }

			// 建立连接
			httpURLConnection.connect();
			result = getResponse(httpURLConnection, in, result);

		} catch (Exception requestException) {
			System.err.println("发送GET请求出现异常!" + requestException);
			// requestException.printStackTrace();
		}
		// 关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception closeException) {
				closeException.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 根据返回码处理返回值
	 * 
	 * @param httpURLConnection
	 * @param in
	 * @param result
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @author linchaojiang 创建时间:xxxx-xx-xx 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static Map<String, String> getResponse(HttpURLConnection httpURLConnection, BufferedReader in,
			Map<String, String> result) throws UnsupportedEncodingException, IOException {
		int contentLengthAllow = -1; // 返回报文长度限制, 为-1时不限制长度

		boolean flag = false;
		for (int i = 0; i < successCode.length; i++) {
			if (successCode[i] == httpURLConnection.getResponseCode()) {
				flag = true;
				break;
			}
		}

		// 返回码非“successCode”时，response为返回message
		if (flag) {
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
			String line;

			// 获取所有响应头字段
			Map<String, List<String>> Hearder = httpURLConnection.getHeaderFields();
			for (String key : Hearder.keySet()) {
				result.put(key, Hearder.get(key).toString());
			}

			// responseList.clear();
			String responseStr = "";
			while ((line = in.readLine()) != null) {
				responseStr += line;
			}

			// Content长度限制
			if (responseStr.length() > contentLengthAllow && contentLengthAllow > 0) {
				responseStr = responseStr.substring(0, contentLengthAllow);
			}

			result.put("Message", httpURLConnection.getResponseMessage());
			result.put("Code", String.valueOf(httpURLConnection.getResponseCode()));
			result.put("Response", responseStr);
		} else {
			result.put("Message", httpURLConnection.getResponseMessage());
			result.put("Code", String.valueOf(httpURLConnection.getResponseCode()));
			//
			result.put("Response", httpURLConnection.getResponseMessage());
			// 获取所有响应头字段
			Map<String, List<String>> Hearder = httpURLConnection.getHeaderFields();
			for (String key : Hearder.keySet()) {
				result.put(key, Hearder.get(key).toString());
			}
		}
		return result;
	}

	/**
	 * 将16位byte[] 转换为32位String,用于GetMD5String()
	 * 
	 * @param buffer
	 *            16位的byte[]
	 * @return 32位的字符串
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	private static String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}

		return sb.toString();
	}

	/**
	 * 给字符串生成MD5码
	 * 
	 * @param str,输入字符串
	 * @return res,生成的MD5码字符串
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String GetMD5String(String str) {
		String res = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes());
			res = toHex(messageDigest.digest());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 接口：删除用户的邮箱帐户。如果刪除失败，会抛出异常
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static void IF_DelUserAccount(String DomainName, String UserEmailAccount) throws Exception {

		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?act=del&email=" + UserEmailAccount + "&salt="
					+ GetMD5String(UserEmailAccount);
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?act=del&email=" + UserEmailAccount + "&salt="
					+ GetMD5String(UserEmailAccount);
		}
		Map<String, String> res = json2map(get(URLCommand).get("Response"));
		if (!"1".equals(res.get("status"))) {
			throw new Exception(
					"Delete user account(" + UserEmailAccount + ") failed!(" + res + ")(" + DomainName + ")");
		}
		Log.logInfo("删除邮箱帐户(" + UserEmailAccount + "),域名(" + DomainName + ").");
	}

	/**
	 * 接口：删除用户的邮箱帐户。如果刪除失败，会抛出异常
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @throws Exception
	 *             抛出全部异常
	 * @author zhangjun 创建时间：2016-12-05 更新时间:2016-12-13 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub 更新说明(2016-12-13)：by
	 *         yuyang,增加校验_resultcode以及抛出异常的步骤
	 */
	public static void IF_DelUserAccount2(String DomainName, String UserEmailAccount) throws Exception {

		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?mod=user&act=del_auto_account&email="
					+ UserEmailAccount;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=del_auto_account&email="
					+ UserEmailAccount;
		}
		Map<String, String> res = json2map(get(URLCommand).get("Response"));
		if (!"200".equals(res.get("_resultcode"))) {
			if ("1001".equals(res.get("_resultcode"))) {
				Log.logInfo("邮箱帐户不存在,不需要删除(" + UserEmailAccount + "),域名(" + DomainName + ").");
			} else {
				throw new Exception(
						"Delete user account(" + UserEmailAccount + ") failed!(" + res + ")(" + DomainName + ")");
			}
		} else {
			Log.logInfo("删除邮箱帐户(" + UserEmailAccount + "),域名(" + DomainName + ").");
		}
	}

	/**
	 * 删除己注册的邮箱帐户，如果删除后帐户仍然是己注册状态，则重试;如果删除前，查询到用户是未注册状态，则不执行删除动作
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param loopTimes
	 *            尝试删除的次数
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang 创建时间：2016-10-06 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static void loopDelUserAccount(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
		boolean eflag = true;
		int ret = loopQueryUserRegisterStatus(DomainName, UserEmailAccount, 3);
		if (ret == 1) {

			boolean res = false;
			for (int i = 1; i <= loopTimes; i++) {
				if (i > 1) {
					Log.logWarn(i + " times,尝试删除邮箱帐户(" + UserEmailAccount + ").");
				}

				try {
					IF_DelUserAccount(DomainName, UserEmailAccount);
					ret = loopQueryUserRegisterStatus(DomainName, UserEmailAccount, 3);
					if (ret == 1) {
						Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
					} else if (ret == 0) {
						if (i > 1) {
							Log.logWarn(i + " times,删除邮箱帐户成功(" + UserEmailAccount + ").");
						}
						res = true;
						break;
					}
				} catch (Exception e) {
					if (eflag) {
						e.printStackTrace();
						eflag = false;
					}
					Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
				}
			}

			if (!res) {
				Log.logWarn("连续 " + loopTimes + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
				throw new Exception();
			}

		} else if (ret == 0) {
			Log.logInfo("Test account is not registered(" + UserEmailAccount + "),不需要执行删除操作.");
		}
	}

	/**
	 * 删除己注册的邮箱帐户，如果删除后帐户仍然是己注册状态，则重试;如果删除前，查询到用户是未注册状态，则不执行删除动作
	 * 备注：与loopDelUserAccount的区别是，调用了统一参数标准的新接口
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param loopTimes
	 *            尝试删除的次数
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang 创建时间：2016-12-13 更新时间:2016-12-13
	 */
	public static void loopDelUserAccount2(String DomainName, String UserEmailAccount, int loopTimes) throws Exception {
		boolean eflag = true;
		int ret = loopQueryUserRegisterStatus2(DomainName, UserEmailAccount, 3);
		if (ret == 1) {

			boolean res = false;
			for (int i = 1; i <= loopTimes; i++) {
				if (i > 1) {
					Log.logWarn(i + " times,尝试删除邮箱帐户(" + UserEmailAccount + ").");
				}

				try {
					IF_DelUserAccount2(DomainName, UserEmailAccount);
					ret = loopQueryUserRegisterStatus2(DomainName, UserEmailAccount, 3);
					if (ret == 1) {
						Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
					} else if (ret == 0) {
						if (i > 1) {
							Log.logWarn(i + " times,删除邮箱帐户成功(" + UserEmailAccount + ").");
						}
						res = true;
						break;
					}
				} catch (Exception e) {
					if (eflag) {
						e.printStackTrace();
						eflag = false;
					}
					Log.logWarn(i + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
				}
			}

			if (!res) {
				Log.logWarn("连续 " + loopTimes + " times,删除邮箱帐户失败(" + UserEmailAccount + ").");
				throw new Exception();
			}

		} else if (ret == 0) {
			Log.logInfo("Test account is not registered(" + UserEmailAccount + "),不需要执行删除操作.");
		}
	}

	/**
	 * 接口：查询用户邮箱帐号是否己注册
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @return 1-用户邮箱帐号己注册;0-未注册
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static int IF_QueryUserRegisterStatus(String DomainName, String UserEmailAccount) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?act=check_email&email=" + UserEmailAccount;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?act=check_email&email=" + UserEmailAccount;
		}
		int ret = Integer.valueOf((json2map(get(URLCommand).get("Response"))).get("status")).intValue();
		if (ret != 0 && ret != 1)
			throw new Exception("Query user register status failed,The user account(" + UserEmailAccount
					+ ") is abnormal!(" + ret + ")(" + DomainName + ")");
		return ret;
	}

	/**
	 * 查询用户邮箱帐号是否己注册，如果查询失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @param loopTimes
	 *            重试次数
	 * @return 1-用户邮箱帐号己注册;0-未注册
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-26 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static int loopQueryUserRegisterStatus(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		boolean eflag = true;

		int res = -1;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态.");
			}

			try {
				res = IF_QueryUserRegisterStatus(DomainName, UserEmailAccount);
				if (i > 1) {
					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
			}
		}

		if (res == -1) {
			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 接口：查询用户是否己作邮箱验证
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @return 1-邮箱己验证;0-邮箱未验证;-1-邮箱未注册
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static int IF_QueryUserVerifyEmailStatus(String DomainName, String UserEmailAccount) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?act=get_user_info&email=" + UserEmailAccount;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?act=get_user_info&email=" + UserEmailAccount;
		}
		Map<String, String> res = json2map(get(URLCommand).get("Response"));
		if (res != null) {
			int ret = Integer.valueOf(res.get("is_validated")).intValue();
			if (ret != 0 && ret != 1)
				throw new Exception("Query user verify email status failed,The email verification status("
						+ UserEmailAccount + ") is abnormal!(" + ret + ")(" + DomainName + ")");
			return ret;
		} else {
			return -1;
		}
	}

	/**
	 * 查询用户是否己作邮箱验证，如果查询失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @param loopTimes
	 *            重试次数
	 * @return 1-邮箱己验证;0-邮箱未验证;-1-邮箱未注册
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-26 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static int loopQueryUserVerifyEmailStatus(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		boolean eflag = true;

		int res = -2;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态.");
			}

			try {
				res = IF_QueryUserVerifyEmailStatus(DomainName, UserEmailAccount);
				if (i > 1) {
					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
			}
		}

		if (res == -2) {
			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 把json字符串转换成Map
	 * 
	 * @param str_json
	 *            待转换的json字符串
	 * @return 转换的结果Map
	 * @author yuyang 创建时间：2016-09-02 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static Map<String, String> json2map(String str_json) {
		Map<String, String> res = null;
		try {
			Gson gson = new Gson();
			res = gson.fromJson(str_json, new TypeToken<Map<String, String>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			// e.printStackTrace();
		}
		return res;
	}

	/**
	 * 根据tag，匹配并返回全部包含tag的连接
	 * 
	 * @param str_html_content
	 *            html内容
	 * @param tag
	 *            提取link的tag，即匹配字符串，一般填写域名
	 * @return 匹配到的全部link数组
	 * @author yuyang 创建时间：2016-09-03 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static ArrayList<String> getLinkOfTag(String str_html_content, String tag) {
		ArrayList<String> match_links = new ArrayList<String>();
		try {
			ArrayList<String> links = HtmlParse.getAllALink(str_html_content);
			for (String link : links) {
				if (link.contains(tag)) {
					match_links.add(link);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return match_links;
	}

	/**
	 * 根据excel中提供的json数据的key值获取value值
	 * 
	 * @param name
	 *            :key值
	 * @param testCase
	 *            :测试方法名
	 * @return String
	 * @author linchaojiang,创建时间：2016-09-06 更新时间:2016-12-06 更新说明(2016-12-06)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String getJsonData(String name, String testCase) {
		String value = null;
		String json = Startbrowser.testCasemap.get(testCase).get(4).toString();// 读取excel中的数据，并把中文的单引号和冒号转换为英文
		String json2 = json.replace("’", "'").replace("‘", "'").replace("，", ",").replace("：", ":");
		try {
			JSONObject jsonObject = JSONObject.fromObject(json2);
			value = jsonObject.getString(name);
		} catch (JSONException e) {
			Log.logInfo(e.getMessage() + "\n读取excel中的json数据出错!!!");
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 根据excel中提供的json数据的key值获取value值
	 * 
	 * @param name
	 *            :key值
	 * @param testCase
	 *            :测试方法名
	 * @return String
	 * @author linchaojiang,创建时间：2016-01-21 更新时间:2016-01-21
	 */
	public static String getJsonData(String name, String testCase, String type) {

		String value = null;
		String json = "";
		if (type.equals("WEB")) {
			json = Startbrowser.testCasemap.get(testCase).get(4).toString();
		} else if (type.equals("WAP")) {
			json = StartPhoneBrowser.testCasemap.get(testCase).get(4).toString();
		} else if (type.equals("ANDROID")) {
			// json =
			// StartAndroidApp.testCasemap.get(testCase).get(4).toString();
		} else if (type.equals("IOS")) {
			// json = Startbrowser.testCasemap.get(testCase).get(4).toString();
		}
		String json2 = json.replace("’", "'").replace("‘", "'").replace("，", ",").replace("：", ":");
		try {
			JSONObject jsonObject = JSONObject.fromObject(json2);
			value = jsonObject.getString(name);
		} catch (JSONException e) {
			Log.logInfo(e.getMessage() + "\n读取excel中的json数据出错!!!");
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 获取本地的IP和域名
	 * 
	 * @return [] [0]是IP地址，[1]是域名
	 * @author yuyang,创建时间：2016-09-07 更新时间:2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String[] getIPv4() {
		ArrayList<String> ipv4 = new ArrayList<String>();
		ArrayList<String> ipv4host = new ArrayList<String>();
		InetAddress ip;
		String[] ipv4res = new String[2];

		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						ipv4.add(ip.getHostAddress());
						ipv4host.add(ip.getCanonicalHostName());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		int ipv4Size = ipv4.size();
		String[] ipv4Array = (String[]) ipv4.toArray(new String[ipv4Size]);
		String[] ipv4hostArray = (String[]) ipv4host.toArray(new String[ipv4Size]);
		StringBuilder ipv4sb = new StringBuilder(ipv4Size);
		StringBuilder ipv4hostsb = new StringBuilder(ipv4Size);
		for (int i = 0; i < ipv4Size; i++) {
			if (!"127.0.0.1".equals(ipv4Array[i])) {
				ipv4sb.append(ipv4Array[i]);
				ipv4hostsb.append(ipv4hostArray[i]);
				if (i < ipv4Size - 1) {
					ipv4sb.append(", ");
					ipv4hostsb.append(", ");
				}
			}
		}

		ipv4res[0] = ipv4sb.toString();
		ipv4res[1] = ipv4hostsb.toString();
		return ipv4res;
	}

	/**
	 * 将字符串转为boolean,转换的规则是0-false,其它值-true
	 * 
	 * @param str
	 *            待转换的字符串
	 * @return false或true
	 * @author yuyang,创建时间：2016-09-07 更新时间:2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static boolean string2boolean(String str) {
		boolean res = false;
		try {
			res = !(Integer.valueOf(str.trim()).intValue() == 0);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 输出测试时长格式化后的字符串
	 * 
	 * @param Seconds
	 *            运行时期长，单位是秒
	 * @return 测试时长字符串，格式是 天 时 分 秒
	 * @author yuyang,创建时间：2016-09-07 更新时间:2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String getRunTimeStr(int Seconds) {
		String res = null;

		int days = Seconds / 86400;
		int hours = (Seconds % 86400) / 3600;
		int minutes = ((Seconds % 86400) % 3600) / 60;
		int seconds = ((Seconds % 86400) % 3600) % 60;
		try {

			res = ((days > 0) ? Integer.toString(days) + "d " : "")
					+ ((hours > 0) ? Integer.toString(hours) + "h:" : "")
					+ ((minutes > 0) ? Integer.toString(minutes) + "m:" : "")
					+ ((seconds >= 0) ? Integer.toString(seconds) + "s" : "");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 获取测试用例的验证点信息，从xls文件中
	 * 
	 * @param testcasename
	 *            测试用例(或者叫方法)的名称
	 * @return 验证点信息
	 * @author yuyang,创建时间：2016-10-06,更新时间：2016-12-07 更新说明(2016-10-19)：by
	 *         yuyang,增加测试描述信息，验证点和测试描述信息由局部变量改为成员变量 更新说明(2016-11-16)：by
	 *         yuyang,由使用Startbrowser.testCasemap的方式，改为参数传递的方式，参数是testCaseMap
	 *         更新说明(2016-12-07)：by yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings("rawtypes")
	public static void getTestCaseInfo(Map<String, List> testCaseMap, String testcasename, testInfo info,
			boolean startMethodFlag) {
		Startbrowser.testDescription = "";
		Startbrowser.testCheckpoint = "";

		String Description = "";
		try {
			Description = testCaseMap.get(testcasename).get(2).toString();
			int tmpidx = Description.indexOf(".");
			if (tmpidx >= 0) {
				Description = Description.substring(tmpidx + 1);
			}
			if (Description.length() > 0) {
				String lastchar = Description.substring(0, Description.length() - 1);
				if (!lastchar.equals(".")) {
					Description += ".";
				}
			}
			Description = "测试描述:" + Description;
			info.testDescription = Description;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的测试描述信息.");
		}
		String Checkpoint = "";
		try {
			Checkpoint = testCaseMap.get(testcasename).get(3).toString();
			int tmpidx = Checkpoint.indexOf(".");
			if (tmpidx >= 0) {
				Checkpoint = Checkpoint.substring(tmpidx + 1);
			}
			if (Checkpoint.length() > 0) {
				String lastchar = Checkpoint.substring(0, Checkpoint.length() - 1);
				if (!lastchar.equals(".")) {
					Checkpoint += ".";
				}
			}
			Checkpoint = "验证点  :" + Checkpoint;
			info.testCheckpoint = Checkpoint;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的验证点信息.");
		}
		String Correlation = "";
		try {
			Correlation = testCaseMap.get(testcasename).get(5).toString();
			int tmpidx = Correlation.indexOf(".");
			if (tmpidx >= 0) {
				Correlation = Correlation.substring(tmpidx + 1);
			}
			if (Correlation.length() > 0) {
				String lastchar = Correlation.substring(0, Correlation.length() - 1);
				if (!lastchar.equals(".")) {
					Correlation += ".";
				}
			}
			Correlation = "相关性说明 :" + Correlation;
			info.testCorrelation = Correlation;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的相关性说明信息.");
		}

		Startbrowser.testDescription = Description;
		Startbrowser.testCheckpoint = Checkpoint;

		info.testExceptionNote = "执行流程异常" + "(" + info.testCheckpoint + ").";
		info.methodName = testcasename;
		info.methodNameFull = info.moduleName + "." + info.methodName;

		if (startMethodFlag) {
			Log.logInfo("(" + info.methodNameFull + ")...test method start...");
			if (Startbrowser.correFail) {
				Log.logWarn("测试脚本关联失败.");
				Assert.fail();
			}
		}
	}

	/**
	 * 获取测试用例的验证点信息，从xls文件中
	 * 备注：从getTestCaseInfo修改而来，因为Startbrowser有两个文件，为了适应这种情况，使关联标志正确
	 * 
	 * @param testcasename
	 *            测试用例(或者叫方法)的名称
	 * @return 验证点信息
	 * @author yuyang,创建时间：2016-11-23,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings("rawtypes")
	public static void getTestCaseInfoWap(Map<String, List> testCaseMap, String testcasename, testInfo info,
			boolean startMethodFlag) {
		String Description = "";
		try {
			Description = testCaseMap.get(testcasename).get(2).toString();
			int tmpidx = Description.indexOf(".");
			if (tmpidx >= 0) {
				Description = Description.substring(tmpidx + 1);
			}
			if (Description.length() > 0) {
				String lastchar = Description.substring(0, Description.length() - 1);
				if (!lastchar.equals(".")) {
					Description += ".";
				}
			}
			Description = "测试描述:" + Description;
			info.testDescription = Description;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的测试描述信息.");
		}
		String Checkpoint = "";
		try {
			Checkpoint = testCaseMap.get(testcasename).get(3).toString();
			int tmpidx = Checkpoint.indexOf(".");
			if (tmpidx >= 0) {
				Checkpoint = Checkpoint.substring(tmpidx + 1);
			}
			if (Checkpoint.length() > 0) {
				String lastchar = Checkpoint.substring(0, Checkpoint.length() - 1);
				if (!lastchar.equals(".")) {
					Checkpoint += ".";
				}
			}
			Checkpoint = "验证点  :" + Checkpoint;
			info.testCheckpoint = Checkpoint;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的验证点信息.");
		}
		String Correlation = "";
		try {
			Correlation = testCaseMap.get(testcasename).get(5).toString();
			int tmpidx = Correlation.indexOf(".");
			if (tmpidx >= 0) {
				Correlation = Correlation.substring(tmpidx + 1);
			}
			if (Correlation.length() > 0) {
				String lastchar = Correlation.substring(0, Correlation.length() - 1);
				if (!lastchar.equals(".")) {
					Correlation += ".";
				}
			}
			Correlation = "相关性说明 :" + Correlation;
			info.testCorrelation = Correlation;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的相关性说明信息.");
		}

		info.testExceptionNote = "执行流程异常" + "(" + info.testCheckpoint + ").";
		info.methodName = testcasename;
		info.methodNameFull = info.moduleName + "." + info.methodName;

		if (startMethodFlag) {
			Log.logInfo("(" + info.methodNameFull + ")...test method start...");
			if (StartPhoneBrowser.correFail) {
				Log.logWarn("测试脚本关联失败.");
				Assert.fail();
			}
		}
	}

	/**
	 * 获取测试用例的验证点信息，从xls文件中
	 * 备注：从getTestCaseInfo修改而来，因为Startbrowser有三个文件，为了适应这种情况，使关联标志正确
	 * 
	 * @param testcasename
	 *            测试用例(或者叫方法)的名称
	 * @return 验证点信息
	 * @author yuyang 创建时间:2017-03-12 更新时间:2017-03-12
	 */
	@SuppressWarnings("rawtypes")
	public static void getTestCaseInfoAndroidApp(Map<String, List> testCaseMap, String testcasename, testInfo info,
			boolean startMethodFlag) {
		String Description = "";
		try {
			Description = testCaseMap.get(testcasename).get(2).toString();
			int tmpidx = Description.indexOf(".");
			if (tmpidx >= 0) {
				Description = Description.substring(tmpidx + 1);
			}
			if (Description.length() > 0) {
				String lastchar = Description.substring(0, Description.length() - 1);
				if (!lastchar.equals(".")) {
					Description += ".";
				}
			}
			Description = "测试描述:" + Description;
			info.testDescription = Description;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的测试描述信息.");
		}
		String Checkpoint = "";
		try {
			Checkpoint = testCaseMap.get(testcasename).get(3).toString();
			int tmpidx = Checkpoint.indexOf(".");
			if (tmpidx >= 0) {
				Checkpoint = Checkpoint.substring(tmpidx + 1);
			}
			if (Checkpoint.length() > 0) {
				String lastchar = Checkpoint.substring(0, Checkpoint.length() - 1);
				if (!lastchar.equals(".")) {
					Checkpoint += ".";
				}
			}
			Checkpoint = "验证点  :" + Checkpoint;
			info.testCheckpoint = Checkpoint;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的验证点信息.");
		}
		String Correlation = "";
		try {
			Correlation = testCaseMap.get(testcasename).get(5).toString();
			int tmpidx = Correlation.indexOf(".");
			if (tmpidx >= 0) {
				Correlation = Correlation.substring(tmpidx + 1);
			}
			if (Correlation.length() > 0) {
				String lastchar = Correlation.substring(0, Correlation.length() - 1);
				if (!lastchar.equals(".")) {
					Correlation += ".";
				}
			}
			Correlation = "相关性说明 :" + Correlation;
			info.testCorrelation = Correlation;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("未获取到测试用例(" + testcasename + ")的相关性说明信息.");
		}

		info.testExceptionNote = "执行流程异常" + "(" + info.testCheckpoint + ").";
		info.methodName = testcasename;
		info.methodNameFull = info.moduleName + "." + info.methodName;

		if (startMethodFlag) {
			Log.logInfo("(" + info.methodNameFull + ")...test method start...");
			if (StartApp.correFail) {
				Log.logWarn("测试脚本关联失败.");
				Assert.fail();
			}
		}
	}

	/**
	 * 打印当前TestCase的信息
	 * 
	 * @author yuyang,创建时间：2016-10-19,更新时间：2016-12-07
	 *         备注：需要与getTestCaseInfo(testCasemap, methodName)配对使用
	 *         更新说明(2016-11-16)：by
	 *         yuyang,由使用Startbrowser.testDescription的方式，改为参数传递的方式
	 *         更新说明(2016-12-07)：by yuyang,由PublicFun移动到Pub
	 */
	public static void printTestCaseInfo(testInfo info) {
		Log.logInfo(info.testDescription);
		Log.logInfo(info.testCheckpoint);
	}

	/**
	 * 测试方法执行异常时，打印当前TestCase的信息，包括异常提示和相关性说明
	 * 
	 * @author yuyang,创建时间：2016-11-17,更新时间：2016-12-07
	 *         备注：需要与getTestCaseInfo(testCasemap, methodName)配对使用
	 *         更新说明(2016-11-16)：by
	 *         yuyang,由使用Startbrowser.testDescription的方式，改为参数传递的方式
	 *         更新说明(2016-12-07)：by yuyang,由PublicFun移动到Pub
	 */
	public static void printTestCaseExceptionInfo(testInfo info) {
		Log.logWarn(info.testExceptionNote);
		Log.logWarn(info.testCorrelation);
	}

	/**
	 * 延时若干秒
	 * 
	 * @param seconds
	 *            延时秒数,最小为1
	 * @author yuyang,创建时间：2016-10-07,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static void sleep(int seconds) {
		try {
			if (seconds > 0) {
				Thread.sleep(seconds * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("Error happened while delaying for " + seconds + "seconds.");
		}
	}

	/**
	 * 清理价格字符前(和后)的美元字符(或其它货币符号)，获取价格字符串，如果价格前后有空格会一并删除掉
	 * 
	 * @param 包含美元符号或其它符串的价格字符串,如,$2.59
	 * @author yuyang ,创建时间：2016-10-07,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub 更新说明(2017-02-07)：by
	 *         yuyang,增加清理后价格字符串后面的货币符号功能
	 */
	public static String getPriceStr(String priceStr) {
		String res = "";
		char c = 0;
		int len = 0;
		try {
			if (hasLength(priceStr)) {
				priceStr = priceStr.trim();
				for (int i = 0; i < priceStr.length(); i++) {
					c = priceStr.charAt(i);
					if (c >= '0' && c <= '9') {
						res = priceStr.substring(i);
						break;
					}
				}
			}
			if (hasLength(res)) {
				len = res.length();
				for (int i = 0; i < len; i++) {
					c = res.charAt(len - 1 - i);
					if (c >= '0' && c <= '9') {
						res = res.substring(0, len - i);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.logWarn("Error happened while getting price string.");
		}
		return res;
	}

	/**
	 * 删除URL字符串中的全部参数
	 * 
	 * @param URLStr
	 *            URL字符串
	 * @return 删除全部参数后的URL字符串
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String removeURLParameter(String URLStr) throws Exception {
		if (hasLength(URLStr)) {
			URLStr = URLStr.trim();
			int idx = URLStr.indexOf("?");
			if (idx >= 0)
				URLStr = URLStr.substring(0, idx);
		}
		return URLStr;
	}

	/**
	 * 清除URL参数后，再修改URL的后缀为.html，适用于web
	 * 
	 * @param URLStr
	 *            URL字符串
	 * @return 修改后的URL
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang ,创建时间：2016-10-12,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String modifyURLSuffix(String URLStr) throws Exception {
		URLStr = removeURLParameter(URLStr);
		if (hasLength(URLStr)) {
			URLStr = URLStr.trim();
			if (!URLStr.substring(URLStr.length() - 5).equals(".html")) {
				int idx = URLStr.lastIndexOf('.');
				if (idx >= 0)
					URLStr = URLStr.substring(0, idx) + ".html";
			}
		}
		return URLStr;
	}

	/**
	 * 清除URL参数后，再修改URL的后缀为.html，替换商品URL为wap端的URL,适用于wap
	 * 
	 * @param URLStr
	 *            URL字符串
	 * @return 修改后的URL
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang ,创建时间：2016-11-18,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String modifyURLSuffixWap(String DomainName, String URLStr) throws Exception {
		URLStr = removeURLParameter(URLStr);
		URLStr = modifyURLSuffix(URLStr);
		if (hasLength(URLStr)) {
			URLStr = URLStr.trim();
			if (DomainName.contains("a.s1.egomsl.com") || DomainName.contains("trunk.s1.egomsl.com")) {
				URLStr = URLStr.replaceFirst("sammydress", "wap-sammydress");
			} else if (DomainName.equals("www.sammydress.com")) {
				URLStr = URLStr.replaceFirst("www", "m");
			}
		}
		return URLStr;
	}

	/**
	 * 删除URL字符串中的http头
	 * 
	 * @param URLStr
	 *            URL字符串
	 * @return 删除http头后的URL字符串
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang ,创建时间：2016-11-24,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String removeURLHttp(String URLStr) throws Exception {
		if (hasLength(URLStr)) {
			URLStr = URLStr.trim();
			if (URLStr.substring(0, 7).equals("http://")) {
				URLStr = URLStr.substring(7);
			}
			if (URLStr.substring(0, 8).equals("https://")) {
				URLStr = URLStr.substring(8);
			}
		}
		return URLStr;
	}

	/**
	 * 打印断言信息
	 * 
	 * @param assertStr
	 *            断言命令字符串
	 * @param msgType
	 *            断言类型
	 * @param msg
	 *            自定义的信息
	 * @author yuyang ,创建时间：2016-10-13,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static void assertInfo(String assertStr, String msgType, String msg) {
		if (assertStr.equals("assertTrue")) {
			if (msgType.equals("contains")) {
				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 包含.");
			} else if (msgType.equals("greater")) {
				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 大于.");
			} else if (msgType.equals("less")) {
				Log.logInfo(assertStr + ":目标值和期望值之间的关系是 小于.");
			}
		} else if (assertStr.equals("assertEquals")) {
			Log.logInfo(assertStr + ":目标值和期望值之间的关系是 等于.");
		} else if (assertStr.equals("assertNotEquals")) {
			Log.logInfo(assertStr + ":目标值和期望值之间的关系是 不等于.");
		} else {
			Log.logInfo(assertStr + ":" + msg);
		}
	}

	/**
	 * 从产品页面的URL获取产品ID，或者叫商品ID
	 * 
	 * @param URLStr
	 * @return 商品ID
	 * @author yuyang,创建时间：2016-10-14,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String getProductID(String URLStr) {
		String IDStr = "";
		if (URLStr.substring(0, 4).equals("http")) {
			int idx = URLStr.indexOf("product");
			int idx2 = URLStr.lastIndexOf('.');
			if (idx >= 0 && idx2 > idx) {
				IDStr = URLStr.substring(idx + "product".length(), idx2);
			}
		}
		if (!hasLength(IDStr)) {
			Startbrowser.captureScreenAll(Startbrowser.projectName, "getProductID(" + URLStr + ")");
			Log.logWarn("失败获取商品ID(" + URLStr + "),测试中止.");
			Assert.fail();
		}
		return IDStr;
	}

	/**
	 * @测试点: IF_Verification
	 * @验证点:通过cookies 获得验证码
	 * @param driver
	 * @param domainName
	 *            测试路径
	 * @param requestId
	 *            请求的id（每次请求保持唯一性）
	 * @param privatekey
	 *            测试的key，需分区正式和测试
	 * @param ftc
	 *            验证码类型，登录=login，注册=regist，忘记密码ftc=''
	 * @return 测试环境 @备注： String
	 * @author zhangjun,创建时间：2016-12-02,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String IF_Verification(WebDriver driver, String domainName, String requestId, String privatekey,
			String ftc) {
		String getdata = null;
		String getsign;
		String getResponse;
		String getverification;
		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("PHPSESSID")) { // 取出cookie值
				getdata = "a:1:{s:10:\"session_id\";s:26:\"" + cookie.getValue() + "\";}";// 生成虚序列化data
			}
		}

		getsign = privatekey + requestId + getdata;// 获得私钥
		System.out.println(requestId);
		String sign = GetMD5String(getsign);// 生成MD5

		String URLCommand = "";
		if ((!domainName.contains("s1")) || domainName.contains("gearbest.com")) {
			URLCommand = "https://" + domainName + "/auto/api.php?mod=common&act=get_user_sign&requestId=" + requestId
					+ "&sign=" + sign + "&data=" + getdata + "&ftc=" + ftc;
		} else {
			URLCommand = "http://" + domainName + "/auto/api.php?mod=common&act=get_user_sign&requestId=" + requestId
					+ "&sign=" + sign + "&data=" + getdata + "&ftc=" + ftc;
		}

		System.out.println(URLCommand);
		Map<String, String> res = get(URLCommand);
		getResponse = res.get("Response");

		int beginIndex = getResponse.lastIndexOf("s:6");// jsoin解析不出来，暂用字符串形式截取验证码
		getverification = getResponse.substring(beginIndex + 6, beginIndex + 12); // 取得字符串
		System.out.println(getverification);
		return getverification;

	}

	/**
	 * 接口：清空用户购物车(或收藏夹,或save for later)中的商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param Type
	 *            支持4种类型,分别是:Cart,Collect,SaveForLater,Address
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @return 1-清除成功;0-清除失败
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-14,更新时间：2017-03-21 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub 更新说明(2017-03-21)：by yuyang,增加一种类型Address
	 */
	public static int IF_ClearProducts(String DomainName, String Type, String UserEmailAccount) throws Exception {
		String actStr = "";
		if (Type.equals("Cart")) {
			actStr = "clear_cart";
		} else if (Type.equals("Collect")) {
			actStr = "clear_user_collect";
		} else if (Type.equals("SaveForLater")) {
			actStr = "clear_save_for_later";
		} else if (Type.equals("Address")) {
			if (DomainName.contains("gamiss.com")) {
				actStr = "clear_user_address";
			} else {
				actStr = "del_user_address";
			}
		} else {
			Log.logWarn("接口命令无效,中止测试(" + GetMethodName() + ",Type=" + Type + ").");
			Assert.fail();
		}
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?mod=user&act=" + actStr + "&email="
					+ UserEmailAccount;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=" + actStr + "&email=" + UserEmailAccount;
		}

		Map<String, String> res = json2map(get(URLCommand).get("Response"));
		if (res != null) {
			String ret = res.get("_resultcode");
			if (!ret.equals("200") && !Type.equals("Address")) {
				throw new Exception("Clearing " + Type + " for user(" + UserEmailAccount + ") is abnormal!(" + ret
						+ ")(" + DomainName + ")");
			}
			if (ret.equals("1001") && Type.equals("Address")) {
				if (DomainName.contains("gamiss.com")) {
					if (!res.get("_msg").equals("操作成功")) {
						throw new Exception("Clearing " + Type + " for user(" + UserEmailAccount + ") is abnormal!("
								+ ret + ")(" + DomainName + ")");
					}
				} else {
					if (!res.get("_msg").equals("User Address Does Not Exist!")) {
						throw new Exception("Clearing " + Type + " for user(" + UserEmailAccount + ") is abnormal!("
								+ ret + ")(" + DomainName + ")");
					}
				}
			}
			return 1;
		}
		return 0;
	}

	/**
	 * 清空用户购物车(或收藏夹,或save for later)中的商品，如果清除失败，则重试多次
	 * 
	 * @param DomainName
	 *            域名
	 * @param Type
	 *            支持3种类型,分别是:Cart,Collect,SaveForLater,Address
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param loopTimes
	 *            尝试清空的次数
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-14,更新时间：2017-03-21 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub 更新说明(2017-03-21)：by yuyang,增加一种类型Address
	 */
	public static void loopClearProducts(String DomainName, String Type, String UserEmailAccount, int loopTimes)
			throws Exception {
		boolean eflag = true;

		String logStr = "";
		if (Type.equals("Cart")) {
			logStr = "购物车";
		} else if (Type.equals("Collect")) {
			logStr = "收藏夹";
		} else if (Type.equals("SaveForLater")) {
			logStr = "Save for later";
		} else if (Type.equals("Address")) {
			logStr = "地址";
		} else {
			Log.logWarn("接口命令无效,中止测试(" + GetMethodName() + ",Type=" + Type + ").");
			Assert.fail();
		}
		boolean res = false;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试清空用户" + logStr + "(" + UserEmailAccount + ").");
			}

			try {
				int ret = Pub.IF_ClearProducts(DomainName, Type, UserEmailAccount);
				if (ret == 0) {
					Log.logWarn(i + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
					continue;
				}

				if (i > 1) {
					Log.logWarn(i + " times,清空用户" + logStr + "成功(" + UserEmailAccount + ").");
				}
				res = true;
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
			}
		}

		if (!res) {
			Log.logWarn("连续 " + loopTimes + " times,清空用户" + logStr + "失败(" + UserEmailAccount + ").");
			throw new RetryException();
		}
	}

	/**
	 * 清空用户购物车,收藏夹和save for later中的商品，如果清除失败，则重试多次
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param loopTimes
	 *            尝试清空的次数
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-14,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static void loopClearProductsAllTypes(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		loopClearProducts(DomainName, "Cart", UserEmailAccount, loopTimes);
		loopClearProducts(DomainName, "Collect", UserEmailAccount, loopTimes);
		loopClearProducts(DomainName, "SaveForLater", UserEmailAccount, loopTimes);
	}

	/**
	 * 清空用户购物车和收藏夹，如果清除失败，则重试多次
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param loopTimes
	 *            尝试清空的次数
	 * @throws Exception
	 * @author yuyang 创建时间：2016-12-19,更新时间：2016-12-19
	 */
	public static void loopClearProducts2Types(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		loopClearProducts(DomainName, "Cart", UserEmailAccount, loopTimes);
		loopClearProducts(DomainName, "Collect", UserEmailAccount, loopTimes);
	}

	/**
	 * 接口：查询并获取商品列表数据，数据结果是一个JSONObject，目前是获取20个商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-清仓商品， 4-普通商品且2<库存<100的
	 * @param page
	 *            搜索的页数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @author yuyang ,创建时间：2016-10-25,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings("unused")
	public static JSONObject IF_GetProductList(String DomainName, int typeProduct, int page, boolean freeShippingFlag)
			throws Exception {
		String URLCommand = "";
		String freeShippingStr = "";
		String maxPriceStr = "";
		if (freeShippingFlag) {
			freeShippingStr = "&is_freeshipping=y";
			maxPriceStr = "5";
		} else { // 测试网不开启免邮，因为测试商品数据比较少，支付也可以随便付
			maxPriceStr = "500";
		}
		String https = "http://";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			https = "https://";
		}

		if (typeProduct == 2) {
			URLCommand = https + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=20&is_promote=y" + freeShippingStr;
		} else if (typeProduct == 3) {
			URLCommand = https + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=20&is_clearance=y" + freeShippingStr;
		} else if (typeProduct == 4) {
			URLCommand = https + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=20&goods_number=100" + freeShippingStr;
		} else {
			URLCommand = https + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=20" + freeShippingStr;
		}
		// String resstr = get(URLCommand).get("Response");
		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if (!"200".equals(res.get("_resultcode").toString())) {
			throw new Exception("Get product list failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
		}
		return res;
	}

	/**
	 * 查询并获取商品列表数据，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-清仓商品
	 * @param page
	 *            搜索的页数
	 * @param loopTimes
	 *            尝试删除的次数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-25,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static JSONObject loopGetProductList(String DomainName, int typeProduct, int page, int loopTimes,
			boolean freeShippingFlag) throws Exception {
		boolean eflag = true;

		String productstr = "普通";
		if (typeProduct == 1) {
			productstr = "普通";
		} else if (typeProduct == 2) {
			productstr = "促销";
		} else if (typeProduct == 3) {
			productstr = "清仓";
		}

		JSONObject res = null;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询并获取" + productstr + "商品列表数据(" + DomainName + ").");
			}

			try {
				res = IF_GetProductList(DomainName, typeProduct, page, freeShippingFlag);
				if (i > 1) {
					Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
				} else {
					Log.logInfo("查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,查询并获取" + productstr + "商品列表数据失败(" + DomainName + ").");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 通过价格条件获取指定类型的商品,如果获取的是普通商品，会返回库存充足的普通商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-清仓商品 ，4-普通商品且2<库存<100的
	 * @param typePrice
	 *            指定类型，1-价格最低,2-价格大于2且小于30的，3-价格不限，4-价格不限且不检查库存
	 * @param firstProductFlag
	 *            1-如果符合typePrice的商品不存在，则返回第一件商品，0-...不返回第一件商品
	 * @param page
	 *            搜索的页数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-25,更新时间：2016-12-17 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub 更新说明(2016-12-17)：by yuyang,增加了产品类型4和5
	 */
	@SuppressWarnings("rawtypes")
	public static Product GetProduct(String DomainName, int typeProduct, int typePrice, int firstProductFlag, int page,
			boolean freeShippingFlag) throws Exception {
		Product res = new Product();
		JSONObject jsonData = loopGetProductList(DomainName, typeProduct, page, 3, freeShippingFlag);
		JSONObject jsonGoodsList = jsonData.getJSONObject("goods_list");
		Iterator it = jsonGoodsList.keys();

		String findkey = "";
		String firstkey = "";
		if (typePrice == 1) {
			double minPrice = 9999;
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
						continue;
					}
				} else if (typeProduct == 3) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					if (Double.valueOf(shopPrice) < minPrice) {
						minPrice = Double.valueOf(shopPrice);
						findkey = key;
					}
				}
			}
		} else if (typePrice == 2) {
			double maxPrice = 30;
			double minPrice = 2;
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
						continue;
					}
				} else if (typeProduct == 3) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					if (Double.valueOf(shopPrice) >= minPrice && Double.valueOf(shopPrice) <= maxPrice) {
						findkey = key;
						break;
					}
				}
			}
		} else if (typePrice == 3) {
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 10) { // 促销商品的库存需要>=10
						continue;
					}
				} else if (typeProduct == 3) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) < 3) || (Integer.valueOf(stockNum) >= 9999)) { // 清仓商品的库存需要>=3且<9999
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) <= 2) || (Integer.valueOf(stockNum) >= 100)) { // 普通商品的库存需要>2且<100
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					findkey = key;
					break;
				}
			}
		} else if (typePrice == 4) {
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				} else if (typeProduct == 3) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					findkey = key;
					break;
				}
			}
		}

		if (firstProductFlag == 1 && !hasLength(res.id)) {
			findkey = firstkey;
		}

		if (hasLength(findkey)) {
			res.id = findkey;
			res.URL = jsonGoodsList.getJSONObject(findkey).getString("url_title");
			res.shopPrice = jsonGoodsList.getJSONObject(findkey).getString("shop_price");
			res.sku = jsonGoodsList.getJSONObject(findkey).getString("goods_sn");
			res.stockNum = str2intpositive(jsonGoodsList.getJSONObject(findkey).getString("goods_number"));
			res.type = typeProduct;
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * 通过索引获取指定类型的商品,只获取第一页的
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-清仓商品
	 * @param index
	 *            返回产品的索引，小于0时按1处理，大于当前商品数量时，返回空
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-25,更新时间：2016-12-07 备注：这个方法是不会判断库存数量的
	 *         更新说明(2016-12-07)：by yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings("rawtypes")
	public static Product GetProductByIndex(String DomainName, int typeProduct, int index, boolean freeShippingFlag)
			throws Exception {
		if (index <= 0) {
			index = 1;
		}
		Product res = new Product();
		JSONObject jsonData = loopGetProductList(DomainName, typeProduct, 1, 3, freeShippingFlag);
		JSONObject jsonGoodsList = jsonData.getJSONObject("goods_list");
		Iterator it = jsonGoodsList.keys();

		String findkey = "";
		int tmpidx = 0;
		while (it.hasNext()) {
			String key = it.next().toString();
			if (hasLength(key)) {
				tmpidx++;
				if (index == tmpidx) {
					findkey = key;
					break;
				}
			}
		}

		if (hasLength(findkey)) {
			res.id = findkey;
			res.URL = jsonGoodsList.getJSONObject(findkey).getString("url_title");
			res.shopPrice = jsonGoodsList.getJSONObject(findkey).getString("shop_price");
			res.sku = jsonGoodsList.getJSONObject(findkey).getString("goods_sn");
			res.stockNum = str2intpositive(jsonGoodsList.getJSONObject(findkey).getString("goods_number"));
			res.type = typeProduct;
		} else {
			Log.logWarn("获取商品数据失败,中止测试.");
			Assert.fail();
		}

		return res;
	}

	/**
	 * 接口：获取商品的详细信息，数据结果是一个JSONObject
	 * 
	 * @param DomainName
	 *            域名
	 * @param productSku
	 *            商品的sku
	 * @author yuyang ,创建时间：2016-10-25,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static JSONObject IF_GetProductInfo(String DomainName, String productSku) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?mod=common&act=get_goods_info&goods_sn=" + productSku;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?mod=common&act=get_goods_info&goods_sn=" + productSku;
		}
		Log.logInfo("获取商品相信信息URL：" + URLCommand);
		Log.logInfo("接口返回:" + get(URLCommand).get("Response"));
		// JSONObject res =
		// JSONObject.fromObject(get(URLCommand).get("Response"));
		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if (!"200".equals(res.get("_resultcode").toString())) {
			throw new Exception(
					"Get product(" + productSku + ") info failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
		}
		return res;
	}

	/**
	 * 获取商品的详细信息，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param productSku
	 *            商品的sku
	 * @param loopTimes
	 *            尝试删除的次数
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-25,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static JSONObject loopGetProductInfo(String DomainName, String productSku, int loopTimes) throws Exception {
		boolean eflag = true;

		JSONObject res = null;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取商品(" + productSku + ")的详细信息.");
			}

			try {
				res = IF_GetProductInfo(DomainName, productSku);
				if (i > 1) {
					Log.logWarn(i + " times,获取商品(" + productSku + ")的详细信息成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取商品(" + productSku + ")的详细信息失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,获取商品(" + productSku + ")的详细信息失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 获取商品详细数据，从中得到商品的阶梯价格，返回商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-清仓商品
	 * @param typePrice
	 *            指定类型，1-价格最低
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-25,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static Product GetProductStepPrice(String DomainName, String productSku, Product product) throws Exception {
		JSONObject jsonData = loopGetProductInfo(DomainName, productSku, 3);
		JSONArray arrayProdcutStepPrice = jsonData.getJSONArray("volume_price_list");

		product.stepNum = arrayProdcutStepPrice.size();
		for (int i = 0; i < product.stepNum; i++) {
			product.stepName.add("priceSection" + i);
		}
		for (int i = 0; i < product.stepNum; i++) {
			JSONObject jsonProdcutStepPrice = (JSONObject) arrayProdcutStepPrice.get(i);
			String stepNum = jsonProdcutStepPrice.getString("number");
			String stepPrice = jsonProdcutStepPrice.getString("format_price");
			product.stepPriceMap.put(product.stepName.get(i), stepPrice);

			List<String> numlist = new ArrayList<String>();
			String[] numarray = stepNum.split("-| ");
			for (int j = 0; j < numarray.length; j++) {
				if (hasLength(numarray[j]) && !numarray[j].contains("max")) {
					numlist.add(numarray[j]);
				}
			}
			if (numlist.size() == 1) {
				numlist.add(numlist.get(0));
			}
			product.stepSectionMap.put(product.stepName.get(i), numlist);
		}

		return product;
	}

	/**
	 * 接口：获取用户的订单信息，主要包括订单状态和编号
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param start_time
	 *            开始的时间戳
	 * @param end_time
	 *            结束的时间戳
	 * @author yuyang ,创建时间：2016-10-26,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static JSONObject IF_GetOrderInfo(String DomainName, String UserEmailAccount, long start_time, long end_time)
			throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?mod=user&act=query_order&email=" + UserEmailAccount
					+ "&start_time=" + start_time + "&end_time=" + end_time;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=query_order&email=" + UserEmailAccount
					+ "&start_time=" + start_time + "&end_time=" + end_time;
		}
		// System.out.println(URLCommand);
		// JSONObject res =
		// JSONObject.fromObject(get(URLCommand).get("Response"));
		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		String rescode = res.get("_resultcode").toString();
		if (!"200".equals(rescode) && !"1001".equals(rescode)) {
			throw new Exception("Get recent order info of user(" + UserEmailAccount + ") failed!("
					+ res.get("_resultcode") + ")(" + DomainName + ")");
		}
		return res;
	}

	/**
	 * 获取用户的订单信息，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param start_time
	 *            开始的时间戳
	 * @param end_time
	 *            结束的时间戳
	 * @param loopTimes
	 *            重试次数
	 * @throws RetryException
	 * @author yuyang 创建时间：2016-10-26,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static JSONObject loopGetOrderInfo(String DomainName, String UserEmailAccount, long start_time,
			long end_time, int loopTimes) throws Exception {
		boolean eflag = true;

		JSONObject res = null;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取用户(" + UserEmailAccount + ")的订单信息.");
			}

			try {
				res = IF_GetOrderInfo(DomainName, UserEmailAccount, start_time, end_time);
				if (i > 1) {
					Log.logWarn(i + " times,获取用户(" + UserEmailAccount + ")的订单信息成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取用户(" + UserEmailAccount + ")的订单信息失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,获取用户(" + UserEmailAccount + ")的订单信息失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 将正整数字符串转换为正整数，如果字符串为非数字，则返回-1
	 * 
	 * @param str
	 *            待转换的数字字符串，或非数字字符串
	 * @return 转换后的数字，或-1，-1表示抛出异常
	 * @author yuyang 创建时间：2016-10-26,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static int str2intpositive(String str) {
		int res = -1;

		try {
			res = Integer.valueOf(str);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		return res;
	}

	/**
	 * 获取商品详细数据，从中得到商品的阶梯价格，返回商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐户
	 * @param start_time
	 *            开始的时间戳
	 * @param end_time
	 *            结束的时间戳
	 * @throws Exception
	 * @author yuyang 创建时间：2016-10-26,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> GetOrderNumberArray(String DomainName, String UserEmailAccount, long start_time,
			long end_time) throws Exception {
		List<String> OrderNumberArray = new ArrayList<String>();
		JSONObject jsonData = loopGetOrderInfo(DomainName, UserEmailAccount, start_time, end_time, 3);

		Iterator it = jsonData.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (str2intpositive(key) >= 0) {
				JSONObject jsonOrder = jsonData.getJSONObject(key);
				OrderNumberArray.add(jsonOrder.getString("order_sn"));
			}
		}

		return OrderNumberArray;
	}

	/**
	 * 接口：获取服务器时间
	 * 
	 * @param DomainName
	 *            域名
	 * @author yuyang ,创建时间：2016-10-26,更新时间：2016-12-07 更新说明(2016-12-07)：by
	 *         yuyang,由PublicFun移动到Pub
	 */
	public static String IF_GetServerTime(String DomainName) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?act=get_unix_time&mod=common";
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?act=get_unix_time&mod=common";
		}
		// JSONObject res =
		// JSONObject.fromObject(get(URLCommand).get("Response"));
		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		String rescode = res.get("_resultcode").toString();
		if (!"200".equals(rescode)) {
			throw new Exception("Get unix time failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
		}

		String restime = res.get("unixtime").toString();
		return restime;
	}

	/**
	 * 获取服务器时间，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param loopTimes
	 *            重试次数
	 * @author yuyang 创建时间：2016-10-26,更新时间：2016-12-07
	 * @throws RetryException
	 *             更新说明(2016-12-07)：by yuyang,由PublicFun移动到Pub
	 */
	public static String loopGetServerTime(String DomainName, int loopTimes) throws RetryException {
		boolean eflag = true;

		String res = "";
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取服务器时间(" + DomainName + ").");
			}

			try {
				res = IF_GetServerTime(DomainName);
				if (i > 1) {
					Log.logWarn(i + " times,获取服务器时间(" + DomainName + ")成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取服务器时间(" + DomainName + ")失败.");
			}
		}

		if (!hasLength(res)) {
			Log.logWarn("连续 " + loopTimes + " times,获取服务器时间(" + DomainName + ")失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 获取当前测试方法的重试标志
	 * 
	 * @param lastMethodName
	 *            上一次执行的方法名
	 * @param currentMethodName
	 *            当前执行的方法名
	 * @author yuyang ,创建时间：2016-11-12,更新时间：2016-12-08 备注：需要传入正确的上一次执行的方法名
	 *         更新说明(2016-12-08)：by yuyang,由PublicFun移动到Pub
	 */
	public static boolean RetryFlag(String lastMethodName, String currentMethodName) {
		boolean res = false;

		try {
			if (currentMethodName.equals(lastMethodName)) {
				res = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 接口：通过cookies 获得验证码
	 * 
	 * @备注：重写了张君的IF_Verification方法，增加了一个参数sessionId
	 * @param driver
	 *            浏览器driver
	 * @param domainName
	 *            域名
	 * @param sessionIdName
	 *            session名
	 * @param requestId
	 *            请求的id（每次请求保持唯一性）
	 * @param privateKey
	 *            测试的key，需分区正式和测试
	 * @param ftc
	 *            验证码类型，登录=login，注册=regist，忘记密码ftc=''
	 * @return 验证码,获取不到验证码时会返回""
	 * @author yuyang,创建时间：2016-12-13,更新时间：2016-12-13
	 */
	public static String IF_Verification(WebDriver driver, String domainName, String sessionIdName, String requestId,
			String privateKey, String ftc) {
		String getdata = null;
		String getsign;
		String getResponse;
		String verifyCode = "";

		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(sessionIdName)) { // 取出cookie值
				// System.out.println(cookie.getValue());
				getdata = "a:1:{s:10:\"session_id\";s:26:\"" + cookie.getValue() + "\";}";// 生成虚序列化data
			}
		}

		if (domainName.equals("www.gearbest.com") && staging == null) { // 预发布环境
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("ORIGINDC")) {
					ORIGINDC = new Cookie("ORIGINDC", cookie.getValue(), domainName.replaceFirst("www.", "."), "/",
							null);
					break;
				}
			}
		}

		getsign = privateKey + requestId + getdata;// 获得私钥
		String sign = Pub.GetMD5String(getsign);// 生成MD5
		String URLCommand = "";
		if ((!domainName.contains("s1")) || domainName.contains("gearbest.com")) {
			URLCommand = "https://" + domainName + "/auto/api.php?mod=common&act=get_user_sign&requestId=" + requestId
					+ "&sign=" + sign + "&data=" + getdata + "&ftc=" + ftc;
		} else {
			URLCommand = "http://" + domainName + "/auto/api.php?mod=common&act=get_user_sign&requestId=" + requestId
					+ "&sign=" + sign + "&data=" + getdata + "&ftc=" + ftc;
		}
		Map<String, String> res = get(URLCommand);
		getResponse = res.get("Response");
		// System.out.println(URLCommand);
		// System.out.println(getResponse);

		verifyCode = getResponse;
		if (verifyCode.contains("{s:6:\\\"verify\\\";")) {
			verifyCode = verifyCode
					.substring(verifyCode.indexOf("{s:6:\\\"verify\\\";") + "{s:6:\\\"verify\\\";".length());
			verifyCode = verifyCode.substring(0, verifyCode.indexOf(";"));
			if (verifyCode.contains("\"")) {
				verifyCode = verifyCode.substring(verifyCode.indexOf("\"") + 1, verifyCode.lastIndexOf("\\"));
			} else {
				verifyCode = "";
				Log.logWarn("Get verify code failed!(" + getResponse + ")");
			}
		} else {
			Log.logWarn("Get verify code failed!(" + getResponse + ")");
		}
		return verifyCode;
	}

	/**
	 * 获得验证码，如果获取失败，则重试
	 * 
	 * @param driver
	 *            浏览器driver
	 * @param domainName
	 *            域名
	 * @param sessionIdName
	 *            session名
	 * @param requestId
	 *            请求的id（每次请求保持唯一性）
	 * @param privateKey
	 *            测试的key，需分区正式和测试
	 * @param ftc
	 *            验证码类型，登录=login，注册=regist，忘记密码ftc=''
	 * @param loopTimes
	 *            重试次数
	 * @return 验证码
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang,创建时间：2016-12-13,更新时间：2016-12-13
	 */
	public static String loopGetVerifyCode(WebDriver driver, String domainName, String sessionIdName, String requestId,
			String privateKey, String ftc, int loopTimes) throws Exception {
		boolean eflag = true;
		String verifyCode = "";

		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取session(" + sessionIdName + ")的验证码(" + ftc + ").");
			}

			try {
				verifyCode = IF_Verification(driver, domainName, sessionIdName, requestId, privateKey, ftc);
				if (Pub.hasLength(verifyCode)) {
					if (i > 1) {
						Log.logWarn(i + " times,获取session(" + sessionIdName + ")的验证码(" + ftc + ")成功.");
					}
					break;
				} else {
					Log.logWarn(i + " times,获取session(" + sessionIdName + ")的验证码(" + ftc + ")失败.");
				}
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取session(" + sessionIdName + ")的验证码(" + ftc + ")失败.");
			}
			driver.navigate().refresh();
			Pub.sleep(2);
		}

		if (!Pub.hasLength(verifyCode)) {
			Log.logWarn("连续 " + loopTimes + " times,获取session(" + sessionIdName + ")的验证码(" + ftc + ")失败.");
			throw new RetryException();
		}
		return verifyCode;
	}

	/**
	 * 接口：查询并获取用户相关信息，数据结果是一个JSONObject
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang,创建时间：2016-12-13,更新时间：2016-12-13
	 */
	public static JSONObject IF_GetUserInfo(String DomainName, String UserEmailAccount) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?act=get_user_info&mod=user&email=" + UserEmailAccount;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?act=get_user_info&mod=user&email=" + UserEmailAccount;
		}

		// JSONObject res =
		// JSONObject.fromObject(get(URLCommand).get("Response"));
		String res0 = get(URLCommand).get("Response");
		Log.logInfo(res0);
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if ("1001".equals(res.get("_resultcode").toString())) {
			res = null;
		} else if ("200".equals(res.get("_resultcode").toString())) {
		} else {
			throw new Exception("Get user info failed!(" + res.get("_resultcode") + ")(" + DomainName + ")("
					+ UserEmailAccount + ")");
		}
		return res;
	}

	/**
	 * 查询用户邮箱帐号是否己注册，如果查询失败，则重试
	 * 备注：与loopQueryUserRegisterStatus的区别是，调用了统一参数标准的新接口
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @param loopTimes
	 *            重试次数
	 * @return 1-用户邮箱帐号己注册;0-未注册
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang,创建时间：2016-12-13,更新时间：2016-12-13
	 */
	public static int loopQueryUserRegisterStatus2(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		boolean eflag = true;
		JSONObject userInfo;

		int res = -1;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态.");
			}

			try {
				userInfo = IF_GetUserInfo(DomainName, UserEmailAccount);
				if (i > 1) {
					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态成功.");
				}
				if (userInfo == null) {
					res = 0;
				} else {
					res = 1;
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
			}
		}

		if (res == -1) {
			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的注册状态失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 查询用户是否己作邮箱验证，如果查询失败，则重试
	 * 备注：与loopQueryUserVerifyEmailStatus的区别是，调用了统一参数标准的新接口
	 * 
	 * @param DomainName
	 *            域名
	 * @param UserEmailAccount
	 *            用户的邮箱帐号
	 * @param loopTimes
	 *            重试次数
	 * @return 1-邮箱己验证;0-邮箱未验证;-1-邮箱未注册
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang,创建时间：2016-12-13,更新时间：2016-12-13
	 */
	public static int loopQueryUserVerifyEmailStatus2(String DomainName, String UserEmailAccount, int loopTimes)
			throws Exception {
		boolean eflag = true;
		JSONObject userInfo;

		int res = -2;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态.");
			}

			try {
				userInfo = IF_GetUserInfo(DomainName, UserEmailAccount);
				if (i > 1) {
					Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态(或注册状态)成功.");
				}
				if (userInfo == null) {
					res = -1;
					break;
				}
				res = Integer.valueOf(userInfo.getString("is_validated"));
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
			}
		}

		if (res == -2) {
			Log.logWarn("连续 " + loopTimes + " times,查询用户邮箱帐号(" + UserEmailAccount + ")的邮箱验证状态失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 获取商品详细数据，从中得到商品的各仓库的库存,指定wid的库存需要大于最小库存参数
	 * 
	 * @param DomainName
	 *            域名
	 * @param productSku
	 *            商品的sku
	 * @param wid
	 *            仓库类型，21-新中国仓，3-欧洲仓， 8-美西仓
	 * @param country
	 *            国家类型，如，en, es, it
	 * @parma minStockNum 最小库存，不需要限制时填0
	 * @param product
	 *            商品数据
	 * @throws Exception
	 *             抛出全部异常
	 * @author yuyang 创建时间：2016-12-17 更新时间：2016-12-17
	 * @更新说明(2016-12-20)：by yuyang,增加了wid和country参数，增加了widCountryMap初始化，
	 *                      增加了wid和country校验
	 */
	@SuppressWarnings("rawtypes")
	public static Product GetProductWid(String DomainName, String productSku, String wid, String country,
			int minStockNum, Product product) throws Exception {
		JSONObject jsonData = loopGetProductInfo(DomainName, productSku, 3);

		Iterator it = jsonData.keys();
		JSONObject jsonGoods;
		while (it.hasNext()) {
			String key = it.next().toString();
			try {
				String w;
				String goods_number;
				jsonGoods = jsonData.getJSONObject(key);
				w = jsonGoods.getString("wid");
				goods_number = jsonGoods.getString("goods_number");
				product.widMap.put(w, goods_number);
				String countrySiteStr = jsonGoods.getString("country_site");
				countrySiteStr = countrySiteStr.replaceAll("\\[", "");
				countrySiteStr = countrySiteStr.replaceAll("\\]", "");
				countrySiteStr = countrySiteStr.replaceAll("\"", "");
				List<String> countrySiteList = new ArrayList<String>();
				countrySiteList = Arrays.asList(countrySiteStr.split(","));
				product.widCountryMap.put(w, countrySiteList);
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}

		for (String w : product.widMap.keySet()) {
			if (Integer.valueOf(product.widMap.get(w)) > 0) {
				product.widMapValid.put(w, product.widMap.get(w));
			}
		}

		boolean widContainFlag = false;
		for (String w : product.widMapValid.keySet()) {
			if (w.equals(wid)) {
				widContainFlag = true;
			}
		}
		if (!widContainFlag) {
			product.widMapValid.clear();
		}

		if (widContainFlag) {
			if (!product.widCountryMap.get(wid).contains(country)) {
				product.widMapValid.clear();
			}
		}

		if (minStockNum > 0 && widContainFlag) {
			if (Integer.valueOf(product.widMapValid.get(wid)) <= minStockNum) {
				product.widMapValid.clear();
			}
		}

		return product;
	}

	/**
	 * 接口：适用于GB的，查询并获取商品列表数据，数据结果是一个JSONObject，目前是获取20个商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-预售商品，
	 *            4-普通商品且2<库存<100的，5-普通商品，且0<价格<1，6-普通商品，且1500<价格<100000
	 * @param wid
	 *            仓库类型，21-新中国仓，3-欧洲仓， 8-美西仓
	 * @param page
	 *            搜索的页数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @author yuyang 创建时间：2016-12-20,更新时间：2017-03-16 更新说明(2017-03-16)：by
	 *         yuyang,增加预售商品类型
	 */
	public static JSONObject IF_GetProductListGB(String DomainName, int typeProduct, String wid, int page,
			boolean freeShippingFlag) throws Exception {
		String URLCommand = "";
		String freeShippingStr = "";
		String maxPriceStr = "";
		if (freeShippingFlag) {
			freeShippingStr = "&is_freeshipping=y";
			maxPriceStr = "5";
		} else { // 测试网不开启免邮，因为测试商品数据比较少，支付也可以随便付
			maxPriceStr = "500";
		}
		if (!hasLength(wid)) {
			wid = "21";
		}
		String httpOrHttps = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			httpOrHttps = "https://";
		} else {
			httpOrHttps = "http://";
		}
		if (typeProduct == 2) {
			URLCommand = httpOrHttps + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=2&wid=" + wid + "&is_promote=y" + freeShippingStr;
		} else if (typeProduct == 3) {
			URLCommand = httpOrHttps + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=2&wid=" + wid + "&is_presale=y" + freeShippingStr;
		} else if (typeProduct == 4) {
			URLCommand = httpOrHttps + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=1&wid=" + wid + "&goods_number=100" + freeShippingStr;
		} else if (typeProduct == 5) {
			URLCommand = httpOrHttps + DomainName
					+ "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price=1&page=" + page
					+ "&page_size=20&wid=" + wid;
		} else if (typeProduct == 6) {
			URLCommand = httpOrHttps + DomainName
					+ "/auto/api.php?mod=common&act=search_goods&min_price=1500&max_price=100000&page=" + page
					+ "&page_size=1&wid=" + wid;
		} else if (typeProduct == 7) {
			URLCommand = httpOrHttps + DomainName
					+ "/auto/api.php?mod=common&act=search_goods&min_price=30.1&max_price=35&page=" + page
					+ "&page_size=1&wid=" + wid;
		} else {
			URLCommand = httpOrHttps + DomainName + "/auto/api.php?mod=common&act=search_goods&min_price=0&max_price="
					+ maxPriceStr + "&page=" + page + "&page_size=20&wid=" + wid + "" + freeShippingStr;
		}
		Log.logInfo("URLCommand:" + URLCommand);
		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if (!"200".equals(res.get("_resultcode").toString())) {
			throw new Exception("Get product list failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
		}
		return res;
	}

	/**
	 * 查询并获取商品列表数据，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-预售商品，
	 *            4-普通商品且2<库存<100的，5-普通商品，且0<价格<1，6-普通商品，且1500<价格<100000,7-普通商品，
	 *            且30<价格<35
	 * @param wid
	 *            仓库类型，21-新中国仓，3-欧洲仓， 8-美西仓
	 * @param page
	 *            搜索的页数
	 * @param loopTimes
	 *            尝试删除的次数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @throws Exception
	 * @author yuyang 创建时间：2016-12-20,更新时间：2017-03-16 更新说明(2017-03-16)：by
	 *         yuyang,增加预售商品类型
	 */
	public static JSONObject loopGetProductListGB(String DomainName, int typeProduct, String wid, int page,
			int loopTimes, boolean freeShippingFlag) throws Exception {
		boolean eflag = true;

		String productstr = "普通";
		if (typeProduct == 1) {
			productstr = "普通";
		} else if (typeProduct == 2) {
			productstr = "促销";
		} else if (typeProduct == 3) {
			productstr = "预售";
		}

		JSONObject res = null;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试查询并获取" + productstr + "商品列表数据(" + DomainName + ").");
			}

			try {
				res = IF_GetProductListGB(DomainName, typeProduct, wid, page, freeShippingFlag);
				if (i > 1) {
					Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
				} else {
					Log.logInfo("查询并获取" + productstr + "商品列表数据成功(" + DomainName + ").");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,查询并获取" + productstr + "商品列表数据失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,查询并获取" + productstr + "商品列表数据失败(" + DomainName + ").");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 通过价格条件获取指定类型的商品,如果获取的是普通商品，会返回库存充足的普通商品
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeProduct
	 *            产品类型，1-普通商品，2-促销商品，3-预售商品，
	 *            4-普通商品且2<库存<100的，5-普通商品，且0<价格<1，6-普通商品，且1500<价格<100000,7-普通商品，
	 *            且30<价格<35
	 * @param wid
	 *            仓库类型，21-新中国仓，3-欧洲仓， 8-美西仓
	 * @param typePrice
	 *            指定类型，1-价格最低,2-价格大于2且小于30的，3-价格不限并检查库存，4-价格不限且不检查库存
	 * @param firstProductFlag
	 *            1-如果符合typePrice的商品不存在，则返回第一件商品，0-...不返回第一件商品
	 * @param page
	 *            搜索的页数
	 * @param freeShippingFlag
	 *            免邮标志，true-免邮，false-不免邮
	 * @throws Exception
	 * @author yuyang 创建时间：2016-12-20,更新时间：2017-03-16 更新说明(2017-03-16)：by
	 *         yuyang,增加预售商品类型
	 */
	@SuppressWarnings("rawtypes")
	public static Product GetProductGB(String DomainName, int typeProduct, String wid, int typePrice,
			int firstProductFlag, int page, boolean freeShippingFlag) throws Exception {
		Product res = new Product();
		JSONObject jsonData = loopGetProductListGB(DomainName, typeProduct, wid, page, 3, freeShippingFlag);
		JSONObject jsonGoodsList = jsonData.getJSONObject("goods_list");
		Iterator it = jsonGoodsList.keys();

		String findkey = "";
		String firstkey = "";
		if (typePrice == 1) {
			double minPrice = 9999;
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 3) { // 促销商品的库存需要>=3
						continue;
					}
				} else if (typeProduct == 3) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 预售商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) <= 2) || (Integer.valueOf(stockNum) > 98)) { // 普通商品的库存需要>2且<=98
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					if (Double.valueOf(shopPrice) < minPrice) {
						minPrice = Double.valueOf(shopPrice);
						findkey = key;
					}
				}
			}
		} else if (typePrice == 2) {
			double maxPrice = 30;
			double minPrice = 2;
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 3) { // 促销商品的库存需要>=3
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) <= 2) || (Integer.valueOf(stockNum) > 98)) { // 普通商品的库存需要>2且<=98
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					if (Double.valueOf(shopPrice) >= minPrice && Double.valueOf(shopPrice) <= maxPrice) {
						findkey = key;
						break;
					}
				}
			}
		} else if (typePrice == 3) {
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 1000) { // 普通商品的库存需要>=1000
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) < 3) { // 促销商品的库存需要>=3
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if ((Integer.valueOf(stockNum) <= 2) || (Integer.valueOf(stockNum) > 98)) { // 普通商品的库存需要>2且<=98
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					findkey = key;
					break;
				}
			}
		} else if (typePrice == 4) {
			JSONObject jsonGoods;
			String shopPrice;
			while (it.hasNext()) {
				String key = it.next().toString();
				jsonGoods = jsonGoodsList.getJSONObject(key);
				shopPrice = jsonGoods.getString("shop_price");

				if (!hasLength(firstkey))
					firstkey = key;

				if (typeProduct == 1) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				} else if (typeProduct == 2) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				} else if (typeProduct == 4) {
					String stockNum = jsonGoods.getString("goods_number");
					if (Integer.valueOf(stockNum) <= 0) { // 库存需要>0
						continue;
					}
				}

				if (hasLength(shopPrice)) {
					findkey = key;
					break;
				}
			}
		}

		if (firstProductFlag == 1 && !hasLength(res.id)) {
			findkey = firstkey;
		}

		if (hasLength(findkey)) {
			res.id = findkey;
			res.URL = jsonGoodsList.getJSONObject(findkey).getString("url_title");
			res.shopPrice = jsonGoodsList.getJSONObject(findkey).getString("shop_price");
			res.sku = jsonGoodsList.getJSONObject(findkey).getString("goods_sn");
			res.stockNum = str2intpositive(jsonGoodsList.getJSONObject(findkey).getString("goods_number"));
			res.type = typeProduct;
			res.promotePrice = jsonGoodsList.getJSONObject(findkey).getString("promote_price");
			res.isPromote = Integer.valueOf(jsonGoodsList.getJSONObject(findkey).getString("is_promote"));
			res.promoteStartDate = jsonGoodsList.getJSONObject(findkey).getString("promote_start_date");
			res.promoteEndDate = jsonGoodsList.getJSONObject(findkey).getString("promote_end_date");
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * 接口：获取订单的详细信息，数据结果是一个JSONObject
	 * 
	 * @param DomainName
	 *            域名
	 * @param orderNumber
	 *            订单编号
	 * @author yuyang,创建时间：2017-01-17,更新时间：2017-01-17
	 */
	public static JSONObject IF_GetOrderInfo(String DomainName, String orderNumber) throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php/?mod=common&act=get_order_info&order_sn="
					+ orderNumber;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php/?mod=common&act=get_order_info&order_sn="
					+ orderNumber;
		}

		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if (!"200".equals(res.get("_resultcode").toString())) {
			throw new Exception(
					"Get order(" + orderNumber + ") info failed!(" + res.get("_resultcode") + ")(" + DomainName + ")");
		}
		return res;
	}

	/**
	 * 获取订单的详细信息，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param orderNumber
	 *            订单编号
	 * @param loopTimes
	 *            重试的次数
	 * @throws Exception
	 * @author yuyang,创建时间：2017-01-17,更新时间：2017-01-17
	 */
	public static JSONObject loopGetOrderInfo(String DomainName, String orderNumber, int loopTimes) throws Exception {
		boolean eflag = true;

		JSONObject res = null;
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取订单(" + orderNumber + ")的详细信息.");
			}

			try {
				res = IF_GetOrderInfo(DomainName, orderNumber);
				if (i > 1) {
					Log.logWarn(i + " times,获取订单(" + orderNumber + ")的详细信息成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取订单(" + orderNumber + ")的详细信息失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,获取订单(" + orderNumber + ")的详细信息失败.");
			throw new RetryException();
		}

		return res;
	}

	/**
	 * 接口：获取订单的详细信息，数据结果是一个JSONObject
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeOfCoupon
	 *            促销码类型,1-百分比,2-直减,3-抽奖直减,4-一口价
	 * @author yuyang,创建时间：2017-01-17,更新时间：2017-01-17
	 */
	public static JSONObject IF_GetCouponInfo(String DomainName, int typeOfCoupon, String email) throws Exception {
		if (typeOfCoupon < 1 || typeOfCoupon > 4) {
			typeOfCoupon = 1;
		}
		String typeOfCouponInfo = "";
		if (typeOfCoupon == 1) {
			typeOfCouponInfo = "百分比";
		} else if (typeOfCoupon == 2) {
			typeOfCouponInfo = "直减";
		} else if (typeOfCoupon == 3) {
			typeOfCouponInfo = "抽奖直减";
		} else if (typeOfCoupon == 4) {
			typeOfCouponInfo = "一口价";
		}
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php/?mod=common&act=one_coupon&type=" + typeOfCoupon
					+ "&email=" + email;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php/?mod=common&act=one_coupon&type=" + typeOfCoupon
					+ "&email=" + email;
		}

		String res0 = get(URLCommand).get("Response");
		String res1 = res0.substring(res0.indexOf("{"), res0.lastIndexOf("}") + 1);
		JSONObject res = JSONObject.fromObject(res1);
		if (!"200".equals(res.get("_resultcode").toString())) {
			throw new Exception("Get coupon(" + typeOfCouponInfo + ") info failed!(" + res.get("_resultcode") + ")("
					+ DomainName + ")");
		}
		return res;
	}

	/**
	 * 获取订单的详细信息，如果获取失败，则重试
	 * 
	 * @param DomainName
	 *            域名
	 * @param typeOfCoupon
	 *            促销码类型,1-百分比,2-直减,3-抽奖直减,4-一口价
	 * @param email
	 *            帐户邮箱
	 * @param loopTimes
	 *            重试的次数
	 * @throws Exception
	 * @author yuyang,创建时间：2017-01-17,更新时间：2017-01-17
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static List<JSONObject> loopGetCouponInfo(String DomainName, int typeOfCoupon, String email, int loopTimes)
			throws Exception {
		if (typeOfCoupon < 1 || typeOfCoupon > 4) {
			typeOfCoupon = 1;
		}
		String typeOfCouponInfo = "";
		if (typeOfCoupon == 1) {
			typeOfCouponInfo = "百分比";
		} else if (typeOfCoupon == 2) {
			typeOfCouponInfo = "直减";
		} else if (typeOfCoupon == 3) {
			typeOfCouponInfo = "抽奖直减";
		} else if (typeOfCoupon == 4) {
			typeOfCouponInfo = "一口价";
		}

		boolean eflag = true;
		JSONObject res = null;
		List<JSONObject> resList = new ArrayList<JSONObject>();
		for (int i = 1; i <= loopTimes; i++) {
			if (i > 1) {
				Log.logWarn(i + " times,尝试获取促销码(" + typeOfCouponInfo + ")信息.");
			}

			try {
				res = IF_GetCouponInfo(DomainName, typeOfCoupon, email);
				if (i > 1) {
					Log.logWarn(i + " times,获取促销码(" + typeOfCouponInfo + ")信息成功.");
				}
				break;
			} catch (Exception e) {
				if (eflag) {
					e.printStackTrace();
					eflag = false;
				}
				Log.logWarn(i + " times,获取促销码(" + typeOfCouponInfo + ")信息失败.");
			}
		}

		if (res == null) {
			Log.logWarn("连续 " + loopTimes + " times,获取促销码(" + typeOfCouponInfo + ")信息失败.");
			throw new RetryException();
		} else {
			Iterator it = res.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				if (str2intpositive(key) >= 0) {
					JSONObject a = res.getJSONObject(key);
					resList.add(res.getJSONObject(key));
				}
			}
		}

		return resList;
	}

	/**
	 * 通过商品SKU获取商品数据
	 * 
	 * @param DomainName
	 *            域名
	 * @param productSku
	 *            商品的sku
	 * @param wid
	 *            仓库类型，21-新中国仓，3-欧洲仓， 8-美西仓
	 * @throws Exception
	 * @author yuyang 创建时间：2017-01-18,更新时间：2017-01-18
	 */
	@SuppressWarnings("rawtypes")
	public static Product GetProductBySkuGB(String DomainName, String productSku, String wid) throws Exception {
		Product res = new Product();
		JSONObject jsonData = loopGetProductInfo(DomainName, productSku, 3);
		JSONObject jsonProduct = null;

		Iterator it = jsonData.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (str2intpositive(key) >= 0) {
				jsonProduct = jsonData.getJSONObject(key);
				if (jsonProduct.getString("wid").equals(wid)) {
					break;
				} else {
					jsonProduct = null;
				}
			}
		}

		if (jsonProduct != null) {
			res.id = jsonProduct.getString("goods_id");
			res.URL = jsonProduct.getString("url_title");
			res.shopPrice = jsonProduct.getString("shop_price");
			res.sku = jsonProduct.getString("goods_sn");
			res.stockNum = Integer.valueOf(jsonProduct.getString("goods_number"));
			res.promotePrice = jsonProduct.getString("promote_price");
			res.isPromote = Integer.valueOf(jsonProduct.getString("is_promote"));
			res.promoteStartDate = jsonProduct.getString("promote_start_date");
			res.promoteEndDate = jsonProduct.getString("promote_end_date");
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * 获取请求的cookie
	 * 
	 * @author linchaojiang
	 * @return String
	 * @param url:请求的url
	 *            创建时间:2017-03-04,最后更新时间:2017-03-04
	 */
	public static String getCookie(String url) {

		int defaultConnectTimeOut = 30000; // 默认连接超时,毫秒
		int defaultReadTimeOut = 30000; // 默认读取超时,毫秒
		String CookieStr = "";

		BufferedReader in = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			httpURLConnection.setConnectTimeout(defaultConnectTimeOut);
			httpURLConnection.setReadTimeout(defaultReadTimeOut);
			if (staging != null) {
				httpURLConnection.setRequestProperty("Cookie", staging.toString());
			}
			if (ORIGINDC != null) {
				httpURLConnection.setRequestProperty("Cookie", ORIGINDC.toString());
				ORIGINDC = null;
			}

			// 建立连接
			httpURLConnection.connect();

			// 从请求中获取cookie列表
			String cookieskey = "Set-Cookie";
			Map<String, List<String>> maps = httpURLConnection.getHeaderFields();
			List<String> coolist = maps.get(cookieskey);
			Iterator<String> it = coolist.iterator();
			StringBuffer sbu = new StringBuffer();
			// 拼接cookie再请求
			sbu.append("eos_style_cookie=default; ");
			while (it.hasNext()) {
				sbu.append(it.next() + ";");
			}
			CookieStr = sbu.toString();
			CookieStr = CookieStr.substring(0, CookieStr.length() - 1);
			System.out.println("**************CookieStr:" + CookieStr);
		} catch (Exception requestException) {
			System.err.println("发送GET请求出现异常!" + requestException);
		}
		// 关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception closeException) {
				closeException.printStackTrace();
			}
		}
		return CookieStr;
	}

	/**
	 * 发送post请求,并带上cookie
	 * 
	 * @param reqData:请求参数,reqUrl:请求url,cookies:请求cookie
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-21,更新时间:2017-02-21
	 * @return String
	 */
	public static String sentPost(String reqData, String reqUrl, String cookies) throws IOException {

		URL url;
		url = new URL(reqUrl);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Cookie", cookies);
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		out.write(reqData); // 向页面传递数据。post的关键所在！
		out.flush();
		out.close();
		// 一旦发送成功，用以下方法就可以得到服务器的回应：
		String sCurrentLine;
		String sTotalString;
		sCurrentLine = "";
		sTotalString = "";
		InputStream l_urlStream;
		l_urlStream = connection.getInputStream();
		// 传说中的三层包装阿！
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString += sCurrentLine + "\r\n";
		}
		return sTotalString;
	}

	/**
	 * test
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		// url1为获取cookie的url,url2为需要带上url1中cookie执行的post请求url
		String url1 = "http://user.gearbest.com/m-users-a-reset_password-step-3-code-8b02c0d9a40e70f973b222fdcf8690fc9479612.html?email=linchaojiang@globalegrow.com ";
		String url2 = "https://user.gearbest.com/m-users-a-reset_password-step-4.html";
		String cookieS = getCookie(url1);
		Map<String, Object> bodyAll = new LinkedHashMap<String, Object>();
		bodyAll.put("email", "linchaojiang@globalegrow.com");
		bodyAll.put("password", "lincj123456");
		bodyAll.put("password_confirm", "lincj123456");
		bodyAll.put("verifycode", "8b02c0d9a40e70f973b222fdcf8690fc9479612");
		try {
			sentPost(PHPURLBuilder.httpBuildQuery(bodyAll, "UTF-8"), url2, cookieS);
		} catch (IOException e) {
			e.printStackTrace();
			Log.logInfo("发送post请求异常,请求url:" + url2);
		}
	}

	/**
	 * 接口：修改订单的状态
	 * 
	 * @param DomainName
	 *            域名
	 * @param orderSn
	 *            订单编号
	 * @param userEmail
	 *            用户名 包含@后面的
	 * @param orderStatus
	 *            订单状态 0 未付款 1 已付款 2 备货 3 已发货 4 已收到货 6 付款中 7 已授权 8 部分付款 10 退款 11
	 *            取消 12 扣款失败 15 部分配货 16 完全配货
	 *            备注：http://gearbest.com.c.s1.egomsl.com/auto/api.php?mod=user&
	 *            act=update_order_status&email=yuyang01@globalegrow.com&status=
	 *            6&order_sn=W1703082052082819
	 * @author huxuebing ,创建时间：2017-4-10
	 */
	public static void IF_UpdateOrderStatus(String DomainName, String orderSn, String userEmail, int orderStatus)
			throws Exception {
		String URLCommand = "";
		if ((!DomainName.contains("s1")) || DomainName.contains("gearbest.com")) {
			URLCommand = "https://" + DomainName + "/auto/api.php?mod=user&act=update_order_status&email=" + userEmail
					+ "&status=" + orderStatus + "&order_sn=" + orderSn;
		} else {
			URLCommand = "http://" + DomainName + "/auto/api.php?mod=user&act=update_order_status&email=" + userEmail
					+ "&status=" + orderStatus + "&order_sn=" + orderSn;
		}
		Log.logInfo(URLCommand);
		Map<String, String> res = json2map(get(URLCommand).get("Response"));
		Log.logInfo(res);
		if (!"200".equals(res.get("_resultcode"))) {
			throw new Exception("update ordersn (" + orderSn + ") failed!(" + res + ")(" + DomainName + ")");
		}
		Log.logInfo("修改订单：(" + orderSn + ")为" + orderStatus + ",域名(" + DomainName + ").");
	}

	/**
	 * 
	 * @测试点: 打印测试方法
	 * @验证点: 监听成功或者失败，会打印测试用例的说明
	 * @param methodname
	 *            打印的方法名
	 * @param map
	 *            打印的map @备注： void
	 * @author zhangjun
	 * @date 2017年7月17日
	 * @修改说明
	 */
	public static void printTestCase(String methodname, HashMap<String, Map<String, String>> map) {
		Map<String, String> cases = map.get(methodname);
		try {
			if (cases.get("item") != null) {
				Log.logInfo("测试项:" + cases.get("item"));
				Log.logInfo("测试步骤:" + cases.get("teststep"));
				Log.logInfo("验证点:" + cases.get("verification_content"));
			} else {
				Log.logInfo("测试项:" + cases.get("case_name"));
				Log.logInfo("测试步骤:" + cases.get("case_step"));
				Log.logInfo("验证点:" + cases.get("case_check"));
			}
		} catch (Exception e) {
			Log.logInfo("没有获取到测试用例，请确认方法名：" + methodname + "，的测试用例是否已导入到数据库中");
		}

	}

	/**
	 * 
	 * @测试点: 检查关联性
	 * @验证点: 检查APP的关联性 @备注： void
	 * @author zhangjun
	 * @date 2017年8月1日
	 * @修改说明
	 */
	public static void checkStatusAPP() {
		if (StartApp.correFail) {
			Log.logError("测试脚本关联失败.");
		}
	}

	/**
	 * 
	 * @测试点: 检查关联性
	 * @验证点: 检查wap的关联性 @备注： void
	 * @author zhangjun
	 * @date 2017年8月1日
	 * @修改说明
	 */
	public static void checkStatusPhoneBrowser() {
		if (StartPhoneBrowser.correFail) {
			Log.logError("测试脚本关联失败.");
		}

	}

	/**
	 * 
	 * @测试点: 检查关联性
	 * @验证点: 检查PC的关联性 @备注： void
	 * @author zhangjun
	 * @date 2017年8月1日
	 * @修改说明
	 */
	public static void checkStatusBrowser() {
		if (Startbrowser.correFail) {
			Log.logError("测试脚本关联失败.");

		}
	}

	/**
	 * 获取运行所使用的浏览器信息
	 * 
	 * @param jse
	 *            JavascriptExecutor对象
	 * @return 浏览器信息，如版本等
	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
	 */
	public static String getBrowserInfo(JavascriptExecutor jse) {
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
	 * 
	 * @测试点: 读取文本文件，读取行数据 ,返回的是一个list数据
	 * @param @return
	 * 			@备注： String
	 * @author zhangjun
	 * @date 2017年10月5日
	 * @修改说明
	 */
	@SuppressWarnings("unchecked")
	public static List<String> readFilebyList(String url) throws IOException {
		ArrayList numList = null;
		File file = new File(url);
		BufferedReader reader = null;
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		try {
			numList = new ArrayList();
			reader = new BufferedReader(new FileReader(file));
			String tempString;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("读出的内容为:" + tempString);
				numList.add(tempString);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return numList;

	}

	/**
	 * 
	 * @测试点: 通过项目名获取项目code，和运行地址，等其他内容，
	 * @验证点: TODO(这里用一句话描述这个方法的作用)
	 * @param @param
	 *            projectName
	 * @param @return
	 * 			@备注： Map<String,String>
	 * @author zhangjun
	 * @date 2018年4月4日
	 * @修改说明
	 */
	public static Map<String, String> getProjectId(String projectName) {
		String keys = null;
		Map<String, String> getIDcode = new HashMap<String, String>();
		// 根据项目的名称获取id
		DBhandle handle = new DBhandle("config/jdbcui.properties");
		Map<String, String> getIdMap = new HashMap<String, String>();
		String sql = "select auto_caseRunEnv.id,auto_projectManage.project_code,auto_caseRunEnv.create_people,auto_caseRunEnv.envdata_key,auto_caseRunEnv.envdata_value from  auto_projectManage left join auto_caseRunEnv  on auto_projectManage.id=auto_caseRunEnv.project_id "
				+ "where   auto_projectManage.project_name='" + projectName + "'  and auto_caseRunEnv.isStart=1";
		@SuppressWarnings("rawtypes")
		List querylist = handle.query(sql);
		// 如果启用相同，获取最后一个数据
		getIdMap = (Map<String, String>) querylist.get(querylist.size() - 1);
		return getIdMap;
	}

	/**
	 * 
	 * @测试点: 通过项目名获取项目code，和运行地址，等其他内容，
	 * @验证点: 根据创建人读取对应的数据
	 * @param projectName
	 *            项目名称
	 * @param operatingPersonnel
	 *            创建人
	 * 
	 * @param @return
	 * 			@备注： Map<String,String>
	 * @author zhangjun
	 * @date 2018年4月4日
	 * @修改说明
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getProjectId(String projectName, String operatingPersonnel) {
		String keys = null;

		Map<String, String> getIDcode = new HashMap<String, String>();
		Map<String, String> m = new HashMap<String, String>();
		// 根据项目的名称获取id
		DBhandle handle = new DBhandle("config/jdbcui.properties");
		Map<String, String> getIdMap = new HashMap<String, String>();
		String sql = "select auto_caseRunEnv.id,auto_projectManage.project_code,auto_caseRunEnv.create_people,auto_caseRunEnv.envdata_key,auto_caseRunEnv.envdata_value from  auto_projectManage left join auto_caseRunEnv  on auto_projectManage.id=auto_caseRunEnv.project_id "
				+ "where   auto_projectManage.project_name='" + projectName + "'  and auto_caseRunEnv.isStart=1";
		List querylist = handle.query(sql);
		// 如果启用相同，获取最后一个数据
		for (int i = 0; i < querylist.size(); i++) {
			m = (Map<String, String>) querylist.get(i);
			if (operatingPersonnel.equals(m.get("create_people"))) {
				getIdMap = (Map<String, String>) querylist.get(i);
				return getIdMap;
			}
		}

		getIdMap = (Map<String, String>) querylist.get(querylist.size() - 1);

		return getIdMap;
	}

	/**
	 * 
	 * @测试点: 获取配置内容项
	 * @验证点: TODO(这里用一句话描述这个方法的作用)
	 * @param @备注：
	 *            void
	 * @author zhangjun
	 * @return
	 * @date 2018年4月4日
	 * @修改说明
	 */
	public static Map<String, String> getExecutionAccount(String projectName) {
		Map<String, String> getcaseRunConfigur = Pub.getProjectId(projectName);
		Map<String, String> environmentmap = Pub.getConfiguration(getcaseRunConfigur.get("id"));
		return environmentmap;
	}

	public static Map<String, String> getExecutionAccount(String projectName, String operatingPersonnel) {
		Map<String, String> getcaseRunConfigur = Pub.getProjectId(projectName, operatingPersonnel);
		Map<String, String> environmentmap = Pub.getConfiguration(getcaseRunConfigur.get("id"));
		return environmentmap;
	}

	/**
	 * 
	 * @测试点: 通过获取的项目id，对应获取环境配置数据
	 * @验证点: TODO(这里用一句话描述这个方法的作用)
	 * @param @param
	 *            keys
	 * @param @return
	 * 			@备注： Map<String,String>
	 * @author zhangjun
	 * @date 2018年4月3日
	 * @修改说明
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getConfiguration(String keys) {
		// 通过id对应到配置的测试数据
		Map<String, String> Environment = new HashMap<String, String>();
		DBhandle handle2 = new DBhandle("config/jdbcui.properties");
		String configura = "select * from auto_caseEnvInfo  where caseRunEvn_id='" + keys + "'";
		@SuppressWarnings("unused")
		List querylist2 = handle2.query(configura);
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < querylist2.size(); i++) {
			m = (Map<String, String>) querylist2.get(i);
			Environment.put(m.get("env_key"), m.get("env_value"));
		}
		return Environment;

	}
}

class RetryException extends Exception {
	private static final long serialVersionUID = 1L;

	public RetryException() {
		Log.logInfo("Exception occured while retrying.");
	}
}
