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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Iterator;
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
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.InitDataService;
import org.ycalendar.domain.EventData;
import org.ycalendar.domain.TaskData;
import org.ycalendar.util.Tuple2;

/**
 * 主界面
 *
 * @author lenovo
 *
 */
public class YCalendar {

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
        eventAndTask.add(newEventme);
        eventAndTask.add(newTaskMe);
        eventAndTask.add(calMenu);
        eventAndTask.add(taskMenu);
        eventAndTask.add(importMenu);

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
        jfc.addChoosableFileFilter(new JAVAFileFilter("ics"));//导入可选择的文件的后缀名类型
        jfc.addChoosableFileFilter(new JAVAFileFilter("csv"));
        jfc.showDialog(new JLabel(), "选择");
        File file = jfc.getSelectedFile();
        if (file.isFile()) {
            try {
                if (file.getName().toLowerCase().endsWith("ics")) {
                    importIcs(file);
                } else {
                    importCsv(file);
                }
            } catch (IOException | ParserException e) {
                JOptionPane.showMessageDialog(f, "错误:" + e.toString(), "错误", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    private void importIcs(File ics) throws IOException, ParserException {
        FileInputStream fis = new FileInputStream(ics);
        CalendarBuilder build = new CalendarBuilder();
        net.fortuna.ical4j.model.Calendar calendar = build.build(fis);
        for (Iterator<CalendarComponent> i = calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
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
                Matcher m = p.matcher(aheadTime);
                int timeTemp = Integer.valueOf(m.replaceAll("").trim());
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

    private void importCsv(File ics) throws FileNotFoundException, IOException, ParserException {

    }

    public void exportFile() {
        try {
            // 创建一个时区（TimeZone）
            TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
            TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
            VTimeZone tz = timezone.getVTimeZone();

            // 创建日历
            net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
            calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            // 时间主题
            String summary = "重复事件测试";
            // 开始时间
            DateTime start = new DateTime(1478016000000l);
            // 开始时间转换为UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
            start.setUtc(true);
            // 结束时间
            DateTime end = new DateTime(1478016000000l);
            // 结束时间设置成UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
            end.setUtc(true);
            // 新建普通事件
            // VEvent event = new VEvent(start, end, summary);
            // 定义全天事件（注意默认是UTC时间）
            VEvent event = new VEvent(new Date(1478016000000l), new Date(1478016000000l), summary);
            event.getProperties().add(new Location("南京堵路"));
            // 生成唯一标示
            event.getProperties().add(new Uid("ss"));
            // 添加时区信息
            event.getProperties().add(tz.getTimeZoneId());
            // 添加邀请者
            Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
            dev1.getParameters().add(Role.REQ_PARTICIPANT);
            dev1.getParameters().add(new Cn("Developer 1"));
            event.getProperties().add(dev1);
            // 重复事件
            Recur recur = new Recur(Recur.WEEKLY, Integer.MAX_VALUE);
            recur.getDayList().add(WeekDay.MO);
            recur.getDayList().add(WeekDay.TU);
            recur.getDayList().add(WeekDay.WE);
            recur.getDayList().add(WeekDay.TH);
            recur.getDayList().add(WeekDay.FR);
            RRule rule = new RRule(recur);
            event.getProperties().add(rule);
            // 提醒,提前10分钟
            VAlarm valarm = new VAlarm(new Dur(0, 0, -10, 0));
            valarm.getProperties().add(new Summary("Event Alarm"));
            valarm.getProperties().add(Action.DISPLAY);
            valarm.getProperties().add(new Description("Progress Meeting at 9:30am"));
            // 将VAlarm加入VEvent
            event.getAlarms().add(valarm);
            // 添加事件
            calendar.getComponents().add(event);
            // 验证
            calendar.validate();
            FileOutputStream fout = new FileOutputStream("D://2.ics");
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newTask() {
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

    private void editEvent() {
        EventData ed = caui.getSelectEventData();
        if (ed == null) {
            JOptionPane.showMessageDialog(null, " 没有选择事件", "没有选择事件", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EventUi evu = new EventUi(f, true, 750, 850, ed.getEventid());
        evu.setEvSe(evSe);
        evu.setDicSer(dicSer);

        evu.initEventUi();

        Tuple2<EventData, Integer> newData = evu.getData();

        caui.refresh(newData);

        evu.dispose();

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

        jB_edit = new JButton();
        myJToolBar.add(jB_edit);
        jB_edit.setText("编辑");
        jB_edit.setToolTipText("编辑选择的任务或事件");
        jB_edit.setFont(new java.awt.Font("楷体", 0, 14));
        // TODO:未来支持两种
        jB_edit.addActionListener((e) -> editEvent());

        jB_del = new JButton();
        myJToolBar.add(jB_del);
        jB_del.setText("删除");
        jB_del.setFont(new java.awt.Font("楷体", 0, 14));
        // jB_del.setBackground(new java.awt.Color(255, 255, 128));
        jB_del.setToolTipText("删除选择的任务或事件");
        // TODO:未来支持两种
        jB_del.addActionListener((e) -> caui.deleteSelectEvent());
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
