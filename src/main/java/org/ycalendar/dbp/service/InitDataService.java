package org.ycalendar.dbp.service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitDataService extends GenalService {

    public static Logger log = Logger.getLogger(InitDataService.class.getName());

    public void loadData() {
        File flag = new File("alreadyLoad");
        final boolean flagex = flag.exists();
        if (log.isLoggable(Level.INFO)) {
            log.log(Level.INFO, "alreadyLoad flag file path: {0} file exists: {1}", new Object[]{flag.getAbsolutePath(), flagex});
        }

        if (!flagex) {
            String initData = "initdata/initData_" + Locale.getDefault().toString() + ".sql";

            if (Thread.currentThread().getContextClassLoader().getResource(initData) == null) {
                log.log(Level.WARNING, "no initData file {0}", initData);
                initData = "initdata/initData" + ".sql";
            }
            log.log(Level.INFO, "load sql :{0}", initData);
            int sqlcount = hd.loadSql(initData);

            log.log(Level.INFO, "load sql item:{0}", sqlcount);
            try {
                flag.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "create flag file" + flag + "error", e);

            }
        }
    }
}
