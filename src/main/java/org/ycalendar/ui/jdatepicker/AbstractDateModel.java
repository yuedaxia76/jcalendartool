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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created 18 April 2010 Updated 26 April 2010
 *
 * @param <T>
 *            The type of this model (e.g. java.util.Date, java.util.Calendar)
 * @author Juan Heyns
 */
public abstract class AbstractDateModel<T> implements DateModel<T> {

	public static final String PROPERTY_YEAR = "year";
	public static final String PROPERTY_MONTH = "month";
	public static final String PROPERTY_DAY = "day";

	public static final String PROPERTY_HOUR = "hour";
	public static final String PROPERTY_MINUTE = "minute";
	public static final String PROPERTY_SECOND = "second";
	
	
	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_SELECTED = "selected";

	private boolean selected;
	private Calendar calendarValue;
	private Set<ChangeListener> changeListeners;
	private Set<PropertyChangeListener> propertyChangeListeners;

	protected AbstractDateModel() {
		changeListeners = new HashSet<ChangeListener>();
		propertyChangeListeners = new HashSet<PropertyChangeListener>();
		selected = false;
		calendarValue = Calendar.getInstance();
	}

	public synchronized void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	public synchronized void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	protected synchronized void fireChangeEvent() {
		for (ChangeListener changeListener : changeListeners) {
			changeListener.stateChanged(new ChangeEvent(this));
		}
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.remove(listener);
	}

	protected synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}

		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
		}
	}

	public int getDay() {
		return calendarValue.get(Calendar.DATE);
	}

	public int getMonth() {
		return calendarValue.get(Calendar.MONTH);
	}

	public int getYear() {
		return calendarValue.get(Calendar.YEAR);
	}

	public T getValue() {
		if (!selected) {
			return null;
		}
		return fromCalendar(calendarValue);
	}

	public int getHour() {
		return calendarValue.get(Calendar.HOUR_OF_DAY);
	}

	public void setHour(int hour) {
		int oldHoureValue = this.calendarValue.get(Calendar.HOUR);
		T oldValue = getValue();
		calendarValue.set(Calendar.HOUR_OF_DAY, hour);
		
		fireChangeEvent();
		firePropertyChange(PROPERTY_HOUR, oldHoureValue, this.calendarValue.get(Calendar.HOUR));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());		
	}

	public int getMinute() {
		return calendarValue.get(Calendar.MINUTE);
	}

	public void setMinute(int minute) {
		int oldMinValue = this.calendarValue.get(Calendar.MINUTE);
		T oldValue = getValue();
		calendarValue.set(Calendar.MINUTE, minute);

		fireChangeEvent();
		firePropertyChange(PROPERTY_MINUTE, oldMinValue, this.calendarValue.get(Calendar.MINUTE));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public int getSecond() {
		return calendarValue.get(Calendar.SECOND);
	}

	public void setSecond(int second) {
		int oldSecValue = this.calendarValue.get(Calendar.SECOND);
		T oldValue = getValue();
		calendarValue.set(Calendar.SECOND, second);
		 
		fireChangeEvent();
		firePropertyChange(PROPERTY_SECOND, oldSecValue, this.calendarValue.get(Calendar.SECOND));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
		
	}

	public void setDay(int day) {
		int oldDayValue = this.calendarValue.get(Calendar.DATE);
		T oldValue = getValue();
		calendarValue.set(Calendar.DATE, day);
		fireChangeEvent();
		firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DATE));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void addDay(int add) {
		int oldDayValue = this.calendarValue.get(Calendar.DATE);
		T oldValue = getValue();
		calendarValue.add(Calendar.DATE, add);
		fireChangeEvent();
		firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DATE));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void setMonth(int month) {
		int oldYearValue = this.calendarValue.get(Calendar.YEAR);
		int oldMonthValue = this.calendarValue.get(Calendar.MONTH);
		int oldDayValue = this.calendarValue.get(Calendar.DAY_OF_MONTH);
		T oldValue = getValue();

		Calendar newVal = Calendar.getInstance();
		newVal.set(Calendar.DAY_OF_MONTH, 1);
		newVal.set(Calendar.MONTH, month);
		newVal.set(Calendar.YEAR, oldYearValue);

		if (newVal.getActualMaximum(Calendar.DAY_OF_MONTH) <= oldDayValue) {
			newVal.set(Calendar.DAY_OF_MONTH, newVal.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else {
			newVal.set(Calendar.DAY_OF_MONTH, oldDayValue);
		}

		calendarValue.set(Calendar.MONTH, newVal.get(Calendar.MONTH));
		calendarValue.set(Calendar.DAY_OF_MONTH, newVal.get(Calendar.DAY_OF_MONTH));

		fireChangeEvent();
		firePropertyChange(PROPERTY_MONTH, oldMonthValue, this.calendarValue.get(Calendar.MONTH));
		// only fire change event when day actually changed
		if (this.calendarValue.get(Calendar.DAY_OF_MONTH) != oldDayValue) {
			firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DAY_OF_MONTH));
		}
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void addMonth(int add) {
		int oldMonthValue = this.calendarValue.get(Calendar.MONTH);
		T oldValue = getValue();
		calendarValue.add(Calendar.MONTH, add);
		fireChangeEvent();
		firePropertyChange(PROPERTY_MONTH, oldMonthValue, this.calendarValue.get(Calendar.MONTH));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void setYear(int year) {
		int oldYearValue = this.calendarValue.get(Calendar.YEAR);
		int oldMonthValue = this.calendarValue.get(Calendar.MONTH);
		int oldDayValue = this.calendarValue.get(Calendar.DAY_OF_MONTH);
		T oldValue = getValue();

		Calendar newVal = Calendar.getInstance();
		newVal.set(Calendar.DAY_OF_MONTH, 1);
		newVal.set(Calendar.MONTH, oldMonthValue);
		newVal.set(Calendar.YEAR, year);

		if (newVal.getActualMaximum(Calendar.DAY_OF_MONTH) <= oldDayValue) {
			newVal.set(Calendar.DAY_OF_MONTH, newVal.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else {
			newVal.set(Calendar.DAY_OF_MONTH, oldDayValue);
		}

		calendarValue.set(Calendar.YEAR, newVal.get(Calendar.YEAR));
		calendarValue.set(Calendar.DAY_OF_MONTH, newVal.get(Calendar.DAY_OF_MONTH));

		fireChangeEvent();
		firePropertyChange(PROPERTY_YEAR, oldYearValue, this.calendarValue.get(Calendar.YEAR));
		// only fire change event when day actually changed
		if (this.calendarValue.get(Calendar.DAY_OF_MONTH) != oldDayValue) {
			firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DAY_OF_MONTH));
		}
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void addYear(int add) {
		int oldYearValue = this.calendarValue.get(Calendar.YEAR);
		T oldValue = getValue();
		calendarValue.add(Calendar.YEAR, add);
		fireChangeEvent();
		firePropertyChange(PROPERTY_YEAR, oldYearValue, this.calendarValue.get(Calendar.YEAR));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public void setValue(T value) {
		int oldYearValue = this.calendarValue.get(Calendar.YEAR);
		int oldMonthValue = this.calendarValue.get(Calendar.MONTH);
		int oldDayValue = this.calendarValue.get(Calendar.DATE);
		

		int oldHourValue = this.calendarValue.get(Calendar.HOUR);
		int oldMinValue = this.calendarValue.get(Calendar.MINUTE);
		int oldSecValue = this.calendarValue.get(Calendar.SECOND);
		
		
		T oldValue = getValue();
		boolean oldSelectedValue = isSelected();

		if (value != null) {
			this.calendarValue = toCalendar(value);
		 
			selected = true;
		} else {
			selected = false;
		}

		fireChangeEvent();
		firePropertyChange(PROPERTY_YEAR, oldYearValue, this.calendarValue.get(Calendar.YEAR));
		firePropertyChange(PROPERTY_MONTH, oldMonthValue, this.calendarValue.get(Calendar.MONTH));
		firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DATE));
		
		firePropertyChange(PROPERTY_HOUR, oldHourValue, this.calendarValue.get(Calendar.HOUR));
		firePropertyChange(PROPERTY_MINUTE, oldMinValue, this.calendarValue.get(Calendar.MINUTE));
		firePropertyChange(PROPERTY_SECOND, oldSecValue, this.calendarValue.get(Calendar.SECOND));		
		
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
		firePropertyChange("selected", oldSelectedValue, this.selected);
	}

	public void setDate(int year, int month, int day) {
		int oldYearValue = this.calendarValue.get(Calendar.YEAR);
		int oldMonthValue = this.calendarValue.get(Calendar.MONTH);
		int oldDayValue = this.calendarValue.get(Calendar.DATE);
		T oldValue = getValue();
		calendarValue.set(year, month, day);
		fireChangeEvent();
		firePropertyChange(PROPERTY_YEAR, oldYearValue, this.calendarValue.get(Calendar.YEAR));
		firePropertyChange(PROPERTY_MONTH, oldMonthValue, this.calendarValue.get(Calendar.MONTH));
		firePropertyChange(PROPERTY_DAY, oldDayValue, this.calendarValue.get(Calendar.DATE));
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		T oldValue = getValue();
		boolean oldSelectedValue = isSelected();
		this.selected = selected;
		fireChangeEvent();
		firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
		firePropertyChange(PROPERTY_SELECTED, oldSelectedValue, this.selected);
	}



	protected abstract Calendar toCalendar(T from);

	protected abstract T fromCalendar(Calendar from);

}