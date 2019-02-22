/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.DatePicker;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.ui.jdatepicker.UtilDateModel;
import org.ycalendar.ui.maincan.JCalendarPanel;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;
import org.ycalendar.util.UtilValidate;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

/**
 * 事件查询界面 TODO:监听日历变化事件，来更新日历
 *
 * @author lenovo
 */
public class EventFindUi extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(EventFindUi.class);

    DatePicker<Date> startPicker;

    DatePicker<Date> endPicker;

    JTextField taskCondi;

    JTable eventTable;
    //任务数据
    private EventModel eventDataModel;

    private Dictionary dicSer;

    //事件显示模板
    private JCalendarPanel eventPanel;

    public JCalendarPanel getEventPanel() {
        return eventPanel;
    }

    public void setEventPanel(JCalendarPanel eventPanel) {
        this.eventPanel = eventPanel;
    }

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }
    private EventService es;

    public EventService getEs() {
        return es;
    }

    public void setEs(EventService es) {
        this.es = es;
    }

    public EventFindUi(LayoutManager layout) {
        super(layout);

    }

    public EventFindUi() {
        this(new BorderLayout());

    }

    public void initUi() {
        JPanel condition = new JPanel(new FlowLayout(FlowLayout.LEFT));
        condition.add(new JLabel("开始日期"));
        condition.add(getStartPicker());
        condition.add(new JLabel("结束日期"));
        condition.add(getEndPicker());
        condition.add(new JLabel("包含"));
        taskCondi = new JTextField();
        taskCondi.setColumns(63);
        condition.add(taskCondi);
        JButton findButton = new JButton("查询");
        condition.add(findButton);

        findButton.addActionListener((ActionEvent e) -> {

            queryEvent();
        });
        add(condition, BorderLayout.NORTH);

        add(createTable(), BorderLayout.CENTER);

        loadCalendarlist();
        //监听日历选择变化

        MessageFac.getMemoryMsg().subMsg("SelectCalChange", (m) -> {
            List<ItemData<String, String>> ses = (List<ItemData<String, String>>) m.getProperty("selectedItem");
            calendarids.clear();
            for (ItemData<String, String> e : ses) {
                calendarids.add(e.e1);
            }
            log.info("reload  calendar :{}", calendarids);

            //重新装入数据，如果已经装入 
            if (dataLoad) {
                queryEvent();
            }
        });

        MessageFac.getMemoryMsg().subMsg("EUeventChange", (m) -> {
            if (dataLoad) {
                queryEvent();
            }
        });

        MessageFac.getMemoryMsg().subMsg("JPeventChange", (m) -> {
            if (dataLoad) {
                queryEvent();
            }
        });
    }

    private void loadCalendarlist() {

        List<DictionaryData> calList = dicSer.getDictList("calendar");
        calendarids = new ArrayList<>(calList.size());
        for (DictionaryData da : calList) {
            calendarids.add(da.getCode());
        }
        log.info("load calendar :{}", calendarids);
    }

    private List<String> calendarids;
    private boolean dataLoad = false;

    private void queryEvent() {
        Long s, e;
        String word = taskCondi.getText();

        Date st = startPicker.getModel().getValue();
        if (st == null) {
            s = null;
        } else {
            s = st.getTime();
        }

        Date et = endPicker.getModel().getValue();
        if (et == null) {
            e = null;
        } else {
            e = et.getTime();
        }
        List<EventData> events = es.readEvent(s, e, calendarids, word);

        eventDataModel.setDatas(events);
        dataLoad = true;
    }

    private JComponent getStartPicker() {
        if (startPicker == null) {

            startPicker = new JDatePicker<>(new JDatePanel<>(new UtilDateModel()), false);
            startPicker.setTextEditable(true);
            startPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) startPicker).getPreferredSize();
            preferredSize.setSize(220, preferredSize.getHeight());
            ((JComponent) startPicker).setPreferredSize(preferredSize);

        }

        return (JComponent) startPicker;
    }

    private JComponent getEndPicker() {
        if (endPicker == null) {

            endPicker = new JDatePicker<>(new JDatePanel<Date>(new UtilDateModel()), false);
            endPicker.setTextEditable(true);
            endPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) endPicker).getPreferredSize();
            preferredSize.setSize(220, preferredSize.getHeight());
            ((JComponent) endPicker).setPreferredSize(preferredSize);

        }
        return (JComponent) endPicker;
    }

    private JScrollPane createTable() {
        Class[] coluClass = {String.class, String.class, String.class, String.class, String.class};

        eventDataModel = new EventModel(coluClass, Collections.emptyList());
        eventTable = new JTable(eventDataModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 表格选择为单选  
        eventTable.setRowHeight(25); // 设置行高  
        eventTable.setShowVerticalLines(false);// 使表格的列线条不显示  

        // 设置各列的呈现方式  
        MiscUtil.HiddenColumn(eventTable, 0);

        TableColumn tem = eventTable.getColumn("标题");
        LabelColumn lc = new LabelColumn();
        tem.setCellEditor(lc);
        tem.setCellRenderer(lc);

        tem = eventTable.getColumn("开始日期");
        LabelColumn dl = new LabelColumn(SwingConstants.LEFT);
        tem.setCellEditor(dl);
        tem.setCellRenderer(dl);

        tem = eventTable.getColumn("结束日期");
        LabelColumn del = new LabelColumn(SwingConstants.CENTER);
        tem.setCellEditor(del);
        tem.setCellRenderer(del);

        tem = eventTable.getColumn("类别");
        LabelColumn type = new LabelColumn(SwingConstants.CENTER);
        tem.setCellEditor(type);
        tem.setCellRenderer(type);

        eventTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) {//点击几次 
                    //单击选择
                    oneClick();
                } else if (e.getClickCount() == 2) {
                    //双击编辑
                    twoClick();
                }
            }
        });

        return new JScrollPane(eventTable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1300, 280);
            }
        };
    }

    private void oneClick() {
        int row = eventTable.getSelectedRow();
        String eventid = eventTable.getValueAt(row, 0).toString();  //得到所在行的第一个列的值为eventid
        if (UtilValidate.isNotEmpty(eventid)) {
            EventData ed = es.readEvent(eventid);
        } else {
            log.debug("no event select");
        }

    }

    private void twoClick() {
        int row = eventTable.getSelectedRow();
        final String eventid = eventTable.getValueAt(row, 0).toString();  //得到所在行的第一个列的值为eventid
        if (UtilValidate.isNotEmpty(eventid)) {
            EventUi evu = new EventUi(MiscUtil.getComJFrame(this), true, 750, 850, eventid);
            evu.setEvSe(es);
            evu.setDicSer(dicSer);
            evu.initEventUi(null);

            Tuple2<EventData, Integer> newData = evu.getData();

            switch (newData.e2) {

                case 3:
                    //取消
                    break;
                case 2:
                    //删除,发消息
                    MemMsg md = new MemMsg("EPeventChange");
                    md.setProperty("eventid", eventid);
                    md.setProperty("actionType", "del");
                    MessageFac.getMemoryMsg().sendMsg(md);

                    break;
                case 0:
                    //insert不可能
                    break;
                case 1:
                    //update发消息
                    MemMsg m = new MemMsg("EPeventChange");
                    m.setProperty("eventid", eventid);
                    m.setProperty("actionType", "update");
                    MessageFac.getMemoryMsg().sendMsg(m);

                    break;
                default:
                    log.warn("error type {}", newData.e2);
            }

            evu.dispose();
        } else {
            log.debug("no event select");
        }
    }

    class EventModel implements TableModel {

        //名称
        private final String[] columnHeaders = {"", "标题", "开始日期", "结束日期", "类别"};
        private final Class[] coluClass;
        //数据
        private List<EventData> datas;

        public EventModel(Class[] coluClass, List<EventData> datas) {
            this.datas = datas;
            this.coluClass = coluClass;
        }

        void setDatas(List<EventData> datas) {
            this.datas = datas;
            fireValueChanged();
        }

        @Override
        public int getRowCount() {
            return datas.size();
        }

        @Override
        public int getColumnCount() {
            return coluClass.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnHeaders[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return coluClass[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        //5列
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            EventData t = datas.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return t.getEventid();
                case 1:
                    return t.getTitle();

                case 2:
                    return UtilDateTime.longToString(t.getStartTime(), UtilDateTime.defaultDatePattern);

                case 3:

                    return UtilDateTime.longToString(t.getEndTime(), UtilDateTime.defaultDatePattern);

                case 4:

                    String st = t.getCategory();
                    String result = dicSer.getDictValue("event_cate", st);
                    if (result == null) {
                        return "";
                    }
                    return result;

            }
            log.error("error columnIndex {}", columnIndex);
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        private final Set<TableModelListener> tableModelListeners = new HashSet<>();

        @Override
        public void addTableModelListener(TableModelListener l) {
            tableModelListeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            tableModelListeners.remove(l);

        }

        private void fireValueChanged() {

            // Update day table
            for (TableModelListener tl : tableModelListeners) {

                tl.tableChanged(new TableModelEvent(this));
            }
        }
    }
}
