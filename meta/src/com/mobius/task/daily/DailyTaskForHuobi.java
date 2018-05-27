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
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 *
 * @author Bill Kratzer
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DailyTaskForHuobi implements Job {


    private String tradeSign = "HUOBIPRO";


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DailyTaskForHuobi() {
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

        System.out.println("run DailyTaskForHuobi ");
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
                            params.put("period", "1day");
                            params.put("size", "2");
                            for (SpotSymbol spotSymbol : symbolList) {
                                params.put("symbol", spotSymbol.getSymbol());
                                try {
                                    Set<String> dateSet = new HashSet<>();
                                    String result = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                    if (StringUtils.isNotBlank(result)) {
                                        JSONObject obj = JSONObject.fromObject(result);
                                        if (!obj.containsKey("data")) {
                                            System.out.print("huobi  kline data was null!");
                                            return;
                                        }
                                        JSONArray klineArray = obj.getJSONArray("data");
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<SpotDailyUsdt> usdtList = new ArrayList<>();
                                            List<SpotDailyBtc> btcList = new ArrayList<>();
                                            List<SpotDailyEth> ethList = new ArrayList<>();
                                            for (int x = 1; x < klineArray.size(); x++) {// 3 2 1
                                                JSONObject jsonObj = klineArray.getJSONObject(x);
                                                String dateStr = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                        DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                if (dateSet.contains(dateStr)) {
                                                    continue;
                                                }
                                                dateSet.add(dateStr);
                                                if (market.equals("usdt")) {
                                                    Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == 1) {
                                                        System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
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
                                            if (!usdtList.isEmpty()) {
                                                spotDailyUsdtStore.save(usdtList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + usdtList.size());
                                            }
                                            if (!btcList.isEmpty()) {
                                                spotDailyBtcStore.save(btcList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + btcList.size());
                                            }
                                            if (!ethList.isEmpty()) {
                                                spotDailyEthStore.save(ethList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + ethList.size());
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
                }
            }
        }
    }

}
