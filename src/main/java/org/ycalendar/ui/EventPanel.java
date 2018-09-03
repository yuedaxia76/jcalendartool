/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.ComponentIconDefaults;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;

/**
 * 界面右面的事件面板
 *
 * @author lenovo
 */
public class EventPanel extends JPanel {

    private final EventService es;

    public EventService getEs() {
        return es;
    }
 
    public EventPanel(EventService es, Dictionary dicSer ) {
        super(new BorderLayout());
        this.es = es;
        this.dicSer = dicSer;
    }
    private final Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }
    //记录日期
    private final Calendar curDate = Calendar.getInstance();

    //设置日期显示
    private void showDate(Date d) {
        getShowDay().setText(getDateStr(d));
        curDate.setTime(d);
    }

    private String getDateStr(java.util.Date date) {
        String tod = UtilDateTime.toDateString(date) + "    " + MiscUtil.toLocalWeek(UtilDateTime.getWeek(date)) + "     ";
        return tod;
    }
    JLabel showDay;

    private JLabel getShowDay() {
        if (showDay == null) {
            String tod = getDateStr(new Date());
            showDay = new JLabel(tod, JLabel.LEFT);
        }
        return showDay;
    }

    JLabel showCond;

    private JLabel getShowCondition() {
        if (showCond == null) {

            showCond = new JLabel("未来5天事件");
        }
        return showCond;
    }

    public void intData() {
        JPanel top = new JPanel(new BorderLayout());
        JPanel dateShow = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateShow.add(getShowDay());
        dateShow.add(getPreviousDayButton());
        dateShow.add(getToDayButton());
        dateShow.add(getNextDayButton());
        dateShow.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        top.add(dateShow, BorderLayout.NORTH);
        JPanel conShow = new JPanel(new FlowLayout());
        conShow.add(getShowCondition());
        conShow.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        top.add(conShow, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        add(loadEvent(new Date(), 5, getSelectCans()), BorderLayout.CENTER);
    }

    public void reload() {
        Calendar cal = Calendar.getInstance();
        if (curDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && curDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && curDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
            fireReloadEvent(curDate.getTime(), 5);
        } else {
            fireReloadEvent(curDate.getTime(), 1);
        }

    }

    JList<ItemData<EventData, String>> eventJlist;

    private Component loadEvent(Date start, final int days, List<String> calendarids) {

        long end = UtilDateTime.getDayEnd(start, days);
        long s = UtilDateTime.getDayStart(start);
        List<EventData> dayEvents = es.readEvent(s, end, calendarids, null);

        DefaultListModel<ItemData<EventData, String>> listModel = new DefaultListModel<>();
        dayEvents.forEach((e) -> {
            listModel.addElement(new ItemData<>(e, e.getTitle()));
        });
        if (eventJlist == null) {
            eventJlist = new JList<ItemData<EventData, String>>(listModel);
            eventJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            eventJlist.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() == 2) {
                        //System.out.println("2次");
                        JList<ItemData<EventData, String>> myList = (JList<ItemData<EventData, String>>) e.getSource();
                        int index = myList.getSelectedIndex(); // 已选项的下标

                        ItemData<EventData, String> obj = myList.getModel().getElementAt(index); // 取出数据

                        editEvent(obj.e1, days);

                    }
                }

            });
        }
        return eventJlist;

    }

    private void editEvent(EventData ed, final int days) {
        EventUi evu = new EventUi(MiscUtil.getComJFrame(this), true, 750, 850, ed.getEventid());
        evu.setEvSe(es);
        evu.setDicSer(dicSer);
        evu.initEventUi(null);

        Tuple2<EventData, Integer> newData = evu.getData();

        switch (newData.e2) {

            case 3:
                //取消
                break;
            default:

                fireReloadEvent(curDate.getTime(), days);

        }

        evu.dispose();
    }

    //重新装入数据
    private void fireReloadEvent(Date start, final int days) {

        long s = UtilDateTime.getDayStart(start);

        long end = UtilDateTime.getDayEnd(start, days);
        List<EventData> dayEvents = this.es.readEvent(s, end, getSelectCans(), null);
        DefaultListModel<ItemData<EventData, String>> listModel = new DefaultListModel<ItemData<EventData, String>>();
        for (EventData e : dayEvents) {
            listModel.addElement(new ItemData<>(e, e.getTitle()));
        }
        eventJlist.setModel(listModel);
    }
    private List<String> selectCan;

    public void setSelectCan(List<String> selectCan) {
        this.selectCan = selectCan;
    }

    private List<String> getSelectCans() {


        return selectCan;
    }
    private JButton previousDayButton;

    private JButton getPreviousDayButton() {
        if (previousDayButton == null) {
            previousDayButton = new JButton();
            previousDayButton.setIcon(ComponentIconDefaults.getInstance().getPreviousMonthIconEnabled());
            previousDayButton.setDisabledIcon(ComponentIconDefaults.getInstance().getPreviousMonthIconDisabled());
            previousDayButton.setText("");
            previousDayButton.setPreferredSize(new java.awt.Dimension(20, 15));
            previousDayButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            previousDayButton.setFocusable(false);
            previousDayButton.setOpaque(true);
            // TODO:统一国际化
            previousDayButton.setToolTipText("前一天");
            previousDayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    curDate.add(Calendar.DAY_OF_MONTH, -1);
                    Date s = curDate.getTime();
                    showDate(s);
                    getShowCondition().setText("事件");

                    fireReloadEvent(s, 1);

                }

            });
        }
        return previousDayButton;
    }

    private JButton nextDayhButton;

    private JButton getNextDayButton() {
        if (nextDayhButton == null) {
            nextDayhButton = new JButton();
            nextDayhButton.setIcon(ComponentIconDefaults.getInstance().getNextMonthIconEnabled());
            nextDayhButton.setDisabledIcon(ComponentIconDefaults.getInstance().getNextMonthIconDisabled());
            nextDayhButton.setText("");
            nextDayhButton.setPreferredSize(new java.awt.Dimension(20, 15));
            nextDayhButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            nextDayhButton.setFocusable(false);
            nextDayhButton.setOpaque(true);

            // TODO:统一国际化
            nextDayhButton.setToolTipText("下一天");

            nextDayhButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    curDate.add(Calendar.DAY_OF_MONTH, 1);
                    Date s = curDate.getTime();
                    showDate(s);
                    getShowCondition().setText("事件");

                    fireReloadEvent(s, 1);

                }

            });
        }
        return nextDayhButton;
    }

    private JButton todayDayButton;

    private JButton getToDayButton() {
        if (todayDayButton == null) {
            todayDayButton = new JButton();
            todayDayButton.setIcon(ComponentIconDefaults.getInstance().getTodayIconEnabled());
            todayDayButton.setDisabledIcon(ComponentIconDefaults.getInstance().getTodayIconDisable());
            todayDayButton.setText("");
            todayDayButton.setPreferredSize(new java.awt.Dimension(17, 17));
            todayDayButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            todayDayButton.setFocusable(false);
            todayDayButton.setOpaque(true);
            // TODO:统一国际化
            todayDayButton.setToolTipText("今天");
            todayDayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Date s = new Date();
                    showDate(s);
                    getShowCondition().setText("未来5天事件");
                    fireReloadEvent(s, 5);
                }

            });
        }
        return todayDayButton;
    }
}
