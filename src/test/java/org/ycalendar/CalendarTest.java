/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar;

import java.io.IOException;
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
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.ui.YCalendar;

/**
 *
 * @author lenovo
 */
public class CalendarTest {

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

    }

    @Test
    public void testAdd() throws IOException, ParserException {
        cs.createCalendar("test", "这是一个测试");
    }

    @Test(dependsOnMethods = {"testAdd"})
    public void testUpdate() throws IOException, ParserException {
        String newName = "这是一个测试1";
        cs.renameCalendar("test", newName);
        DictionaryData cd = cs.getCalendar("test");

        Assert.assertEquals(cd.getDictdataValue(), newName);
    }

    @Test(dependsOnMethods = {"testUpdate"})
    public void testDel() throws IOException, ParserException {

        cs.delCalendar("test");
        DictionaryData cd = cs.getCalendar("test");
        Assert.assertEquals(cd, null);
    }

}
