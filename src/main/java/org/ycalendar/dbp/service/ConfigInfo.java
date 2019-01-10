package org.ycalendar.dbp.service;

import java.util.Properties;
import org.ycalendar.util.MiscUtil;

/**
 * 配置信息
 *
 * @author lenovo
 *
 */
public class ConfigInfo {

    static class ConfRef {

        static final Properties cp = MiscUtil.getPropertiesFromRes("YCalendar.xml");
    }

    /**
     * 获取默认日历
     *
     * @return
     */
    public String getDefaultCalId() {
        return ConfRef.cp == null ? "main" : ConfRef.cp.getProperty("DefaultCalId");
    }

    //发布后改为false,因为数据库虽程序发布
    public boolean isRunInitDb() {

        return ConfRef.cp == null ? true : "true".equals(ConfRef.cp.getProperty("DefaultCalId"));

    }

    public String getDefaultTaskStatus() {
        return ConfRef.cp == null ? "NOTSET" : ConfRef.cp.getProperty("DefaultTaskStatus");

    }
    
    public boolean isDebug(){
    return ConfRef.cp == null ? false : "true".equals(ConfRef.cp.getProperty("debug"));
    }

}
