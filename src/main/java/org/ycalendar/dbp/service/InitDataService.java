package org.ycalendar.dbp.service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitDataService extends GenalService {

    private static Logger log = LoggerFactory.getLogger(InitDataService.class);

    public void loadData() {
        File flag = new File("alreadyLoad");
        final boolean flagex = flag.exists();

        log.info("alreadyLoad flag file path: {} file exists: {}", flag.getAbsolutePath(), flagex);

        if (!flagex) {
            String initData = "initdata/initData_" + Locale.getDefault().toString() + ".sql";

            if (Thread.currentThread().getContextClassLoader().getResource(initData) == null) {
                log.warn("no initData file {}", initData);
                initData = "initdata/initData" + ".sql";
            }
            log.info("load sql :{}", initData);
            int sqlcount = hd.loadSql(initData);

            log.info( "load sql item:{}", sqlcount);
            try {
                flag.createNewFile();
            } catch (IOException e) {
                log.error("create flag file" + flag + "error", e);

            }
        }
    }
}
