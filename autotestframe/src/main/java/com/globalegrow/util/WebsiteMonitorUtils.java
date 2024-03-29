package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;




public class WebsiteMonitorUtils {
	
	/**
	 * 发送post请求,并带上cookie
	 * @param reqData:请求参数,reqUrl:请求url,cookies:请求cookie
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-21,更新时间:2017-02-21
	 * @return String
	 */
	public static String sentPost(String reqData, String reqUrl) throws IOException {

		URL url;
		url = new URL(reqUrl);
		URLConnection connection = url.openConnection();
		//connection.setRequestProperty("Content-type", "application/json; charset=utf-8"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		System.out.println("reqData:"+reqData);
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
	
	public static String GetMD5String(String str) {
		String res = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes("UTF-8"));
			res = toHex(messageDigest.digest());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 将16位byte[] 转换为32位String,用于GetMD5String()
	 * @param buffer 16位的byte[]
	 * @return 32位的字符串
	 * @author yuyang
	 * 创建时间：2016-09-02  更新时间:2016-12-06
	 * 更新说明(2016-12-06)：by yuyang,由PublicFun移动到Pub
	 */
	private static String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}

		return sb.toString();
	}
	
	
	public static void getMonitor(String projectcode,String pointCode,
			String errorCode,String warnNo,boolean result,String result_msg){
		
		Log.logInfo("project_code:"+projectcode);
		Log.logInfo("point_code:"+pointCode);
		Log.logInfo("error_code:"+errorCode);
		Log.logInfo("sourceId:"+warnNo);
		
		JSONArray json = new JSONArray();
		
		JSONObject jo = new JSONObject();
		try {
			jo.put("project_code",projectcode.trim());
			jo.put("point_code", pointCode.trim());
		    jo.put("error_code", errorCode.trim());
		 	jo.put("sourceId", warnNo.trim());
		 	jo.put("result_stauts", result);
			jo.put("result_msg", result_msg);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		json.add(jo);
			
		Log.logInfo(json.toString());
		//监控那边的加密MD5
		String appKey ="2c14128c383ac8525d72cea30c37013e";
		String sign=GetMD5String(appKey+json.toString());
		//  System.out.println(URLEncoder.encode(json.toString()));
		Log.logInfo("MD5:"+sign);
		
		//发送post请求
		// url1为获取cookie的url,url2为需要带上url1中cookie执行的post请求url
		//测试域名： http://rms110.com.a.s1cg.egomsl.com
		String url1 = "http://www.rms110.com/api-qc/get-auto-test-result";
		/*Map<String, Object> bodyAll = new LinkedHashMap<String, Object>();
		bodyAll.put("sign", sign);
		bodyAll.put("jsonData", json.toString());*/
		
		String bodyAll1 = "sign="+sign+"&jsonData="+json;
		System.out.println("bodyAll1:"+bodyAll1);
		try {
			//String result=sentPost(PHPURLBuilder.httpBuildQuery(bodyAll, "UTF-8"), url1);
			String results=sentPost(bodyAll1, url1);
			Log.logInfo(results);
		} catch (Exception e) {
			e.printStackTrace();				
		}		

	}
	
	  
    public static void insertSql(String projectcode,String pointCode,String errorCode,
    		String warnNo,boolean result,String result_msg){
    	SimpleDateFormat sdf2 = new SimpleDateFormat("YYYYMMddHHmmssSSS"); 
    	Date date2=new Date();
    	String id=Pub.getRandomString(32-sdf2.format(date2).toString().length())+sdf2.format(date2);
    	Log.logInfo("id的长度:"+id.length());
    	Date date=new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); 
    	String sql="insert into auto_monitorrunresult values('"+id+"','"+projectcode+"','"+pointCode+"','"+errorCode+"','"+warnNo
    			+"',"+result+",'"+result_msg+"','"+sdf.format(date)+"')";
    	Log.logInfo(sql);
    	DBhandle db=new DBhandle();
    	db.insert(sql);
    	Log.logInfo("写入数据成功！");
    }
}
