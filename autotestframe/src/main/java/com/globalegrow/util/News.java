package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 发送电子邮件和rtx消息类
 * @author linchaojiang
 * 创建日期:2017-05-15,最后更新日期:2017-05-15
 *
 */
public class News {
	
	static String mailHost = "webmail.globalegrow.com";// 设置邮件服务器,如果不用163的,自己找找看相关的  
    static String sendMailAddress = "autotest01@globalegrow.com";
    static String sendMailUserAddress = "autotest01@globalegrow.com";// 登录账号,一般都是和邮箱名一样
    static String sendMailPwd = "autotest123456!";// 发件人邮箱的登录密码  
    public static boolean send=true;
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param str
	 * @return String
	 * @author linchaojiang
	 * 2017-05-15
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 获取执行统计结果
	 * @param jobName
	 * @param taskId
	 * @author linchaojiang
	 * 2017-05-15
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Integer> getSumResult(String jobName,String taskId){
		
		DBhandle db2=chooseDB(jobName);
		String sumSql = "select "
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Success' THEN 1 END),0) as pass,"
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Failure' THEN 0 END),0) as failure,"
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Skipped' THEN 2 END),0) as skipped,"
				+ "COUNT(run_result) as sumcase "
				+ "from auto_runinfo "
				+ "where job = '"+jobName+"' and taskid = '"+taskId+"';";		
		List sumResult =  db2.query(sumSql);
		Map<String, Integer> map = new HashMap<String, Integer>();//Integer
		if(sumResult.size()>0){
			map.put("pass", Integer.parseInt(((Map)sumResult.get(0)).get("pass").toString()));
			map.put("failure", Integer.parseInt(((Map)sumResult.get(0)).get("failure").toString()));
			map.put("skipped", Integer.parseInt(((Map)sumResult.get(0)).get("skipped").toString()));
			map.put("sumcase", Integer.parseInt(((Map)sumResult.get(0)).get("sumcase").toString()));
		}		
		/*for (String key : map.keySet()) {
			Log.logInfo(key+"="+map.get(key));
	    }*/
		//判断通过数是否=全部案例数，如果等于，就将send设置为true；
		if(map.get("pass")==map.get("sumcase")){
			Log.logInfo("全部通过");
			send=false;
		}
		return map;
	}
	
	/**
	 * 获取发送电子邮件内容
	 * @param projectName
	 * @param jobName
	 * @param taskId
	 * @author linchaojiang
	 * 创建日期:2017-05-15,最后更新日期:2017-05-15
	 */
	@SuppressWarnings("rawtypes")
	public static String getEamilMsg(String projectName,String jobName,String taskId){
		
		DBhandle db1=chooseDB(jobName);	
		//概览信息
		String overviewSql = "select project_code,os,environment,env "
				+ "from auto_runinfo "
				+ "where job = '"+jobName+"' and taskid = '"+taskId+"' limit 1;";
		List overviewResult =  db1.query(overviewSql);		
		Map overviewMap = (Map)overviewResult.get(0);
		Map<String, Integer> rumResultmap = getSumResult(jobName,taskId);
		String throughputRate = "0.00%";
		if(rumResultmap.get("sumcase")>0){
			throughputRate = new DecimalFormat("######0.00").format(((double)rumResultmap.get("pass")/rumResultmap.get("sumcase"))*100)+"%";
		}
		String url;
		if(projectName.contains("platform")){
			url="http://autotestqa.hqygou.com:8090/web/module-operation!executeOperation?operation=Form&"
					+ "componentCode=uiautotest_01&windowCode=form13&token={%22data%22:%20{%22inputParam%22:%20{%22variable%22:%20{%22xmlCode%22:%20%22"
					+ ""+jobName+"%22,%22maxTaskId%22:%20%22"+taskId+"%22}}}}";
		}else{
			url= "http://autotest.hqygou.com/jenkins/job/"+jobName+"/HTML_Report/";
		}
		StringBuffer demo = new StringBuffer();
		//html头
		demo.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		demo.append("<html>\r\n");
		demo.append("<head>\r\n");
		demo.append("<title>自动化测试报告</title>\r\n");
		demo.append("<style type=\"text/css\">\r\n");
		demo.append("td{\r\n");
		demo.append("  padding:2px 5px 2px 5px;\r\n");
		demo.append("} \r\n");
		demo.append("</style>\r\n");
		demo.append("</head> \r\n");
		demo.append("<body>\r\n");
		demo.append("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"99%\">\r\n");						
		demo.append("<tr height=\"63\">\r\n");
		demo.append("<td height=\"63\" colspan=\"9\" bgcolor=\"#006699\"><div align=\"center\" class=\"STYLE1\">自动化测试报告</div></td>\r\n");
		
		//项目概览信息
		demo.append("<tr align=\"center\" height=\"25\">\r\n");
		demo.append("<td>测试项目</td>\r\n");
		demo.append("<td>"+overviewMap.get("project_code")+"</td>\r\n");
		demo.append("<td>测试环境</td>\r\n");
		demo.append("<td>"+overviewMap.get("env")+"</td>\r\n");
		demo.append("<td>run_system</td>\r\n");
		demo.append("<td>"+overviewMap.get("os")+"</td>\r\n");
		demo.append("<td>运行环境</td>\r\n");
		demo.append("<td>"+overviewMap.get("environment")+"</td>\r\n");
		demo.append("</tr>\r\n");
		//数据统计
		demo.append("<tr align=\"center\" height=\"25\">\r\n");			
		demo.append("<td>执行总场景数据</td>\r\n");
		demo.append("<td>"+rumResultmap.get("sumcase")+"</td>\r\n");
		demo.append("<td>执行成功数量</td>\r\n");
		demo.append("<td>"+rumResultmap.get("pass")+"</td>\r\n");
		demo.append("<td>执行失败数量</td>\r\n");
		demo.append("<td>"+rumResultmap.get("failure")+"</td>\r\n");
		demo.append("<td>通过率</td>\r\n");
		demo.append("<td>"+throughputRate+"</td>\r\n");
		demo.append("</tr>\r\n");
		//测试报告详细连接
		demo.append("<tr height=\"24\"> \r\n");
		demo.append("<td width=\"95\" height=\"24\" ><div align=\"right\">报告连接地址:</div></td>\r\n");
		demo.append("<td colspan=\"7\"><a target=\"_blank\">"+url+"</a></td>\r\n");
		demo.append("</tr>\r\n");
		
		if(rumResultmap.get("failure")>0||rumResultmap.get("skipped")>0){
			//测试报告详细连接
			demo.append("<tr height=\"24\"> \r\n");
			demo.append("<td colspan=\"8\">执行失败场景如下:</a></td>\r\n");
			demo.append("</tr>\r\n");
			
			//失败用例信息
			demo.append("<tr align=\"center\" height=\"25\">\r\n");
			demo.append("<td bgcolor=\"#0099CC\">脚本名称</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">测试项</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">测试描述</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">验证点</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">所属模块</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">运行人</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">运行时间</td>\r\n");
			demo.append("<td bgcolor=\"#0099CC\">运行结果</td>\r\n");
			demo.append("</tr>\r\n\r\n");
			
			//失败查询
			DBhandle db3=chooseDB(jobName);	
			String runFailureSql;
			if(projectName.contains("platform")){
				runFailureSql="select a.test_id, a.test_name,a.module_code,a.project_code,a.run_time,a.run_result,a.run_person,a.job,b.case_name,b.case_step,b.case_check  "
						+ " from auto_runinfo  as a   INNER JOIN auto_testcase as  b  on a.test_id=b.id "
						+ "  where job = '"+jobName+"' and taskid = '"+taskId+"' and run_result = 'Failure';";
			}else {				
				runFailureSql = "select "
						+ "a.test_name,a.module_code,a.project_code,a.run_time,a.run_result,a.run_person,b.item,"
						+ "b.test_describe,b.verification_content "
						+ "from auto_runinfo a LEFT JOIN auto_testcase b "
						+ "on  a.module_code = b.module and a.test_name= b.testName "
						+ "where job = '"+jobName+"' and taskid = '"+taskId+"' and run_result = 'Failure';";
			}			
			List runFailureResult =  db3.query(runFailureSql);
			
			for(int i=0;i<runFailureResult.size();i++){
				Map runFailureMap = (Map)runFailureResult.get(i);
				demo.append("<tr align=\"center\" height=\"25\">\r\n");
				demo.append("<td>"+runFailureMap.get("test_name")+"</td>\r\n");
				if(projectName.contains("platform")){
					demo.append("<td>"+runFailureMap.get("case_name")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("case_step")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("case_check")+"</td>\r\n");
					
				}else{
					demo.append("<td>"+runFailureMap.get("item")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("test_describe")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("verification_content")+"</td>\r\n");
				}
				String mode = runFailureMap.get("module_code").toString().split("_")[runFailureMap.get("module_code").toString().split("_").length-1];
				demo.append("<td>"+mode+"</td>\r\n");
				if(runFailureMap.get("run_person").equals("$BUILD_USER_ID")){
					demo.append("<td>定时任务</td>\r\n");
				}else {
					demo.append("<td>"+runFailureMap.get("run_person")+"</td>\r\n");
				}
				demo.append("<td>"+runFailureMap.get("run_time")+"</td>\r\n");
				demo.append("<td><font color=\"#FF0000\">"+runFailureMap.get("run_result")+"</font></td>\r\n");
				demo.append("</tr>\r\n");
			}
		}
		
		if(rumResultmap.get("sumcase")==rumResultmap.get("skipped")){
			demo.append("<tr height=\"24\"> \r\n");
			demo.append("<td colspan=\"8\"><font size=\"12\" color=\"#FF0000\">作业："+jobName+"整个测试执行跳过,请自动化测试人员排查原因！！！</font></td>\r\n");
			demo.append("</tr>\r\n");
		}
		
		//html结尾
		demo.append("</table>\r\n");
		demo.append("</body>\r\n");
		demo.append("</html>\r\n");
		
		return demo.toString();
		
	}

	@SuppressWarnings("unchecked")
	public static String getRtxMsg(String projectName,String jobName,String taskId,String test_specification,String CustomRTX){
		DBhandle db;
		String ReportUrl ;
         if(projectName.contains("platform")||jobName.contains(".xml")){//jenkins的job名会以.xml文件结尾命名，带有平台表示内容，都会带有platform
 			db= new DBhandle("config/jdbcui.properties");	
 			ReportUrl="http://autotestqa.hqygou.com:8090/web/module-operation!executeOperation?operation=Form&"
 					+ "componentCode=uiautotest_01&windowCode=form13&token={%22data%22:%20{%22inputParam%22:%20{%22variable%22:%20{%22xmlCode%22:%20%22"
 					+ ""+jobName+"%22,%22maxTaskId%22:%20%22"+taskId+"%22}}}}";
 		}else{
 			db= new DBhandle();	
 			String url = "";// 获取数据的url地址
 			String sql = "SELECT * from auto_server_ip";// 从数据库中读取rtx的IP
 			List querylist = db.query(sql);
 			for (int i = 0; i < querylist.size(); i++) {
 				Map runFailureMap = (Map) querylist.get(i);
 				url = (String) runFailureMap.get("url");
 			}
 			//测试报告地址
 			ReportUrl = url+"module-operation!executeOperation?componentCode=auto_uitest&windowCode=ui_01"
 					+ "&token=%7B%22data%22:%7B%22inputParam%22:%7B%22variable%22:%7B%22jobName%22:%22"+jobName+"%22%7D%7D%7D%7D";
 		}
		
		
		//发送消息内容
		StringBuffer rtxMsg = new StringBuffer();
		//获取运行结果信息
		Map<String, Integer> rumResultmap = getSumResult(jobName,taskId); 
		if(test_specification != null){
			rtxMsg.append("运行描述:"+test_specification+";\r\n");
		}
		rtxMsg.append("job名称:"+jobName+";");
		rtxMsg.append("运行id:"+taskId+";");
		// 获取存放在生成成功后的订单编号,用于自定义
				if (CustomRTX != null) {
					List<String> customText = null;
					try {
						customText = Pub.readFilebyList(new File("").getAbsolutePath() + "\\config\\" + CustomRTX);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rtxMsg.append("生成的订单编号为:" + customText.get(0) + ";\r\n");
				}
		
		rtxMsg.append("运行结果:总场景数:"+rumResultmap.get("sumcase")+",通过数:"+rumResultmap.get("pass")+",失败场景数:"+rumResultmap.get("failure")+",跳过场景数:"+rumResultmap.get("skipped")+";\r\n");
		rtxMsg.append("报告地址:[点击这里查看报告|"+ReportUrl+"].\r\n");
		Date date = new Date();
	    long times = date.getTime();//时间戳
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(date);
		rtxMsg.append("报告发送时间:"+dateString);
		return rtxMsg.toString();
	}

	
	
	/**
	 * 获取发送电子邮件Email地址和RTX消息信息id
	 * @param project_code
	 * @author linchaojiang
	 * 创建日期:2017-05-15,最后更新日期:2017-05-15
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getSendPersonInfo(String project_code,String news_code){
		
		String sql = "";
		if(news_code != null){
			sql = "select * from auto_news where news_code = '"+news_code+"';";
		}else {
			sql = "select * from auto_news where project_code = '"+project_code+"';";
		}
		
		
		DBhandle  db = chooseDB(project_code);
		List list= db.query(sql);
		Map<String, String> map = new HashMap<String, String>();
		if(list.size()>0){
			map.put("rtx_id", replaceBlank(((Map)list.get(0)).get("rtx_id").toString()));
			map.put("cc_mail", replaceBlank(((Map)list.get(0)).get("cc_mail").toString()));
			map.put("send_mail", replaceBlank(((Map)list.get(0)).get("send_mail").toString()));
			map.put("auto_mail", replaceBlank(((Map)list.get(0)).get("auto_mail").toString()));
			map.put("enable", ((Map)list.get(0)).get("email_enable").toString());
			map.put("write_db", ((Map)list.get(0)).get("rtx_enable").toString());	
		}		
		/*for (String key : map.keySet()) {
			Log.logInfo(key+"="+map.get(key));
	    }*/
		return map;
	}
	
	/**
	 * 发送post请求
	 * @param str
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-21,更新时间:2017-02-21
	 */
	public static void TestPost(String str) throws IOException{          
        URL url;
        //url = new URL("http://branched.msgcenter100.com/api-source/index");//测试环境
        url = new URL("http://msgcenter100.com/api-source/index"); //正式环境
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
	public static void sendRtx(String projectName,String jobName,String taskId,String test_specification,String news_code,String CustomRTX){		
        try {
        	String rtxMsg = getRtxMsg(projectName,jobName,taskId,test_specification,CustomRTX);//获取rtx消息内容
    		Map<String, String> map = getSendPersonInfo(projectName,news_code);
            Map<String, Object> bodyAll = new LinkedHashMap<String, Object>();
            //通过job名称
            //测试环境
            /*bodyAll.put("account", "autotestuser01");
            bodyAll.put("password", "autotestpwd01");
            bodyAll.put("api_key","thdeHUoGzuApBrY984XX");*/
            //正式环境
            bodyAll.put("account", "chd");
            bodyAll.put("password", "123456");
            bodyAll.put("api_key","mFjUeqcCCNJ3o8yQU3uE");
            
            Map<String, Object> rtx = new LinkedHashMap<String, Object>();
            rtx.put("channel", "rtx");
            rtx.put("to", map.get("rtx_id"));
            rtx.put("title",projectName+"自动化测试结果");
            rtx.put("content",rtxMsg);
            
            List<Map> list = new ArrayList<>();
            list.add(rtx);
            bodyAll.put("data", list);
            
            try {
    			TestPost(PHPURLBuilder.httpBuildQuery(bodyAll, "UTF-8"));
    		} catch (IOException e) {
    			e.printStackTrace();
    			Log.logInfo("发送rtx消息失败！！！！");
    		}
		} catch (Exception e) {
			Log.logInfo("发送rtx消息失败！！！");
		}
	}
	
	/**
	 * 发送电子邮件,调用公司发送消息接口
	 * @param project_code
	 * @param msg
	 * @author linchaojiang
	 * 创建日期:2017-05-15,最后更新日期:2017-05-15
	 */
	@SuppressWarnings("rawtypes")
	public static void  sendEmail(String project_code,String msg,String news_code) {
		
		Map<String, Object> bodyAll = new LinkedHashMap<String, Object>();
        bodyAll.put("account", "autotestuser01");
        bodyAll.put("password", "autotestpwd01");
        bodyAll.put("api_key","thdeHUoGzuApBrY984XX");
		
		Map<String, String> map = getSendPersonInfo(project_code,news_code);
		String sendEmail = map.get("send_mail")+","+map.get("auto_mail");
		Map<String, Object> email = new LinkedHashMap<String, Object>();
		email.put("channel", "cheetahmail");
		email.put("to", sendEmail);
		email.put("title",sendEmail+"自动化测试结果");
		email.put("content",msg);       
        List<Map> list = new ArrayList<>();
        list.add(email);
        bodyAll.put("data", list);        
        try {
			TestPost(PHPURLBuilder.httpBuildQuery(bodyAll, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			Log.logInfo("发送rtx消息失败！！！！");
		}		
	}
	
	/**
	 * 自定义发送电子邮件
	 * @param project_code
	 * * @author linchaojiang
	 * 创建日期:2017-05-15,最后更新日期:2017-05-15
	 */
	public static void sendEmailCustom(String projectName,String jobName,String taskId,String news_code){
		try {
			Map<String, String> map = getSendPersonInfo(projectName,news_code);
			String ccEmail[]  = map.get("cc_mail").split(",");
			String sendEmail[] = (map.get("send_mail")+","+map.get("auto_mail")).split(",");
			String emailmsg = getEamilMsg(projectName,jobName,taskId);
			Mail mail = new Mail();
	        mail.setHost(mailHost); // 设置邮件服务器,如果不用163的,自己找找看相关的  
	        mail.setSender(sendMailAddress); 
	        mail.setUsername(sendMailUserAddress); // 登录账号,一般都是和邮箱名一样
	        mail.setPassword(sendMailPwd); // 发件人邮箱的登录密码  
	        mail.setReceivers(sendEmail); //收件人地址
	        mail.setCcaddress(ccEmail); //抄送人地址
	        mail.setSubject(projectName+"自动化测试报告"); //发送主题               
	        mail.setMessage(emailmsg); //发送内容
	        boolean flag = new MailUtil().send(mail);//发送邮件
	        if(flag){
	        	Log.logInfo("发送邮件成功！！！");
	        }else {
				Log.logInfo("发送邮件失败！！！");
			}
		} catch (Exception e) {
			Log.logInfo("发送电子邮件失败！！！");
		}
			      	        
	}

	/**
	 * 同时发送电子邮件和rtx消息
	 * @author linchaojiang
	 * 创建时间:2017-05-15,最后更新时间:2017-05-15
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendNews(String projectName,String jobName,String taskId,String test_specification,String news_code,String CustomRTX) throws UnsupportedEncodingException{
		
			sendRtx(projectName,jobName,taskId,test_specification,news_code,CustomRTX);//发送rtx消息
			sendEmailCustom(projectName,jobName,taskId,news_code);//发送Emaill	
		
	}
	
	/**
	 * 判断是否需要发送电子邮件
	 * @param project_code
	 * @return
	 * @author linchaojiang
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static boolean isNotSendEmail(String project_code,String tastjob){
		DBhandle  db=chooseDB(project_code);
		boolean status = false; 
		String sql = null;
		if(project_code.contains("platform")){
			sql="select xmlcode,email_enable from auto_news where  project_code = '"+project_code+"';";
		}else{
			sql = "select email_enable from auto_news where  project_code = '"+project_code+"';";
		}
		
		System.out.println("sql"+sql);
		List list= db.query(sql);
		Map<String, String> map = new HashMap<String, String>();
		if(list.size()>0){
			if(project_code.contains("platform")){
				String xml = String.valueOf(((Map)list.get(0)).get("xmlcode").toString());				
				if(!tastjob.contains(xml)){
					status=false;
					return  status;
				}
			}			
			int enable = Integer.parseInt(((Map)list.get(0)).get("email_enable").toString());
			if(enable==1){				
				status = true;
			}
			
		}else {
			Log.logInfo("系统中对项目"+project_code+"没有配置发送邮件的信息,请登录http://10.33.4.123:9001/web/系统新增配置！！！");
			return status;
		}
		return status;
	}
	
	/**
	 * 判断是否需要发送rtx消息
	 * @param project_code
	 * @return
	 * @author linchaojiang
	 */	
	@SuppressWarnings({ "unused", "rawtypes" })
	public static boolean isNotSendRtx(String project_code,String tastjob){
	    DBhandle  db=chooseDB(project_code);
		
		boolean status = false;
		String sql;
		if(project_code.contains("platform")){
			sql="select rtx_enable,xmlcode from auto_news where  project_code = '"+project_code+"';";
		}else{		
			sql= "select rtx_enable from auto_news where  project_code = '"+project_code+"';";
		}
		List list= db.query(sql);
		Map<String, String> map = new HashMap<String, String>();
		if(list.size()>0){
			if(project_code.contains("platform")){
				String xml = String.valueOf(((Map)list.get(0)).get("xmlcode").toString());				
				if(!tastjob.contains(xml)){
					status=false;
					return  status;
				}
			}
			
			int enable = Integer.parseInt(((Map)list.get(0)).get("rtx_enable").toString());
			if(enable==1){
				status = true;
			}
		}else {
			Log.logInfo("系统中对项目"+project_code+"没有配置发送rtx的信息,请登录http://autotest.hqygou.com/jsweb/index.jsp系统新增配置！！！");
			return status;
		}
		return status;
	}
	
	public static DBhandle chooseDB(String type){
		DBhandle db;
		if(type.contains("platform")||type.contains(".xml")){//jenkins的job名会以.xml文件结尾命名，带有平台表示内容，都会带有platform
			db= new DBhandle("config/jdbcui.properties");	
		}else{
			db= new DBhandle();	//调用原来的数据库
		}
		return db;
	}
	
	/**
	 * test
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String jobName = "Sammydress_prd_Order_Promotion";
		String taskId = "159";		

	    getSumResult(jobName,taskId);
		//String emailmsg = getEamilMsg("sammydress","Sammydress_prd_Order_Promotion","157");
		//sendRtx(jobName,"fdsafdsa","10537");
		//sendEmail("sammydress-pc-prd",emailmsg);
		//sendEmailCustom("sammydress");
		
		//getSumResult("Oms_wms_thirdOrder","45");
		
		//System.out.println(getEamilMsg("oms-pc-test","Oms_wms_thirdOrder","45"));
		//sendRtx("rosewholesale","Rosewholesale_pc_prd_Sigup_Login_address","1005","","","");
		//String projectName,String jobName,String taskId,String test_specification,String news_code,String CustomRTX
		//System.out.println(getSendPersonInfo("oms-pc-test"));
		sendEmailCustom("oms-pc-test","Oms_wms_thirdOrder","45","rosewholesale-pc-prd");
		//send=false;
		//sendNews("Gamiss_PC_PRD_Cart","Gamiss_PC_PRD_Cart","146","11","11");
		//sendRtx("lincj-test","Oms_wms_thirdOrder","45","OMS第三方完全配货完全发货主流程","lincj_test");
		
	}

}
