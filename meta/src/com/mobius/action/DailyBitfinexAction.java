package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.TimeUtils;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Action(name = "bitfinex", namespace = "/daily")
public class DailyBitfinexAction extends BaseAction {

    private static String tradeSign = "BITFINEX";


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }

    public String initSymbol() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        if (spotSymbolStore != null) {
                            String resultStr = OKHttpUtil.get("https://api.bitfinex.com/v1/symbols", null);
                            if (StringUtils.isNotBlank(resultStr)) {
                                JSONArray symbols = JSONArray.fromObject(resultStr);
                                if (symbols != null && !symbols.isEmpty()) {
                                    int displayOrder = 1;
                                    List<SpotSymbol> spotSymbolList = new ArrayList<>();
                                    for (Object obj : symbols) {
                                        String symbol = (String) obj;
                                        if (StringUtils.isNotBlank(symbol)) {
                                            SpotSymbol spotSymbol = new SpotSymbol();
                                            spotSymbol.setTradeId(sysTrade);
                                            spotSymbol.setSymbol(symbol);
                                            String market = null;
                                            if (symbol.endsWith("usdt")) {
                                                market = "usdt";
                                            } else if (symbol.endsWith("btc")) {
                                                market = "btc";
                                            } else if (symbol.endsWith("eth")) {
                                                market = "eth";
                                            }
                                            if (StringUtils.isNotBlank(market)) {
                                                spotSymbol.setMarket(market);
                                                spotSymbol.setDisplayOrder(displayOrder);
                                                displayOrder++;
                                                spotSymbol.setUseYn("Y");
                                                spotSymbolList.add(spotSymbol);
                                            }
                                        }
                                    }
                                    if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                                        spotSymbolStore.save(spotSymbolList, Persistent.SAVE);
                                        result.put("result", "0");
                                    }
                                }
                            }
                            writeJsonByAction(result.toString());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private long callApi(SpotSymbol spotSymbol, long startTime, Map<String, String> params, SysTrade sysTrade) throws Exception {

        SpotDailyUsdtStore spotDailyUsdtStore = null;
        SpotDailyBtcStore spotDailyBtcStore = null;
        SpotDailyEthStore spotDailyEthStore = null;
        if (spotSymbol.getMarket().equals("usdt")) {
            spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
            if (spotDailyUsdtStore == null) {
                return DateFormatUtil.getCurrentDate(true).getTime();
            }
        } else if (spotSymbol.getMarket().equals("btc")) {
            spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
            if (spotDailyBtcStore == null) {
                return DateFormatUtil.getCurrentDate(true).getTime();
            }
        } else if (spotSymbol.getMarket().equals("eth")) {
            spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
            if (spotDailyEthStore == null) {
                return DateFormatUtil.getCurrentDate(true).getTime();
            }
        }
        if (startTime == 0) {
            startTime = DateFormatUtil.parse("2017-01-01 00:00:00", DateFormatUtil.YMDHMS_PATTERN).getTime();
        }
        params.put("sort", "1");
        params.put("limit", "1000");
        params.put("start", startTime + "");
        long endTime = 0l;
        try {
            long s = System.currentTimeMillis();
            String url = "https://api.bitfinex.com/v2/candles/trade:1h:t" + spotSymbol.getSymbol().toUpperCase() + "/hist";
            String resultStr = OKHttpUtil.get(url, params);
            if (StringUtils.isNotBlank(resultStr)) {
                JSONArray klineArray = JSONArray.fromObject(resultStr);
                if (klineArray != null && !klineArray.isEmpty()) {
                    List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                    List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                    List<SpotDailyEth> dailyEthList = new ArrayList<>();

                    Double volumeOne = 0.00d;
                    Double turnoverOne = 0.00d;
                    for (Object dayObj : klineArray) {
                        JSONArray dayAttr = (JSONArray) dayObj;
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            endTime = times;
                            Double lastPrice = dayAttr.getDouble(2);
                            Double volume = dayAttr.getDouble(5);
                            Double turnover = NumberUtils.multiply(lastPrice, volume, 8);
                            Date timeDate = new Date(times);
                            int hours = DateFormatUtil.getDayInHour(timeDate);
                            System.out.println(endTime + " hours  = " + hours);
                            if (hours != 0) {
                                volumeOne = NumberUtils.add(volumeOne, volume, 8);

                                turnoverOne = NumberUtils.add(turnoverOne, turnover, 8);
                                continue;
                            }

                            if (spotSymbol.getMarket().equals("usdt")) {
                                SpotDailyUsdt spotDailyUsdt = new SpotDailyUsdt();
                                spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyUsdt.setTradeId(sysTrade);
                                spotDailyUsdt.setSymbolId(spotSymbol);
                                spotDailyUsdt.setTradingDay(timeDate);
                                spotDailyUsdt.setLastPrice(lastPrice);
                                volumeOne = NumberUtils.add(volumeOne, volume, 8);
                                turnoverOne = NumberUtils.add(turnoverOne, turnover, 8);
                                spotDailyUsdt.setVolume(volumeOne);
                                spotDailyUsdt.setTurnover(turnoverOne);

                                dailyUsdtList.add(spotDailyUsdt);
                                volumeOne = 0.00d;
                                turnoverOne = 0.00d;
                            } else if (spotSymbol.getMarket().equals("btc")) {
                                SpotDailyBtc spotDailyBtc = new SpotDailyBtc();
                                spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyBtc.setTradeId(sysTrade);
                                spotDailyBtc.setSymbolId(spotSymbol);
                                spotDailyBtc.setTradingDay(timeDate);
                                spotDailyBtc.setLastPrice(lastPrice);
                                volumeOne = NumberUtils.add(volumeOne, volume, 8);
                                turnoverOne = NumberUtils.add(turnoverOne, turnover, 8);
                                spotDailyBtc.setVolume(volumeOne);
                                spotDailyBtc.setTurnover(turnoverOne);

                                dailyBtcList.add(spotDailyBtc);
                                volumeOne = 0.00d;
                                turnoverOne = 0.00d;
                            } else if (spotSymbol.getMarket().equals("eth")) {
                                SpotDailyEth spotDailyEth = new SpotDailyEth();
                                spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyEth.setTradeId(sysTrade);
                                spotDailyEth.setSymbolId(spotSymbol);
                                spotDailyEth.setTradingDay(timeDate);
                                spotDailyEth.setLastPrice(lastPrice);
                                volumeOne = NumberUtils.add(volumeOne, volume, 8);
                                turnoverOne = NumberUtils.add(turnoverOne, turnover, 8);
                                spotDailyEth.setVolume(volumeOne);
                                spotDailyEth.setTurnover(turnoverOne);

                                dailyEthList.add(spotDailyEth);
                                volumeOne = 0.00d;
                                turnoverOne = 0.00d;
                            }
                        }
                    }
                    if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                        spotDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                        System.out.println(spotSymbol.getSymbol() + " save success============================= " + dailyUsdtList.size());
                    }
                    if (dailyBtcList != null && !dailyBtcList.isEmpty()) {
                        spotDailyBtcStore.save(dailyBtcList, Persistent.SAVE);
                        System.out.println(spotSymbol.getSymbol() + " save success============================= " + dailyBtcList.size());
                    }
                    if (dailyEthList != null && !dailyEthList.isEmpty()) {
                        spotDailyEthStore.save(dailyEthList, Persistent.SAVE);
                        System.out.println(spotSymbol.getSymbol() + " save success============================= " + dailyEthList.size());
                    }
                    long e = System.currentTimeMillis();
                    System.out.println("============********============" + TimeUtils.getTimeDiff(s, e));
                    System.out.println("============********============sleep start" );
                    TimeUnit.SECONDS.sleep(3);//秒
                    System.out.println("============********============sleep end" );

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return endTime;
    }

    public String buildSpotUsdt() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                buildSpot("usdt", result);
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String buildSpotBtc() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                buildSpot("btc", result);
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String buildSpotEth() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                buildSpot("eth", result);
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    private void buildSpot(String market, JSONObject result) throws Exception {
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                if (spotSymbolStore != null) {
                    List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), market);
                    if (symbolList != null && !symbolList.isEmpty()) {
                        Map<String, String> params = new HashMap<>();
                        long currentDayTime = DateFormatUtil.getCurrentDate(false).getTime();
                        for (SpotSymbol spotSymbol : symbolList) {
                            long endTime = callApi(spotSymbol, 0l, params, sysTrade);
                            while (endTime < currentDayTime) {
                                System.out.println(spotSymbol.getSymbol() + " while ");
                                endTime = callApi(spotSymbol, endTime, params, sysTrade);
                            }
                        }

                        System.out.println(" over========================================== ");
                        result.put("result", "0");
                    }
                }
            }
        }
    }
}