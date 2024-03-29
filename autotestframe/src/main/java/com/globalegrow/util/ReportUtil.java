package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;

import com.globalegrow.base.TestngListener2;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReportUtil {
		   
	static FileOutputStream outputStream = null;
	static String projectPath=System.getProperty("user.dir");
	static String projectName = TestngListener2.getProjectName();
	static Properties props=System.getProperties();
    
	 /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public static List<String> readTxtFile(String filePath){
    	
    	List<String> testResult = new ArrayList<String>();
    	List<String> testResultList = new ArrayList<String>();
        try {
                String encoding="UTF-8";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	testResult.add(lineTxt);
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
		try {
			testResultList = listSort(testResult);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return testResultList;   
    }
    
    /**
     * 结果分类
     * @param list
     * @return
     * @author linchaojiang,创建时间:2016-11-02
     */
    public static Map<String, List<String>> resultClassliy(List<String> list){
    	
    	List<String> sumList = new ArrayList<String>();//onStart和onFinish收集的信息
    	List<String> testCase = new ArrayList<String>(); //执行成功和失败的用例信息
    	List<String> skippedList = new ArrayList<String>(); //跳过执行的用例
    	List<String> otherList = new ArrayList<String>(); //其他信息
    	Map<String, List<String>> resultList = new HashMap<String,List<String>>();
    	for(int i=0;i<list.size();i++){
    		String[] result = list.get(i).split(";");
    		if(result[4].equals("Success")||result[4].equals("Failure")||result[4].equals("Skipped")){
    			testCase.add(list.get(i));
    		}else if (result[4].equals("Skipped")) {
				skippedList.add(list.get(i));
			}else if (result[4].equals("onStart")||result[4].equals("onFinish")){
				sumList.add(list.get(i));
			}else {
				otherList.add(list.get(i));
			}
    	}
    	
    	/**
    	 * 获取模块分类
    	 */
    	List<String> testItemList = new ArrayList<String>(); 
    	for(int i=0;i<testCase.size();i++){
    		String litem = testCase.get(i);
    		String[] litems = litem.split(";");
    		testItemList.add(litems[2]);
    	}  	
    	List<String> ItemListfl = new ArrayList<String>(); 
    	ItemListfl = removeList(testItemList); //获取模块分类,去重
    	
    	resultList.put("sumList", sumList);
    	resultList.put("testCase", testCase);
    	resultList.put("skippedList", skippedList);
    	resultList.put("otherList", otherList);
    	resultList.put("ItemListfl", ItemListfl);
		return resultList;  	
    }
    
    /**
     * list去重
     * @param li
     * @return
     * @author linchaojiang,创建时间:2016-11-02
     */
    public static List<String> removeList(List<String> li){  	
    	List<String> list = new ArrayList<String>();   	
    	for(int i=0;i<li.size();i++){    		
    		if(i==0){
    			list.add(li.get(i));
    			continue;
    		}else {
    			String str = li.get(i);
    			if(!list.contains(str)){
        			list.add(str);
        		}
			}   		
    	}
		return list;    	
    }
    
    /**
     * String[]数组去重
     * @param str
     * @return
     * @author linchaojiang,创建时间:2016-11-02
     */
    public static String[] removeString(String[] str){  
        Set<String> set = new HashSet<>();  
        for(int i=0;i<str.length;i++){  
            set.add(str[i]);  
        }  
        String[] arrayResult = (String[]) set.toArray(new String[set.size()]);
		return arrayResult;  
    }
    
    /**
     * 把txt中成功和失败的测试用例文本处理成二维数组
     * @param list
     * @return
     * @author linchaojiang,创建时间:2016-11-02
     */
    public static String[][] readResult(List<String> list){
    	    	
    	String[][] testlist = new String[list.size()][4];
    	
    	for(int i =0;i<list.size();i++){
    		String[] ss = list.get(i).split(";");
    		for(int j=0;j<ss.length;j++){
    			testlist[i][j] = ss[j];
    		} 		
    	}    	
    	for(int i =0;i<testlist.length;i++){
    		for(int j=0;j<testlist[i].length;j++){
    			System.out.println(testlist[i][j]);
    		}
    	}
		return testlist;   	
    }
              
    /**
     * 创建HTML文件
     * @param msg
     * @param fileName
     * @param path
     * @throws IOException
     */
	public static void htmlReport(StringBuffer msg,String fileName,String path) throws IOException{
		File file = new File(path);
		File file2 = new File(path+fileName);
		if(!file.exists()){//如果文件夹不存在，创建文件夹
			try {
				file .mkdir();
				if(!file2.exists()){//如果文件不存在，创建文件					
					file2.createNewFile();//创建文件
				}
				//file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			if(!file2.exists()){//如果文件不存在，创建文件					
				file2.createNewFile();//创建文件
			}
		}
		//把字符串写入文件
		try {
			outputStream = new FileOutputStream(file2,true);
			try {
				outputStream.write(msg.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			outputStream.close();	
		}	
	}	
    
	/**
	 * 计算时间差
	 * @param startTime
	 * @param endTime
	 * @param format
	 */
	public static String dateDiff(String startTime, String endTime, String format) {
		//按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数
		//获得两个时间的毫秒时间差异
		long diff = 0;
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		long min = diff%nd%nh/nm;//计算差多少分钟
		long sec = diff%nd%nh%nm/ns;//计算差多少秒
		String dateString = "";
		if(day==0){
		    if(hour==0){
		    	if(min==0){
		    		if(sec==0){	
		    			dateString = "0秒";
		    		}else {
		    			dateString = sec+"秒";
					}
		    	}else {
		    		dateString = min+"分钟"+sec+"秒";
				}
		    }else {
		    	dateString = hour+"小时"+min+"分钟"+sec+"秒";
			}	
		}else{
			dateString = day+"天"+hour+"小时"+min+"分钟"+sec+"秒";
		}
		return dateString;		
	}
	
	/** 
     * 新建目录  
     * @param path 文件路径 
     * @throws Exception
     * @author linchaojiang 
     */  
    public static void makeDirs(String path) {  
        if (StringUtils.isEmpty(path)) {  
            return;  
        }  
        try {    
            File f = new File(path);  // 获得文件对象  
            if (!f.exists()) {                
                f.mkdirs(); // 如果路径不存在,则创建  
            }  
        } catch (Exception e) {  
            Log.logInfo("创建目录错误.path=" + path);  
            throw e;  
        }  
    }
    
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     * @author linchaojiang
     */  
    public static boolean deleteFile(String sPath) {  
        boolean flag = false;
        try {
        	File file = new File(sPath);  
            // 路径为文件且不为空则进行删除  
            if (file.isFile() && file.exists()) {  
                file.delete();  
                flag = true;  
            }else {
            	flag = true;
 			}  
 		} catch (Exception e) {
 			flag = false;
 			e.printStackTrace();
 			Log.logInfo("删除文件异常！！！");
 		}       
        return flag;  
    } 
    
  
	/**
	 * 读取excle文件，返回map对象
	 * @author linchaojiang
	 * 创建时间:2016-08-30
	 * 更新时间:2016-08-30
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Map<String, List> readTestCase(String className) {

		Map<String, List> map = new HashMap<String, List>();

		List<String[]> list = new ArrayList<String[]>();

		Workbook rwb = null;
		Cell cell = null;

		InputStream stream;
		String proName = "";
		try {
			//stream = new FileInputStream(Startbrowser.resourcesPath + Startbrowser.projectName + ".xls");
			String pm = projectName.substring(projectName.length()-1,projectName.length());			
			if(pm.equals("w")||pm.equals("a")||pm.equals("i")){
				proName = projectName.substring(0,projectName.length()-1);
			}else {
				proName = projectName;
			}			
			String filePath1 = projectPath+"/testCase/"+projectName+"/"+projectName+".xls";
			String filePath2 = projectPath+"/testCase/"+proName+"/"+projectName+".xls";
			String testCasePath = "";
			File file1 = new File(filePath1);
			File file2 = new File(filePath2);
			if(file1.exists()){
				testCasePath = filePath1;
			}else{
				if(file2.exists()){
					testCasePath = filePath2;
				}else {
					Log.logError("没有找到文件,文件路径:"+filePath2+"出错！");
				}
			}
			stream = new FileInputStream(testCasePath);
			rwb = Workbook.getWorkbook(stream);
		} catch (FileNotFoundException e) {
			Log.logError("没有找到文件,文件路径:"+projectPath+"/testCase/"+proName+"/"+projectName+".xls出错！");
			e.printStackTrace();
		} catch (BiffException e) {
			Log.logError("读取excel出现异常，请检测名称是否对应正确或其他异常！！！");
			e.printStackTrace();
		} catch (IOException e) {
			Log.logError("读取excel出现异常，出现空指针,请检测字段正确性！！！");
			e.printStackTrace();
		}
		Sheet sheet = rwb.getSheet(className);
		int rows = sheet.getRows();
		int coumn = sheet.getColumns();

		String[] strkey = new String[rows - 1];// 存取testCase的值

		for (int i = 1; i < rows; i++) {
			String[] strValue = new String[coumn - 1];// 存取每一行的数据
			strkey[i - 1] = sheet.getCell(0, i).getContents().trim();
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
	 * 获取运行所使用的浏览器信息
	 * @param jse JavascriptExecutor对象
	 * @return 浏览器信息，如版本等
	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
	 */
	public static String getBrowserInfo(JavascriptExecutor jse) {
		String JS_getBrowserInfo = "var browser_info = window.navigator.userAgent;return browser_info;";
		String res = "";
		try {
			res = (String) (jse).executeScript(JS_getBrowserInfo);
		} catch (Exception e) {
			Log.logInfo("无法获取到浏览器的详细信息！！！");
			res = "";
			//e.printStackTrace();
		}
		return res;
	}

	/**
	 * 获取本地的IP和域名
	 * @return [] [0]是IP地址，[1]是域名
	 * @author yuyang ,创建时间：2016-09-07,更新时间：2016-09-07
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
	 * 根据不同的类型和长度获取随机字符串//0：字符加数字，1：纯字符串，2：纯数字，3：纯小写字母，4：纯大写字母
	 * @param length
	 * @param type
	 * @return String
	 * @author linchaojiang
	 */
	public static String getRandomString(int length,int type){ 
        Random random = new Random();  
        StringBuffer randomstr = new StringBuffer();  
        String str=""; 
        int number = 0;       
        for(int i = 0 ; i < length; ++i){ 
        	if(type==0){
            	str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
            	number = random.nextInt(62);
            }else if (type==1) {
            	str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
            	number = random.nextInt(52);
    		}else if (type==2) {
    			str="0123456789";
    			number = random.nextInt(10);
    		}else if (type==3) {
    			str="abcdefghijklmnopqrstuvwxyz";
    			number = random.nextInt(26);
    		}else if (type==4) {
    			str="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    			number = random.nextInt(26);
    		}else{
    			Log.logInfo("传入的type类型有误，0：字符加数字，1：纯字符串，2：纯数字，3：纯小写字母，4：纯大写字母");
    		}  
            randomstr.append(str.charAt(number));  
        }
        return randomstr.toString();
    }
	
    /**
     * 获取环境变量的值
     * @param variableStr
     * @return
     * @author linchaojiang
     * 创建时间:2017-01-02,最后更新时间:2017-01-02
     */
    public static String getEnvPath(String variableStr){
    	
    	Map<?, ?> m = System.getenv();
    	String key = "";
        String value = "";
    	for (Iterator<?> it = m.keySet().iterator(); it.hasNext();){
           key = (String ) it.next();           
           if(key.equals(variableStr)){
        	   value = (String ) m.get(key);
        	   break;
           }
	    }
		return value;
    }
	
    /**
     * 复制jenkins_home下对应的job下图片到tomcat部署的autotest的web应用下
     * @param jobName
     * @param suiteName
     * @author linchaojiang
     * 创建时间:2017-01-05,最后更新时间:2017-01-05
     */
    public static void copyImage(String jobName,String suiteName){
    	
    	/*String osName = props.getProperty("os.name");//操作系统名称
    	String jenkins_home =  ReportUtil.getEnvPath("JENKINS_HOME");
    	if(osName.contains("Windows")){
    		String imagePath = jenkins_home+"\\workspace\\"+jobName+"\\test-output\\html-report\\"+suiteName+"\\screenShot";
    		String targetPath = "C:\\tomcat\\apache-tomcat-7.0.70-windows-x64\\apache-tomcat-7.0.70\\webapps\\autotest\\screenShot";
        	Log.logInfo("复制图片路径 :"+imagePath);
    		Log.logInfo("复制图片目标路径:"+targetPath);
    		String runcmd = "cmd /c xcopy /d/e/c/i/h/r "+imagePath+"  "+targetPath+" /s";
    	    Cmd.runCommand(runcmd);
    	}else if (osName.contains("Linux")) {
    		String imagePath = jenkins_home+"/jobs/"+jobName+"/htmlreports/HTML_Report/screenShot";
    		String targetPath = "/usr/local/tomcat/webapps/autotest/";
    		Log.logInfo("复制图片路径 :"+imagePath);
    		Log.logInfo("复制图片目标路径:"+targetPath);
    		String shell = "cp -rf "+imagePath+" "+targetPath;
    		Cmd.runShell(shell);
		}*/    	
    	try {
    		String jenkins_home = "/opt/jenkins_workspace/jenkins_home";
    		String imagePath = jenkins_home+"/jobs/"+jobName+"/htmlreports/HTML_Report/screenShot";
    		String targetPath = "/usr/local/tomcat/webapps/autotest/";
    		Log.logInfo("复制图片路径 :"+imagePath);
    		Log.logInfo("复制图片目标路径:"+targetPath);
    		String shell = "cp -rf "+imagePath+" "+targetPath;
    		Log.logInfo("执行命令:"+shell);
			Cmd.connect(); 
			Cmd.execCommand(shell);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			Cmd.conn.close();
		}   	
    }
    
	/**
	 * 传入字符串数组,通过冒泡排序法,获取最大日期
	 * @param list
	 * @return String
	 * @throws ParseException
	 * @author linchaojiang
	 * 创建日期:2017-03-09,最后更新日期:2017-03-09
	 */
	public static List<String> listSort(List<String> list) throws ParseException{
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    int n = list.size();   	  
	    for (int i = 0; i < n - 1; i++) {
	    	for (int j = 0; j < n - 1; j++) { 	  
	    		Date date1 = format.parse(list.get(j).split(";")[0]);
	    		Date date2 = format.parse(list.get(j+1).split(";")[0]);
	    		if(date1.getTime()>date2.getTime()){
	    			String temp = list.get(j);
	    			list.set(j, list.get(j+1));
	    			list.set(j+1, temp);
	    		} 	  
	    	}   	  
	    }   
	    return list;
	}
   
   /**
    * test
    * @param argv
    */
   public static void main(String argv[]){
    	
    	
   }
   	
}

