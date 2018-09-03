package org.ycalendar.dbp.service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

 

public class InitDataService extends GenalService {
	public static Logger log = Logger.getLogger(InitDataService.class.getName());
	
	public void loadData() {
		File flag=new File("alreadyLoad");
		final boolean flagex=flag.exists();
		if(log.isLoggable(Level.INFO)) {
			log.info("alreadyLoad flag file path: "+flag.getAbsolutePath()+" file exists: "+flagex);	
		}
		
		if(!flagex) {
			String initData="initdata/initData_"+Locale.getDefault().toString()+".sql";
			
			if(Thread.currentThread().getContextClassLoader().getResource(initData)==null) {
				log.warning("no initData file "+initData);
				initData="initdata/initData" +".sql";
			}
			log.info("load sql :"+initData);
			int sqlcount=hd.loadSql(initData);
			
			log.info("load sql item:"+sqlcount);
			try {
				flag.createNewFile();
			} catch (IOException e) {
	            log.log(Level.SEVERE, "create flag file" +flag+"error", e);
 
			}
		}
	}
}
