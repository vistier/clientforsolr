package com.dzxiaoshuo.solr.server;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dzxiaoshuo.solr.server.container.EmbeddedSolrServerContainer;

/**
 * SolrServer工厂类，主要提供两种类型的SolrServer -- EmbeddedSolrServer & CommonsHttpSolrServer
 * <br/>
 * 2009-12-12 15:25
 * 
 * @author 1987
 * @version 0.1
 */
public class SolrServerFactory {
	
	private static Logger logger = LoggerFactory.getLogger(SolrServerFactory.class);
	
	private static Map<String, SolrServer> solrServerMap = Collections.synchronizedMap(new HashMap<String, SolrServer>());
	/**
	 * 存储其他的CoreContainer, 也就是说当应用中会有其他的SOLR_HOME
	 */
	private static Map<String, EmbeddedSolrServerContainer> esscMap = new HashMap<String, EmbeddedSolrServerContainer>();
	
	private SolrServerFactory() {
	}
	
	/**
	 * 获取EmbeddedSolrServer
	 * 
	 * @param solrName
	 * @return SolrServer
	 */
	public static SolrServer getEmbeddedSolrServer(String solrName) {
		// 获取配置文件中配置的solrServerHome的核心server
		return getEmbeddedSolrServer(EmbeddedSolrServerContainer.DEFAULT_SOLR_SERVER_HOME, solrName);
	}
	
	
	/**
	 * 获取EmbeddedSolrServer, 非配置文件中的SOLR_HOME, 而是另外的SOLR_HOME
	 * 此方法是当应用中会有两个不同的SOLR_HOME
	 * 注意：所有的solr核心名称要全部不同！  切记！！！
	 * 
	 * @param solrServerHome
	 * @param solrName
	 * @return SolrServer
	 */
	public static SolrServer getEmbeddedSolrServer(String solrServerHome, String solrName) {
		// 当第一次获取SOLR_HOME的EmbeddedSolrServer的时候，将所有核心服务全部加载起来
		if (!esscMap.containsKey(solrServerHome)) {
			EmbeddedSolrServerContainer essc = new EmbeddedSolrServerContainer(solrServerHome);
			if (essc != null) {
				esscMap.put(solrServerHome, essc);
				CoreContainer cc = essc.getContainer();
				Collection<String> solrCoreNameCollection = cc.getCoreNames();
				for (String scn : solrCoreNameCollection) {
					solrServerMap.put(scn, essc.getSolrServer(scn));
				}
				logger.info(solrServerHome + "的所有核心EmbeddedSolrServer全部加在完毕！");
			}
		}
		SolrServer solrServer = null;
		if (!solrServerMap.containsKey(solrName)) {
			solrServer = esscMap.get(solrServerHome).getSolrServer(solrName);
			if (solrServer != null) {
				solrServerMap.put(solrName, solrServer);
				logger.info("服务 " + solrName + " 加载完毕");
			}
		}
		return solrServerMap.get(solrName);
	}
	
	/**
	 * 获取CommonsHttpSolrServer
	 * 
	 * @param SOLR_URL
	 * @return SolrServer
	 */
	public static SolrServer getCommonsHttpSolrServer(final String SOLR_URL) {
		SolrServer solrServer = null;
		if (!solrServerMap.containsKey(SOLR_URL)) {
			try {
				solrServer = new CommonsHttpSolrServer(SOLR_URL);
				if (solrServer != null) {
					solrServerMap.put(SOLR_URL, solrServer);
					logger.info("服务 " + SOLR_URL + " 加载完毕");
				}
			} catch (MalformedURLException e) {
				logger.warn("SOLR_URL出错！");
				e.printStackTrace();
			}
		}
		return solrServerMap.get(SOLR_URL);
	}

}
