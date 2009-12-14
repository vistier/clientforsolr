package com.dzxiaoshuo.solr.server.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实体类与SolrInputDocument或者SolrDocument转换<br/>
 * 注意：在使用的时候，实体类必须严格遵守 JAVABEAN 规范
 * <br/>
 * 2009-12-12 17:10
 * 
 * @author 1987
 * @version 0.1
 */
public class EntityConvert {
	
	private static Logger logger = LoggerFactory.getLogger(EntityConvert.class);
	
	/**
	 * 实体类与SolrInputDocument转换
	 * @param obj
	 * @return SolrInputDocument
	 */
	public static SolrInputDocument entity2SolrInputDocument(Object obj) {
		if (obj != null) {
			Class<?> cls = obj.getClass();
			Field[] filedArrays = cls.getDeclaredFields();
			Method m = null;
			SolrInputDocument sid = new SolrInputDocument();
			for (Field f : filedArrays) {
				try {
					m = cls.getMethod(getMethodName(f.getName(), "get"));
					sid.addField(f.getName(), m.invoke(obj));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return sid;
		}
		logger.warn("即将要转换的实体类为null！");
		return null;
	}
	
	/**
	 * SolrDocument与实体类转换
	 * @param sd
	 * @param cls
	 * @return Object
	 */
	public static Object solrDocument2Entity(SolrDocument sd, Class<?> cls) {
		if (sd != null) {
			try {
				Object obj = cls.newInstance();
				Method m = null;
				Field f = null;
				Class<?> fieldType = null;
				for (String fieldName : sd.getFieldNames()) {
					
					f = cls.getDeclaredField(fieldName);
					fieldType = f.getType();
					m = cls.getMethod(getMethodName(fieldName, "set"), fieldType);
					// 如果是 int, float等基本类型，则需要转型
					if (fieldType.equals(Integer.TYPE)) {
						fieldType = Integer.class;
					} else if (fieldType.equals(Float.TYPE)) {
						fieldType = Float.class;
					} else if (fieldType.equals(Double.TYPE)) {
						fieldType = Double.class;
					} else if (fieldType.equals(Boolean.TYPE)) {
						fieldType = Boolean.class;
					} else if (fieldType.equals(Short.TYPE)) {
						fieldType = Short.class;
					} else if (fieldType.equals(Long.TYPE)) {
						fieldType = Long.class;
					}
					m.invoke(obj, fieldType.cast(sd.getFieldValue(fieldName)));
				}
				return obj;
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
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		logger.warn("即将要转换的SolrDocument为null！");
		return null;
	}
	
	/**
	 * 批量转换, 将实体类的List转换为SolrInputDocument Collection
	 * @param entityList
	 * @return Collection<SolrInputDocument>
	 */
	public static Collection<SolrInputDocument> entity2SolrInputDocument(List<Object> entityList) {
		if (entityList != null && entityList.size() > 0) {
			Collection<SolrInputDocument> solrInputDocumentList = new ArrayList<SolrInputDocument>();
			for (Object o : entityList) {
				SolrInputDocument sid = entity2SolrInputDocument(o);
				if (sid != null) {
					solrInputDocumentList.add(entity2SolrInputDocument(o));
				}
			}
			return solrInputDocumentList;
		}
		logger.warn("即将要转换的entityList为null或者size为0！");
		return null;
	}
	
	/**
	 * 批量转换, 将solrDocumentList转换为实体类 List
	 * 
	 * @param solrDocumentList
	 * @param cls
	 * @return List<Object>
	 */
	public static List<Object> solrDocument2Entity(SolrDocumentList solrDocumentList, Class<?> cls) {
		if (solrDocumentList != null && solrDocumentList.size() > 0) {
			List<Object> objectList = new ArrayList<Object>();
			for (SolrDocument sd : solrDocumentList) {
				Object obj = solrDocument2Entity(sd, cls);
				if (obj != null) {
					objectList.add(obj);
				}
			}
			return objectList;
		}
		logger.warn("即将要转换的solrDocumentList为null或者size为0！");
		return null;
	}
	
	/**
	 * 更新数据时用到，给出要更新的对象，Id为必须给出的属性，然后加上要更新的属性
	 * 如果对应的属性的值为空或者为0，这不需要更新
	 * 
	 * @param sd 查询到得SolrDocument
	 * @param object
	 * @return SolrInputDocument
	 */
	public static SolrInputDocument solrDocument2SolrInputDocument(SolrDocument sd, Object object) {
		if (object != null && sd != null) {
			SolrInputDocument sid = new SolrInputDocument();
			Collection<String> fieldNameCollection = sd.getFieldNames();// 得到所有的属性名
			
			Class<?> cls = object.getClass();
			Object o = null;
			for (String fieldName : fieldNameCollection) {
				try {
					// 如果对应的属性的值为空或者为0，这不需要更新
					o = cls.getMethod(EntityConvert.getMethodName(fieldName, "get")).invoke(object);
					
					Class<?> fieldType = cls.getDeclaredField(fieldName).getType();
					
					if (fieldType.equals(Integer.TYPE)) {
						Integer fieldValue = Integer.class.cast(o);
						if (fieldValue != null && fieldValue.compareTo(0) != 0) {
							sid.addField(fieldName, fieldValue);
						}
					} else if (fieldType.equals(Float.TYPE)) {
						Float fieldValue = Float.class.cast(o);
						if (fieldValue != null && fieldValue.compareTo(0f) != 0) {
							sid.addField(fieldName, fieldValue);
						}
					} else if (fieldType.equals(Double.TYPE)) {
						Double fieldValue = Double.class.cast(o);
						if (fieldValue != null && fieldValue.compareTo(0d) != 0) {
							sid.addField(fieldName, fieldValue);
						}
					} else if (fieldType.equals(Short.TYPE)) {
						Short fieldValue = Short.class.cast(o);
						if (fieldValue != null && fieldValue.compareTo((short)0) != 0) {
							sid.addField(fieldName, fieldValue);
						}
					} else if (fieldType.equals(Long.TYPE)) {
						Long fieldValue = Long.class.cast(o);
						if (fieldValue != null && fieldValue.compareTo((long)0) != 0) {
							sid.addField(fieldName, fieldValue);
						}
					} else {
						if (o != null) {
							sid.addField(fieldName, o.toString());
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
			return sid;
		}
		logger.warn("即将要转换的SolrDocument或者要更新的Object为null");
		return null;
	}
	
	/**
	 * 批量更新数据时用到
	 * 
	 * @param sdl 查询到得SolrDocumentList
	 * @param idName
	 * @param objectMap
	 * @return List<SolrInputDocument>
	 */
	public static List<SolrInputDocument> solrDocumentList2SolrInputDocumentList(SolrDocumentList sdl, String idName, Map<Object, Object> objectMap) {
		List<SolrInputDocument> solrInputDocuemntList = new ArrayList<SolrInputDocument>();
		
		// 获得元素的主键的类型，即Map的key类型
		Class<?> cls = null;
		try {
			cls = objectMap.get(0).getClass().getDeclaredField(idName).getType();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		for (SolrDocument sd : sdl) {
			Collection<String> fieldNameCollection = sd.getFieldNames();
			SolrInputDocument sid = null;
			
			Object object = objectMap.get(cls.cast(sd.getFieldValue(idName)));
			
			Object o = null;
			
			if (object != null) {
				sid = new SolrInputDocument();
				for (String fieldName : fieldNameCollection) {
					try {
						// 如果对应的属性的值为空或者为0，这不需要更新
						o = cls.getMethod(EntityConvert.getMethodName(fieldName, "get")).invoke(object);
						
						Class<?> fieldType = cls.getDeclaredField(fieldName).getType();
						
						if (fieldType.equals(Integer.TYPE)) {
							Integer fieldValue = Integer.class.cast(o);
							if (fieldValue != null && fieldValue.compareTo(0) != 0) {
								sid.addField(fieldName, fieldValue);
							}
						} else if (fieldType.equals(Float.TYPE)) {
							Float fieldValue = Float.class.cast(o);
							if (fieldValue != null && fieldValue.compareTo(0f) != 0) {
								sid.addField(fieldName, fieldValue);
							}
						} else if (fieldType.equals(Double.TYPE)) {
							Double fieldValue = Double.class.cast(o);
							if (fieldValue != null && fieldValue.compareTo(0d) != 0) {
								sid.addField(fieldName, fieldValue);
							}
						} else if (fieldType.equals(Short.TYPE)) {
							Short fieldValue = Short.class.cast(o);
							if (fieldValue != null && fieldValue.compareTo((short)0) != 0) {
								sid.addField(fieldName, fieldValue);
							}
						} else if (fieldType.equals(Long.TYPE)) {
							Long fieldValue = Long.class.cast(o);
							if (fieldValue != null && fieldValue.compareTo((long)0) != 0) {
								sid.addField(fieldName, fieldValue);
							}
						} else {
							if (o != null) {
								sid.addField(fieldName, o.toString());
							}
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return solrInputDocuemntList;
	}
	
	/**
	 * 获得类的方法名，按照JAVABEAN的规范
	 * 
	 * @param property 属性名称
	 * @param prefix 前缀，一般为set 或 get
	 * @return String
	 */
	public static String getMethodName(String property, String prefix){   
        String prop = Character.toUpperCase(property.charAt(0)) + property.substring(1);   
        return prefix + prop;   
    }  
	
}
