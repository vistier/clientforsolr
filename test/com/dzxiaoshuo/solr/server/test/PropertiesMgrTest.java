package com.dzxiaoshuo.solr.server.test;

import com.dzxiaoshuo.solr.server.util.PropertiesMgr;

import junit.framework.TestCase;

/**
 * 测试类：对PropertiesMgr类进行测试
 * <br/>
 * 2009-12-12 15:10
 * 
 * @author 1987
 * @version 0.1
 */
public class PropertiesMgrTest extends TestCase {

	public void testGetValue() {
		String value = PropertiesMgr.getProperty("solrServerHome");
		String defaultValue = "E:/education/search/apache-solr-1.4.0/example/multicore";
		assertEquals(defaultValue, value);
	}
	
}
