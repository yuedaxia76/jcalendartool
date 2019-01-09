package org.ycalendar;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;

import org.ycalendar.dbp.dao.GernDAO;
import org.ycalendar.dbp.dao.H2Dao;
import org.ycalendar.dbp.service.ConfigInfo;
import org.ycalendar.dbp.service.DicService;
import org.ycalendar.dbp.service.EventService;
import org.ycalendar.dbp.service.InitDataService;
import org.ycalendar.dbp.service.TaskService;
import org.ycalendar.ui.YCalendar;
import org.ycalendar.util.MiscUtil;

/**
 * 启动程序
 *
 */
public class JCalendar {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            YCalendar yc = new YCalendar();
            InitDataService ints = new InitDataService();
            yc.setDataLoad(ints);

            H2Dao hd = H2Dao.getH2Dao();
            ConfigInfo conInfo = new ConfigInfo();
            if (conInfo.isDebug()) {
                showClassPath();
            }

            if (conInfo.isRunInitDb()) {
                hd.loadSql("initdata/init.sql");
            }

            GernDAO dao = new GernDAO();

            ints.setGdao(dao);
            ints.setHd(hd);

            EventService es = new EventService();
            es.setGdao(dao);
            es.setHd(hd);
            es.setConInfo(conInfo);

            DicService dic = new DicService();
            dic.setGdao(dao);
            dic.setHd(hd);

            TaskService ts = new TaskService();
            ts.setGdao(dao);
            ts.setHd(hd);
            ts.setConInfo(conInfo);

            yc.setConInfo(conInfo);
            yc.setEvSe(es);
            yc.setDicSer(dic);
            yc.setTsSe(ts);

            yc.initMainUi();
        });

    }

    public static void showClassPath() {
        final String nl = System.getProperty("line.separator");
            StringBuilder tem = new StringBuilder(1024);

            tem.append("root:").append(ClassLoader.getSystemResource("")).append(nl);
            tem.append("app class root:").append(JCalendar.class.getClassLoader().getResource("")).append(nl);
            tem.append("app class root:").append(JCalendar.class.getResource("/")).append(nl);
            tem.append("app class root:").append(JCalendar.class.getResource("")).append(nl);       // 获得当前类所在路径
            tem.append("user.dir:").append(System.getProperty("user.dir")).append(nl);              // 获得项目根目录的绝对路径
            tem.append("java.class.path:").append(System.getProperty("java.class.path")).append(nl);   //得到类路径和包路径
            
            
        try (FileOutputStream out = new FileOutputStream("debugInfo.txt")){
            


            MiscUtil.writeString(out, StandardCharsets.UTF_8, tem.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
