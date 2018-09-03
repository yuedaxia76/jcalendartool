/**
 Copyright 2004 Juan Heyns. All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are
 permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of
 conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list
 of conditions and the following disclaimer in the documentation and/or other materials
 provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JUAN HEYNS OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those of the
 authors and should not be interpreted as representing official policies, either expressed
 or implied, of Juan Heyns.
 */
package org.ycalendar.ui.jdatepicker;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ycalendar.ui.jdatepicker.constraints.DateSelectionConstraint;

/**
 * Created on 25 Mar 2004 Refactored 21 Jun 2004 Refactored 14 May 2009
 * Refactored 16 April 2010 Updated 26 April 2010 Updated 10 August 2012 Updated
 * 6 Jun 2015
 *
 * @author Juan Heyns
 * @author JC Oosthuizen
 * @author Yue Huang
 */
public class JDatePicker<T> extends JComponent implements DatePicker<T> {

	private static final long serialVersionUID = 2814777654384974503L;

	private Popup popup;
	private JFormattedTextField formattedTextField;
	private JButton button;

	private JDatePanel<T> datePanel;

 

	/**
	 * Create a JDatePicker with a custom date model.
	 *
	 * @param model
	 *            a custom date model
	 */
	public JDatePicker(DateModel<T> model) {
		this(new JDatePanel<>(model));
	}

	/**
	 * Create a JDatePicker with a custom date model.
	 *
	 * @param model
	 *            a custom date model
	 */
	public JDatePicker(DateModel<T> model, boolean showTimePanel) {
		this(new JDatePanel<>(model), showTimePanel);
	}

	/**
	 * You are able to set the format of the date being displayed on the label.
	 * Formatting is described at:
	 *
	 * @param datePanel
	 *            The DatePanel to use
	 */
	public JDatePicker(JDatePanel<T> datePanel) {
		this(datePanel, false);
	}

	/**
	 * You are able to set the format of the date being displayed on the label.
	 * Formatting is described at:
	 *
	 * @param datePanel
	 *            The DatePanel to use
         * @param showTimePanel 是否显示时间
	 */
	public JDatePicker(JDatePanel<T> datePanel, boolean showTimePanel) {
		this.datePanel = datePanel;
		datePanel.setShowTimePanel(showTimePanel);
		// Initialise Variables
		popup = null;
		datePanel.setBorder(BorderFactory.createLineBorder(getColors().getColor(ComponentColorDefaults.Key.POPUP_BORDER)));
		InternalEventHandler internalEventHandler = new InternalEventHandler();

		// Create Layout
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		// Create and Add Components
		// Add and Configure TextField
		//System.out.println(":::show:" + datePanel.getShowTimePanel());
		formattedTextField = new JFormattedTextField(new DateComponentFormatter(showTimePanel));
		DateModel<?> model = datePanel.getModel();
		if(showTimePanel) {
			setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(),model.getHour(),model.getMinute(),model.getSecond(), model.isSelected());	
		}else {
			setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(), model.isSelected());	
		}
		
		
		formattedTextField.setEditable(false);

		layout.putConstraint(SpringLayout.WEST, formattedTextField, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, formattedTextField);

		// Add and Configure Button
		button = new JButton();
		button.setFocusable(true);
		ImageIcon icon = new ImageIcon(JDatePicker.class.getResource("datepicker.gif"));
		// Icon icon = ComponentIconDefaults.getInstance().getPopupButtonIcon();
		// button.setSize(icon.getImage().getWidth(null),
		// icon.getImage().getHeight(null));
		button.setPreferredSize(new Dimension(button.getWidth(), 22));
		// icon.setImage(icon.getImage().getScaledInstance(button.getWidth(),
		// 22, Image.SCALE_DEFAULT));
		button.setIcon(icon);
		button.setMargin(new Insets(0, 0, 0, 0));// 将边框外的上下左右空间设置为0
		button.setIconTextGap(0);// 将标签中显示的文本和图标之间的间隔量设置为0
		button.setBorderPainted(false);// 不打印边框
		button.setBorder(null);// 除去边框
		button.setText(null);// 除去按钮的默认名称
		button.setFocusPainted(false);// 除去焦点的框
		button.setContentAreaFilled(false);// 除去默认的背景填充

		// remove text
		button.setText("");

		add(button);
		// formattedTextField.add(button,BorderLayout.EAST);
		add(formattedTextField);

		layout.putConstraint(SpringLayout.WEST, button, 1, SpringLayout.EAST, formattedTextField);
		layout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, button);
		layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, button);

		// Do layout formatting
		int h = (int) button.getPreferredSize().getHeight();
		int w = (int) datePanel.getPreferredSize().getWidth();
		button.setPreferredSize(new Dimension(h, h));
		formattedTextField.setPreferredSize(new Dimension(w - h - 1, h));

		// Add event listeners
		addHierarchyBoundsListener(internalEventHandler);
		// TODO addAncestorListener(listener)
		button.addActionListener(internalEventHandler);
		formattedTextField.addPropertyChangeListener("value", internalEventHandler);
		datePanel.addActionListener(internalEventHandler);
		datePanel.getModel().addChangeListener(internalEventHandler);
		long eventMask = MouseEvent.MOUSE_PRESSED;
		Toolkit.getDefaultToolkit().addAWTEventListener(internalEventHandler, eventMask);
	}

	private static ComponentColorDefaults getColors() {
		return ComponentColorDefaults.getInstance();
	}

	public void addActionListener(ActionListener actionListener) {
		datePanel.addActionListener(actionListener);
	}

	public void removeActionListener(ActionListener actionListener) {
		datePanel.removeActionListener(actionListener);
	}

	public DateModel<T> getModel() {
		return datePanel.getModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDatePicker#setTextEditable(boolean)
	 */
	public void setTextEditable(boolean editable) {
		formattedTextField.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDatePicker#isTextEditable()
	 */
	public boolean isTextEditable() {
		return formattedTextField.isEditable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDatePicker#setButtonFocusable(boolean)
	 */
	public void setButtonFocusable(boolean focusable) {
		button.setFocusable(focusable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDatePicker#getButtonFocusable()
	 */
	public boolean getButtonFocusable() {
		return button.isFocusable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdatepicker.JDatePicker#getJDateInstantPanel()
	 */
	public DatePanel<T> getJDateInstantPanel() {
		return datePanel;
	}

	public boolean isShowTimePanel() {
		return true;
	}

	/**
	 * Called internally to popup the dates.
	 */
	private void showPopup() {
		if (popup == null) {
			PopupFactory fac = new PopupFactory();
			Point xy = getLocationOnScreen();
			datePanel.setVisible(true);
			popup = fac.getPopup(this, datePanel, (int) xy.getX(), (int) (xy.getY() + this.getHeight()));
			popup.show();
		}
	}

	/**
	 * Called internally to hide the popup dates.
	 */
	private void hidePopup() {
		if (popup != null) {
			popup.hide();
			popup = null;
		}
	}

	private Set<Component> getAllComponents(Component component) {
		Set<Component> children = new HashSet<Component>();
		children.add(component);
		if (component instanceof Container) {
			Container container = (Container) component;
			Component[] components = container.getComponents();
			for (int i = 0; i < components.length; i++) {
				children.addAll(getAllComponents(components[i]));
			}
		}
		return children;
	}

	public boolean isDoubleClickAction() {
		return datePanel.isDoubleClickAction();
	}

	public boolean isShowYearButtons() {
		return datePanel.isShowYearButtons();
	}

	public void setDoubleClickAction(boolean doubleClickAction) {
		datePanel.setDoubleClickAction(doubleClickAction);
	}

	public void setShowYearButtons(boolean showYearButtons) {
		datePanel.setShowYearButtons(showYearButtons);
	}

	public void setShowTimePanel(boolean showTimePanel) {
		datePanel.setShowTimePanel(showTimePanel);
	}

 

	private void setTextFieldValue(JFormattedTextField textField, int year, int month, int day, boolean isSelected) {
		if (!isSelected) {
			textField.setValue(null);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			textField.setValue(calendar);
		}
	}

	private void setTextFieldValue(JFormattedTextField textField, int year, int month, int day, int hour, int minute, int second, boolean isSelected) {
		if (!isSelected) {
			textField.setValue(null);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day, hour, minute, second);
			calendar.set(Calendar.MILLISECOND, 0);
			textField.setValue(calendar);
		}
	}

	public void addDateSelectionConstraint(DateSelectionConstraint constraint) {
		datePanel.addDateSelectionConstraint(constraint);
	}

	public void removeDateSelectionConstraint(DateSelectionConstraint constraint) {
		datePanel.removeDateSelectionConstraint(constraint);
	}

	public void removeAllDateSelectionConstraints() {
		datePanel.removeAllDateSelectionConstraints();
	}

	public Set<DateSelectionConstraint> getDateSelectionConstraints() {
		return datePanel.getDateSelectionConstraints();
	}

	public int getTextfieldColumns() {
		return formattedTextField.getColumns();
	}

	public void setTextfieldColumns(int columns) {
		formattedTextField.setColumns(columns);
	}

	@Override
	public void setVisible(boolean aFlag) {
		if (!aFlag) {
			hidePopup();
		}
		super.setVisible(aFlag);
	}

	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		datePanel.setEnabled(enabled);
		formattedTextField.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	/**
	 * 日期选择事件处理器
	 */
	private class InternalEventHandler implements ActionListener, HierarchyBoundsListener, ChangeListener, PropertyChangeListener, AWTEventListener {

		public void ancestorMoved(HierarchyEvent arg0) {
			hidePopup();
		}

		public void ancestorResized(HierarchyEvent arg0) {
			hidePopup();
		}
        @Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == button) {
				if (popup == null) {
					showPopup();
				} else {
					hidePopup();
				}
			} else if (arg0.getSource() == datePanel) {
				hidePopup();
			}
		}
        @Override
		public void stateChanged(ChangeEvent arg0) {
			if (arg0.getSource() == datePanel.getModel()) {
				DateModel<?> model = datePanel.getModel();
				// System.out.println("hour:" + model.getHour());
				// 显示了时分秒
				if (datePanel.getShowTimePanel()) {
					setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(), model.getHour(), model.getMinute(), model.getSecond(), model.isSelected());
				} else {
					setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(), model.isSelected());
				}
			}
		}
        @Override
		public void propertyChange(PropertyChangeEvent evt) {
			// Short circuit if the following cases are found
			if (evt.getOldValue() == null && evt.getNewValue() == null) {
				return;
			}
			if (evt.getOldValue() != null && evt.getOldValue().equals(evt.getNewValue())) {
				return;
			}
			if (!formattedTextField.isEditable()) {
				return;
			}

			// If the field is editable and we need to parse the date entered
			if (evt.getNewValue() != null) {
				Calendar value = (Calendar) evt.getNewValue();
				DateModel<Calendar> model = new UtilCalendarModel(value);
				// check constraints
				if (!datePanel.checkConstraints(model)) {
					// rollback
					formattedTextField.setValue(evt.getOldValue());
					return;
				}
				if (datePanel.getShowTimePanel()) {
					datePanel.getModel().setDate(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));	
					datePanel.getModel().setHour(value.get(Calendar.HOUR));
					datePanel.getModel().setMinute(value.get(Calendar.MINUTE));
					datePanel.getModel().setSecond(value.get(Calendar.SECOND));
				}else {
					datePanel.getModel().setDate(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));	
				}
				
				datePanel.getModel().setSelected(true);
			}

			// Clearing textfield will also fire change event
			if (evt.getNewValue() == null) {
				// Set model value unselected, this will fire an event
				getModel().setSelected(false);
			}
		}

		public void eventDispatched(AWTEvent event) {
			if (MouseEvent.MOUSE_CLICKED == event.getID() && event.getSource() != button) {
				Set<Component> components = getAllComponents(datePanel);
				boolean clickInPopup = false;
				for (Component component : components) {
					if (event.getSource() == component) {
						clickInPopup = true;
					}
				}
				if (!clickInPopup) {
					hidePopup();
				}
			}
		}

	}

}
