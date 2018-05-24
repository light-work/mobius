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
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

import java.util.*;

@Action(name = "binance", namespace = "/daily")
public class DailyBinanceAction extends BaseAction {

    private static String tradeSign = "BINANCE";


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
                            String resultStr = OKHttpUtil.get("https://api.binance.com/api/v1/exchangeInfo", null);
                            if (StringUtils.isNotBlank(resultStr)) {
                                JSONObject jsonObject = JSONObject.fromObject(resultStr);
                                if (jsonObject != null) {
                                    JSONArray symbols = jsonObject.getJSONArray("symbols");
                                    if (symbols != null && !symbols.isEmpty()) {
                                        int displayOrder = 1;
                                        List<SpotSymbol> spotSymbolList = new ArrayList<>();
                                        for (Object obj : symbols) {
                                            JSONObject symbolObj = (JSONObject) obj;
                                            if (symbolObj != null) {
                                                String symbol = symbolObj.getString("symbol");
                                                String market = symbolObj.getString("quoteAsset");
                                                SpotSymbol spotSymbol = new SpotSymbol();
                                                spotSymbol.setTradeId(sysTrade);
                                                spotSymbol.setSymbol(symbol);
                                                spotSymbol.setMarket(market.toLowerCase());
                                                spotSymbol.setDisplayOrder(displayOrder);
                                                spotSymbol.setUseYn("Y");
                                                spotSymbolList.add(spotSymbol);
                                            }
                                        }
                                        if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                                            spotSymbolStore.save(spotSymbolList, Persistent.SAVE);
                                            result.put("result", "0");
                                        }
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


    private long usdt(SpotSymbol spotSymbol, long startTime, Map<String, String> params, SysTrade sysTrade,
                      SpotDailyUsdtStore spotDailyUsdtStore) throws Exception {
        if (startTime == 0) {
            startTime = DateFormatUtil.parse("2017-01-01 00:00:00", DateFormatUtil.YMDHMS_PATTERN).getTime();
        }
        params.put("symbol", spotSymbol.getSymbol());
        params.put("startTime", startTime + "");
        long endTime = 0l;
        try {
            String resultStr = OKHttpUtil.get("https://api.binance.com/api/v1/klines", params);
            if (StringUtils.isNotBlank(resultStr)) {
                JSONArray klineArray = JSONArray.fromObject(resultStr);
                if (klineArray != null && !klineArray.isEmpty()) {
                    System.out.println("klineArray  = " + klineArray.size());
                    List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                    Double volumeOne = 0.00d;
                    Double turnoverOne = 0.00d;
                    for (Object dayObj : klineArray) {
                        JSONArray dayAttr = (JSONArray) dayObj;
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            endTime = times;
                            Double lastPrice = dayAttr.getDouble(4);
                            Double volume = dayAttr.getDouble(5);
                            Double turnover = dayAttr.getDouble(7);
                            Date timeDate = new Date(times);
                            int hours = DateFormatUtil.getDayInHour(timeDate);
                            System.out.println(times+" hours  = " + hours);

                            if (hours != 0) {
                                System.out.print("volumeOne  = " + volumeOne + "+" + volume);
                                volumeOne = NumberUtils.add(volumeOne, volume, 8);
                                System.out.println("=  = " + volumeOne);

                                System.out.print("turnoverOne  = " + turnoverOne + "+" + turnover);
                                turnoverOne = NumberUtils.add(turnoverOne, turnover, 8);
                                System.out.println("=  = " + turnoverOne);
                                continue;
                            }
                            System.out.println("hours  =0000000 " + hours);
                            System.out.println("volumeOne  =0000000 " + volumeOne);
                            System.out.println("turnoverOne  =0000000 " + turnoverOne);
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
                        }
                    }
                    if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                        spotDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                        System.out.println(spotSymbol.getSymbol() + " save success============================= " + dailyUsdtList.size());
                    }

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
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                        if (spotSymbolStore != null && spotDailyUsdtStore != null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "usdt");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("interval", "8h");
                                long currentDayTime = DateFormatUtil.getCurrentDate(false).getTime();
                                for (SpotSymbol spotSymbol : symbolList) {
                                    long endTime = usdt(spotSymbol, 0l, params, sysTrade, spotDailyUsdtStore);
                                    while (currentDayTime < endTime) {
                                        System.out.println(spotSymbol.getSymbol() + " while ");
                                        endTime = usdt(spotSymbol, endTime, params, sysTrade, spotDailyUsdtStore);
                                    }
                                }

                                System.out.println( " over========================================== ");
                                result.put("result", "0");
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