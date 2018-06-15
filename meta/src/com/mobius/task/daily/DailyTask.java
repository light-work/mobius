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
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.biz.meta.DailyBiz;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysIpServerStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
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
public class DailyTask implements Job {



    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public DailyTask() {
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
        String releaseEnvironment = dataMap.getString("releaseEnvironment");
        if (injector != null) {
            HSFServiceFactory hsfServiceFactory = injector.getInstance(HSFServiceFactory.class);
            if (hsfServiceFactory != null) {
                try {
                    buildDaily(hsfServiceFactory, localIP,releaseEnvironment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void buildDaily(HSFServiceFactory hsfServiceFactory, String localIP,String releaseEnvironment ) throws Exception {
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        SysIpServerStore sysIpServerStore = hsfServiceFactory.consumer(SysIpServerStore.class);
        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
        DailyBiz dailyBiz = hsfServiceFactory.consumer(DailyBiz.class);
        if (sysTradeStore != null && sysIpServerStore != null && dailyBiz != null && spotSymbolStore != null) {
            List<String> marketList = new ArrayList<>();
            marketList.add("usdt");
            marketList.add("btc");
            marketList.add("eth");

            Date dailyDate=DateFormatUtil.getCurrentDate(false);
            dailyDate=DateFormatUtil.addDay(dailyDate,-2);//减去2天
            for (String market : marketList) {
                SysIpServer sysIpServer = sysIpServerStore.getByIpServerMarket(localIP, market);
                if (sysIpServer != null) {
                    SysTrade sysTradeBINANCE = sysTradeStore.getBySign("BINANCE");
                    if (sysTradeBINANCE != null) {
                        List<SpotSymbol> spotSymbolListBINANCE = spotSymbolStore.getListByTradeMarketServer(sysTradeBINANCE.getId(), market, sysIpServer.getServerNo());
                        if (spotSymbolListBINANCE != null && !spotSymbolListBINANCE.isEmpty()) {
                            for (SpotSymbol spotSymbol : spotSymbolListBINANCE) {
                                try {
                                    dailyBiz.dailyForBinance(spotSymbol, sysTradeBINANCE,releaseEnvironment,dailyDate,true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    System.out.println("============********======task======sleep start binance serverNo=" + sysIpServer.getServerNo());
                                    TimeUnit.MILLISECONDS.sleep(500);//秒
                                    System.out.println("============********======task======sleep end binance serverNo=" + sysIpServer.getServerNo());
                                }

                            }
                        }
                    }


                    SysTrade sysTradeBITFINEX = sysTradeStore.getBySign("BITFINEX");

                    if (sysTradeBITFINEX != null) {
                        List<SpotSymbol> spotSymbolListBITFINEX = spotSymbolStore.getListByTradeMarketServer(sysTradeBITFINEX.getId(), market, sysIpServer.getServerNo());
                        if (spotSymbolListBITFINEX != null && !spotSymbolListBITFINEX.isEmpty()) {
                            for (SpotSymbol spotSymbol : spotSymbolListBITFINEX) {
                                try {
                                    dailyBiz.dailyForBitfinex(spotSymbol, sysTradeBITFINEX,releaseEnvironment,dailyDate,true);
                                } catch (Exception e) {
                                    e.printStackTrace();

                                } finally {
                                    System.out.println("============********======task======sleep start bitfinex serverNo=" + sysIpServer.getServerNo());
                                    TimeUnit.SECONDS.sleep(20);//秒
                                    System.out.println("============********======task======sleep end bitfinex serverNo=" + sysIpServer.getServerNo());
                                }
                            }
                        }
                    }


                    SysTrade sysTradeOKEX = sysTradeStore.getBySign("OKEX");
                    if (sysTradeOKEX != null) {
                        List<SpotSymbol> spotSymbolListOKEX = spotSymbolStore.getListByTradeMarketServer(sysTradeOKEX.getId(), market, sysIpServer.getServerNo());
                        if (spotSymbolListOKEX != null && !spotSymbolListOKEX.isEmpty()) {
                            for (SpotSymbol spotSymbol : spotSymbolListOKEX) {
                                try {
                                    dailyBiz.dailyForOkex(spotSymbol, sysTradeOKEX,releaseEnvironment,dailyDate,true);
                                } catch (Exception e) {
                                    e.printStackTrace();

                                } finally {
                                    System.out.println("============********======task======sleep start okex serverNo=" + sysIpServer.getServerNo());
                                    TimeUnit.SECONDS.sleep(2);//秒
                                    System.out.println("============********======task======sleep end okex serverNo=" + sysIpServer.getServerNo());
                                }
                            }
                        }
                    }


                    SysTrade sysTradeHUOBIPRO = sysTradeStore.getBySign("HUOBIPRO");
                    if (sysTradeHUOBIPRO != null) {
                        List<SpotSymbol> spotSymbolListHUOBIPRO = spotSymbolStore.getListByTradeMarketServer(sysTradeHUOBIPRO.getId(), market, sysIpServer.getServerNo());
                        if (spotSymbolListHUOBIPRO != null && !spotSymbolListHUOBIPRO.isEmpty()) {
                            for (SpotSymbol spotSymbol : spotSymbolListHUOBIPRO) {
                                try {
                                    dailyBiz.dailyForHuobiPro(spotSymbol, sysTradeHUOBIPRO,releaseEnvironment,dailyDate,true);
                                } catch (Exception e) {
                                    e.printStackTrace();

                                } finally {
                                    System.out.println("============********======task======sleep start huobi serverNo=" + sysIpServer.getServerNo());
                                    TimeUnit.SECONDS.sleep(2);//秒
                                    System.out.println("============********======task======sleep end huobi serverNo=" + sysIpServer.getServerNo());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
