package com.dzxiaoshuo.solr.server.test;

import com.dzxiaoshuo.solr.server.util.StringUtils;

import junit.framework.TestCase;

/**
 * 测试类：对StringUtils类进行测试
 * <br/>
 * 2009-12-12 16:04
 * 
 * @author 1987
 * @version 0.1
 */
public class StringUrilsTest extends TestCase {
	
	public void testFilterString() {
		assertEquals("", StringUtils.filterString("[]:{}+*~^?"));
	}
}
