package org.ycalendar.ui;

import org.ycalendar.uiutil.ValueRadioButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.ButtonGroup;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.ycalendar.dbp.service.CalendarService;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.domain.DictionaryData;

import org.ycalendar.domain.TaskData;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilDateTime;
import org.ycalendar.util.UtilValidate;

/**
 * 任务界面
 *
 * @author lenovo
 *
 */
public class TaskUi {

    private static final Logger log = LoggerFactory.getLogger(TaskUi.class);

    private final String[] columnHeaders = {"", "标题", "%完成", "状态", "开始日期", "结束日期"};
    JPanel left;

    JPanel center;

    EventPanel right;
    JTextField taskCondi;

    JSplitPane splitLeft;

    JSplitPane splitright;
    private JTable taskTable;
    //任务数据
    private TaskModel taskDataModel;

    CalList call;

    private void createLeft() {
        left = new JPanel(new BorderLayout());
        JDatePanel jp = new JDatePanel(JDatePanel.createModel(), false);
        left.add(jp, BorderLayout.NORTH);
        //左边tab
        JTabbedPane dispa = new JTabbedPane(JTabbedPane.TOP);

        JPanel jpanelFirst = new JPanel();
        //jpanelFirst.setLayout(new BorderLayout());
        jpanelFirst.setLayout(new FlowLayout(FlowLayout.LEFT));

        // jpanelFirst.setBorder(BorderFactory.createLineBorder(Color.red, 3) );
        setSelectCondition(jpanelFirst);

        dispa.addTab("显示", null, jpanelFirst, "选择当前显示的任务");
        left.add(dispa, BorderLayout.CENTER);
        //左下tab
        JTabbedPane calenlistPa = new JTabbedPane(JTabbedPane.TOP);

        JPanel jpanelcall = new JPanel(new BorderLayout());
        //jpanelcall.setLayout(new BorderLayout());
        call = new CalList();
        call.setDicSer(dicSer);
        call.setCalServ(calServ);
        call.initCalList();

        jpanelcall.add(call.getCalCompont());

        Dimension dim = jpanelcall.getPreferredSize();
        jpanelcall.setPreferredSize(new java.awt.Dimension(dim.width, 230));

        calenlistPa.addTab("日历", null, jpanelcall, "选择当前显示的日历");
        left.add(calenlistPa, BorderLayout.SOUTH);
    }
    private ButtonGroup conditions;

    private void setSelectCondition(JPanel com) {
        if (conditions == null) {
            conditions = new ButtonGroup();
            List<DictionaryData> calList = dicSer.getDictList("task_cond");
            for (DictionaryData da : calList) {
                ValueRadioButton<String> conOne = new ValueRadioButton<>(da.getDictdataValue(), da.getCode());
                conOne.setOpaque(false);
                conOne.addActionListener((e) -> selectTask(e));
                com.add(conOne);
                conditions.add(conOne);
            }
        }

    }

    public List<String> getSelectCal() {
        return call.getSelectCans();
    }
    public List<String> getListSelectCal(final boolean checkb) {
        return call.getSelectCans(checkb);
    }
    private void selectTask(ActionEvent e) {
        ValueRadioButton<String> vb = (ValueRadioButton<String>) e.getSource();
        String cond = MiscUtil.evaluateExpr(vb.getValue());
        String word = taskCondi.getText();
        log.info(cond);
        List<TaskData> tasks = this.taskSer.queryTask(cond, call.getSelectCans(), word);

        taskDataModel.setDatas(tasks);
    }
    public void delSelectCal() {
        call.deleteCal();
    }
    private void createCenter() {
        center = new JPanel(new BorderLayout());

        JPanel condition = new JPanel(new FlowLayout(FlowLayout.LEFT));
        taskCondi = new JTextField();
        taskCondi.setColumns(117);
        condition.add(taskCondi);
        JButton findButton = new JButton("查询");
        condition.add(findButton);
        findButton.addActionListener((ActionEvent e) -> {
            String word = taskCondi.getText();
            if (UtilValidate.isEmpty(word)) {
                this.reload();
            } else {
                List<TaskData> tasks = queryTask(word);
                taskDataModel.setDatas(tasks);
            }

        });

        JPanel centerData = new JPanel(new BorderLayout());

        centerData.add(createTable(), BorderLayout.CENTER);

        center.add(condition, BorderLayout.NORTH);
        center.add(centerData, BorderLayout.CENTER);

    }

    /**
     * 按照条件查询
     *
     * @param word
     * @return
     */
    public List<TaskData> queryTask(String word) {
        List<String> st = taskSer.getNotcompleteStatus();
        List<TaskData> tasks = this.taskSer.queryTask(st, -1, -1, call.getSelectCans(), word, -1);
        return tasks;
    }

    private void createRight() {
        right = new EventPanel(es, dicSer);
        right.setSelectCan(call.getSelectCans());
        right.intData();

        //right.add(dataShow, BorderLayout.CENTER);
    }

    public void reload() {
        List<String> st = taskSer.getNotcompleteStatus();
        List<TaskData> tasks = this.taskSer.queryTask(st, -1, -1, call.getSelectCans(), null, -1);
        taskDataModel.setDatas(tasks);
    }

    class TaskModel implements TableModel {
        //名称

        private final Class[] coluClass;
        //数据
        private List<TaskData> datas;

        public TaskModel(Class[] coluClass, List<TaskData> datas) {
            this.datas = datas;
            this.coluClass = coluClass;
        }

        void setDatas(List<TaskData> datas) {
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
            TaskData t = datas.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return t.getTaskid();
                case 1:
                    return t.getTitle();

                case 2:
                    return t.getPercentage();

                case 3:
                    String st = t.getTstatus();
                    return dicSer.getDictValue("task_status", st);

                case 4:
                    Long sdt = t.getStartTime();
                    if (sdt == null) {
                        return "";
                    }
                    return UtilDateTime.longToString(sdt, UtilDateTime.defaultDatePattern);

                case 5:
                    Long edt = t.getEndTime();
                    if (edt == null) {
                        return "";
                    }
                    return UtilDateTime.longToString(edt, UtilDateTime.defaultDatePattern);

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

    public void initUi(Rectangle scbounds) {
        createLeft();

        createCenter();
        createRight();

        // f.add(left,BorderLayout.WEST);
        //
        // f.add(center,BorderLayout.CENTER);
        //
        // f.add(right,BorderLayout.EAST);
        // 最右区域距离左边距离
        int leftWidth = (int) (scbounds.width * 0.85);

        splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, left, center);

        int llWidth = (int) (leftWidth * 0.15);
        splitLeft.setDividerLocation(llWidth);
        splitLeft.setOneTouchExpandable(false);
        splitLeft.setDividerSize(3);// 设置分隔线宽度的大小，以pixel为计算单位。

        // f.add(splitLeft);
        splitright = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, splitLeft, right);

        splitright.setDividerLocation(leftWidth);
        splitright.setOneTouchExpandable(true);
        splitright.setDividerSize(8);// 设置分隔线宽度的大小，以pixel为计算单位。
    }
    private TaskService taskSer;

    public TaskService getTaskSer() {
        return taskSer;
    }

    public void setTaskSer(TaskService taskSer) {
        this.taskSer = taskSer;
    }

    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }
    private CalendarService calServ;

    public CalendarService getCalServ() {
        return calServ;
    }

    public void setCalServ(CalendarService calServ) {
        this.calServ = calServ;
    }    
    private EventService es;

    public EventService getEs() {
        return es;
    }

    public void setEs(EventService es) {
        this.es = es;
    }

    private JScrollPane createTable() {
        Class[] coluClass = {String.class, String.class, Integer.class, String.class, String.class, String.class};
        List<String> st = taskSer.getNotcompleteStatus();
        List<TaskData> tasks = this.taskSer.queryTask(st, -1, -1, call.getSelectCans(), null, -1);
        log.debug("任务数量{}", tasks.size());
        taskDataModel = new TaskModel(coluClass, tasks);
        taskTable = new JTable(taskDataModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 表格选择为单选  
        taskTable.setRowHeight(25); // 设置行高  
        taskTable.setShowVerticalLines(false);// 使表格的列线条不显示  

        // 设置表格排序，表格默认将所有值都作为字符串进行排序，  
        // 所以对于表格中已有字符型列可以不用自定义排序类  
        // 但需要对非字符串的列进行自定义排序  
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(taskDataModel);
        sorter.setComparator(2, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        taskTable.setRowSorter(sorter);
        // 设置各列的呈现方式  
        MiscUtil.HiddenColumn(taskTable, 0);

        TableColumn tem = taskTable.getColumn("标题");
        LabelColumn lc = new LabelColumn();
        tem.setCellEditor(lc);
        tem.setCellRenderer(lc);

        tem = taskTable.getColumn("%完成");
        ProgressColumn pc = new ProgressColumn(100);
        tem.setCellEditor(pc);
        tem.setCellRenderer(pc);

        tem = taskTable.getColumn("状态");
        LabelColumn llc = new LabelColumn();
        tem.setCellEditor(llc);
        tem.setCellRenderer(llc);

        tem = taskTable.getColumn("开始日期");
        LabelColumn dl = new LabelColumn(SwingConstants.LEFT);
        tem.setCellEditor(dl);
        tem.setCellRenderer(dl);

        tem = taskTable.getColumn("结束日期");
        LabelColumn del = new LabelColumn(SwingConstants.CENTER);
        tem.setCellEditor(del);
        tem.setCellRenderer(del);

        taskTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {//点击几次，这里是双击事件
                    editTask();
                }
            }
        });

        return new JScrollPane(taskTable);
    }

    public void editTask() {
        int row = taskTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, " 没有选择任务", "没有选择任务", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String preId = taskTable.getValueAt(row, 0).toString();  //得到所在行的第一个列的值，作为下面事件
        CalTaskUi evu = new CalTaskUi(MiscUtil.getComJFrame(center), true, 750, 850, preId);
        evu.setTaskSe(taskSer);
        evu.setDicSer(dicSer);

        evu.initTaskUi(Calendar.getInstance());

        Tuple2<TaskData, Integer> newData = evu.getData();

        if (!newData.e2.equals(3)) {
            reload();
        }

        evu.dispose();
    }

    public int delTask() {
        int row = taskTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, " 没有选择任务", "没有选择任务", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        String taskid = taskTable.getValueAt(row, 0).toString();
        int result = taskSer.delTask(taskid);
        if (result == 1) {
            reload();
        }
        return result;
    }

    public void restArea(Rectangle scbounds) {
        // 最右区域距离左边距离
        int leftWidth = (int) (scbounds.width * 0.85);

        int llWidth = (int) (leftWidth * 0.15);

        splitLeft.setDividerLocation(llWidth);

        splitright.setDividerLocation(leftWidth);
    }

}
