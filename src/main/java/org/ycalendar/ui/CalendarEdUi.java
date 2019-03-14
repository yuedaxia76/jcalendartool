/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.ycalendar.dbp.service.CalendarService;

/**
 * 日历编辑
 *
 * @author lenovo
 */
public class CalendarEdUi extends JDialog {

    JTextField calShowName;

    JTextField calId;
    private CalendarService calServ;

    public CalendarService getCalServ() {
        return calServ;
    }

    public void setCalServ(CalendarService calServ) {
        this.calServ = calServ;
    }
    private String caldarid;
    private JButton ok;

    public CalendarEdUi(Frame parent, boolean modal, int windowWidth, int windowHeight, String calId) {
        super(parent, modal);
        this.caldarid = calId;
        setLayout(new GridLayout(3, 2));

        setSize(windowWidth, windowHeight);
    }

    public void iniUi() {
        add(new JLabel("名称："));
        add(calShowName);
        add(new JLabel("ID："));
        add(calId);

        ok = new JButton("button1");

        add(ok);
    }

}
