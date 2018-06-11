package com.mobius.task.detail;

import com.google.inject.Injector;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysIpServerStore;
import com.mobius.providers.store.sys.SysTradeStore;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2018/5/25.
 */

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DetailTaskForOkexSpotUsdt implements Job {


    private String tradeSign = "OKEX";


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
    public DetailTaskForOkexSpotUsdt() {
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
//                            System.out.println("DailyTaskForOKexSpotUsdt " + DateFormatUtil.getCurrentDateFormat(DateFormatUtil.YMDHMS_PATTERN)
//                                    + " 当前ip" + localIP + "获取market " + market +
//                                    " serverNo" + sysIpServer.getServerNo() + "的币");

                            //
                            Date current = Calendar.getInstance().getTime();
                            SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                            if (sysTradeStore != null) {
                                SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                                if (sysTrade != null) {
                                    CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
                                    if (calSampleSpotSymbolWeightStore != null) {
                                        Date weightDate=DateFormatUtil.getCurrentDate(false);
                                        weightDate=DateFormatUtil.addMonth(weightDate,-1);
                                        Integer weightYear=DateFormatUtil.getDayInYear(weightDate);
                                        Integer weightMonth=DateFormatUtil.getDayInMonth(weightDate)+1;
                                        List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getListByYearMonthTradeMarketServerNo(weightYear, weightMonth,
                                                sysTrade.getId(), market, sysIpServer.getServerNo());
                                        if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                                            for (CalSampleSpotSymbolWeight calSampleSpotSymbolWeight : calSampleSpotSymbolWeightList) {
                                                SpotSymbol spotSymbol = calSampleSpotSymbolWeight.getSymbolId();
                                                if (spotSymbol != null) {
                                                    FastCallForOkex.callSpotUsdt(calSampleSpotSymbolWeight,spotSymbol, hsfServiceFactory, sysTrade, current);
                                                }
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
