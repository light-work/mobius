package com.mobius.task;

import com.google.gson.JsonObject;
import com.google.inject.Injector;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.entity.sys.SysCapitalizationLast;
import com.mobius.entity.sys.SysCoin;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.sys.SysCapitalizationLastStore;
import com.mobius.providers.store.sys.SysCapitalizationStore;
import com.mobius.providers.store.sys.SysCoinStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.quartz.*;

import java.util.*;
import java.util.Calendar;

/**
 * Created by Lara Croft on 2018/5/27.
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CapitalizationTask implements Job {


    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public CapitalizationTask() {
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
                    getTicker(hsfServiceFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void getTicker(HSFServiceFactory hsfServiceFactory) throws Exception {
        SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);
        SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);

        if (sysCapitalizationStore != null && sysCoinStore != null) {
            List<Selector> vailDataExistSelector = new ArrayList<>();
            vailDataExistSelector.add(SelectorUtils.$eq("recordDate", DateFormatUtil.getCurrentDate(false)));
            List<SysCapitalization> todayList = sysCapitalizationStore.getList(vailDataExistSelector);
            if (todayList != null && !todayList.isEmpty()) {
                return;
            }
            List<Selector> selectorList = new ArrayList<>();
            selectorList.add(SelectorUtils.$eq("useYn", "Y"));
            List<SysCoin> sysCoinList = sysCoinStore.getList(selectorList);

            Map<Long, SysCoin> coinMap = new HashMap<>();
            if (!sysCoinList.isEmpty()) {
                for (SysCoin coin : sysCoinList) {
                    coinMap.put(coin.getId(), coin);
                }
                //
                List<SysCapitalization> sysCapitalizationList = new ArrayList<>();
                //
                Map<String, String> params = new HashMap<>();
                params.put("sort", "id");
                params.put("structure", "array");
                params.put("limit", "100");
                Date date = Calendar.getInstance().getTime();
                for (int x = 0; x < sysCoinList.size(); x += 100) {
                    params.put("start", (x + 1) + "");
                    String result = OKHttpUtil.get("https://api.coinmarketcap.com/v2/ticker/?structure=array", params);
                    if (StringUtils.isNotBlank(result)) {
                        JSONObject root = JSONObject.fromObject(result);
                        if (root != null && root.containsKey("data")) {
                            JSONArray array = root.getJSONArray("data");
                            if (array != null && !array.isEmpty()) {


                                for (Object obj : array) {
                                    JSONObject data = JSONObject.fromObject(obj);
                                    SysCapitalization sysCapitalization = new SysCapitalization();
                                    sysCapitalization.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                    if (coinMap.get(data.getLong("id")) == null) {
                                        System.out.print("-------coin cant find and coin id is " + data.getLong("id")
                                                + " name=" + data.getString("name") + " symbol=" + data.getString("symbol") + "  start=" + (x + 1));
                                    }
                                    sysCapitalization.setCoinId(coinMap.get(data.getLong("id")));
                                    sysCapitalization.setCirculating(0d);
                                    if (data.containsKey("circulating_supply") && !"null".equals(data.getString("circulating_supply") + "")) {
                                        sysCapitalization.setCirculating(data.getDouble("circulating_supply"));
                                    }
                                    sysCapitalization.setVolume(0d);
                                    if (data.containsKey("quotes")) {
                                        JSONObject quotes = data.getJSONObject("quotes");
                                        if (quotes.containsKey("USD")) {
                                            JSONObject usd = quotes.getJSONObject("USD");
                                            if (usd.containsKey("market_cap") && !"null".equals(usd.getString("market_cap") + "")) {
                                                sysCapitalization.setVolume(usd.getDouble("market_cap"));
                                            }
                                        }
                                    }
                                    sysCapitalization.setRecordDate(date);
                                    sysCapitalization.setRecordTime(date);
                                    sysCapitalization.setCreated(date);
                                    sysCapitalization.setUseYn("Y");
                                    sysCapitalizationList.add(sysCapitalization);
                                }
                            }
                        }
                    }
                }
                if (!sysCapitalizationList.isEmpty()) {
                    System.out.println("CoinMarketCapAction task save star.......");
                    sysCapitalizationStore.save(sysCapitalizationList, Persistent.SAVE);
                    System.out.println("CoinMarketCapAction task save over");
                }
            }


        }
    }

}
