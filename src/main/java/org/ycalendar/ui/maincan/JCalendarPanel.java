package org.ycalendar.ui.maincan;

import java.awt.Color;
import java.awt.event.FocusListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.ycalendar.dbp.service.Dictionary;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.domain.EventData;
import org.ycalendar.ui.EventUi;
import org.ycalendar.ui.ItemData;
import org.ycalendar.ui.VFlowLayout;
import org.ycalendar.ui.jdatepicker.ComponentColorDefaults;
import org.ycalendar.ui.jdatepicker.ComponentIconDefaults;
import org.ycalendar.ui.jdatepicker.ComponentTextDefaults;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.Tuple2;
import org.ycalendar.util.UtilValidate;

/**
 * 带有表格的日历，考虑支持周显示
 *
 * @author lenovo
 *
 */
public class JCalendarPanel extends JComponent {

    public static final Logger log = Logger.getLogger(JCalendarPanel.class.getName());
    private static final long serialVersionUID = -2299239311312882915L;

    private final int firstDayOfWeek;

    private final InternalCalendarModel internalModel;
    private final InternalController internalController;
    private final InternalView internalView;

    private final EventService es;
    // 需要显示的日历
    private List<String> calendarids;

    public List<String> getCalendarids() {
        return calendarids;
    }

    public void setCalendarids(List<String> calendarids) {
        this.calendarids = calendarids;
        // 日历变化，重新装入数据
        reload();
    }

    public void reload() {
        internalModel.stateChanged(new ChangeEvent(this));
    }

//	private List<EventData> getEventBydate(Calendar date) {
//		if (UtilValidate.isEmpty(monthEvents)) {
//			return null;
//		}
//		List<EventData> result = new ArrayList<>();
//		int y = date.get(Calendar.YEAR);
//		int m = date.get(Calendar.MONTH);
//		int d = date.get(Calendar.DAY_OF_MONTH);
//
//		for (EventData e : monthEvents) {
//			long stl = e.getStartTime();
//
//			Calendar st = Calendar.getInstance();
//			st.setTimeInMillis(stl);
//			if (st.get(Calendar.YEAR) == y && st.get(Calendar.MONTH) == m && st.get(Calendar.DAY_OF_MONTH) == d) {
//
//				result.add(e);
//			}
//
//		}
//		return result;
//	}
    /**
     *
     * @param model	* 日期
     * @param calendarids	日历
     * @param es	服务
     */
    public JCalendarPanel(MainDateModel model, List<String> calendarids, EventService es) {
        this.calendarids = calendarids;
        this.es = es;

        firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();

        loadEvent(model);
        internalModel = new InternalCalendarModel(model);

        internalController = new InternalController();
        internalView = new InternalView();

        setLayout(new GridLayout(1, 1));
        add(internalView);

    }
    private Dictionary dicSer;

    public Dictionary getDicSer() {
        return dicSer;
    }

    public void setDicSer(Dictionary dicSer) {
        this.dicSer = dicSer;
    }
    //装入事件数据

    public void loadEvent(MainDateModel model) {

        Calendar s = model.getCalendar(0, 0).dateCan;
        Calendar e = model.getCalendar(5, 6).dateCan;
        e.set(Calendar.HOUR_OF_DAY, 23);
        e.set(Calendar.MINUTE, 59);
        e.set(Calendar.SECOND, 59);
        List<EventData> monthEvents = es.readEvent(s.getTimeInMillis(), e.getTimeInMillis(), calendarids, null);
        for (EventData et : monthEvents) {
            model.addEvent(et);
        }

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDateComponent#getModel()
     */
    public MainDateModel getModel() {
        return internalModel.getModel();
    }

    public EventData getSelectEventData() {
        int y = internalView.dayTable.getSelectedColumn();
        if (y == -1) {
            log.log(Level.WARNING, "no select cell");
            return null;
        }
        int x = internalView.dayTable.getSelectedRow();
        CellObject curObj = this.getModel().getCalendar(x, y);
        EventData ed = curObj.getSelectEvent();
        if (ed == null) {
            log.log(Level.WARNING, "no event select cell Row:" + x + " Column:" + y);
            return null;
        }
        return ed;
    }

    /**
     * 删除选择的事件
     */
    public boolean delSelectEvent() {
        int y = internalView.dayTable.getSelectedColumn();
        if (y == -1) {
            log.log(Level.WARNING, "no select cell");
            return false;
        }
        int x = internalView.dayTable.getSelectedRow();
        CellObject curObj = this.getModel().getCalendar(x, y);
        EventData ed = curObj.getSelectEvent();
        if (ed == null) {
            log.log(Level.WARNING, "no event select cell Row:" + x + " Column:" + y);
            return false;
        }

        es.delEvent(ed.getEventid());
        curObj.setSelectEvent(null);
        Tuple2<EventData, Integer> dataResult = new Tuple2<>(ed, 2);
        refreshData(dataResult);
        return true;
    }

    public void refreshData(Tuple2<EventData, Integer> newData) {
        int col = internalView.dayTable.getSelectedColumn();
        if (col == -1) {
            log.log(Level.WARNING, "no Column select");
            return;
        }
        int row = internalView.dayTable.getSelectedRow();
        CellObject editData = (CellObject) internalView.dayTable.getValueAt(row, col);

        switch (newData.e2) {

            case 3:
                //取消
                break;
            case 2:
                //删除
                editData.del(newData.e1);

                break;
            case 0:
                //insert
                editData.insert(newData.e1);

                break;
            case 1:
                //update
                editData.update(newData.e1);

                break;
        }

        //internalView.dayTable.setValueAt(editData, row, col);
        //internalView.dayTable.setModel(internalModel);
        //internalView.dayTable.updateUI();
        //internalView.dayTable.editingStopped(new TableModelEvent(internalModel, row, row, col));
        internalView.dayTable.editingStopped(new ChangeEvent(internalModel));
    }

    public Calendar getSelectData() {
        if (internalView.dayTable.getSelectedColumn() == -1) {
            log.log(Level.WARNING, "no select date");
            return null;
        }
        int x = internalView.dayTable.getSelectedRow();
        int y = internalView.dayTable.getSelectedColumn();
        return getModel().getCalendar(x, y).dateCan;
    }

    /**
     * 选定一个单元格
     */
    public boolean selectByDay(Calendar s) {
        MainDateModel mm = getModel();
        Tuple2<Integer, Integer> coordinate = mm.getIndex(s);
        if (coordinate.e1 == -1) {
            log.log(Level.FINE, "no Calendar{0} in curent model", s.toString());
            mm.setYear(s.get(Calendar.YEAR));
            mm.setMonth(s.get(Calendar.MONTH));

            coordinate = mm.getIndex(s);
            if (coordinate.e1 == -1) {
                log.log(Level.WARNING, "no Calendar{0} in curent model", s.toString());
                return false;
            }

        }
        internalView.dayTable.setColumnSelectionInterval(coordinate.e2, coordinate.e2);
        internalView.dayTable.setRowSelectionInterval(coordinate.e1, coordinate.e1);
        return true;
    }

    private static ComponentTextDefaults getTexts() {
        return ComponentTextDefaults.getInstance();
    }

    private static ComponentIconDefaults getIcons() {
        return ComponentIconDefaults.getInstance();
    }

    private static ComponentColorDefaults getColors() {
        return ComponentColorDefaults.getInstance();
    }

    @Override
    public void setEnabled(boolean enabled) {
        internalView.setEnabled(enabled);

        super.setEnabled(enabled);
    }

    /**
     * Logically grouping the view controls under this internal class.
     *
     * @author Juan Heyns
     */
    private class InternalView extends JPanel {

        private JPanel centerPanel;
        private JPanel northCenterPanel;
        private JPanel northPanel;
        private JPanel previousButtonPanel;
        private JPanel nextButtonPanel;
        private JTable dayTable;
        private JTableHeader dayTableHeader;

        private JLabel monthLabel;

        private JPopupMenu monthPopupMenu;
        private JMenuItem[] monthPopupMenuItems;
        private JButton nextMonthButton;
        private JButton previousMonthButton;
        private JButton previousYearButton;
        private JButton nextYearButton;
        private JSpinner yearSpinner;

        /**
         * Update the UI of the monthLabel
         */
        private void updateMonthLabel() {
            monthLabel.setText(getTexts().getText(ComponentTextDefaults.Key.getMonthKey(internalModel.getModel().getMonth())));
        }

        public InternalView() {
            initialise();
        }

        /**
         * Initialise the control.
         */
        private void initialise() {
            this.setLayout(new java.awt.BorderLayout());
            this.setPreferredSize(new Dimension(200, 180));
            this.setOpaque(false);
            this.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
            this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        }

        /**
         * This method initializes northPanel
         *
         * @return javax.swing.JPanel The north panel
         */
        private JPanel getNorthPanel() {
            if (northPanel == null) {
                northPanel = new javax.swing.JPanel();
                northPanel.setLayout(new java.awt.BorderLayout());
                northPanel.setName("");
                northPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
                // northPanel.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_MONTH_SELECTOR));
                northPanel.setBackground(Color.CYAN);
                northPanel.add(getPreviousButtonPanel(), java.awt.BorderLayout.WEST);
                northPanel.add(getNextButtonPanel(), java.awt.BorderLayout.EAST);
                northPanel.add(getNorthCenterPanel(), java.awt.BorderLayout.CENTER);
            }
            return northPanel;
        }

        /**
         * This method initializes northCenterPanel
         *
         * @return javax.swing.JPanel
         */
        private JPanel getNorthCenterPanel() {
            if (northCenterPanel == null) {
                northCenterPanel = new javax.swing.JPanel();
                northCenterPanel.setLayout(new java.awt.BorderLayout());
                northCenterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
                northCenterPanel.setOpaque(false);
                northCenterPanel.add(getMonthLabel(), java.awt.BorderLayout.CENTER);
                northCenterPanel.add(getYearSpinner(), java.awt.BorderLayout.EAST);
            }
            return northCenterPanel;
        }

        /**
         * This method initializes monthLabel
         *
         * @return javax.swing.JLabel
         */
        private JLabel getMonthLabel() {
            if (monthLabel == null) {
                monthLabel = new javax.swing.JLabel();
                monthLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_MONTH_SELECTOR));
                monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                monthLabel.addMouseListener(internalController);
                updateMonthLabel();
            }
            return monthLabel;
        }

        /**
         * This method initializes yearSpinner
         *
         * @return javax.swing.JSpinner
         */
        private JSpinner getYearSpinner() {
            if (yearSpinner == null) {
                yearSpinner = new javax.swing.JSpinner();
                yearSpinner.setModel(internalModel);
            }
            return yearSpinner;
        }

        /**
         * This method initializes centerPanel
         *
         * @return javax.swing.JPanel
         */
        private JPanel getCenterPanel() {
            if (centerPanel == null) {
                centerPanel = new javax.swing.JPanel();
                centerPanel.setLayout(new java.awt.BorderLayout());
                centerPanel.setOpaque(false);
                centerPanel.add(getDayTableHeader(), java.awt.BorderLayout.NORTH);
                centerPanel.add(getDayTable(), java.awt.BorderLayout.CENTER);
            }
            return centerPanel;
        }

        /**
         * This method initializes dayTable
         *
         * @return javax.swing.JTable
         */
        private JTable getDayTable() {
            if (dayTable == null) {
                dayTable = new javax.swing.JTable();
                dayTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
                dayTable.setModel(internalModel);
                dayTable.setShowGrid(true);
                dayTable.setGridColor(getColors().getColor(ComponentColorDefaults.Key.MG_GRID));
                // dayTable.setGridColor(Color.BLACK);

                dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                dayTable.setCellSelectionEnabled(true);
                dayTable.setRowSelectionAllowed(true);
                dayTable.setFocusable(false);

                dayTable.addMouseListener(internalController);
                for (int i = 0; i < 7; i++) {
                    TableColumn column = dayTable.getColumnModel().getColumn(i);
                    InternalTableCellRender rAnde = new InternalTableCellRender();
                    column.setCellRenderer(rAnde);
                    column.setCellEditor(rAnde);

                }
                dayTable.addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        // The new size of the table
                        final double w = e.getComponent().getSize().getWidth();
                        final double h = e.getComponent().getSize().getHeight();

                        // Set the size of the font as a fraction of the width
                        // or the height, whichever is smallest
                        final float sw = (float) Math.floor(w / 16);
                        final float sh = (float) Math.floor(h / 8);
                        dayTable.setFont(dayTable.getFont().deriveFont(Math.min(sw, sh)));

                        // Set the row height as a fraction of the height
                        final int r = (int) Math.floor(h / 6);
                        dayTable.setRowHeight(r);
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
            }
            return dayTable;
        }

        private JTableHeader getDayTableHeader() {
            if (dayTableHeader == null) {
                dayTableHeader = getDayTable().getTableHeader();
                dayTableHeader.setResizingAllowed(false);
                dayTableHeader.setReorderingAllowed(false);
                //dayTableHeader.setDefaultRenderer(new HeadCellRenderer());
            }
            return dayTableHeader;
        }

        /**
         * This method initializes previousButtonPanel
         *
         * @return javax.swing.JPanel
         */
        private JPanel getPreviousButtonPanel() {
            if (previousButtonPanel == null) {
                previousButtonPanel = new javax.swing.JPanel();
                java.awt.GridLayout layout = new java.awt.GridLayout(1, 2);
                layout.setHgap(3);
                previousButtonPanel.setLayout(layout);
                previousButtonPanel.setName("");
                previousButtonPanel.setOpaque(false);

                previousButtonPanel.add(getPreviousYearButton());

                previousButtonPanel.add(getPreviousMonthButton());
            }
            return previousButtonPanel;
        }

        /**
         * This method initializes nextButtonPanel
         *
         * @return javax.swing.JPanel
         */
        private JPanel getNextButtonPanel() {
            if (nextButtonPanel == null) {
                nextButtonPanel = new javax.swing.JPanel();
                java.awt.GridLayout layout = new java.awt.GridLayout(1, 2);
                layout.setHgap(3);
                nextButtonPanel.setLayout(layout);
                nextButtonPanel.setName("");
                nextButtonPanel.setOpaque(false);
                nextButtonPanel.add(getNextMonthButton());

                nextButtonPanel.add(getNextYearButton());

            }
            return nextButtonPanel;
        }

        /**
         * This method initializes nextMonthButton
         *
         * @return javax.swing.JButton
         */
        private JButton getNextMonthButton() {
            if (nextMonthButton == null) {
                nextMonthButton = new JButton();
                nextMonthButton.setIcon(getIcons().getNextMonthIconEnabled());
                nextMonthButton.setDisabledIcon(getIcons().getNextMonthIconDisabled());
                nextMonthButton.setText("");
                nextMonthButton.setPreferredSize(new java.awt.Dimension(20, 15));
                nextMonthButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                nextMonthButton.setFocusable(false);
                nextMonthButton.setOpaque(true);
                nextMonthButton.addActionListener(internalController);
                nextMonthButton.setToolTipText(getTexts().getText(ComponentTextDefaults.Key.MONTH));
            }
            return nextMonthButton;
        }

        /**
         * This method initializes nextYearButton
         *
         * @return javax.swing.JButton
         */
        private JButton getNextYearButton() {
            if (nextYearButton == null) {
                nextYearButton = new JButton();
                nextYearButton.setIcon(getIcons().getNextYearIconEnabled());
                nextYearButton.setDisabledIcon(getIcons().getNextYearIconDisabled());
                nextYearButton.setText("");
                nextYearButton.setPreferredSize(new java.awt.Dimension(20, 15));
                nextYearButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                nextYearButton.setFocusable(false);
                nextYearButton.setOpaque(true);
                nextYearButton.addActionListener(internalController);
                nextYearButton.setToolTipText(getTexts().getText(ComponentTextDefaults.Key.YEAR));
            }
            return nextYearButton;
        }

        /**
         * This method initializes previousMonthButton
         *
         * @return javax.swing.JButton
         */
        private JButton getPreviousMonthButton() {
            if (previousMonthButton == null) {
                previousMonthButton = new JButton();
                previousMonthButton.setIcon(getIcons().getPreviousMonthIconEnabled());
                previousMonthButton.setDisabledIcon(getIcons().getPreviousMonthIconDisabled());
                previousMonthButton.setText("");
                previousMonthButton.setPreferredSize(new java.awt.Dimension(20, 15));
                previousMonthButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                previousMonthButton.setFocusable(false);
                previousMonthButton.setOpaque(true);
                previousMonthButton.addActionListener(internalController);
                previousMonthButton.setToolTipText(getTexts().getText(ComponentTextDefaults.Key.MONTH));
            }
            return previousMonthButton;
        }

        /**
         * This method initializes previousMonthButton
         *
         * @return javax.swing.JButton
         */
        private JButton getPreviousYearButton() {
            if (previousYearButton == null) {
                previousYearButton = new JButton();
                previousYearButton.setIcon(getIcons().getPreviousYearIconEnabled());
                previousYearButton.setDisabledIcon(getIcons().getPreviousYearIconDisabled());
                previousYearButton.setText("");
                previousYearButton.setPreferredSize(new java.awt.Dimension(20, 15));
                previousYearButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                previousYearButton.setFocusable(false);
                previousYearButton.setOpaque(true);
                previousYearButton.addActionListener(internalController);
                previousYearButton.setToolTipText(getTexts().getText(ComponentTextDefaults.Key.YEAR));
            }
            return previousYearButton;
        }

        /**
         * This method initializes monthPopupMenu
         *
         * @return javax.swing.JPopupMenu
         */
        private JPopupMenu getMonthPopupMenu() {
            if (monthPopupMenu == null) {
                monthPopupMenu = new javax.swing.JPopupMenu();
                JMenuItem[] menuItems = getMonthPopupMenuItems();
                for (int i = 0; i < menuItems.length; i++) {
                    monthPopupMenu.add(menuItems[i]);
                }
            }
            return monthPopupMenu;
        }

        private JMenuItem[] getMonthPopupMenuItems() {
            if (monthPopupMenuItems == null) {
                monthPopupMenuItems = new JMenuItem[12];
                for (int i = 0; i < 12; i++) {
                    JMenuItem mi = new JMenuItem(getTexts().getText(ComponentTextDefaults.Key.getMonthKey(i)));
                    mi.addActionListener(internalController);
                    monthPopupMenuItems[i] = mi;
                }
            }
            return monthPopupMenuItems;
        }

        @Override
        public void setEnabled(boolean enabled) {
            dayTable.setEnabled(enabled);

            nextMonthButton.setEnabled(enabled);
            if (nextYearButton != null) {
                nextYearButton.setEnabled(enabled);
            }
            previousMonthButton.setEnabled(enabled);
            if (previousYearButton != null) {
                previousYearButton.setEnabled(enabled);
            }
            yearSpinner.setEnabled(enabled);

            super.setEnabled(enabled);
        }
    }

//	private class HeadCellRenderer  extends DefaultTableCellRenderer {
//
//		private static final long serialVersionUID = -2341614459632756921L;
//
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//
//			if (value == null) {
//				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			}
//
//			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			label.setHorizontalAlignment(JLabel.CENTER);
//
//			label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_HEADER));
//			label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_HEADER));
//
//			return label;
//
//		}
//
//	}
    private void setSelectEvent(EventData selectEvent) {
        int y = internalView.dayTable.getSelectedColumn();
        if (y == -1) {
            log.log(Level.WARNING, "no select cell");
            return;
        }
        int x = internalView.dayTable.getSelectedRow();

        //System.out.println("设置event x:"+x+" y:"+y+" selectEvent "+selectEvent);
        CellObject curObj = this.getModel().getCalendar(x, y);
        curObj.setSelectEvent(selectEvent);
    }

    /**
     * This inner class renders the table of the days, setting colors based on
     * whether it is in the month, if it is today, if it is selected etc.
     *
     * @author Juan Heyns
     */
    private class InternalTableCellRender extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        CellObject editData;

        private void editEvent(EventData ed) {
            EventUi evu = new EventUi(MiscUtil.getComJFrame(JCalendarPanel.this), true, 750, 850, ed.getEventid());
            evu.setEvSe(es);
            evu.setDicSer(dicSer);
            evu.initEventUi(null);

            Tuple2<EventData, Integer> newData = evu.getData();

            switch (newData.e2) {

                case 3:
                    //取消
                    break;
                case 2:
                    //删除
                    editData.del(newData.e1);
                    fireEditingStopped();
                    break;
                case 0:
                    //insert
                    editData.insert(newData.e1);
                    fireEditingStopped();
                    break;
                case 1:
                    //update
                    editData.update(newData.e1);
                    fireEditingStopped();
                    break;
            }

            evu.dispose();
        }

        private void toCellPanal(JPanel result, CellObject cellObj) {
            //result.addMouseListener(internalController);

            List<EventData> dayEvents = cellObj.dayEvents;

            if (UtilValidate.isNotEmpty(dayEvents)) {

                DefaultListModel<ItemData<EventData, String>> listModel = new DefaultListModel<ItemData<EventData, String>>();

                for (EventData e : dayEvents) {
                    listModel.addElement(new ItemData<>(e, e.getTitle()));
                }

                JList<ItemData<EventData, String>> calJlist = new JList<ItemData<EventData, String>>(listModel);
                calJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                calJlist.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2) {
                            //System.out.println("2次");
                            JList<ItemData<EventData, String>> myList = (JList<ItemData<EventData, String>>) e.getSource();
                            int index = myList.getSelectedIndex(); // 已选项的下标

                            ItemData<EventData, String> obj = myList.getModel().getElementAt(index); // 取出数据
                            //System.out.println(obj.e1.getTitle() + obj.e1.getEventid());
                            editData = cellObj;

                            editEvent(obj.e1);

                        } else if (e.getClickCount() == 1) {
                            //System.out.println("1次");
                            JList<ItemData<EventData, String>> myList = (JList<ItemData<EventData, String>>) e.getSource();
                            int index = myList.getSelectedIndex(); // 已选项的下标
                            if (index == -1) {
                                setSelectEvent(null);
                            } else {
                                ItemData<EventData, String> obj = myList.getModel().getElementAt(index); // 取出数据
                                setSelectEvent(obj.e1);
                            }

                        }
                    }

                });
                calJlist.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        //System.out.println("获取焦点"); 
                        JList<ItemData<EventData, String>> myList = (JList<ItemData<EventData, String>>) e.getSource();
                        int index = myList.getSelectedIndex(); // 已选项的下标
                        if (index == -1) {
                            setSelectEvent(null);
                        } else {
                            ItemData<EventData, String> obj = myList.getModel().getElementAt(index); // 取出数据
                            setSelectEvent(obj.e1);
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        //System.out.println("失去焦点");
                        JList<ItemData<EventData, String>> myList = (JList<ItemData<EventData, String>>) e.getSource();
                        int index = myList.getSelectedIndex();
                        if (index == -1) {
                            setSelectEvent(null);
                        }

                    }
                });
                result.add(calJlist);

            }

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //System.out.println("getTableCellRendererComponent  isSelected="+isSelected+" row= "+row+ " column= "+column);
            JPanel result = new JPanel(new VFlowLayout());
            result.setOpaque(true);

            if (value == null) {
                return result;
            }
            JLabel label = new JLabel();
            label.setOpaque(true);

            //label.setFont(new java.awt.Font("楷体", 0, 16));
            label.setHorizontalAlignment(JLabel.RIGHT);
            result.add(label);

            CellObject cellObj = (CellObject) value;
            // 日期
            Calendar cur = cellObj.dateCan;
            // 当前月
            if (internalModel.getModel().getCanType(cur) == 0) {
                label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_THIS_MONTH));

                Calendar todayCal = Calendar.getInstance();

                label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID));

                // Today
                if (todayCal.get(Calendar.DATE) == cur.get(Calendar.DATE) && todayCal.get(Calendar.MONTH) == cur.get(Calendar.MONTH) && todayCal.get(Calendar.YEAR) == cur.get(Calendar.YEAR)) {
                    label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_TODAY));
                    // Selected
                    if (isSelected) {
                        label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_TODAY_SELECTED));
                        label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_TODAY_SELECTED));
                    }
                } // Other day
                else {
                    // Selected
                    if (isSelected) {
                        label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_SELECTED));
                        label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_SELECTED));
                    }
                }
                label.setText(String.valueOf(cur.get(Calendar.DATE)));
            } else {
                if (isSelected) {
                    label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_SELECTED));
                    label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_SELECTED));
                } else {
                    label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_OTHER_MONTH));

                    label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID));
                }

                label.setText(MiscUtil.toLocalMonth(cur.get(Calendar.MONTH)) + String.valueOf(String.valueOf(cur.get(Calendar.DATE))));

            }

            toCellPanal(result, cellObj);
            return result;

        }

        @Override
        public Object getCellEditorValue() {
            Object result = editData;
            editData = null;
            return result;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            //System.out.println("getTableCellEditorComponent  isSelected="+isSelected+" row= "+row+ " column= "+column);
            return getTableCellRendererComponent(table, value, true, true, row, column);

        }

    }

    /**
     * This inner class hides the public view event handling methods from the
     * outside. This class acts as an internal controller for this component. It
     * receives events from the view components and updates the model.
     *
     * @author Juan Heyns
     */
    private class InternalController implements ActionListener, MouseListener {

        /**
         * 月、年变化
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (!JCalendarPanel.this.isEnabled()) {
                return;
            }

            if (arg0.getSource() == internalView.getNextMonthButton()) {
                internalModel.getModel().addMonth(1);
            } else if (arg0.getSource() == internalView.getPreviousMonthButton()) {
                internalModel.getModel().addMonth(-1);
            } else if (arg0.getSource() == internalView.getNextYearButton()) {
                internalModel.getModel().addYear(1);
            } else if (arg0.getSource() == internalView.getPreviousYearButton()) {
                internalModel.getModel().addYear(-1);
            } else {
                for (int month = 0; month < internalView.getMonthPopupMenuItems().length; month++) {
                    if (arg0.getSource() == internalView.getMonthPopupMenuItems()[month]) {
                        internalModel.getModel().setMonth(month);
                    }
                }
            }

        }

        /**
         * Mouse down on monthLabel pops up a table. Mouse down on todayLabel
         * sets the value of the internal model to today. Mouse down on day
         * table will set the day to the value. Mouse down on none label will
         * clear the date. 给model赋值事件
         */
        @Override
        public void mousePressed(MouseEvent arg0) {

            if (!JCalendarPanel.this.isEnabled()) {
                return;
            }

            if (arg0.getSource() == internalView.getMonthLabel()) {
                internalView.getMonthPopupMenu().setLightWeightPopupEnabled(false);
                internalView.getMonthPopupMenu().show((Component) arg0.getSource(), arg0.getX(), arg0.getY());
            } else if (arg0.getSource() == internalView.getDayTable()) {
                int row = internalView.getDayTable().getSelectedRow();
                int col = internalView.getDayTable().getSelectedColumn();

                if (row >= 0 && row <= 5) {

                    internalModel.getModel().setSelected(row, col);

                    if (arg0.getClickCount() == 2) {

                        EventUi evu = new EventUi(MiscUtil.getComJFrame(JCalendarPanel.this), true, 750, 850, null);
                        evu.setEvSe(es);
                        evu.setDicSer(dicSer);
                        Calendar curDay = getSelectData();
                        if (curDay == null) {
                            JOptionPane.showMessageDialog(null, " 没有选择日期", "没有选择日期", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        evu.initEventUi(curDay);

                        Tuple2<EventData, Integer> newData = evu.getData();

                        refreshData(newData);

                        evu.dispose();
                    }

                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
        }

    }

    /**
     * This model represents the selected date. The model implements the
     * TableModel interface for displaying days, and it implements the
     * SpinnerModel for the year.
     *
     * @author Juan Heyns
     */
    protected class InternalCalendarModel implements TableModel, SpinnerModel, ChangeListener {

        private final MainDateModel model;
        private final Set<ChangeListener> spinnerChangeListeners;
        private final Set<TableModelListener> tableModelListeners;

        public InternalCalendarModel(MainDateModel model) {
            this.spinnerChangeListeners = new HashSet<>();
            this.tableModelListeners = new HashSet<>();

            this.model = model;
            model.addChangeListener(this);
        }

        public MainDateModel getModel() {
            return model;
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public void addChangeListener(ChangeListener arg0) {
            spinnerChangeListeners.add(arg0);
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public void removeChangeListener(ChangeListener arg0) {
            spinnerChangeListeners.remove(arg0);
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public Object getNextValue() {
            return Integer.toString(model.getYear() + 1);
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public Object getPreviousValue() {
            return Integer.toString(model.getYear() - 1);
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public void setValue(Object text) {
            String year = (String) text;
            model.setYear(Integer.parseInt(year));
        }

        /**
         * Part of SpinnerModel, year
         */
        @Override
        public Object getValue() {
            return Integer.toString(model.getYear());
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public void addTableModelListener(TableModelListener arg0) {
            tableModelListeners.add(arg0);
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public void removeTableModelListener(TableModelListener arg0) {
            tableModelListeners.remove(arg0);
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public int getColumnCount() {
            return 7;
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public int getRowCount() {
            return 6;
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public String getColumnName(int columnIndex) {
            ComponentTextDefaults.Key key = ComponentTextDefaults.Key.getDowKey(((firstDayOfWeek - 1) + columnIndex) % 7);
            return getTexts().getText(key);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            return model.getCalendar(rowIndex, columnIndex);

        }

        /**
         * Part of TableModel, day
         *
         * @param arg0
         */
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Class getColumnClass(int arg0) {
            return CellObject.class;
        }

        /**
         * Part of TableModel, day
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            return true;

        }

        /**
         * Part of TableModel, day
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (value == null) {
                return;
            }
            if (value instanceof CellObject) {
                model.setCalendar(row, col, (CellObject) value);
            } else {
                model.addEvent(row, col, (EventData) value);
            }

            fireCellValueChange(row, col);
        }

        private void fireCellValueChange(int row, int column) {

            for (TableModelListener tl : tableModelListeners) {
                //System.out.println("单元格变化"+tl.getClass().getName());
                tl.tableChanged(new TableModelEvent(this, row, row, column));
            }

        }

        /**
         * Called whenever a change is made to the model value. Notify the
         * internal listeners and update the simple controls. Also notifies the
         * (external) ChangeListeners of the component, since the internal state
         * has changed.
         */
        private void fireValueChanged() {
            // Update year spinner
            for (ChangeListener cl : spinnerChangeListeners) {
                cl.stateChanged(new ChangeEvent(this));
            }

            // Update month label
            internalView.updateMonthLabel();

            // Update day table
            for (TableModelListener tl : tableModelListeners) {

                tl.tableChanged(new TableModelEvent(this));
            }
        }

        /**
         * The model has changed and needs to notify the InternalModel.
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            fireValueChanged();
            loadEvent(internalModel.getModel());
        }

    }

}
