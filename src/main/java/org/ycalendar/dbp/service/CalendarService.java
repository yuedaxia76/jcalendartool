package org.ycalendar.dbp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

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

    private static Logger log = LoggerFactory.getLogger(CalendarService.class);
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

        MemMsg md;
        md = new MemMsg("CalCreateNew");
        md.setProperty("caldarid", id);
        md.setProperty("name", disName);
        MessageFac.getMemoryMsg().sendMsg(md);
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
        log.info("del calendar {}", id);
        try {
            hd.begin();
            drs.delDictionaryData("calendar", id);
            //删除日历的事件与任务
            int dele = eser.delEventByCalendarId(id);
            int delt = tse.delTaskByCalendarId(id);
            hd.commit();

            MemMsg md = new MemMsg("CalDelete");
            md.setProperty("caldarid", id);

            MessageFac.getMemoryMsg().sendMsg(md);

            return new Tuple2<>(dele, delt);
        } catch (Exception e) {
            hd.rollback();
            log.error("delCalendar error{}", e.toString());
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

            MemMsg md = new MemMsg("CalChange");
            md.setProperty("caldarid", cid);
            md.setProperty("name", newName);
            MessageFac.getMemoryMsg().sendMsg(md);
        }

    }

    public DictionaryData getCalendar(String cid) {
        return drs.getDictDataByCode("calendar", cid);

    }
}
