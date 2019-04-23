/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.uiutil;

import javax.swing.JCheckBox;

/**
 *
 * @author lenovo
 */
public class ValueJCheckBoxButton<V> extends JCheckBox {

    private V value;

    /**
     * * 设置radioButton的值
     *
     *
     * @param _value 字符串
     */
    public void setValue(V _value) {
        this.value = _value;
    }

    /**
     * * 取radioButton的值 * @return
     */
    public V getValue() {
        return this.value;
    }

    public ValueJCheckBoxButton() {
        super();
    }

    public ValueJCheckBoxButton(String text, V v) {
        super(text);
        this.value = v;
    }

    public ValueJCheckBoxButton(final String text, V v, boolean selected) {
        super(text, selected);
        this.value = v;
    }



    @Override
    public String toString() {
        if(value==null){
         return "";
        }
        return value.toString();
    }
}
