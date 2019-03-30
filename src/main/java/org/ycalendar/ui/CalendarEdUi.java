/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.dbp.service.CalendarService;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.util.UtilValidate;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MessageFac;

/**
 * 日历编辑
 *
 * @author lenovo
 */
public class CalendarEdUi extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(CalendarEdUi.class);
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
    private JButton cacele;

    public CalendarEdUi(Frame parent, boolean modal, int windowWidth, int windowHeight, String calId) {
        super(parent, modal);
        this.caldarid = calId;
        setLayout(new GridLayout(3, 2));

        setSize(windowWidth, windowHeight);
    }

    public void iniUi() {
        add(new JLabel("名称："));

        calShowName = new JTextField();
        add(calShowName);
        add(new JLabel("ID："));

        if (UtilValidate.isEmpty(caldarid)) {
            calId = new JTextField();
        } else {

            calId = new JTextField(caldarid);
            calId.setEditable(false);
            DictionaryData cal = calServ.getCalendar(caldarid);
            if (cal != null) {
                calShowName.setText(cal.getDictdataValue());
            } else {
                log.warn("caldarid {} no data founed", caldarid);
            }
        }

        add(calId);

        ok = new JButton("确定");

        add(ok);
        ok.addActionListener((ActionEvent e) -> {

            createOrSave();
        });

        cacele = new JButton("取消");
        add(cacele);

        cacele.addActionListener((ActionEvent e) -> {

            cancel();
        });
        super.setVisible(true);
    }

    private void createOrSave() {
        MemMsg md;
        if (UtilValidate.isEmpty(caldarid)) {
            caldarid = calId.getText();
            calId.setEditable(false);
            String name = calShowName.getText();
            calServ.createCalendar(caldarid, name);

            md = new MemMsg("CalCreateNew");
            md.setProperty("caldarid", caldarid);
            md.setProperty("name", name);
        } else {
            String name = calShowName.getText();
            calServ.renameCalendar(caldarid, name);
            md = new MemMsg("CalChange");
            md.setProperty("caldarid", caldarid);
            md.setProperty("name", name);
        }

        MessageFac.getMemoryMsg().sendMsg(md);
    }

    private void cancel() {
        super.setVisible(false);
    }

}
