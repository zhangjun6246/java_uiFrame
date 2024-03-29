package com.globalegrow.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;

import com.globalegrow.code.Page;
import com.globalegrow.util.Cmd;
import com.globalegrow.util.FileUtil;
import com.globalegrow.util.Log;
import com.globalegrow.util.PropUtil;

/**
 * 初始化
 * @author linchaojiang
 * 创建时间:2017-04-05,最后更新时间:2017-04-05
 */
public class Init {
	
	public static String appiumServerUrl;
	public static String freeDeviceName;
	public static String freePort;
	public static String pappPackage;
	public static String pappActivity;
	public static String pappName;
	static Map<String, String> devicesMap;
	Properties props=System.getProperties();
	String osName = props.getProperty("os.name");
	
	public Init(){}
	
	/**
	 * 初始化
	 */
	public void initTest(String project_code,String tastjob,String devices){
		
		Log.logInfo("执行项目:"+project_code);
		Log.logInfo("运行appium节点的操作系统:"+osName);	
		/**
		 *linux初始化测试端口
		 */	
		if(osName.contains("Linux")){
			List<String> portList = Cmd.getTestPort();
			for(int i=0;i<portList.size();i++){//初始化服务器端口
				Cmd.intServerPort(portList.get(i));
			}
		}
		
		/**
		 * 获取空闲的端口
		 * 如果开发人员本地调试,读取devices.properties的配置文件
		 * 如果正式运行jenkins任务,则随机分配
		 * 如果运行jenkins任务,指定了设备,取指定设备运行
		 * 备注：设备必须在数据库中提前新增
		 */	
		String[] freeSeverPort = {"","0"};
		if(tastjob.equals("${tastjob}")){
			/**
			 * 读取devices设备进行测试
			 */	
			Log.logInfo("开始读取本地配置信息");
			PropUtil.GetProperties("resources/devices.properties");	
			freePort = PropUtil.get("port");
			Log.logInfo("获取到本地调试服务端口:"+freePort);
		}else {
			if(!osName.contains("Mac")){
				freeSeverPort = Cmd.getNodeFreePort(project_code);
				if(!freeSeverPort[0].equals("")){
					freePort = freeSeverPort[0];
				}else {
					Log.logInfo("服务器端口已被任务占用,无法正常运行任务！！！");
					Assert.fail();
					//结束本次任务   
		    		System.exit(0);
				}
			}		
		}
		//获取数据库中设备的信息，如果取到的设备已经连接了adb,则取对应的设备名称
		/*if((!devices.equals("${devices}"))&&(!devices.equals("0"))){//jenkins指定设备运行
			freeDeviceName = devices;
			devicesMap = Cmd.getDevicesInfo2(devices);//根据设备名称获取设备信息
		}else {//jenkins执行任务
			devicesMap = Cmd.getDevicesInfo(freePort);//根据端口获取设备信息
			if(freeSeverPort[1].equals("1")){
				freeDeviceName = devicesMap.get("devices_name");
			}else {
				freeDeviceName = devicesMap.get("devices_ip");
			}				
		}*/		
		if(devices.equals("${devices}")){
			/**
			 * 本地调试
			 * 如果系统中设备设置了已经连接数据线，优先使用设备名称进行连接
			 */
			devicesMap = Cmd.getDevicesInfo(freePort);//根据端口获取设备名称
			if(devicesMap.get("devices_linkuseusb").equals("1")){
				freeDeviceName = devicesMap.get("devices_name");
			}else {
				freeDeviceName = devicesMap.get("devices_ip");
			}			
		}else if (devices.equals("0")) {
			/**
			 * jenkins随机分配
			 */
			devicesMap = Cmd.getDevicesInfo(freePort);//根据端口获取设备信息
			if(osName.contains("Linux")){
				freeDeviceName = devicesMap.get("devices_ip");
			}else {
				if(freeSeverPort[1].equals("1")){
					freeDeviceName = devicesMap.get("devices_name");
				}else {
					freeDeviceName = devicesMap.get("devices_ip");
				}	
			}	
		}else{
			/**
			 * jenkins指定设备
			 */
			freeDeviceName = devices;
			devicesMap = Cmd.getDevicesInfo2(devices);//根据设备名称获取设备信息
			freePort=devicesMap.get("appium_port");
		}
		if(osName.contains("Mac")){
			freePort=devicesMap.get("appium_port");
		}
		Log.logInfo("使用测试的设备:"+freeDeviceName);	
		appiumServerUrl = "http://localhost:"+freePort+"/wd/hub";
		Log.logInfo("appium服务器地址:"+appiumServerUrl);
		if(osName.contains("Mac")){
			
		}else{
			/**
			 * 操作系统连接adb,若adb连接不上则中断测试
			 */
			boolean isFindDevce = false;
			if(osName.contains("Windows")){
				isFindDevce = Cmd.connectDevice(freeDeviceName);
			}else if (osName.contains("Linux")) {
				isFindDevce = Cmd.connectDevice_linux_01(freeDeviceName);
			}
			Log.logInfo("是否正常连接到设备:"+isFindDevce);
			if(!isFindDevce){
				Assert.fail();
			}
			
			/**
			 * 启动appium服务
			 */
			Log.logInfo("开始启动appium,端口:"+freePort+"·············");
			String sucessMsg = "Appium REST http interface listener started on 0.0.0.0:"+freePort;
			if(osName.contains("Windows")){						
				try {
					Cmd.runServer(Integer.parseInt(freePort), freeDeviceName);
				} catch (Exception e) {
					Log.logInfo("appium启动异常,端口:"+freePort+",异常信息:"+e.getMessage());
				}				
				Page.pause(10);
				int timeout = 0;
				OK:
				while (true) {
					if(timeout > 20){
						Log.logInfo("启动appium超时,60秒未启动成功··········");
						break;
					}
					timeout++;
					List<String> readTxt = new ArrayList<String>();
					readTxt = FileUtil.readTxtFile("C:\\aa\\"+freePort+".txt");
					for(int i = readTxt.size()-1;i > 0; i--){
						if(readTxt.get(i).contains(sucessMsg)){
							Log.logInfo("appium启动成功,端口号为:"+freePort);
							break OK;
						}
					}
					Page.pause(2);			
				}
				Page.pause(5);		
			}else if (osName.contains("Linux")) {			
				long date = System.currentTimeMillis();
		    	String sR=String.valueOf(date);
		    	String fileName = freePort+"_"+sR;
		    	Log.logInfo("appium日志的文件:"+fileName);
		    	try {
					Cmd.runServer_linux(Integer.parseInt(freePort), freeDeviceName,fileName);
				} catch (Exception e) {
					Log.logInfo("appium启动异常,端口:"+freePort+",异常信息:"+e.getMessage());
				}
		    	Page.pause(10);
		    	int timeout = 0;
				OK_SCE:
				while (true) {
					if(timeout > 15){
						Log.logInfo("启动appium超时,60秒未启动成功··········");
						break;
					}
					timeout++;
					List<String> readTxt = new ArrayList<String>();
					readTxt = FileUtil.readTxtFile("/home/jenkin/appium_log/"+fileName+".log");
					Log.logInfo("sucessMsg:"+sucessMsg);
					for(int i = readTxt.size()-1;i > 0; i--){
						if(readTxt.get(i).contains(sucessMsg)){
							Log.logInfo("appium启动成功,端口号为:"+freePort);
							break OK_SCE;
						}
					}
					Page.pause(4);			
				}
				Page.pause(5);
				appiumServerUrl = "http://localhost:"+freePort+"/wd/hub";
				Log.logInfo("服务器url为:"+appiumServerUrl);	
			}
		}
	}

	/**
	 * 停止appium服务及断掉adb链接
	 */
	public void stopServer(){		
		if(osName.contains("Windows")){
			Cmd.StopServer(freePort, freeDeviceName);
		}else if (osName.contains("Linux")) {
			Cmd.disconnectDevices(osName,freeDeviceName);//断开设备链接
			Cmd.stopNodeSever(freePort);
		}
	}
}
