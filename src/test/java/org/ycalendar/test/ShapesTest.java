/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.test;

import java.awt.BasicStroke;
import java.awt.Graphics;

//Fig. 5.27: ShapesTest.java, modified by pandenghuang@163.com
//Test application that displays class Shapes.
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Graphics; //handle the display
import java.awt.Graphics2D;

/**
 *
 * @author lenovo
 */
public class ShapesTest {

    public static class Shapes extends JPanel {

        private int choice; // user's choice of which shape to draw

        // constructor sets the user's choice
        public Shapes(int userChoice) {
            choice = userChoice;
        }

        // draws a cascade of shapes starting from the top-left corner
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth(); // total width   
            int height = getHeight(); // total height
            int barHorizontalDistance = width / 11;
            int barVerticalDistance = height / 11;
            Graphics2D g2 = (Graphics2D) g;  //g是Graphics对象
            g2.setStroke(new BasicStroke(3.0f));

            for (int i = 0; i < 10; i++) {
                // pick the shape based on the user's choice
                switch (choice) {
                    case 1: // draw rectangles
                        g.drawRect(10 + i * 10, 10 + i * 10,
                                50 + i * 10, 50 + i * 10);
                        break;
                    case 2: // draw ovals
                        g.drawOval(10 + i * 10, 10 + i * 10,
                                50 + i * 10, 50 + i * 10);
                        break;
                    case 3: // draw concentric circles
                        g.drawOval(width / 2 - (i + 1) * 10, height / 2 - (i + 1) * 10,
                                10 + 20 * i, 10 + 20 * i);
                        break;
                    case 4: // draw bar chart
                        g.drawRect(i * barHorizontalDistance, height - i * barVerticalDistance,
                                20, height + i * height / 11);
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        // obtain user's choice
        String input = JOptionPane.showInputDialog(
                "输入1画长方形\n"
                + "输入2画圆形\n"
                + "输入3画同心圆\n"
                + "输入4画条形图");

        int choice = Integer.parseInt(input); // convert input to int

        // create the panel with the user's input
        Shapes panel = new Shapes(choice);

        JFrame application = new JFrame(); // creates a new JFrame

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.add(panel);
        application.setSize(300, 300);
        application.setVisible(true);
    }
}
