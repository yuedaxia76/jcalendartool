package org.ycalendar.ui.maincan;

import java.util.Calendar;

import javax.swing.event.ChangeListener;

import org.ycalendar.domain.EventData;
import org.ycalendar.util.Tuple2;

public interface MainDateModel  {
    /**
     * Adds a ChangeListener. ChangeListeners will be notified when the internal
     * state of the control changes. This means that as a user scrolls through
     * dates the internal model changes, which fires a ChangeEvent each time it
     * changes.
     *
     * @param changeListener The changelistener to add.
     */
    void addChangeListener(ChangeListener changeListener);

    /**
     * Removes the specified ChangeListener. ChangeListeners will be notified
     * when the selected date is changed.
     *
     * @param changeListener The changelistener to remove.
     */
    void removeChangeListener(ChangeListener changeListener);
 

    /**
     * Getters and setters which represent a gregorian date.
     *
     * @return year
     */
    int getYear();

    /**
     * 设置年份，会重新计算
     *
     * @param year year
     */
    void setYear(int year);

    /**
     * Getters and setters which represent a gregorian date.
     *
     * @return month
     */
    int getMonth();
    
 
 

    /**
     * 设置月份，会重新计算
     *
     * @param month month
     */
    void setMonth(int month);

    /**
     * Getters and setters which represent a gregorian date.
     *
     * @return day
     */
    int getDay();

 
    
    

    /**
     * Add or substract number of years.
     *
     * @param add years
     */
    void addYear(int add);

    /**
     * Add or substract number of months.
     *
     * @param add months
     */
    void addMonth(int add);

 

    /**
     * Get the value this model represents.
     *
     * @return current value
     */
    Calendar getValue();

 
    CellObject getEventValue();
 

    /**
     * Set the value as selected.
     *
     * @param selected select this value?
     */
    void setSelected(int x,int y);
    
    
    CellObject getCalendar(int x,int y);
    /**
     * 设置一个单元格的值
     * @param x
     * @param y
     * @param value
     */
    void setCalendar(int x,int y,CellObject value);
    
    /**
     * 为一个日期增加事件
     * @param x
     * @param y
     * @param value
     */
    void addEvent(int x,int y,EventData value);
    
    /**
     * 
     * @param todayCal
     * @return -1前一月，0本月，1下一月
     */
    public int getCanType(Calendar todayCal);
    
    public Tuple2<Integer,Integer> getIndex(Calendar cal);

 
    public void addEvent(EventData e);
    
    /**
     * 清除日历数据
     */
    public void clearEvent();
}
