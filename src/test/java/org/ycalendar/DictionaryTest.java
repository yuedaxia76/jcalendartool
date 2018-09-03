package org.ycalendar;

import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.DicService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.util.MiscUtil;

/**
 * 字典测试
 * @author lenovo
 *
 */
public class DictionaryTest {
	private H2Dao hd;
	GernDAO dao;
 
	DicService ds;
 
	@BeforeSuite
	public void setup() {
		hd=H2Dao.getH2Dao();
		dao=new GernDAO();
		
		ds=new DicService();
		ds.setGdao(dao);
		ds.setHd(hd);
	}
	
	
	@AfterSuite
	public void testEnd() {
 
		ds.delDictionaryByType(testDicType);
		
	}	
	
	
	
	private final String  testDicType="testType";
	
	@Test
	public void testInServ() {
		
		

 
		
		DictionaryData ed=new DictionaryData();
		
		ed.setId(MiscUtil.getId());
		ed.setDictType(testDicType);
		ed.setCode("1");
		ed.setDictdataValue("测试1");
		ed.setDictOrder(1);

		
		ds.insertDictionary(ed);		
		
		
		ed.setId(MiscUtil.getId());
		ed.setDictType(testDicType);
		ed.setCode("2");
		ed.setDictdataValue("测试2");
		ed.setDictOrder(2);		
		
		ds.insertDictionary(ed);
		
		
		ed.setId(MiscUtil.getId());
		ed.setDictType(testDicType);
		ed.setCode("3");
		ed.setDictdataValue("测试3");
		ed.setDictOrder(3);		
		
		ds.insertDictionary(ed);		
	}
	
	@Test(dependsOnMethods = { "testInServ" })
	public void testgetMap() {
		Map<String, String> ma=ds.getDictMap(testDicType,(s)->s);
		assert(ma.size()==3);
		assert("测试1".equals(ma.get("1")));
		
		
		
		Map<Integer, String> mi=ds.getDictMap(testDicType,(s)->Integer.valueOf(s));
		assert(mi.size()==3);
		assert("测试2".equals(mi.get(2)));
	}
	
	@Test(dependsOnMethods = { "testInServ" })
	public void testgetList() {
		List<DictionaryData> ma=ds.getDictList(testDicType);
		assert(ma.size()==3);
		assert("1".equals(ma.get(0).getCode()));
	}	
	
	
	
	
	@Test(dependsOnMethods = { "testInServ" })
	public void testgetCode() {
		DictionaryData ma=ds.getDictDataByCode(testDicType,"3");
		assert(ma!=null);
		assert("3".equals(ma.getCode()));
	}

	
	
	@Test(dependsOnMethods = { "testInServ" })
	public void testgetByValue() {
		DictionaryData ma=ds.getDictDataByValue(testDicType,"测试3");
		assert(ma!=null);
		assert("3".equals(ma.getCode()));
	}
	
	
	@Test(dependsOnMethods = { "testInServ" })
	public void testgetValue() {
		String v=ds.getDictValue(testDicType,"3");
		assert(v!=null);
		assert("测试3".equals(v));
	}
}
