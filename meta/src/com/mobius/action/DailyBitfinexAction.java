package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.Utils;
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
import org.guiceside.web.annotation.ReqGet;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Action(name = "bitfinex", namespace = "/daily")
public class DailyBitfinexAction extends BaseAction {

    private static String tradeSign = "BITFINEX";


    @ReqGet
    private Long marketId;
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
                                            if (symbol.endsWith("usdt")||symbol.endsWith("usd")) {
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
            startTime = DateFormatUtil.parse("2018-05-26 00:00:00", DateFormatUtil.YMDHMS_PATTERN).getTime();
        }
        params.put("sort", "1");
        params.put("limit", "1000");
        params.put("start", startTime + "");
        long endTime = 0l;
        try {
            long s = System.currentTimeMillis();
            String url = "https://api.bitfinex.com/v2/candles/trade:1D:t" + spotSymbol.getSymbol().toUpperCase() + "/hist";
            String resultStr = OKHttpUtil.get(url, params);
            if (StringUtils.isNotBlank(resultStr)) {
                JSONArray klineArray = JSONArray.fromObject(resultStr);
                if (klineArray != null && !klineArray.isEmpty()) {
                    List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                    List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                    List<SpotDailyEth> dailyEthList = new ArrayList<>();

                    Date curDate = DateFormatUtil.getCurrentDate(false);
                    int curYear = DateFormatUtil.getDayInYear(curDate);
                    int curMonth = DateFormatUtil.getDayInMonth(curDate) + 1;
                    int curDay = DateFormatUtil.getDayInDay(curDate);
                    Set<String> dateSet=new HashSet<>();

                    for (Object dayObj : klineArray) {
                        JSONArray dayAttr = (JSONArray) dayObj;
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            endTime = times;
                            Double lastPrice = dayAttr.getDouble(2);
                            Double volume = dayAttr.getDouble(5);
                            Double turnover = NumberUtils.multiply(lastPrice, volume, 8);
                            Date timeDate = new Date(times);
                            String ymdStr=DateFormatUtil.format(timeDate,DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                            if(dateSet.contains(ymdStr)){
                                continue;
                            }
                            dateSet.add(ymdStr);
                            int timeYear = DateFormatUtil.getDayInYear(timeDate);
                            int timeMonth = DateFormatUtil.getDayInMonth(timeDate) + 1;
                            int timeDay = DateFormatUtil.getDayInDay(timeDate);

                            if (timeYear == curYear && timeMonth == curMonth && timeDay == curDay) {
                                continue;
                            }
                            if (spotSymbol.getMarket().equals("usdt")) {
                                SpotDailyUsdt spotDailyUsdt = new SpotDailyUsdt();
                                spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyUsdt.setTradeId(sysTrade);
                                spotDailyUsdt.setSymbolId(spotSymbol);
                                spotDailyUsdt.setTradingDay(timeDate);
                                spotDailyUsdt.setLastPrice(lastPrice);
                                spotDailyUsdt.setVolume(volume);
                                spotDailyUsdt.setTurnover(turnover);

                                dailyUsdtList.add(spotDailyUsdt);
                            } else if (spotSymbol.getMarket().equals("btc")) {
                                SpotDailyBtc spotDailyBtc = new SpotDailyBtc();
                                spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyBtc.setTradeId(sysTrade);
                                spotDailyBtc.setSymbolId(spotSymbol);
                                spotDailyBtc.setTradingDay(timeDate);
                                spotDailyBtc.setLastPrice(lastPrice);
                                spotDailyBtc.setVolume(volume);
                                spotDailyBtc.setTurnover(turnover);

                                dailyBtcList.add(spotDailyBtc);
                            } else if (spotSymbol.getMarket().equals("eth")) {
                                SpotDailyEth spotDailyEth = new SpotDailyEth();
                                spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                spotDailyEth.setTradeId(sysTrade);
                                spotDailyEth.setSymbolId(spotSymbol);
                                spotDailyEth.setTradingDay(timeDate);
                                spotDailyEth.setLastPrice(lastPrice);
                                spotDailyEth.setVolume(volume);
                                spotDailyEth.setTurnover(turnover);

                                dailyEthList.add(spotDailyEth);
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


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            endTime=0;
        }finally {
            System.out.println("============********============sleep start" );
            TimeUnit.SECONDS.sleep(20);//秒
            System.out.println("============********============sleep end" );

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
                            while (endTime>0&&endTime < currentDayTime) {
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

    public String buildSymbolId() throws Exception {
        if (marketId != null) {
            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
            if (sysTradeStore != null) {
                SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                if (sysTrade != null) {
                    SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                    SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                    if (spotSymbolStore != null && spotDailyUsdtStore != null) {
                        SpotSymbol spotSymbol = spotSymbolStore.getById(marketId);
                        if (spotSymbol != null) {
                            Map<String, String> params = new HashMap<>();
                            long currentDayTime = DateFormatUtil.getCurrentDate(false).getTime();
                            long endTime = callApi(spotSymbol, 0l, params, sysTrade);
                            while (endTime>0&&endTime < currentDayTime) {
                                System.out.println(spotSymbol.getSymbol() + " while ");
                                endTime = callApi(spotSymbol, endTime, params, sysTrade);
                            }

                            System.out.println(" over========================================== ");
                        }
                    }
                }
            }
        }


        return null;
    }




    public void buildDaily() throws Exception {
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
                            Date d = DateFormatUtil.getCurrentDate(false);
                            d = DateFormatUtil.addDay(d, -1);//提前一天
                            params.put("sort", "1");//旧的在前面
                            params.put("limit", "1");//只显示前一天数据
                            params.put("start", d.getTime() + "");//从前一天开始返回
                            for (SpotSymbol spotSymbol : symbolList) {
                                try {
                                    String url = "https://api.bitfinex.com/v2/candles/trade:1D:t" + spotSymbol.getSymbol().toUpperCase() + "/hist";
                                    String resultStr = OKHttpUtil.get(url, params);
                                    if (StringUtils.isNotBlank(resultStr)) {
                                        JSONArray klineArray = JSONArray.fromObject(resultStr);
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                                            List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                                            List<SpotDailyEth> dailyEthList = new ArrayList<>();
                                            JSONArray dayAttr = klineArray.getJSONArray(0);
                                            if (dayAttr != null && !dayAttr.isEmpty()) {
                                                Long times = dayAttr.getLong(0);
                                                Double lastPrice = dayAttr.getDouble(2);
                                                Double volume = dayAttr.getDouble(5);
                                                Double turnover = NumberUtils.multiply(lastPrice,volume,8);
                                                Date timeDate = new Date(times);
                                                String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                                                if (market.equals("usdt")) {
                                                    Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == null) {
                                                        count = 0;
                                                    } else {
                                                        System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    }
                                                    if (count.intValue() == 0) {
                                                        SpotDailyUsdt spotDailyUsdt = new SpotDailyUsdt();
                                                        spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyUsdt.setTradeId(sysTrade);
                                                        spotDailyUsdt.setSymbolId(spotSymbol);
                                                        spotDailyUsdt.setTradingDay(timeDate);
                                                        spotDailyUsdt.setLastPrice(lastPrice);


                                                        spotDailyUsdt.setVolume(volume);
                                                        spotDailyUsdt.setTurnover(turnover);

                                                        Utils.bind(spotDailyUsdt,"task");
                                                        dailyUsdtList.add(spotDailyUsdt);

                                                    }
                                                } else if (market.equals("btc")) {
                                                    Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == null) {
                                                        count = 0;
                                                    } else {
                                                        System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    }
                                                    if (count.intValue() == 0) {
                                                        SpotDailyBtc spotDailyBtc = new SpotDailyBtc();
                                                        spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyBtc.setTradeId(sysTrade);
                                                        spotDailyBtc.setSymbolId(spotSymbol);
                                                        spotDailyBtc.setTradingDay(timeDate);
                                                        spotDailyBtc.setLastPrice(lastPrice);


                                                        spotDailyBtc.setVolume(volume);
                                                        spotDailyBtc.setTurnover(turnover);
                                                        Utils.bind(spotDailyBtc,"task");
                                                        dailyBtcList.add(spotDailyBtc);

                                                    }
                                                } else if (market.equals("eth")) {
                                                    Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == null) {
                                                        count = 0;
                                                    } else {
                                                        System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    }
                                                    if (count.intValue() == 0) {
                                                        SpotDailyEth spotDailyEth = new SpotDailyEth();
                                                        spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyEth.setTradeId(sysTrade);
                                                        spotDailyEth.setSymbolId(spotSymbol);
                                                        spotDailyEth.setTradingDay(timeDate);
                                                        spotDailyEth.setLastPrice(lastPrice);


                                                        spotDailyEth.setVolume(volume);
                                                        spotDailyEth.setTurnover(turnover);
                                                        Utils.bind(spotDailyEth,"task");
                                                        dailyEthList.add(spotDailyEth);

                                                    }
                                                }
                                            }
                                            if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                                                spotDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success =====task======" + dailyUsdtList.size());

                                            }
                                            if (dailyBtcList != null && !dailyBtcList.isEmpty()) {
                                                spotDailyBtcStore.save(dailyBtcList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success =====task======" + dailyBtcList.size());

                                            }
                                            if (dailyEthList != null && !dailyEthList.isEmpty()) {
                                                spotDailyEthStore.save(dailyEthList, Persistent.SAVE);
                                                System.out.println(spotSymbol.getSymbol() + " save success =====task======" + dailyEthList.size());

                                            }

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }finally {
                                    System.out.println("============********============sleep start" );
                                    TimeUnit.SECONDS.sleep(20);//秒
                                    System.out.println("============********============sleep end" );

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}