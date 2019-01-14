/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.ycalendar.ui.jdatepicker.DatePicker;
import org.ycalendar.ui.jdatepicker.JDatePanel;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.ui.jdatepicker.UtilDateModel;

/**
 * 事件查询界面
 *
 * @author lenovo
 */
public class EventFindUi extends JPanel {

    JPanel condition;
    DatePicker<Date> startPicker;

    DatePicker<Date> endPicker;

    JTextField taskCondi;

    public EventFindUi() {
        condition = new JPanel(new FlowLayout(FlowLayout.LEFT));

        condition.add(getStartPicker() );
        
        condition.add(getEndPicker() );
        
        taskCondi = new JTextField();
        taskCondi.setColumns(57);
        condition.add(taskCondi);
        JButton findButton = new JButton("查询");
        condition.add(findButton);
    }

    private JComponent getStartPicker() {
        if (startPicker == null) {

            startPicker = new JDatePicker<>(new JDatePanel<>(new UtilDateModel()), false);
            startPicker.setTextEditable(true);
            startPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) startPicker).getPreferredSize();
            preferredSize.setSize(320, preferredSize.getHeight());
            ((JComponent) startPicker).setPreferredSize(preferredSize);

        }

        return (JComponent)startPicker;
    }

    private JComponent getEndPicker() {
        if (endPicker == null) {

            endPicker = new JDatePicker<>(new JDatePanel<Date>(new UtilDateModel()), false);
            endPicker.setTextEditable(true);
            endPicker.setShowYearButtons(true);

            Dimension preferredSize = ((JComponent) endPicker).getPreferredSize();
            preferredSize.setSize(320, preferredSize.getHeight());
            ((JComponent) endPicker).setPreferredSize(preferredSize);
            
        }
        return (JComponent)endPicker;
    }
 }