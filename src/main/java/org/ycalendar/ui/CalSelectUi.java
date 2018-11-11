/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.domain.DictionaryData;

/**
 * 日历选择
 *
 * @author lenovo
 */
public class CalSelectUi extends JDialog {
    
    JList<ItemData<String, String>> calJlist;
    
    JButton jB_Ok;
    JButton jB_cacel;
    
    private Dictionary dicSer;
    
    public Dictionary getDicSer() {
        return dicSer;
    }
    
    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }
    
    public CalSelectUi(Frame parent, boolean modal, int windowWidth, int windowHeight) {
        super(parent, modal);
        
        setLayout(new BorderLayout());
        
        setSize(windowWidth, windowHeight);
        
    }
    
    public void initCalSelectUi(String title) {
        
        setResizable(false);
        super.setTitle(title);

        // JPanel center = new JPanel(new GridLayout(7, 2));
        JPanel center = new JPanel(new BorderLayout());
        // JPanel center = new JPanel();
        // BoxLayout lo = new BoxLayout(center, BoxLayout.Y_AXIS);
        // center.setLayout(lo);

        add(center, BorderLayout.CENTER);
        // Dimension td=titleText.getPreferredSize();
        //
        // titleText.setPreferredSize(preferredSize);
        center.add(new JLabel("日历:"));
        
        center.add(getCalJlist());
        
        jB_Ok = new JButton("确定");
        
        jB_cacel = new JButton("取消");
        
        JPanel south = new JPanel(new GridLayout(2, 2, 4, 4));
        
        JPanel lb = new JPanel(new BorderLayout());
        
        lb.add(jB_Ok, BorderLayout.EAST);
        south.add(lb);
        
        JPanel rb = new JPanel(new BorderLayout());
        
        rb.add(jB_cacel, BorderLayout.WEST);
        
        jB_Ok.addActionListener(e -> ok());
        jB_cacel.addActionListener(e -> cacel());
        
        south.add(rb);
        
        south.add(new JPanel());
        south.add(new JPanel());

        // south.add(new JPanel( ), BorderLayout.SOUTH );
        add(south, BorderLayout.SOUTH);
        
        super.setLocationRelativeTo(null); //使窗体居中显示
        super.setVisible(true);
    }
    
    private void ok() {
        super.setVisible(false);
    }
    
    private void cacel() {
        getCalJlist().clearSelection();
        super.setVisible(false);
    }
    
    public String getSelectCans() {
        ItemData<String, String> result = getCalJlist().getSelectedValue();
        if (result == null) {
            return null;
        }
        return result.e1;
    }
    //private void select

    private JList<ItemData<String, String>> getCalJlist() {
        if (calJlist == null) {
            DefaultListModel<ItemData<String, String>> sm = getCalendarlist();
            calJlist = new JList<ItemData<String, String>>(sm);
            calJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return calJlist;
        
    }
    
    private DefaultListModel<ItemData<String, String>> getCalendarlist() {
        DefaultListModel<ItemData<String, String>> listModel = new DefaultListModel<ItemData<String, String>>();
        List<DictionaryData> calList = dicSer.getDictList("calendar");
        for (DictionaryData da : calList) {
            listModel.addElement(new ItemData<String, String>(da.getCode(), da.getDictdataValue()));
        }
        return listModel;
    }
}
