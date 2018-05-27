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

    public String spotDailyTask() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                List<String> marketList = new ArrayList<>();
                marketList.add("usdt");
                marketList.add("btc");
                marketList.add("eth");

                SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
                SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
                if (spotSymbolStore != null && spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
                    for (String market : marketList) {
                        List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), market);
                        if (symbolList != null && !symbolList.isEmpty()) {
                            Map<String, String> params = new HashMap<>();
                            params.put("type", "1day");
                            params.put("since", (DateFormatUtil.getCurrentDate(false).getTime() - 24 * 60 * 60 * 1000) + "");
                            for (SpotSymbol spotSymbol : symbolList) {
                                params.put("symbol", spotSymbol.getSymbol());
                                try {
                                    String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/kline.do", params);
                                    if (StringUtils.isNotBlank(resultStr)) {
                                        JSONArray klineArray = JSONArray.fromObject(resultStr);
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                                            List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                                            List<SpotDailyEth> dailyEthList = new ArrayList<>();
                                            for (int x = 0; x < klineArray.size() - 1; x++) {
                                                JSONArray dayAttr = klineArray.getJSONArray(x);
                                                if (dayAttr != null && !dayAttr.isEmpty()) {
                                                    Long times = dayAttr.getLong(0);
                                                    Double lastPrice = dayAttr.getDouble(4);
                                                    Double volume = dayAttr.getDouble(5);
                                                    Date timeDate = new Date(times);

                                                    String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                    Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                                                    if (market.equals("usdt")) {
                                                        Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                                spotSymbol.getId(), tradingDate);
                                                        if (count == 1) {
                                                            System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                            System.out.println(resultStr);
                                                        } else if (count == 0) {
                                                            SpotDailyUsdt spotDailyUsdt = new SpotDailyUsdt();
                                                            spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                            spotDailyUsdt.setTradeId(sysTrade);
                                                            spotDailyUsdt.setSymbolId(spotSymbol);
                                                            spotDailyUsdt.setTradingDay(timeDate);
                                                            spotDailyUsdt.setLastPrice(lastPrice);
                                                            spotDailyUsdt.setVolume(volume);
                                                            spotDailyUsdt.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                            spotDailyUsdt.setCreatedBy("task");
                                                            spotDailyUsdt.setCreated(new Date());
                                                            dailyUsdtList.add(spotDailyUsdt);
                                                        }
                                                    } else if (market.equals("btc")) {
                                                        Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                                spotSymbol.getId(), tradingDate);
                                                        if (count == 1) {
                                                            System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                            System.out.println(resultStr);
                                                        } else if (count == 0) {
                                                            SpotDailyBtc spotDailyBtc = new SpotDailyBtc();
                                                            spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                            spotDailyBtc.setTradeId(sysTrade);
                                                            spotDailyBtc.setSymbolId(spotSymbol);
                                                            spotDailyBtc.setTradingDay(timeDate);
                                                            spotDailyBtc.setLastPrice(lastPrice);
                                                            spotDailyBtc.setVolume(volume);
                                                            spotDailyBtc.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                            spotDailyBtc.setCreatedBy("task");
                                                            spotDailyBtc.setCreated(new Date());
                                                            dailyBtcList.add(spotDailyBtc);
                                                        }
                                                    } else if (market.equals("eth")) {
                                                        Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                                spotSymbol.getId(), tradingDate);
                                                        if (count == 1) {
                                                            System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                            System.out.println(resultStr);
                                                        } else if (count == 0) {
                                                            SpotDailyEth spotDailyEth = new SpotDailyEth();
                                                            spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                            spotDailyEth.setTradeId(sysTrade);
                                                            spotDailyEth.setSymbolId(spotSymbol);
                                                            spotDailyEth.setTradingDay(timeDate);
                                                            spotDailyEth.setLastPrice(lastPrice);
                                                            spotDailyEth.setVolume(volume);
                                                            spotDailyEth.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                            spotDailyEth.setCreatedBy("task");
                                                            spotDailyEth.setCreated(new Date());
                                                            dailyEthList.add(spotDailyEth);
                                                        }
                                                    }
                                                }
                                            }
                                            if (!dailyUsdtList.isEmpty()) {
                                                spotDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success " + dailyUsdtList.size());
                                            }
                                            if (!dailyBtcList.isEmpty()) {
                                                spotDailyBtcStore.save(dailyBtcList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success " + dailyBtcList.size());
                                            }
                                            if (!dailyEthList.isEmpty()) {
                                                spotDailyEthStore.save(dailyEthList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success " + dailyEthList.size());
                                            }
                                            TimeUnit.SECONDS.sleep(2);//秒
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    System.out.println("okex spot daily task over---------");
                    result.put("result", "0");
                    writeJsonByAction(result.toString());
                }
            }
        }
        return null;
    }


    public String futuresDailyTask() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                List<String> marketList = new ArrayList<>();
                marketList.add("usdt");

                FuturesSymbolStore symbolStoreConsumer = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                FuturesDailyUsdtStore dailyUsdtStore = hsfServiceFactory.consumer(FuturesDailyUsdtStore.class);
                if (symbolStoreConsumer != null && dailyUsdtStore != null) {
                    for (String market : marketList) {
                        List<FuturesSymbol> symbolList = symbolStoreConsumer.getListByTradeMarket(sysTrade.getId(), market);
                        if (symbolList != null && !symbolList.isEmpty()) {
                            Map<String, String> params = new HashMap<>();
                            params.put("type", "1day");
                            params.put("since", (DateFormatUtil.getCurrentDate(false).getTime() - 24 * 60 * 60 * 1000) + "");
                            for (FuturesSymbol symbol : symbolList) {
                                params.put("symbol", symbol.getSymbol());
                                params.put("contract_type", symbol.getSymbolDesc());
                                try {
                                    String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/future_kline.do", params);
                                    if (StringUtils.isNotBlank(resultStr)) {
                                        JSONArray klineArray = JSONArray.fromObject(resultStr);
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<FuturesDailyUsdt> dailyUsdtList = new ArrayList<>();
                                            for (int x = 0; x < klineArray.size() - 1; x++) {// 1 2 3
                                                JSONArray dayAttr = klineArray.getJSONArray(x);
                                                if (dayAttr != null && !dayAttr.isEmpty()) {
                                                    Long times = dayAttr.getLong(0);
                                                    Double lastPrice = dayAttr.getDouble(4);
                                                    Double volume = dayAttr.getDouble(5);
                                                    Date timeDate = new Date(times);

                                                    String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                    Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                                                    Integer count = dailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(), symbol.getId(), tradingDate);
                                                    if (count == null) {
                                                        count = 0;
                                                    } else {
                                                        System.out.println("okex daily task print--- " + dateStr + " " + symbol.getSymbol() + " count >1");
                                                    }
                                                    if (count == 0) {
                                                        FuturesDailyUsdt dailyUsdt = new FuturesDailyUsdt();
                                                        dailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        dailyUsdt.setTradeId(sysTrade);
                                                        dailyUsdt.setSymbolId(symbol);
                                                        dailyUsdt.setTradingDay(timeDate);
                                                        dailyUsdt.setLastPrice(lastPrice);
                                                        dailyUsdt.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                        dailyUsdt.setVolume(volume);
                                                        dailyUsdt.setCreatedBy("task");
                                                        dailyUsdt.setCreated(new Date());
                                                        dailyUsdtList.add(dailyUsdt);
                                                    }
                                                }
                                            }
                                            if (!dailyUsdtList.isEmpty()) {
                                                dailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                                            }
                                            TimeUnit.SECONDS.sleep(2);//秒
                                            System.out.println(symbol.getSymbol() + "okex daily task save success " + dailyUsdtList.size());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                    System.out.println("okex futures daily task over---------");
                    result.put("result", "0");
                    writeJsonByAction(result.toString());
                }
            }
        }
        return null;
    }
}