package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ycalendar.dbp.service.ConfigInfo;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.InitDataService;
import org.ycalendar.domain.EventData;
import org.ycalendar.domain.TaskData;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;
import org.ycalendar.util.UtilValidate;

/**
 * 主界面
 *
 * @author lenovo
 *
 */
public class YCalendar {

    public static final Logger log = Logger.getLogger(YCalendar.class.getName());
    final JFrame f = new JFrame("日历");

    private JMenuBar jmb;

    private JToolBar myJToolBar;

    private CalendarUi caui;
    private TaskUi tasui;

    private JTabbedPane areare;

    private void initMenu() {
        jmb = new JMenuBar();

        JMenu eventAndTask = new JMenu("事件与任务");

        jmb.add(eventAndTask);

        JMenuItem newEventme = new JMenuItem("新事件");
        JMenuItem newTaskMe = new JMenuItem("新建任务");
        JMenuItem calMenu = new JMenuItem("日历");
        JMenuItem taskMenu = new JMenuItem("任务");
        JMenuItem importMenu = new JMenuItem("导入...");
        JMenuItem exportMenu = new JMenuItem("导出...");
        eventAndTask.add(newEventme);
        eventAndTask.add(newTaskMe);
        eventAndTask.add(calMenu);
        eventAndTask.add(taskMenu);
        eventAndTask.add(importMenu);
        eventAndTask.add(exportMenu);
        // 这里是添加菜单
        f.setJMenuBar(jmb);

        newEventme.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件

                newEvent();
            }
        });
        newTaskMe.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件

                newTask();
            }
        });
        calMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件

                areare.setSelectedIndex(0);
            }
        });
        taskMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件

                areare.setSelectedIndex(1);
            }
        });
        importMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件

                importFile();
            }
        });
        exportMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {// 只能检测到mousePressed事件
                exportToFile();
            }
        });
    }

    void exportToFile() {
        CalSelectUi cui = new CalSelectUi(f, true, 350, 550);
        cui.setDicSer(dicSer);
        cui.initCalSelectUi("请选择导出日历");
        String calid = cui.getSelectCans();
        cui.dispose();
        if (UtilValidate.isEmpty(calid)) {
            log.log(Level.INFO, "没有选择");
            return;
        }
        try {

            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
            jfc.addChoosableFileFilter(new JAVAFileFilter("ics"));//导入可选择的文件的后缀名类型
            jfc.addChoosableFileFilter(new JAVAFileFilter("csv"));
            jfc.showDialog(new JLabel(), "选择");
            File file = jfc.getSelectedFile();

            if (file.getName().toLowerCase().endsWith("ics")) {
                exportIcsFile(calid, file);
            } else {

            }

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    class JAVAFileFilter extends FileFilter {

        final String ext;

        public JAVAFileFilter(String ext) {
            this.ext = '.' + ext;
        }

        /* 在accept()方法中,当程序所抓到的是一个目录而不是文件时,我们返回true值,表示将此目录显示出来. */
        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String fileName = file.getName().toLowerCase();
            return fileName.endsWith(ext);

        }

        @Override
        public String getDescription() {
            return "只能选择" + ext + "文件";
        }
    }

    private void importFile() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
        jfc.addChoosableFileFilter(new JAVAFileFilter("ics"));//导入可选择的文件的后缀名类型
        jfc.addChoosableFileFilter(new JAVAFileFilter("csv"));
        jfc.showDialog(new JLabel(), "选择");
        File file = jfc.getSelectedFile();
        if (file == null) {
            log.log(Level.FINE, "no select file");
            return;
        }
        if (file.isFile()) {

            CalSelectUi cui = new CalSelectUi(f, true, 350, 550);
            cui.setDicSer(dicSer);
            cui.initCalSelectUi("请选择导入日历");
            String calid = cui.getSelectCans();
            cui.dispose();
            if (UtilValidate.isEmpty(calid)) {
                log.log(Level.INFO, "没有选择");
                return;
            }
            try {
                Tuple2<Integer, Integer> importCount;
                if (file.getName().toLowerCase().endsWith("ics")) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        importCount = importIcs(fis, calid);
                    }

                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        importCount = importCsv(fis, calid, "UTF-8");
                    }

                }
                //刷新显示
                caui.refresh();

                JOptionPane.showMessageDialog(f, "导入:" + importCount.e1 + "条事件," + importCount.e2 + "条任务", "导入成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                log.log(Level.SEVERE, "importFile {0} error{1}", new Object[]{file.getName(), e.toString()});
                JOptionPane.showMessageDialog(f, "错误:" + e.toString(), "错误", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    /**
     * 返回导入的数据条数
     *
     * @param in
     * @param calendarid 导入那个日历
     * @return 第一个导入的event ,第二个导入的任务
     * @throws IOException
     * @throws ParserException
     */
    protected Tuple2<Integer, Integer> importIcs(InputStream in, String calendarid) throws IOException, ParserException {

        int eventResult = 0;

        CalendarBuilder build = new CalendarBuilder();
        net.fortuna.ical4j.model.Calendar calendar = build.build(in);
        for (Iterator<CalendarComponent> i = calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
            VEvent event = (VEvent) i.next();
            EventData ev = new EventData();
            ev.setCalendarid(calendarid);
            ev.setEventid(event.getUid().getValue());
            // 开始时间
            ev.setStartTime(event.getStartDate().getDate().getTime());

            // 结束时间
            ev.setEndTime(event.getEndDate().getDate().getTime());

            // 主题
            ev.setTitle(event.getSummary().getValue());

            // 地点
            if (null != event.getLocation()) {
                ev.setLocation(event.getLocation().getValue());
            }

            // 描述
            if (null != event.getDescription()) {
                ev.setEventDesc(event.getDescription().getValue());
            }

            // 创建时间
            if (null != event.getCreated()) {
                ev.setCreateTime(event.getCreated().getDate().getTime());

            }
            // 最后修改时间
            if (null != event.getLastModified()) {
                ev.setLastChangeTime(event.getLastModified().getDate().getTime());

            }
            //隐私分类
            if (null != event.getClassification()) {
                ev.setEventType(event.getClassification().getValue());

            }
            //分类
            if (null != event.getProperty("CATEGORIES")) {
                Property pro = event.getProperty("CATEGORIES");
                String catl = pro.getValue();
                if (UtilValidate.isNotEmpty(catl)) {
                    String code = dicSer.getDictCode("event_cate", catl);
                    if (code != null) {
                        ev.setCategory(code);
                    } else {
                        log.log(Level.WARNING, "Category {0} no code", catl);
                        ev.setCategory("-1");
                    }

                } else {
                    ev.setCategory("-1");
                }

            } else {
                ev.setCategory("-1");
            }
            // 重复规则

            if (null != event.getProperty("RRULE")) {
                String errule = event.getProperty("RRULE").getValue();
                if (errule.startsWith(":")) {
                    errule = errule.substring(1);
                }
                Map<String, String> rulePropers = MiscUtil.strToMap(errule, true, ";");
                String freValue = rulePropers.get("FREQ");
                String value = dicSer.getDictValue("repeat_interval", freValue);
                if (value != null) {
                    ev.setEventRepeat(freValue);
                } else {
                    log.log(Level.WARNING, "RRULE {0} no value set none", errule);
                    ev.setEventRepeat("NONE");
                }
                String utilDate = rulePropers.get("UNTIL");
                if (UtilValidate.isNotEmpty(utilDate)) {
                    //重复截止时间
                    int tpos = utilDate.indexOf('T');
                    if (tpos < 0) {
                        ev.setRepeatEnd(UtilDateTime.toLong(utilDate, "yyyyMMdd"));
                    } else {
                        ev.setRepeatEnd(UtilDateTime.toLong(utilDate.substring(0, tpos), "yyyyMMdd"));
                    }

                } else {
                    ev.setRepeatEnd(0);
                }

            } else {
                //不重复
                ev.setEventRepeat("NONE");
                ev.setRepeatEnd(0);
            }
            // 提前多久提醒
            for (VAlarm alarm : event.getAlarms()) {
                Pattern p = Pattern.compile("[0-9]");
                String aheadTime = alarm.getTrigger().getValue();
                Matcher m = p.matcher(aheadTime);
                if (m.find()) {
                    String saveAl = aheadTime.substring(m.start());
                    ev.setRemind(saveAl.trim());
                } else {
                    ev.setRemind(aheadTime.trim());
                }
            }
            //可以多次导入，不出错
            evSe.saveOrUpdate(ev);
            eventResult++;
// 邀请人
//                if (null != event.getProperty("ATTENDEE")) {
//                    ParameterList parameters = event.getProperty("ATTENDEE").getParameters();
//                    System.out.println(event.getProperty("ATTENDEE").getValue().split(":")[1]);
//                    System.out.println(parameters.getParameter("PARTSTAT").getValue());
//                }

        }
        int taskResult = 0;
        for (Iterator<CalendarComponent> i = calendar.getComponents(Component.VTODO).iterator(); i.hasNext();) {
            VToDo task = (VToDo) i.next();
            TaskData td = new TaskData();
            td.setCalendarid(calendarid);
            td.setTaskid(task.getUid().getValue());
            td.setTitle(task.getSummary().getValue());
            if (task.getStatus() != null) {
                td.setTstatus(task.getStatus().getValue());
            } else {
                td.setTstatus(conInfo.getDefaultTaskStatus());
            }
            // 开始时间
            if (task.getStartDate() != null) {
                td.setStartTime(task.getStartDate().getDate().getTime());
            } else {
                td.setStartTime(null);
            }

            // 结束时间
            if (task.getDue() != null) {
                td.setEndTime(task.getDue().getDate().getTime());
            } else {
                td.setEndTime(null);
            }

            // 地点
            if (null != task.getLocation()) {
                td.setLocation(task.getLocation().getValue());
            }

            // 描述
            if (null != task.getDescription()) {
                td.setTaskdesc(task.getDescription().getValue());
            }

            // 创建时间
            if (null != task.getCreated()) {
                td.setCreateTime(task.getCreated().getDate().getTime());

            }
            // 最后修改时间
            if (null != task.getLastModified()) {
                td.setLastChangeTime(task.getLastModified().getDate().getTime());

            }
            //隐私分类
            if (null != task.getClassification()) {
                td.setEventType(task.getClassification().getValue());

            }
            //结束时间
            if (null != task.getDateCompleted()) {
                td.setCompleteTime(task.getDateCompleted().getDate().getTime());

            }
            //进度
            if (task.getPercentComplete() != null) {
                td.setPercentage(task.getPercentComplete().getPercentage());
            }
            //分类
            if (null != task.getProperty("CATEGORIES")) {
                Property pro = task.getProperty("CATEGORIES");
                String catl = pro.getValue();
                if (UtilValidate.isNotEmpty(catl)) {
                    String code = dicSer.getDictCode("event_cate", catl);
                    if (code != null) {
                        td.setCategory(code);
                    } else {
                        log.log(Level.WARNING, "Category {0} no dic code", catl);
                        td.setCategory("-1");
                    }

                } else {
                    td.setCategory("-1");
                }

            } else {
                td.setCategory("-1");
            }

            tsSe.saveOrUpdate(td);
            taskResult++;
        }

        return new Tuple2(eventResult, taskResult);

    }

    private final String[] csvFileHeaders = {"Subject", "Start Date", "Start Time", "End Date", "End Time", "All day event", "Reminder on/off", "Reminder Date", "Reminder Time", "Categories", "Description", "Location", "Private"};

    protected Tuple2<Integer, Integer> importCsv(InputStream in, String calendarid, String encode) throws IOException, ParserException {
        int result = 0;

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(csvFileHeaders);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));
                // 初始化 CSVParser object
                CSVParser csvFileParser = new CSVParser(br, csvFileFormat);) {
            // 初始化FileReader object

            // CSV文件records
            for (CSVRecord csvr : csvFileParser) {
                if (result > 0) {
                    EventData ev = new EventData();
                    ev.setCalendarid(calendarid);
                    ev.setTitle(csvr.get("Subject"));
                    long events = UtilDateTime.parseDate(csvr.get("Start Date") + " " + csvr.get("Start Time"), "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH).getTime();
                    ev.setStartTime(events);
                    ev.setEndTime(UtilDateTime.parseDate(csvr.get("End Date") + " " + csvr.get("End Time"), "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH).getTime());
                    ev.setAllDay("true".equalsIgnoreCase(csvr.get("All day event")));
                    String Reminder = csvr.get("Reminder on/off");
                    if ("TRUE".equalsIgnoreCase(Reminder)) {
                        //提醒，这里只有提醒时间，与ics不匹配
                        long eventr = UtilDateTime.parseDate(csvr.get("Reminder Date") + " " + csvr.get("Reminder Time"), "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH).getTime();
                        long timeInterval = eventr - events;
                        if (timeInterval <= 1000l * 60) {
                            ev.setRemind("0M");
                        } else if (timeInterval <= 1000l * 60 * 5) {
                            ev.setRemind("5M");
                        } else if (timeInterval <= 1000l * 60 * 15) {
                            ev.setRemind("15M");
                        } else if (timeInterval <= 1000l * 60 * 60) {
                            ev.setRemind("1H");
                        } else if (timeInterval <= 1000l * 60 * 60 * 2) {
                            ev.setRemind("2H");
                        } else if (timeInterval <= 1000l * 60 * 60 * 24) {
                            ev.setRemind("1D");
                        } else if (timeInterval <= 1000l * 60 * 60 * 24 * 2) {
                            ev.setRemind("2D");
                        } else {
                            ev.setRemind("1W");
                        }

                    } else {
                        ev.setRemind("-1S");
                    }

                    ev.setCategory(csvr.get("Categories"));
                    ev.setEventDesc(csvr.get("Description"));
                    ev.setLocation(csvr.get("Location"));
                    ev.setEventType(("FALSE".equalsIgnoreCase(csvr.get("Private"))) ? "PUBLIC" : "PRIVATE");
                    evSe.saveOrUpdate(ev);
                }

                result++;
            }

        }

        return new Tuple2(result - 1, 0);
    }

    private DateTime longToDateTime(long javatime) {
        DateTime icaltime = new DateTime(javatime);
        // 开始时间转换为UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
        icaltime.setUtc(false);
        return icaltime;
    }

    public void exportIcsFile(String calendarid, File toSave) throws FileNotFoundException, IOException, ParseException {

        // 创建一个时区（TimeZone）
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone(TimeZone.getDefault().getID());
        VTimeZone tz = timezone.getVTimeZone();

        // 创建日历
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        List<EventData> allEv = evSe.readEvent(null, null, Arrays.asList(UtilValidate.isNotEmpty(calendarid) ? calendarid : conInfo.getDefaultCalId()), null);
        for (EventData ed : allEv) {
            // 时间主题
            String summary = ed.getTitle();
            // 开始时间
            DateTime start = longToDateTime(ed.getStartTime());
 
            // 结束时间
            DateTime end = longToDateTime(ed.getEndTime());
 
            // 新建普通事件
            // VEvent event = new VEvent(icaltime, end, summary);
            // 定义全天事件（注意默认是UTC时间）
            VEvent event = new VEvent(start, end, summary);
            event.getProperties().add(new Location(ed.getLocation()));
            // 生成唯一标示
            event.getProperties().add(new Uid(ed.getEventid()));
            // 添加时区信息
            event.getProperties().add(tz.getTimeZoneId());
            event.getProperties().add(new Description(ed.getEventDesc()));
            event.getProperties().add(new Categories(ed.getCategory()));
            event.getProperties().add(new Created(longToDateTime(ed.getCreateTime())));
            event.getProperties().add(new LastModified(longToDateTime(ed.getLastChangeTime())));
            event.getProperties().add(new Clazz(ed.getEventType()));
            // 添加邀请者,目前不支持
//            Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
//            dev1.getParameters().add(Role.REQ_PARTICIPANT);
//            dev1.getParameters().add(new Cn("Developer 1"));
//            event.getProperties().add(dev1);

            // 重复事件
            String rep = ed.getEventRepeat();
            if (!"NONE".equalsIgnoreCase(rep)) {
                Recur recur;
                if (ed.getRepeatEnd() == 0) {
                    recur = new Recur(rep, Integer.MAX_VALUE);
                } else {
                    recur = new Recur(rep, new Date(ed.getRepeatEnd()));
                }
                RRule rule = new RRule(recur);
                event.getProperties().add(rule);
            }

            String ale = ed.getRemind();
            if (!"-1S".equalsIgnoreCase(ale)) {
                VAlarm valarm;
                // 提醒
                switch (ale) {
                    case "0M":
                        valarm = new VAlarm(new Dur(0, 0, 0, 0));
                        break;
                    case "5M":
                        valarm = new VAlarm(new Dur(0, 0, -5, 0));
                        break;
                    case "15M":
                        valarm = new VAlarm(new Dur(0, 0, -15, 0));
                        break;
                    case "1H":
                        valarm = new VAlarm(new Dur(0, -1, 0, 0));
                        break;
                    case "2H":
                        valarm = new VAlarm(new Dur(0, -2, 0, 0));
                        break;
                    case "1D":
                        valarm = new VAlarm(new Dur(-1, 0, 0, 0));
                        break;
                    case "2D":
                        valarm = new VAlarm(new Dur(-2, 0, 0, 0));
                        break;
                    case "1W":
                        valarm = new VAlarm(new Dur(-7, 0, 0, 0));
                        break;
                    default:
                        valarm = new VAlarm(new Dur(0, 0, 0, 0));
                }

                valarm.getProperties().add(new Summary("时间提醒"));
                valarm.getProperties().add(Action.DISPLAY);
                valarm.getProperties().add(new Description(ed.getTitle() + "在" + UtilDateTime.longToString(ed.getStartTime())));
                // 将VAlarm加入VEvent
                event.getAlarms().add(valarm);
            }

            // 添加事件
            calendar.getComponents().add(event);
        }
        List<TaskData> tasks = tsSe.queryTask("1=1", Arrays.asList(UtilValidate.isNotEmpty(calendarid) ? calendarid : conInfo.getDefaultCalId()), null);
        for (TaskData td : tasks) {
            // 时间主题
            String summary = td.getTitle();
            // 开始时间
            DateTime start = new DateTime(td.getStartTime());
            // 开始时间转换为UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
            start.setUtc(false);
            // 结束时间
            DateTime end = new DateTime(td.getEndTime());
            // 结束时间设置成UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
            end.setUtc(false);
            // 新建普通事件
            // VEvent event = new VEvent(icaltime, end, summary);
            // 定义全天事件（注意默认是UTC时间）
            VToDo todo = new VToDo(start, end, summary);
            todo.getProperties().add(new Location(td.getLocation()));
            // 生成唯一标示
            todo.getProperties().add(new Uid(td.getTaskid()));
            // 添加时区信息
            todo.getProperties().add(tz.getTimeZoneId());
            todo.getProperties().add(new Description(td.getTaskdesc()));
            todo.getProperties().add(new Categories(td.getCategory()));
            todo.getProperties().add(new Created(longToDateTime(td.getCreateTime())));
            todo.getProperties().add(new LastModified(longToDateTime(td.getLastChangeTime())));
            todo.getProperties().add(new Clazz(td.getEventType()));
            todo.getProperties().add(new Status(td.getTstatus()));
            // 添加邀请者,目前不支持
//            Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
//            dev1.getParameters().add(Role.REQ_PARTICIPANT);
//            dev1.getParameters().add(new Cn("Developer 1"));
//            event.getProperties().add(dev1);

            String ale = td.getRemind();
            if (!"-1S".equalsIgnoreCase(ale)) {
                VAlarm valarm;
                // 提醒
                switch (ale) {
                    case "0M":
                        valarm = new VAlarm(new Dur(0, 0, 0, 0));
                        break;
                    case "5M":
                        valarm = new VAlarm(new Dur(0, 0, -5, 0));
                        break;
                    case "15M":
                        valarm = new VAlarm(new Dur(0, 0, -15, 0));
                        break;
                    case "1H":
                        valarm = new VAlarm(new Dur(0, -1, 0, 0));
                        break;
                    case "2H":
                        valarm = new VAlarm(new Dur(0, -2, 0, 0));
                        break;
                    case "1D":
                        valarm = new VAlarm(new Dur(-1, 0, 0, 0));
                        break;
                    case "2D":
                        valarm = new VAlarm(new Dur(-2, 0, 0, 0));
                        break;
                    case "1W":
                        valarm = new VAlarm(new Dur(-7, 0, 0, 0));
                        break;
                    default:
                        valarm = new VAlarm(new Dur(0, 0, 0, 0));
                }

                valarm.getProperties().add(new Summary("时间提醒"));
                valarm.getProperties().add(Action.DISPLAY);
                valarm.getProperties().add(new Description(td.getTitle() + "在" + UtilDateTime.longToString(td.getStartTime())));
                // 将VAlarm加入VEvent
                todo.getAlarms().add(valarm);
                // 添加事件
                calendar.getComponents().add(todo);
            }
        }
        // 验证
        calendar.validate();

        log.log(Level.INFO, "export ics to {0}", toSave.getAbsolutePath());
        try (FileOutputStream fout = new FileOutputStream(toSave)) {

            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);
        }

    }

    private void newTask() {
        areare.setSelectedIndex(1);
        CalTaskUi evu = new CalTaskUi(f, true, 750, 850, null);
        evu.setTaskSe(tsSe);
        evu.setDicSer(dicSer);

        evu.initTaskUi(Calendar.getInstance());

        Tuple2<TaskData, Integer> newData = evu.getData();

        if (!newData.e2.equals(3)) {
            tasui.reload();
        }

        evu.dispose();
    }

    private void newEvent() {
        areare.setSelectedIndex(0);
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                EventUi evu = new EventUi(f, true, 750, 850, null);
                evu.setEvSe(evSe);
                evu.setDicSer(dicSer);
                Calendar curDay = caui.getSelectDate();
                if (curDay == null) {
                    JOptionPane.showMessageDialog(null, " 没有选择日期", "没有选择日期", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                evu.initEventUi(curDay);

                Tuple2<EventData, Integer> newData = evu.getData();

                caui.refresh(newData);

                evu.dispose();
            }

        });

    }

    private void intToolbar() {

        myJToolBar = new JToolBar();
        f.add(myJToolBar, BorderLayout.NORTH);

        // myJToolBar.setBounds(29, 12, 320, 38);
        myJToolBar.setBackground(new java.awt.Color(255, 255, 255));

        JButton jB_event;
        JButton jB_edit;
        JButton jB_del;
        JButton jB_task;

        jB_event = new JButton();
        myJToolBar.add(jB_event);
        jB_event.setText("事件");
        // jB_event.setPreferredSize(new java.awt.Dimension(80, 34));
        jB_event.setFont(new java.awt.Font("楷体", 0, 14));
        jB_event.setToolTipText("新建事件");
        // jB_event.setBackground(new java.awt.Color(255, 255, 128));
        jB_event.addActionListener((e) -> newEvent());

        jB_task = new JButton();
        myJToolBar.add(jB_task);
        jB_task.setText("任务");
        jB_task.setFont(new java.awt.Font("楷体", 0, 14));
        // jB_task.setBackground(new java.awt.Color(255, 255, 128));
        jB_task.setToolTipText("新建任务");
        // jB_task.setPreferredSize(new java.awt.Dimension(95, 34));
        jB_task.addActionListener((e) -> newTask());

        jB_edit = new JButton();
        myJToolBar.add(jB_edit);
        jB_edit.setText("编辑");
        jB_edit.setToolTipText("编辑选择的任务或事件");
        jB_edit.setFont(new java.awt.Font("楷体", 0, 14));

        jB_edit.addActionListener((e) -> edit());

        jB_del = new JButton();
        myJToolBar.add(jB_del);
        jB_del.setText("删除");
        jB_del.setFont(new java.awt.Font("楷体", 0, 14));
        // jB_del.setBackground(new java.awt.Color(255, 255, 128));
        jB_del.setToolTipText("删除选择的任务或事件");

        jB_del.addActionListener((e) -> delete());
    }

    //编辑事件或者任务
    private void edit() {
        if (areare.getSelectedIndex() == 0) {
            caui.editSelectEvent();
        } else {
            tasui.editTask();
        }
    }

    //删除事件或者任务
    private void delete() {
        if (areare.getSelectedIndex() == 0) {
            caui.deleteSelectEvent();
        } else {
            tasui.delTask();
        }
    }

    private void initFistFuncArea(Rectangle scbounds, JPanel pare) {

        caui = new CalendarUi(evSe, dicSer);
        caui.initUi(scbounds);

        pare.add(caui.splitright, BorderLayout.CENTER);
    }

    private void initSecondFuncArea(Rectangle scbounds, JPanel pare) {
        tasui = new TaskUi();
        tasui.setDicSer(dicSer);
        tasui.setTaskSer(tsSe);
        tasui.setEs(evSe);

        tasui.initUi(scbounds);

        pare.add(tasui.splitright, BorderLayout.CENTER);
    }

    private void resetFuncArea(Rectangle scbounds) {
        caui.restArea(scbounds);
        tasui.restArea(scbounds);
    }

    private void initMainform() {
        f.setLayout(new BorderLayout());

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置满屏
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle bounds = new Rectangle(screenSize);
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(f.getGraphicsConfiguration());
        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= insets.left + insets.right;
        bounds.height -= insets.top + insets.bottom;

        f.setBounds(bounds);

    }

    public YCalendar() {
        initMainform();

    }
    private ConfigInfo conInfo;

    public ConfigInfo getConInfo() {
        return conInfo;
    }

    public void setConInfo(ConfigInfo conInfo) {
        this.conInfo = conInfo;
    }

    public void initMainUi() {
        initMenu();

        intToolbar();
        initTab();

        initFistFuncArea(f.getBounds(), (JPanel) areare.getComponent(0));

        initSecondFuncArea(f.getBounds(), (JPanel) areare.getComponent(1));

        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resetFuncArea(f.getBounds());
            }
        });

        loadInitData();

        f.setVisible(true);
    }

    private InitDataService dataLoad;

    public void setDataLoad(InitDataService dataLoad) {
        this.dataLoad = dataLoad;
    }
    private TaskService tsSe;

    public void setTsSe(TaskService tsSe) {
        this.tsSe = tsSe;
    }

    private EventService evSe;

    public EventService getEvSe() {
        return evSe;
    }

    public void setEvSe(EventService evSe) {
        this.evSe = evSe;
    }

    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }

    // 装入初始化数据
    private void loadInitData() {
        dataLoad.loadData();
    }

    private void initTab() {
        areare = new JTabbedPane(JTabbedPane.BOTTOM);

        // areare.setAlignmentX(Component.LEFT_ALIGNMENT);
        // areare.setAlignmentY(Component.TOP_ALIGNMENT);
        JPanel jpanelFirst = new JPanel();
        jpanelFirst.setLayout(new BorderLayout());
        // jpanelFirst.setBorder(BorderFactory.createLineBorder(Color.red, 3) );

        areare.addTab("日历", null, jpanelFirst, "日历");

        JPanel jpanelSecond = new JPanel();
        jpanelSecond.setLayout(new BorderLayout());
        areare.addTab("任务", null, jpanelSecond, "任务");// 加入第一个页面

        f.add(areare, BorderLayout.CENTER);
    }

}
