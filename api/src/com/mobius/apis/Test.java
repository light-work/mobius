package com.mobius.apis;

import org.guiceside.commons.lang.DateFormatUtil;

import java.util.Date;

public class Test {

    public static void main(String as[]) throws Exception{
        System.out.println(DateFormatUtil.parse("2017-01-01 00:00:00",DateFormatUtil.YMDHMS_PATTERN).getTime());
        System.out.println(DateFormatUtil.format(new Date(1500076799999l
        ),DateFormatUtil.YMDHMS_PATTERN));
    }
}
