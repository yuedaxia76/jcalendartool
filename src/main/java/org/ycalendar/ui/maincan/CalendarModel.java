package org.ycalendar.ui.maincan;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ycalendar.domain.EventData;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;


public   class CalendarModel implements MainDateModel {
	public static final Logger log = Logger.getLogger(CalendarModel.class.getName());
	private final Set<ChangeListener> changeListeners;

	private int x;
	private int y;

	private final Calendar curDayInfo;

	@Override
	public int getCanType(Calendar todayCal) {
		if (todayCal.get(Calendar.MONTH) == curDayInfo.get(Calendar.MONTH) && todayCal.get(Calendar.YEAR) == curDayInfo.get(Calendar.YEAR)) {
			return 0;
		}
		if (todayCal.getTimeInMillis() < curDayInfo.getTimeInMillis()) {
			return -1;
		}
		return 1;
	}

	@Override
	public CellObject getCalendar(int x, int y) {
		return days[x][y];
	}

	protected CellObject[][] days;

	public CalendarModel(Calendar cur) {
		changeListeners = new HashSet<ChangeListener>();
		curDayInfo = cur;
		curDayInfo.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日
		curDayInfo.set(Calendar.HOUR_OF_DAY, 0);
		curDayInfo.set(Calendar.MINUTE, 0);
		curDayInfo.set(Calendar.SECOND, 0);
		
		days = new CellObject[6][7];
		initMonth(curDayInfo);
		if(!setCurSelect(curDayInfo)) {
			x=0;y=0;
			log.log(Level.WARNING, "not set select day,date :{0}", UtilDateTime.longToString(curDayInfo.getTimeInMillis()));
		}
		
	}

 
	private boolean setCurSelect(Calendar calendarValue) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (calendarValue.get(Calendar.YEAR) == days[i][j].dateCan.get(Calendar.YEAR) && calendarValue.get(Calendar.DATE) == days[i][j].dateCan.get(Calendar.DATE)
						&& calendarValue.get(Calendar.MONTH) == days[i][j].dateCan.get(Calendar.MONTH)) {
					this.x = i;
					this.y = j;
					return true;
				}

			}
		}
		return false;
	}

	private void initMonth(Calendar s) {

		Calendar c = UtilDateTime.copyCalendar(s);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日

		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - (c.get(Calendar.DAY_OF_WEEK) - 1)); // 设置为本月日历中的第一天

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				days[i][j] = new CellObject(UtilDateTime.copyCalendar(c));

				c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
			}
		}
	}

	@Override
	public synchronized void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	@Override
	public synchronized void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	protected synchronized void fireChangeEvent() {
		for (ChangeListener changeListener : changeListeners) {
			changeListener.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public int getDay() {
		return days[x][y].dateCan.get(Calendar.DATE);
	}

	@Override
	public int getMonth() {
		return days[x][y].dateCan.get(Calendar.MONTH);
	}

	@Override
	public int getYear() {
		return days[x][y].dateCan.get(Calendar.YEAR);
	}

	@Override
	public Calendar getValue() {

		return fromCalendar(days[x][y].dateCan);
	}

	@Override
	public void addMonth(int add) {

		curDayInfo.add(Calendar.MONTH, add);

		this.initMonth(curDayInfo);

		setCurSelect(curDayInfo);
		fireChangeEvent();
	}

	@Override
	public void setMonth(int month) {
		curDayInfo.set(Calendar.MONTH, month);

		this.initMonth(curDayInfo);

		setCurSelect(curDayInfo);
		fireChangeEvent();
	}

	@Override
	public void setYear(int year) {
        if(curDayInfo.get(Calendar.YEAR)==year) {
        	return;
        }
		curDayInfo.set(Calendar.YEAR, year);
		this.initMonth(curDayInfo);
		setCurSelect(curDayInfo);
		fireChangeEvent();
	}

	@Override
	public void addYear(int add) {

		curDayInfo.add(Calendar.YEAR, add);
		this.initMonth(curDayInfo);
		setCurSelect(curDayInfo);
		fireChangeEvent();
	}

	public void setSelected(int x, int y) {
		this.x = x;
		this.y = y;
	}

 
    protected Calendar fromCalendar(Calendar from) {
        return (Calendar) from.clone();
    }

 
    protected Calendar toCalendar(Calendar from) {
        return (Calendar) from.clone();
    }

	@Override
	public CellObject getEventValue() {
 
		return days[x][y];
	}

	@Override
	public void addEvent(EventData e) {
		for (int i = 0; i < days.length; i++) {
			for (int j = 0; j < days[i].length; j++) {
				
				Calendar tem=(Calendar)days[i][j].dateCan.clone();
				UtilDateTime.setDayStart(tem);
				long s=tem.getTimeInMillis();
				UtilDateTime.setDayEnd(tem);
				long en=tem.getTimeInMillis();
				if((e.getStartTime()>s && e.getStartTime()<en) || (e.getEndTime()<en && e.getEndTime()>s ) || (s>e.getStartTime() && en<e.getEndTime())) {
					days[i][j].addEventData(e);
					//System.out.println("day :"+UtilDateTime.longToString(s)+"事件"+e.getTitle()+" 事件开始"+UtilDateTime.longToString(e.getStartTime()));
				}
				if(s>e.getEndTime()) {
					return;
				}
 
			}
		}	 
		
	}

	@Override
	public void setCalendar(int x, int y, CellObject value) {
		if(value==null) return;
		days[x][y] = value;
		
	}

	@Override
	public void addEvent(int x, int y, EventData value) {
		CellObject cellObj=this.getCalendar(x, y);
		cellObj.addEventData(value);
		
	}

	@Override
	public Tuple2<Integer, Integer> getIndex(Calendar calendarValue) {
		if(calendarValue!=null) {
			for (int i=0;i<days.length;i++) {
				for (int j=0;j<days[i].length;j++) {
					if (calendarValue.get(Calendar.YEAR) == days[i][j].dateCan.get(Calendar.YEAR) && calendarValue.get(Calendar.DATE) == days[i][j].dateCan.get(Calendar.DATE)
							&& calendarValue.get(Calendar.MONTH) == days[i][j].dateCan.get(Calendar.MONTH)) {
						return new Tuple2<>(i,j);
					}
				}
				
			}			
		}else {
			log.log(Level.WARNING, "calendarValue is null");
		}

		return new Tuple2<>(-1,-1);
	}

}