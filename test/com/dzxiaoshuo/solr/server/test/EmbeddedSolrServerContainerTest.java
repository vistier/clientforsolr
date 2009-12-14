package com.dzxiaoshuo.solr.server.test;

import junit.framework.TestCase;

import com.dzxiaoshuo.solr.server.container.EmbeddedSolrServerContainer;

/**
 * 测试类：对EmbeddedSolrServerContainer类进行测试
 * <br/>
 * 2009-12-12 15:33
 * 
 * @author 1987
 * @version 0.1
 */
public class EmbeddedSolrServerContainerTest extends TestCase {
	
	public void testGetContainer() {
		EmbeddedSolrServerContainer essc = new EmbeddedSolrServerContainer();
		assertNotNull(essc.getContainer());
	}
	
	public void testGetSolrServer() {
		EmbeddedSolrServerContainer essc = new EmbeddedSolrServerContainer();
		assertNotNull(essc.getSolrServer("core1"));
	}
}
