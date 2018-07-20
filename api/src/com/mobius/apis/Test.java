package com.mobius.apis;

import net.sf.json.JSONObject;
import org.guiceside.commons.lang.DateFormatUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String as[]) throws Exception{

//        String dateStr="2018-01-31";
//        Date d=DateFormatUtil.parse(dateStr,DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
//        d=DateFormatUtil.addMonth(d,-1);
//        int y=DateFormatUtil.getDayInYear(d);
//        int m=DateFormatUtil.getDayInMonth(d)-1;
        System.out.println(new BigDecimal(Double.parseDouble("6.789E-5")));


        Date weightDate=DateFormatUtil.getCurrentDate(false);
        weightDate=DateFormatUtil.addMonth(weightDate,-1);
        Integer weightYear=DateFormatUtil.getDayInYear(weightDate);
        Integer weightMonth=DateFormatUtil.getDayInMonth(weightDate)+1;
        System.out.println(weightYear+"   "+weightMonth);
        System.out.println(DateFormatUtil.parse("2018-05-21 00:00:00",DateFormatUtil.YMDHMS_PATTERN).getTime());
        System.out.println(DateFormatUtil.format(new Date(1527292800000l
        ),DateFormatUtil.YMDHMS_PATTERN));

        TimeUnit.SECONDS.sleep(5);//秒

        String time = "2018-05-25T00:00:00.000Z";
        time = time.replace("Z", " UTC");//UTC是本地时间
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date d = null;
        try {
            d = format.parse(time);
            System.out.println(DateFormatUtil.format(d,DateFormatUtil.YMDHMS_PATTERN));
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        //2017-01-01T00:00:00.000Z
    }
}
