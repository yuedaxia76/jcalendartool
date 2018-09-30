package org.ycalendar;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.service.StringCinvert;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MemoryMsg;
import org.ycalendar.util.msg.MemoryMsgImpli;

public class MiscTest {

    @Test
    public void testStoS() {
        StringCinvert<String> sc = (s) -> s;
        String tess = "a";
        String tem = sc.convert(tess);
        assert (tess.equals(tem));

        StringCinvert<Integer> si = (s) -> Integer.valueOf(s);

        String tes1s = "12";

        assert (12 == si.convert(tes1s));
    }
    static MemoryMsg ms;

    private static AtomicInteger count;

    @BeforeSuite
    public void setup() {
        ms = new MemoryMsgImpli();
        count = new AtomicInteger(0);
    }

    @AfterSuite
    public void testEnd() {

        count.set(0);
    }

    @Test
    public void testSubc() {

        ms.subMsg("t1", (m) -> {
            count.incrementAndGet();
            System.out.println("handle test type " + m.getMsgType() + " count=" + count.get());
        });
        ms.subMsg("t1", (m) -> {
            count.incrementAndGet();
            System.out.println("handle test type " + m.getMsgType() + " count=" + count.get());
        });
        //throw new RuntimeException();
    }

    @Test(dependsOnMethods = {"testSubc"})
    public void testSend() throws ExecutionException, InterruptedException {
        MemMsg m = new MemMsg("t1");
        Future<?> wait = ms.sendMsg(m);
        try {
            //异步，还没有执行,需要等待
            wait.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            Logger.getLogger(MiscTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertEquals(count.get(), 2, "应该=2");
        count.set(0);
    }
}
