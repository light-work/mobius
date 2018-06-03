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
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysIpServerStore;
import com.mobius.providers.store.sys.SysTradeStore;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 *
 * @author Bill Kratzer
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DetailTaskForBinanceEth implements Job {


    private String tradeSign = "BINANCE";

    private String market = "eth";


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DetailTaskForBinanceEth() {
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
                        System.out.print(System.currentTimeMillis() + " localIP=" + localIP + " market=" + market);
                        SysIpServer sysIpServer = sysIpServerStore.getByIpServerMarket(localIP, market);
                        System.out.println(" sysIpServer=" + sysIpServer.getServerNo());
                        if (sysIpServer != null) {
                            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                            SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                            if (spotSymbolStore != null && sysTradeStore != null) {
                                SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                                if (sysTrade != null) {
                                    Date d = DateFormatUtil.getCurrentDate(true);
                                    List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarketServer(sysTrade.getId(), market, sysIpServer.getServerNo());
                                    if (symbolList != null && !symbolList.isEmpty()) {
                                        for (SpotSymbol spotSymbol : symbolList) {
                                            try {
                                                FastCallForBinance.call(sysTrade, spotSymbol, hsfServiceFactory, d);
                                            } catch (Exception e) {

                                            }

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
