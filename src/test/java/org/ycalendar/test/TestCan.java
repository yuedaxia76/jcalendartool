package org.ycalendar.test;

import java.util.Calendar;

import org.ycalendar.util.UtilDateTime;

public class TestCan {
	public static void main(String[] args) {
		//ffee();
		//f11fee();
		//test00();
                eee();
	}
        public static void eee(){
            System.out.print("wwwwwwwwwwwwwwww");
            float re=4/100f;
            System.out.println(re);
        }

	public static void ffede() {
		Calendar calendarValue = Calendar.getInstance();
		System.out.println(calendarValue.get(Calendar.MONTH));
		System.out.println(calendarValue.get(Calendar.DATE));

		calendarValue.set(Calendar.DATE, 1);
		System.out.println(UtilDateTime.longToString(calendarValue.getTimeInMillis()));
		System.out.println(calendarValue.get(Calendar.MONTH));

		calendarValue.set(Calendar.DATE, 0);
		System.out.println(UtilDateTime.longToString(calendarValue.getTimeInMillis()));
		System.out.println(calendarValue.get(Calendar.MONTH));

		calendarValue.set(Calendar.DATE, -1);
		System.out.println(UtilDateTime.longToString(calendarValue.getTimeInMillis()));
		System.out.println(calendarValue.get(Calendar.MONTH));

		calendarValue.set(Calendar.DATE, 33);
		System.out.println(UtilDateTime.longToString(calendarValue.getTimeInMillis()));
		System.out.println(calendarValue.get(Calendar.MONTH));

		calendarValue.set(Calendar.MONTH, 0);

		System.out.println(UtilDateTime.longToString(calendarValue.getTimeInMillis()));
		System.out.println(calendarValue.get(Calendar.MONTH));
	}

	public static void ffee() {
		Calendar c = Calendar.getInstance(); // 当前时间

		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日
		// System.out.println(c.get(Calendar.DAY_OF_MONTH));
		System.out.println(UtilDateTime.longToString(c.getTimeInMillis())); // 输出本月一号的date

		System.out.println(c.get(Calendar.DAY_OF_WEEK)); // 获取本月一号是星期几

		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - (c.get(Calendar.DAY_OF_WEEK) - 1)); // 设置为本月日历中的第一天
		
		System.out.println(UtilDateTime.longToString(c.getTimeInMillis()));// 输出本月日历显示的第一天的date
		
		System.out.println(c.get(Calendar.DATE));// 输出本月日历显示的第一天

		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 6 * 7 - 1);// 获取日历上显示的最后一天

		System.out.println(UtilDateTime.longToString(c.getTimeInMillis()));// 输出日历上显示的最后一天的date

	}
	public static void f11fee() {
 
		Calendar [][] days;
			Calendar c= Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日
			
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - (c.get(Calendar.DAY_OF_WEEK) - 1)); // 设置为本月日历中的第一天
	 
			days=new Calendar[6][7];
			for (int i=0;i<6;i++) {
				for (int j=0;j<7;j++) {
					days[i][j]=UtilDateTime.copyCalendar(c);
					 
	 
					c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
				}
			}
			
			System.out.println(UtilDateTime.longToString(days[0][0].getTimeInMillis()));
			System.out.println(UtilDateTime.longToString(days[5][6].getTimeInMillis()));
			
			Calendar d= Calendar.getInstance();
			d.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日			
			d.set(Calendar.DAY_OF_MONTH, d.get(Calendar.DAY_OF_MONTH) - (d.get(Calendar.DAY_OF_WEEK) - 1)); 
			d.set(Calendar.DAY_OF_MONTH, d.get(Calendar.DAY_OF_MONTH) + 6 * 7 - 1);// 获取日历上显示的最后一天
			System.out.println(UtilDateTime.longToString(d.getTimeInMillis()));// 输出日历上显示的最后一天的date
	}
	
	public static void test00() {
		Calendar curDayInfo=Calendar.getInstance();
		curDayInfo.set(Calendar.DAY_OF_MONTH, 1);// 设置为本月1日
		curDayInfo.set(Calendar.HOUR_OF_DAY, 0);
		curDayInfo.set(Calendar.MINUTE, 0);
		curDayInfo.set(Calendar.SECOND, 0);
		
		System.out.println(UtilDateTime.longToString(curDayInfo.getTimeInMillis()));// 输出日历上显示的最后一天的date
		
		
		curDayInfo.set(Calendar.HOUR_OF_DAY, 12);
		curDayInfo.set(Calendar.MINUTE, 0);
		curDayInfo.set(Calendar.SECOND, 0);
		
		System.out.println(UtilDateTime.longToString(curDayInfo.getTimeInMillis()));// 输出日历上显示的最后一天的date
	}
	
	
	

}
