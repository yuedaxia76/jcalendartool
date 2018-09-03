/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui.jdatepicker.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 *
 * @author lenovo
 */
public class JtodayIcon implements Icon {

    private int width;
    private int height;

    private boolean enabled;

    public JtodayIcon(int width, int height, boolean enabled) {
        setDimension(width, height);

        this.enabled = enabled;
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (enabled) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.GRAY);
        }
        Graphics2D g2 = (Graphics2D) g;  //g是Graphics对象
        g2.setStroke(new BasicStroke(2.0f));
        g.drawOval(width / 2, height / 2, 9, 9);

    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

}
