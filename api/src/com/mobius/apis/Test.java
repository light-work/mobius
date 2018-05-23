package com.mobius.apis;

import org.guiceside.commons.lang.DateFormatUtil;

import java.util.Date;

public class Test {

    public static void main(String as[]) throws Exception{
        System.out.println(DateFormatUtil.format(new Date(1527048000000l),DateFormatUtil.YMDHMS_PATTERN));
    }
}
