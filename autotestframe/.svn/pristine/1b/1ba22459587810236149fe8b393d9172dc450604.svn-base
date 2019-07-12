package com.globalegrow.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;

import com.globalegrow.base.TestngListener2;

/**
 *  
 * log日志
 * @author linchaojiang
 * 创建时间：2016-08-23
 * 更新时间：2016-08-31
 *
 */  
public class Log {
	private static Logger logger;

    private static String filePath = "config/Log4j.properties";

    private static boolean flag = false;
    
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static synchronized void getPropertyFile() {
        logger = Logger.getLogger("TestProject");
        PropertyConfigurator.configure(new File(filePath).getAbsolutePath());
        flag = true;
    }

    private static void getFlag() {
        if (flag == false)
            Log.getPropertyFile();
    }

    public static void logInfo(Object message) {
        Log.getFlag();
        logger.info(message);
        /**
		 * 日志写入logStr
		 */
		TestngListener2.logStr.append("INFO "+formatter.format(new Date())+": "+message+"\r\n");
       
    }

    public static void logError(Object message) {
        Log.getFlag();
        logger.error(message);
        /**
		 * 日志写入logStr
		 */
		TestngListener2.logStr.append("ERROR "+formatter.format(new Date())+": "+message+"\r\n");
        Assert.assertTrue(false);
    }

    public static void logWarn(Object message) {
        Log.getFlag();
        logger.warn("INFO"+formatter.format(new Date())+": "+message);
        /**
		 * 日志写入logStr
		 */
		TestngListener2.logStr.append("WARW "+formatter.format(new Date())+": "+message+"\r\n");
    }
    
	public static void cleanLog(){
		/**
		 * 清除StringBuffer内容
		 */
		int  sb_length = TestngListener2.logStr.length();
		TestngListener2.logStr.delete(0,sb_length);
	}	
	
	public static void writeLogToFile(String path){
		/**
		 * 把日志写入文件
		 */
		try {			
			String fileName = TestngListener2.test_id+".txt";
			FileUtil.createFile(path, fileName);
			FileUtil.writeMsgToFile(path+"/"+fileName, TestngListener2.logStr.toString());			
		} catch (Exception e) {
			Log.logInfo("日志写入文件异常！！！");
			Log.logInfo(e.getMessage());		
		}
		
	}
}
