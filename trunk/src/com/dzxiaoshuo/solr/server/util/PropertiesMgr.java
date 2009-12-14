package com.dzxiaoshuo.solr.server.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析properties工具类
 * <br/>
 * 2009-12-12 14:50
 * 
 * @author 1987
 * @version 0.1
 */
public class PropertiesMgr {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesMgr.class);
    private static Properties properties = new Properties();
	
    /**
	 * 装载参数配置文件
	 */
    static{
		try {
			properties.load(PropertiesMgr.class.getClassLoader().getResourceAsStream("solrclient.properties"));
		} catch (IOException e) {
			logger.error("没有发现solrclient.propertis文件，请检查该配置文件是否存在于根目录！" + PropertiesMgr.class.getClassLoader());
		}
	}
	
	/**
	 * 根据给出的键值找出配置文件的对应值
	 * @param key 键
	 * @return 值
	 */
	public static String getProperty(String key){
		return (String) properties.getProperty(key);
	}
	
}
