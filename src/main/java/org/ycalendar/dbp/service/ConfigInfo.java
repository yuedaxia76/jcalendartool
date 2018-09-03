package org.ycalendar.dbp.service;
/**
 * 配置信息
 * @author lenovo
 *
 */
public class ConfigInfo {
    /**
     * 获取默认日历
     * @return
     */
	public String getDefaultCalId() {
		return "main";
	}
	
	//发布后改为false,因为数据库虽程序发布
	public boolean isRunInitDb() {
		return true;
	}
	
	public String getDefaultTaskStatus() {
		return "notset";
	}
}
