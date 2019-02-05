package org.ycalendar.domain;

public class DictionaryData {

	private String id;

	// 字典名称
	private String dictType;
	//区域名称
	private String localStr;

	public String getLocalStr() {
		return localStr;
	}

	public void setLocalStr(String localStr) {
		this.localStr = localStr;
	}

	// 编码
	private String code;
	// 显示
	private String dictdataValue;

	// 顺序
	private Long dictOrder;

 


	/** default constructor */
	public DictionaryData() {
	}

	/** minimal constructor */
	public DictionaryData(String id) {
		this.id = id;
	}

	/** full constructor */
	public DictionaryData(String id, String code, String dictdataValue, String dictType, long dictOrder) {
		this.id = id;
		this.code = code;
		this.dictdataValue = dictdataValue;
		this.dictType = dictType;
		this.dictOrder = dictOrder;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
        /**
         * 获取编码
         * @return 
         */
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
        /**
         * 获取显示
         * @return 
         */
	public String getDictdataValue() {
		return this.dictdataValue;
	}

	public void setDictdataValue(String dictdataValue) {
		this.dictdataValue = dictdataValue;
	}

	public String getDictType() {
		return this.dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public Long getDictOrder() {
		return dictOrder;
	}

	public void setDictOrder(long dictOrder) {
		this.dictOrder = dictOrder;
	}

}
