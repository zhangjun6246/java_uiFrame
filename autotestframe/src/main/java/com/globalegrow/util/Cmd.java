package com.globalegrow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.globalegrow.base.TestngListener2;
import com.globalegrow.code.Page;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Cmd {
	
	static Connection conn = null;
	static String hostname = "10.40.6.232";
	static String username = "root";
	static String password = "zhou123";
	static int port = 36000;

	/**
	 * 根据进程id,结束进程
	 * @param pid
	 * @author linchaojiang
	 * 创建时间:2016-11-30,最后更新时间:2016-11-30
	 */
    public static void KillTaskPid(String pid){
        String Command = "cmd /c taskkill /F /PID "+pid;
        System.out.println("Command："+Command);
        String killComd = "cmd /C start wmic process where name='cmd.exe' call terminate";
        Log.logInfo("kill进程:" + pid + " task ...");
        runCommand(Command);
        runCommand(killComd);
    }
    
    /**
     * 根据任务管理器中的名称结束进程
     * @param taskname
     * @author linchaojiang,创建时间:2016-12-02,最后更新时间:2016-12-02
     */
    public static void KillTaskName(String taskname){
         String Command = "cmd /c taskkill /F /IM " + taskname;
         Log.logInfo("kill " + taskname + " task ...");
         runCommand(Command);
     }
	
    /**
     * 执行dos命令
     * @param command
     * @return
     * @author linchaojiang
     * 创建时间:2016-11-30,最后更新时间:2016-11-30
     */
    public static void runCommand(String command){
    	Log.logInfo("执行命令:"+command);
        try {   
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    
    
    /**
     * 根据端口,获取进程id
     * @param port
     * @return
     * 创建时间:2016-11-30,最后更新时间:2016-11-30
     */
    public static List<String> getPid(int port){
    	
		List<String> result = new ArrayList<String>();
        try {
            String cmd = "cmd /c netstat -ano|findstr "+port;  
            Runtime rt = Runtime.getRuntime(); // 获取运行时系统  
            Process proc = rt.exec(cmd); // 执行命令  
            InputStream stderr =  proc.getInputStream(); // 获取输入流  
            InputStreamReader isr = new InputStreamReader(stderr);  
            BufferedReader br = new BufferedReader(isr);  
            String line = null;  
            while ((line = br.readLine()) != null) { // 打印出命令执行的结果  
            	result.add(line.split(" ")[line.split(" ").length-1]);
            }  
        } catch (Throwable t) {  
        	Log.logInfo("获取进程Pid异常:"+t.getMessage());  
        }
		return result; 
	}
    
	/**
	 * 通过传入设备名称,连接设备
	 * @param devices
	 * @author linchaojiang
	 * 创建时间:2016-11-30,最后更新时间:2017-02-10
	 */
	public static boolean connectDevice_old(String devices){
		boolean flag = false;
	    List<String> result = getDosResult("cmd /c adb devices");	
	    int connetflag = 0;
	    Log.logInfo("result:"+result.size());
	    for(String dd:result){
	    	Log.logInfo("getDosResult(cmd /c adb devices):"+dd);
	    }
	    
	    OK:
	    	if(result.size()>0){
	    		if(result.get(0).equals("List of devices attached")){
		    		Log.logInfo("adb命令正常·······"); 
		    		for(String ss: result){
		    			if(ss.contains(devices)&&ss.contains("device")){
			    			Log.logInfo(devices+"已经连接，设备信息："+ss);
			    			flag = true;
			    			connetflag = 1;
			    			break OK;
			    		}
		    	    }
		    		for(String sss: result){
		    			if(sss.contains(devices)&&sss.contains("offline")){
		    				String command1 = "cmd /c adb disconnect "+devices;
		    				String command2 = "cmd /c adb connect "+devices;
		    				runCommand(command1);
		    				Page.pause(3);
		    				List<String> result33 = getDosResult(command2);	
		    				for(String ss33: result33){
		    	    			if(ss33.contains(devices)&&ss33.contains("device")){
		    		    			Log.logInfo(devices+"已经连接，设备信息："+ss33);
		    		    			flag = true;
		    		    			connetflag = 2;
		    		    			break OK;
		    		    		}
		    	    	    }
		    				Log.logInfo("1."+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
		    				connetflag = 3;
		    				break OK;
		    			}
		    	    }
		    		
		    		OK2:
			    		if(connetflag == 0){
			    			Log.logInfo("开始连接设备:"+devices);
				    		String connectDevice = "cmd /c adb connect "+devices; 
				    		List<String> result2 = getDosResult(connectDevice);
				    		//connected to 10.32.1.43:5555
				    		String adblist = "cmd /c adb devices";
				    		List<String> result_adblist = getDosResult(adblist);
				    		
				    		Log.logInfo("result2:"+result2.size());
				    		for(String dd:result2){
				    	    	Log.logInfo("getDosResult(cmd /c adb connect devices):"+dd);
				    	    }
				    		
				    		 if(result2.size() > 0){
				    			 for(String ss22: result2){
			    	    			if(ss22.contains(devices)&&ss22.contains("unauthorized")){
			    		    			Log.logInfo(devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+ss22);
			    		    			break OK2;
			    		    		}
				    			 }
				    			 for(String ssff: result_adblist){
				    	    			if(ssff.contains(devices)&&ssff.contains("unauthorized")){
				    		    			Log.logInfo(devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+ssff);
				    		    			break OK2;
				    		    		}
					    		 }
				    			 String mgs = "connected to "+devices;
					    	     boolean flag1 = result2.get(0).contains(mgs);
					    	     Log.logInfo("flag1:"+flag1);
					    	     boolean flag2 = false;
					    	     for(String sss:result_adblist){
					    	    	Log.logInfo("result_adblist:"+sss);
					    	    	if(sss.contains(devices)&&sss.contains("device")){
					    	    		flag2 = true;
					    	    		break;
					    	    	}
					    	     }
					    	     if(flag1&&flag2){ 
					    	    	flag = true;
					    	    	Log.logInfo("已连接上设备"+devices);
					    	    	break OK2;
						    	  }	  
				    		 }
				    		 List<String> result44 = getDosResult("cmd /c adb devices");
			    			 for(String ss44: result44){
		    	    			if(ss44.contains(devices)&&ss44.contains("offline")){
		    	    				Log.logInfo("2."+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
		    		    			break OK2;
		    	    			}		
		    		    	 }
			    		}
		    		
	    		}else {
	    			Log.logInfo("adb命令无法使用,请检查环境······");
				}
	    	}else {
	    		Log.logInfo("执行dos命令异常,请检查环境········");
			}
	    
		return flag;	
	}
		
	public static boolean connectDevice(String devices){
		
		boolean flag = false;
		List<String> result = getDosResult("cmd /c adb devices");
		int connetflag = 0;
		for(String s1:result){
			if(s1.contains(devices)){
				connetflag = 1;
			}
		}		
		if(connetflag == 1){
			for(String s2: result){
    			if(s2.contains(devices)&&s2.contains("device")){
	    			Log.logInfo("设备:"+devices+"已经连接，设备信息："+s2);
	    			return true;
	    		}else if (s2.contains(devices)&&s2.contains("offline")) {
	    			Log.logInfo("设备:"+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
	    			runCommand("cmd /c adb disconnect "+devices);
	    			return false;
				}else if (s2.contains(devices)&&s2.contains("unauthorized")) {
					Log.logInfo("设备:"+devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+s2);
					return false;
				}/*else{
					Log.logInfo(devices+"设备链接有问题，设备链接信息："+s2);
					return false;
				}*/
    	    }			
		}else{
			Log.logInfo("开始连接设备:"+devices);
    		String connectDevice = "cmd /c adb connect "+devices; 
    		runCommand(connectDevice);
    		Page.pause(4);
    		List<String> result2 = getDosResult("cmd /c adb devices");
    		
    		for(String s3: result2){
    			if(s3.contains(devices)&&s3.contains("device")){
	    			Log.logInfo("设备:"+devices+"已经连接，设备信息："+s3);
	    			return true;
	    		}else if (s3.contains(devices)&&s3.contains("offline")) {
	    			Log.logInfo("设备:"+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
	    			runCommand("cmd /c adb disconnect "+devices);
	    			return false;
				}else if (s3.contains(devices)&&s3.contains("unauthorized")) {
					Log.logInfo("设备:"+devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+s3);
					return false;
				}/*else{
					Log.logInfo(devices+"设备链接有问题，设备链接信息："+s3);
					return false;
				}*/
    		}
		}
		Log.logInfo(devices+":设备不存在,或者手机未开启监听端口！！！");
		return flag;				
	}
	
	/**
	 * 执行dos命令,并获取执行的结果
	 * @param cmd
	 * @return
	 * @author linchaojiang
	 * 创建时间:2016-11-30,最后更新时间:2016-11-30
	 */
    public static  List<String> getDosResult(String cmd){
    	Log.logInfo("执行命令:"+cmd);
		List<String> result = new ArrayList<String>();
        try { 
            Runtime rt = Runtime.getRuntime(); // 获取运行时系统  
            Process proc = rt.exec(cmd); // 执行命令  
            InputStream stderr =  proc.getInputStream(); // 获取输入流  
            InputStreamReader isr = new InputStreamReader(stderr);  
            BufferedReader br = new BufferedReader(isr);  
            String line = null;  
            while ((line = br.readLine()) != null) { // 打印出命令执行的结果  
            	result.add(line); 
            }  
        } catch (Throwable t) {  
        	Log.logInfo("执行dos命令获取内容异常:"+t.getMessage());  
        }
		return result; 
	}
       
    /**
     * 启动appium服务器
     * @param port
     * @param udid
     * @author linchaojiang
     * 创建时间:2016-11-30,最后更新时间:2016-11-30
     */
    public static void runServer(int port,String udid) {
    	Log.logInfo("run " + udid + " Appium Server in port " + port + "...");
        int bpport = port +1;
        int chromeport = port + 4792;
        //多设备server端需要手动指定每台设备的udid,安卓无线连接下就是设备的ip:port..
        String Command = "cmd /c appium  -p " + port + " -bp " + bpport + " --session-override --chromedriver-port "+ chromeport 
        		+" -U " + udid +  " >C://aa//" + port + ".txt";
        Log.logInfo("启动appium命令:"+Command);
        runCommand(Command);     
    }
    
	/**
	 * 判断是否有空闲的设备,若存在返回第一个可用的端口号
	 * @return int
	 * @author linchaojiang
	 * 创建时间:2016-12-07,最后更新时间:2016-12-07
	 */
	public static String isFreeDevices(){
		
		PropUtil.GetProperties("resources/devices.properties");
		Log.logInfo("开始检查空闲设备······");
		String port = "0";
		String[] portList = PropUtil.get("port").split(",");	
		String[] ipList = PropUtil.get("ip").split(",");		
		for(int i = 0; i < portList.length; i++){
			int sumPort = 0;
			for(int j = 0; j < ipList.length; j++){
				String requestUrl = "http://"+ipList[j]+":"+portList[i]+"/wd/hub/status";
				boolean bconnect = getStatus(requestUrl);
				if(bconnect){
					Log.logInfo(portList[i]+" 端口已在机子:"+ipList[j]+"启动");
					continue;//如果发现有一个连续突出循环
				}else {
					sumPort++;
				}	
			}			
			if(sumPort == ipList.length){
				port = portList[i];
				break;
			}
		}
		return port;
	}
	
	/**
	 * 判断appium服务是否启动
	 * @param adress
	 * @return boolean
	 * @author linchaojiang
	 * 创建时间:2016-12-07,最后更新时间:2016-12-07
	 */
	public static boolean getStatus(String adress){
		boolean flag = false;
		Map<String, String> result = new HashMap<String, String>();
		try {
			result = Pub.get(adress);
			if(result.get("Code").equals("200")){
				flag = true;
			}
		} catch (Exception e) {
			//Log.logInfo("执行异常:"+e.getMessage());
		}	
		return flag;
	}
	
	/**
	 * 关掉appium服务和断开adb链接
	 * @param freePort
	 * @param freeDeviceName
	 * @author linchaojiang
	 * 创建时间:2017-01-13,最后更新时间:2017-01-13
	 */
	public static void StopServer(String freePort,String freeDeviceName){
		List<String> result = Cmd.getPid(Integer.parseInt(freePort));
		for(int i = 0; i < result.size(); i++){
			Cmd.KillTaskPid(result.get(i));
		}
		List<String> dosResult = Cmd.getDosResult("cmd /c adb devices");
		Log.logInfo("开始断开设备链接·······"); 
		for(String ss: dosResult){
			if(ss.contains(freeDeviceName)&&ss.contains("device")){	
    			List<String> disconnDevices = Cmd.getDosResult("cmd /c adb disconnect "+freeDeviceName);
    			if(disconnDevices.size()>0){
    				String disconnMsg = "disconnected "+freeDeviceName;
    				if(disconnDevices.get(0).contains(disconnMsg)){
    					Log.logInfo(freeDeviceName+"设备已断开链接,断开链接信息:"+disconnDevices.get(0));
    				}else {
    					Log.logInfo(freeDeviceName+"设备断开异常,控制台打印信息:"+disconnDevices.get(0));
					}
    			}  			
    			break;
    		}
	    }
	}
	
    /**
     * 启动appium服务器
     * @param port
     * @param udid
     * @author linchaojiang
     * 创建时间:2016-11-30,最后更新时间:2016-11-30
     */
    public static void runServer_linux(int port,String udid,String fileName) {
    	
    	Log.logInfo("run " + udid + " Appium Server in port " + port + "...");
        int bpport = port +1;
        int chromeport = port + 4792;
        String logFileName = "/home/jenkin/appium_log/"+fileName+".log";
        String Command = "bash /home/jenkin/shell/startAppium.sh "+port+" " +bpport+ " "+chromeport+" "+udid+ " "+logFileName;
        Log.logInfo("启动appium命令:"+Command);
        runShell(Command);         
    }
	
	/**
	 * 关掉appium服务和断开adb链接
	 * @param freePort
	 * @param freeDeviceName
	 * @author linchaojiang
	 * 创建时间:2017-01-19,最后更新时间:2017-01-19
	 */
	public static void StopServer_linux(String freePort,String freeDeviceName){
		
		killAppium(freePort);
		List<String> shellResult = Cmd.getRunShellRusult("/usr/local/android_sdk/platform-tools/adb devices");
		Log.logInfo("开始断开设备链接·······"); 
		for(String ss: shellResult){
			if(ss.contains(freeDeviceName)&&ss.contains("device")){	
    			List<String> disconnDevices = Cmd.getRunShellRusult("/usr/local/android_sdk/platform-tools/adb disconnect "+freeDeviceName);
    			if(disconnDevices.size()>0){
    				String disconnMsg = "disconnected "+freeDeviceName;
    				if(disconnDevices.get(0).contains(disconnMsg)){
    					Log.logInfo(freeDeviceName+"设备已断开链接,断开链接信息:"+disconnDevices.get(0));
    				}else {
    					Log.logInfo(freeDeviceName+"设备断开异常,控制台打印信息:"+disconnDevices.get(0));
					}
    			}  			
    			break;
    		}
	    }
	}
	
	/**
	 * 
	 * @param freePort
	 * @author linchaojiang
	 */
	public static void killAppium(String freePort){
		
		String as = "netstat -apn|grep "+freePort;
		List<String> a = Cmd.getRunShellRusult(as);
		String ipport = "0 0.0.0.0:"+freePort;
		String pidid2 = "";
		for(String ss:a){
			if(ss.contains("/node")&&ss.contains(ipport)){
				String pidid = ss.split(" ")[ss.split(" ").length-1];
				pidid2 = pidid.split("/")[0];
				break;
			}
		}
		String bs = "ps -aux|grep "+pidid2;
		List<String> b = Cmd.getRunShellRusult(bs);
		
		String killId = "";
		OK:
		for(String cc: b){
			if(cc.contains("pidid2")&&cc.contains("appium")){
				String[] ttStrings = cc.split(" ");
				for(int i=0;i<ttStrings.length;i++){
					if((i>0)&&(!ttStrings[i].equals(" "))){
						killId = ttStrings[i];
						break OK;
					}
				}
			}
		}
		String cString = "kill -s 9 "+killId;
		Cmd.runShell(cString);
	}
	
	/**
	 * linux系统中,结束node进程
	 * @author linchaojiang
	 * 创建时间:2017-05-10,最后更新时间:2017-05-10
	 */
	public static void stopNodeSever(String port){
		String shell = "netstat -nlp | grep :"+port+" | awk '{print $7}'";
		List<String> runResult = Cmd.getRunShellRusult(shell);
		for (int i = 0; i < runResult.size(); i++) {
			String process_id = runResult.get(i).split("/")[runResult.get(i).split("/").length-1];
			String cString = "kill -s 9 "+process_id;
			Cmd.runShell(cString);
		}		
	}
	
	/**
	 * 
	 * @param shell
	 */
	public static void runShell(String shell){
    	try {
    		Log.logInfo("执行命令:"+shell);
    		Runtime.getRuntime().exec(shell); 
    	} catch (Exception e) {
    		Log.logInfo("执行linux命令异常:"+shell);
    		e.printStackTrace();
    	}
    }
	
	/**
	 * 
	 * @param shell
	 */
	public static void runShell1(String[] shell){
    	try {
    		Log.logInfo("执行命令:"+shell);
    		Runtime.getRuntime().exec(shell); 
    	} catch (Exception e) {
    		Log.logInfo("执行linux命令异常:"+shell);
    		e.printStackTrace();
    	}
    }
	
	/**
	 * 
	 * @param shell
	 * @return
	 */
    public static List<String> getRunShellRusult(String shell){

		// TODO Auto-generated method stub   	
        Process process = null;  
        List<String> processList = new ArrayList<String>();  
        try {  
        	Log.logInfo("执行命令:"+shell);
            process = Runtime.getRuntime().exec(shell);  
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            String line = "";  
            while ((line = input.readLine()) != null) {  
                processList.add(line);  
            }  
            input.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
            Log.logInfo("执行linux命令异常:"+shell);
        }  
        for (String line : processList) {  
        	Log.logInfo(line);  
        }
		return processList;  
    }
    
	/**
	 * 通过传入设备名称,连接设备
	 * @param devices
	 * @author linchaojiang
	 * 创建时间:2016-11-30,最后更新时间:2016-11-30
	 */
	public static void connectDevice_linux(String devices){
		
		String shell = "/usr/local/android_sdk/platform-tools/adb devices";
	    List<String> result = getRunShellRusult(shell);	     	     
	    OK:
	    if(result.size()>0){
	    	if(result.get(0).equals("List of devices attached")){
	    		Log.logInfo("adb命令正常·······"); 
	    		for(String ss: result){
	    			if(ss.contains(devices)&&ss.contains("device")){
		    			Log.logInfo(devices+"设备连接正常·····");
		    			break OK;
		    		}
	    	    }
	    		for(String sss: result){
	    			if(sss.contains(devices)&&sss.contains("offiline")){
	    				Log.logInfo(devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接····");
	    				break OK;
	    			}
	    	    }
	    		Log.logInfo("开始连接设备:"+devices);
	    		String connectDevice = "/usr/local/android_sdk/platform-tools/adb connect "+devices; 
	    		List<String> result2 = getRunShellRusult(connectDevice);
	    		if(result2.size() != 0){
	    			String msg1 =  "connected to "+devices;
	    			String msg2 =  "already connected to "+devices;
	    			if(result2.get(0).contains(msg1)||result2.get(0).contains(msg2)){
	    				Log.logInfo("连接上设备,连接信息:"+result2.get(0));
	    			}else {
						Log.logInfo("连接设备异常,请检测设备是否存在,或者已开启adb无线连接,异常信息:"+result2.get(0));
					}
	    		}  			
	    	 }else {
				Log.logInfo("adb命令无法使用,请检查环境······");
			}
	     }	
	}
	
	/**
	 * 获取空闲设备
	 * @param operatingSystem:操作系统类型
	 * @return list
	 * @author linchaojiang
	 * 创建时间:2017-02-07,最后更新时间:2017-02-07
	 */
	public static List<String> getFreeDervices(String operatingSystem){		
		//String[] devices = {"10.32.1.135:5555","10.32.1.36:5555"};
		PropUtil.GetProperties("resources/devices.properties");	
		final String[] devices = PropUtil.get("devices").split(",");
		String shell = "";
		List<String> result = new ArrayList<String>();
		List<String> freeDevices = new ArrayList<String>();
		if(operatingSystem.contains("Windows")){
			shell = "cmd /c adb devices";
			result = getDosResult(shell);
		}else if (operatingSystem.contains("Linux")) {
			shell = "/usr/local/android_sdk/platform-tools/adb devices";
			result = getRunShellRusult(shell);
		}	
		for(int i=0;i<devices.length;i++){
			int flag = 0;
			for(int j=0;j<result.size();j++){
				if(result.get(j).contains(devices[i])){
					flag = 1;
					continue;
				}
			}
			if(flag == 0){
				freeDevices.add(devices[i]);
			}
		}
		return freeDevices;		
	}
	
	/**
	 * 
	 * @param operatingSystem
	 * @param deviceName
	 * @return void
	 * @author linchaojiang
	 * 创建时间:2017-02-07,最后更新时间:2017-02-07
	 */
	public static void disconnectDevices(String operatingSystem,String deviceName){
		String shell = "";
		Log.logInfo("开始断开设备:"+deviceName+"链接····");
		if(operatingSystem.contains("Windows")){
			shell = "cmd /c adb disconnect "+deviceName;
		}else if (operatingSystem.contains("Linux")) {
			shell = "/usr/local/android_sdk/platform-tools/adb disconnect "+deviceName;
		}
		try {
			if(operatingSystem.contains("Windows")){
				runCommand(shell);
			}else if (operatingSystem.contains("Linux")) {
				runShell(shell);
			}
			
			Log.logInfo("断开设备:"+deviceName+"链接成功！！");
		} catch (Exception e) {
			// TODO: handle exception
			Log.logInfo("断开设备:"+deviceName+"链接异常！！");
		}
	}
	
	/**
	 * 链接服务器
	 * @param hostname
	 * @param username
	 * @param password
	 * @param port
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-20,更新时间:2017-02-20
	 */
	public static void connect()
			throws IOException {
		try {
			conn = new Connection(hostname,port);
			conn.connect();
			conn.authenticateWithPassword(username, password);
		} catch (Exception e) {
			Log.logInfo("执行异常");
			System.out.println("" + e);
		}
	}

	/**
	 * 执行命令
	 * @param command
	 * @return String
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-20,更新时间:2017-02-20
	 */
	@SuppressWarnings("resource")
	public static String execCommand(String command) throws IOException {

		Session session = conn.openSession(); 
		session.execCommand(command); 
		StreamGobbler stdout = new StreamGobbler(session.getStdout());  
		BufferedReader br = new BufferedReader(new InputStreamReader((stdout),
				"UTF-8"));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		session.close();
		return line;
	}

	/**
	 * 执行命令并返回结果
	 * @param command
	 * @return list
	 * @throws IOException
	 * @author linchaojiang,创建时间:2017-02-20,更新时间:2017-02-20
	 */
	@SuppressWarnings("resource")
	public static ArrayList<String> execCommandd(String command)
			throws IOException {
		connect();
		Session session = conn.openSession();
		session.execCommand(command);
		ArrayList<String> array_result = new ArrayList<String>();
		StreamGobbler stdout = new StreamGobbler(session.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader((stdout),
				"UTF-8"));
		String line;
		while ((line = br.readLine()) != null) {
			array_result.add(line);
		}
		session.close();
		return array_result;
	}
	
	public static boolean connectDevice_linux_01(String devices){
		
		boolean flag = false;
		List<String> result = getRunShellRusult("/usr/local/android_sdk/platform-tools/adb devices");
		int connetflag = 0;
		for(String s1:result){
			if(s1.contains(devices)){
				connetflag = 1;
			}
		}		
		if(connetflag == 1){
			for(String s2: result){
    			if(s2.contains(devices)&&s2.contains("device")){
	    			Log.logInfo("设备:"+devices+"已经连接，设备信息："+s2);
	    			return true;
	    		}else if (s2.contains(devices)&&s2.contains("offline")) {
	    			Log.logInfo("设备:"+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
	    			runShell("/usr/local/android_sdk/platform-tools/adb disconnect "+devices);
	    			return false;
				}else if (s2.contains(devices)&&s2.contains("unauthorized")) {
					Log.logInfo("设备:"+devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+s2);
					return false;
				}/*else{
					Log.logInfo(devices+"设备链接有问题，设备链接信息："+s2);
					return false;
				}*/
    	    }			
		}else{
			Log.logInfo("开始连接设备:"+devices);
    		String connectDevice = "/usr/local/android_sdk/platform-tools/adb connect "+devices; 
    		runShell(connectDevice);
    		Page.pause(4);
    		List<String> result2 = getRunShellRusult("/usr/local/android_sdk/platform-tools/adb devices");
    		
    		for(String s3: result2){
    			if(s3.contains(devices)&&s3.contains("device")){
	    			Log.logInfo("设备:"+devices+"已经连接，设备信息："+s3);
	    			return true;
	    		}else if (s3.contains(devices)&&s3.contains("offline")) {
	    			Log.logInfo("设备:"+devices+"处于离线状态(offerlin),请检查此设备是否在其他机器连接,或者关掉手机的wifi重新打开wifi·····");
	    			runShell("/usr/local/android_sdk/platform-tools/adb disconnect "+devices);
	    			return false;
				}else if (s3.contains(devices)&&s3.contains("unauthorized")) {
					Log.logInfo("设备:"+devices+"未经授权，请点击手机上的允许授权，否则无法进行下一步操作，连接信息："+s3);
					return false;
				}/*else{
					Log.logInfo(devices+"设备链接有问题，设备链接信息："+s3);
					return false;
				}*/
    		}
		}
		Log.logInfo(devices+":设备不存在,或者手机未开启监听端口！！！");
		return flag;				
	}
	
	/**
	 * linux服务器端口前置处理。。。。。
	 * @param port
	 * @author linchaojiang,创建时间:2017-04-05,最后更新时间:2017-04-05
	 */
	 public static void intServerPort(String port){
	
		 List<String> result = new ArrayList<String>();
			String runShell = "netstat -anp|grep "+port;
			result = Cmd.getRunShellRusult(runShell);
			for(String ss:result){
				Log.logInfo("执行命令获取到的结果信息:"+ss);
			}			
			if(result.size()==1){
				if(result.get(0).contains("LISTEN")||result.get(0).contains("LISTENING")
						||result.get(0).contains("ESTABLISHED")){
					Log.logInfo(port+"端口已用任务运行！！！");
					
				}else if (result.get(0).contains("TIME_WAIT")||result.get(0).contains("CLOSE_WAIT")) {
					Log.logInfo(port+"端口可用!!!");
					String pid_01 = result.get(0).split(" ")[result.get(0).split(" ").length-1];//
					String pid_02 = pid_01.split("/")[0];
					String stopshell = "kill -s 9 "+pid_02;
					Cmd.runShell(stopshell);
				}				
			}else if (result.size()>1) {
				int status_01 = 0;
				int status_02 = 0;
				int status_03 = 0;
				String pid_01 = "";
				String pid_02 = "";
				for(String str:result){
					if(str.contains("LISTEN")||str.contains("LISTENING")||str.contains("ESTABLISHED")){
						status_01 = 1;
					}
					if (str.contains("ESTABLISHED")) {
						status_02 = 1;
					}
					if (str.contains("TIME_WAIT")||str.contains("CLOSE_WAIT")) {
						status_03 = 1;
						pid_01 = result.get(0).split(" ")[result.get(0).split(" ").length-1];//
						pid_02 = pid_01.split("/")[0];
					}
				}
				if(status_03 == 1){
					Log.logInfo(port+"端口可用!!!");
					String stopshell = "kill -s 9 "+pid_02;
					Cmd.runShell(stopshell);
				}
				if((status_01 == 1)||(status_02 == 1)){
					Log.logInfo(port+"不可用！！！");
				}
			}else {
				Log.logInfo(port+"可用！！！");
			}
	 }
	 	 
	 /**
	  * 获取空闲端口
	  * @param projectCode
	  * @return
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] getNodeFreePort(String projectCode){
		
		String sql_server = "select * from auto_server;";//查询服务器sql
		String sql_devices = "select * from auto_devices where devices_awaken=1;";//查询设备sql
		String[] freePortStr = {"",""};
		List ipList = new ArrayList();
		List portList = new ArrayList();
		List freePort =  new ArrayList();
		Map<String, Integer> usb_linkdevices = new HashMap<String, Integer>();
		
		/**
		 * 获取服务器ip
		 */
		Log.logInfo("开始获取服务器的IP信息");
		DBhandle dBhandle = new DBhandle();
		List list= dBhandle.query(sql_server);
		for(int i=0;i<list.size();i++){
			Log.logInfo("查询到服务器ip:"+((Map)list.get(i)).get("ip"));
			ipList.add(((Map)list.get(i)).get("ip"));
		}
		
		/**
		 * 获取设备对应的端口
		 */
		Log.logInfo("开始获取服务器appium测试端口");
		DBhandle dBhandle2 = new DBhandle();
		List list2 = dBhandle2.query(sql_devices);
		for(int i=0;i<list2.size();i++){
			portList.add(((Map)list2.get(i)).get("appium_port"));
			freePort.add(((Map)list2.get(i)).get("appium_port"));
			String devices_linkuseusb = ((Map)list2.get(i)).get("devices_linkuseusb").toString();
			if(devices_linkuseusb.equals("1")){
				usb_linkdevices.put(((Map)list2.get(i)).get("appium_port").toString(), i);
			}
			Log.logInfo("服务器获取到测试端口:"+((Map)list2.get(i)).get("appium_port"));
		}
		
		/**
		 * 获取空闲的端口
		 */
		Log.logInfo("开始获取可用于测试的空闲端口服务");
		for(int i=0;i<ipList.size();i++){
			for(int j=0;j<portList.size();j++){
				String requestUrl = "http://"+ipList.get(i)+":"+portList.get(j)+"/wd/hub/status";
				boolean bconnect = getStatus(requestUrl);
				if(bconnect){					
					freePort.remove(portList.get(j));
				}
			}
		}
		
		if(freePort.size()>0){
			for (int i = 0; i < freePort.size(); i++) {
				Log.logInfo("获取到服务器可用于测试的端口:"+freePort.get(i));
			}	
		}else {
			Log.logInfo("服务器没有用于测试的端口服务,请检测服务器的服务是否正常的关闭,或其他任务执行异常！！！");
		}
		
		if(freePort.size()>0){
			Log.logInfo("开始分配用于测试的服务器端口");
			/**
			 * 根据测试的project_code,如果测试的安卓app应用，优先分配连接数据线的设备，否则优先分配wifi链接的设备
			 */
			if(projectCode.contains("android")){			
				for(int i=0;i<freePort.size();i++){
					if(usb_linkdevices.get(freePort.get(i))!=null){
						freePortStr[0] = freePort.get(i).toString();
						freePortStr[1] = "1";
						break;
					}else {
						freePortStr[0] = freePort.get(i).toString();
						freePortStr[1] = "0";
					}
				}				
			}else {
				for(int i=0;i<freePort.size();i++){
					if(usb_linkdevices.get(freePort.get(i))==null){
						freePortStr[0] = freePort.get(i).toString();
						freePortStr[1] = "0";
						break;
					}else {
						freePortStr[0] = freePort.get(i).toString();
						freePortStr[1] = "1";
					}
				}		
			}				
		}
		Log.logInfo("用于测试的端口为:"+freePortStr[0]);
		return freePortStr;	
	}
	
	/**
	 * 根据端口获取设备信息
	 * @param port
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getDevicesInfo(String port){
		Log.logInfo("根据端口:"+port+"开始获取设备信息");
		Map<String, String> map = new HashMap<String, String>();
		String sql_devices = "select * from auto_devices where appium_port='"+port+"';";
		DBhandle db = new DBhandle();
		List list= db.query(sql_devices);
		if(list.size()>0){
			map.put("devices_name", ((Map)list.get(0)).get("devices_name").toString());
			map.put("devices_ip", ((Map)list.get(0)).get("devices_ip").toString());
			map.put("appium_port", ((Map)list.get(0)).get("appium_port").toString());
			map.put("devices_awaken", ((Map)list.get(0)).get("devices_awaken").toString());
			map.put("devices_linkuseusb", ((Map)list.get(0)).get("devices_linkuseusb").toString());
			map.put("platformName", ((Map)list.get(0)).get("platformname").toString());
			map.put("automationName", ((Map)list.get(0)).get("automationname").toString());
			map.put("platformVersion", ((Map)list.get(0)).get("platformversion").toString());
			map.put("deviceReadyTimeout", ((Map)list.get(0)).get("devicereadytimeout").toString());
			map.put("newCommandTimeout", ((Map)list.get(0)).get("newcommandtimeout").toString());
			map.put("phone_model", ((Map)list.get(0)).get("phone_model").toString());
		}else {
			Log.logInfo("数据库中不存在端口为:"+port+"的设备,请新增设备！！！");
		}
		Log.logInfo("获取到的设备信息为:");
		for (String key : map.keySet()) {
			Log.logInfo(key+"="+map.get(key));
	    }
		return map;
	}

	/**
	 * 根据设备名称获取设备信息
	 * @param devices
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getDevicesInfo2(String devices){
		Log.logInfo("根据设备:"+devices+"开始获取设备信息");
		Map<String, String> map = new HashMap<String, String>();
		String sql_devices = "";
		if(devices.contains(":555")){
			sql_devices = "select * from auto_devices where devices_ip ='"+devices+"';";
		}else {
			sql_devices = "select * from auto_devices where devices_name ='"+devices+"';";
		}
		DBhandle db = new DBhandle();
		List list= db.query(sql_devices);
		if(list.size()>0){
			map.put("devices_name", ((Map)list.get(0)).get("devices_name").toString());
			map.put("devices_ip", ((Map)list.get(0)).get("devices_ip").toString());
			map.put("appium_port", ((Map)list.get(0)).get("appium_port").toString());
			map.put("devices_awaken", ((Map)list.get(0)).get("devices_awaken").toString());
			map.put("devices_linkuseusb", ((Map)list.get(0)).get("devices_linkuseusb").toString());
			map.put("platformName", ((Map)list.get(0)).get("platformname").toString());
			map.put("platformVersion", ((Map)list.get(0)).get("platformversion").toString());
			map.put("automationName", ((Map)list.get(0)).get("automationname").toString());
			map.put("deviceReadyTimeout", ((Map)list.get(0)).get("devicereadytimeout").toString());
			map.put("newCommandTimeout", ((Map)list.get(0)).get("newcommandtimeout").toString());
			map.put("phone_model", ((Map)list.get(0)).get("phone_model").toString());	
		}else {
			Log.logInfo("数据库中不存在设备为:"+devices+"的信息,请新增设备！！！");
		}
		Log.logInfo("获取到的设备信息为:");
		for (String key : map.keySet()) {
			Log.logInfo(key+"="+map.get(key));
	    }
		return map;
	}
	
	/**
	 * 获取服务器用于测试的端口
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getTestPort(){
		List<String> portList = new ArrayList<String>();
		String sql_devices = "select * from auto_devices where devices_awaken=1;";//查询设备sql
		DBhandle dbd = new DBhandle();
		List list= dbd.query(sql_devices);
		for (int i = 0; i < list.size(); i++) {
			portList.add(((Map)list.get(i)).get("appium_port").toString());
		}
		return portList;
	}
	
	/**
	 * 启动代理服务器
	 * @param port
	 * @author linchaojiang
	 */
	/*public static void startAnyproxy(String port){		
		String path = "C:\\mdtest";
		FileUtil.makeDirs(path);
		String commd = "cmd /c anyproxy -i -l 5120 --port "+port+" |find \"s.logsss.com\" > C:\\mdtest\\data.txt";
		Cmd.runCommand(commd);
	}*/
	
	public static void startAnyproxy(String projectPath,String workspacePath){	
		String ruleJsPath = "";
		String gbBatPath = "";
		if(workspacePath.equals("${WORKSPACE}")){
			gbBatPath = projectPath + "/resources/gb.bat";
			ruleJsPath = projectPath + "/resources/rule.js";
		}else {
			gbBatPath = workspacePath + "/resources/gb.bat";
			ruleJsPath = workspacePath + "/resources/rule.js";
		}
//		String commd = "cmd /c anyproxy -i --port "+port+" --rule C:\\mdtest\\rule.js > C:\\mdtest\\alldata.txt";
		//String commd = "cmd.exe /C start C:\\mdtest\\gb.bat";
		String commd = "cmd.exe /C start " + gbBatPath + " " + ruleJsPath;
		System.out.println(commd);
		Cmd.runCommand(commd);
	}

	/**
	 * 结束掉代理服务器
	 * @param port
	 * @return List
	 * @author linchaojiang
	 */
	public static void killAnyproxy(String port){
		String qdport = port+" |findstr  LISTENING";
		System.out.println("qdport:"+qdport);
		List<String> pidlist = Cmd.getPid(qdport);
		for(int i=0;i<pidlist.size();i++){
			KillTaskPid(pidlist.get(i));
		}				
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String commd = "cmd /c anyproxy -i --port 5231 |find \"s.logsss.com\" >> D:\\mdtest\\111.txt";
		Cmd.runCommand(commd);*/	
		/*String commd = "cmd.exe /C start /b C:\\Users\\Lujun\\Desktop\\aa\\gb.bat";
		System.out.println(commd);
		Cmd.runCommand(commd);*/
		System.out.println(System.getProperty("user.dir").replace("\\", "/")+ "/resources/gb.bat");
		
	}
	
    /**
     * 根据端口,获取进程id
     * @param port
     * @return
     * 创建时间:2016-11-30,最后更新时间:2016-11-30
     */
    public static List<String> getPid(String port){
    	
		List<String> result = new ArrayList<String>();
        try {
            String cmd = "cmd /c netstat -ano|findstr "+port;  
            Runtime rt = Runtime.getRuntime(); // 获取运行时系统  
            Process proc = rt.exec(cmd); // 执行命令  
            InputStream stderr =  proc.getInputStream(); // 获取输入流  
            InputStreamReader isr = new InputStreamReader(stderr);  
            BufferedReader br = new BufferedReader(isr);  
            String line = null;  
            while ((line = br.readLine()) != null) { // 打印出命令执行的结果  
            	result.add(line.split(" ")[line.split(" ").length-1]);
            }  
        } catch (Throwable t) {  
        	Log.logInfo("获取进程Pid异常:"+t.getMessage());  
        }
		return result; 
	}
}
