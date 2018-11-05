package org.ycalendar.dbp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ycalendar.dbp.dao.BeanHandler;
import org.ycalendar.dbp.dao.BeanListHandler;
import org.ycalendar.dbp.dao.ExecuDbopention;
import org.ycalendar.dbp.dao.ExecueQuery;
import org.ycalendar.dbp.dao.ResultSetHandler;
import org.ycalendar.domain.DictionaryData;
import org.ycalendar.util.UtilValidate;

/**
 * 字典服务
 *
 * @author lenovo
 *
 */
public class DicService extends GenalService implements Dictionary {

    public static Logger log = Logger.getLogger(DicService.class.getName());

    @Override
    public <T> Map<T, String> getDictMap(String type, StringCinvert<T> sc) {
        List<DictionaryData> dics = getDictList(type);
        Map<T, String> result = new HashMap<>();
        for (DictionaryData d : dics) {
            result.put(sc.convert(d.getCode()), d.getDictdataValue());
        }
        return result;
    }

    @Override
    public List<DictionaryData> getDictList(final String type) {
        return hd.exeQuery(new ExecueQuery<List<DictionaryData>>() {
            public List<DictionaryData> exeDbAction() {

                String sql = "select * from dictionary_data where dict_type=? and local_str=? order by dict_order";
                BeanListHandler<DictionaryData> rsh = new BeanListHandler<>(DictionaryData.class);

                List<DictionaryData> tem = gdao.query(hd.getCurCnection(), sql, rsh, type, Locale.getDefault().toString());
                if (UtilValidate.isEmpty(tem)) {
                    log.log(Level.WARNING, "error dictionary type {0} no data", type);
                }
                return tem;

            }
        });

    }

    @Override
    public DictionaryData getDictDataByCode(String type, String code) {
        return hd.exeQuery(new ExecueQuery<DictionaryData>() {
            public DictionaryData exeDbAction() {

                String sql = "select * from dictionary_data where dict_type=? and code=? and local_str=?";
                ResultSetHandler<DictionaryData> rsh = new BeanHandler<>(DictionaryData.class);

                DictionaryData tem = gdao.query(hd.getCurCnection(), sql, rsh, type, code, Locale.getDefault().toString());
                if (tem == null) {
                    log.log(Level.WARNING, "error dic type {0} dic code :{1} no data", new Object[]{type, code});
                }
                return tem;

            }
        });
    }

    @Override
    public DictionaryData getDictDataByValue(String type, String value) {
        return hd.exeQuery(new ExecueQuery<DictionaryData>() {
            public DictionaryData exeDbAction() {

                String sql = "select * from dictionary_data where dict_type=? and dictdata_value=? and local_str=?";
                ResultSetHandler<DictionaryData> rsh = new BeanHandler<>(DictionaryData.class);

                DictionaryData tem = gdao.query(hd.getCurCnection(), sql, rsh, type, value, Locale.getDefault().toString());
                if (tem == null) {
                    log.log(Level.WARNING, "error dic type {0} dic value :{1} no data", new Object[]{type, value});
                }
                return tem;

            }
        });
    }

    @Override
    public String getDictCode(String type, String value) {
        DictionaryData d = getDictDataByValue(type, value);
        if (d != null) {
            return d.getCode();
        }
        return null;
    }

    @Override
    public String getDictValue(String type, String code) {
        DictionaryData d = getDictDataByCode(type, code);
        if (d != null) {
            return d.getDictdataValue();
        }
        return null;
    }

    public void insertDictionary(final DictionaryData d) {
        if (UtilValidate.isEmpty(d.getLocalStr())) {
            d.setLocalStr(Locale.getDefault().toString());
        }
        if (d.getDictOrder() == null) {
            d.setDictOrder(System.currentTimeMillis());
        }
        ExecuDbopention<Void> ei = () -> {
            gdao.create(hd.getCurCnection(), DictionaryData.class, d);
            return null;
        };
        hd.exeTran(ei);
    }

    public Integer delDictionary(final String id) {

        return hd.exeTran(() -> gdao.delete(hd.getCurCnection(), DictionaryData.class, "id", id));
    }

    public Integer delDictionaryByType(final String dicType) {

        return hd.exeTran(() -> gdao.delete(hd.getCurCnection(), DictionaryData.class, "dict_type", dicType));
    }
}
