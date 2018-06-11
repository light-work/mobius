package com.mobius.task.detail;


import com.mobius.Utils;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailBtcHuobi;
import com.mobius.entity.spot.SpotDetailEthHuobi;
import com.mobius.entity.spot.SpotDetailUsdtHuobi;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDetailBtcHuobiStore;
import com.mobius.providers.store.spot.SpotDetailEthHuobiStore;
import com.mobius.providers.store.spot.SpotDetailUsdtHuobiStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.DateFormatUtil;
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


    public static void callUsdt(final CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    Integer y=DateFormatUtil.getDayInYear(tradingTime);
                    Integer m=DateFormatUtil.getDayInMonth(tradingTime)+1;
                    Integer d=DateFormatUtil.getDayInDay(tradingTime);
                    SpotDetailUsdtHuobiStore spotDetailUsdtHuobiStore = hsfServiceFactory.consumer(SpotDetailUsdtHuobiStore.class);
                    if(spotDetailUsdtHuobiStore!=null){
                        Map<String, String> params = new HashMap<>();
                        params.put("symbol", symbol.getSymbol());
                        String action = "https://api.huobi.pro/market/detail/merged";
                        String resultData = OKHttpUtil.get(action, params);
                        if (StringUtils.isNotBlank(resultData)) {
                            JSONObject root = JSONObject.fromObject(resultData);
                            if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                                JSONObject tick = root.getJSONObject("tick");
                                Date date = new Date(root.getLong("ts"));
                                SpotDetailUsdtHuobi detail = new SpotDetailUsdtHuobi();
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
                                calSampleSpotSymbolWeight.setLastPrice(detail.getPrice());
                                Utils.bind(calSampleSpotSymbolWeight,"task");

                                CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice=new CalSampleSpotSymbolWeightPrice();
                                calSampleSpotSymbolWeightPrice.setId(DrdsIDUtils.getID(DrdsTable.SYS));
                                calSampleSpotSymbolWeightPrice.setPrice(detail.getPrice());
                                calSampleSpotSymbolWeightPrice.setSymbolId(calSampleSpotSymbolWeight);
                                calSampleSpotSymbolWeightPrice.setYear(y);
                                calSampleSpotSymbolWeightPrice.setMonth(m);
                                calSampleSpotSymbolWeightPrice.setDay(d);
                                Utils.bind(calSampleSpotSymbolWeightPrice,"task");

                                spotDetailUsdtHuobiStore.save(detail, Persistent.SAVE,calSampleSpotSymbolWeight,calSampleSpotSymbolWeightPrice);
//                            System.out.println("DetailTaskForHuobiUsdt --- " + symbol.getSymbol() + " save success 1.");
                            }

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
                    SpotDetailBtcHuobiStore spotDetailBtcHuobiStore = hsfServiceFactory.consumer(SpotDetailBtcHuobiStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://api.huobi.pro/market/detail/merged";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                            JSONObject tick = root.getJSONObject("tick");
                            Date date = new Date(root.getLong("ts"));
                            SpotDetailBtcHuobi detail = new SpotDetailBtcHuobi();
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
                            spotDetailBtcHuobiStore.save(detail, Persistent.SAVE);
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
                    SpotDetailEthHuobiStore spotDetailEthHuobiStore = hsfServiceFactory.consumer(SpotDetailEthHuobiStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://api.huobi.pro/market/detail/merged";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("tick") && root.getString("status").equals("ok")) {
                            JSONObject tick = root.getJSONObject("tick");
                            Date date = new Date(root.getLong("ts"));
                            SpotDetailEthHuobi detail = new SpotDetailEthHuobi();
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
                            spotDetailEthHuobiStore.save(detail, Persistent.SAVE);
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
