package com.dzxiaoshuo.solr.server.test;

import com.dzxiaoshuo.solr.server.SolrServerFactory;

import junit.framework.TestCase;

/**
 * 测试类：对SolrServerFactory类进行测试
 * <br/>
 * 2009-12-12 16:04
 * 
 * @author 1987
 * @version 0.1
 */
public class SolrServerFactoryTest extends TestCase {
	
	public void testGetEmbeddedSolrServer() {
		assertNotNull(SolrServerFactory.getEmbeddedSolrServer("core1"));
		assertNotNull(SolrServerFactory.getEmbeddedSolrServer("core1"));
	}
	
	public void testGetEmbeddedSolrServer1() {
		String solrServerHome = "E:/solr-multicore/solr";
		assertNotNull(SolrServerFactory.getEmbeddedSolrServer(solrServerHome, "core2"));
	}
	
	public void testGetCommonsHttpSolrServer() {
		assertNotNull(SolrServerFactory.getCommonsHttpSolrServer("http://localhost:8888/solr/"));
	}
	
}
