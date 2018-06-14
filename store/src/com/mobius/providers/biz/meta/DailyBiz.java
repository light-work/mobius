package com.mobius.providers.biz.meta;

import com.mobius.common.BizException;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;

import java.util.Date;


public interface DailyBiz {

    String dailyForOkex(SpotSymbol spotSymbol, SysTrade sysTrade,String releaseEnvironment,Date dailyDate) throws BizException;

    String dailyForHuobiPro(SpotSymbol spotSymbol,SysTrade sysTrade,String releaseEnvironment,Date dailyDate) throws BizException;

    String dailyForBitfinex(SpotSymbol spotSymbol,SysTrade sysTrade,String releaseEnvironment,Date dailyDate) throws BizException;

    String dailyForBitmex(SpotSymbol spotSymbol,SysTrade sysTrade,String releaseEnvironment,Date dailyDate) throws BizException;

    String dailyForBinance(SpotSymbol spotSymbol,SysTrade sysTrade,String releaseEnvironment,Date dailyDate) throws BizException;

}
