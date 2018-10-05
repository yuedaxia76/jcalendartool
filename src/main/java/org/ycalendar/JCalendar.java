package org.ycalendar;

import javax.swing.SwingUtilities;

import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.DicService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.InitDataService;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.ui.YCalendar;

/**
 * 启动程序
 *
 */
public class JCalendar {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
                    YCalendar yc = new YCalendar();
                    InitDataService ints = new InitDataService();
                    yc.setDataLoad(ints);
                    
                    H2Dao hd = H2Dao.getH2Dao();
                    ConfigInfo conInfo=new ConfigInfo();
                    
                    if(conInfo.isRunInitDb()) {
                        hd.loadSql("initdata/init.sql");
                    }
                    
                    
                    GernDAO dao = new GernDAO();
                    
                    ints.setGdao(dao);
                    ints.setHd(hd);
                    
                    EventService es=new EventService();
                    es.setGdao(dao);
                    es.setHd(hd);                    
                    es.setConInfo(conInfo);
                    
                    
                    
                    DicService dic=new DicService();
                    dic.setGdao(dao);
                    dic.setHd(hd);
                    
                    TaskService ts=new TaskService();
                    ts.setGdao(dao);
                    ts.setHd(hd);                    
                    ts.setConInfo(conInfo);                    
                    
                    yc.setConInfo(conInfo);
                    yc.setEvSe(es);
                    yc.setDicSer(dic);
                    yc.setTsSe(ts);
                    
                    yc.initMainUi();
                });

	}
}
