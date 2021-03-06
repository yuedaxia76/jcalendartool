package org.ycalendar.dbp.dao;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ycalendar.util.UtilValidate;

public class PreparedStatementHandler {
	private static final Logger log = LoggerFactory.getLogger(PreparedStatementHandler.class);

	private static final PreparedStatementHandler psh = new PreparedStatementHandler();
 

	public static PreparedStatementHandler getInstance() {
		return psh;
	}

	public void pager(StringBuffer sql, List<Object> params, int pageSize, int pageNo) {

		sql.append(" limit ?, ?");
		params.add(pageSize * (pageNo - 1));
		params.add(pageSize);

	}

	/**
	 * operator: and/or/where
	 */
	public <T> void in(StringBuilder sql, List<Object> params, String operator, String field, List<T> values) {
		if (UtilValidate.isEmpty(values)) {
			throw new NullPointerException(); // 抛空指针异常，避免执行非预期的动作
		}

		sql.append(' ');
		sql.append(operator);

		sql.append(' ');
		sql.append(field);
 
		sql.append(generateQuestionMarks(values.size()));
 
		
		params.addAll(values);
 
	}

	/* 生成带有in 的占位符 */
	public static String generateQuestionMarks(int n) {
		if (n < 1)
			return " in ()";
		
		StringBuilder buf = new StringBuilder(n<<2);
		buf.append(" in (");
		for (int i = 0; i < n; i++) {
			buf.append("?,");
		}
		buf.deleteCharAt(buf.length() - 1);
		buf.append(") ");
		return buf.toString();
	}

	/* 调整参数 */
	public void adjustParams(final int len,Object... params) {
		if(UtilValidate.isEmpty(params)) {
			return ;
		}
		for (int i = 0; i < len; i++) {
			Object value = params[i];
			if (value == null)
				continue;
			if (value instanceof Date) {
				params[i] =  ((Date) value).getTime();// 转化为long
			} else if (value.getClass().isEnum()) {
				params[i] = params[i].toString();
			}  
		}
	}



	/**
	 * 大写字母前加_,nameAge->name_age
	 * 
	 * @param camel
	 * @return
	 */
	public static String camel2underscore(String camel) {
		camel = camel.replaceAll("([a-z])([A-Z])", "$1_$2");
		return camel.toLowerCase();
	}

	public static String underscore2camel(String underscore) {
		if (!underscore.contains("_")) {
			return underscore;
		}
		StringBuffer buf = new StringBuffer();
		underscore = underscore.toLowerCase();
		Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
		while (m.find()) {
			m.appendReplacement(buf, m.group(1).toUpperCase());
		}
		return m.appendTail(buf).toString();
	}

	/* 语句和参数已经经过调整 */
	public void print(final String sql,final int len, final Object[] params) {
		if (!match(sql, len)) {
			log.error(sql);
			return;
		}

		 
		Object[] values = new Object[len];
		System.arraycopy(params, 0, values, 0, len);

		for (int i = 0; i < len; i++) {
			Object value = values[i];
			if (value instanceof String) {
				values[i] = toQuote(value);
			}
		}
		String statement = String.format(sql.replaceAll("\\?", "%s"), values);
		log.error(statement);
	}

	/* ?和参数的实际个数是否匹配 */
	private boolean match(String sql, int len) {
		Matcher m = Pattern.compile("(\\?)").matcher(sql);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count == len;
	}

	private String toQuote(Object value) {
		return "'" + value + "'";
	}

}
