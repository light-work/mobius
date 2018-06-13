package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.common.BizException;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.biz.meta.DailyBiz;
import com.mobius.providers.biz.meta.DetailBiz;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import com.mobius.providers.store.sys.SysTradeStore;
import com.mobius.task.detail.FastCallForBinance;
import com.mobius.task.detail.FastCallForBitfinex;
import com.mobius.task.detail.FastCallForHuobi;
import com.mobius.task.detail.FastCallForOkex;
import net.sf.json.JSONObject;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.*;

/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class DetailBizImp extends BaseBiz implements DetailBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;


    @Override
    public String dailyForOkex(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            if (sysTradeStore != null && calSampleSpotSymbolWeightStore != null) {
                SysTrade sysTradeOKEX = sysTradeStore.getBySign("OKEX");
                if (sysTradeOKEX != null) {
                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                            sysTradeOKEX.getId(), market, sysIpServer.getServerNo());
                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                            SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                            if (spotSymbol != null) {
                                FastCallForOkex.callSpotUsdt(calSampleSpotSymbolWeight, spotSymbol, hsfServiceFactory, sysTradeOKEX, date);
                                resultObj.put("result", "0");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String dailyForHuobiPro(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            if (sysTradeStore != null && calSampleSpotSymbolWeightStore != null) {
                SysTrade sysTradeHuoBi = sysTradeStore.getBySign("HUOBIPRO");
                if (sysTradeHuoBi != null) {
                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                            sysTradeHuoBi.getId(), market, sysIpServer.getServerNo());
                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                            SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                            if (spotSymbol != null) {
                                FastCallForHuobi.callUsdt(calSampleSpotSymbolWeight, spotSymbol, hsfServiceFactory, sysTradeHuoBi, date);
                                resultObj.put("result", "0");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();

    }

    @Override
    public String dailyForBitfinex(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            if (sysTradeStore != null && calSampleSpotSymbolWeightStore != null) {

                SysTrade sysTradeBITFINEX = sysTradeStore.getBySign("BITFINEX");
                if (sysTradeBITFINEX != null) {
                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                            sysTradeBITFINEX.getId(), market, sysIpServer.getServerNo());
                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                        List<SpotSymbol> spotSymbolList = new ArrayList<>();
                        Map<String, CalSampleSpotSymbolWeight> sampleSpotSymbolWeightMap = new HashMap<>();
                        Map<String, SpotSymbol> spotSymbolHashMap = new HashMap<>();

                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                            try {
                                SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                if (spotSymbol != null) {
                                    spotSymbolList.add(spotSymbol);
                                    sampleSpotSymbolWeightMap.put("t" + spotSymbol.getSymbol().toUpperCase(), calSampleSpotSymbolWeight);
                                    spotSymbolHashMap.put("t" + spotSymbol.getSymbol().toUpperCase(), spotSymbol);
                                }
                            } catch (Exception e) {

                            }
                        }
                        try {
                            FastCallForBitfinex.call(sampleSpotSymbolWeightMap, spotSymbolHashMap, sysTradeBITFINEX, spotSymbolList, hsfServiceFactory, date);
                            resultObj.put("result", "0");
                        } catch (Exception e) {

                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();


    }

    @Override
    public String dailyForBinance(Integer weightYear, Integer weightMonth, Date date, String market, SysIpServer sysIpServer) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            if (sysTradeStore != null && calSampleSpotSymbolWeightStore != null) {
                SysTrade sysTradeBinance = sysTradeStore.getBySign("BINANCE");
                if (sysTradeBinance != null) {
                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                            sysTradeBinance.getId(), market, sysIpServer.getServerNo());
                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                            try {
                                SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                if (spotSymbol != null) {
                                    FastCallForBinance.call(calSampleSpotSymbolWeight, sysTradeBinance, spotSymbol, hsfServiceFactory, date);
                                    resultObj.put("result", "0");
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

}
