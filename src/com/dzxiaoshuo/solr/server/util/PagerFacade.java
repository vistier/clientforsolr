package com.dzxiaoshuo.solr.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 2009-12-12 23:01
 * 
 * @author 1987
 * @version 0.1
 */
public abstract class PagerFacade {
	
    private static Logger log = LoggerFactory.getLogger(PagerFacade.class);  
    
    /**
     * Offset 
     * @return offset 
     */
    public static int getOffset(String pagerOffset) {
      
        int offset= 0;
        try {
            offset = Integer.parseInt(pagerOffset);
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error during get pager.offset", e);
            }
        }
        return offset;
    }
    
    /**
     * 
     * @return int
     */
    public static int getOffset() {
    	return 0;
    }
    
    /**
     * maxPageItems
     * @return maxPageItems
     */
    public static int getMaxPageItems() {
        int interval = PaginationSupport.DEFAULT_MAX_PAGE_ITEMS;
       
        return interval;
    }
    
    
    /**
     * DEFAULT_MAX_PAGE_ITEMS
     * @return DEFAULT_MAX_INDEX_PAGES
     */
    public static int getMaxIndexPages() {
        int maxIndexPages = PaginationSupport.DEFAULT_MAX_INDEX_PAGES;
      
        return maxIndexPages;
    }
    
    
    public static String getIndex() {
        return  PaginationSupport.DEFALUT_INDEX;
    }
    
}
