/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.uiutil;

import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.Font;

/**
 *
 * @author lenovo
 */
public class   ValueRadioButton<V> extends JRadioButton {

    private V value  ;
    private Object customObj = null;
    private int fontSize = 18;

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * * 设置radioButton的值 * 
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

    /**
     * * 设置与radioButton绑定的自定义对象 *
     * @param _customObj
     */
    public void setCustomObj(Object _customObj) {
        this.customObj = _customObj;
    }

    /**
     * * 取与radioButton绑定的自定义对象 * @return
     */
    public Object getCustomObj() {
        return this.customObj;
    }

    /**
     * * 构造方法 * 
     * @param _text 字符串,radioButton后显示的文本  
     * @param _value
     * 字符串,radioButton绑定的值
     */
    public ValueRadioButton(String _text, V _value) {
        super(_text);
        this.setText(_text);
        this.setValue(_value);
        publicSetting();
    }

    /**
     * * 构造方法,支持初始化时设定选中状态 * 
     * @param _text 字符串,显示的文本 * 
     * @param _value 字符串,单选框的值
     *
     *
     * @param _selected 布尔型,是否选中
     */
    public ValueRadioButton(String _text, V _value, boolean _selected) {
        super(_text,null,_selected);
        this.setText(_text);
        this.setValue(_value);
        this.setSelected(_selected);
        publicSetting();
    }

    /**
     * * 支持初始化时同时绑定一个自定义对象到radioButton的方法 * 
     * @param _text * 
     * @param _value
     *
     *
     * @param _obj
     */
    public ValueRadioButton(String _text, V _value, Object _obj) {
        super(_text);
        this.setText(_text);
        this.setValue(_value);
        this.setCustomObj(_obj);
        this.setSelected(false);
        publicSetting();
    }

    /**
     * * 支持同时绑定自定义对象和指定选中状态的构造方法 * 
     * @param _text * 
     * @param _value *
     * @param _obj
     *
     *
     * @param _selected true表示将些radioButton置于选中状态,false表示不选中(默认)
     */
    public ValueRadioButton(String _text, V _value, Object _obj, boolean _selected) {
        super(_text,null,_selected);
        this.setText(_text);
        this.setValue(_value);
        this.setCustomObj(_obj);
        this.setSelected(_selected);
        publicSetting();
    }

    /**
     * * 一些对radioButton的公共设置
     */
    public void publicSetting() {
        //设置边距       
        this.setMargin(new java.awt.Insets(10, 35, 0, 0));
        //设置字体样式,粗细,字号    
        this.setFont(new Font("宋体", Font.BOLD, fontSize));
        //设置背景颜色      
        this.setBackground(Color.WHITE);
    }

}
