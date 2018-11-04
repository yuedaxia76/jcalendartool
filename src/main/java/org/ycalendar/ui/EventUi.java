package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.DateModel;
import org.ycalendar.ui.jdatepicker.DatePicker;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.ui.jdatepicker.UtilDateModel;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilValidate;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

/**
 * 事件编辑界面
 *
 * @author lenovo
 */
public class EventUi extends JDialog {

    public static final Logger log = Logger.getLogger(EventUi.class.getName());
    private JToolBar myJToolBar;

    private JTextField titleText;

    private JComboBox<ItemData<String, String>> typeJlist;

    private JComboBox<ItemData<String, String>> calJlist;

    private JCheckBox allDay;
    private DatePicker<Date> startPicker;

    private DatePicker<Date> endPicker;

    private JComboBox<ItemData<String, String>> repeatCom;

    private ButtonGroup forever;

    private DatePicker<Date> repeatEndPicker;

    // 提醒时间，单位分钟
    private JComboBox<ItemData<String, String>> remindTime;

    private JTextArea eventDesc;

    private String eventId;

    private EventData data;
    private boolean userClose = false;
    //操作类型，0insert,1update,2delete,3取消
    private int acType = 3;

    private int getAcType() {
        if (userClose) {
            return acType;
        } else {
            return 3;
        }
    }

    /**
     * 返回编辑结果
     *
     * @return Tuple2<EventData,Integer> e1事件对象 e2 状态
     */
    public Tuple2<EventData, Integer> getData() {
        return new Tuple2<>(data, getAcType());
    }

    /**
     * 编辑数据
     *
     * @param parent
     * @param modal
     * @param windowWidth
     * @param windowHeight
     * @param eventId
     */
    public EventUi(Frame parent, boolean modal, int windowWidth, int windowHeight, String eventId) {
        super(parent, modal);
        this.eventId = eventId;
        setLayout(new BorderLayout());

        setSize(windowWidth, windowHeight);

    }

    private void loadData() {
        if (UtilValidate.isNotEmpty(eventId)) {
            data = evSe.readEvent(eventId);
            if (data == null) {
                log.log(Level.WARNING, "eventid {0} no data", eventId);
                super.setTitle("新建事件");
            } else {
                super.setTitle("编辑事件:" + data.getTitle());
            }
        } else {
            super.setTitle("新建事件");
        }

    }

    public void initEventUi() {
        initEventUi(null);
    }

    public void initEventUi(Calendar defaultDate) {

        setResizable(false);

        loadData();

        JPanel top = new JPanel(new BorderLayout());

        intToolbar(top);
        add(top, BorderLayout.NORTH);

        // JPanel center = new JPanel(new GridLayout(7, 2));
        JPanel center = new JPanel(new FlowLayout());
        // JPanel center = new JPanel();
        // BoxLayout lo = new BoxLayout(center, BoxLayout.Y_AXIS);
        // center.setLayout(lo);

        add(center, BorderLayout.CENTER);

        // Dimension td=titleText.getPreferredSize();
        //
        // titleText.setPreferredSize(preferredSize);
        center.add(new JLabel("标题:"));

        center.add(getTitleField());

        center.add(new JLabel("分类:"));
        center.add(getEventCategory());

        center.add(new JLabel("日历:"));
        center.add(getCalenList());

        center.add(getAllDay());
        center.add(new JLabel("开始:"));
        center.add((JComponent) getStartPicker(defaultDate));

        center.add(new JLabel("结束:"));
        center.add((JComponent) getEndPicker(defaultDate));

        center.add(new JLabel("重复:"));
        center.add(getRepeat());
        getForever(center);
        center.add(new JLabel("截至时间:"));
        center.add((JComponent) getRepeatEndPicker());

        center.add(new JLabel("提醒:"));
        center.add(getRemindTime());

        center.add(new JLabel("描述:"));
        center.add(getEventDesc());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setLocation((screenWidth - this.getWidth()) / 2, (screenHeight - this.getHeight()) / 2);
        super.setVisible(true);
    }

    private JTextField getTitleField() {
        if (titleText == null) {
            titleText = new JTextField();
            titleText.setColumns(63);
            if (this.data != null) {
                titleText.setText(data.getTitle());
            }
        }
        return titleText;
    }

    private JComboBox<ItemData<String, String>> getEventCategory() {
        if (typeJlist == null) {
            List<DictionaryData> typeLi = dicSer.getDictList("event_cate");

            int selectIndex = 0;

            Vector<ItemData<String, String>> typeModel = new Vector<ItemData<String, String>>(typeLi.size());
            for (int i = 0; i < typeLi.size(); i++) {
                DictionaryData dd = typeLi.get(i);
                typeModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && dd.getCode().equals(data.getCategory())) {

                    selectIndex = i;

                }
            }

            typeJlist = new JComboBox<ItemData<String, String>>(typeModel);
            Dimension preferredSize = typeJlist.getPreferredSize();
            preferredSize.setSize(328, preferredSize.getHeight());
            typeJlist.setPreferredSize(preferredSize);

            typeJlist.setSelectedIndex(selectIndex);

        }

        return typeJlist;
    }

    private JComboBox<ItemData<String, String>> getCalenList() {
        if (calJlist == null) {
            List<DictionaryData> calList = dicSer.getDictList("calendar");

            Vector<ItemData<String, String>> dayCountModel = new Vector<ItemData<String, String>>(calList.size());
            // dayCountModel.addElement(new ItemData<String, String>("main", "主日历"));
            int selectIndex = 0;

            for (int i = 0; i < calList.size(); i++) {
                DictionaryData dd = calList.get(i);
                dayCountModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && data.getCalendarid().equals(dd.getCode())) {

                    selectIndex = i;

                }
            }

            calJlist = new JComboBox<ItemData<String, String>>(dayCountModel);

            Dimension preferredSize = calJlist.getPreferredSize();
            preferredSize.setSize(328, preferredSize.getHeight());
            calJlist.setPreferredSize(preferredSize);
            calJlist.setSelectedIndex(selectIndex);
        }

        return calJlist;
    }

    private JCheckBox getAllDay() {
        if (allDay == null) {
            allDay = new JCheckBox("全天事件");
        }
        return allDay;
    }

    private DatePicker<Date> getStartPicker(Calendar defaultDate) {
        if (startPicker == null) {
            DateModel<Date> stime;

            if (this.data != null) {
                stime = new UtilDateModel(new Date(data.getStartTime()));

            } else {
                if (defaultDate == null) {
                    stime = new UtilDateModel(new Date(System.currentTimeMillis()));
                } else {
                    stime = new UtilDateModel(new Date(defaultDate.getTimeInMillis()));
                }

            }

            startPicker = new JDatePicker<Date>(new JDatePanel<Date>(stime), true);
            startPicker.setTextEditable(true);
            startPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) startPicker).getPreferredSize();
            preferredSize.setSize(290, preferredSize.getHeight());
            ((JComponent) startPicker).setPreferredSize(preferredSize);

        }

        return startPicker;
    }

    private DatePicker<Date> getEndPicker(Calendar defaultDate) {
        if (endPicker == null) {

            DateModel<Date> stime;

            if (this.data != null) {
                stime = new UtilDateModel(new Date(data.getEndTime()));

            } else {
                if (defaultDate == null) {
                    stime = new UtilDateModel(new Date(System.currentTimeMillis() + 60 * 60 * 1000l));
                } else {
                    stime = new UtilDateModel(new Date(defaultDate.getTimeInMillis() + 60 * 60 * 1000l));
                }

            }

            endPicker = new JDatePicker<Date>(new JDatePanel<Date>(stime), true);
            endPicker.setTextEditable(true);
            endPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) endPicker).getPreferredSize();
            preferredSize.setSize(290, preferredSize.getHeight());
            ((JComponent) endPicker).setPreferredSize(preferredSize);

        }

        return endPicker;
    }

    private JComboBox<ItemData<String, String>> getRepeat() {
        if (repeatCom == null) {
            List<DictionaryData> inte = dicSer.getDictList("repeat_interval");

            Vector<ItemData<String, String>> repeatModel = new Vector<ItemData<String, String>>(inte.size());
            int selectIndex = 0;

            for (int i = 0; i < inte.size(); i++) {
                DictionaryData dd = inte.get(i);
                repeatModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && Integer.valueOf(dd.getCode()).equals(data.getEventRepeat())) {

                    selectIndex = i;

                }
            }

            repeatCom = new JComboBox<ItemData<String, String>>(repeatModel);

            Dimension preferredSize = repeatCom.getPreferredSize();
            preferredSize.setSize(150, preferredSize.getHeight());
            repeatCom.setPreferredSize(preferredSize);
            repeatCom.setSelectedIndex(selectIndex);
        }

        return repeatCom;
    }

    private ButtonGroup getForever(JPanel center) {
        if (forever == null) {
            JRadioButton forEver = new JRadioButton("永远");
            JRadioButton limitTime = new JRadioButton("截至");

            center.add(forEver);
            center.add(limitTime);

            // 创建按钮组，把两个单选按钮添加到该组
            forever = new ButtonGroup();
            forever.add(forEver);
            forever.add(limitTime);

            if (this.data != null) {
                if (data.getRepeatEnd() == 0) {
                    forEver.setSelected(true);
                } else {
                    limitTime.setSelected(true);
                }

            } else {
                forEver.setSelected(true);
            }

            // 设置第一个单选按钮选中
        }
        return forever;

    }

    private DatePicker<Date> getRepeatEndPicker() {
        if (repeatEndPicker == null) {
            repeatEndPicker = new JDatePicker<Date>(new JDatePanel<Date>(new UtilDateModel()), false);
            repeatEndPicker.setTextEditable(true);
            repeatEndPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) repeatEndPicker).getPreferredSize();
            preferredSize.setSize(145, preferredSize.getHeight());
            ((JComponent) repeatEndPicker).setPreferredSize(preferredSize);

            if (this.data != null && data.getRepeatEnd() > 0) {
                UtilDateModel dm = (UtilDateModel) repeatEndPicker.getModel();
                dm.setValue(new Date(data.getRepeatEnd()));
            }

        }

        return repeatEndPicker;
    }

    private JComboBox<ItemData<String, String>> getRemindTime() {
        if (remindTime == null) {
            List<DictionaryData> remDic = dicSer.getDictList("remind_time");

            Vector<ItemData<String, String>> remindModel = new Vector<>(remDic.size());

            int selectIndex = 0;

            for (int i = 0; i < remDic.size(); i++) {
                DictionaryData dd = remDic.get(i);
                remindModel.insertElementAt(new ItemData<>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && dd.getCode().equals(data.getRemind())) {

                    selectIndex = i;

                }
            }

            remindTime = new JComboBox<>(remindModel);

            Dimension preferredSize = remindTime.getPreferredSize();
            preferredSize.setSize(166, preferredSize.getHeight());
            remindTime.setPreferredSize(preferredSize);
            remindTime.setSelectedIndex(selectIndex);
        }

        return remindTime;
    }

    private JTextArea getEventDesc() {
        if (eventDesc == null) {
            eventDesc = new JTextArea(40, 60);
            eventDesc.setLineWrap(true);
            eventDesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            if (this.data != null && UtilValidate.isNotEmpty(data.getEventDesc())) {
                eventDesc.setText(data.getEventDesc());
            }
        }
        return eventDesc;
    }

    private void intToolbar(JPanel top) {

        myJToolBar = new JToolBar();
        top.add(myJToolBar, BorderLayout.WEST);

        // myJToolBar.setBounds(29, 12, 320, 38);
        myJToolBar.setBackground(new java.awt.Color(255, 255, 255));

        JButton jB_save;
        JButton jB_cancel;
        JButton jB_del;

        jB_save = new JButton();
        myJToolBar.add(jB_save);
        jB_save.setText("保存并关闭");
        jB_save.setFont(new java.awt.Font("楷体", 0, 14));
        jB_save.setToolTipText("保存并关闭");
        jB_save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveEvent();

            }

        });

        jB_del = new JButton();
        myJToolBar.add(jB_del);
        jB_del.setText("删除");
        jB_del.setFont(new java.awt.Font("楷体", 0, 14));
        // jB_del.setBackground(new java.awt.Color(255, 255, 128));
        jB_del.setToolTipText("删除选择的任务或事件");
        jB_del.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delEvent();

            }

        });

        jB_cancel = new JButton();
        myJToolBar.add(jB_cancel);
        jB_cancel.setText("取消");
        jB_cancel.setToolTipText("不保存关闭");
        jB_cancel.setFont(new java.awt.Font("楷体", 0, 14));
        jB_cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cacelEvent();

            }

        });
    }

    void delEvent() {
        acType = 2;
        if (data != null) {
            evSe.delEvent(data.getEventid());
            MemMsg m = new MemMsg("EUeventChange");
            m.setProperty("event", data);
            m.setProperty("actionType", "del");
            MessageFac.getMemoryMsg().sendMsg(m);
        }
        super.setVisible(false);
        userClose = true;
    }

    void cacelEvent() {
        acType = 3;
        super.setVisible(false);
        userClose = true;
    }

    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }

    private EventService evSe;

    public EventService getEvSe() {
        return evSe;
    }

    public void setEvSe(EventService evSe) {
        this.evSe = evSe;
    }

    void saveEvent() {
        if (data == null) {
            data = new EventData();
            acType = 0;
        } else {
            acType = 1;
        }

        String title = titleText.getText();
        if (UtilValidate.isEmpty(title)) {
            JOptionPane.showMessageDialog(this, "错误，标题必须填写", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            data.setTitle(title);
        }
        ItemData<String, String> eventtYPE = typeJlist.getItemAt(typeJlist.getSelectedIndex());
        data.setCategory(eventtYPE.e1);

        ItemData<String, String> calen = calJlist.getItemAt(calJlist.getSelectedIndex());
        data.setCalendarid(calen.e1);

        data.setAllDay(allDay.isSelected());

        Date st = (Date) startPicker.getModel().getValue();
        data.setStartTime(st.getTime());

        Date et = (Date) endPicker.getModel().getValue();
        data.setEndTime(et.getTime());

        ItemData<String, String> rep = repeatCom.getItemAt(repeatCom.getSelectedIndex());
        data.setEventRepeat(rep.e1);

        ItemData<String, String> remTi = remindTime.getItemAt(remindTime.getSelectedIndex());
        data.setRemind(remTi.e1);

        data.setEventDesc(eventDesc.getText());

        evSe.saveOrUpdate(data);

        MemMsg m = new MemMsg("EUeventChange");
        m.setProperty("event", data);
        m.setProperty("actionType", "saveOrUpdate");
        MessageFac.getMemoryMsg().sendMsg(m);

        this.eventId = data.getEventid();
        super.setVisible(false);
        userClose = true;
        //super.dispose();
    }

}
