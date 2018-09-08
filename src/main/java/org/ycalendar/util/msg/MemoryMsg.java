/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.util.msg;

import java.util.concurrent.Future;

/**
 *
 * @author lenovo
 */
public interface MemoryMsg {
    /**
     * @param m 消息
     */
    public Future<?> sendMsg(MemMsg m);
    
    /**
     * 订阅消息
     * @param msgType
     * @param task 
     */
    public void subMsg(String msgType,DealMsg task);
}
