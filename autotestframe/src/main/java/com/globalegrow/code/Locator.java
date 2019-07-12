package com.globalegrow.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;


import com.globalegrow.util.DBhandle;
import com.globalegrow.util.Log;

import junit.framework.Assert;

public class Locator {

	List list=new ArrayList<>();
	
	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	public Locator(String project_code) {
		Log.logInfo("project_code:"+project_code);
		DBhandle db;
		String[] str=project_code.split("-");
		String sql;
		if(str.length==3&&str[2].contains("platform")){//平台code多加了一个标识，用于表示是平台
			sql="select * from  auto_elementinfo where project_id  in (select id from auto_projectManage where project_name='"+str[0]+"')";
			db=new DBhandle("config/jdbcui.properties");
			
			list=db.query(sql);
		}else{
			sql="SELECT * FROM `auto_pageproperty` WHERE `auto_pageproperty`.`module_id` IN"
				+ " (SELECT id FROM auto_projectTree WHERE PID="
				+ "(SELECT id FROM auto_projectTree WHERE NAME='"+str[1]+"' AND PID="
				+ "(SELECT id FROM auto_projectTree WHERE NAME ='"+str[0]+"')))";
			db=new DBhandle();
			
		}
		list=db.query(sql);
		Log.logInfo(sql);
	}
	public  Map<String,String> getMap(String identification){
		LinkedHashMap<String,String> map=new LinkedHashMap<String,String>();
		for (int i = 0; i < list.size(); i++) {
			Map<String,String>	map2=(Map<String, String>) list.get(i);
			try {
				if(map2.get("identification").equals(identification)){
					map.put("id", map2.get("element_id"));
					map.put("name", map2.get("element_name"));
					map.put("className", map2.get("element_classname"));
					map.put("css", map2.get("element_css"));
					map.put("xpath", map2.get("element_xpath"));
					map.put("linkText", map2.get("element_linktext"));
					break;
				}
			} catch (Exception e) {
				//平台中的数据
				if(map2.get("ele_code").equals(identification)){
					map.put("id", map2.get("elelab_id"));
					map.put("name", map2.get("elelab_name"));
					map.put("className", map2.get("elelab_classname"));
					map.put("css", map2.get("elelab_css"));
					map.put("xpath", map2.get("elelab_xpath"));
					map.put("linkText", map2.get("elelab_linktext"));
					break;
				}
			}
			
			
		}
		//过滤掉map里面value为空的值
		Set set = map.keySet();   
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {   
            Object obj = (Object) iterator.next();   
            Object value =(Object)map.get(obj);   
            remove(value, iterator);   
        }
        if(map.isEmpty()){
        	Log.logError("你所传入的对象"+identification+"未查找到，请检查元素表");
        	Assert.fail();
        }
		//Log.logInfo(map);
		return map;
	}
	
    private static void remove(Object obj,Iterator iterator){   
        if(obj instanceof String){   
            String str = (String)obj;  
            if(isEmpty(str)){  //过滤掉为null和""的值 主函数输出结果map：{2=BB, 1=AA, 5=CC, 8=  }  
//            if("".equals(str.trim())){  //过滤掉为null、""和" "的值 主函数输出结果map：{2=BB, 1=AA, 5=CC}  
                iterator.remove();   
            }   
             
        }else if(obj instanceof Collection){   
            Collection col = (Collection)obj;   
            if(col==null||col.isEmpty()){   
                iterator.remove();   
            }   
               
        }else if(obj instanceof Map){   
            Map temp = (Map)obj;   
            if(temp==null||temp.isEmpty()){   
                iterator.remove();   
            }   
               
        }else if(obj instanceof Object[]){   
            Object[] array =(Object[])obj;   
            if(array==null||array.length<=0){   
                iterator.remove();   
            }   
        }else{   
            if(obj==null){   
                iterator.remove();   
            }   
        }   
    }   
    
    public static boolean isEmpty(Object obj){  
        return obj == null || obj.toString().length() == 0;  
    }  

	/**
	 * 传入yaml中的标识，返回by对象
	 * @param identification
	 * @return By
	 * @author linchaojiang
	 */
	
	public By getBy(Map<String,String> ml,String type) {

		By by = null;
			if (type.equals("id")) {
				by = By.id(ml.get(type));
			} else if (type.equals("name")) {
				by = By.name(ml.get(type));
			} else if (type.equals("xpath")) {
				by = By.xpath(ml.get(type));
			} else if (type.equals("className")) {
				by = By.className(ml.get(type));
			} else if (type.equals("linkText")) {
				by = By.linkText(ml.get(type));
			} else if (type.equals("css")) {
				by = By.cssSelector(ml.get(type));
			} else {
				Log.logInfo("返回by对象出错！！！");
			}
		return by;
	}
	
	/**
	 * 更新locator中变量的值，使用新字符串替换值中的变量字符串
	 * @param itemName 变量名
	 * @param replaceStr 待替换的字符串，或者叫变量字符串
	 * @param newStr 新字符串
	 * @throws Exception 执出全部异常
	 * @author huxuebing 创建时间:2017-7-24
	 */	
	public  void renewLocatorValue(String itemName, String replaceStr, String newStr) throws Exception {
		Map<String,String> map=null;
		for (int i = 0; i < list.size(); i++) {
			Map<String,String>	map2=(Map<String, String>) list.get(i);
			if(map2.get("identification").equals(itemName)){
				map=map2;
				list.remove(i);
			}
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {  
			String type=entry.getKey();
    	    String value=entry.getValue();
    	    if(value!=null){
	    	    if(value.contains(replaceStr)){
	    	    String newVal=value.replace(replaceStr, newStr);
	    	    	map.put(type, newVal);
	    	    }
    	    }
		}
		list.add(map);
	}


}
