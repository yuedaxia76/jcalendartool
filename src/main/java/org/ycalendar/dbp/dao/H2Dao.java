package org.ycalendar.dbp.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.ycalendar.dbp.JdbcDbTransaction;
import org.ycalendar.util.UtilValidate;

public class H2Dao implements Executdb {

    public static final Logger log = LoggerFactory.getLogger(H2Dao.class);

    @Override
    public void begin() {
        localTran.begin();
    }

    @Override
    public void rollback() {
        localTran.rollback();
        localTran.release();
    }

    @Override
    public void commit() {
        localTran.commit();
        localTran.release();
    }

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

    public void close() {
        log.info("close   H2Dao");
        if (localTran != null) {
            localTran.close();
            localTran=null;
        }

    }

    private String delimiter = "#";

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public int loadSql(String sqlFile) {
        log.info( " load sql file {}", sqlFile);
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

                    if (one.indexOf(delimiter, one.length() - delimiter.length()) > -1) {
                        // 到分隔符了，一个完整的sql
                        one.delete(one.length() - delimiter.length(), one.length());

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
            log.warn( "loadSql  " + sqlFile + " error", e);
            log.error( "execuSql error  {}", e.toString());
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
            log.error( "exeQuery error", e);
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
            log.error( "exeTran error", e);
            throw new RuntimeException(e);
        } finally {
            localTran.release();
        }

    }
}
