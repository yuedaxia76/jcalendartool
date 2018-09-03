package org.ycalendar.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class LabelColumn extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private Font font;
    private Color fontColor;
    private JLabel lblRender;

    public LabelColumn(Font font, Color fontColor, int alignment) {
        lblRender = new JLabel();
        lblRender.setOpaque(true);

        if (font != null) {
            this.font = font;
        }
        if (fontColor != null) {
            this.fontColor = fontColor;
        }
        if (alignment != -1) {
            lblRender.setHorizontalAlignment(alignment);
        }
    }

    public LabelColumn(Font font) {
        this(font, null, SwingConstants.LEFT);
    }

    public LabelColumn(Color fontColor) {
        this(null, fontColor, SwingConstants.LEFT);
    }

    public LabelColumn(int alignment) {
        this(null, null, alignment);
    }

    public LabelColumn() {
        this(null, null, SwingConstants.LEFT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        lblRender.setText(value != null ? value.toString() : "");
        lblRender.setFont(this.font == null ? table.getFont() : this.font);
        lblRender.setForeground(this.fontColor == null ? table.getForeground() : this.fontColor);
        lblRender.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return lblRender;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return null;
    }

    @Override
    public Object getCellEditorValue() {

        return null;
    }
}
