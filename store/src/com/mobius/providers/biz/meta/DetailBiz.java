package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;
import com.mobius.entity.sys.SysIpServer;

import java.util.Date;

public interface DetailBiz {

    String dailyForOkex(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer,String releaseEnvironment) throws BizException;

    String dailyForHuobiPro(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer,String releaseEnvironment) throws BizException;

    String dailyForBitfinex(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer,String releaseEnvironment) throws BizException;

    String dailyForBinance(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer,String releaseEnvironment) throws BizException;

}
