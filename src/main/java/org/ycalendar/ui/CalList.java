/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.uiutil.ValueJCheckBoxButton;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilValidate;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

/**
 * 日历列表，暂时采用jlist
 *
 * @author lenovo
 */
public class CalList {

    private static final Logger log = LoggerFactory.getLogger(CalList.class);
    //日历选择

    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }

    public CalList() {
    }

    public void initCalList() {

        getCalJlist(false);

        MessageFac.getMemoryMsg().subMsg("CalCreateNew", (m) -> {
            log.info("new cal create ,reload");
            getCalJlist(true);
        });
        MessageFac.getMemoryMsg().subMsg("CalDelete", (m) -> {
            log.info("del cal   ,reload");
            getCalJlist(true);
        });
    }

    JList<ValueJCheckBoxButton<ItemData<String, String>>> calJlist;

    /**
     *
     * @param checkSelect 是否检测checkbox选择状态
     * @return 已经选择的日历
     */
    protected List<String> getSelectCans(final boolean checkSelect) {
        List<ValueJCheckBoxButton<ItemData<String, String>>> ses = calJlist.getSelectedValuesList();
        List<String> result = new ArrayList<>();
        if (UtilValidate.isNotEmpty(ses)) {
            for (ValueJCheckBoxButton<ItemData<String, String>> c : ses) {
                if (checkSelect) {
                    if (c.isSelected()) {
                        result.add(c.getValue().e1);
                    }
                } else {
                    result.add(c.getValue().e1);
                }

            }
        }

        return result;
    }

    /**
     * 获取选择日历，只要checkbox选中即可
     *
     * @return
     */
    protected List<String> getSelectCans() {
        ListModel<ValueJCheckBoxButton<ItemData<String, String>>> ses = calJlist.getModel();
        List<String> result = new ArrayList<>(ses.getSize());

        for (int i = 0; i < ses.getSize(); i++) {
            ValueJCheckBoxButton<ItemData<String, String>> c = ses.getElementAt(i);
            if (c.isSelected()) {
                result.add(c.getValue().e1);
            }

        }

        return result;
    }

    /**
     * 获取目前选中日历，只要checkbox选中即可
     *
     * @return
     */
    protected List<ItemData<String, String>> getSelectCansItem() {

        ListModel<ValueJCheckBoxButton<ItemData<String, String>>> ses = calJlist.getModel();
        List<ItemData<String, String>> result = new ArrayList<>(ses.getSize());

        for (int i = 0; i < ses.getSize(); i++) {
            ValueJCheckBoxButton<ItemData<String, String>> c = ses.getElementAt(i);
            if (c.isSelected()) {
                result.add(c.getValue());
            }

        }

        return result;

    }

    protected JComponent getCalCompont() {
        //calJlist.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        return calJlist;
    }

    protected JList<ValueJCheckBoxButton<ItemData<String, String>>> getCalJlist(final boolean reloadData) {
        if (calJlist == null) {
            log.info("create cal reloadData :{}", reloadData);
            Tuple2<DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>>, int[]> calsinfo = getCalendarlist();
            DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>> sm = calsinfo.e1;
            calJlist = new JList<ValueJCheckBoxButton<ItemData<String, String>>>(sm);
            calJlist.setCellRenderer(new CellRenderer());

            calJlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            calJlist.setSelectedIndices(calsinfo.e2);

            calJlist.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    //log.warn("com class :{}",event.getSource().getClass());
                    JList list = (JList) event.getSource();
                    int index = list.locationToIndex(event.getPoint());// Get index of item
                    // clicked
                    ValueJCheckBoxButton<ItemData<String, String>> item = (ValueJCheckBoxButton<ItemData<String, String>>) list.getModel()
                            .getElementAt(index);
                    item.setSelected(!item.isSelected()); // Toggle selected state
                    list.repaint(list.getCellBounds(index, index));// Repaint cell
                }
            });

            //变化发布事件,TODO:修改
            calJlist.addListSelectionListener((ListSelectionEvent e) -> {
                MemMsg m = new MemMsg("SelectCalChange");
                m.setProperty("changeInfo", e);

                List<ItemData<String, String>> ses = getSelectCansItem();
                m.setProperty("selectedItem", ses);
                log.info("select cal change to :{}", ses);
                MessageFac.getMemoryMsg().sendMsg(m);
            });

        } else if (reloadData) {
            DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>> lm = (DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>>) calJlist.getModel();
            lm.removeAllElements();
            Tuple2<DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>>, int[]> calsinfo = getCalendarlist();
            DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>> sm = calsinfo.e1;
            for (int i = 0; i < sm.size(); i++) {
                lm.add(i, sm.get(i));
            }
            calJlist.setSelectedIndices(calsinfo.e2);
        }
        return calJlist;

    }
    /**
     * 在数据库读取日历，应考虑通过CalendarService读取
     * @return 
     */
    private Tuple2<DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>>, int[]> getCalendarlist() {
        DefaultListModel<ValueJCheckBoxButton<ItemData<String, String>>> listModel = new DefaultListModel<>();
        List<DictionaryData> calListdr = dicSer.getDictList("calendar");
        int[] index = new int[calListdr.size()];
        for (int i = 0; i < calListdr.size(); i++) {
            DictionaryData da = calListdr.get(i);
            ItemData<String, String> id = new ItemData<>(da.getCode(), da.getDictdataValue());
            log.debug("cal index {} text :{}", i, id.e2);
            listModel.addElement(new ValueJCheckBoxButton<>(id.e2, id, true));
            index[i] = i;
        }

        return new Tuple2(listModel, index);
    }

    protected class CellRenderer implements ListCellRenderer<JCheckBox> {

        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = value;

            //Drawing checkbox, change the appearance here
            checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            checkbox.setEnabled(list.isEnabled());
            checkbox.setFont(list.getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            //checkbox.setSelected(isSelected);
//            checkbox.setBorder(isSelected ? UIManager
//                    .getBorder("List.focusCellHighlightBorder") : noFocusBorder);
//            String x = checkbox.toString();
//            log.info("cal index {} text :{}", index, x);
//            checkbox.setText(x);
            return checkbox;
        }
    }
}
