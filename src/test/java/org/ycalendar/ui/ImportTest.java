/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import net.fortuna.ical4j.data.ParserException;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.DicService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.TaskService;
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

        TaskService ts = new TaskService();
        ts.setGdao(dao);
        ts.setHd(hd);
        ts.setConInfo(conInfo);

        yc.setTsSe(ts);
        yc.setDicSer(dic);

    }

    @AfterSuite
    public void testEnd() {

    }

    @Test
    public void testImpo() throws IOException, ParserException {
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test1.ics")) {
            Tuple2<Integer,Integer> result = yc.importIcs(in, null);
            Assert.assertEquals(result.e1.intValue(), 2);
            Assert.assertEquals(result.e2.intValue(), 1);
        }

    }

    @Test
    public void testImpoCsv() throws IOException, ParserException {
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test1.csv")) {
            Tuple2<Integer,Integer> result = yc.importCsv(in, null, "UTF-8");
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
}
