package org.ycalendar.dbp.service;

import java.util.ArrayList;
import java.util.List;

import org.ycalendar.dbp.dao.BeanListHandler;
import org.ycalendar.dbp.dao.ExecuDbopention;
import org.ycalendar.dbp.dao.ExecueQuery;
import org.ycalendar.domain.EventData;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.UtilValidate;

/**
 * 事件管理
 *
 * @author lenovo
 *
 */
public class EventService extends GenalService {

    private ConfigInfo conInfo;

    public ConfigInfo getConInfo() {
        return conInfo;
    }

    public void setConInfo(ConfigInfo conInfo) {
        this.conInfo = conInfo;
    }

    /**
     * 创建事件
     *
     * @param ed
     */
    public void createEvent(EventData ed) {

        hd.exeTran(new ExecuDbopention<Void>() {
            @Override
            public Void exeDbAction() {
                if (UtilValidate.isEmpty(ed.getEventid())) {
                    ed.setEventid(MiscUtil.getId());
                }
                if (UtilValidate.isEmpty(ed.getCalendarid())) {
                    ed.setCalendarid(conInfo.getDefaultCalId());
                }
                if (Long.MIN_VALUE == ed.getCreateTime()) {
                    long s = System.currentTimeMillis();
                    ed.setCreateTime(s);
                    ed.setLastChangeTime(s);
                }

                gdao.create(hd.getCurCnection(), EventData.class, ed);
                return null;
            }
        });

    }

    public EventData saveOrUpdate(EventData ed) {
        if (UtilValidate.isEmpty(ed.getEventid())) {
            createEvent(ed);
            return ed;
        } else {
            if (readEvent(ed.getEventid()) == null) {
                createEvent(ed);
            } else {
                updateEvent(ed);
            }

            return ed;
        }
    }

    public Integer delEvent(String evid) {

        Integer result = hd.exeTran(new ExecuDbopention<Integer>() {
            public Integer exeDbAction() {
                return gdao.delete(hd.getCurCnection(), EventData.class, "eventid", evid);
            }
        });

        return result;
    }

    /**
     * 修改事件
     *
     * @param ed
     * @return
     */
    public Integer updateEvent(EventData ed) {
        if (UtilValidate.isEmpty(ed.getEventid())) {
            throw new RuntimeException("缺少主键");
        }
        Integer result = hd.exeTran(new ExecuDbopention<Integer>() {
            public Integer exeDbAction() {

                ed.setLastChangeTime(System.currentTimeMillis());

                return gdao.update(hd.getCurCnection(), EventData.class, ed, "eventid", false);

            }
        });

        return result;
    }

    /**
     * 读取
     *
     * @param eventid
     * @return
     */
    public EventData readEvent(String eventid) {
        if (UtilValidate.isEmpty(eventid)) {
            throw new RuntimeException("缺少主键");
        }
        return hd.exeTran(new ExecuDbopention<EventData>() {
            public EventData exeDbAction() {
                return gdao.read(hd.getCurCnection(), EventData.class, "eventid", eventid);

            }
        });

    }

    /**
     * TODO:对周期的处理 获取指定时间段的事件
     *
     * @param start
     * @param end
     * @return
     */
    public List<EventData> readEvent(long start, long end, List<String> calendarids, String title) {

        return hd.exeQuery(new ExecueQuery<List<EventData>>() {
            public List<EventData> exeDbAction() {
                BeanListHandler<EventData> rsh = new BeanListHandler<>(EventData.class);

                if (UtilValidate.isEmpty(calendarids) || calendarids.size() == 1) {
                    //(start_time>=开始 and start_time<结束) or (end_time>开始 and end_time<=结束 
                    StringBuffer sql = new StringBuffer("select * from event_data where ((start_time>=? and start_time<?) or (end_time>? and end_time<=?  )) and calendarid=?");
                    if (UtilValidate.isNotEmpty(title)) {
                        sql.append(" and (title like ?) order by start_time");
                        return gdao.query(hd.getCurCnection(), sql.toString(), rsh, start, end, start, end, UtilValidate.isEmpty(calendarids) ? conInfo.getDefaultCalId() : calendarids.get(0), '%' + title + '%');
                    } else {
                        sql.append("  order by start_time");
                        return gdao.query(hd.getCurCnection(), sql.toString(), rsh, start, end, start, end, UtilValidate.isEmpty(calendarids) ? conInfo.getDefaultCalId() : calendarids.get(0));
                    }

                } else {
                    StringBuffer sql = new StringBuffer(256);

                    sql.append("select * from event_data where ((start_time>=? and start_time<?) or (end_time>? and end_time<=?  ))  ");
                    List<Object> params = new ArrayList<>();
                    params.add(start);
                    params.add(end);
                    params.add(start);
                    params.add(end);

                    gdao.in(sql, params, "and", "calendarid", calendarids);
                    if (UtilValidate.isNotEmpty(title)) {
                        sql.append(" and (title like ?)");
                        params.add('%' + title + '%');
                    }
                    sql.append("  order by start_time");

                    return gdao.query(hd.getCurCnection(), sql, rsh, params);
                }

            }
        });

    }
}
