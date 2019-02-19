package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.maincan.CalendarModel;
import org.ycalendar.ui.maincan.JCalendarPanel;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilValidate;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

/**
 * 日历界面
 *
 * @author lenovo
 *
 */
public class CalendarUi {
    // JPanel left;

    // JPanel center;
    // JPanel right;
    JSplitPane splitLeft;

    //包含splitLeft
    JSplitPane splitright;
    JCalendarPanel jpce;

    EventFindUi eventFindPael;
    JList<ItemData<String, String>> calJlist;

    private final EventService es;

    public CalendarUi(EventService es, Dictionary dicSer) {
        this.es = es;
        this.dicSer = dicSer;

    }

    public void showEventFindUi(final boolean show) {
        eventFindPael.setVisible(show);
    }

    private List<String> getSelectCans() {
        List<ItemData<String, String>> ses = calJlist.getSelectedValuesList();
        List<String> result = new ArrayList<>();
        if (UtilValidate.isNotEmpty(ses)) {
            for (ItemData<String, String> c : ses) {
                result.add(c.e1);
            }
        }

        return result;
    }
    //private void select

    private JList<ItemData<String, String>> getCalJlist() {
        if (calJlist == null) {
            DefaultListModel<ItemData<String, String>> sm = getCalendarlist();
            calJlist = new JList<ItemData<String, String>>(sm);
            calJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            for (int i = 0; i < sm.size(); i++) {
                calJlist.setSelectedIndex(i);
            }
            //变化监听
            calJlist.addListSelectionListener((ListSelectionEvent e) -> {
                MemMsg m = new MemMsg("SelectCalChange");
                m.setProperty("changeInfo", e);

                List<ItemData<String, String>> ses = calJlist.getSelectedValuesList();
                m.setProperty("selectedItem", ses);
                MessageFac.getMemoryMsg().sendMsg(m);
            });
        }
        return calJlist;

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

        left.add(leftList);

        JLabel calName = new JLabel("日历");
        leftList.add(calName, BorderLayout.NORTH);

        leftList.add(getCalJlist());

        // left.add(calJlist,BorderLayout.SOUTH);
        center = new JPanel(new BorderLayout());
        // center.add(new JButton("center"));

        //事件查询,默认隐藏
        eventFindPael = new EventFindUi();
        eventFindPael.setDicSer(dicSer);
        eventFindPael.setEs(es);
        eventFindPael.initUi();
        eventFindPael.setVisible(false);
        center.add(eventFindPael, BorderLayout.NORTH);
        jpce = new JCalendarPanel(new CalendarModel(Calendar.getInstance()), getSelectCans(), es);
        jpce.setDicSer(dicSer);

        center.add(jpce, BorderLayout.CENTER);
        eventFindPael.setEventPanel(jpce);

        right = new EventPanel(es, dicSer);
        right.setSelectCan(getSelectCans());
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

    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }

    private DefaultListModel<ItemData<String, String>> getCalendarlist() {
        DefaultListModel<ItemData<String, String>> listModel = new DefaultListModel<ItemData<String, String>>();
        List<DictionaryData> calList = dicSer.getDictList("calendar");
        for (DictionaryData da : calList) {
            listModel.addElement(new ItemData<String, String>(da.getCode(), da.getDictdataValue()));
        }
        return listModel;
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
