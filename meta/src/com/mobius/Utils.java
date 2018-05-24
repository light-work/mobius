package com.mobius;

import org.guiceside.commons.lang.DateFormatUtil;

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
            System.out.println(DateFormatUtil.format(d, DateFormatUtil.YMDHMS_PATTERN));
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }
}

