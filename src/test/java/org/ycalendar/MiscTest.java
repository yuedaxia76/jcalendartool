package org.ycalendar;

import org.testng.annotations.Test;
import org.ycalendar.dbp.service.StringCinvert;

public class MiscTest {
	@Test
	public void testStoS() {
		StringCinvert<String> sc=(s)->  s;
		String tess="a";
		String tem=sc.convert(tess);
		assert(tess.equals(tem));
		
		StringCinvert<Integer> si=(s)->  Integer.valueOf(s);
		
		
		String tes1s="12";
 
		assert(12==si.convert(tes1s));
	}
}
