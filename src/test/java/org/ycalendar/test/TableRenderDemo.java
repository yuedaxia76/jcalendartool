package org.ycalendar.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class TableRenderDemo extends JPanel {

	private boolean DEBUG = false;

	public TableRenderDemo() {
		super(new GridLayout(1, 0));

		JTable table = new JTable(new MyTableModel());
		
		//JTable table = new JTable();
		
		table.setPreferredScrollableViewportSize(new Dimension(500, 400));
		table.setFillsViewportHeight(true);
		table.setEditingColumn(1);
		table.editCellAt(0, 1);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(true);
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		

		String[] answer = { "A", "B", "C" };

		table.getColumnModel().getColumn(1).setCellRenderer( new MyRadioCellRenderer(answer));

		table.getColumnModel().getColumn(1).setCellEditor( new MyRadioCellEditor(new MyRadioCellRenderer(answer)));

		table.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				// The new size of the table
				final double w = e.getComponent().getSize().getWidth();
				final double h = e.getComponent().getSize().getHeight();

				// Set the size of the font as a fraction of the width
				// or the height, whichever is smallest
				final float sw = (float) Math.floor(w / 16);
				final float sh = (float) Math.floor(h / 8);
				table.setFont(table.getFont().deriveFont(Math.min(sw, sh)));

				// Set the row height as a fraction of the height
				final int r = (int) Math.floor(h / 6);
				table.setRowHeight(r);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// Do nothing
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// Do nothing
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// Do nothing
			}

		});
		
		// Add the scroll pane to this panel.
		add(scrollPane);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUpSportColumn(JTable table, TableColumn sportColumn) {
		// Set up the editor for the sport cells.
//		JComboBox comboBox = new JComboBox();
//		comboBox.addItem("Snowboarding");
//		comboBox.addItem("Rowing");
//		comboBox.addItem("Knitting");
//		comboBox.addItem("Speed reading");
//		comboBox.addItem("Pool");
//		comboBox.addItem("None of the above");
//		sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

		// Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new CellRenderer();
		renderer.setToolTipText("Click for combo box");
		sportColumn.setCellRenderer(renderer);
	}
	private class CellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -2341614459632756921L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			JComboBox comboBox = new JComboBox();
			comboBox.addItem("Snowboarding");
			comboBox.addItem("Rowing");
			comboBox.addItem("Knitting");
			comboBox.addItem("Speed reading");
			comboBox.addItem("Pool");
			comboBox.addItem("None of the above");

			return comboBox;

		}

	}
	
	
	
	
	

	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };
		private Object[][] data = { { "Kathy", "1", "Snowboarding", new Integer(5), new Boolean(false) }, { "John", "2", "Rowing", new Integer(3), new Boolean(true) },
				{ "Sue", "3", "Knitting", new Integer(2), new Boolean(false) }, { "Jane", "4", "Speed reading", new Integer(20), new Boolean(true) },
				{ "Joe", "5", "Pool", new Integer(10), new Boolean(false) } };

		public final Object[] longValues = { "Jane", "Kathy", "None of the above", new Integer(20), Boolean.TRUE };

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for each
		 * cell. If we didn't implement this method, then the last column would contain
		 * text ("true"/"false"), rather than a check box.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col + " to " + value + " (an instance of " + value.getClass() + ")");
			}

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("TableRenderDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		TableRenderDemo newContentPane = new TableRenderDemo();
		newContentPane.setOpaque(true); // content panes must be opaque
		

		
		
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
