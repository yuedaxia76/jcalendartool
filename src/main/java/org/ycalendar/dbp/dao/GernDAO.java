package org.ycalendar.dbp.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ycalendar.util.UtilValidate;

/**
 * 如果仅仅考虑h2，可以作为静态类
 */
public class GernDAO {

    private static final Logger log = LoggerFactory.getLogger(GernDAO.class.getName());
    private final PreparedStatementHandler psh;

    public GernDAO() {

        this.psh = PreparedStatementHandler.getInstance();

    }

    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, List<Object> params) {
        return this.query(conn, sql, rsh, params == null ? new Object[0] : params.toArray(new Object[params.size()]));
    }

    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        T result = null;
        try {
            psh.adjustParams(params.length, params);
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params.length, params);
            rs = stmt.executeQuery();
            result = rsh.handle(rs);
        } catch (SQLException e) {
            psh.print(sql, params.length, params);
            throw new RuntimeException(e);
        } finally {
            close(rs, stmt);
        }
        return result;
    }

    public int update(Connection conn, StringBuffer sql, List<Object> params) {
        return this.update(conn, sql.toString(), params.toArray(new Object[]{}));
    }

    public int update(Connection conn, String sql, Object... params) {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            psh.adjustParams(params.length, params);
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params.length, params);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
            psh.print(sql, params.length, params);
            throw new RuntimeException(e);
        } finally {
            close(stmt);
        }
        return rows;
    }

    public int[] batch(Connection conn, String sql, Object[][] params) {
        PreparedStatement stmt = null;
        int[] rows = null;
        try {

            //psh.adjustParams(params[0].length,params[0]);
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                psh.adjustParams(params[i].length, params[i]);
                this.fillStatement(stmt, params[i].length, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(stmt);
        }
        return rows;
    }

    public <T> int create(Connection conn, Class<T> cls, T bean) {
        int rows = 0;
        PreparedStatement stmt = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            String table = PreparedStatementHandler.camel2underscore(cls.getSimpleName());
            StringBuilder columns = new StringBuilder(64), questionMarks = new StringBuilder(64);

            Object[] params = new Object[pds.length];

            int j = 0;
            for (PropertyDescriptor pd : pds) {
                Method getter = pd.getReadMethod();
                String name = pd.getName();
                Object value = getter.invoke(bean);
                if (value == null) {
                    continue;
                }

                columns.append(PreparedStatementHandler.camel2underscore(name)).append(',');
                questionMarks.append("?,");
                params[j] = value;
                j++;

            }
            columns.delete(columns.length() - 1, columns.length());
            questionMarks.delete(questionMarks.length() - 1, questionMarks.length());
            String sql = String.format("insert into %s (%s) values (%s)", table, columns.toString(), questionMarks.toString());

            psh.adjustParams(j, params);

            stmt = conn.prepareStatement(sql);

            this.fillStatement(stmt, j, params);
            try {
                rows = stmt.executeUpdate();
            } catch (SQLException e) {
                psh.print(sql, j, params);
                throw new RuntimeException(e);
            }

        } catch (IllegalArgumentException | IllegalAccessException | SQLException | IntrospectionException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            close(stmt);
        }
        return rows;
    }

    public <T> int[] create(Connection conn, Class<T> cls, List<T> beans) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(cls, Object.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        // build SQL
        String table = PreparedStatementHandler.camel2underscore(cls.getSimpleName());
        StringBuilder columns = new StringBuilder(64), questionMarks = new StringBuilder(64);

        for (PropertyDescriptor pd : pds) {
            String name = pd.getName();

            columns.append(PreparedStatementHandler.camel2underscore(name)).append(',');
            questionMarks.append("?,");

        }
        columns.delete(columns.length() - 1, columns.length());
        questionMarks.substring(questionMarks.length() - 1, questionMarks.length());
        String sql = String.format("insert into %s (%s) values (%s)", table, columns.toString(), questionMarks.toString());

        // build parameters */
        int rows = beans.size();
        int cols = pds.length;

        Object[][] params = new Object[rows][cols];
        try {
            for (int i = 0; i < rows; i++) {
                int j = 0;
                for (PropertyDescriptor pd : pds) {
                    Method getter = pd.getReadMethod();

                    Object value = getter.invoke(beans.get(i));

                    params[i][j] = value;
                    j++;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        // execute
        return batch(conn, sql, params);
    }

    public <T> T read(Connection conn, Class<T> cls, String column, String id) {
        String table = PreparedStatementHandler.camel2underscore(cls.getSimpleName());

        String sql = String.format("select * from %s where %s=?", table, column);
        return (T) query(conn, sql, new BeanHandler<T>(cls), id);
    }

    public <T> int update(Connection conn, Class<T> cls, T bean, String primaryKey, final boolean setNull) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            List<Object> params = new ArrayList<>(pds.length);
            primaryKey = PreparedStatementHandler.underscore2camel(primaryKey);
            Object id = 0;
            StringBuilder columnAndQuestionMarks = new StringBuilder(64);

            for (PropertyDescriptor pd : pds) {
                Method getter = pd.getReadMethod();
                String name = pd.getName();
                Object value = getter.invoke(bean);
                if (name.equals(primaryKey)) {
                    id = value;
                } else {
                    if (setNull || value != null) {
                        columnAndQuestionMarks.append(PreparedStatementHandler.camel2underscore(name)).append("=?,");
                        params.add(value);
                    }

                }
            }
            params.add(id);
            String table = PreparedStatementHandler.camel2underscore(cls.getSimpleName());
            columnAndQuestionMarks.delete(columnAndQuestionMarks.length() - 1, columnAndQuestionMarks.length());
            String sql = String.format("update %s set %s where %s = ?", table, columnAndQuestionMarks, PreparedStatementHandler.camel2underscore(primaryKey));
            return update(conn, sql, params.toArray(new Object[params.size()]));
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> int[] update(Connection conn, Class<T> cls, List<T> beans, String primaryKey) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            primaryKey = PreparedStatementHandler.underscore2camel(primaryKey);
            StringBuilder columnAndQuestionMarks = new StringBuilder();

            for (PropertyDescriptor pd : pds) {
                String name = pd.getName();
                if (name.equals(primaryKey)) {
                } else {
                    columnAndQuestionMarks.append(PreparedStatementHandler.camel2underscore(name)).append("=?,");
                }
            }
            String table = PreparedStatementHandler.camel2underscore(cls.getSimpleName());
            columnAndQuestionMarks.delete(columnAndQuestionMarks.length() - 1, columnAndQuestionMarks.length());
            String sql = String.format("update %s set %s where %s = ?", table, columnAndQuestionMarks, PreparedStatementHandler.camel2underscore(primaryKey));

            // build parameters
            int rows = beans.size();
            int cols = pds.length;
            Object id = 0;
            Object[][] params = new Object[rows][cols];
            for (int i = 0; i < rows; i++) {
                int j = 0;
                for (PropertyDescriptor pd : pds) {
                    Method getter = pd.getReadMethod();
                    String name = pd.getName();
                    Object value = getter.invoke(beans.get(i));
                    if (name.equals(primaryKey)) {
                        id = value;
                    } else {
                        params[i][j] = value;
                        j++;
                    }
                }
                params[i][j] = id;
            }
            return batch(conn, sql, params);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> int delete(Connection conn, Class<T> cls, String column, String id) {
        String sql = String.format("delete from %s where %s=?", PreparedStatementHandler.camel2underscore(cls.getSimpleName()), column);
        return update(conn, sql, new Object[]{id});
    }

    public <T> int delete(Connection conn, Class<T> cls, Map<String, Object> cond) {
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(PreparedStatementHandler.camel2underscore(cls.getSimpleName()));
        if (UtilValidate.isNotEmpty(cond)) {
            Object[] param = new Object[cond.size()];
            sql.append(" where ");
            int i = 0;
            for (Map.Entry<String, Object> en : cond.entrySet()) {
                if (i > 0) {
                    sql.append(" and ");
                }
                sql.append(en.getKey()).append("=? ");

                param[i] = en.getValue();
                i++;
            }
            return update(conn, sql.toString(), param);
        } else {
            return update(conn, sql.toString(), new Object[0]);
        }

    }

    public void pager(StringBuffer sql, List<Object> params, int pageSize, int pageNo) {
        psh.pager(sql, params, pageSize, pageNo);
    }

    /**
     * 拼带in条件sql
     *
     * @param sql
     * @param params 参数
     * @param operator and /or /where
     * @param field 条件字段
     * @param values 条件值
     */
    public <T> void in(StringBuilder sql, List<Object> params, String operator, String field, List<T> values) {
        psh.in(sql, params, operator, field, values);
    }

    private void fillStatement(PreparedStatement stmt, final int len, Object... params) {
        if (params == null) {
            return;
        }
        try {
            for (int i = 0; i < len; i++) {

                if (params[i] == null) {
                    stmt.setNull(i + 1, Types.VARCHAR);

                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execuSql(Connection conn, String sql) {
        try (Statement st = conn.createStatement()) {
            log.debug(sql);
            st.execute(sql);

        } catch (IllegalArgumentException | SQLException e) {
            log.error( "execuSql error, sql :{}", sql);
            throw new RuntimeException(e);
        }
    }

    protected void close(ResultSet rs, Statement stmt) {

        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }

    }

    protected void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }

    }

}
