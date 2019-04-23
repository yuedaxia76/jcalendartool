package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.maincan.CalendarModel;
import org.ycalendar.ui.maincan.JCalendarPanel;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;

/**
 * 日历界面
 *
 * @author lenovo
 *
 */
public class CalendarUi {

    private static final Logger log = LoggerFactory.getLogger(CalendarUi.class);
    // JPanel left;

    // JPanel center;
    // JPanel right;
    JSplitPane splitLeft;

    //包含splitLeft
    JSplitPane splitright;
    JCalendarPanel jpce;

    EventFindUi eventFindPael;
    CalList call;

    private final EventService es;

    public CalendarUi(EventService es, Dictionary dicSer) {
        this.es = es;
        this.dicSer = dicSer;

    }

    public void showEventFindUi(final boolean show) {
        eventFindPael.setVisible(show);
    }

    JPanel left;
    EventPanel right;
    JPanel center;

    public void initUi(Rectangle scbounds) {
        left = new JPanel(new BorderLayout());

        JDatePanel<Calendar> jp = new JDatePanel<>(JDatePanel.createModel(), false);
        jp.addActionListener((e) -> jpce.selectByDay(jp.getModel().getValue()));

        jp.addTodayListener((e) -> jpce.selectByDay(Calendar.getInstance()));
        left.add(jp, BorderLayout.NORTH);
        // jp.setShowClear(false);

        JPanel leftList = new JPanel(new BorderLayout());

        left.add(leftList, BorderLayout.CENTER);

        JLabel calName = new JLabel("日历");
        leftList.add(calName, BorderLayout.NORTH);
        call = new CalList();
        call.setDicSer(dicSer);
        call.initCalList();

        leftList.add(call.getCalCompont(), BorderLayout.CENTER);
        //leftList.updateUI();

        //leftList.repaint();
        //leftList.setBorder(BorderFactory.createLineBorder(Color.BLACK ,5));
        // left.add(newcalJlist,BorderLayout.SOUTH);
        center = new JPanel(new BorderLayout());
        // center.add(new JButton("center"));

        //事件查询,默认隐藏
        eventFindPael = new EventFindUi();
        eventFindPael.setDicSer(dicSer);
        eventFindPael.setEs(es);
        eventFindPael.initUi();
        eventFindPael.setVisible(false);
        center.add(eventFindPael, BorderLayout.NORTH);
        jpce = new JCalendarPanel(new CalendarModel(Calendar.getInstance()), call.getSelectCans(), es);
        jpce.setDicSer(dicSer);

        center.add(jpce, BorderLayout.CENTER);
        eventFindPael.setEventPanel(jpce);

        right = new EventPanel(es, dicSer);
        right.setSelectCan(call.getSelectCans());
        right.intData();

        // 最右区域距离左边距离
        int leftWidth = (int) (scbounds.width * 0.85);

        splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, left, center);

        int llWidth = (int) (leftWidth * 0.15);
        splitLeft.setDividerLocation(llWidth);
        splitLeft.setOneTouchExpandable(false);
        splitLeft.setDividerSize(3);// 设置分隔线宽度的大小，以pixel为计算单位。

        splitright = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, splitLeft, right);

        splitright.setDividerLocation(leftWidth);
        splitright.setOneTouchExpandable(true);
        splitright.setDividerSize(8);// 设置分隔线宽度的大小，以pixel为计算单位。
        jpce.selectByDay(Calendar.getInstance());

    }

    public List<String> getSelectCal() {
        return call.getSelectCans();
    }
    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }

    public void restArea(Rectangle scbounds) {
        // 最右区域距离左边距离
        int leftWidth = (int) (scbounds.width * 0.85);

        int llWidth = (int) (leftWidth * 0.15);

        splitLeft.setDividerLocation(llWidth);

        splitright.setDividerLocation(leftWidth);
    }

    /**
     * 获取目前选择的日期
     *
     * @return
     */
    public Calendar getSelectDate() {
        return jpce.getSelectData();
    }

    public boolean deleteSelectEvent() {
        boolean result = jpce.delSelectEvent();
        if (!result) {
            JOptionPane.showMessageDialog(null, " 没有选择事件", "没有选择事件", JOptionPane.ERROR_MESSAGE);

        }
        return result;
    }

    public void editSelectEvent() {
        EventData ed = getSelectEventData();
        if (ed == null) {
            JOptionPane.showMessageDialog(null, " 没有选择事件", "没有选择事件", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EventUi evu = new EventUi(MiscUtil.getComJFrame(jpce), true, 750, 850, ed.getEventid());
        evu.setEvSe(es);
        evu.setDicSer(dicSer);

        evu.initEventUi();

        Tuple2<EventData, Integer> newData = evu.getData();

        refresh(newData);

        evu.dispose();
    }

    public EventData getSelectEventData() {
        return jpce.getSelectEventData();
    }

    public void refresh(Tuple2<EventData, Integer> data) {
        jpce.refreshData(data);
    }

    public void refresh() {
        jpce.reload();
    }

}
