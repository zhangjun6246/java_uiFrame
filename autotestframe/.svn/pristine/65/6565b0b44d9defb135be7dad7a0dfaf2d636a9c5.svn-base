package com.globalegrow.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.ITestContext;

import com.globalegrow.base.Startbrowser;

/**
 *  
 * 把StringBuffer类型的信息写入文件
 * @author linchaojiang
 * 创建时间：2016-08-23
 * 更新时间：2016-09-27 by linchaojiang
 *
 */ 
public class CreateHtmlFile {
	
	FileOutputStream outputStream = null;
	
	public void htmlReport(StringBuffer msg,String fileName,String path) throws IOException{
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
	  * 通过递归得到某一路径下所有的目录及其文件
	  */
	public static ArrayList<String> getFiles_01(String filePath){
		 ArrayList<String> filelist = new ArrayList<String>();
		 ArrayList<String> fileNameList = new ArrayList<String>();
		 File root = new File(filePath);		  
		 File[] files = root.listFiles();
		 for(File file:files){
		     if(file.isDirectory()){//递归调用
		    	 getFiles_01(file.getAbsolutePath());
		     filelist.add(file.getAbsolutePath());
		     }else{
		    	 if((!file.getName().endsWith("index.html"))&&(!file.getName().endsWith("left.html"))){
		    		 fileNameList.add(file.getName());
		    	 }	    	 
		    }     
		 }
		 /*for(int i=fileNameList.size()-1;i>=0;i--){
			 System.out.println(fileNameList.get(i));
		 }*/
		return fileNameList;
	}
	
   /**  
    * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true  ，在原有文件的末尾增加文件  
    * @param fileName  
    * @param content  
    * @return 
    * @author linchaojiang
    */   
   public  static   void  writeMsgToFile(String file, String conent) {  
       BufferedWriter out = null ;  
       try  {  
           out = new  BufferedWriter( new  OutputStreamWriter(  
                   new  FileOutputStream(file,  true )));  
           out.write(conent);  
       } catch  (Exception e) {  
           e.printStackTrace();
       } finally  {  
           try  {  
               out.close();  
           } catch  (IOException e) {  
               e.printStackTrace();  
           }  
       }  
   } 
   
   
   /**
    * 创建文件
    * @param path
    * @param fileName
    * @throws IOException
    */
   public static void createFile(String path,String fileName){
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
				e.printStackTrace();
			}
		}else{
			if(!file2.exists()){//如果文件不存在，创建文件					
				try {
					file2.createNewFile();//创建文件
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("创建文件异常！！！");
				}
			}
		}
   }
   
   /**
    * 写html，index.html信息
    * @param spath
    * @return
    */
   public static String index(String spath){
   	List<File> fileName = new ArrayList<File>();
   	fileName = getFileSort(spath);
   	StringBuffer msg = new StringBuffer();   
   	msg.append("<html>\r\n"); 
   	msg.append("<frameset rows=\"*\" cols=\"15%,85%\">\r\n");
   	msg.append("<frame src=\"left.html\"/>\r\n");
   	for(int i=0;i<fileName.size();i++){
		if(fileName.get(i).getName().contains("index.html")){
			continue;
		}
		if(fileName.get(i).getName().contains("left.html")){
			continue;
		}
		msg.append("<frame src=\""+fileName.get(i).getName().replace("#", "%23")+"\" name=\"view_frame\" />\r\n");
		break;
	}  	
   	msg.append("</frameset>\r\n");
   	msg.append("</html>\r\n");
	    return msg.toString();	
   }
   
   /**
    * 写html，左侧导航信息
    * @param spath
    * @param tc
    * @return String
    * @author linchaojiang
    */
   public static String left(String spath,ITestContext tc){
	   	StringBuffer msg = new StringBuffer();   
	   	msg.append("<html>\r\n");
	   	msg.append("<head>\r\n");
	   	msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />\r\n");
	   	msg.append("<title>自动化测试报告</title>\r\n");
	   	msg.append("</head>\r\n");
	   	msg.append("<body>\r\n");
	   	msg.append("<div id=\"sidebarHeader\">\r\n");
	   	msg.append("<h3>Test Results Report</h3>\r\n");
	   	msg.append("</div>\r\n");
	   	msg.append("<h3>"+tc.getSuite().getName()+"</h3>\r\n");
	   	msg.append("<ul>\r\n");  	
	   	List<File> fileName = new ArrayList<File>();
	   	fileName = getFileSort(spath);
	   	for(int i=0;i<fileName.size();i++){
	   		String showName = null;
	   		if(fileName.get(i).getName().contains("blue")){
	   			if(fileName.get(i).getName().split("_").length>=2){
	   				showName = fileName.get(i).getName().split("_")[0]+"_"+fileName.get(i).getName().split("_")[1];
	   			}else {
	   				showName = fileName.get(i).getName().replace("_blue.html", "");
				} 			
	   			msg.append("<li><a href=\""+fileName.get(i).getName().replace("#", "%23")+"\" target=\"view_frame\"><font color=\"blue\">"+showName+"</font></a></li>\r\n");  
	   		}
	    	if(fileName.get(i).getName().contains("yellow")){
	    		if(fileName.get(i).getName().split("_").length>=2){
	   				showName = fileName.get(i).getName().split("_")[0]+"_"+fileName.get(i).getName().split("_")[1];
	   			}else {
	   				showName = fileName.get(i).getName().replace("_yellow.html", "");
				} 	
	    		msg.append("<li><a href=\""+fileName.get(i).getName().replace("#", "%23")+"\" target=\"view_frame\"><font color=\"#CC9900\">"+showName+"</font></a></li>\r\n");  	
	    	}
			if(fileName.get(i).getName().contains("red")){
				if(fileName.get(i).getName().split("_").length>=2){
	   				showName = fileName.get(i).getName().split("_")[0]+"_"+fileName.get(i).getName().split("_")[1];
	   			}else {
	   				showName = fileName.get(i).getName().replace("_red.html", "");
				} 	
				msg.append("<li><a href=\""+fileName.get(i).getName().replace("#", "%23")+"\" target=\"view_frame\"><font color=\"red\">"+showName+"</font></a></li>\r\n");  
			}
	   	}	 	
	   	msg.append("</ul></body></html>\r\n");
	    return msg.toString();	
   }
 
   /**
    * 写html，test运行前信息
    * @param osName
    * @return String
    */
   public static String testHead(String osName){
	   
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
				+ " width=\"90%\" height=\"100%\">\r\n");
		msg.append("<tr height=\"63\">\r\n");
		msg.append("<td height=\"63\" colspan=\"6\" bgcolor=\"#006699\"><div align=\"center\" " + "class=\"STYLE1\">"
				+ Startbrowser.projectName + "自动化测试报告</div></td>\r\n");
		msg.append("</tr>\r\n");
		msg.append("<tr height=\"24\">\r\n");
		msg.append("<td width=\"95\" height=\"24\"><div align=\"right\">测试项目:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + Startbrowser.projectName + "</td>\r\n");
		msg.append("</tr>\r\n");
		msg.append("<tr height=\"24\">\r\n");
		msg.append("<td height=\"24\"><div align=\"right\">浏览器:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + Startbrowser.browserName +" &lt;&lt;&lt;"+"&gt;&gt;&gt;"+ "</td>\r\n");
		msg.append("</tr>\r\n");
		msg.append("<tr height=\"24\">\r\n");
		msg.append("<td height=\"24\"><div align=\"right\">操作系统:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
		msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" + osName + " &lt;&lt;&lt;"+"&gt;&gt;&gt;" + "</td>\r\n");
		msg.append("</tr>\r\n");
		msg.append("<tr height=\"19\">\r\n");
		msg.append("<td width=\"19\" bgcolor=\"#0099CC\"><div align=\"center\">TESTCASE</div></td>\r\n");
		msg.append("<td width=\"79\" bgcolor=\"#0099CC\"><div align=\"center\">模块</div></td>\r\n");
		msg.append("<td width=\"99\" bgcolor=\"#0099CC\"><div align=\"center\">测试项</div></td>\r\n");		
		msg.append("<td width=\"250\" bgcolor=\"#0099CC\"><div align=\"center\">测试描述</div></td>\r\n");		
		msg.append("<td width=\"300\" bgcolor=\"#0099CC\"><div align=\"center\">验证点</td>\r\n");
		msg.append("<td width=\"36\" bgcolor=\"#0099CC\"><div align=\"center\">结果</td>\r\n");
		msg.append("</tr>\r\n");
	   return msg.toString();
   }
 
   /**
    * 写html，test运行成功信息
    * @param testCaseName
    * @param dateSign
    * @return String
    */
   public static String testBodySuccess(String testCaseName,String imgName){
	   StringBuffer msg = new StringBuffer();
	   try {
		   msg.append("<tr height=\"21\">\r\n");
		   msg.append("<td align=\"center\">" + testCaseName + "</td>\r\n");
		   msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + Startbrowser.testCasemap.get(testCaseName).get(0) + "</td>\r\n");
		   msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + Startbrowser.testCasemap.get(testCaseName).get(1) + "</td>\r\n");
		   msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + Startbrowser.testCasemap.get(testCaseName).get(2) + "</td>\r\n");		
		   msg.append("<td align=\"left\">" + "&nbsp;&nbsp;" + Startbrowser.testCasemap.get(testCaseName).get(3) + "</td>\r\n");
		   if (Startbrowser.screenShotWhilePassFlag) {
			   msg.append("<td align=\"center\"><a href=\"screenShot/"+Startbrowser.projectName +"/"+imgName+"\""+" target=\"_blank\" >"
			   		+ "<font color=\"#00FF00\">PASS</font></a></td>\r\n");
		   } else {
			   msg.append("<td align=\"center\"><font color=\"#00FF00\">PASS</font></td>\r\n");
		   }
		   msg.append("</tr>\r\n");
		   return msg.toString();
	 } catch (Exception e) {
		 Log.logInfo(e.getMessage());
		 Log.logInfo("写报告出错！！！！");
		 return null; 
	 }
	   
   }
   
   /**
    * 写html，test运行失败信息
    * @param testCaseName
    * @param dateSign
    * @return String
    * @author linchaojiang
    */
   public static String testBodyFailure(String testCaseName,String imgName){
	   StringBuffer msg = new StringBuffer();
	   try {
		   msg.append("<tr height=\"21\">\r\n");
		   msg.append("<td height=\"21\" align=\"center\"><div align=\"center\">"+testCaseName+"</td>\r\n");
		   msg.append("<td align=\"left\">"+ "&nbsp;&nbsp;"+Startbrowser.testCasemap.get(testCaseName).get(0)+"</td>\r\n");
		   msg.append("<td align=\"left\">"+ "&nbsp;&nbsp;"+Startbrowser.testCasemap.get(testCaseName).get(1)+"</td>\r\n");
		   msg.append("<td align=\"left\">"+ "&nbsp;&nbsp;"+Startbrowser.testCasemap.get(testCaseName).get(2)+"</td>\r\n");			;
		   msg.append("<td align=\"left\">"+ "&nbsp;&nbsp;"+Startbrowser.testCasemap.get(testCaseName).get(3)+"</td>\r\n");
		   msg.append("<td align=\"center\"><a href=\"screenShot/"+Startbrowser.projectName+"/"+imgName+"\""+"  target=\"_blank\" >"
		   		+ "<font color=\"#FF0000\">FAIL</font></a></td>\r\n");
		   msg.append("</tr>\r\n");
		   return msg.toString();
	   } catch (Exception e) {
		   Log.logInfo(e.getMessage());
		   Log.logInfo("写报告出错！！！！");
		   return null; 
	   }
	   
   }
   
   /**
    * 写html，test结尾信息
    * @param SDate
    * @param EDate
    * @param runSummaryTime
    * @param runSuccessTime
    * @param runTimeStr
    * @param throughputRate
    * @return String
    * @author linchaojiang
    */
   public static String testEnd(String SDate,String EDate,int runSummaryTime,int runSuccessTime,
		   String runTimeStr,String throughputRate){
	   
	    StringBuffer msg = new StringBuffer();
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">开始执行时间:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +SDate+"</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">结束执行时间:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +EDate+"</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">用例执行总数:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +runSummaryTime+"</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">用例成功数据:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +runSuccessTime+"</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">用例执行时长:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +runTimeStr+"</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("<tr height=\"24\">\r\n");
	    msg.append("<td height=\"24\"><div align=\"right\">通过率:" + "&nbsp;&nbsp;" + "</div></td>\r\n");
	    msg.append("<td colspan=\"5\">" + "&nbsp;&nbsp;" +throughputRate+"%</td>\r\n");
	    msg.append("</tr>\r\n");
	    msg.append("</table></body></html>\r\n");
	   
	   return msg.toString();
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
    * 获取目录下所有文件(按时间排序,由近及远)
    * @param path
    * @return List
    * @author linchaojiang
    */
   public static List<File> getFileSort(String path) {

       List<File> list = getFiles(path, new ArrayList<File>());

       if (list != null && list.size() > 0) {

           Collections.sort(list, new Comparator<File>() {
               public int compare(File file, File newFile) {
                   if (file.lastModified() < newFile.lastModified()) {
                       return 1;
                   } else if (file.lastModified() == newFile.lastModified()) {
                       return 0;
                   } else {
                       return -1;
                   }

               }
           });

       }

       return list;
   }
   
   /** 
    * 获取目录下所有文件 
    * @param realpath
    * @param files
    * @return List
    * @author linchaojiang
    */
   public static List<File> getFiles(String realpath, List<File> files) {

       File realFile = new File(realpath);
       if (realFile.isDirectory()) {
           File[] subfiles = realFile.listFiles();
           for (File file : subfiles) {       	   
               if (!file.isDirectory()) {
            	   files.add(file);
               }              
               //如果需要递归路径下的所有目录，用以下方法把文件添加到list中
              /* if (file.isDirectory()) {
                   getFiles(file.getAbsolutePath(), files);
               } else {
                   files.add(file);
               }*/
           }
       }
       return files;
   }


}