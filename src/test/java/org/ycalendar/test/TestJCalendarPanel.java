package org.ycalendar.test;

import java.awt.BorderLayout;
import java.util.Calendar;


import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.ycalendar.ui.maincan.CalendarModel;
import org.ycalendar.ui.maincan.JCalendarPanel;

public class TestJCalendarPanel {

	public static void main(String[] args) {
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        BorderLayout fb = new BorderLayout();
        testFrame.setLayout(fb);
        
        testFrame.setSize(500, 500);
        
        JPanel jPanel = new JPanel(new BorderLayout());
        JCalendarPanel picker = new JCalendarPanel(new CalendarModel(Calendar.getInstance()), null,null );
 
        jPanel.add((JComponent) picker);
        


        testFrame.add(jPanel, BorderLayout.CENTER);
        testFrame.setVisible(true);

	}

}
