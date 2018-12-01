package org.ycalendar.dbp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.ycalendar.util.UtilObject;
import org.ycalendar.util.UtilValidate;

public class JdbcDbTransaction {

    public static final Logger log = Logger.getLogger(JdbcDbTransaction.class.getName());

    private final DataSource key;

    private final String closeMothod;// 数据源关闭方法

    public JdbcDbTransaction(DataSource key, String closeMothod) {
        this.key = key;
        this.closeMothod = closeMothod;
    }

    public JdbcDbTransaction(DataSource key) {
        this(key, null);
    }

    public String getCloseMothod() {
        return closeMothod;
    }

//	public void setCloseMothod(String closeMothod) {
//		this.closeMothod = closeMothod;
//	}
    public void begin() {
        begin(null);
    }

    public void begin(Integer transactionIsolation) {

        if (key == null) {
            throw new GenericTransactionException("datasource is null");
        }
        if (ThreadLocalDbSession.hasResource(key)) {
            if (log.isLoggable(Level.INFO)) {
                log.log(Level.INFO, ("has bindResource count is  " + ThreadLocalDbSession.getResourceMap().get(key).count));
            }

        } else {

            Connection con;
            try {
                con = key.getConnection();
            } catch (SQLException e) {
                throw new GenericTransactionException(e.getMessage(), e);
            }
            ThreadLocalDbSession.bindResource(key, con);
            try {
                if (transactionIsolation != null) {
                    con.setTransactionIsolation(transactionIsolation.intValue());
                }
                con.setAutoCommit(false);
            } catch (SQLException e) {
                throw new GenericTransactionException(e.getMessage(), e);
            }
        }
        ThreadLocalDbSession.getResourceMap().get(key).count++;
    }

    public void commit() throws GenericTransactionException {
        if (checkAndDec()) {
            if (isRollBack()) {
                log.info("isRollBack only ,rollback");
                try {
                    ThreadLocalDbSession.currentSession(key).rollback();
                } catch (SQLException e) {
                    throw new GenericTransactionException(e.getMessage(), e);
                }
                return;
            }
            log.log(Level.FINE, "commit");
            try {
                ThreadLocalDbSession.currentSession(key).commit();
            } catch (SQLException e) {
                throw new GenericTransactionException(e.getMessage(), e);
            }
        } else {
            log.info("commit has activity transaction");
        }

    }

    private boolean isRollBack() {
        return ThreadLocalDbSession.getResourceMap().get(key).rollbackOnly;
    }

    public void release() throws GenericTransactionException {
        int count = ThreadLocalDbSession.getResourceMap().get(key).count;
        if (0 == count) {
            log.log(Level.FINE, "closeSession");
            try {
                ThreadLocalDbSession.closeSession(key).close();
            } catch (SQLException e) {
                log.warning("close session error:" + e.toString());
            }
        } else {
            log.info("not closeSession count is  " + count);
        }

    }

    public void rollback() throws GenericTransactionException {
        if (checkAndDec()) {
            try {
                ThreadLocalDbSession.currentSession(key).rollback();
            } catch (SQLException e) {
                throw new GenericTransactionException(e.getMessage(), e);
            }
        } else {
            ThreadLocalDbSession.getResourceMap().get(key).rollbackOnly = true;
            log.info("rollback has activity transaction, set rollbackOnly true");
        }

    }

    private boolean checkAndDec() {
        ThreadLocalDbSession.getResourceMap().get(key).count--;
        return 0 == ThreadLocalDbSession.getResourceMap().get(key).count;
    }

    /**
     * ÿ��ʵ�ֿ����Լ����� ����� ����Ϊconnection
     *
     * @return
     */
    public Connection getCurrentTransaction() {
        return ThreadLocalDbSession.currentSession(key);
    }

    public DataSource getKey() {
        return key;
    }

//	public void setKey(DataSource key) {
//		this.key = key;
//	}
    /**
     * 关闭数据源
     */
    public void close() {
        if (key != null) {
            if (UtilValidate.isNotEmpty(closeMothod)) {
                try {
                    UtilObject.runMethod(key, closeMothod, null);
                } catch (Exception e) {
                    log.log(Level.WARNING, "close dataSource error:{0}", e.toString());
                }

            } else {
                log.warning("closeMothod is empty");
            }
        }
    }

}
