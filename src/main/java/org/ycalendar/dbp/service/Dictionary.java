package org.ycalendar.dbp.service;
import java.util.List;
import java.util.Map;

import org.ycalendar.domain.DictionaryData;

public interface Dictionary {
 
	/**
	 * 查询type类型字典数据
	 * @param type
	 * @return
	 * 			返回Map<String key,String value>
	 */
	public <T> Map<T, String> getDictMap(String type,StringCinvert<T> sc) ;

	/**
	 * 查询type类型的字典数据
	 * 
	 * @return
	 * 			返回DictionaryData对象的List
	 */
	public List<DictionaryData> getDictList(String type) ;
	/**
	 * 通过字典数据的分类和编码查询出一条字典数据
	 * @param type
	 * @param code
	 * @return
	 * 			返回字典数据对象DictionaryData
	 */
	public DictionaryData getDictDataByCode(String type,String code);
	/**
	 * 通过字典数据的分类和字典数据值查询出一条字典数据
	 * @param type
	 * @param value
	 * @return
	 * 			返回字典数据对象DictionaryData
	 */
	public DictionaryData getDictDataByValue(String type,String value);
	/**
	 * 查询type类型字典数据中值为value的数据的编码（code）
	 * @param type
	 * @param value
	 * @return
	 * 			返回字典数据String
	 */
	
	public String getDictCode(String type,String value);
	/**
	 * 查询type类型字典数据中编码为code的数据的值（dict_value）
	 * @param type
	 * @param code
	 * @return
	 * 			返回字典数据编码String
	 */
	public String getDictValue(String type,String code);
}
