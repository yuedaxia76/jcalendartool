package org.ycalendar;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.EventData;

/**
 * Unit test for simple App.
 */
public class EventServiceTest {

    private H2Dao hd;
    GernDAO dao;
    ConfigInfo conInfo;

    EventService es;

    private final String testEventId = "testInser1";

    @BeforeSuite
    public void setup() {
        hd = H2Dao.getH2Dao();
        dao = new GernDAO();
        conInfo = new ConfigInfo();

        es = new EventService();

        es.setGdao(dao);
        es.setHd(hd);
        es.setConInfo(conInfo);
        clear();
    }

    private void clear() {
        es.delEvent(testEventId);
    }

    @AfterSuite
    public void testEnd() {

        clear();

    }

    @Test
    public void testInServ() {

        EventData ed = new EventData();

        ed.setTitle("测试");
        ed.setEventid(testEventId);
        ed.setCalendarid(conInfo.getDefaultCalId());

        es.createEvent(ed);
    }
    //TODO:测试跨月事件选择

    @Test(dependsOnMethods = {"testSelect"})
    public void testDel() {

        int res = es.delEvent(testEventId);

        assert (res == 1);
    }

    @Test(dependsOnMethods = {"testInServ"})
    public void testUpdate() {

        EventData ed = new EventData();

        ed.setTitle("测试111111");
        ed.setEventid(testEventId);
        ed.setCalendarid(conInfo.getDefaultCalId());
        long s = System.currentTimeMillis();
        ed.setStartTime(s);
        ed.setEndTime(s + 60 * 60 * 10000l);

        int res = es.updateEvent(ed);
        assert (res == 1);
    }

    @Test(dependsOnMethods = {"testInServ"})
    public void testReadId() {

        EventData res = es.readEvent(testEventId);
        assert (res != null);
    }

    @Test(dependsOnMethods = {"testUpdate"})
    public void testSelect() {

        long start = System.currentTimeMillis() - 100;
        long end = System.currentTimeMillis() + 10000;
        List<EventData> sees = es.readEvent(start, end, null, null);

        assert (sees.size() >= 1);

        boolean found = false;
        for (EventData e : sees) {
            if (e.getEventid().equals(testEventId)) {
                found = true;
                break;
            }
        }
        assert (found);

        List<String> calendarids = new ArrayList<>();
        calendarids.add(conInfo.getDefaultCalId());
        calendarids.add("test222");
        sees = es.readEvent(start, end, calendarids, "测试11111");

        assert (sees.size() >= 1);

        assert (sees.get(0).getEventid().equals(testEventId));

        sees = es.readEvent(start, end, null, "YYY！！SDX");

        assert (sees.size() == 0);

    }
}
