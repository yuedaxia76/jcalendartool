package org.ycalendar;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.ycalendar.dbp.service.StringCinvert;
import org.ycalendar.ui.jdatepicker.JDatePicker;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.UtilDateTime;
import org.ycalendar.util.msg.MemMsg;
import org.ycalendar.util.msg.MemoryMsg;
import org.ycalendar.util.msg.MemoryMsgImpli;

public class MiscTest {

    protected static final Logger log = LoggerFactory.getLogger(MiscTest.class);

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
            log.error("testSend error", ex);
        }

        Assert.assertEquals(count.get(), 2, "应该=2");
        count.set(0);
    }

    @Test
    public void testStrToDate() {
        String ds = "08/03/13 5:00:00 PM";
        Date d = UtilDateTime.parseDate(ds, "MM/dd/yy hh:mm:ss aaa", Locale.ENGLISH);
        System.out.println(d);

    }

    @Test
    public void testJsStr() {
        String ds = "js:var comd='tstatus in (\\'NOTSET\\',\\'IN-PROCESS\\', \\'NEEDS-ACTION\\') (start_time is null or start_time>=';\n"
                + "var dt = Java.type('org.ycalendar.util.UtilDateTime');\n"
                + "var date = Java.type('java.util.Date');\n"
                + "var t1=dt.getDayStart(new date());\n"
                + "var t2=dt.getDayEnd(new date());\n"
                + "comd=comd+t1+') and (end_time is null or end_time<='+t2+'))';";
        String s = MiscUtil.evaluateExpr(ds);
        System.out.println(s);
        ds = "aaa";
        s = MiscUtil.evaluateExpr(ds);
        Assert.assertEquals(s, ds);
    }

    @Test
    public void testresName() {

        String name = MiscUtil.getResourceName(JDatePicker.class, "/datePicker.gif");
        Assert.assertEquals(name, "org/ycalendar/ui/jdatepicker/datePicker.gif");
    }
}
