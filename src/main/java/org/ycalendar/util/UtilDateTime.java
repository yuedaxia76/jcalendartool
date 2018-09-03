package org.ycalendar.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UtilDateTime {

    public static final String datePartition = "-";

    public static final String defaultDatePattern = "yyyy-MM-dd";
    public static final String defaultDateTimePattern = "yyyy-MM-dd HH:mm:ss";

    public static final String default_Time_Pattern = "HH:mm:ss";

    public static final Calendar copyCalendar(final Calendar source) {
        Calendar copyedCal = Calendar.getInstance();
        copyedCal.setTimeInMillis(source.getTimeInMillis());
        return copyedCal;
    }

    public static final void setDayStart(Calendar source) {
        source.set(Calendar.HOUR_OF_DAY, 0);
        source.set(Calendar.MINUTE, 0);
        source.set(Calendar.SECOND, 0);
        source.set(Calendar.MILLISECOND, 0);
    }

    public static final void setDayEnd(Calendar source) {
        source.set(Calendar.HOUR_OF_DAY, 23);
        source.set(Calendar.MINUTE, 59);
        source.set(Calendar.SECOND, 59);
        source.set(Calendar.MILLISECOND, 999);
    }

    public static String nowDateString() {
        SimpleDateFormat df = new SimpleDateFormat(defaultDatePattern);
        return df.format(new Date());
    }

    /**
     * 按照指定格式返回当前日期时间
     *
     * @return String formatted for right now
     */
    public static String nowDateString(final String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    public static String nowDateTimeString() {

        return new SimpleDateFormat(defaultDateTimePattern).format(new Date());
    }

    public static String longToString(final long time) {

        return longToString(time, null);
    }

    public static String longToString(final long time, final String datePattern) {
        SimpleDateFormat df;
        if (UtilValidate.isEmpty(datePattern)) {
            df = new SimpleDateFormat(defaultDateTimePattern);
        } else {
            df = new SimpleDateFormat(datePattern);
        }
        return df.format(new Date(time));
    }

    public static String toGmtTimestampString(Date dateTime) {
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(dateTime);
    }

    /**
     * 取得给定时间的开始时间 例如：2003-10-12 00:00:00.0
     *
     * @param stamp 给定的日期
     * @return 返回当天的起始时间
     */
    public static long getDayStart(Date stamp) {
		Calendar tempCal = Calendar.getInstance();

		tempCal.setTime(stamp);
		tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return tempCal.getTimeInMillis();
    }

    /**
     * 取得给定时间多少天以后的开始时间 例如：2003-10-12 00:00:00.0
     *
     * @param stamp 给定的日期
     * @param daysLater 推迟的天数
     * @return 返回推迟天数以后的起始时间
     */
    public static long getDayStart(Date stamp, int daysLater) {
		Calendar tempCal = Calendar.getInstance();

		tempCal.setTime(stamp);
		tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
		return tempCal.getTimeInMillis();
    }

    /**
     * 制定时间在一年中的周数
     *
     * @param input
     * @return
     */
    public static int weekNumber(Date input) {
        Calendar calendar = Calendar.getInstance();
        return weekNumber(input, calendar.getFirstDayOfWeek());
    }

    public static int weekNumber(Date input, int startOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(startOfWeek);

        if (startOfWeek == Calendar.MONDAY) {
            calendar.setMinimalDaysInFirstWeek(4);
        } else if (startOfWeek == Calendar.SUNDAY) {
            calendar.setMinimalDaysInFirstWeek(3);
        }

        calendar.setTime(input);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 给定时间当天的结束时间
     *
     * @param stamp 日期
     * @return 得到的日期
     */
    public static long getDayEnd(Date stamp) {
		Calendar tempCal = Calendar.getInstance();

		tempCal.setTime(stamp);
		tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		return tempCal.getTimeInMillis();
    }

    /**
     * 取得给定时间多少天以后的结束时间 例如：2003-10-12 00:00:00.0
     *
     * @param stamp 给定的日期
     * @param daysLater 推迟的天数
     * @return 返回推迟天数以后的结束时间
     */
    public static long getDayEnd(Date stamp, int daysLater) {
		Calendar tempCal = Calendar.getInstance();

		tempCal.setTime(stamp);
		tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
		return tempCal.getTimeInMillis();
    }

    /**
     * 处理形如 Fri, 03 Apr 2009 02:28:31 GMT 的时间,注意如果GMT后有+{1}8:00 必须是标准的
     *
     * @param gmt
     * @return date对象
     */
    public static Date gmtStrToDate(String gmt) {
        SimpleDateFormat defaultDf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
        // defaultDf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return defaultDf.parse(gmt);
        } catch (ParseException e) {
            if (gmt.indexOf('+') > -1) {
                gmt = gmt + ":00";
                try {
                    return defaultDf.parse(gmt);
                } catch (ParseException e1) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public static String dateToGMT(Date d) {
        SimpleDateFormat defaultDf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
        defaultDf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return defaultDf.format(d);

    }

    /**
     * 将日期字符串转换为java.util.Date类型
     *
     * @param dateTime 日期时间格式的字符串 例如："2003-10-12 15:20:30"
     * @return 转换后的java.util.Date
     */
    public static java.util.Date toDate(final String date) {

        if (date.indexOf(':') < 0) {
            return parseDate(date, defaultDatePattern);
        } else {
            int timeColon1 = date.indexOf(':');
            int timeColon2 = date.lastIndexOf(':');
            if (timeColon1 == timeColon2) {
                return parseDate(date + ":00", defaultDateTimePattern);
            } else {
                return parseDate(date, defaultDateTimePattern);
            }

        }

    }

    public static long toLong(final String date) {

        if (date.indexOf(':') < 0) {
            return parseDate(date, defaultDatePattern).getTime();
        } else {
            int timeColon1 = date.indexOf(':');
            int timeColon2 = date.lastIndexOf(':');
            if (timeColon1 == timeColon2) {
                return parseDate(date + ":00", defaultDateTimePattern).getTime();
            } else {
                return parseDate(date, defaultDateTimePattern).getTime();
            }

        }

    }

    public static DateFormat toTimeFormat(final String timeFormat, final Locale locale) {
        DateFormat df = null;
        if (timeFormat == null) {
            df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        } else {
            df = new SimpleDateFormat(timeFormat);
        }

        return df;
    }

    /**
     * 将日期、时间字符串转换为java.util.Date类型
     *
     * @param date 日期字符串 格式：YYYY-MM-DD
     * @param time 时间字符串 格式： HH:MM or HH:MM:SS
     * @return 转换后的java.util.Date
     */
    public static java.util.Date toDate(final String date, final String time) {
        if (date == null || time == null) {
            return null;
        }

        return toDate(date + " " + time);

    }

    /**
     * 将日期、时间字符串转换为java.util.Date类型
     *
     * @param monthStr 月份字符串
     * @param dayStr 日字符串
     * @param yearStr 年字符串
     * @param hourStr 小时字符串
     * @param minuteStr 分钟字符串
     * @param secondStr 秒字符串
     * @return 转换后的java.util.Date
     */
    public static java.util.Date toDate(String yearStr, String monthStr, String dayStr, String hourStr, String minuteStr, String secondStr) {
        int month, day, year, hour, minute, second;

        try {
            month = Integer.parseInt(monthStr);
            day = Integer.parseInt(dayStr);
            year = Integer.parseInt(yearStr);
            hour = Integer.parseInt(hourStr);
            minute = Integer.parseInt(minuteStr);
            second = Integer.parseInt(secondStr);
        } catch (Exception e) {
            return null;
        }
        return toDate(year, month, day, hour, minute, second);
    }

    /**
     * 将日期、时间的整数串转换为java.util.Date类型
     *
     * @param month 月份整数
     * @param day 日整数
     * @param year 年整数
     * @param hour 小时整数
     * @param minute 分钟整数
     * @param second 秒整数
     * @return 转换后的java.util.Date
     */
    public static java.util.Date toDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);//设置毫秒部分

        return calendar.getTime();
    }

    public static java.util.Date toSqlTime(String hourStr, String minuteStr, String secondStr) {
        return toDate("0", "0", "0", hourStr, minuteStr, secondStr);

    }

    /**
     * 将java.util.Date类型转换为字符串,取日期 格式: YYYY-MM-DD
     *
     * @param date 日期
     * @return 转换后的字符串 格式: YYYY-MM-DD
     */
    public static String toDateString(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat(defaultDatePattern);
        return df.format(date);
    }

    /**
     * Makes a date String in the given from a Date
     *
     * @param date The Date
     * @return A date String in the given format
     */
    public static String toDateString(java.util.Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = null;
        if (format != null) {
            dateFormat = new SimpleDateFormat(format);
        } else {
            dateFormat = new SimpleDateFormat();
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        return dateFormat.format(date);
    }

    /**
     * 将java.util.Date中的时间取出来，转换为Time格式的字符串 格式：HH:MM:SS
     *
     * @param date 日期时间
     * @return 转换后的字符串 格式：HH:MM:SS
     */
    public static String toTimeString(java.util.Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat(default_Time_Pattern);
        return df.format(date);
    }

    /**
     * 将时间的整数转换为标准格式的字符串 格式：HH:MM:SS
     *
     * @param hour 小时整数
     * @param minute 分钟整数
     * @param second 秒整数
     * @return 符合格式的字符串
     */
    public static String toTimeString(int hour, int minute, int second) {
        String hourStr;
        String minuteStr;
        String secondStr;

        if (hour < 10) {
            hourStr = "0" + hour;
        } else {
            hourStr = "" + hour;
        }
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = "" + minute;
        }
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = "" + second;
        }
        if (second == 0) {
            return hourStr + ":" + minuteStr;
        } else {
            return hourStr + ":" + minuteStr + ":" + secondStr;
        }
    }

    /**
     * 将java.util.Date中的时间取出来，转换为DateTime格式的字符串 格式：YYYY-MM-DD HH:MM:SS
     *
     * @param date 日期时间
     * @return 转换后的字符串 格式：YYYY-MM-DD HH:MM:SS
     */
    public static String toDateTimeString(java.util.Date date, final String datePattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat df;
        if (UtilValidate.isEmpty(datePattern)) {
            df = new SimpleDateFormat(defaultDateTimePattern);
        } else {
            df = new SimpleDateFormat(datePattern);
        }

        return df.format(date);
    }

    public static String toDateTimeString(java.util.Date date) {
        return toDateTimeString(date, null);
    }

    /**
     * 将java.sql.Date中的时间取出来，转换为DateTime格式的字符串 格式：YYYY-MM-DD HH:MM:SS
     *
     * @param date 日期时间
     * @return 转换后的字符串 格式：YYYY-MM-DD HH:MM:SS
     */
    public static String toDateTimeString(java.sql.Date date) {
        return toDateTimeString(date, null);
    }

    /**
     * 得到当前月开始那天
     *
     * @return java.sql.Timestamp类型
     */
    public static Date monthBegin() {
        Calendar mth = Calendar.getInstance();

        mth.set(Calendar.DAY_OF_MONTH, 1);
        mth.set(Calendar.HOUR_OF_DAY, 0);
        mth.set(Calendar.MINUTE, 0);
        mth.set(Calendar.SECOND, 0);
        mth.set(Calendar.AM_PM, Calendar.AM);
        return new Date(mth.getTime().getTime());
    }

    public static long addDaysToTimestamp(long now, int n) {
        return now + n * 24 * 3600l * 1000l;
    }

    public static Timestamp addDaysToTimestamp(Date start, int days) {
        return new Timestamp(start.getTime() + (24L * 60L * 60L * 1000L * days));
    }

    public static Timestamp addDaysToTimestamp(Date start, Double days) {
        return new Timestamp(start.getTime() + ((int) (24L * 60L * 60L * 1000L * days)));
    }

    /**
     * 得到月
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD
     * @return month
     */
    public static String getMonth(final String date) {

        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        int dateSlash1 = date.indexOf(UtilDateTime.datePartition);
        int dateSlash2 = date.lastIndexOf(UtilDateTime.datePartition);

        return date.substring(dateSlash1 + 1, dateSlash2);

    }

    /**
     * 得到年
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD
     * @return month
     */
    public static String getYear(final String date) {

        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        int dateSlash1 = date.indexOf(UtilDateTime.datePartition);
        // int dateSlash2 = date.lastIndexOf(UtilDateTime.datePartition);

        return date.substring(0, dateSlash1);

    }

    /**
     * 得到日
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD
     * @return Day
     */
    public static String getDay(final String date) {

        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        // int dateSlash1 = date.indexOf(UtilDateTime.datePartition);
        int dateSlash2 = date.lastIndexOf(UtilDateTime.datePartition);

        return date.substring(dateSlash2 + 1, (date.length() < (dateSlash2 + 3)) ? date.length() : (dateSlash2 + 3));

    }

    /**
     * 得到小时
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD HH:MM:SS
     * @return
     */
    public static String getHour(final String date) {
        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        int dateSlash1 = date.indexOf(':');
        // int dateSlash2 = date.lastIndexOf(":");

        return date.substring(((dateSlash1 - 2) < 0) ? 0 : dateSlash1 - 2, dateSlash1).trim();
    }

    /**
     * 得到分钟
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD HH:MM:SS
     * @return
     */
    public static String getMinute(final String date) {
        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        int dateSlash1 = date.indexOf(':');
        int dateSlash2 = date.lastIndexOf(':');
        if (dateSlash1 == dateSlash2) {
            return date.substring(dateSlash1 + 1, (date.length() < (dateSlash2 + 3)) ? date.length() : (dateSlash2 + 3)).trim();
        }
        return date.substring(dateSlash1 + 1, dateSlash2).trim();
    }

    /**
     * 得到秒
     *
     * @param date 日期格式的字符串，格式：YYYY-MM-DD HH:MM:SS
     * @return
     */
    public static String getSecond(final String date) {
        if (UtilValidate.isEmpty(date)) {
            return "";
        }

        int dateSlash1 = date.indexOf(':');
        int dateSlash2 = date.lastIndexOf(':');
        if (dateSlash1 == dateSlash2) {
            return "0";
        }
        return date.substring(dateSlash2 + 1, (date.length() < (dateSlash2 + 3)) ? date.length() : (dateSlash2 + 3)).trim();
    }

    /**
     * 得到年
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getYear(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getYear(str);
    }

    /**
     * 得到年
     *
     * @param date java.util.Date
     * @return
     */
    public static String getYear(java.util.Date date) {
        String str = toDateTimeString(date);
        return getYear(str);
    }

    /**
     * 得到月
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getMonth(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getMonth(str);
    }

    /**
     * 得到月
     *
     * @param date java.util.Date
     * @return
     */
    public static String getMonth(java.util.Date date) {
        String str = toDateTimeString(date);
        return getMonth(str);
    }

    /**
     * 得到日
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getDay(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getDay(str);
    }

    /**
     * 得到日
     *
     * @param date java.util.Date
     * @return
     */
    public static String getDay(java.util.Date date) {
        String str = toDateTimeString(date);
        return getDay(str);
    }

    /**
     * 得到小时
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getHour(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getHour(str);
    }

    /**
     * 得到小时
     *
     * @param date java.util.Date
     * @return
     */
    public static String getHour(java.util.Date date) {
        String str = toDateTimeString(date);
        return getHour(str);
    }

    /**
     * 得到分钟
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getMinute(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getMinute(str);
    }

    /**
     * 得到分钟
     *
     * @param date java.util.Date
     * @return
     */
    public static String getMinute(java.util.Date date) {
        String str = toDateTimeString(date);
        return getMinute(str);
    }

    /**
     * 得到秒
     *
     * @param date java.sql.Date
     * @return
     */
    public static String getSecond(java.sql.Date date) {
        String str = toDateTimeString(date);
        return getSecond(str);
    }

    /**
     * 得到秒
     *
     * @param date java.util.Date
     * @return
     */
    public static String getSecond(java.util.Date date) {
        String str = toDateTimeString(date);
        return getSecond(str);
    }

    /**
     * 得到星期1-7对应周一-周日
     *
     * @param date java.util.Date
     * @return 字符串1，2，3，4，5，6，7
     */
    public static int getWeek(java.util.Date date) {
        if (date == null) {
            return 1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekNum == 0) {
            weekNum = 7;
        }
        return weekNum;
    }

    /**
     * 得到星期1-7对应周一-周日
     *
     * @param date date格式的字符串，例如：2003-12-12
     * @return 字符串1，2，3，4，5，6，7
     */
    public static int getWeek(String date) {
        return getWeek(toDate(date));
    }

    /**
     * 得到当前星期1-7对应周一-周日
     *
     * @return 字符串1，2，3，4，5，6，7
     */
    public static int getWeek() {
        Calendar calendar = Calendar.getInstance();
        int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekNum == 0) {
            weekNum = 7;
        }
        return weekNum;
    }

    public static String getTimeStr(long time) {
        return longToString(time);
    }

    /**
     *
     * @param str
     * @param format 指定格式 比如yyyyMMdd hh:mm:ss
     * @return
     */
    public static Date parseDate(final String str, String format) {
        if (format == null) {
            format = defaultDatePattern;
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("Could not convert " + str + " to java.util.Date", e);
        }
    }

    public static Date getEarliestDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        cal.set(Calendar.YEAR, cal.getActualMinimum(Calendar.YEAR));
        cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getLatestDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        cal.set(Calendar.YEAR, cal.getActualMaximum(Calendar.YEAR));
        cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Returns a TimeZone object based upon a time zone ID. Method defaults to
     * server's time zone if tzID is null or empty.
     *
     * @see java.util.TimeZone
     */
    public static TimeZone toTimeZone(String tzId) {
        if (UtilValidate.isEmpty(tzId)) {
            return TimeZone.getDefault();
        } else {
            return TimeZone.getTimeZone(tzId);
        }
    }

    /**
     * Returns a TimeZone object based upon an hour offset from GMT.
     *
     * @see java.util.TimeZone
     */
    public static TimeZone toTimeZone(int gmtOffset) {
        if (gmtOffset > 12 || gmtOffset < -14) {
            throw new IllegalArgumentException("Invalid GMT offset");
        }
        String tzId = gmtOffset > 0 ? "Etc/GMT+" : "Etc/GMT";
        return TimeZone.getTimeZone(tzId + gmtOffset);
    }

    /**
     * Returns an initialized DateFormat object.
     *
     * @param timeFormat optional format string
     * @param tz
     * @param locale can be null if timeFormat is not null
     * @return DateFormat object
     */
    public static DateFormat toTimeFormat(String timeFormat, TimeZone tz, Locale locale) {
        DateFormat df = null;
        if (UtilValidate.isEmpty(timeFormat)) {
            df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        } else {
            df = new SimpleDateFormat(timeFormat, locale == null ? Locale.getDefault() : locale);
        }
        df.setTimeZone(tz);
        return df;
    }

    /**
     * Returns an initialized DateFormat object.
     *
     * @param dateTimeFormat optional format string
     * @param tz
     * @param locale can be null if dateTimeFormat is not null
     * @return DateFormat object
     */
    public static DateFormat toDateTimeFormat(String dateTimeFormat, TimeZone tz, Locale locale) {
        DateFormat df = null;
        if (UtilValidate.isEmpty(dateTimeFormat)) {
            df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        } else {
            df = new SimpleDateFormat(dateTimeFormat, locale == null ? Locale.getDefault() : locale);
        }
        df.setTimeZone(tz);
        return df;
    }

    /**
     * Returns a List of month name Strings - suitable for calendar headings.
     *
     * @param locale
     * @return List of month name Strings
     */
    public static List<String> getMonthNames(Locale locale) {
        Calendar tempCal = Calendar.getInstance(locale);
        tempCal.set(Calendar.MONTH, Calendar.JANUARY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", locale);
        List<String> resultList = new ArrayList<String>();
        for (int i = Calendar.JANUARY; i <= tempCal.getActualMaximum(Calendar.MONTH); i++) {
            resultList.add(dateFormat.format(tempCal.getTime()));
            tempCal.roll(Calendar.MONTH, 1);
        }
        return resultList;
    }

    /**
     * Returns an initialized DateFormat object.
     *
     * @param dateFormat optional format string
     * @param tz
     * @param locale can be null if dateFormat is not null
     * @return DateFormat object
     */
    public static DateFormat toDateFormat(String dateFormat, TimeZone tz, Locale locale) {
        DateFormat df = null;
        if (UtilValidate.isEmpty(dateFormat)) {
            df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        } else {
            df = new SimpleDateFormat(dateFormat, locale == null ? Locale.getDefault() : locale);
        }
        df.setTimeZone(tz);
        return df;
    }

    /**
     * Returns a Calendar object initialized to the specified date/time, time
     * zone, and locale.
     *
     * @param date date/time to use
     * @param timeZone
     * @param locale
     * @return Calendar object
     * @see java.util.Calendar
     */
    public static Calendar toCalendar(Date date, TimeZone timeZone, Locale locale) {
        Calendar cal = Calendar.getInstance(timeZone, locale);
        if (date != null) {
            cal.setTime(date);
        }
        return cal;
    }

    /**
     * Returns a List of day name Strings - suitable for calendar headings.
     *
     * @param locale
     * @return List of day name Strings
     */
    public static List<String> getDayNames(Locale locale) {
        Calendar tempCal = Calendar.getInstance(locale);
        tempCal.set(Calendar.DAY_OF_WEEK, tempCal.getFirstDayOfWeek());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", locale);
        List<String> resultList = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            resultList.add(dateFormat.format(tempCal.getTime()));
            tempCal.roll(Calendar.DAY_OF_WEEK, 1);
        }
        return resultList;
    }

    /**
     * JDBC日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * JDBC日期时间
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * JDBC 时间
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

}
