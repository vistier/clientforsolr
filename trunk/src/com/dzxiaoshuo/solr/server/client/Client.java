package com.dzxiaoshuo.solr.server.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dzxiaoshuo.solr.server.reflect.EntityConvert;
import com.dzxiaoshuo.solr.server.util.PaginationSupport;


/**
 * solrJ 交换客户端 
 * <br/>
 * 2009-12-12 19:02
 * 
 * @author 1987
 * @version 0.1
 */

public class Client {

	private static Logger logger = LoggerFactory.getLogger(Client.class);
	
	/**
	 * 利用solr的CommonParams.Q查询
	 * 
	 * @param keyword 查询字符串
	 * @param cls 查询的类
	 * @param start 查询起始记录
	 * @param rows 查询条数
	 * @param server
	 * @return PaginationSupport<T>
	 */
	public static <T>PaginationSupport<T> query(String keyword, Class<T> cls, int start, int rows, SolrServer server) {
		SolrQuery query = new SolrQuery();
		query.setQuery(keyword);
		query.setStart(start);
		query.setRows(rows);
		QueryResponse response = null;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		SolrDocumentList sdl = response.getResults();
		int totalCount = Long.valueOf(response.getResults().getNumFound()).intValue();
		return new PaginationSupport<T>(EntityConvert.solrDocument2Entity(sdl, cls), totalCount, start, rows);
	}
	
	/**
	 *  利用solr的SolrParams查询
	 * 
	 * @param params 查询参数Map
	 * @param cls 查询的类
	 * @param start 查询起始记录
	 * @param rows 查询条数
	 * @param server
	 * @return PaginationSupport<T>
	 */
	public static <T>PaginationSupport<T> query(SolrParams params, Class<T> cls, int start, int rows, SolrServer server) {
		QueryResponse response = null;
		try {
			response = server.query(params);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		SolrDocumentList sdl = response.getResults();
		int totalCount = Long.valueOf(response.getResults().getNumFound()).intValue();
		return new PaginationSupport<T>(EntityConvert.solrDocument2Entity(sdl, cls), totalCount, start, rows);
	}
	
	/**
	 * 提交数据
	 * @param obj
	 * @param server
	 */
	public static void commitObject(Object obj, SolrServer server) {
		try {
			server.add(EntityConvert.entity2SolrInputDocument(obj));
			server.commit(false, false);
			logger.info("成功提交 1 个元素到SOLR SYSTEM中");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 提交数据采用批量提交
	 * @param objectList
	 * @param server
	 */
	public static <T> void commitList(List<T> objectList, SolrServer server) {
		try {
			server.add(EntityConvert.entityList2SolrInputDocument(objectList));
			server.commit(false, false);
			logger.info("成功提交 " + objectList.size() + " 个元素到SOLR SYSTEM中");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提交数据采用批量提交
	 * 注意：该Map元素必须不大于1024或者去更改solrconfig.xml中的参数，参数如下：
	 * &ltmaxBooleanClauses&gt1024&lt/maxBooleanClauses&gt
	 * 
	 * @param objMap key 为实体类的ID，value为即将要更新的object，该object的id键值不能为空
	 * @param idName
	 * @param server
	 */
	public static void update(Map<Object, Object> objMap, String idName, SolrServer server) {
		if (objMap != null && objMap.size() > 0 && StringUtils.isNotBlank(idName)) {
			SolrQuery query = new SolrQuery();
			Set<Object> objSet = objMap.keySet();
			int i = 0;
			StringBuffer q = new StringBuffer();
			for (Object o : objSet) {
				if (i == 0) {
					q.append(idName + ":" + o.toString());
					i ++;
				} else {
					q.append(" OR " + idName + ":" + o.toString());
				}
			}
			query.setQuery(q.toString());
			query.setStart(0);
			query.setRows(objMap.size());
			QueryResponse qr = null;
			try {
				qr = server.query(query);
				SolrDocumentList sdl = qr.getResults();
				if (sdl.size() > 0) {
					UpdateRequest updateRequest = new UpdateRequest();
					updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);
					updateRequest.add(EntityConvert.solrDocumentList2SolrInputDocumentList(sdl, idName, objMap));
					updateRequest.process(server);
					
					logger.info("从SOLR SYSTEM中更新 " + objMap.size() + " 个元素");
				}
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 更新数据
	 * 
	 * @param obj
	 * @param idName
	 * @param server
	 */
	public static void update(Object obj, String idName, SolrServer server) {
		if (obj != null && StringUtils.isNotBlank(idName)) {
			Class<?> cls = obj.getClass();
			try {
				Method method = cls.getMethod(EntityConvert.getMethodName(idName, "get"));
				Object o = method.invoke(obj);
				
				if (o != null) {// 主键不能为空
					SolrQuery query = new SolrQuery();
					query.setQuery(idName + ":" + o.toString());
					query.setStart(0);
					query.setRows(1);
					QueryResponse qr = server.query(query);
					SolrDocument sd = qr.getResults().get(0);
					
					UpdateRequest updateRequest = new UpdateRequest();
					updateRequest.setAction(AbstractUpdateRequest.ACTION.COMMIT, false, false);
					updateRequest.add(EntityConvert.solrDocument2SolrInputDocument(sd, obj));
					updateRequest.process(server);
					
					logger.info("从SOLR SYSTEM中更新 1 个元素， id:" + o.toString());
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除给出的一个对象 
	 * @param obj
	 * @param idName
	 * @param server
	 */
	public static void deleteByExample(Object obj, String idName, SolrServer server) {
		Class<?> cls = obj.getClass();
		try {
			Method method = cls.getMethod(EntityConvert.getMethodName(idName, "get"));
			Object o = method.invoke(obj);
			if (o != null) {
				deleteById(method.invoke(obj), idName, server);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除数据
	 * @param id     Id值
	 * @param idName Id名称
	 * @param server
	 */
	public static void deleteById(Object id, String idName, SolrServer server) {
		try {
			server.deleteById(idName + ":" + id.toString());
			server.commit(false, false);
			logger.info("从SOLR SYSTEM中删除 1 个元素， id:" + id.toString());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 批量删除数据
	 * 注意：该数组元素必须不大于1024或者去更改solrconfig.xml中的参数，参数如下：
	 * &lgmaxBooleanClauses&gt1024&lg/maxBooleanClauses&gt
	 * 
	 * @param idArrays Id数组
	 * @param idName   Id名称
	 * @param server
	 */
	public static void deleteById(Object[] idArrays, String idName, SolrServer server) {
		if (idArrays.length > 0) {
			try {
				StringBuffer query = new StringBuffer(idName + ":" + idArrays[0]);
				for (int i = 1; i < idArrays.length; i++) {
					if (idArrays[i] != null) {
						query.append(" OR " + idName + ":" + idArrays[i].toString());
					}
				}
				server.deleteByQuery(query.toString());
				server.commit(false, false);
				logger.info("从SOLR SYSTEM中删除 " + idArrays.length + " 个元素");
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除所有元素
	 * 
	 * @param server
	 */
	public static void deleteAll(SolrServer server) {
		try {
			server.deleteByQuery("*:*");
			server.commit(false, false);
			logger.info("从SOLR SYSTEM中删除所有元素");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 与SOLR SYSTEM PING, 主要是检测solr是否down掉
	 * 
	 * @param server
	 * @return String
	 */
	public static String ping(SolrServer server) {
		try {
			logger.info("与SOLR SYSTEM PING成功");
			return server.ping().getResponse().toString();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 优化solr数据存储结构
	 * 
	 * @param server
	 */
	public static void optimize(SolrServer server) {
		try {
			logger.info("正在优化 SOLR SYSTEM ... ...");
			long now = System.currentTimeMillis();
			server.optimize(true, false);
			server.optimize(false, false);
			logger.info("优化 SOLR SYSTEM 完毕， 花费时间：" + (System.currentTimeMillis() - now) / 1000 + "秒");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}