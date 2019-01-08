/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.util.msg;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.util.UtilValidate;

/**
 *
 * @author lenovo
 */
public class MemoryMsgImpli implements MemoryMsg {

    protected static final Logger log = LoggerFactory.getLogger(MemoryMsgImpli.class);

    private final ExecutorService taskExe = Executors.newSingleThreadExecutor();

    @Override
    public Future<?> sendMsg(final MemMsg m) {
        return taskExe.submit(new Runnable() {
            @Override
            public void run() {
                List<DealMsg> events = getMsgHandle(m);
                if (UtilValidate.isEmpty(events)) {
                    log.debug( "mesage type {} no event ", m.getMsgType());
                    return;
                }
                events.forEach((h) -> {
                    h.dealMsg(m);
                });
            }

        });
    }
    private final ConcurrentMap<String, List<DealMsg>> subInfo = new ConcurrentHashMap<>();

    private List<DealMsg> getMsgHandle(final MemMsg m) {
        return subInfo.get(m.getMsgType());
    }

    @Override
    public void subMsg(String msgType, DealMsg task) {
        List<DealMsg> newList = subInfo.putIfAbsent(msgType, new CopyOnWriteArrayList<>(new DealMsg[]{task}));
        if (newList != null) {
            newList.add(task);
        }
    }

}
