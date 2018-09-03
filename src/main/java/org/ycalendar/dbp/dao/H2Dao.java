package org.ycalendar.dbp.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.ycalendar.dbp.JdbcDbTransaction;
import org.ycalendar.util.UtilValidate;

public class H2Dao implements Executdb {

    public static final Logger log = Logger.getLogger(H2Dao.class.getName());

    static class H2daoRef {

        private static final H2Dao h2r = new H2Dao();

    }

    public static H2Dao getH2Dao() {
        return H2daoRef.h2r;
    }

    protected JdbcDbTransaction localTran;

    protected H2Dao() {
        super();
        initSetUp();
    }

    protected void initSetUp() {
        log.info("start init H2Dao");
        localTran = createJdbcDbTransaction(getMainUrl());

    }

    @Override
    public int loadSql(String sqlFile) {
        log.log(Level.INFO, "    load sql file {0}", sqlFile);
        int result = 0;
        localTran.begin();

        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            List<String> multSql = new ArrayList<>();
            StringBuilder one = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream(sqlFile), StandardCharsets.UTF_8));) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 去掉头尾空格
                    line = line.trim();
                    // 忽略注释
                    if (UtilValidate.isEmpty(line) || line.startsWith("//")) {

                        continue;
                    }

                    // 多个行组成一个sql，以分隔符分开
                    one.append(' ').append(line);

                    if (one.indexOf(";", one.length() - 1) > -1) {
                        // 到分隔符了，一个完整的sql
                        one.deleteCharAt(one.length() - 1);

                        multSql.add(one.toString());
                        one.setLength(0);
                    }
                }
            }

            for (String sql : multSql) {
                log.info(sql);
                execuSql(localTran.getCurrentTransaction(), sql);
                result++;
            }

            localTran.commit();
        } catch (Exception e) {
            log.log(Level.WARNING, "loadSql  " + sqlFile + " error", e);
            log.log(Level.SEVERE, "execuSql error  {0}", e.toString());
            localTran.rollback();
        } finally {
            localTran.release();
        }
        return result;
    }

    private void execuSql(Connection conn, String sql) {
        try (Statement st = conn.createStatement()) {

            st.execute(sql);

        } catch (IllegalArgumentException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String dbUrl = "jdbc:h2:./calendardb";

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    private String getMainUrl() {
        return dbUrl;
    }

    protected JdbcDbTransaction createJdbcDbTransaction(String url) {
        JdbcDbTransaction result = new JdbcDbTransaction(getDataSource(url), "dispose");

        return result;
    }

    protected DataSource getDataSource(String url) {

        JdbcConnectionPool cp = JdbcConnectionPool.create(url, "sa", "");

        return cp;
    }

    /* (non-Javadoc)
	 * @see org.ycalendar.dbp.dao.Executdb#exeQuery(org.ycalendar.dbp.dao.ExecueQuery)
     */
    @Override
    public final <T> T exeQuery(ExecueQuery<T> dba) {

        localTran.begin();
        try {
            T result = dba.exeDbAction();
            localTran.commit();
            return result;
        } catch (Exception e) {

            localTran.rollback();
            log.log(Level.SEVERE, "exeQuery error", e);
            throw new RuntimeException(e);

        } finally {
            localTran.release();
        }

    }

    @Override
    public final Connection getCurCnection() {
        return localTran.getCurrentTransaction();
    }

    /* (non-Javadoc)
	 * @see org.ycalendar.dbp.dao.Executdb#exeTran(org.ycalendar.dbp.dao.ExecuDbopention)
     */
    @Override
    public final <T> T exeTran(ExecuDbopention<T> dba) {

        localTran.begin();
        try {
            T result = dba.exeDbAction();
            localTran.commit();
            return result;
        } catch (Exception e) {

            localTran.rollback();
            log.log(Level.SEVERE, "exeTran error", e);
            throw new RuntimeException(e);
        } finally {
            localTran.release();
        }

    }
}
