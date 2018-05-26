package com.mobius.task.detail;


import com.mobius.entity.spot.SpotDetailBtc;
import com.mobius.entity.spot.SpotDetailEth;
import com.mobius.entity.spot.SpotDetailUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDetailBtcStore;
import com.mobius.providers.store.spot.SpotDetailEthStore;
import com.mobius.providers.store.spot.SpotDetailUsdtStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastCallForHuobi {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void callUsdt(final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    SpotDetailUsdtStore spotDetailUsdtStore = hsfServiceFactory.consumer(SpotDetailUsdtStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://api.huobi.pro/market/detail/merged";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                            JSONObject tick = root.getJSONObject("tick");
                            Date date = new Date(root.getLong("ts"));
                            SpotDetailUsdt detail = new SpotDetailUsdt();
                            detail.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                            detail.setTradeId(sysTrade);
                            detail.setSymbolId(symbol);
                            detail.setTradingDay(date);
                            detail.setTradingTime(tradingTime);
                            if (tick.containsKey("close")) {
                                detail.setPrice(tick.getDouble("close"));
                            }
                            if (tick.containsKey("bid")) {
                                JSONArray array = tick.getJSONArray("bid");
                                if (array.size() > 0) {
                                    detail.setBidPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setBidVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("ask")) {
                                JSONArray array = tick.getJSONArray("ask");
                                if (array.size() > 0) {
                                    detail.setAskPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setAskVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("amount")) {
                                detail.setVolume(tick.getDouble("amount"));
                            }
                            if (tick.containsKey("vol")) {
                                detail.setTurnover(tick.getDouble("vol"));
                            }
                            detail.setCreated(tradingTime);
                            detail.setCreatedBy("task");
                            spotDetailUsdtStore.save(detail, Persistent.SAVE);
//                            System.out.println("DetailTaskForHuobiUsdt --- " + symbol.getSymbol() + " save success 1.");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void callBtc(final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    SpotDetailBtcStore detailBtcStore = hsfServiceFactory.consumer(SpotDetailBtcStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://api.huobi.pro/market/detail/merged";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                            JSONObject tick = root.getJSONObject("tick");
                            Date date = new Date(root.getLong("ts"));
                            SpotDetailBtc detail = new SpotDetailBtc();
                            detail.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                            detail.setTradeId(sysTrade);
                            detail.setSymbolId(symbol);
                            detail.setTradingDay(date);
                            detail.setTradingTime(tradingTime);
                            if (tick.containsKey("close")) {
                                detail.setPrice(tick.getDouble("close"));
                            }
                            if (tick.containsKey("bid")) {
                                JSONArray array = tick.getJSONArray("bid");
                                if (array.size() > 0) {
                                    detail.setBidPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setBidVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("ask")) {
                                JSONArray array = tick.getJSONArray("ask");
                                if (array.size() > 0) {
                                    detail.setAskPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setAskVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("amount")) {
                                detail.setVolume(tick.getDouble("amount"));
                            }
                            if (tick.containsKey("vol")) {
                                detail.setTurnover(tick.getDouble("vol"));
                            }
                            detail.setCreated(tradingTime);
                            detail.setCreatedBy("task");
                            detailBtcStore.save(detail, Persistent.SAVE);
//                            System.out.println("DetailTaskForHuobiBtc --- " + symbol.getSymbol() + " save success 1.");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void callEth(final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    SpotDetailEthStore spotDetailEthStore = hsfServiceFactory.consumer(SpotDetailEthStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://api.huobi.pro/market/detail/merged";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                            JSONObject tick = root.getJSONObject("tick");
                            Date date = new Date(root.getLong("ts"));
                            SpotDetailEth detail = new SpotDetailEth();
                            detail.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                            detail.setTradeId(sysTrade);
                            detail.setSymbolId(symbol);
                            detail.setTradingDay(date);
                            detail.setTradingTime(tradingTime);
                            if (tick.containsKey("close")) {
                                detail.setPrice(tick.getDouble("close"));
                            }
                            if (tick.containsKey("bid")) {
                                JSONArray array = tick.getJSONArray("bid");
                                if (array.size() > 0) {
                                    detail.setBidPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setBidVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("ask")) {
                                JSONArray array = tick.getJSONArray("ask");
                                if (array.size() > 0) {
                                    detail.setAskPrice(array.getDouble(0));
                                }
                                if (array.size() > 1) {
                                    detail.setAskVolume(array.getDouble(1));
                                }
                            }
                            if (tick.containsKey("amount")) {
                                detail.setVolume(tick.getDouble("amount"));
                            }
                            if (tick.containsKey("vol")) {
                                detail.setTurnover(tick.getDouble("vol"));
                            }
                            detail.setCreated(tradingTime);
                            detail.setCreatedBy("task");
                            spotDetailEthStore.save(detail, Persistent.SAVE);
//                            System.out.println("DetailTaskForHuobiEth --- " + symbol.getSymbol() + " save success 1.");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
