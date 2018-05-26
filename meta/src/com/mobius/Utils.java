package com.mobius;

import ognl.NoSuchPropertyException;
import org.guiceside.commons.lang.BeanUtils;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date parseDateForZ(String timeStr) {
        timeStr = timeStr.replace("Z", " UTC");//UTC是本地时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date d = null;
        try {
            d = format.parse(timeStr);
            d = DateFormatUtil.addDay(d, -1);
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

    public static void bind(Object entity,String by) throws Exception {
        if (entity instanceof Tracker) {
            BeanUtils.setValue(entity, "created", DateFormatUtil.getCurrentDate(true));
            BeanUtils.setValue(entity, "updated", DateFormatUtil.getCurrentDate(true));
            BeanUtils.setValue(entity, "createdBy", StringUtils.defaultIfEmpty(by,"sys"));
            BeanUtils.setValue(entity, "updatedBy", StringUtils.defaultIfEmpty(by,"sys"));
        }
        try {
            String useYn = BeanUtils.getValue(entity, "useYn", String.class);
            if (StringUtils.isBlank(useYn)) {
                BeanUtils.setValue(entity, "useYn", "Y");
            }
        } catch (NoSuchPropertyException e) {
            BeanUtils.setValue(entity, "useYn", "Y");
        }
    }
}

