package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.Utils;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.providers.store.futures.FuturesSymbolStore;
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

@Action(name = "bitmex", namespace = "/daily")
public class DailyBitmexAction extends BaseAction {

    private static String tradeSign = "BITMEX";


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }



    private long usdt(FuturesSymbol futuresSymbol, long startTime, Map<String, String> params, SysTrade sysTrade,
                      FuturesDailyUsdtStore futuresDailyUsdtStore) throws Exception {
        Date d = null;
        if (startTime == 0) {
            d = DateFormatUtil.parse("2017-01-01 00:00", DateFormatUtil.YMDHM_PATTERN);
        } else {
            d = new Date(startTime);
        }
        params.put("partial", "false");
        params.put("symbol", futuresSymbol.getSymbol());
        params.put("count", "500");
        params.put("reverse", "false");
        params.put("startTime", DateFormatUtil.format(d, DateFormatUtil.YMDHM_PATTERN));
        long endTime = 0l;
        try {
            long s = System.currentTimeMillis();
            String resultStr = OKHttpUtil.get("https://www.bitmex.com/api/v1/trade/bucketed", params);
            if (StringUtils.isNotBlank(resultStr)) {
                JSONArray klineArray = JSONArray.fromObject(resultStr);
                if (klineArray != null && !klineArray.isEmpty()) {
                    List<FuturesDailyUsdt> dailyUsdtList = new ArrayList<>();
                    Date curDate = DateFormatUtil.getCurrentDate(false);
                    int curYear = DateFormatUtil.getDayInYear(curDate);
                    int curMonth = DateFormatUtil.getDayInMonth(curDate) + 1;
                    int curDay = DateFormatUtil.getDayInDay(curDate);
                    Set<String> dateSet=new HashSet<>();

                    for (Object dayObj : klineArray) {
                        JSONObject dayAttr = (JSONObject) dayObj;
                        String timesStr = dayAttr.getString("timestamp");
                        Date timeDate = Utils.parseDateForZ(timesStr);
                        endTime = timeDate.getTime();
                        Double lastPrice = dayAttr.getDouble("close");
                        Double volume = dayAttr.getDouble("volume");
                        Double turnover = dayAttr.getDouble("turnover");

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


                        FuturesDailyUsdt futuresDailyUsdt = new FuturesDailyUsdt();
                        futuresDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                        futuresDailyUsdt.setTradeId(sysTrade);
                        futuresDailyUsdt.setSymbolId(futuresSymbol);
                        futuresDailyUsdt.setTradingDay(timeDate);
                        futuresDailyUsdt.setLastPrice(lastPrice);
                        futuresDailyUsdt.setVolume(volume);
                        futuresDailyUsdt.setTurnover(turnover);

                        dailyUsdtList.add(futuresDailyUsdt);
                    }
                    if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                        futuresDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                        System.out.println(futuresSymbol.getSymbol() + " save success============================= " + dailyUsdtList.size());
                    }

                    long e = System.currentTimeMillis();
                    System.out.println("============********============"+TimeUtils.getTimeDiff(s, e));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            endTime=0;
        }finally {
            System.out.println("============********============sleep start" );
            TimeUnit.SECONDS.sleep(2);//秒
            System.out.println("============********============sleep end" );
        }

        return endTime;
    }

    public String buildFuturesUsdt() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        FuturesSymbolStore futuresSymbolStore = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                        FuturesDailyUsdtStore futuresDailyUsdtStore = hsfServiceFactory.consumer(FuturesDailyUsdtStore.class);
                        if (futuresSymbolStore != null && futuresDailyUsdtStore != null) {
                            List<FuturesSymbol> symbolList = futuresSymbolStore.getListByTradeMarket(sysTrade.getId(), "usdt");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                //https://www.bitmex.com/api/v1/trade/bucketed?binSize=1d&partial=false&symbol=XBTUSD&count=500&reverse=false&startTime=2016-12-31%2023%3A59

                                params.put("binSize", "1d");
                                Date d=DateFormatUtil.getCurrentDate(false);
                                d=DateFormatUtil.addDay(d,-1);
                                long currentDayTime = d.getTime();
                                for (FuturesSymbol spotSymbol : symbolList) {
                                    long endTime = usdt(spotSymbol, 0l, params, sysTrade, futuresDailyUsdtStore);
                                    //2017-01-22
                                    //2018-05-24
                                    while (endTime>0&&endTime < currentDayTime) {
                                        System.out.println(spotSymbol.getSymbol() + " while ");
                                        endTime = usdt(spotSymbol, endTime, params, sysTrade, futuresDailyUsdtStore);
                                    }
                                }

                                System.out.println(" over========================================== ");
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