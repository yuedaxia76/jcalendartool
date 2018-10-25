package org.ycalendar.dbp.service;

import org.ycalendar.dbp.dao.ExecuDbopention;
import org.ycalendar.dbp.dao.Executdb;
import org.ycalendar.dbp.dao.GernDAO;

public class GenalService{ 
	protected GernDAO gdao;
	
	public GernDAO getGdao() {
		return gdao;
	}

	public void setGdao(GernDAO gdao) {
		this.gdao = gdao;
	}

	protected Executdb hd;

	public Executdb getHd() {
		return hd;
	}

	public void setHd(Executdb hd) {
		this.hd = hd;
	}
	
	
	/**
	 * 执行sql
	 * @param sql
	 */
	public void runSql(String sql) {
		hd.exeTran(new ExecuDbopention<Void>() {
			public Void exeDbAction() {
				gdao.execuSql(hd.getCurCnection(), sql);
				return null;
			}
		});		
	}
}
