package org.ycalendar.ui.maincan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.ycalendar.domain.EventData;
import org.ycalendar.util.UtilValidate;
// 单元格保存的对象
public class CellObject {

	Calendar dateCan;
	List<EventData> dayEvents;

	CellObject(Calendar dateCan) {
		this.dateCan = dateCan;
	}
	//当前选择的事件
	private EventData selectEvent;

	public EventData getSelectEvent() {
		return selectEvent;
	}
	public void setSelectEvent(EventData selectEvent) {
		this.selectEvent = selectEvent;
	}
	public void setEventData(List<EventData> des) {
		dayEvents = des;
	}
	public void addEventData(EventData ed) {
		if(dayEvents==null) {
			dayEvents=new ArrayList<>();
		}
		dayEvents.add(ed);
	}
	public void del(EventData ed) {
		if(ed==null || UtilValidate.isEmpty(dayEvents)) {
			return ;
		}
		for(Iterator<EventData> it=dayEvents.iterator();it.hasNext();) {
			EventData t=it.next();
			if(t.equals(ed)) {
				it.remove();
				return;
			}
		}
			
 
		
	}
	
	public void update(EventData ed) {
		if(ed==null || UtilValidate.isEmpty(dayEvents)) {
			return ;
		}
		for(int i=0;i<dayEvents.size();i++) {
			EventData t=dayEvents.get(i);
			if(t.equals(ed)) {
				dayEvents.set(i, ed);
				
				//System.out.println("修改"+ed.getTitle());
				return;
			}			
		}
		//System.out.println("没有z找到修改"+ed.getTitle()+"id:"+ed.getEventid()+"\n"+dayEvents);
	}
	
	public void insert(EventData ed) {
		if(ed==null ) {
			return ;
		}
		if(dayEvents==null) {
			dayEvents=new ArrayList<>();
		}
		//System.out.println("add "+ed.getTitle());
		dayEvents.add(ed);

	}
	@Override
	public String toString() {
		return dateCan == null ? "" : Integer.toString(dateCan.get(Calendar.DAY_OF_MONTH));
	}

}
