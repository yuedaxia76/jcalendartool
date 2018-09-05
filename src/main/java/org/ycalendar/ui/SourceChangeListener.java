/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.ui;

import javax.swing.event.ChangeListener;

/**
 *
 * @author lenovo
 */
public interface SourceChangeListener extends ChangeListener {
    /**
     * 设置监听源
     */
    public void setSourceObj();
    
    /**
     * 获取监听源
     * @return 
     */
    public Object getSourceObj();
}
