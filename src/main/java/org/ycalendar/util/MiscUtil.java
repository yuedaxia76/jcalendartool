package org.ycalendar.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.ycalendar.dbp.service.ConfigInfo;

public class MiscUtil {

    private static final Logger log = LoggerFactory.getLogger(MiscUtil.class);

    public static final String getId() {

        return UUIDToStr(java.util.UUID.randomUUID());

    }

    private static final String UUIDToStr(final UUID uuid) {
        StringBuilder sb = new StringBuilder(32);
        sb.append(digitsl(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digitsl(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digitsl(uuid.getMostSignificantBits(), 4));
        sb.append(digitsl(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digitsl(uuid.getLeastSignificantBits(), 12));

        return sb.toString();
    }

    private static final String digitsl(final long val, final int digits) {
        long hi = 1L << (digits << 2);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    public static void main(String[] args) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = environment.getAvailableFontFamilyNames();// ���ϵͳ����
        for (int i = 0; i < fonts.length; i++) {
            System.out.println(fonts[i]);
        }
    }

    public static String toLocalMonth(final int month) {
        switch (month) {
            case 0:
                return "一月";
            case 1:
                return "二月";
            case 2:
                return "三月";
            case 3:
                return "四月";
            case 4:
                return "五月";
            case 5:
                return "六月";
            case 6:
                return "七月";
            case 7:
                return "八月";
            case 8:
                return "九月";
            case 9:
                return "十月";
            case 10:
                return "十一月";
            case 11:
                return "十二月";

        }
        log.error("error month {}", month);
        return "一月";
    }

    public static String toLocalWeek(final int week) {
        switch (week) {

            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 0:
            case 7:
                return "星期天";
        }
        log.error("error week {}", week);
        return "星期一";
    }

    public static Locale parseLocale(final String localeString) {
        if (localeString == null || localeString.length() == 0) {
            return null;
        }

        Locale locale = null;
        if (localeString.length() == 2) {
            // two letter language code
            locale = new Locale(localeString);
        } else if (localeString.length() == 5) {
            // positions 0-1 language, 3-4 are country
            String language = localeString.substring(0, 2);
            String country = localeString.substring(3, 5);
            locale = new Locale(language, country);
        } else if (localeString.length() > 6) {
            // positions 0-1 language, 3-4 are country, 6 and on are special
            // extensions
            String language = localeString.substring(0, 2);
            String country = localeString.substring(3, 5);
            String extension = localeString.substring(6);
            locale = new Locale(language, country, extension);
        } else {

            log.error("Do not know what to do with the localeString [{}], should be length 2, 5, or greater than 6, returning null", localeString);
        }

        return locale;
    }

    public static JFrame getComJFrame(Component com) {
        Container result;
        while (com != null) {
            result = com.getParent();
            if (result instanceof JFrame) {
                return (JFrame) result;
            } else {
                com = result;
            }
        }
        return null;
    }

    /**
     * 隐藏指定JTable的指定列
     *
     * @param table 指定JTable
     * @param column 指定列
     */
    public static void HiddenColumn(JTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
    }

    /**
     * 显示指定JTable的指定列
     *
     * @param table 指定JTable
     * @param column 指定列
     * @param width 指定列显示宽度
     */
    public static void showColumn(JTable table, int column, int width) {
        TableColumn tc = table.getColumnModel().getColumn(column);
        tc.setMaxWidth(width);
        tc.setPreferredWidth(width);
        tc.setWidth(width);
        tc.setMinWidth(width);
        table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(width);
        table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(width);
    }

    public static String evaluateExpr(String str) {

        if (str.startsWith("js:")) {
            try {
                return RunJs.evaluate(str.substring(3), null).toString();
            } catch (Exception ex) {
                log.error("evaluateExpr error", ex);
                throw new RuntimeException(ex);
            }
        } else {
            return str;
        }
    }

    public static Map<String, String> strToMap(final String str, final boolean trim, final String splite) {

        if (UtilValidate.isEmpty(str)) {
            return new HashMap<>();
        }
        Map<String, String> decodedMap = new HashMap<>();
        List<String> elements = split(str, splite);
        setToMap(decodedMap, elements, trim);
        return decodedMap;

    }

    /**
     * 分割字符串
     *
     * @param str 被分割字符串
     * @param delim 分割符号
     * @return
     */
    public static List<String> split(final String str, final String delim) {
        List<String> splitList = null;
        StringTokenizer st;

        if (str == null) {
            return splitList;
        }

        if (delim != null) {
            st = new StringTokenizer(str, delim);
        } else {
            st = new StringTokenizer(str);
        }

        splitList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            splitList.add(st.nextToken());
        }

        return splitList;
    }

    public static void setToMap(Map<String, String> decodedMap, Iterable<String> source, final boolean trim) {
        for (String s : source) {

            List<String> e = split(s, "=");

            if (e.size() != 2) {
                continue;
            }
            String name = (String) e.get(0);
            String value = (String) e.get(1);
            if (trim) {
                if (name != null) {
                    name = name.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
            }

            decodedMap.put(name, value);
        }

    }

    public static final boolean closeObjNoExc(final Closeable o) {
        if (o != null) {
            try {
                o.close();

            } catch (Exception e) {
                log.info("closeObjNoExc error ", e);
                log.warn("closeObjNoExc error :{}", e.toString());
                return false;
            }
        }
        return true;
    }

    public static final Properties getPropertiesFromRes(final String resourceName) {

        InputStream is = ConfigInfo.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }
        if (is == null) {
            log.error("找不到资源:{}", resourceName);
            return null;
        }

        Properties p;
        try {
            p = new Properties();

            p.loadFromXML(is);

            return p;

        } catch (IOException ex) {
            MiscUtil.closeObjNoExc(is);

            log.error("处理发生错误:{}", resourceName, ex);
            return null;

        } finally {
            MiscUtil.closeObjNoExc(is);
        }

    }

    public final static void writeString(OutputStream out, final Charset charset, final String value) throws IOException {

        final String nl = System.getProperty("line.separator");
        try (Writer writer = new OutputStreamWriter(out, charset)) {
            int r = 0;
            while (r < value.length()) {
                int i = value.indexOf('\n', r);
                if (i == -1) {
                    break;
                }
                writer.write(value.substring(r, i));
                writer.write(nl);
                r = i + 1;
            }
            writer.write(value.substring(r));
        }

    }

    public static String getResourceName(Class resClass, final String resName) {

        String posion = resClass.getPackage().getName();
        posion = posion.replace('.', '/');

        if (resName.charAt(0)=='/') {
            return posion.concat(resName);
        } else {
            return posion + "/" + resName;
        }

    }

}
