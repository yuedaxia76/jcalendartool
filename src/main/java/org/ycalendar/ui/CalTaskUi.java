/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.domain.TaskData;
import org.ycalendar.ui.jdatepicker.DateModel;
import org.ycalendar.ui.jdatepicker.DatePicker;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.ui.jdatepicker.UtilDateModel;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilValidate;

/**
 * 任务编辑界面
 *
 * @author lenovo
 */
public class CalTaskUi extends JDialog {

    public static final Logger log = Logger.getLogger(CalTaskUi.class.getName());
    private JToolBar myJToolBar;

    private JTextField titleText;

    private JComboBox<ItemData<String, String>> typeJlist;

    private JComboBox<ItemData<String, String>> calJlist;

    private DatePicker<Date> startPicker;

    private DatePicker<Date> endPicker;

    //状态
    private JComboBox<ItemData<String, String>> taskstatus;

    //完成百分比
    private JSpinner percentageSpinner;

    // 提醒时间，单位分钟
    private JComboBox<ItemData<String, String>> remindTime;

    private JTextArea taskDesc;

    private String taskId;

    private TaskData data;
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
     * @return Tuple2<TaskData,Integer> e1事件对象 e2 状态
     */
    public Tuple2<TaskData, Integer> getData() {
        return new Tuple2<>(data, getAcType());
    }

    /**
     * 编辑数据
     *
     * @param parent
     * @param modal
     * @param windowWidth
     * @param windowHeight
     * @param taskId
     */
    public CalTaskUi(Frame parent, boolean modal, int windowWidth, int windowHeight, String taskId) {
        super(parent, modal);
        this.taskId = taskId;
        setLayout(new BorderLayout());

        setSize(windowWidth, windowHeight);

    }

    private void loadData() {
        if (UtilValidate.isNotEmpty(taskId)) {
            data = taskSe.readTask(taskId);
            if (data == null) {
                log.log(Level.WARNING, "taskid {0} no data", taskId);
                super.setTitle("新建任务");
            } else {
                super.setTitle("编辑任务:" + data.getTitle());
            }
        } else {
            super.setTitle("新建任务");
        }

    }

    public void initTaskUi() {
        initTaskUi(null);
    }

    public void initTaskUi(Calendar defaultDate) {

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
        center.add(getTaskCategory());

        center.add(new JLabel("日历:"));
        center.add(getCalenList());

        center.add(new JLabel("开始:"));
        center.add((JComponent) getStartPicker(defaultDate));

        center.add(new JLabel("结束:"));
        center.add((JComponent) getEndPicker(defaultDate));
        //状态，百分比
        center.add(new JLabel("任务状态:"));
        center.add(getStatus());
        center.add(getTaskPercentage());
        center.add(new JLabel("%完成  "));

        center.add(new JLabel("提醒:"));
        center.add(getRemindTime());

        center.add(new JLabel("描述:"));
        center.add(getTaskDesc());

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

    private JComboBox<ItemData<String, String>> getTaskCategory() {
        if (typeJlist == null) {
            List<DictionaryData> typeLi = dicSer.getDictList("event_cate");

            int selectIndex = 0;

            Vector<ItemData<String, String>> typeModel = new Vector<ItemData<String, String>>(typeLi.size());
            for (int i = 0; i < typeLi.size(); i++) {
                DictionaryData dd = typeLi.get(i);
                typeModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && data.getCategory().equals(dd.getCode())) {

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

    private JComboBox<ItemData<String, String>> getStatus() {
        if (taskstatus == null) {
            List<DictionaryData> typeLi = dicSer.getDictList("task_status");

            int selectIndex = 0;

            Vector<ItemData<String, String>> typeModel = new Vector<>(typeLi.size());
            for (int i = 0; i < typeLi.size(); i++) {
                DictionaryData dd = typeLi.get(i);
                typeModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && data.getTstatus().equals(dd.getCode())) {

                    selectIndex = i;

                }
            }

            taskstatus = new JComboBox<>(typeModel);
            Dimension preferredSize = taskstatus.getPreferredSize();
            preferredSize.setSize(240, preferredSize.getHeight());
            taskstatus.setPreferredSize(preferredSize);

            taskstatus.setSelectedIndex(selectIndex);
            taskstatus.addActionListener((ActionEvent e) -> {
                int seIndex= taskstatus.getSelectedIndex();
                if(seIndex==-1){
                return;
                        }
                String newStatus=taskstatus.getItemAt(seIndex).e1;
                switch (newStatus){
                    case "process":
                        getTaskPercentage().setEnabled(true);
                        break;
                       case "unprocessed":
                           getTaskPercentage().setEnabled(false);
                        break;    
                       case "complete":
                            getTaskPercentage().setEnabled(true);
                            getTaskPercentage().setValue(100);
                        break;  
                       case "cancel":
                           getTaskPercentage().setEnabled(false);
                        break;                            
                }
            });

        }

        return taskstatus;
    }
    private JSpinner getTaskPercentage() {
        if (percentageSpinner == null) {
            percentageSpinner = new javax.swing.JSpinner();
            SpinnerModel model;

            if (data != null) {
                model = new SpinnerNumberModel(data.getPercentage(), 0, 100, 1);

            } else {
                model = new SpinnerNumberModel(0, 0, 100, 1);
            }
            percentageSpinner.setModel(model);

            Dimension preferredSize = percentageSpinner.getPreferredSize();
            preferredSize.setSize(150, preferredSize.getHeight());
            percentageSpinner.setPreferredSize(preferredSize);
            ItemData<String, String> tsu=getStatus().getItemAt(getStatus().getSelectedIndex());
            percentageSpinner.setEnabled("process".equals(tsu.e1) || "complete".equals(tsu.e1));
 
        }
        return percentageSpinner;
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

            startPicker = new JDatePicker<>(new JDatePanel<Date>(stime), false);
            startPicker.setTextEditable(true);
            startPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) startPicker).getPreferredSize();
            preferredSize.setSize(320, preferredSize.getHeight());
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
                    stime = new UtilDateModel(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000l));
                } else {
                    stime = new UtilDateModel(new Date(defaultDate.getTimeInMillis() + 24 * 60 * 60 * 1000l));
                }

            }

            endPicker = new JDatePicker<>(new JDatePanel<Date>(stime), false);
            endPicker.setTextEditable(true);
            endPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) endPicker).getPreferredSize();
            preferredSize.setSize(320, preferredSize.getHeight());
            ((JComponent) endPicker).setPreferredSize(preferredSize);

        }

        return endPicker;
    }

    private JComboBox<ItemData<String, String>> getRemindTime() {
        if (remindTime == null) {
            List<DictionaryData> remDic = dicSer.getDictList("remind_time");

            Vector<ItemData<String, String>> remindModel = new Vector<>(remDic.size());

            int selectIndex = 0;

            for (int i = 0; i < remDic.size(); i++) {
                DictionaryData dd = remDic.get(i);
                remindModel.insertElementAt(new ItemData<String, String>(dd.getCode(), dd.getDictdataValue()), i);
                if (this.data != null && dd.getCode().equals(data.getRemind())) {

                    selectIndex = i;

                }
            }

            remindTime = new JComboBox<ItemData<String, String>>(remindModel);

            Dimension preferredSize = remindTime.getPreferredSize();
            preferredSize.setSize(190, preferredSize.getHeight());
            remindTime.setPreferredSize(preferredSize);
            remindTime.setSelectedIndex(selectIndex);
        }

        return remindTime;
    }

    private JTextArea getTaskDesc() {
        if (taskDesc == null) {
            taskDesc = new JTextArea(40, 60);
            taskDesc.setLineWrap(true);
            taskDesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            if (this.data != null && UtilValidate.isNotEmpty(data.getTaskdesc())) {
                taskDesc.setText(data.getTaskdesc());
            }
        }
        return taskDesc;
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
                saveTask();

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
                delTask();

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
                cacelTask();

            }

        });
    }

    void delTask() {
        acType = 2;
        if (data != null) {
            taskSe.delTask(data.getTaskid());
        }
        super.setVisible(false);
        userClose = true;
    }

    void cacelTask() {
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

    private TaskService taskSe;

    public TaskService getTaskSe() {
        return taskSe;
    }

    public void setTaskSe(TaskService evSe) {
        this.taskSe = evSe;
    }

    void saveTask() {
        if (data == null) {
            data = new TaskData();
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
        ItemData<String, String> taskType = typeJlist.getItemAt(typeJlist.getSelectedIndex());
        data.setCategory(taskType.e1);

        ItemData<String, String> calen = calJlist.getItemAt(calJlist.getSelectedIndex());
        data.setCalendarid(calen.e1);

        Date st = (Date) startPicker.getModel().getValue();
        data.setStartTime(st.getTime());

        Date et = (Date) endPicker.getModel().getValue();
        data.setEndTime(et.getTime());

        ItemData<String, String> remTi = remindTime.getItemAt(remindTime.getSelectedIndex());
        data.setRemind(remTi.e1);

        ItemData<String, String> ts = taskstatus.getItemAt(taskstatus.getSelectedIndex());

        data.setTstatus(ts.e1);
        if (getTaskPercentage().isEnabled()) {
            int p = (Integer) percentageSpinner.getValue();
            data.setPercentage(p);
        }else{
            data.setPercentage(0);
        }

        data.setTaskdesc(taskDesc.getText());

        taskSe.saveOrUpdate(data);
        this.taskId = data.getTaskid();
        super.setVisible(false);
        userClose = true;
        //super.dispose();
    }



}
