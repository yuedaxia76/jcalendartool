/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
 

/**
 *
 * @author lenovo
 */
public class Testics {
    public static void main(String[] args) throws IOException, ParserException {
        Testics t=new Testics();
        File f=new File("C:\\temp\\test11.ics");
        t.importIcs(f);
    }

    int max = 3;

    private int count;

    private void importIcs(File ics) throws IOException, ParserException {
        try (FileInputStream fis = new FileInputStream(ics);) {

            CalendarBuilder build = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = build.build(fis);
            for (Iterator<CalendarComponent> i = calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
                if (count > max) {
                    break;
                }
                count++;
                VEvent event = (VEvent) i.next();

                // 开始时间
                System.out.println("开始时间：" + event.getStartDate().getValue());
                // 结束时间
                System.out.println("结束时间：" + event.getEndDate().getValue());
                if (null != event.getProperty("DTSTART")) {
                    ParameterList parameters = event.getProperty("DTSTART").getParameters();
                    if (null != parameters.getParameter("VALUE")) {
                        System.out.println(parameters.getParameter("VALUE").getValue());
                    }
                }
                // 主题
                System.out.println("主题：" + event.getSummary().getValue());
                // 地点
                if (null != event.getLocation()) {
                    System.out.println("地点：" + event.getLocation().getValue());
                }
                // 描述
                if (null != event.getDescription()) {
                    System.out.println("描述：" + event.getDescription().getValue());
                }
                // 创建时间
                if (null != event.getCreated()) {
                    System.out.println("创建时间：" + event.getCreated().getValue());
                }
                // 最后修改时间
                if (null != event.getLastModified()) {
                    System.out.println("最后修改时间：" + event.getLastModified().getValue());
                }
                // 重复规则
                if (null != event.getProperty("RRULE")) {
                    System.out.println("RRULE:" + event.getProperty("RRULE").getValue());
                }
                // 提前多久提醒
                for (Iterator alrams = event.getAlarms().iterator(); alrams.hasNext();) {
                    VAlarm alarm = (VAlarm) alrams.next();
                    Pattern p = Pattern.compile("[^0-9]");
                    String aheadTime = alarm.getTrigger().getValue();
                    System.out.println("提前:" + aheadTime);
                    Matcher m = p.matcher(aheadTime);


                    
                    int timeTemp = Integer.valueOf(m.replaceAll("").trim());
                    
                    p = Pattern.compile("[0-9]");
                     m = p.matcher(aheadTime);
                     if(m.find()){
                         int s=m.start();
                     System.out.println("开始:" + s);
                      System.out.println("开始:" + aheadTime.substring(s));
                     }
                     
                    if (aheadTime.endsWith("W")) {
                        System.out.println("提前多久：" + timeTemp + "周");
                    } else if (aheadTime.endsWith("D")) {
                        System.out.println("提前多久：" + timeTemp + "天");
                    } else if (aheadTime.endsWith("H")) {
                        System.out.println("提前多久：" + timeTemp + "小时");
                    } else if (aheadTime.endsWith("M")) {
                        System.out.println("提前多久：" + timeTemp + "分钟");
                    } else if (aheadTime.endsWith("S")) {
                        System.out.println("提前多久：" + timeTemp + "秒");
                    }
                }
                // 邀请人
                if (null != event.getProperty("ATTENDEE")) {
                    ParameterList parameters = event.getProperty("ATTENDEE").getParameters();
                    System.out.println(event.getProperty("ATTENDEE").getValue().split(":")[1]);
                    System.out.println(parameters.getParameter("PARTSTAT").getValue());
                }
                System.out.println("----------------------------");
            }
        }

    }
}
