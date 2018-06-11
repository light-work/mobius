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

package com.mobius.task.detail;

import com.google.inject.Injector;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import com.mobius.providers.store.sys.SysIpServerStore;
import com.mobius.providers.store.sys.SysTradeStore;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
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
public class DetailTaskForUsdt implements Job {


    private String market = "usdt";


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DetailTaskForUsdt() {
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
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Injector injector = (Injector) dataMap.get("injector");
        String localIP = dataMap.getString("localIP");
        if (injector != null && StringUtils.isNotBlank(localIP)) {
            HSFServiceFactory hsfServiceFactory = injector.getInstance(HSFServiceFactory.class);
            if (hsfServiceFactory != null) {
                try {
                    SysIpServerStore sysIpServerStore = hsfServiceFactory.consumer(SysIpServerStore.class);
                    if (sysIpServerStore != null) {
                        SysIpServer sysIpServer = sysIpServerStore.getByIpServerMarket(localIP, market);
                        if (sysIpServer != null) {
                            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
                            if (calSampleSpotSymbolWeightStore != null && sysTradeStore != null) {
                                Date d = DateFormatUtil.getCurrentDate(true);
                                Date weightDate = DateFormatUtil.getCurrentDate(false);
                                weightDate = DateFormatUtil.addMonth(weightDate, -1);
                                Integer weightYear = DateFormatUtil.getDayInYear(weightDate);
                                Integer weightMonth = DateFormatUtil.getDayInMonth(weightDate) + 1;

                                System.out.println(d.getTime()+" ===");


                                SysTrade sysTradeBinance = sysTradeStore.getBySign("BINANCE");
                                if (sysTradeBinance != null) {
                                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                                            sysTradeBinance.getId(), market, sysIpServer.getServerNo());
                                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                                            try {
                                                SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                                if (spotSymbol != null) {
                                                    FastCallForBinance.call(calSampleSpotSymbolWeight, sysTradeBinance, spotSymbol, hsfServiceFactory, d);
                                                }
                                            } catch (Exception e) {

                                            }
                                        }
                                    }
                                }

                                SysTrade sysTradeHuoBi = sysTradeStore.getBySign("HUOBIPRO");
                                if (sysTradeHuoBi != null) {
                                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                                            sysTradeHuoBi.getId(), market, sysIpServer.getServerNo());
                                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                                            SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                            if (spotSymbol != null) {
                                                FastCallForHuobi.callUsdt(calSampleSpotSymbolWeight, spotSymbol, hsfServiceFactory, sysTradeHuoBi, d);
                                            }
                                        }
                                    }
                                }

                                SysTrade sysTradeOKEX = sysTradeStore.getBySign("OKEX");
                                if (sysTradeOKEX != null) {
                                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                                            sysTradeOKEX.getId(), market, sysIpServer.getServerNo());
                                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                                            SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                            if (spotSymbol != null) {
                                                FastCallForOkex.callSpotUsdt(calSampleSpotSymbolWeight, spotSymbol, hsfServiceFactory, sysTradeOKEX, d);
                                            }
                                        }
                                    }
                                }

                                SysTrade sysTradeBITFINEX = sysTradeStore.getBySign("BITFINEX");
                                if (sysTradeBITFINEX != null) {
                                    List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                                            sysTradeBITFINEX.getId(), market, sysIpServer.getServerNo());
                                    if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                                        List<SpotSymbol> spotSymbolList = new ArrayList<>();
                                        Map<String, CalSampleSpotSymbolWeight> sampleSpotSymbolWeightMap = new HashMap<>();
                                        Map<String, SpotSymbol> spotSymbolHashMap = new HashMap<>();

                                        for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                                            try {
                                                SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                                if (spotSymbol != null) {
                                                    spotSymbolList.add(spotSymbol);
                                                    sampleSpotSymbolWeightMap.put("t" + spotSymbol.getSymbol().toUpperCase(), calSampleSpotSymbolWeight);
                                                    spotSymbolHashMap.put("t" + spotSymbol.getSymbol().toUpperCase(), spotSymbol);
                                                }
                                            } catch (Exception e) {

                                            }
                                        }
                                        try {
                                            FastCallForBitfinex.call(sampleSpotSymbolWeightMap, spotSymbolHashMap, sysTradeBITFINEX, spotSymbolList, hsfServiceFactory, d);
                                        } catch (Exception e) {

                                        }
                                    }
                                }
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
