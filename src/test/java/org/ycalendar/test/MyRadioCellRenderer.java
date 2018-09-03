package org.ycalendar.test;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.ycalendar.util.UtilValidate;

public class MyRadioCellRenderer extends MyRadioPanel implements TableCellRenderer {

	public MyRadioCellRenderer(String[] strButtonTexts) {

		super(strButtonTexts);

	}

	@Override

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if ((value instanceof Integer) ) {

			setSelectedIndex(((Integer) value).intValue());

		}else if(value!=null && UtilValidate.isNumber(value.toString())) {
			setSelectedIndex((Integer.valueOf(value.toString())).intValue());
		}
		else {
			System.out.println("错误数据"+value);
		}

		if(isSelected) {
			table.editCellAt(row, column)	;
		}
		return this;

	}

}
