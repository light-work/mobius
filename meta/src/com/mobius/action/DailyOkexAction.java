package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.futures.FuturesDailyBtc;
import com.mobius.entity.futures.FuturesDailyEth;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesDailyBtcStore;
import com.mobius.providers.store.futures.FuturesDailyEthStore;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Action(name = "okex", namespace = "/daily")
public class DailyOkexAction extends BaseAction {

    private static String tradeSign="OKEX";


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }


    public String buildSpotUsdt() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                        if (spotSymbolStore != null&&spotDailyUsdtStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "usdt");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyUsdt> dailyUsdtList=new ArrayList<>();
                                                for (int x = 0; x < klineArray.size()-1; x++) {
                                                    JSONArray dayAttr =  klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice = dayAttr.getDouble(4);
                                                        Double volume = dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);
                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
                                                        SpotDailyUsdt spotDailyUsdt=new SpotDailyUsdt();
                                                        spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyUsdt.setTradeId(sysTrade);
                                                        spotDailyUsdt.setSymbolId(spotSymbol);
                                                        spotDailyUsdt.setTradingDay(timeDate);
                                                        spotDailyUsdt.setLastPrice(lastPrice);
                                                        spotDailyUsdt.setVolume(volume);
                                                        spotDailyUsdt.setTurnover(NumberUtils.multiply(volume,lastPrice,8));
                                                        spotDailyUsdt.setCreatedBy("batch");
                                                        spotDailyUsdt.setCreated(new Date());
                                                        dailyUsdtList.add(spotDailyUsdt);
                                                    }
                                                }
                                                spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------OKex buildSpotUsdt  running over----");
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String buildSpotBtc() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
                        if (spotSymbolStore != null&&spotDailyBtcStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "btc");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyBtc> dailyBtcList=new ArrayList<>();
                                                for (int x = 0; x < klineArray.size()-1; x++) {
                                                    JSONArray dayAttr =  klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice = dayAttr.getDouble(4);
                                                        Double volume = dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);
                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
                                                        SpotDailyBtc spotDailyBtc=new SpotDailyBtc();
                                                        spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyBtc.setTradeId(sysTrade);
                                                        spotDailyBtc.setSymbolId(spotSymbol);
                                                        spotDailyBtc.setTradingDay(timeDate);
                                                        spotDailyBtc.setLastPrice(lastPrice);
                                                        spotDailyBtc.setVolume(volume);
                                                        spotDailyBtc.setTurnover(NumberUtils.multiply(volume,lastPrice,8));
                                                        spotDailyBtc.setCreatedBy("batch");
                                                        spotDailyBtc.setCreated(new Date());
                                                        dailyBtcList.add(spotDailyBtc);
                                                    }
                                                }
                                                spotDailyBtcStore.save(dailyBtcList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyBtcList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------OKex buildSpotBtc  running over----");
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }



    public String buildSpotEth() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
                        if (spotSymbolStore != null&&spotDailyEthStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "eth");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyEth> dailyEthList=new ArrayList<>();
                                                for (int x = 0; x < klineArray.size()-1; x++) {
                                                    JSONArray dayAttr =  klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice = dayAttr.getDouble(4);
                                                        Double volume = dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);
                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
                                                        SpotDailyEth spotDailyEth=new SpotDailyEth();
                                                        spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyEth.setTradeId(sysTrade);
                                                        spotDailyEth.setSymbolId(spotSymbol);
                                                        spotDailyEth.setTradingDay(timeDate);
                                                        spotDailyEth.setLastPrice(lastPrice);
                                                        spotDailyEth.setVolume(volume);
                                                        spotDailyEth.setTurnover(NumberUtils.multiply(volume,lastPrice,8));
                                                        spotDailyEth.setCreatedBy("batch");
                                                        spotDailyEth.setCreated(new Date());
                                                        dailyEthList.add(spotDailyEth);
                                                    }
                                                }
                                                spotDailyEthStore.save(dailyEthList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyEthList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------OKex buildSpotEth  running over----");
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public String buildFuturesUsdt() throws Exception{
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        FuturesSymbolStore storeConsumer = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                        FuturesDailyUsdtStore dailyUsdtStore = hsfServiceFactory.consumer(FuturesDailyUsdtStore.class);
                        if (storeConsumer != null&&dailyUsdtStore!=null) {
                            List<FuturesSymbol> symbolList = storeConsumer.getListByTradeMarket(sysTrade.getId(), "usdt");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");

                                for (FuturesSymbol symbol : symbolList) {
                                    params.put("symbol", symbol.getSymbol());
                                    params.put("contract_type", symbol.getSymbolDesc());
                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/future_kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty() && klineArray.isArray()) {
                                                List<FuturesDailyUsdt> dailyUsdtList=new ArrayList<>();
                                                for (int x=0 ;x<klineArray.size()-1;x++ ) {
                                                    JSONArray dayAttr =  klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice=dayAttr.getDouble(4);
                                                        Double volume=dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);

                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);

                                                        FuturesDailyUsdt dailyUsdt=new FuturesDailyUsdt();
                                                        dailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        dailyUsdt.setTradeId(sysTrade);
                                                        dailyUsdt.setSymbolId(symbol);
                                                        dailyUsdt.setTradingDay(timeDate);
                                                        dailyUsdt.setLastPrice(lastPrice);
                                                        dailyUsdt.setTurnover(NumberUtils.multiply(volume,lastPrice,8));
                                                        dailyUsdt.setVolume(volume);
                                                        dailyUsdt.setCreatedBy("batch");
                                                        dailyUsdt.setCreated(new Date());
                                                        dailyUsdtList.add(dailyUsdt);
                                                    }
                                                }
                                                dailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(symbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------OKex buildFuturesUsdt  running over----");
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String buildFuturesBtc()throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        FuturesSymbolStore storeConsumer = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                        FuturesDailyBtcStore dailyBtcStore = hsfServiceFactory.consumer(FuturesDailyBtcStore.class);
                        if (storeConsumer != null && dailyBtcStore != null) {
                            List<FuturesSymbol> symbolList = storeConsumer.getListByTradeMarket(sysTrade.getId(), "btc");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");
                                for (FuturesSymbol symbol : symbolList) {
                                    params.put("symbol", symbol.getSymbol());
                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/future_kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<FuturesDailyBtc> dailyUsdtList = new ArrayList<>();
                                                for (int x=0 ;x<klineArray.size()-1;x++ ) {
                                                    JSONArray dayAttr = klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice=dayAttr.getDouble(4);
                                                        Double volume=dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);
                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);

                                                        FuturesDailyBtc dailyBtc = new FuturesDailyBtc();
                                                        dailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        dailyBtc.setTradeId(sysTrade);
                                                        dailyBtc.setSymbolId(symbol);
                                                        dailyBtc.setTradingDay(timeDate);
                                                        dailyBtc.setLastPrice(lastPrice);
                                                        dailyBtc.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                        dailyBtc.setVolume(volume);
                                                        dailyBtc.setCreatedBy("batch");
                                                        dailyBtc.setCreated(new Date());
                                                        dailyUsdtList.add(dailyBtc);
                                                    }
                                                }
                                                dailyBtcStore.save(dailyUsdtList, Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(symbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public String buildFuturesEth()throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        FuturesSymbolStore storeConsumer = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                        FuturesDailyEthStore dailyEthStore = hsfServiceFactory.consumer(FuturesDailyEthStore.class);
                        if (storeConsumer != null && dailyEthStore != null) {
                            List<FuturesSymbol> symbolList = storeConsumer.getListByTradeMarket(sysTrade.getId(), "eth");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("type", "1day");
                                for (FuturesSymbol symbol : symbolList) {
                                    params.put("symbol", symbol.getSymbol());
                                    try {
                                        Set<String> dateSet = new HashSet<>();
                                        String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/future_kline.do", params);
                                        if (StringUtils.isNotBlank(resultStr)) {
                                            JSONArray klineArray = JSONArray.fromObject(resultStr);
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<FuturesDailyEth> dailyUsdtList = new ArrayList<>();
                                                for (int x=0 ;x<klineArray.size()-1;x++ ) {
                                                    JSONArray dayAttr = klineArray.getJSONArray(x);
                                                    if (dayAttr != null && !dayAttr.isEmpty()) {
                                                        Long times = dayAttr.getLong(0);
                                                        Double lastPrice=dayAttr.getDouble(4);
                                                        Double volume=dayAttr.getDouble(5);
                                                        Date timeDate = new Date(times);
                                                        String happenDay = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
                                                        FuturesDailyEth dailyEth = new FuturesDailyEth();
                                                        dailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        dailyEth.setTradeId(sysTrade);
                                                        dailyEth.setSymbolId(symbol);
                                                        dailyEth.setTradingDay(timeDate);
                                                        dailyEth.setLastPrice(lastPrice);
                                                        dailyEth.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                        dailyEth.setVolume(volume);
                                                        dailyEth.setCreatedBy("batch");
                                                        dailyEth.setCreated(new Date());
                                                        dailyUsdtList.add(dailyEth);
                                                    }
                                                }
                                                dailyEthStore.save(dailyUsdtList, Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(symbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}