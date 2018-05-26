package com.mobius.task.detail;

import com.google.inject.Injector;
import com.mobius.entity.spot.SpotDetailUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDetailUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysIpServerStore;
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
import java.util.Calendar;

/**
 * Created by Lara Croft on 2018/5/25.
 */

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DetailTaskForHuobiUsdt implements Job {


    private String tradeSign = "HUOBIPRO";


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
    public DetailTaskForHuobiUsdt() {
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
                            System.out.println("DailyTaskForHuobiUsdt " + DateFormatUtil.getCurrentDateFormat(DateFormatUtil.YMDHMS_PATTERN)
                                    + " 当前ip" + localIP + "获取market " + market +
                                    " serverNo" + sysIpServer.getServerNo() + "的币");

                            //
                            Date current = Calendar.getInstance().getTime();
                            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                            if (sysTradeStore != null) {
                                SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                                if (sysTrade != null) {
                                    SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                                    if (spotSymbolStore != null) {
                                        List<SpotSymbol> spotSymbolList = spotSymbolStore.getListByTradeMarketServer(sysTrade.getId(), market, sysIpServer.getServerNo());
                                        if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                                            for (SpotSymbol symbol : spotSymbolList) {
                                                FastCallForHuobi.callUsdt(symbol, hsfServiceFactory, sysTrade, current);
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
