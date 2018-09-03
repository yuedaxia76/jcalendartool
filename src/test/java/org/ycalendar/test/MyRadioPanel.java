package org.ycalendar.test;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MyRadioPanel extends JPanel {
	private JRadioButton[] buttons = null;

	public MyRadioPanel(String[] strButtonText) {
		buttons=new JRadioButton[strButtonText.length];

		for (int i = 0; i < strButtonText.length; i++) {

			buttons[i] = new JRadioButton(strButtonText[i]);

			add(buttons[i]);
		}

	}

	/**
	 * 
	 * get button groups.
	 * 
	 */

	public JRadioButton[] getButtons() {

		return buttons;

	}

	/**
	 * 
	 * set which index select.
	 * 
	 */

	public void setSelectedIndex(int index) {

		for (int i = 0; i < buttons.length; i++) {

			buttons[i].setSelected(i == index);

		}

	}
	
	
	public int getSelectedIndex() {
		for (int i = 0; i < buttons.length; i++) {

			if (buttons[i].isSelected()) {
				return i;
			}

		}
		return -1;
	}
}