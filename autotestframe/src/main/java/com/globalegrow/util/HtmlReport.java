package com.globalegrow.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.testng.ITestContext;

import com.globalegrow.base.TestngListener2;

public class HtmlReport {
	
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    /**
     * 概览信息，统计各个模块运行情况:模块名称、成功数据、失败数目、总数目、运行时间、通过率
     */
    public static void overview_html(String path,String filePath,ITestContext tc,String runDate){
    	
    	StringBuffer msg = new StringBuffer();
    	Map<String, List<String>> resultList = new HashMap<String,List<String>>();
        resultList = ReportUtil.resultClassliy(ReportUtil.readTxtFile(filePath));
        List<String> sumList = resultList.get("sumList");
    	List<String> testCase = resultList.get("testCase");//运行的的具体实例
    	List<String> ItemListfl = resultList.get("ItemListfl");//运行类，即模块
    	//List<String> otherList = resultList.get("otherList"); //获取onTestStart及其他
    	
    	String  startDate = null;//总测试运行开始时间
    	String  endDate = null;//总测试运行结束时间
    	int runSummaryTime = 0;//总运行次数
    	int runSuccessTime = 0;//总运行成功数
    	int runFailureTime = 0;//总运行失败数据
    	int runSkipTime = 0;//跳过场景数
    	
    	for(int i = 0;i<sumList.size();i++){
    		String string = sumList.get(i);
    		String[] strings = string.split(";");
    		if(strings[4].equals("onStart")){
    			startDate=strings[0];
    		}
    		if(strings[4].equals("onFinish")){
    			endDate=strings[0];
    		}
    	}
    	for(int i = 0;i<testCase.size();i++){
    		String string = testCase.get(i);
    		String[] strings = string.split(";");
    		if(strings[4].equals("Success")){
    			runSuccessTime++;
    		}
    		if(strings[4].equals("Failure")){
    			runFailureTime++;
    		}
    		if(strings[4].equals("Skipped")){
    			runSkipTime++;
    		}
    	}
    	runSummaryTime = testCase.size();
    	
    	/**
    	 * 头部
    	 */
    	String headString = getHeadhteml(tc,runDate);
    	msg.append(headString);
    	
    	/**
    	 * 各个模块
    	 */
    	for(int i=0;i<ItemListfl.size();i++){
    		String startD = "2017-03-08 18:40:03";
    		String endD = "2017-03-08 18:40:03";
    		String modleName = "";
    		int passTime = 0;
    		int failureTime = 0;
    		int skipTime = 0;
    		   		
    		//得到开始运行时间
    		startD = TestngListener2.modeTime.get(ItemListfl.get(i));
    		
    		//获取结束运行时间
    		List<String> list = new ArrayList<String>();
    		for(int x=0;x<testCase.size();x++){
    			String[] ssh = testCase.get(x).split(";");
    			if(ssh[2].equals(ItemListfl.get(i))){
    				list.add(ssh[1]);
    			}
    		}
    		try {
				endD = getMaxDate(list);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		//获取每个类执行的开始时间
    		/*for(int h=0;h<otherList.size();h++){
    			String[] re = otherList.get(h).split(";");
    			if(ItemListfl.get(i).equals(re[1])&&re[3].equals("onTestStart")){
    				startD = re[0];
    				break;
    			}
    		}*/  		
    		for(int j=0;j<testCase.size();j++){
    			String string = testCase.get(j);
        		String[] strings = string.split(";");       		
        		if(ItemListfl.get(i).equals(strings[2])){        			
        			if(modleName == ""){
        				modleName = strings[2].split("\\.")[strings[2].split("\\.").length-1];       				
        			}
        			endD = strings[0];//每个类执行的结束时间
        			if(strings[4].equals("Success")){
        				passTime++;
            		}
            		if(strings[4].equals("Failure")){
            			failureTime++;
            		}
            		if(strings[4].equals("Skipped")){
            			skipTime++;
            		}
        		}
    		}
    		String throughputRate = "0.00%";
    		if(passTime == 0){
    			throughputRate = "0.00%";
    		}else {
    			throughputRate = new DecimalFormat("######0.00").format(((double)passTime/(passTime+failureTime+skipTime))*100);
			}
    				
    		msg.append("<tr>\r\n");
    		msg.append("<td bgcolor=\"#CCCCCC\"><a href = \""+modleName+".html\" >"+modleName+"</a></td>\r\n");
			msg.append("<td bgcolor=\"#CCCCCC\">"+startD+"</td>\r\n");
			msg.append("<td bgcolor=\"#CCCCCC\">"+endD+"</td>\r\n");
			msg.append("<td bgcolor=\"#CCCCCC\">"+ReportUtil.dateDiff(startD,endD,"yyyy-MM-dd HH:mm:ss")+"</td>\r\n");
			msg.append("<td bgcolor=\"#CCCCCC\">"+(passTime+failureTime+skipTime)+"</td>\r\n");
			msg.append("<td bgcolor=\"#00FF99\">"+passTime+"</td>\r\n");
			msg.append("<td bgcolor=\"#FF0000\">"+failureTime+"</td>\r\n");
			msg.append("<td bgcolor=\"#FFFF99\">"+skipTime+"</td>\r\n");
			msg.append("<td bgcolor=\"#CCCCCC\">"+throughputRate+"%</td>\r\n");
			msg.append("</tr>\r\n");
    	}
    	
    	/**
    	 * 汇总信息
    	 */
    	msg.append("<tr>\r\n");
    	msg.append("<td bgcolor=\"#CCCCCC\">汇总</td>\r\n");
		msg.append("<td bgcolor=\"#CCCCCC\">"+startDate+"</td>\r\n");
		msg.append("<td bgcolor=\"#CCCCCC\">"+endDate+"</td>\r\n");
		msg.append("<td bgcolor=\"#CCCCCC\">"+ReportUtil.dateDiff(startDate,endDate,"yyyy-MM-dd HH:mm:ss")+"</td>\r\n");
		msg.append("<td bgcolor=\"#CCCCCC\">"+runSummaryTime+"</td>\r\n");
		msg.append("<td bgcolor=\"#00FF99\">"+runSuccessTime+"</td>\r\n");
		msg.append("<td bgcolor=\"#FF0000\">"+runFailureTime+"</td>\r\n");
		msg.append("<td bgcolor=\"#FFFF99\">"+runSkipTime+"</td>\r\n");
		msg.append("<td bgcolor=\"#CCCCCC\">"+new DecimalFormat("######0.00").format(((double)runSuccessTime/runSummaryTime)*100)+"%</td>\r\n");
		msg.append("</tr>\r\n");
		
		/**
		 * 收尾
		 */
    	msg.append("</table></body></html>\r\n");
    	
    	try {
    		ReportUtil.htmlReport(msg,"overview.html",path);
		} catch (IOException e) {
			e.printStackTrace();
		}   
    }
    
    /**
     * 得到overview的头部
     * @return
     * @author linchaojiang
     */
    public static String getHeadhteml(ITestContext tc,String runDate){
    	
    	StringBuffer msg = new StringBuffer();
    	msg.append("<html>\r\n");
    	msg.append("<head>\r\n");
	   	msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n");
	   	msg.append("<title>自动化测试报告</title>\r\n");
	   	msg.append("</head>\r\n");
    	msg.append("<body>\r\n");
    	msg.append("<h1>Test Results Report--------运行时间:"+runDate+"</h1>\r\n");
    	msg.append("<h2>"+tc.getSuite().getName()+"</h2>\r\n");
    	msg.append("<h5></h5>\r\n");
    	msg.append("<table border=\"1\" width=\"100%\"  align=\"center\">\r\n");
    	msg.append("<tr>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">Test</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">开始运行时间</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">结束运行时间</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">运行时长</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">SumTestCase</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">Pass</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">Failure</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">Skipped</td>\r\n");
    	msg.append("<td bgcolor=\"#0099CC\" align = \"center\">通过率</td>\r\n");
    	msg.append("</tr>\r\n");
    	
		return msg.toString();
    	
    }
        
    /**
     * 模块的详细信息:概览 和 详细
     * 概览：报告标题、操作系统、浏览器
     * 详细：测试项、测试描述、验证点、是否通过、截图
     */
    @SuppressWarnings("rawtypes")
	public static void output_html(String path,String filePath,String osName,String browserName,ITestContext tc,HashMap<String,Map<String, String >> casemap){
    	  	
    	Map<String, List<String>> resultList = new HashMap<String,List<String>>();
        resultList = ReportUtil.resultClassliy(ReportUtil.readTxtFile(filePath));
    	List<String> testCase = resultList.get("testCase");
    	List<String> ItemListfl = resultList.get("ItemListfl"); 
    	boolean screenShotWhilePassFlag = false;
    	String[] strings=null;
    	for(int i =0;i<ItemListfl.size();i++){
    		StringBuffer msg = new StringBuffer();
    		String headMsg = testHead(osName,browserName,tc);//头部信息
    		msg.append(headMsg);  
    		
    		for(int j=0;j<testCase.size();j++){
    			try {
					String string = testCase.get(j);
					strings = string.split(";");					
					Map<String, String> testCasemap2=casemap.get(strings[3]);//读取数据库中对应方法名的所有内容
					if(ItemListfl.get(i).equals(strings[2])){//用户读取数据时排重
						msg.append("<tr height=\"21\">\r\n");
					    msg.append("<td align=\"center\"><a href=\"JavaScript:;\" onclick=\"window.open ('../log/"+strings[6]+".txt','newwindow','width=800, height=500, top=150,left=300,scrollbars=yes')\">" + strings[3] + "</a></td>\r\n");//测试testcase
						if(casemap.get(strings[3])==null){//如果测试方法为空，就把需要的数据全部置为空，以免影响到正常读取的内容的
					        for (int k=0;k<4;k++) {
					        	  msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + "null" + "</td>\r\n");
							}
						}else{
						    //Log.logInfo("测试模块:"+testCasemap.get(strings[3]).get(0));
						    msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + testCasemap2.get("funmodule")+ "</td>\r\n");
						    //Log.logInfo("测试项:"+testCasemap.get(strings[3]).get(1));
						    msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + testCasemap2.get("item")+ "</td>\r\n");
						    //Log.logInfo("测试步骤:"+testCasemap.get(strings[3]).get(2));
						    msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + testCasemap2.get("teststep") + "</td>\r\n");
						   // Log.logInfo("验证点:"+testCasemap.get(strings[3]).get(3));
						    msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + testCasemap2.get("verification_content") + "</td>\r\n");
						}
						 //是否通过
					    if(strings[4].equals("Success")){
					    	 if(TestngListener2.platformFlag){
					    		 if (screenShotWhilePassFlag) {
					    			 msg.append("<td align=\"center\"><a href=\"JavaScript:;\" "
							    				+ "onclick=\"window.open ('../screenShot/"+strings[5]+"','newwindow','width=700, height=900, top=150,left=300,scrollbars=yes')\">"
							    						+ "<font color=\"#00FF00\">" + strings[4] + "</font></a>|<a href=\"JavaScript:;\" "
							    								+ "onclick=\"window.open ('../videoShot/"+strings[7]+"/"+ strings[3] +".mp4',"
							    										+ "'newwindow','width=700, height=900, top=150,left=300,scrollbars=no')\">视频</a></td>\r\n");
								 } else {		 
									 msg.append("<td align=\"center\"><font color=\"#00FF00\">"+strings[4]+"</font>|<a href=\"../videoShot/"+ strings[7] +"/"+ strings[3] +".mp4\">视频下载</a></td>\r\n");
								 } 
					    	 }else {
					    		 if (screenShotWhilePassFlag) {
					    			 Log.logInfo("5"+strings[5]);
									 msg.append("<td align=\"center\"><a href=\"screenShot/"+strings[5]+"\""+" target=\"_blank\" >"
									   		+ "<font color=\"#00FF00\">PASS</font></a></td>\r\n");
								 } else {		 
									 msg.append("<td align=\"center\"><font color=\"#00FF00\">"+strings[4]+"</font></td>\r\n");
								 }
							} 
					    }	
					    if(strings[4].equals("Failure")){
					    	if(TestngListener2.platformFlag){
					    		msg.append("<td align=\"center\"><a href=\"JavaScript:;\" "
					    				+ "onclick=\"window.open ('../screenShot/"+strings[5]+"','newwindow','width=700, height=900, top=150,left=300,scrollbars=yes')\">"
					    						+ "<font color=\"#FF0000\">" + strings[4] + "</font></a>|<a href=\"JavaScript:;\" "
					    								+ "onclick=\"window.open ('../videoShot/"+strings[7]+"/"+ strings[3] +".mp4',"
					    										+ "'newwindow','width=700, height=900, top=150,left=300,scrollbars=yes')\">视频</a></td>\r\n");
					    	}else {
					    		msg.append("<td align=\"center\"><a href=\"JavaScript:;\" "
					    				+ "onclick=\"window.open ('../screenShot/"+strings[5]+"','newwindow','width=1600, height=900, top=50,left=50,scrollbars=yes')\">"
					    						+ "<font color=\"#FF0000\">" + strings[4] + "</font></a></td>\r\n");
							}    	
					    }
					    if(strings[4].equals("Skipped")){
					    	if (screenShotWhilePassFlag) {
								 msg.append("<td align=\"center\"><a href=\"screenShot/"+strings[5]+"\""+" target=\"_blank\" >"
								   		+ "<font color=\"#CCCCCC\">Skipped</font></a></td>\r\n");
							 } else {		 
								 msg.append("<td align=\"center\"><font color=\"#CCCCCC\">"+strings[4]+"</font></td>\r\n");
							 }
					    }
					    msg.append("</tr>\r\n");
					}
				} catch (Exception e) {
					Log.logInfo("testcase:"+testCase.get(j)+",写html报告异常！！！");
					e.printStackTrace();
				
				}		
    		}
    		
    		try {
    			ReportUtil.htmlReport(msg,ItemListfl.get(i).split("\\.")[ItemListfl.get(i).split("\\.").length-1]+".html",path);
			} catch (IOException e) {
				e.printStackTrace();
			}   		
    	}   	
    }

    /**
     * 写html，index.html信息
     * @param spath
     * @return
     */
    public static void index(String path){
    	List<String> fileName = new ArrayList<String>();
    	fileName = getFileName_orderByDate(path);
    	StringBuffer msg = new StringBuffer();   
    	msg.append("<html>\r\n"); 
    	msg.append("<head>\r\n");
	   	msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n");
	   	msg.append("<title>自动化测试报告</title>\r\n");
	   	msg.append("</head>\r\n");
    	msg.append("<frameset rows=\"*\" cols=\"10%,90%\">\r\n");
    	msg.append("<frame src=\"left.html\"/>\r\n");
    	if(fileName.size()>0){
    		msg.append("<frame src=\""+fileName.get(0)+"/overview.html\" name=\"view_frame\" />\r\n");
    	}   	
    	msg.append("</frameset>\r\n");
    	msg.append("</html>\r\n");
    	try {
			ReportUtil.htmlReport(msg,"index.html",path);
		} catch (IOException e) {
			e.printStackTrace();
		}   	
    }
    
    /**
     * 写html，左侧导航信息
     * @param spath
     * @param tc
     * @return String
     * @author linchaojiang
     */
    public static void left(String path,ITestContext tc){
 	   	StringBuffer msg = new StringBuffer();   
 	   	msg.append("<html>\r\n");
 	   	msg.append("<head>\r\n");
 	   	msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n");
 	   	msg.append("<title>自动化测试报告</title>\r\n");
 	   	msg.append("</head>\r\n");
 	   	msg.append("<body>\r\n");
 	   	msg.append("<div id=\"sidebarHeader\">\r\n");
 	   	msg.append("<h3>Test Results Report</h3>\r\n");
 	   	msg.append("</div>\r\n");
 	   	msg.append("<h3>"+tc.getSuite().getName()+"</h3>\r\n");
 	   	msg.append("<ul>\r\n");  	
 	   	List<String> fileName = new ArrayList<String>();
 	   	fileName = getFileName_orderByDate(path);
 	   	for(int i=0;i<fileName.size();i++){
 	   		if(!fileName.contains("svn")){
 	   			msg.append("<li><a href=\""+fileName.get(i)+"/overview.html\" target=\"view_frame\">"+fileName.get(i)+"</a></li>\r\n"); 
 	   		}	   		
 	   	}
 	   	msg.append("</ul></body></html>\r\n");
 	   try {
			ReportUtil.htmlReport(msg,"left.html",path);
		} catch (IOException e) {
			e.printStackTrace();
		}   	
 	    
    }
    
    /**
     * 写html，test运行前信息
     * @param osName
     * @return String
     */
    public static String testHead(String osName,String browserName,ITestContext tc){
 	   
 	    StringBuffer msg = new StringBuffer();  
 		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
 				+ " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
 		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
 		msg.append("<head>\r\n");
 		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n");
 		msg.append("<title>自动化测试报告</title>\r\n");
 		msg.append("<style type=\"text/css\">\r\n");
 		msg.append("<!--\r\n");
 		msg.append(".STYLE1 {font-size: 24px}\r\n");
 		msg.append(".STYLE2 {color: #FF0000}\r\n");
 		msg.append("-->\r\n");
 		msg.append("</style>\r\n");
 		msg.append("</head>\r\n");
 		msg.append("<body>\r\n");
 		msg.append("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\""
 				+ " width=\"98%\" height=\"100%\">\r\n");
 		msg.append("<tr height=\"63\">\r\n");
 		msg.append("<td height=\"63\" colspan=\"6\" bgcolor=\"#006699\"><div align=\"center\" " + "class=\"STYLE1\">"
 				+ tc.getSuite().getName() + "自动化测试报告</div></td>\r\n");
 		msg.append("</tr>\r\n");
 		msg.append("<tr height=\"24\">\r\n");
 		msg.append("<td width=\"95\" height=\"24\"><div align=\"right\">测试项目:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
 		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + tc.getSuite().getName() + "</td>\r\n");
 		msg.append("</tr>\r\n");
 		msg.append("<tr height=\"24\">\r\n");
 		msg.append("<td height=\"24\"><div align=\"right\">浏览器:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
 		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + browserName +"&gt;&gt;&gt;"+ "</td>\r\n");
 		msg.append("</tr>\r\n");
 		msg.append("<tr height=\"24\">\r\n");
 		msg.append("<td height=\"24\"><div align=\"right\">操作系统:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
 		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + osName + "&gt;&gt;&gt;" + "</td>\r\n");
 		msg.append("</tr>\r\n");
 		msg.append("<tr height=\"19\">\r\n");
 		msg.append("<td width=\"19\" bgcolor=\"#0099CC\"><div align=\"center\">TESTCASE</div></td>\r\n");
 		msg.append("<td width=\"79\" bgcolor=\"#0099CC\"><div align=\"center\">模块</div></td>\r\n");
 		msg.append("<td width=\"99\" bgcolor=\"#0099CC\"><div align=\"center\">测试项</div></td>\r\n");		
 		msg.append("<td width=\"250\" bgcolor=\"#0099CC\"><div align=\"center\">测试步骤</div></td>\r\n");		
 		msg.append("<td width=\"300\" bgcolor=\"#0099CC\"><div align=\"center\">验证点</td>\r\n");
 		msg.append("<td width=\"36\" bgcolor=\"#0099CC\"><div align=\"center\">结果</td>\r\n");
 		msg.append("</tr>\r\n");
 	   return msg.toString();
    }
    
    /**
     * 获取文件下的目录名称，无排序
     * @param path
     * @return
     * @author linchaojiang
     */
    public static List<String> getFileName(String path) {
 	   List<String> fileName = new ArrayList<String>();
 	   File f = new File(path);
 	   if (!f.exists()) {
 	       System.out.println(path + " not exists");
 	    }	    
 	   File fa[] = f.listFiles();
 	   if(fa.length>0){
 		  for (int i = 0; i < fa.length; i++) {
 	 	       File fs = fa[i];
 	 	       if(!(fs.getName().equals("index.html")||fs.getName().equals("screenShot")||fs.getName().equals("left.html"))){
 	 	    	  fileName.add(fs.getName());
 	 	       }       
 	 	   } 
 	   }	   
	   return fileName;
 	}

    /**
     * 获取目录下文件夹名称，安时间倒序排序
     * @param fliePath
     * @author linchaojiang
     */
	public static List<String> getFileName_orderByDate(String fliePath) { 
		
		List<String> fileName = new ArrayList<String>();
		try {
			File file = new File(fliePath);  
			File[] fs = file.listFiles();  
			Arrays.sort(fs,new Comparator< File>(){  
				public int compare(File f1, File f2) {
				    long diff = f1.lastModified() - f2.lastModified();  
				    if (diff > 0) 
				        return 1;  
				    else if (diff == 0)  
				        return 0;  
				    else  
				        return -1;  
			    } 			
			    public boolean equals(Object obj) { 	    	
				    return true;  
				}  		          
			}); 		
			for (int i = fs.length-1; i >-1; i--) { 			
				if(!(fs[i].getName().equals("index.html")||fs[i].getName().equals("screenShot")||fs[i].getName().equals("left.html")||fs[i].getName().equals(".svn")||fs[i].getName().equals("log")||fs[i].getName().equals("videoShot"))){
					fileName.add(fs[i].getName());
			       }    
			    //System.out.println(new Date(fs[i].lastModified()));  
			 }
		} catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("获取目录:"+fliePath+",文件名称异常，请检查目录是否正确！！！");
		}
		return fileName;  
	} 
	
	/**
	 * 删除文件夹及下的文件
	 * @param filepath
	 * @author linchaojiang
	 */
	public static void delDirectoryAndFile(String filepath) { 
		
		try {
			File f = new File(filepath);//定义文件路径         
			if(f.exists() && f.isDirectory()){//判断是文件还是目录  
			   if(f.listFiles().length==0){//若目录下没有文件则直接删除  
			      f.delete();  
			   }else{//若有则把文件放进数组，并判断是否有下级目录  
			       File delFile[]=f.listFiles();  
			       int i =f.listFiles().length;  
			       for(int j=0;j<i;j++){  
			           if(delFile[j].isDirectory()){  
			        	   delDirectoryAndFile(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径  
			           }
			           delFile[j].delete();//删除文件  
			         }  
			    }  
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.logInfo("删除文件夹及文件夹下文件异常,删除的目录为:"+filepath);
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
	public static String getMaxDate(List<String> list) throws ParseException{
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    int n = list.size();   	  
	    for (int i = 0; i < n - 1; i++) {
	    	for (int j = 0; j < n - 1; j++) { 	    	  
	    		Date date1 = format.parse(list.get(j));
	    		Date date2 = format.parse(list.get(j+1));
	    		if(date1.getTime()>date2.getTime()){
	    			String temp = list.get(j);
	    			list.set(j, list.get(j+1));
	    			list.set(j+1, temp);
	    		} 	  
	    	}   	  
	    }   
	    return list.get(list.size()-1);
	}
		
}
