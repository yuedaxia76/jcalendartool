package org.ycalendar.util;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
public class UtilValidate {


	/**
	 * 如果为空是否正确
	 * 
	 */
	private static final boolean defaultEmptyOK = true;

	/** 空白字符串 */
	private static final String whitespace = " \t\n\r";

	/**
	 * 电话号码，非数字部分
	 */
	private static final String phoneNumberDelimiters = "()- ";

	/**
	 * 月最后一天
	 */
	private static final int[] daysInMonth = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	public static final boolean areEqual(final Object obj,final  Object obj2) {
		if (obj == null) {
			return obj2 == null;
		} else {
			return obj.equals(obj2);
		}
	}

	// public static final boolean isEmpty(Object[] s) {
	// return ((s == null) || (s.length == 0));
	// }

	public static final <T> boolean isEmpty(final T[] s) {
		return ((s == null) || (s.length == 0));
	}

	public static final <T> boolean isNotEmpty(final T[] c) {
		return ((c != null) && (c.length > 0));
	}

	public static final   boolean isEmpty(final byte[] s) {
		return ((s == null) || (s.length == 0));
	}

	public static final   boolean isNotEmpty(final byte[] c) {
		return ((c != null) && (c.length > 0));
	}
 

	public static final boolean isEmpty(final CharSequence s) {
		return ((s == null) || (s.length() == 0));
	}

	/**
	 * 是否空
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isEmpty(final Map<?, ?> m) {
		return ((m == null) || (m.isEmpty()));
	}

	/**
	 * 是否空
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isEmpty(final Collection<?> c) {
		return ((c == null) || c.isEmpty());
	}

	/**
	 * 是否不空
	 * 
	 * @param s
	 * @return
	 */
 

	public static final boolean isNotEmpty(final CharSequence s) {
		return ((s != null) && (s.length() > 0));
	}

	/**
	 * 是否不空
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isNotEmpty(final Map<?, ?> m) {
		return ((m != null) && (!m.isEmpty()));
	}

	/**
	 * 是否不空
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isNotEmpty(final Collection<?> c) {
		return ((c != null) && !c.isEmpty());
	}

	/**
	 * 是否空白字符串
	 * 
	 * @param s
	 * @return
	 */
	public final static boolean isWhitespace(final String s) {

		if (isEmpty(s))
			return true;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			// Check that current character isn't whitespace.
			char c = s.charAt(i);

			if (whitespace.indexOf(c) == -1)
				return false;
		}
		// All characters are whitespace.
		return true;
	}

	/**
	 * 删除掉，bag中出现的符号
	 * 
	 * @param s
	 * @param bag
	 * @return
	 */
	public final static String stripCharsInBag(final String s, final String bag) {
		int i;
		StringBuilder returnString = new StringBuilder(s.length());

		// Search through string's characters one by one.
		// If character is not in bag, append to returnString.
		for (i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (bag.indexOf(c) == -1)
				returnString.append(c);
		}
		return returnString.toString();
	}

	/** 删除空白. */
	public static String stripCharsNotInBag(final String s, final String bag) {
		int i;
		StringBuilder returnString = new StringBuilder(s.length());

		// Search through string's characters one by one.
		// If character is in bag, append to returnString.
		for (i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (bag.indexOf(c) != -1)
				returnString.append(c);
		}
		return returnString.toString();
	}

	/**
	 * 去除空白字符串
	 * 
	 * @param s
	 * @return
	 */
	public final static String stripWhitespace(final String s) {
		return stripCharsInBag(s, whitespace);
	}

	/**
	 * 是否包含字符 string s.
	 */
	public static boolean charInString(char c,final  String s) {
		return (s.indexOf(c) != -1);

	}

	/**
	 * 去掉开头的空白
	 * 
	 */
	public static String stripInitialWhitespace(final String s) {
		int i = 0;

		while ((i < s.length()) && charInString(s.charAt(i), whitespace))
			i++;
		return s.substring(i);
		// return s.substring(i, s.length);
	}

	/**
	 * 是否字母
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c) {
		return Character.isLetter(c);
	}

	/** 是否数字 (0 .. 9). */
	public static boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	/** 是否字母或者数字. */
	public static boolean isLetterOrDigit(char c) {
		return Character.isLetterOrDigit(c);
	}

	/**
	 * 是否整数
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String regu = "^[-,+]{0,1}[0-9]{1,8}$";
		return s.matches(regu);
	}

	/**
	 * 是否有符号整数
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSignedInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String regu = "^[-,+]{0,1}[0-9]{1,8}$";
		return s.matches(regu);
	}

	public static boolean isNumber(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String regu = "^[-]{0,1}(\\d+)[\\.]?(\\d*)$";
		return s.matches(regu);
	}

	/**
	 * 是否有符号long
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSignedLong(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String regu = "^[-,+]{0,1}[0-9]{1,16}$";
		return s.matches(regu);
	}

	/**
	 * 是否正整数
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isPositiveInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		String reg = "^[1-9]\\d*$";

		return s.matches(reg);

	}

	/**
	 * 非负数
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNonnegativeInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String reg = "\\+{0,1}\\d+";
		return s.matches(reg);

	}

	/**
	 * 是否负数
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNegativeInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		String reg = "^-[1-9]\\d*$";

		return s.matches(reg);

	}

	/** 是否小于等于0整数. */
	public static boolean isNonpositiveInteger(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String reg = "^-[1-9]\\d*|0$";

		return s.matches(reg);
	}

	/**
	 * 是否浮点数,必须是
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isFloat(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String reg = "^[-,+]{0,1}(\\d+)[\\.]+(\\d+)$";
		return s.matches(reg);

	}

	/**
	 * 是否有符号float
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSignedFloat(final String s) {
		return isFloat(s);

	}

	/**
	 * 是否double
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSignedDouble(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		String reg = "^-?([1-9]d*.d*|0.d*[1-9]d*|0?.0+|0)$";
		return s.matches(reg);
	}

	/**
	 * 是否仅字母
	 * 
	 * 这里的字母是基于当前字符集的
	 * 
	 */
	public static boolean isAlphabetic(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		int len = s.length();
		for (int i = 0; i < len; i++) {
			// Check that current character is letter.
			char c = s.charAt(i);

			if (!isLetter(c))
				return false;
		}

		// All characters are letters.
		return true;
	}

	/**
	 * 仅是字母或者数字,注意字母是当前语言的字母
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isAlphanumeric(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		int len = s.length();
		for (int i = 0; i < len; i++) {
			// Check that current character is number or letter.
			char c = s.charAt(i);

			if (!isLetterOrDigit(c))
				return false;
		}

		// All characters are numbers or letters.
		return true;
	}

	/**
	 * 是否国际电话号码
	 */
	public static boolean isInternationalPhoneNumber(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String normalizedPhone = stripCharsInBag(s, phoneNumberDelimiters);

		return isPositiveInteger(normalizedPhone);
	}

	/**
	 * 是否固定电话号码
	 */
	public static boolean isPhoneNumber(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		String reg = "^((d{3,4})|d{3,4}-)?d{7,8}$";

		return s.matches(reg);
	}

	/**
	 * 是否email
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmail(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		String myReg = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
		return s.matches(myReg);

	}

	/**
	 * 是否下划线,拉丁字母,数字,-,.
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isEngNum(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		String myReg = "[A-Za-z0-9_\\.-]+";
		return s.matches(myReg);

	}

	/**
	 * 是否年
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isYear(final String s) {
		if (isEmpty(s))
			return defaultEmptyOK;

		if (!isNonnegativeInteger(s))
			return false;
		return ((s.length() == 2) || (s.length() == 4));
	}

	/**
	 * 是否制定范围内整数 即 a<=s<=b
	 */
	public static boolean isIntegerInRange(final String s, int a, int b) {
		if (isEmpty(s))
			return defaultEmptyOK;
		if (!isSignedInteger(s))
			return false;

		int num = Integer.parseInt(s);

		return ((num >= a) && (num <= b));
	}

	/** 是否月 1 and 12. */
	public static boolean isMonth(String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		return isIntegerInRange(s, 1, 12);
	}

	/** 是否天 1 and 31. */
	public static boolean isDay(String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		return isIntegerInRange(s, 1, 31);
	}

	/**
	 * 2月天数
	 * 
	 * @param year
	 * @return
	 */
	public static int daysInFebruary(int year) {

		return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28);
	}

	/** 是否小时0-23. */
	public static boolean isHour(String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		return isIntegerInRange(s, 0, 23);
	}

	/** 是否分钟 0 and 59. */
	public static boolean isMinute(String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		return isIntegerInRange(s, 0, 59);
	}

	/**
	 * 是否秒
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSecond(String s) {
		if (isEmpty(s))
			return defaultEmptyOK;
		return isIntegerInRange(s, 0, 59);
	}

	/**
	 * 是否日期
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return
	 */
	public static boolean isDate(String year, String month, String day) {

		if (!(isYear(year) && isMonth(month) && isDay(day)))
			return false;

		int intYear = Integer.parseInt(year);
		int intMonth = Integer.parseInt(month);
		int intDay = Integer.parseInt(day);

		if (intDay > daysInMonth[intMonth - 1])
			return false;
		if ((intMonth == 2) && (intDay > daysInFebruary(intYear)))
			return false;
		return true;
	}

	/**
	 * 是否日期
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isDate(final String date) {
		if (isEmpty(date))
			return defaultEmptyOK;
		String month;
		String day;
		String year;

		int dateSlash1 = date.indexOf(UtilDateTime.datePartition);
		int dateSlash2 = date.lastIndexOf(UtilDateTime.datePartition);

		if (dateSlash1 <= 0 || dateSlash1 == dateSlash2)
			return false;
		month = UtilDateTime.getMonth(date);
		day = UtilDateTime.getDay(date);
		year = UtilDateTime.getYear(date);

		return isDate(year, month, day);
	}

	/**
	 * 今天之后日期
	 */
	public static boolean isDateAfterToday(final String date) {
		if (isEmpty(date))
			return defaultEmptyOK;
		int dateSlash1 = date.indexOf(UtilDateTime.datePartition);
		int dateSlash2 = date.lastIndexOf(UtilDateTime.datePartition);

		if (dateSlash1 <= 0)
			return false;

		java.util.Date passed = null;
		if (dateSlash1 == dateSlash2) {
			// consider the day to be optional; use the first day of the
			// following month for comparison since this is an is after test
			String month = UtilDateTime.getMonth(date);
			String day = "28";
			String year = UtilDateTime.getYear(date);
			if (!isDate(year, month, day))
				return false;

			try {
				int monthInt = Integer.parseInt(month);
				int yearInt = Integer.parseInt(year);
				Calendar calendar = Calendar.getInstance();
				calendar.set(yearInt, monthInt - 1, 0, 0, 0, 0);
				calendar.add(Calendar.MONTH, 1);
				passed = new java.util.Date(calendar.getTime().getTime());
			} catch (Exception e) {
				passed = null;
			}
		} else {
			String month = UtilDateTime.getMonth(date);
			String day = UtilDateTime.getDay(date);
			String year = UtilDateTime.getYear(date);
			if (!isDate(year, month, day))
				return false;
			passed = UtilDateTime.toDate(year,month, day,  "0", "0", "0");
		}

		java.util.Date now = new Date();
		if (passed != null) {
			return passed.after(now);
		} else {
			return false;
		}
	}

	/**
	 * 是否时间
	 * 
	 * @param hour
	 *            小时
	 * @param minute
	 *            分
	 * @param second
	 *            秒
	 * @return
	 */
	public static boolean isTime(String hour, String minute, String second) {
		if (isHour(hour) && isMinute(minute) && isSecond(second))
			return true;
		else
			return false;
	}

	/**
	 * 是否时间
	 * 
	 * @param time
	 * @return
	 */
	public final static boolean isTime(final String time) {
		if (isEmpty(time))
			return defaultEmptyOK;

		String hour;
		String minute;
		String second;

		int timeColon1 = time.indexOf(':');
		int timeColon2 = time.lastIndexOf(':');

		if (timeColon1 <= 0)
			return false;
		hour = time.substring(0, timeColon1);
		if (timeColon1 == timeColon2) {
			minute = time.substring(timeColon1 + 1);
			second = "0";
		} else {
			minute = time.substring(timeColon1 + 1, timeColon2);
			second = time.substring(timeColon2 + 1);
		}

		return isTime(hour, minute, second);
	}
    /** isUrl returns true 如果包含 contains ://
     * @param s String  
     * @return true 如果包含 ://
     */
    public final static boolean isUrl(final String s) {
        if (isEmpty(s)) return defaultEmptyOK;
        if (s.indexOf("://") != -1)
            return true;
        return false;
    }

}
