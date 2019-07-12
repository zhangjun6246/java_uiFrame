package com.globalegrow.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.bcel.generic.DREM;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.globalegrow.util.DBhandle;
import com.globalegrow.util.HtmlReport;
import com.globalegrow.util.Log;
import com.globalegrow.util.News;
import com.globalegrow.util.Op;
import com.globalegrow.util.Pub;
import com.globalegrow.util.ReportUtil;
import com.globalegrow.util.ScreenRecord;

public class TestngListener2  extends TestListenerAdapter{
	WebDriver driver;
	Startbrowser startbrowser = null;
	StartPhoneBrowser startPhoneBrowser = null;
	static String browserName = "";//拼接前浏览器
	String browserName1 = "";//拼接后的浏览器
	String imgName = "";
	FileOutputStream outputStream = null;
	StringBuffer msg = new StringBuffer();
	Properties props=System.getProperties();
	String projectPath=System.getProperty("user.dir");
	static String projectName = "";
	static List<String> modeName = new ArrayList<>();
	public static String test_id = "";
	String runTestStartTime = "1700-01-01 00:00:00";
	String runTestFinishTime = "2050-01-01 00:00:00";
	static HashMap<String, List<String>> repmap = new HashMap<String, List<String>>();
	public static HashMap<String, String> modeTime = new HashMap<String, String>();
	static HashMap<String, String> headmap = new HashMap<String, String>();
	static HashMap<String,Map<String, String >> testcasemap = new HashMap<String,Map<String, String >>();
	Map<String, String> environmentmap = new HashMap<String, String>();//原testng中参数
	private Process process;
	private String module;
	private String picDateSign;
	private String testName;
	private String env;//所在环境，可能后期会去掉
	private String tastjob;// 项目code
	private String projectid;// 项目id
	private String userid;// 运行人员
	private static String writeindb;// 是否写入到数据读
	private static String testurl;//项目的测试地址
	
	public static boolean platformFlag = false;//用于判断是否需要录制视频
	public static String projectCode = "";
	public static StringBuffer logStr = new StringBuffer();
	String logPath = "";
	
	public String getWriteindb() {
		return writeindb;
	}

	public static void setWriteindb(String writeindb) {
		TestngListener2.writeindb = writeindb;
	}
	
	public static void setBrowserName(String browserName) {
		TestngListener2.browserName = browserName;
	}

	public static String getBrowserName() {
		return browserName;
	}

	public static String getTesturl() {
		return testurl;
	}

	public static void setTesturl(String testurl) {

		TestngListener2.testurl = testurl;
	}
	
	
	
	public static String getProjectCode() {
		return projectCode;
	}

	public static void setProjectCode(String projectCode) {
		TestngListener2.projectCode = projectCode;
	}
	public static String getProjectName() {
		return projectName;
	}

	public static void setProjectName(String projectName) {
		TestngListener2.projectName = projectName;
	}

	protected static JavascriptExecutor jse = null;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	@Override
	public void onStart(ITestContext testContext) {
		super.onStart(testContext);
		projectName = testContext.getSuite().getName();// 获取项目名称
		browserName = testContext.getSuite().getParameter("browserName");// 获取浏览器名称
		tastjob = testContext.getSuite().getParameter("tastjob");// Jenkins的job
		projectid = testContext.getSuite().getParameter("id");// Jenkins的id
		userid = testContext.getSuite().getParameter("userid");// 运行人员，代码
		if(userid.contains("$")){
			Log.logInfo("使用的本地运行，useid为:"+userid);
		}else {
			Log.logInfo("使用的平台运行,useid为:"+userid);
		}
	
				
		
		if (browserName != null) {// 使用的是以前框架
			browserName = testContext.getSuite().getParameter("browserName");// 获取浏览器名称
			projectCode = testContext.getSuite().getParameter("project_code");// 项目code
			env = testContext.getSuite().getParameter("env");
			writeindb = testContext.getSuite().getParameter("writeindb");
		} else {
			Log.logInfo("读取的平台数据");
			Map<String, String>  getcaseRunConfigur=Pub.getProjectId(projectName);
			Map<String, String> 	environmentmap=Pub.getConfiguration(getcaseRunConfigur.get("id"));
			projectCode = getcaseRunConfigur.get("project_code").trim();// 获取project_code
			testurl = getcaseRunConfigur.get("envdata_value").trim();//获取的testurl
			browserName = environmentmap.get("browserName").trim();	//获取浏览器 		
			writeindb = environmentmap.get("writeindb").trim();//获取是否写入到数据中
	
		}

		if(projectCode.contains("android")){
			platformFlag = true;
		}
		String osName = props.getProperty("os.name");//操作系统名称	
		Date startDate = testContext.getStartDate();//测试执行时间
		
		msg.append(formatter.format(startDate)+";"+formatter.format(startDate)+";"+testContext.getClass().getName()+";"+testContext.getName()+";onStart;图片名称\r\n");
		msg.append(formatter.format(startDate)+";"+formatter.format(startDate)+";"+"操作系统"+";"+osName+";onStart;图片名称\r\n");
		msg.append(formatter.format(startDate)+";"+formatter.format(startDate)+";"+"浏览器"+";"+browserName+";onStart;图片名称\r\n");		
		Log.logInfo("...Start to run Suite(" + testContext.getSuite().getName() + ")...\r\n");			
		
		headmap.put("os", osName);
		headmap.put("environment", browserName);
		headmap.put("project_code", projectCode);
		headmap.put("env", env);
		headmap.put("job", tastjob);
		headmap.put("task_id", projectid);
		headmap.put("run_person", userid);
		
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentTime = new Date();
		picDateSign = formatter1.format(currentTime);
		logPath = projectPath + "/test-output/html-report/"+projectName+"/log/";
		
		//读取数据库 
		String sql = null;
		DBhandle handle = null;
		String[] code = projectCode.split("-");// 切割code
		if (browserName.contains("platform")||projectCode.contains("platform")) {// 读取数据库
			handle = new DBhandle("config/jdbcui.properties");
			sql = "select * from  auto_testcase where project_id  in (select id from auto_projectManage where project_name='"
					+ code[0] + "')";
		} else {
			handle = new DBhandle();
			sql = "select  *  from auto_testcase where project_code=" + "'" + projectCode + "'" + "or project_code="
					+ "'" + code[0] + '-' + code[1] + "'";// 分割项目兼容prd和pc
		}	
		@SuppressWarnings("rawtypes")
		List  querylist=handle.query(sql);
		Map<String, String> m = new HashMap<String, String>();
	    String name = null;//获取方法名
	    for(int i=0;i<querylist.size();i++){
	    	 m=(Map<String, String>) querylist.get(i);
	    	 @SuppressWarnings("rawtypes")
			Set keySet = m.keySet(); // key的set集合  
	         Iterator<String> it = keySet.iterator();  
	         while(it.hasNext()){  
	              Object  k = it.next(); // key       
	             name=m.get("testname");//存放key
	         	if(name==null){//使用的平台数据
					name=m.get("case_code");
				}
	         } 
	         testcasemap.put(name, m);//存入键和值
	    }
		
	}
	
	@Override
	public void onTestStart(ITestResult tr) {
		super.onTestStart(tr);
		Log.cleanLog();//清除日志
		test_id = Long.toString(System.currentTimeMillis())+ReportUtil.getRandomString(5, 2);
		Date date = new Date();	
		String modeStr = tr.getInstanceName().split("\\.")[tr.getInstanceName().split("\\.").length-1];
		if(!modeName.contains(modeStr)){
			modeName.add(modeStr);
			modeTime.put(tr.getInstanceName(), formatter.format(date));
		}
		runTestStartTime = formatter.format(date);
		
		if(modeStr.contains("_A_")){
			browserName1 = browserName+" &lt;&lt;&lt;";
		}
		//获取浏览器详细信息
		if(browserName1.equals("")){
			Object obj = tr.getInstance();//获取运行对象实例		
			driver = (WebDriver)getFieldValueByName("driver",obj);//获取对象实例属性的值
			jse = (JavascriptExecutor) driver;		
			browserName1 = browserName+" &lt;&lt;&lt;"+ReportUtil.getBrowserInfo(jse);
		}
		//录制视频
		if(platformFlag){
			try{
				module = tr.getInstanceName().split("\\.")[tr.getInstanceName().split("\\.").length-1]; 
				testName = tr.getName();
				//开始录制视频
				TimeUnit.SECONDS.sleep(1);
				process = ScreenRecord.startScreenRecord(projectName,testName,module,picDateSign);
			}catch(Exception e){
				e.printStackTrace();
				Log.logInfo("开始录制视频失败，继续执行");
			}
		}	
		Log.logInfo("...Start to run test(" + tr.getName() + ")...");
		
		if(!repmap.containsKey(tr.getName())){//把测试数据写入map中			
			List<String> testl = new ArrayList<String>();
			String modestr = tr.getInstanceName().split("\\.")[tr.getInstanceName().split("\\.").length-1]+picDateSign;
			testl.add(0, formatter.format(date));
			testl.add(1, formatter.format(date));
			testl.add(2, tr.getInstanceName());
			testl.add(3, tr.getName());
			testl.add(4, "onTestStart");
			testl.add(5, "图片名称");
			testl.add(6, test_id);
			testl.add(7, modestr);
			repmap.put(tr.getName(), testl);			
		}else{
			repmap.get(tr.getName()).set(0, formatter.format(date));
			repmap.get(tr.getName()).set(1, formatter.format(date));
		}	
	}
	
	@Override
	public void onTestFailure(ITestResult tr){
		super.onTestStart(tr);
		Pub.printTestCase(tr.getName(),testcasemap);
		Date date = new Date();
		Object obj = tr.getInstance();//获取运行对象实例		
		driver = (WebDriver)getFieldValueByName("driver",obj);//获取对象实例属性的值
		String captureScreenPathName = captureScreen("test-output/html-report/"+projectName+"/screenShot",tr.getName()+"_fail",driver);
		imgName = captureScreenPathName.split("/")[captureScreenPathName.split("/").length-1];
		Log.logInfo("(" + tr.getName() + ")Test Result...Failure\r\n");	
		Log.writeLogToFile(logPath);//把日志写入日志
		if(platformFlag){
			//结束录制视频
			ScreenRecord.endScreenRecord(process,"fail");
		}		
		Startbrowser.correFail = true;
		StartPhoneBrowser.correFail = true;
		StartApp.correFail = true;
		runTestFinishTime = formatter.format(date);
		if(repmap.containsKey(tr.getName())){
			repmap.get(tr.getName()).set(1, formatter.format(date));
			repmap.get(tr.getName()).set(4, "Failure");
			repmap.get(tr.getName()).set(5, imgName);
		}
	}
	

	@Override
	public void onTestSuccess(ITestResult tr) {
		super.onTestSuccess(tr);	
		Pub.printTestCase(tr.getName(),testcasemap);
		Date date = new Date();	
		Log.logInfo("(" + tr.getName() + ")Test Result...Success\r\n");
		Log.writeLogToFile(logPath);//把日志写入日志
		if(platformFlag){
			//结束录制视频
			ScreenRecord.endScreenRecord(process,"success");
		}	
		runTestFinishTime = formatter.format(date);
		if(repmap.containsKey(tr.getName())){
			repmap.get(tr.getName()).set(1, formatter.format(date));
			repmap.get(tr.getName()).set(4, "Success");
		}
	}
	@Override
	public void onTestSkipped(ITestResult tr) {
		super.onTestSkipped(tr);		
		Date date = new Date();	
		runTestFinishTime = formatter.format(date);
		Log.logInfo("(" + tr.getName() + ")Test Result...Skipped\r\n");	
		Log.writeLogToFile(logPath);//把日志写入日志
		if(platformFlag){
			//结束录制视频
			ScreenRecord.endScreenRecord(process,"skip");
		}	

		if(repmap.containsKey(tr.getName())){
			repmap.get(tr.getName()).set(1, formatter.format(date));
			repmap.get(tr.getName()).set(4, "Skipped");
		}
		try {
			if(tr.getInstance().getClass().getMethod("getDriver").invoke(tr.getInstance()).getClass().getName().contains("org.openqa.selenium")){
				//从testcast失败结果中捕获异常信息，如果存在“cannot determine loading status”，则重启driver
				if(tr.getThrowable() != null && tr.getThrowable().getMessage() != null){
					if(tr.getThrowable().getMessage().contains("timeout: cannot determine loading status")){
						Log.logInfo("捕获异常信息:cannot determine loading status,即将退出driver,然后重启driver");
						driver = (WebDriver)tr.getInstance().getClass().getMethod("getDriver").invoke(tr.getInstance());
						driver.close();
						driver.quit();
						if(projectCode.contains("wap")){
							Log.logInfo("当前运行环境为：wap版本的webdriver");
							if(startPhoneBrowser == null){
								startPhoneBrowser = new StartPhoneBrowser();
							}
							startPhoneBrowser.start(tr.getTestContext().getSuite().getParameter("devicesName"));
							driver = StartPhoneBrowser.driver;
						}else {
							Log.logInfo("当前运行环境为：pc版本的webdriver");
							if(startbrowser == null){
								startbrowser = new Startbrowser();
							}
							startbrowser.start(tr.getTestContext().getSuite().getParameter("browserName"));
							driver = Startbrowser.driver;
						}
						//给op操作类中driver重新赋值
						Op.setDriver(driver);
						//初始化测试类中init函数
						for (Method method : tr.getInstance().getClass().getMethods()) {
							if(method.getName().equals("init")){
								tr.getInstance().getClass().getMethod("init").invoke(tr.getInstance());
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if(!e.getClass().getName().equals("java.lang.NoSuchMethodException"))
				e.printStackTrace();
		}
	}
	@SuppressWarnings({ "unused", "rawtypes" })
	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);
		//把测试数据写入msg变量中
        Iterator keys = repmap.keySet().iterator();  
        while(keys.hasNext()){  
            String key = (String)keys.next();
            String line = "";
            for(int i= 0;i<repmap.get(key).size();i++){
            	if(i == (repmap.get(key).size()-1)){
            		line = line+repmap.get(key).get(i);
            	}else {
            		line = line+repmap.get(key).get(i)+";";
				}           	
            }
            msg.append(line+"\r\n");
        }
		int pass = 0;//成功
		int failure = 0;//失败用例
		int skied = getSkippedTests().size();//跳过用例
		int sum = 0;//总用例
		/**
		 * 执行用例统计
		 */
		ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
		Set<Integer> passedTestIds = new HashSet<Integer>();
		for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
			pass++;
			passedTestIds.add(getId(passedTest));
		}
		Set<Integer> failedTestIds = new HashSet<Integer>();
		for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
			failure++;
			int failedTestId = getId(failedTest);
			if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)) {
				testsToBeRemoved.add(failedTest);
			} else {
				failedTestIds.add(failedTestId);
			}
		}
		sum = pass+failure+skied;
		Date endDate = testContext.getEndDate();
		Date SDate = testContext.getStartDate();//开始运行时间
		msg.append(formatter.format(endDate)+";"+formatter.format(endDate)+";"+testContext.getClass().getName()+";"+testContext.getName()+";onFinish;图片名称\r\n");	
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		String runDate = formatter.format(SDate);//拼接文件名称
		//String id = testContext.getSuite().getParameter("id");
		String path = projectPath + "/test-output/html-report/"+projectName+"/"+projectid+"-"+projectName+"/";
		String spath = projectPath + "/test-output/html-report/"+projectName+"/";
		String filePath = path+"result.txt"; 
		//获取操作系统相关信息
		String osName = props.getProperty("os.name")+" &lt;&lt;&lt;"+"IP: "+ ReportUtil.getIPv4()[0]+"; HOST: "+ReportUtil.getIPv4()[1];		
		Log.logInfo("是否写数据库writeindb = :"+writeindb);	
		List sumResult;
		if (writeindb.equals("true")) {// 是否写入数据库进入数据库
			DBhandle dBhandle;
			if (headmap.get("environment").contains("platform")||headmap.get("project_code").contains("platform")) {// 读取数据库
				Log.logInfo("使用平台读取数据");
				dBhandle = new DBhandle("config/jdbcui.properties");
				dBhandle.insertDataInDb3(headmap, repmap, testcasemap, dBhandle);
			} else {
				dBhandle = new DBhandle();
				dBhandle.insertDataInDb2(headmap, repmap, testcasemap);
			}
			
			News.getSumResult(tastjob, projectid);
		
			String sumSql = "select run_person " + "from auto_runinfo " + "where job = '" + tastjob + "' and taskid = '"+ projectid + "';";		
			sumResult =  dBhandle.query(sumSql);
			if((!News.send)&&sumResult.toString().contains("$BUILD_USER_ID")){
				Log.logInfo("当前为定时任务，且全部运行通过，不发送RTX消息和邮件！！！");
			}else if (failure==0&&projectCode.contains("dresslily-pc-feed")) {
				Log.logInfo("当前为datafeed任务，且全部运行通过，不发送RTX消息和邮件！！！");
			}else{
				Log.logInfo("检测配置文件是否发送消息");
				if(News.isNotSendEmail(projectCode,tastjob)){//是否发送电子邮件
					News.sendEmailCustom(projectCode, tastjob, projectid,
							testContext.getSuite().getParameter("news_code"));
				}
				if(News.isNotSendRtx(projectCode,tastjob)){//是否发送rtx消息
					News.sendRtx(projectCode, tastjob, projectid,testContext.getSuite().getParameter("test_specification"),
							testContext.getSuite().getParameter("news_code"),testContext.getSuite().getParameter("CustomRTX"));//CustomRTX 自定义rtx消息;
				}
			}
			ReportUtil.copyImage(testContext.getSuite().getParameter("tastjob"), testContext.getSuite().getName());	
		}		
		
		if (headmap.get("environment").contains("platform")||headmap.get("project_code").contains("platform")) {// 用于更新平台启动Jenkins的状态
			Log.logInfo("使用平台读取数据");
			DBhandle dBhandle = new DBhandle("config/jdbcui.properties");
			String job;
			try {
				job = headmap.get("job");
				String[] strs = null;
				if (job.contains("_m_")) {
					strs = job.split("_m_");
				} else if (job.contains("_pc_")) {
					strs = job.split("_pc_");
				}
				String sql = "update auto_caseList   set runStart='运行结束'  where testngXml_file='" + strs[1]
						+ "' and project_id in(select id from auto_projectManage where project_code='"
						+ headmap.get("project_code") + "') ";
				System.out.println("更新Jenkins状态:" + sql);
				dBhandle.update(sql);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("使用的本地方式运行平台数据");
			}
		}
			
			
		
		HtmlReport.delDirectoryAndFile(path);//如果目录存在先删除目录及下的文件
		ReportUtil.makeDirs(path);//创建目录
		try {
			ReportUtil.htmlReport(msg,"result.txt",path);//把结果写到txt文件
		} catch (IOException e) {
			e.printStackTrace();
		}
		HtmlReport.output_html(path,filePath,osName,browserName1,testContext,testcasemap);//各个模块的报告
		HtmlReport.overview_html(path,filePath,testContext,runDate);//概览
		ReportUtil.deleteFile(spath+"index.html");//删除index.html
		HtmlReport.index(spath);//重写index.html
		ReportUtil.deleteFile(spath+"left.html");//删除left.html
		HtmlReport.left(spath,testContext);//重写left.html	
		
		if(platformFlag){
			//将所有视频复制到服务器
			try {
				ScreenRecord.videoProces(projectName,testName,module,picDateSign);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.logInfo("...End to run Suite(" + testContext.getSuite().getName() + ")...\r\n");
	}
			
	/**
	 * copy xml
	 * 
	 * @param path
	 */
	public static void updateXmlName(String path,String oldName,String newName) {
		File f=new File(path+oldName);   
        String c=f.getParent();   
        System.out.println(c);
        File mm=new File(c+File.separator +newName);   
        if(f.renameTo(mm))   
        {   
        System.out.println("修改成功!");   
        }   
        else   
        {   
        System.out.println("修改失败");   
        }  
	}
	
	public static void deleteXmlName(String path,String oldName){
		 File file = new File(path+oldName);
	        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
	        if (file.exists() && file.isFile()) {
	            if (file.delete()) {
	                System.out.println("删除单个文件" + oldName + "成功！");
	                
	            } else {
	                System.out.println("删除单个文件" + oldName + "失败！");
	            
	            }
	        } else {
	            System.out.println("删除单个文件失败：" + oldName + "不存在！");
	           
	        }
	}
	private int getId(ITestResult result) {
		int id = result.getTestClass().getName().hashCode();
		id = id + result.getMethod().getMethodName().hashCode();
		id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
		return id;
	}

	
	/** 
	 * 利用java反射机制，根据属性名获取属性值 
	 * @author linchaojiang
	 */ 
	private Object getFieldValueByName(String fieldName, Object o) {  
       try {    
           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
           String getter = "get" + firstLetter + fieldName.substring(1);    
           Method method = o.getClass().getMethod(getter, new Class[] {});    
           Object value = method.invoke(o, new Object[] {});    
           return value;    
        } catch (Exception e) {    
        	Log.logInfo(e.getMessage());    
           return null;    
        }    
    }
		
	/**
	 * 截取屏幕图片
	 * @author：linchaojiang
	 * @Parameters 路径和名称
	 * 创建时间：2016-08-23 更新时间:2016-09-27
	 * 更新说明(2016-09-27)：by yuyang,增加重试来解决概率截图失败的问题
	 */
	public static String captureScreen(String directory, String captureName,WebDriver driver) {

		String screenShotPath = "";
		Date currentTime = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyyMMddHHmmss");
		String picDateSign = formats.format(currentTime);
		String screenShotFileName = screenShotPath + directory + "/" + picDateSign + "_" + captureName + ".jpg";
		File screenShotFile = null;
		int count = 0;
		while (true) {
			Log.logInfo("screen shot:" + screenShotFileName);
			try {
				Thread.sleep(500);
				screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenShotFile, new File(screenShotFileName));
				break;
			} catch (Exception e) {
				// e.printStackTrace();
				count++;
				if (count >= 3) {
					Log.logInfo("Screenshot failed！");
					break;
				}
			}
		}
		return screenShotFileName;
	}
}
