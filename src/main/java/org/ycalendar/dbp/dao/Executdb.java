package org.ycalendar.dbp.dao;

import java.sql.Connection;

public interface Executdb {

	public  <T> T exeQuery(ExecueQuery<T> dba);

	public <T> T exeTran(ExecuDbopention<T> dba);
	
	
	public   Connection getCurCnection( );
	
	
	public int loadSql(String sqlFile);

        public void begin();
        
        public void rollback();
        
        
        public void commit();
}