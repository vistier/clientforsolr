package com.dzxiaoshuo.solr.server.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SolrServer 异常类
 * <br/>
 * 2009-12-12 15:02
 * 
 * @author 1987
 * @version 0.1
 */
public class SolrServerException extends RuntimeException {

	private static final long serialVersionUID = -6150092853751278477L;

	Log log = LogFactory.getLog(SolrServerException.class);

	public SolrServerException(String message) {
		super(message);
		log.warn(message);
	}

}
