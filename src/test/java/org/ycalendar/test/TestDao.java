package org.ycalendar.test;

import java.awt.FlowLayout;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ycalendar.dbp.dao.H2Dao;

public class TestDao {

    public static void main(String[] args) {

        //ddssd();
       // eee();
       
       ee11e();
    }

    public static void ee11e() {
        Testb Test = new Testb();
        Test.setVisible(true);
    }

    public static void eee() {
        System.out.println(Locale.getDefault().toString());
    }

    public static void ddssd() {
        H2Dao hd = H2Dao.getH2Dao();

        System.out.println(hd.getDbUrl());
    }

    public static void ddd() {
        StringBuilder one = new StringBuilder();
        one.append("aaab;");

        if (one.indexOf(";", one.length() - 1) > -1) {

            one.deleteCharAt(one.length() - 1);

            System.out.println(one.toString());

        }

        one.setLength(0);

        one.append("aaab;");
        int s = one.indexOf("b;");
        one.delete(s, s + "b;".length());
        System.out.println(one.toString());

    }

    public static class Testb extends JFrame {

        public Testb() {
            setSize(500, 600);
            setLocationRelativeTo(null); //使窗体居中显示
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new FlowLayout());
            setTitle("用户信息");

            String str[] = {"属性", "信息"};
            Object str2[][] = {{"姓名", ""}, {"职工号", ""}, {"身份证号", ""}, {"性别", ""}, {"出生年月", ""}};
            JTable table = new JTable(str2, str);
            JButton Button1 = new JButton("修改信息");
            JButton Button2 = new JButton("修改密码");
            JLabel Label = new JLabel("用户:", JLabel.CENTER);
            JScrollPane scrollpane = new JScrollPane(table);

            Box box = Box.createVerticalBox();
            Box buttonBox = Box.createHorizontalBox();

            buttonBox.add(Button1);
            buttonBox.add(new JPanel());
            buttonBox.add(Button2);

            box.add(scrollpane);
            box.add(Label);
            box.add(buttonBox);

            add(box);
        }

    }
}
