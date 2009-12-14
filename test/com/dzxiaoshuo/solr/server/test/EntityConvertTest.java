package com.dzxiaoshuo.solr.server.test;

import org.apache.solr.common.SolrDocument;

import junit.framework.TestCase;

import com.dzxiaoshuo.solr.server.reflect.EntityConvert;
import com.dzxiaoshuo.solr.server.test.entity.People;

/**
 * 测试类：对EntityConvert类进行测试
 * <br/>
 * 2009-12-12 17:10
 * 
 * @author 1987
 * @version 0.1
 */
public class EntityConvertTest extends TestCase {

	public void testEntity2SolrInputDocument() {
		People p = new People();
		p.setId(1);
		// p.setName("1987");
		p.setAge(22);
		EntityConvert.entity2SolrInputDocument(p);
	}
	
	public void testSolrDocument2Entity() {
		SolrDocument sd = new SolrDocument();
		sd.addField("id", 1);
		sd.addField("name", "1987");
		sd.addField("age", 22);
		EntityConvert.solrDocument2Entity(sd, People.class);
	}
	
}
