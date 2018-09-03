package org.ycalendar;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.GenalService;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.domain.TaskData;
import org.ycalendar.util.MiscUtil;

public class TaskTest {

    private H2Dao hd;
    GernDAO dao;
    ConfigInfo conInfo;

    TaskService ts;

    private final String testTaskId = "testInser1";

    @BeforeSuite
    public void setup() {
        hd = H2Dao.getH2Dao();
        dao = new GernDAO();
        conInfo = new ConfigInfo();

        ts = new TaskService();

        ts.setGdao(dao);
        ts.setHd(hd);
        ts.setConInfo(conInfo);
        clear();
    }

    private void clear() {
        ts.delTask(testTaskId);
    }

    @AfterSuite
    public void testEnd() {

        GenalService gs = new GenalService();
        gs.setGdao(dao);
        gs.setHd(hd);
        clear();

    }

    @Test
    public void testInServ() {

        TaskData td = new TaskData();

        td.setTitle("测试");
        td.setTaskid(testTaskId);
        td.setCalendarid(conInfo.getDefaultCalId());
        td.setTstatus(conInfo.getDefaultTaskStatus());
        td.setPercentage(0);

        ts.createTask(td);
    }

    @Test(dependsOnMethods = {"testInServ"})
    public void testUpdate() {

        TaskData td = new TaskData();

        td.setTitle("测试111111");
        td.setTaskid(testTaskId);
        td.setCalendarid(conInfo.getDefaultCalId());
        long s = System.currentTimeMillis();
        td.setStartTime(s);
        td.setEndTime(s + 60 * 60 * 10000l);
        td.setTstatus(conInfo.getDefaultTaskStatus());
        td.setPercentage(10);

        int res = ts.updateTask(td);
        assert (res == 1);
    }

    @Test(dependsOnMethods = {"testInServ"})
    public void testReadId() {

        TaskData res = ts.readTask(testTaskId);
        assert (res != null);
    }

    @Test(dependsOnMethods = {"testUpdate"})
    public void testSelect() {

        long start = System.currentTimeMillis() - 100;
        long end = start + 60 * 70 * 10000l;
        List<TaskData> sees = ts.queryTask(null, start, end, null, "111111", 10);

        assert (sees.size() >= 1);

        boolean found = false;
        for (TaskData e : sees) {
            if (e.getTaskid().equals(testTaskId)) {
                found = true;
                break;
            }
        }
        assert (found);

        List<String> calendarids = MiscUtil.toList(conInfo.getDefaultCalId(), "test222");

        List<String> st = MiscUtil.toList(conInfo.getDefaultTaskStatus());

        sees = ts.queryTask(st, start, end, calendarids, "测试111111", -1);

        assert (sees.size() >= 1);

        assert (sees.get(0).getTaskid().equals(testTaskId));

        sees = ts.queryTask(null, start, end, calendarids, "YYY！！SDX", -1);

        assert (sees.isEmpty());

    }

    private void insert(String status, int count) {
        TaskData td = new TaskData();

        td.setTitle("测试" + count);
        td.setTaskid(testTaskId + count);
        td.setCalendarid(conInfo.getDefaultCalId());
        td.setTstatus(status);
        td.setPercentage(0);

        ts.createTask(td);
    }

    @Test(dependsOnMethods = {"testSelect"})
    public void testNotcomplete() {
        int count = 0;

        insert("unprocessed", count);
        count++;
        
        insert("processed", count);
        count++;
        
        List<String> st = ts.getNotcompleteStatus();
        List<TaskData> sees = ts.queryTask(st, -1, -1, null, null, -1);

        assert (sees.size() >= 3);

        boolean found = false;
        for (TaskData e : sees) {
            if (e.getTaskid().equals(testTaskId)) {
                found = true;
                break;
            }
        }
        assert (found);

    }

    @Test(dependsOnMethods = {"testNotcomplete"})
    public void testDel() {

        int res = ts.delTask(testTaskId);

        assert (res == 1);
    }
}
