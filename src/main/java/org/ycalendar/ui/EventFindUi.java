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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.jdatepicker.DatePicker;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.ui.jdatepicker.UtilDateModel;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.UtilDateTime;

/**
 * 事件查询界面
 *
 * @author lenovo
 */
public class EventFindUi extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(EventFindUi.class);
    private final String[] columnHeaders = {"", "标题", "开始日期", "结束日期", "类别"};

    DatePicker<Date> startPicker;

    DatePicker<Date> endPicker;

    JTextField taskCondi;

    JTable taskTable;
    //任务数据
    private EventModel taskDataModel;

    private Dictionary dicSer;

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
        add(condition, BorderLayout.NORTH);

        add(createTable(), BorderLayout.CENTER);
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

        taskDataModel = new EventModel(coluClass, Collections.emptyList());
        taskTable = new JTable(taskDataModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 表格选择为单选  
        taskTable.setRowHeight(25); // 设置行高  
        taskTable.setShowVerticalLines(false);// 使表格的列线条不显示  

        // 设置各列的呈现方式  
        MiscUtil.HiddenColumn(taskTable, 0);

        TableColumn tem = taskTable.getColumn("标题");
        LabelColumn lc = new LabelColumn();
        tem.setCellEditor(lc);
        tem.setCellRenderer(lc);

        tem = taskTable.getColumn("开始日期");
        LabelColumn dl = new LabelColumn(SwingConstants.LEFT);
        tem.setCellEditor(dl);
        tem.setCellRenderer(dl);

        tem = taskTable.getColumn("结束日期");
        LabelColumn del = new LabelColumn(SwingConstants.CENTER);
        tem.setCellEditor(del);
        tem.setCellRenderer(del);

        tem = taskTable.getColumn("类别");
        LabelColumn type = new LabelColumn(SwingConstants.CENTER);
        tem.setCellEditor(type);
        tem.setCellRenderer(type);

        taskTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {//点击几次，这里是双击事件

                }
            }
        });

        return new JScrollPane(taskTable);
    }

    class EventModel implements TableModel {
        //名称

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
