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
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

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
        JMenuItem item3 = new JMenuItem("日历");
        JMenuItem item4 = new JMenuItem("任务");
        eventAndTask.add(newEventme);
        eventAndTask.add(newTaskMe);
        eventAndTask.add(item3);
        eventAndTask.add(item4);

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
