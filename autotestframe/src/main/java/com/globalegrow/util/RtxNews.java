package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送rtx消息
 * @author linchaojiang
 * 创建时间:2017-02-20,更新时间:2017-02-20
 *
 */
public class RtxNews {
			
	/**
	 * 发送post请求
	 * @param str
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-21,更新时间:2017-02-21
	 */
	public static void TestPost(String str) throws IOException{          
        URL url;
        //url = new URL("http://branched.msgcenter100.com/api-source/index");
        url = new URL("http://msgcenter100.com/api-source/index"); 
		URLConnection connection = url.openConnection();  
        connection.setDoOutput(true);  
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");  
        out.write(str); // 向页面传递数据。post的关键所在！  
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
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(  
                l_urlStream));  
        while ((sCurrentLine = l_reader.readLine()) != null) {  
            sTotalString += sCurrentLine + "\r\n";  
  
        }  
        System.out.println(sTotalString); 
         
    } 
	
	/**
	 * 发送rtx消息
	 * @author linchaojiang
	 * 创建时间:2017-02-20,更新时间:2017-02-20
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("rawtypes")
	public static void sendRtx(String projectName,String content,String rtxid) throws UnsupportedEncodingException{

        Map<String, Object> bodyAll = new LinkedHashMap<String, Object>();
       /* bodyAll.put("account", "autotestuser01");
        bodyAll.put("password", "autotestpwd01");
        bodyAll.put("api_key","thdeHUoGzuApBrY984XX");*/
        bodyAll.put("account", "chd");
        bodyAll.put("password", "123456");
        bodyAll.put("api_key","mFjUeqcCCNJ3o8yQU3uE");
        
        Map<String, Object> rtx = new LinkedHashMap<String, Object>();
        rtx.put("channel", "rtx");
        rtx.put("to", rtxid);
        rtx.put("title",projectName+"自动化测试结果");
        rtx.put("content",content);
        
        List<Map> list = new ArrayList<>();
        list.add(rtx);
        bodyAll.put("data", list);
        
        try {
			TestPost(PHPURLBuilder.httpBuildQuery(bodyAll, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			Log.logInfo("发送rtx消息失败！！！！");
		}
	}
		
	/**
	 * test
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		sendRtx("sammydress","test","10537,10537");			
	}
	
}
