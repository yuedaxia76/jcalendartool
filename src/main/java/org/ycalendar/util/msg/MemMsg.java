/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.util.msg;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lenovo
 */
public class MemMsg {

    //消息名称
    private final String msgType;

    public String getMsgType() {
        return msgType;
    }

    public Map<String, Object> getMsg() {
        return msg;
    }

    public MemMsg(String msgType) {
        this.msgType = msgType;
    }

    private final Map<String, Object> msg = new HashMap<>();

    public Object setProperty(String name, Object value) {
        return msg.put(name, value);
    }

    public Object getProperty(String name) {
        return msg.get(name);
    }
}
