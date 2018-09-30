/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.io.IOException;
import java.io.InputStream;
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

        yc.setEvSe(es);

        DicService dic = new DicService();
        dic.setGdao(dao);
        dic.setHd(hd);

        yc.setDicSer(dic);

    }

    @AfterSuite
    public void testEnd() {

    }

    @Test
    public void testImpo() throws IOException, ParserException {
        try (InputStream in = ImportTest.class.getClassLoader().getResourceAsStream("resource/test11.ics")) {
           int result= yc.importIcs(in, null);
           Assert.assertEquals(result, 2);
        }

    }
}
