package com.globalegrow.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.globalegrow.util.DBhandle;
import com.globalegrow.util.Log;
import com.globalegrow.util.PropUtil;

/**
 * 获取html邮件内容
 * @author linchaojiang,创建时间:2017-02-17
 * 
 */
public class HtmlMail {
	 
    static String mailHost = "yxhs.globalegrow.com";// 设置邮件服务器,如果不用163的,自己找找看相关的  
    static String sendMailAddress = "autotest01@globalegrow.com";
    static String sendMailUserAddress = "autotest01@globalegrow.com";// 登录账号,一般都是和邮箱名一样
    static String sendMailPwd = "autotest123456!";// 发件人邮箱的登录密码  
	static String Status = "Failure";//Failure Success
	static String[] receiversAdress = {};//收件地址
	static String[] ccaddress = {};//抄送人地址
	static int sumCase = 0;
	static int passCase = 0;
	static int failureCase = 0;
	static int skipped = 0;
	
	static int mailStatus = -1;//0表示用例全部通过 1表示用例全部失败 2表示用例全部跳过 3表示用例只有部分通过
	
	/**
	 * 得到HTML的邮件内容
	 * @return String
	 * @author linchaojiang,创建时间:2017-02-17,最后更新时间:2017-02-20
	 */
	@SuppressWarnings("rawtypes")
	public static String getHtmlReport(String jobName,String taskId){
		
		DBhandle db1 = new DBhandle();		
		//概览信息
		String overviewSql = "select project_code,os,environment,env "
				+ "from auto_runinfo "
				+ "where job = '"+jobName+"' and taskid = '"+taskId+"' limit 1;";
		List overviewResult =  db1.query(overviewSql);			
		
		//统计
		DBhandle db2 = new DBhandle();	
		String sumSql = "select "
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Success' THEN 1 END),0) as pass,"
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Failure' THEN 0 END),0) as failure,"
				+ "COALESCE(COUNT(CASE WHEN run_result = 'Skipped' THEN 2 END),0) as skipped,"
				+ "COUNT(run_result) as sumcase "
				+ "from auto_runinfo "
				+ "where job = '"+jobName+"' and taskid = '"+taskId+"';";		
		List sumResult =  db2.query(sumSql);
		//失败查询
		DBhandle db3 = new DBhandle();	
		String runFailureSql = "select "
				+ "a.test_name,a.module_code,a.project_code,a.run_time,a.run_result,a.run_person,b.item,"
				+ "b.test_describe,b.verification_content "
				+ "from auto_runinfo a LEFT JOIN auto_testcase b "
				+ "on a.project_code = b.project_code and a.module_code = b.module and a.test_name= b.testName "
				+ "where job = '"+jobName+"' and taskid = '"+taskId+"' and run_result = '"+Status+"';";		
		List runFailureResult =  db3.query(runFailureSql);		
		//连接
		String url = "http://autotest.hqygou.com/jenkins/job/"+jobName+"/HTML_Report/";	
		Log.logInfo("overviewResult_count:"+overviewResult.size());
		Log.logInfo("sumResult_count:"+sumResult.size());
		Log.logInfo("runFailureResult_count:"+runFailureResult.size());
		
		//0表示用例全部通过 1表示用例全部失败 2表示用例只有部分通过 3表示用例全部跳过
		if(sumResult.size()>0 && overviewResult.size()>0){
			Map aa = (Map) sumResult.get(0);
			if(aa.get("sumcase").equals(aa.get("pass"))){
				mailStatus = 0;
			}else if (aa.get("sumcase").equals(aa.get("failure"))) {
				mailStatus = 1;
			}else {
				mailStatus = 2;
			}
			if(aa.get("sumcase").equals("null")||aa.get("sumcase").equals(null)||aa.get("sumcase").equals("")){
				sumCase = 0;
			}else {
				sumCase = Integer.valueOf(aa.get("sumcase").toString());
			}
			if(aa.get("failure").equals("null")||aa.get("failure").equals(null)||aa.get("failure").equals("")){
				failureCase = 0;
			}else {
				failureCase =  Integer.valueOf(aa.get("failure").toString());
			}
			if(aa.get("pass").equals("null")||aa.get("pass").equals(null)||aa.get("pass").equals("")){
				passCase = 0;
			}else {
				passCase = Integer.valueOf(aa.get("pass").toString());
			}			
			if(aa.get("skipped").equals("null")||aa.get("skipped").equals(null)||aa.get("skipped").equals("")){
				skipped = 0;
			}else {
				skipped = Integer.valueOf(aa.get("skipped").toString());
			}			
		}else {
			mailStatus = 3;
		}
		
		System.out.println("执行结果状态mailStatus:"+mailStatus);
		
		StringBuffer demo = new StringBuffer();
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
		demo.append("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"99%\">\r\n");
		demo.append("<tr height=\"63\">\r\n");
		demo.append("<td height=\"63\" colspan=\"9\" bgcolor=\"#006699\"><div align=\"center\" class=\"STYLE1\">自动化测试报告</div></td>\r\n");
		
		if(overviewResult.size()>0){
			
			Map overviewMap = (Map)overviewResult.get(0);
			Map sumMap = (Map)sumResult.get(0);
			String throughputRate = "0.00%";
			System.out.println(sumMap.get("pass"));
			int passtimes = Integer.parseInt(sumMap.get("pass").toString());
			int sumcase = Integer.parseInt(sumMap.get("sumcase").toString());
			if(sumcase>0){
				throughputRate = new DecimalFormat("######0.00").format(((double)passtimes/sumcase)*100)+"%";
			}
			
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
			demo.append("<td>"+sumMap.get("sumcase")+"</td>\r\n");
			demo.append("<td>执行成功数量</td>\r\n");
			demo.append("<td>"+sumMap.get("pass")+"</td>\r\n");
			demo.append("<td>执行失败数量</td>\r\n");
			demo.append("<td>"+sumMap.get("failure")+"</td>\r\n");
			demo.append("<td>通过率</td>\r\n");
			demo.append("<td>"+throughputRate+"</td>\r\n");
			demo.append("</tr>\r\n");
			//测试报告详细连接
			demo.append("<tr height=\"24\"> \r\n");
			demo.append("<td width=\"95\" height=\"24\" ><div align=\"right\">报告连接地址:</div></td>\r\n");
			demo.append("<td colspan=\"7\"><a href=\""+url+"\">"+url+"</a></td>\r\n");
			demo.append("</tr>\r\n");
						
			if((mailStatus == 1) || (mailStatus == 2)){//例全部失败 或者 用例只有部分通过 写失败的信息
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
				
				for(int i=0;i<runFailureResult.size();i++){
					Map runFailureMap = (Map)runFailureResult.get(i);
					demo.append("<tr align=\"center\" height=\"25\">\r\n");
					demo.append("<td>"+runFailureMap.get("test_name")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("item")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("test_describe")+"</td>\r\n");
					demo.append("<td>"+runFailureMap.get("verification_content")+"</td>\r\n");
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
		}else{
			demo.append("<tr height=\"24\"> \r\n");
			demo.append("<td colspan=\"8\"><font size=\"12\" color=\"#FF0000\">作业："+jobName+"整个测试执行跳过,请自动化测试人员排查原因！！！</font></td>\r\n");
			demo.append("</tr>\r\n");
		}		
		demo.append("</table>\r\n");
		demo.append("</body>\r\n");
		demo.append("</html>\r\n");	
		//Log.logInfo(demo.toString());		
		return demo.toString();
	}
	
	/**
	 * 发送结果测试报告(发送邮件和rtx消息)
	 * @return
	 * @author linchaojiang,创建时间:2017-02-20,最后更新时间:2017-02-20
	 */
	public static void sendEmailReport(String projectName,String jobName,String taskId){
		
		PropUtil.GetProperties("config/"+projectName+"/config.properties");	
		String ReportUrl =  "http://autotest.hqygou.com/jenkins/job/"+jobName+"/HTML_Report/";
		String rtxid = "";
		String content = "";
		StringBuffer rtxMsg = new StringBuffer();
		try {
        	String mailmsg = getHtmlReport(jobName,taskId);				
			String receivers = "";
			String ccmaildress = "";
			if((mailStatus == 0) || (mailStatus == 1) || (mailStatus == 2)){//全部成功或部分失败
				receivers =  PropUtil.get("receivers")+","+PropUtil.get("automailaddress");
				ccmaildress = PropUtil.get("ccaddress");
				ccaddress = ccmaildress.split(",");//抄送人地址
				rtxid = PropUtil.get("autoRtxMsgId")+","+PropUtil.get("receiverRtxMsgId");
				rtxMsg.append("自动化测试项目:"+projectName+";");
				rtxMsg.append("运行job:"+jobName+";");
				rtxMsg.append("任务id:"+taskId+";");
				rtxMsg.append("运行结果:总场景数:"+sumCase+",通过数:"+passCase+",失败场景数:"+failureCase+",跳过场景数:"+skipped+";");
				rtxMsg.append("详细测试报告地址:"+ReportUrl+";");
				content = rtxMsg.toString();
			}else if (mailStatus == 3) {//全部运行失败或跳过
				receivers =  PropUtil.get("automailaddress");
				rtxid = PropUtil.get("autoRtxMsgId");
				rtxMsg.append("自动化测试项目:"+projectName+";");
				rtxMsg.append("运行job:"+jobName+";");
				rtxMsg.append("任务id:"+taskId+";");
				rtxMsg.append("运行结果:运行全部失败,请查看原因;");
				content = rtxMsg.toString();
			}
			RtxNews.sendRtx(projectName,content,rtxid);//发送rtx
			
			//发送邮件
			receiversAdress = receivers.split(",");//收件地址										
			Mail mail = new Mail();
	        mail.setHost(mailHost); // 设置邮件服务器,如果不用163的,自己找找看相关的  
	        mail.setSender(sendMailAddress); 
	        mail.setUsername(sendMailUserAddress); // 登录账号,一般都是和邮箱名一样
	        mail.setPassword(sendMailPwd); // 发件人邮箱的登录密码  
	        mail.setReceivers(receiversAdress); //收件人地址
	        mail.setCcaddress(ccaddress); //抄送人地址
	        mail.setSubject(projectName+"自动化测试报告"); //发送主题               
	        mail.setMessage(mailmsg); //发送内容
	        boolean flag = new MailUtil().send(mail);//发送邮件
	        if(flag){
	        	Log.logInfo("发送邮件成功！！！");
	        }else {
				Log.logInfo("发送邮件失败！！！");
			}	      	        
		} catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("发送自动化结果报告邮件失败·····");
		}
	}
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		
		sendEmailReport("sammydress","Sammydress-DB-test-Address","10");		
	}

}
