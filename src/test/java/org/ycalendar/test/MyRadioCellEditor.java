package org.ycalendar.test;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.ycalendar.util.UtilValidate;

public class MyRadioCellEditor extends AbstractCellEditor implements ItemListener ,TableCellEditor{
	MyRadioCellRenderer panel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyRadioCellEditor(MyRadioCellRenderer panel) {

		this.panel = panel;

		JRadioButton[] buttons = panel.getButtons();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].addItemListener(this);
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();

	}

 
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int row, int column) {

		if (value instanceof Integer) {

			panel.setSelectedIndex(((Integer) value).intValue());

		}else if(value!=null && UtilValidate.isNumber(value.toString())) {
			panel.setSelectedIndex((Integer.valueOf(value.toString())).intValue());
		}else {
			System.out.println("错误数据"+value);
		}
		System.out.println("执行getTableCellEditorComponent");
		return panel;

	}

	@Override

	public Object getCellEditorValue() {

		return new Integer(panel.getSelectedIndex());

	}
}
