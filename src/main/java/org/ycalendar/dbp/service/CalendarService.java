package org.ycalendar.dbp.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 增加，删除日历
 *
 * @author lenovo
 */
public class CalendarService extends GenalService {

    public static Logger log = Logger.getLogger(CalendarService.class.getName());
    private DicService drs;

    public DicService getDrs() {
        return drs;
    }

    public void setDrs(DicService drs) {
        this.drs = drs;
    }

    /**
     * 建立一个日历
     *
     * @param id
     * @param disName
     */
    public void createCalendar(String id, String disName) {
        DictionaryData d = new DictionaryData();
        d.setDictType("calendar");
        d.setCode(id);
        d.setDictdataValue(disName);
        d.setDictOrder(System.currentTimeMillis());
        d.setId(MiscUtil.getId());

        drs.insertDictionary(d);
    }

    /**
     * 指定日历，是否已经建立
     *
     * @param id
     * @return
     */
    public boolean hasCalendar(String id) {

        return drs.getDictValue("calendar", id) != null;
    }
    private TaskService tse;

    public TaskService getTse() {
        return tse;
    }

    public void setTse(TaskService tse) {
        this.tse = tse;
    }
    private EventService eser;

    public EventService getEser() {
        return eser;
    }

    public void setEser(EventService eser) {
        this.eser = eser;
    }

    public Tuple2<Integer, Integer> delCalendar(String id) {
        //删除日历 
        log.log(Level.INFO, "del calendar {0}", id);
        try {
            hd.begin();
            drs.delDictionaryData("calendar", id);
            //删除日历的事件与任务
            int dele = eser.delEventByCalendarId(id);
            int delt = tse.delTaskByCalendarId(id);
            hd.commit();
            return new Tuple2<>(dele, delt);
        } catch (Exception e) {
            hd.rollback();
            log.log(Level.SEVERE, "delCalendar error{0}", e.toString());
            throw new RuntimeException(e);
        }

    }

    /**
     * 修改名称
     *
     * @param cid
     * @param newName
     */
    public void renameCalendar(String cid, String newName) {
        DictionaryData d = drs.getDictDataByCode("calendar", cid);
        if (d != null) {
            d.setDictdataValue(newName);
            drs.updateDictionary(d);
        }

    }

    public DictionaryData getCalendar(String cid) {
        return drs.getDictDataByCode("calendar", cid);

    }
}
