/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package com.mobius.task;

import com.google.inject.Injector;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.*;
import java.util.Calendar;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 *
 * @author Bill Kratzer
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DailyTaskForOkex implements Job {


    private String tradeSign = "OKEX";


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DailyTaskForOkex() {
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a
     * <code>{@link Trigger}</code> fires that is associated with
     * the <code>Job</code>.
     * </p>
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        System.out.println("run DailyTaskForOkex ");
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Injector injector = (Injector) dataMap.get("injector");
        System.out.println(injector);
        if (injector != null) {
            HSFServiceFactory hsfServiceFactory = injector.getInstance(HSFServiceFactory.class);
            if (hsfServiceFactory != null) {
                try {
                    buildDaily(hsfServiceFactory);
                    futuresDailyTask(hsfServiceFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void buildDaily(HSFServiceFactory hsfServiceFactory) throws Exception {
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
                            params.put("type", "12hour");
                            params.put("since", DateFormatUtil.getCurrentDate(false).getTime() + "");
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
                                            for (Object dayObj : klineArray) {
                                                JSONArray dayAttr = (JSONArray) dayObj;
                                                if (dayAttr != null && !dayAttr.isEmpty()) {
                                                    Long times = dayAttr.getLong(0);
                                                    Double lastPrice = dayAttr.getDouble(4);
                                                    Double volume = dayAttr.getDouble(5);
                                                    Date timeDate = new Date(times);
                                                    int hours = DateFormatUtil.getDayInHour(timeDate);
                                                    if (hours == 12) {
                                                        continue;
                                                    }
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
                                                            spotDailyUsdt.setCreatedBy("task");
                                                            spotDailyUsdt.setCreated(new Date());
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
                                                            spotDailyBtc.setCreatedBy("task");
                                                            spotDailyBtc.setCreated(new Date());
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
                                                            spotDailyEth.setCreatedBy("task");
                                                            spotDailyEth.setCreated(new Date());
                                                            dailyEthList.add(spotDailyEth);
                                                        }
                                                    }
                                                }
                                            }
                                            if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                                                spotDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                                            }
                                            if (dailyBtcList != null && !dailyBtcList.isEmpty()) {
                                                spotDailyBtcStore.save(dailyBtcList, Persistent.SAVE);
                                            }
                                            if (dailyEthList != null && !dailyEthList.isEmpty()) {
                                                spotDailyEthStore.save(dailyEthList, Persistent.SAVE);
                                            }
                                            System.out.println(spotSymbol.getSymbol() + " save success " + dailyUsdtList.size());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private long getLastNightTimeInMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return (c.getTimeInMillis() - 24 * 60 * 60 * 1000) / 1000 * 1000;
    }

    private void futuresDailyTask(HSFServiceFactory hsfServiceFactory) throws Exception {
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
                            params.put("since", getLastNightTimeInMillis() + "");
                            for (FuturesSymbol symbol : symbolList) {
                                params.put("symbol", symbol.getSymbol());
                                params.put("contract_type", symbol.getSymbolDesc());
                                try {
                                    String resultStr = OKHttpUtil.get("https://www.okex.com/api/v1/future_kline.do", params);
                                    if (StringUtils.isNotBlank(resultStr)) {
                                        JSONArray klineArray = JSONArray.fromObject(resultStr);
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<FuturesDailyUsdt> dailyUsdtList = new ArrayList<>();
                                            for (int x = 0; x < klineArray.size() - 1; x++) {
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
                                            System.out.println(symbol.getSymbol() + "okex daily task save success " + dailyUsdtList.size());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
