package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.Utils;
import com.mobius.common.BizException;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.biz.meta.DailyBiz;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.*;

/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class DailyBizImp extends BaseBiz implements DailyBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String dailyForOkex(SpotSymbol spotSymbol, SysTrade sysTrade, String releaseEnvironment,Date dailyDate,boolean override) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
        SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
        SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
        if (spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null && dailyDate != null) {
            String market = spotSymbol.getMarket();
            Map<String, String> params = new HashMap<>();
            params.put("type", "1day");
            String dailyDateFmt = DateFormatUtil.format(dailyDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            Long since = DateFormatUtil.parse(dailyDateFmt + " 00:00:00", DateFormatUtil.YMDHMS_PATTERN).getTime();
            params.put("since", since + "");
            params.put("symbol", spotSymbol.getSymbol());
            try {
                String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/kline.do", params);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONArray klineArray = JSONArray.fromObject(resultStr);
                    if (klineArray != null && !klineArray.isEmpty()) {
                        List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                        List<SpotDailyEth> dailyEthList = new ArrayList<>();

                        List<SpotDailyUsdt> dailyUsdtListUpdate = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcListUpdate = new ArrayList<>();
                        List<SpotDailyEth> dailyEthListUpdate = new ArrayList<>();

                        for (int x = 0; x < 1; x++) {
                            JSONArray dayAttr = klineArray.getJSONArray(x);
                            if (dayAttr != null && !dayAttr.isEmpty()) {
                                Long times = dayAttr.getLong(0);
                                Double lastPrice = dayAttr.getDouble(4);
                                Double volume = dayAttr.getDouble(5);
                                Date timeDate = new Date(times);
                                if (!times.equals(since)) {
                                    System.out.println("----DailyBizImp dailyForOkex con't find  symbol last price and symbol is " +
                                            spotSymbol.getSymbol() + " date=" + DateFormatUtil.format(dailyDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN));
                                    break;
                                }

                                String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                                if (market.equals("usdt")) {
                                    Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                            spotSymbol.getId(), tradingDate);
                                    if (count == 1) {
                                        System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                        if(override){
                                            SpotDailyUsdt spotDailyUsdt = spotDailyUsdtStore.getTradeSymbolDay(sysTrade.getId(),
                                                    spotSymbol.getId(), tradingDate);
                                            if(spotDailyUsdt!=null){
                                                spotDailyUsdt.setVolume(volume);
                                                spotDailyUsdt.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                Utils.bind(spotDailyUsdt, "task");
                                                dailyUsdtListUpdate.add(spotDailyUsdt);
                                            }
                                        }
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
                                        if(override){
                                            SpotDailyBtc spotDailyBtc = spotDailyBtcStore.getTradeSymbolDay(sysTrade.getId(),
                                                    spotSymbol.getId(), tradingDate);
                                            if(spotDailyBtc!=null){
                                                spotDailyBtc.setVolume(volume);
                                                spotDailyBtc.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                Utils.bind(spotDailyBtc, "task");
                                                dailyBtcListUpdate.add(spotDailyBtc);
                                            }
                                        }
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
                                        if(override){
                                            SpotDailyEth spotDailyEth = spotDailyEthStore.getTradeSymbolDay(sysTrade.getId(),
                                                    spotSymbol.getId(), tradingDate);
                                            if(spotDailyEth!=null){
                                                spotDailyEth.setVolume(volume);
                                                spotDailyEth.setTurnover(NumberUtils.multiply(volume, lastPrice, 8));
                                                Utils.bind(spotDailyEth, "task");
                                                dailyEthListUpdate.add(spotDailyEth);
                                            }
                                        }
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
                        saveOrUpdate(spotSymbol,dailyUsdtList,dailyBtcList,dailyEthList,
                                dailyUsdtListUpdate,dailyBtcListUpdate,dailyEthListUpdate,
                                spotDailyUsdtStore,spotDailyBtcStore,spotDailyEthStore,releaseEnvironment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultObj.toString();
    }

    private static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    @Override
    public String dailyForHuobiPro(SpotSymbol spotSymbol, SysTrade sysTrade,String releaseEnvironment,Date dailyDate,boolean override) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
        SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
        SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
        if (spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
            String market = spotSymbol.getMarket();
            Map<String, String> params = new HashMap<>();
            params.put("period", "1day");
            params.put("size", (getGapCount(dailyDate, new Date()) + 1) + "");
            params.put("symbol", spotSymbol.getSymbol());
            try {
                Set<String> dateSet = new HashSet<>();
                String result = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject obj = JSONObject.fromObject(result);
                    if (!obj.containsKey("data")) {
                        System.out.print("huobi  kline data was null!");
                        return null;
                    }
                    JSONArray klineArray = obj.getJSONArray("data");
                    if (klineArray != null && !klineArray.isEmpty()) {
                        List<SpotDailyUsdt> usdtList = new ArrayList<>();
                        List<SpotDailyBtc> btcList = new ArrayList<>();
                        List<SpotDailyEth> ethList = new ArrayList<>();

                        List<SpotDailyUsdt> dailyUsdtListUpdate = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcListUpdate = new ArrayList<>();
                        List<SpotDailyEth> dailyEthListUpdate = new ArrayList<>();

                        for (int x = klineArray.size() - 1; x < klineArray.size(); x++) {// 3 2 1
                            JSONObject jsonObj = klineArray.getJSONObject(x);
                            String dateStr = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                    DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                            Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                            if (!dateStr.equals(DateFormatUtil.format(dailyDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN))) {
                                System.out.println("----DailyBizImp dailyForHuobiPro con't find  symbol last price and symbol is " +
                                        spotSymbol.getSymbol() + " date=" + DateFormatUtil.format(dailyDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN));
                                break;
                            }
                            if (dateSet.contains(dateStr)) {
                                continue;
                            }
                            dateSet.add(dateStr);
                            if (market.equals("usdt")) {
                                Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == 1) {
                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");

                                    if(override){
                                        SpotDailyUsdt spotDailyUsdt = spotDailyUsdtStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyUsdt!=null){
                                            if (jsonObj.containsKey("amount")) {
                                                spotDailyUsdt.setVolume(jsonObj.getDouble("amount"));
                                            }
                                            if (jsonObj.containsKey("vol")) {
                                                spotDailyUsdt.setTurnover(jsonObj.getDouble("vol"));
                                            }
                                            Utils.bind(spotDailyUsdt, "task");
                                            dailyUsdtListUpdate.add(spotDailyUsdt);
                                        }
                                    }
                                } else if (count == 0) {
                                    SpotDailyUsdt usdt = new SpotDailyUsdt();
                                    usdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                    usdt.setTradeId(sysTrade);
                                    usdt.setSymbolId(spotSymbol);
                                    usdt.setTradingDay(tradingDate);
                                    usdt.setLastPrice(jsonObj.getDouble("close"));
                                    if (jsonObj.containsKey("amount")) {
                                        usdt.setVolume(jsonObj.getDouble("amount"));
                                    }
                                    if (jsonObj.containsKey("vol")) {
                                        usdt.setTurnover(jsonObj.getDouble("vol"));
                                    }
                                    usdt.setCreatedBy("task");
                                    usdt.setCreated(new Date());
                                    usdtList.add(usdt);
                                }
                            } else if (market.equals("btc")) {
                                Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == 1) {
                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyBtc spotDailyBtc = spotDailyBtcStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyBtc!=null){
                                            if (jsonObj.containsKey("amount")) {
                                                spotDailyBtc.setVolume(jsonObj.getDouble("amount"));
                                            }
                                            if (jsonObj.containsKey("vol")) {
                                                spotDailyBtc.setTurnover(jsonObj.getDouble("vol"));
                                            }
                                            Utils.bind(spotDailyBtc, "task");
                                            dailyBtcListUpdate.add(spotDailyBtc);
                                        }
                                    }
                                } else if (count == 0) {
                                    SpotDailyBtc btc = new SpotDailyBtc();
                                    btc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                    btc.setTradeId(sysTrade);
                                    btc.setSymbolId(spotSymbol);
                                    btc.setTradingDay(tradingDate);
                                    btc.setLastPrice(jsonObj.getDouble("close"));
                                    if (jsonObj.containsKey("amount")) {
                                        btc.setVolume(jsonObj.getDouble("amount"));
                                    }
                                    if (jsonObj.containsKey("vol")) {
                                        btc.setTurnover(jsonObj.getDouble("vol"));
                                    }
                                    btc.setCreatedBy("task");
                                    btc.setCreated(new Date());
                                    btcList.add(btc);
                                }
                            } else if (market.equals("eth")) {
                                Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == 1) {
                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyEth spotDailyEth = spotDailyEthStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyEth!=null){
                                            if (jsonObj.containsKey("amount")) {
                                                spotDailyEth.setVolume(jsonObj.getDouble("amount"));
                                            }
                                            if (jsonObj.containsKey("vol")) {
                                                spotDailyEth.setTurnover(jsonObj.getDouble("vol"));
                                            }
                                            Utils.bind(spotDailyEth, "task");
                                            dailyEthListUpdate.add(spotDailyEth);
                                        }
                                    }
                                } else if (count == 0) {
                                    SpotDailyEth eth = new SpotDailyEth();
                                    eth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                    eth.setTradeId(sysTrade);
                                    eth.setSymbolId(spotSymbol);
                                    eth.setTradingDay(tradingDate);
                                    eth.setLastPrice(jsonObj.getDouble("close"));
                                    if (jsonObj.containsKey("amount")) {
                                        eth.setVolume(jsonObj.getDouble("amount"));
                                    }
                                    if (jsonObj.containsKey("vol")) {
                                        eth.setTurnover(jsonObj.getDouble("vol"));
                                    }
                                    eth.setCreatedBy("task");
                                    eth.setCreated(new Date());
                                    ethList.add(eth);
                                }
                            }
                        }

                        saveOrUpdate(spotSymbol,usdtList,btcList,ethList,
                                dailyUsdtListUpdate,dailyBtcListUpdate,dailyEthListUpdate,
                                spotDailyUsdtStore,spotDailyBtcStore,spotDailyEthStore,releaseEnvironment);


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultObj.toString();
    }

    @Override
    public String dailyForBitfinex(SpotSymbol spotSymbol, SysTrade sysTrade,String releaseEnvironment,Date dailyDate,boolean override) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
        SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
        SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
        if (spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
            String market = spotSymbol.getMarket();
            Map<String, String> params = new HashMap<>();

            params.put("sort", "1");//旧的在前面
            params.put("limit", "1");//只显示前一天数据
            params.put("start", dailyDate.getTime() + "");//从前一天开始返回
            try {
                String url = "https://api.bitfinex.com/v2/candles/trade:1D:t" + spotSymbol.getSymbol().toUpperCase() + "/hist";
                String resultStr = OKHttpUtil.get(url, params);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONArray klineArray = JSONArray.fromObject(resultStr);
                    if (klineArray != null && !klineArray.isEmpty()) {
                        List<SpotDailyUsdt> dailyUsdtList = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcList = new ArrayList<>();
                        List<SpotDailyEth> dailyEthList = new ArrayList<>();

                        List<SpotDailyUsdt> dailyUsdtListUpdate = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcListUpdate = new ArrayList<>();
                        List<SpotDailyEth> dailyEthListUpdate = new ArrayList<>();

                        JSONArray dayAttr = klineArray.getJSONArray(0);
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            Double lastPrice = dayAttr.getDouble(2);
                            Double volume = dayAttr.getDouble(5);
                            Double turnover = NumberUtils.multiply(lastPrice, volume, 8);
                            Date timeDate = new Date(times);
                            String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                            Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                            if (market.equals("usdt")) {
                                Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyUsdt spotDailyUsdt = spotDailyUsdtStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyUsdt!=null){
                                            spotDailyUsdt.setVolume(volume);
                                            spotDailyUsdt.setTurnover(turnover);
                                            Utils.bind(spotDailyUsdt, "task");
                                            dailyUsdtListUpdate.add(spotDailyUsdt);
                                        }
                                    }
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
                                    Utils.bind(spotDailyUsdt, "task");

                                    dailyUsdtList.add(spotDailyUsdt);

                                }
                            } else if (market.equals("btc")) {
                                Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyBtc spotDailyBtc = spotDailyBtcStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyBtc!=null){
                                            spotDailyBtc.setVolume(volume);
                                            spotDailyBtc.setTurnover(turnover);
                                            Utils.bind(spotDailyBtc, "task");
                                            dailyBtcListUpdate.add(spotDailyBtc);
                                        }
                                    }
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
                                    Utils.bind(spotDailyBtc, "task");

                                    dailyBtcList.add(spotDailyBtc);

                                }
                            } else if (market.equals("eth")) {
                                Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyEth spotDailyEth = spotDailyEthStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyEth!=null){
                                            spotDailyEth.setVolume(volume);
                                            spotDailyEth.setTurnover(turnover);
                                            Utils.bind(spotDailyEth, "task");
                                            dailyEthListUpdate.add(spotDailyEth);
                                        }
                                    }
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
                                    Utils.bind(spotDailyEth, "task");

                                    dailyEthList.add(spotDailyEth);

                                }
                            }
                        }

                        saveOrUpdate(spotSymbol,dailyUsdtList,dailyBtcList,dailyEthList,
                                dailyUsdtListUpdate,dailyBtcListUpdate,dailyEthListUpdate,
                                spotDailyUsdtStore,spotDailyBtcStore,spotDailyEthStore,releaseEnvironment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultObj.toString();
    }

    @Override
    public String dailyForBitmex(SpotSymbol spotSymbol, SysTrade sysTrade,String releaseEnvironment,Date dailyDate,boolean override) throws BizException {
        return null;
    }

    @Override
    public String dailyForBinance(SpotSymbol spotSymbol, SysTrade sysTrade,String releaseEnvironment,Date dailyDate,boolean override) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
        SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
        SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
        if (spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
            String market = spotSymbol.getMarket();
            Map<String, String> params = new HashMap<>();
            params.put("startTime", dailyDate.getTime() + "");//从前一天开始返回
            params.put("limit", "1");//只显示前一天数据
            params.put("interval", "1d");//按天返回
            params.put("symbol", spotSymbol.getSymbol());
            try {
                String resultStr = OKHttpUtil.get("https://api.binance.com/api/v1/klines", params);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONArray klineArray = JSONArray.fromObject(resultStr);
                    if (klineArray != null && !klineArray.isEmpty()) {
                        List<SpotDailyUsdt> dailyUsdtListSave = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcListSave = new ArrayList<>();
                        List<SpotDailyEth> dailyEthListSave = new ArrayList<>();

                        List<SpotDailyUsdt> dailyUsdtListUpdate = new ArrayList<>();
                        List<SpotDailyBtc> dailyBtcListUpdate = new ArrayList<>();
                        List<SpotDailyEth> dailyEthListUpdate = new ArrayList<>();

                        JSONArray dayAttr = klineArray.getJSONArray(0);
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            Double lastPrice = dayAttr.getDouble(4);
                            Double volume = dayAttr.getDouble(5);
                            Double turnover = dayAttr.getDouble(7);
                            Date timeDate = new Date(times);
                            String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                            Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                            if (market.equals("usdt")) {
                                Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyUsdt spotDailyUsdt = spotDailyUsdtStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyUsdt!=null){
                                            spotDailyUsdt.setVolume(volume);
                                            spotDailyUsdt.setTurnover(turnover);
                                            Utils.bind(spotDailyUsdt, "task");
                                            dailyUsdtListUpdate.add(spotDailyUsdt);
                                        }
                                    }
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

                                    Utils.bind(spotDailyUsdt, "task");
                                    dailyUsdtListSave.add(spotDailyUsdt);

                                }
                            } else if (market.equals("btc")) {
                                Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyBtc spotDailyBtc = spotDailyBtcStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyBtc!=null){
                                            spotDailyBtc.setVolume(volume);
                                            spotDailyBtc.setTurnover(turnover);
                                            Utils.bind(spotDailyBtc, "task");
                                            dailyBtcListUpdate.add(spotDailyBtc);
                                        }
                                    }
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

                                    Utils.bind(spotDailyBtc, "task");
                                    dailyBtcListSave.add(spotDailyBtc);

                                }
                            } else if (market.equals("eth")) {
                                Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                        spotSymbol.getId(), tradingDate);
                                if (count == null) {
                                    count = 0;
                                }
                                if (count.intValue() > 0) {
                                    System.out.println(dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                    if(override){
                                        SpotDailyEth spotDailyEth = spotDailyEthStore.getTradeSymbolDay(sysTrade.getId(),
                                                spotSymbol.getId(), tradingDate);
                                        if(spotDailyEth!=null){
                                            spotDailyEth.setVolume(volume);
                                            spotDailyEth.setTurnover(turnover);
                                            Utils.bind(spotDailyEth, "task");
                                            dailyEthListUpdate.add(spotDailyEth);
                                        }
                                    }
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

                                    Utils.bind(spotDailyEth, "task");
                                    dailyEthListSave.add(spotDailyEth);

                                }
                            }
                        }
                        saveOrUpdate(spotSymbol,dailyUsdtListSave,dailyBtcListSave,dailyEthListSave,
                                dailyUsdtListUpdate,dailyBtcListUpdate,dailyEthListUpdate,
                                spotDailyUsdtStore,spotDailyBtcStore,spotDailyEthStore,releaseEnvironment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultObj.toString();
    }

    private void saveOrUpdate(SpotSymbol spotSymbol,List<SpotDailyUsdt> dailyUsdtListSave,
                              List<SpotDailyBtc> dailyBtcListSave,
                              List<SpotDailyEth> dailyEthListSave,
                              List<SpotDailyUsdt> dailyUsdtListUpdate,
                              List<SpotDailyBtc> dailyBtcListUpdate,
                              List<SpotDailyEth> dailyEthListUpdate,
                              SpotDailyUsdtStore spotDailyUsdtStore,
                              SpotDailyBtcStore spotDailyBtcStore,
                              SpotDailyEthStore spotDailyEthStore,String releaseEnvironment) throws Exception {
        if (dailyUsdtListSave != null && !dailyUsdtListSave.isEmpty()) {
            spotDailyUsdtStore.save(dailyUsdtListSave, Persistent.SAVE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyUsdtListSave != null && !dailyUsdtListSave.isEmpty()) {
                    for (SpotDailyUsdt usdt : dailyUsdtListSave) {
                        Utils.setDailySymbolPrice(usdt.getSymbolId(), usdt.getLastPrice(),usdt.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " save success ===task========" + dailyUsdtListSave.size());

        }
        if (dailyBtcListSave != null && !dailyBtcListSave.isEmpty()) {
            spotDailyBtcStore.save(dailyBtcListSave, Persistent.SAVE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyBtcListSave != null && !dailyBtcListSave.isEmpty()) {
                    for (SpotDailyBtc btc : dailyBtcListSave) {
                        Utils.setDailySymbolPrice(btc.getSymbolId(), btc.getLastPrice(),btc.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " save success ====task=======" + dailyBtcListSave.size());

        }
        if (dailyEthListSave != null && !dailyEthListSave.isEmpty()) {
            spotDailyEthStore.save(dailyEthListSave, Persistent.SAVE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyEthListSave != null && !dailyEthListSave.isEmpty()) {
                    for (SpotDailyEth eth : dailyEthListSave) {
                        Utils.setDailySymbolPrice(eth.getSymbolId(), eth.getLastPrice(),eth.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " save success ====task=======" + dailyEthListSave.size());

        }



        if (dailyUsdtListUpdate != null && !dailyUsdtListUpdate.isEmpty()) {
            spotDailyUsdtStore.save(dailyUsdtListUpdate, Persistent.UPDATE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyUsdtListUpdate != null && !dailyUsdtListUpdate.isEmpty()) {
                    for (SpotDailyUsdt usdt : dailyUsdtListUpdate) {
                        Utils.setDailySymbolPrice(usdt.getSymbolId(), usdt.getLastPrice(),usdt.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " update success ===task========" + dailyUsdtListUpdate.size());
        }
        if (dailyBtcListUpdate != null && !dailyBtcListUpdate.isEmpty()) {
            spotDailyBtcStore.save(dailyBtcListUpdate, Persistent.UPDATE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyBtcListUpdate != null && !dailyBtcListUpdate.isEmpty()) {
                    for (SpotDailyBtc btc : dailyBtcListUpdate) {
                        Utils.setDailySymbolPrice(btc.getSymbolId(), btc.getLastPrice(),btc.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " update success ====task=======" + dailyBtcListUpdate.size());

        }
        if (dailyEthListUpdate != null && !dailyEthListUpdate.isEmpty()) {
            spotDailyEthStore.save(dailyEthListUpdate, Persistent.UPDATE);
            if (releaseEnvironment.equals("DIS")) {
                if (dailyEthListUpdate != null && !dailyEthListUpdate.isEmpty()) {
                    for (SpotDailyEth eth : dailyEthListUpdate) {
                        Utils.setDailySymbolPrice(eth.getSymbolId(), eth.getLastPrice(),eth.getTradingDay());
                    }
                }
            }
            System.out.println(spotSymbol.getSymbol() + " update success ====task=======" + dailyEthListUpdate.size());
        }
    }
}
