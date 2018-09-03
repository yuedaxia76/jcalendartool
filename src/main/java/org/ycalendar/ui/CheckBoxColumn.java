package org.ycalendar.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class CheckBoxColumn extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private final JCheckBox chkRender;
    private final JCheckBox chkEditor;
    private Object value;

    public CheckBoxColumn() {
        chkRender = new JCheckBox();
        chkEditor = new JCheckBox();
        chkEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        chkRender.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        chkRender.setSelected((Boolean) value);
        return chkRender;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        chkEditor.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        if (value != null) {
            Boolean res = (Boolean) value;
            res = !res;
            this.value = res;
            chkEditor.setSelected(res);
        }
        return chkEditor;
    }

    @Override
    public Object getCellEditorValue() {
        return this.value;
    }
}
