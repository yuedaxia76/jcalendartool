/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

    private JPanel topJPanel;
    private JPanel bottomJPanel;

    public CalendarEdUi(Frame parent, boolean modal, int windowWidth, int windowHeight, String calId) {
        super(parent, modal);
        this.caldarid = calId;
        //setLayout(new BorderLayout(3, 2));
        topJPanel = new JPanel();
        BoxLayout layout = new BoxLayout(topJPanel, BoxLayout.Y_AXIS);
        topJPanel.setLayout(layout);
        add(topJPanel, BorderLayout.NORTH);
        bottomJPanel = new JPanel();
        add(bottomJPanel, BorderLayout.SOUTH);
        setSize(windowWidth, windowHeight);
    }

    public void iniUi() {

        Box box1, box2, boxBase;
        boxBase = Box.createHorizontalBox();
        box1 = Box.createVerticalBox();

        box1.add(Box.createVerticalStrut(10));
        box1.add(new JLabel(" 名称："));
        box1.add(Box.createVerticalStrut(8));
        box1.add(new JLabel(" ID："));
        box1.add(Box.createVerticalStrut(8));

        box2 = Box.createVerticalBox();

        calShowName = new JTextField();
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
        box2.add(Box.createVerticalStrut(10));
        box2.add(calId);
        box2.add(Box.createVerticalStrut(8));
        box2.add(calShowName);
        box2.add(Box.createVerticalStrut(8));

        boxBase.add(box1);
        boxBase.add(Box.createHorizontalStrut(8));
        boxBase.add(box2);
        topJPanel.add(boxBase);

        ok = new JButton("确定");

        bottomJPanel.add(ok);
        ok.addActionListener((ActionEvent e) -> {

            createOrSave();
        });

        cacele = new JButton("取消");
        bottomJPanel.add(cacele);

        cacele.addActionListener((ActionEvent e) -> {

            cancel();
        });

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setLocation((screenWidth - this.getWidth()) / 2, (screenHeight - this.getHeight()) / 2);
        super.setVisible(true);
    }

    private void createOrSave() {
        MemMsg md;
        if (UtilValidate.isEmpty(caldarid)) {
            String temid = calId.getText();

            String name = calShowName.getText();
            if (UtilValidate.isEmpty(temid) || UtilValidate.isEmpty(name)) {

                JOptionPane.showMessageDialog(this, " 日历id名称不能为空", "缺少信息", JOptionPane.ERROR_MESSAGE);
                return;
            }
            caldarid = temid;
            calId.setEditable(false);
            calServ.createCalendar(caldarid, name);

            md = new MemMsg("CalCreateNew");
            md.setProperty("caldarid", caldarid);
            md.setProperty("name", name);
        } else {
            String name = calShowName.getText();
            if (UtilValidate.isEmpty(name)) {

                JOptionPane.showMessageDialog(this, " 日历名称不能为空", "缺少信息", JOptionPane.ERROR_MESSAGE);
                return;
            }

            calServ.renameCalendar(caldarid, name);
            md = new MemMsg("CalChange");
            md.setProperty("caldarid", caldarid);
            md.setProperty("name", name);
        }

        MessageFac.getMemoryMsg().sendMsg(md);
        super.setVisible(false);
    }

    private void cancel() {
        super.setVisible(false);
    }

}
