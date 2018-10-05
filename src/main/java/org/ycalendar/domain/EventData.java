package org.ycalendar.domain;

import org.ycalendar.util.UtilValidate;

public class EventData {

    private String eventid;

    private String eventDesc;

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    private String title;
    //事件分类
    private String category;

    private long startTime;
    private long endTime;
    //日历id
    private String calendarid;

    public String getCalendarid() {
        return calendarid;
    }

    public void setCalendarid(String calendarid) {
        this.calendarid = calendarid;
    }
    //重复周期，0不重复，1每天，2每周
    private int eventRepeat;

    public int getEventRepeat() {
        return eventRepeat;
    }

    public void setEventRepeat(int eventRepeat) {
        this.eventRepeat = eventRepeat;
    }

    //重复截至时间0永远，其他截至时间
    private long repeatEnd;

    public long getRepeatEnd() {
        return repeatEnd;
    }

    public void setRepeatEnd(long repeatEnd) {
        this.repeatEnd = repeatEnd;
    }

    //提醒时间-1S不提醒 
    private String remind;

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    //全天事件
    private boolean allDay;

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventData) {
            if (UtilValidate.isEmpty(eventid)) {
                return false;
            } else {
                return eventid.equals(((EventData) obj).getEventid());
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return eventid + "|" + title;
    }

    private long createTime = Long.MIN_VALUE;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(long lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }
    private long lastChangeTime = Long.MIN_VALUE;
    //地点
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //公开：隐私
    private String eventType;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}
