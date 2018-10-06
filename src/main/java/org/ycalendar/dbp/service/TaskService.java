package org.ycalendar.dbp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ycalendar.dbp.dao.BeanListHandler;
import org.ycalendar.dbp.dao.ExecuDbopention;
import org.ycalendar.dbp.dao.ExecueQuery;
import org.ycalendar.dbp.dao.PreparedStatementHandler;
import org.ycalendar.domain.TaskData;
import org.ycalendar.util.MiscUtil;
import org.ycalendar.util.UtilValidate;

/**
 * 任务管理
 *
 * @author lenovo
 *
 */
public class TaskService extends GenalService {

    private ConfigInfo conInfo;

    public ConfigInfo getConInfo() {
        return conInfo;
    }

    public void setConInfo(ConfigInfo conInfo) {
        this.conInfo = conInfo;
    }

    /**
     * 创建任务
     *
     * @param td
     */
    public void createTask(TaskData td) {

        hd.exeTran(new ExecuDbopention<Void>() {
            @Override
            public Void exeDbAction() {
                if (UtilValidate.isEmpty(td.getTaskid())) {
                    td.setTaskid(MiscUtil.getId());
                }
                if (UtilValidate.isEmpty(td.getCalendarid())) {
                    td.setCalendarid(conInfo.getDefaultCalId());
                }
                if (Long.MIN_VALUE == td.getCreateTime()) {
                    long s = System.currentTimeMillis();
                    td.setCreateTime(s);
                    td.setLastChangeTime(s);
                }                
                gdao.create(hd.getCurCnection(), TaskData.class, td);
                return null;
            }
        });

    }

    public TaskData saveOrUpdate(TaskData td) {
        if (UtilValidate.isEmpty(td.getTaskid())) {
            createTask(td);
            return td;
        } else {
            if (this.readTask(td.getTaskid()) == null) {
                createTask(td);
            } else {
                updateTask(td);
            }

            return td;
        }
    }

    /**
     * 修改任务
     *
     * @param td
     * @return
     */
    public Integer updateTask(TaskData td) {
        if (UtilValidate.isEmpty(td.getTaskid())) {
            throw new RuntimeException("缺少主键");
        }
        return hd.exeTran(new ExecuDbopention<Integer>() {
            @Override
            public Integer exeDbAction() {
                td.setLastChangeTime(System.currentTimeMillis());
                return gdao.update(hd.getCurCnection(), TaskData.class, td, "taskid", false);

            }
        });

    }

    public Integer delTask(String taskid) {

        return hd.exeTran(new ExecuDbopention<Integer>() {
            @Override
            public Integer exeDbAction() {
                return gdao.delete(hd.getCurCnection(), TaskData.class, "taskid", taskid);
            }
        });

    }

    public TaskData readTask(String taskid) {
        if (UtilValidate.isEmpty(taskid)) {
            throw new RuntimeException("缺少主键");
        }
        return hd.exeTran(new ExecuDbopention<TaskData>() {
            @Override
            public TaskData exeDbAction() {
                return gdao.read(hd.getCurCnection(), TaskData.class, "taskid", taskid);

            }
        });

    }

    /**
     * 获取没有结束任务状态列表
     *
     * @return
     */
    public List<String> getNotcompleteStatus() {
        return Arrays.asList(getConInfo().getDefaultTaskStatus(), "IN-PROCESS", "NEEDS-ACTION");
    }

    public List<TaskData> queryTask(List<String> status, long start, long end, List<String> calendarids, String title, int percentage) {

        return hd.exeQuery(new ExecueQuery<List<TaskData>>() {
            @Override
            public List<TaskData> exeDbAction() {
                BeanListHandler<TaskData> rsh = new BeanListHandler<>(TaskData.class);
                List<String> condition = new ArrayList<>(6);
                List<Object> params = new ArrayList<>(6);
                if (UtilValidate.isEmpty(calendarids) || calendarids.size() == 1) {
                    condition.add(" calendarid=? ");
                    params.add(UtilValidate.isEmpty(calendarids) ? conInfo.getDefaultCalId() : calendarids.get(0));
                } else {
                    condition.add(" calendarid " + PreparedStatementHandler.generateQuestionMarks(calendarids.size()));
                    params.addAll(calendarids);
                }
                if (UtilValidate.isEmpty(status) || status.size() == 1) {
                    condition.add(" tstatus=? ");
                    params.add(UtilValidate.isEmpty(status) ? conInfo.getDefaultTaskStatus() : status.get(0));
                } else {
                    condition.add(" tstatus " + PreparedStatementHandler.generateQuestionMarks(status.size()));
                    params.addAll(status);
                }
                if (UtilValidate.isNotEmpty(title)) {
                    condition.add(" (title like ?  or taskdesc like ?) ");
                    String likeCon = '%' + title + '%';
                    params.add(likeCon);
                    params.add(likeCon);
                }
                if (start > -1) {
                    condition.add(" start_time>=? ");
                    params.add(start);
                }
                if (end > -1) {
                    condition.add(" end_time<=? ");
                    params.add(end);
                }
                if (percentage > -1) {
                    condition.add(" percentage=? ");
                    params.add(percentage);
                }
                StringBuffer sql = new StringBuffer("select * from task_data ");
                if (condition.size() > 0) {
                    sql.append(" where ");
                }
                for (Iterator<String> it = condition.iterator(); it.hasNext();) {

                    sql.append(it.next());
                    if (it.hasNext()) {
                        sql.append(" and ");
                    }

                }
                sql.append("  order by create_time");
                return gdao.query(hd.getCurCnection(), sql, rsh, params);

            }
        });

    }
}
