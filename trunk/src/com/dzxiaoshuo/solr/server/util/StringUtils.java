package com.dzxiaoshuo.solr.server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串帮助类，处理最常见的一些字符串问题
 * <br/>
 * 2009-12-14 17:03
 * 
 * @author 1987
 * @version 0.1
 */
public class StringUtils {
	
	private final static Pattern p = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——\\-+|{}【】‘；：”“’。，、？]");
	
	/**
	 * 过滤查询字符串中的非法字符
	 * 建议：当提交查询字符串的时候，应先过滤查询的字符串中是否已经包含非法字符
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterString(String str) {
		Matcher m = p.matcher(str);     
		return m.replaceAll("").trim();  
	}
	
}
