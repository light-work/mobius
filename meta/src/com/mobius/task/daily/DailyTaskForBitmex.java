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

package com.mobius.task.daily;

import com.google.inject.Injector;
import com.mobius.Utils;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesDailyBtcStore;
import com.mobius.providers.store.futures.FuturesDailyEthStore;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.*;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 *
 * @author Bill Kratzer
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DailyTaskForBitmex implements Job {


    private String tradeSign = "BITMEX";


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DailyTaskForBitmex() {
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
                FuturesSymbolStore futuresSymbolStore = hsfServiceFactory.consumer(FuturesSymbolStore.class);
                FuturesDailyUsdtStore futuresDailyUsdtStore = hsfServiceFactory.consumer(FuturesDailyUsdtStore.class);
                FuturesDailyBtcStore futuresDailyBtcStore = hsfServiceFactory.consumer(FuturesDailyBtcStore.class);
                FuturesDailyEthStore futuresDailyEthStore = hsfServiceFactory.consumer(FuturesDailyEthStore.class);
                if (futuresSymbolStore != null && futuresDailyUsdtStore != null && futuresDailyBtcStore != null && futuresDailyEthStore != null) {
                    for (String market : marketList) {
                        List<FuturesSymbol> symbolList = futuresSymbolStore.getListByTradeMarket(sysTrade.getId(), market);
                        if (symbolList != null && !symbolList.isEmpty()) {
                            Map<String, String> params = new HashMap<>();
                            Date d = DateFormatUtil.getCurrentDate(false);
                            d = DateFormatUtil.addDay(d, -1);//减去一天
                            params.put("binSize", "1d");//按天
                            params.put("partial", "false");
                            params.put("symbol", market);
                            params.put("count", "1");//只返回前一天
                            params.put("reverse", "false");//旧的在前面
                            params.put("startTime", DateFormatUtil.format(d, DateFormatUtil.YMDHM_PATTERN));
                            for (FuturesSymbol futuresSymbol : symbolList) {
                                params.put("symbol", futuresSymbol.getSymbol());
                                try {
                                    String resultStr = OKHttpUtil.get("https://www.bitmex.com/api/v1/trade/bucketed", params);
                                    if (StringUtils.isNotBlank(resultStr)) {
                                        JSONArray klineArray = JSONArray.fromObject(resultStr);
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<FuturesDailyUsdt> dailyUsdtList = new ArrayList<>();
                                            JSONObject dayAttr = klineArray.getJSONObject(0);
                                            String timesStr = dayAttr.getString("timestamp");
                                            Date timeDate = Utils.parseDateForZ(timesStr);
                                            Double lastPrice = dayAttr.getDouble("close");
                                            Double volume = dayAttr.getDouble("volume");
                                            Double turnover = dayAttr.getDouble("turnover");
                                            String dateStr = DateFormatUtil.format(timeDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                            Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

                                            if (market.equals("usdt")) {
                                                Integer count = futuresDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                        futuresSymbol.getId(), tradingDate);
                                                if (count == null) {
                                                    count = 0;
                                                } if(count.intValue()>0){
                                                    System.out.println(dateStr + " " + futuresSymbol.getSymbol() + " count >1");
                                                }
                                                if (count.intValue() == 0) {
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
                                            }
                                            if (dailyUsdtList != null && !dailyUsdtList.isEmpty()) {
                                                futuresDailyUsdtStore.save(dailyUsdtList, Persistent.SAVE);
                                                System.out.println(futuresSymbol.getSymbol() + " save success =====task======" + dailyUsdtList.size());

                                            }
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
