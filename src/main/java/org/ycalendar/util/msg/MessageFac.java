/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.util.msg;

/**
 *
 * @author lenovo
 */
public class MessageFac {
    
    private static final MemoryMsg MemMsg=new MemoryMsgImpli();
    
    public static MemoryMsg getMemoryMsg(){
      return MemMsg;
    }
}
