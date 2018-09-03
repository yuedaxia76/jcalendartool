package org.ycalendar.ui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


public class ProgressColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JProgressBar pbRender;

    public ProgressColumn( int maximum) {
        pbRender = new JProgressBar();
        pbRender.setMaximum(maximum);
        pbRender.setMinimum(0);
        pbRender.setBorder(null);

    }

    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        pbRender.setString(value.toString());
        pbRender.setStringPainted(true);
        pbRender.setValue(Integer.parseInt(value.toString()));
        return pbRender;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return null;
    }
}
