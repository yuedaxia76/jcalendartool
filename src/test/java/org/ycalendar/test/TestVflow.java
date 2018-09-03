package org.ycalendar.test;

 
import javax.swing.JButton;
import javax.swing.JFrame;

import org.ycalendar.ui.VFlowLayout;

public class TestVflow {
	public static void main(String[] args)
	{
		System.out.println("Just for test ...");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 600, 600);
		frame.setLayout(new VFlowLayout());
		
		int i = 0;
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		frame.add(new JButton(String.valueOf(i++)));
		
		frame.setVisible(true);
	}
}
