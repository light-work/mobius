package com.mobius.task.daily;

import com.google.inject.Injector;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lara Croft on 2018/5/25.
 */

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DetailTaskForHuobi implements Job {


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
    public DetailTaskForHuobi() {
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
                    //TODO
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void usdtTask(HSFServiceFactory hsfServiceFactory) {
        String action = "https://api.huobi.pro/market/detail/merged";
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                List<String> marketList = new ArrayList<>();
                marketList.add("usdt");
                marketList.add("btc");
                marketList.add("eth");
                SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
            }
        }
    }
}
