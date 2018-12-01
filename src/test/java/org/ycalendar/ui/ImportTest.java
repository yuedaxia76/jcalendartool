/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.fortuna.ical4j.data.ParserException;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.CalendarService;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.DicService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.domain.EventData;
import org.ycalendar.domain.TaskData;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;

/**
 *
 * @author lenovo
 */
public class ImportTest {

    private H2Dao hd;
    GernDAO dao;
    ConfigInfo conInfo;

    EventService es;
    YCalendar yc;
    TaskService ts;
    CalendarService cs;

    @BeforeSuite
    public void setup() {

        hd = H2Dao.getH2Dao();
        dao = new GernDAO();
        conInfo = new ConfigInfo();

        es = new EventService();

        es.setGdao(dao);
        es.setHd(hd);
        es.setConInfo(conInfo);

        yc = new YCalendar();
        yc.setConInfo(conInfo);
        yc.setEvSe(es);

        DicService dic = new DicService();
        dic.setGdao(dao);
        dic.setHd(hd);

        ts = new TaskService();
        ts.setGdao(dao);
        ts.setHd(hd);
        ts.setConInfo(conInfo);

        yc.setTsSe(ts);
        yc.setDicSer(dic);

        cs = new CalendarService();
        cs.setDrs(dic);
        cs.setEser(es);
        cs.setTse(ts);
        cs.setGdao(dao);
        cs.setHd(hd);

    }

    @AfterSuite
    public void testEnd() {
//       if(hd!=null){
//        hd.close();
//       }
    }

    @Test
    public void testImpo() throws IOException, ParserException {
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test1.ics")) {
            Tuple2<Integer, Integer> result = yc.importIcs(in, null);
            Assert.assertEquals(result.e1.intValue(), 2);
            Assert.assertEquals(result.e2.intValue(), 1);
        }

    }

    @Test(dependsOnMethods = {"testImpo"})
    public void testExport() throws IOException, ParserException, FileNotFoundException, ParseException {
        yc.exportIcsFile("main", new File("testexpIcs.ics"));

    }

    @Test
    public void testImpoCsv() throws IOException, ParserException {
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test1.csv")) {
            Tuple2<Integer, Integer> result = yc.importCsv(in, null, "UTF-8");
            Assert.assertEquals(result.e1.intValue(), 2);
        }

    }

    @Test
    public void testTimePase() {
        long t1 = UtilDateTime.parseDate("08/12/13 11:00:00 PM", "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH).getTime();
        assert (t1 > 1000);
        t1 = UtilDateTime.parseDate("08/15/13 10:00:00 PM", "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH).getTime();
        assert (t1 > 1000);
    }

    @Test
    public void testMulInmExp() throws IOException, ParserException, FileNotFoundException, ParseException {
        //建立测试日历 
        cs.createCalendar("test1", "测试日历");
        //导入 
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test2.ics")) {
            Tuple2<Integer, Integer> result = yc.importIcs(in, "test1");
            Assert.assertEquals(result.e1.intValue(), 1);
            Assert.assertEquals(result.e2.intValue(), 1);
        }
        //读取记录
        List<EventData> ess = es.readEvent(null, null, Arrays.asList("test1"), null);
        Assert.assertEquals(ess.size(), 1);
        EventData teste = ess.get(0);

        List<TaskData> tss = ts.queryTask("1=1", Arrays.asList("test1"), null);
        Assert.assertEquals(tss.size(), 1);
        TaskData tsda = tss.get(0);

        //导出
        File outf=new File("test2expIcs.ics");
        yc.exportIcsFile("test1", outf);

        //再次导入
        
        try (InputStream in = new FileInputStream(outf)) {
            Tuple2<Integer, Integer> result = yc.importIcs(in, "test1");
            Assert.assertEquals(result.e1.intValue(), 1);
            Assert.assertEquals(result.e2.intValue(), 1);
        }
        //与原始的比较一下
        
        
                //读取记录
        ess = es.readEvent(null, null, Arrays.asList("test1"), null);
        Assert.assertEquals(ess.size(), 1);
        EventData testen = ess.get(0);

        tss = ts.queryTask("1=1", Arrays.asList("test1"), null);
        Assert.assertEquals(tss.size(), 1);
        TaskData tsdan = tss.get(0);
        
        Assert.assertEquals(testen.equals(teste), true);
        Assert.assertEquals(tsdan.equals(tsda), true);
        
        //加上其他属性比较
        String one=testen.getCategory()+testen.getEventDesc()+testen.getEventRepeat()+testen.getEventType()+testen.getLocation();
        String two=teste.getCategory()+teste.getEventDesc()+teste.getEventRepeat()+teste.getEventType()+teste.getLocation();
        Assert.assertEquals(one, two);
        
        one=tsdan.getCategory()+tsdan.getTaskdesc()+tsdan.getTstatus()+tsdan.getEventType()+tsdan.getLocation();
        two=tsda.getCategory()+tsda.getTaskdesc()+tsda.getTstatus()+tsda.getEventType()+tsda.getLocation();
        Assert.assertEquals(one, two);    
        
        
        //删除测试日历 
        cs.delCalendar("test1");
    }
}
