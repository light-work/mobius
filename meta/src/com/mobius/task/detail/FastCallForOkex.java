package com.mobius.task.detail;


import com.mobius.Utils;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.futures.FuturesDetailUsdtOkex;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.spot.SpotDetailBtcOkex;
import com.mobius.entity.spot.SpotDetailEthOkex;
import com.mobius.entity.spot.SpotDetailUsdtOkex;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesDetailUsdtOkexStore;
import com.mobius.providers.store.spot.SpotDetailBtcOkexStore;
import com.mobius.providers.store.spot.SpotDetailEthOkexStore;
import com.mobius.providers.store.spot.SpotDetailUsdtOkexStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.BeanUtils;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.NumberUtils;
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
public class FastCallForOkex {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private void setHuobiValue(Object obj, JSONObject tick, SysTrade sysTrade, Date date, SpotSymbol symbol) throws Exception {
        BeanUtils.setValue(obj, "id", DrdsIDUtils.getID(DrdsTable.SPOT));
        BeanUtils.setValue(obj, "tradeId", sysTrade);
        BeanUtils.setValue(obj, "symbolId", symbol);
        BeanUtils.setValue(obj, "tradingDay", date);
        BeanUtils.setValue(obj, "tradingTime", date);
        BeanUtils.setValue(obj, "created", date);
        BeanUtils.setValue(obj, "createdBy", "task");
        if (tick.containsKey("close")) {
            BeanUtils.setValue(obj, "price", tick.getDouble("close"));
        }
        if (tick.containsKey("bid")) {
            JSONArray array = tick.getJSONArray("bid");
            if (array.size() > 0) {
                BeanUtils.setValue(obj, "bidPrice", array.getDouble(0));
            }
            if (array.size() > 1) {
                BeanUtils.setValue(obj, "bidVolume", array.getDouble(1));
            }
        }
        if (tick.containsKey("ask")) {
            JSONArray array = tick.getJSONArray("ask");
            if (array.size() > 0) {
                BeanUtils.setValue(obj, "askPrice", array.getDouble(0));
            }
            if (array.size() > 1) {
                BeanUtils.setValue(obj, "askVolume", array.getDouble(1));
            }
        }
        if (tick.containsKey("amount")) {
            BeanUtils.setValue(obj, "volume", tick.getDouble("amount"));
        }
        if (tick.containsKey("vol")) {
            BeanUtils.setValue(obj, "turnover", tick.getDouble("vol"));
        }
    }

    private static void setValue(Object obj, JSONObject tick, SysTrade sysTrade, Date date, SpotSymbol symbol) throws Exception {
        BeanUtils.setValue(obj, "tradeId", sysTrade);
        BeanUtils.setValue(obj, "symbolId", symbol);
        BeanUtils.setValue(obj, "tradingDay", date);
        BeanUtils.setValue(obj, "tradingTime", date);
        BeanUtils.setValue(obj, "created", date);
        BeanUtils.setValue(obj, "createdBy", "task");
        if (tick.containsKey("last")) {
            BeanUtils.setValue(obj, "price", tick.getDouble("last"));
        }
        if (tick.containsKey("buy")) {
            BeanUtils.setValue(obj, "bidPrice", tick.getDouble("buy"));
            BeanUtils.setValue(obj, "bidVolume", 0);
        }
        if (tick.containsKey("sell")) {
            BeanUtils.setValue(obj, "askPrice", tick.get("sell"));
            BeanUtils.setValue(obj, "askVolume", 0);
        }
        if (tick.containsKey("vol")) {
            BeanUtils.setValue(obj, "volume", tick.getDouble("vol"));
            if (tick.containsKey("last")) {
                BeanUtils.setValue(obj, "turnover", NumberUtils.multiply(tick.getDouble("vol"), tick.getDouble("last"), 8));
            }
        }
    }

    public static void callSpotUsdt(final CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime,
                                    final String releaseEnvironment) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    Integer y=DateFormatUtil.getDayInYear(tradingTime);
                    Integer m=DateFormatUtil.getDayInMonth(tradingTime)+1;
                    Integer d=DateFormatUtil.getDayInDay(tradingTime);
                    SpotDetailUsdtOkexStore spotDetailUsdtOkexStore = hsfServiceFactory.consumer(SpotDetailUsdtOkexStore.class);
                    if(spotDetailUsdtOkexStore!=null){
                        Map<String, String> params = new HashMap<>();
                        params.put("symbol", symbol.getSymbol());
                        String action = "https://www.okex.com/api/v1/ticker.do";
                        String resultData = OKHttpUtil.get(action, params);
                        if (StringUtils.isNotBlank(resultData)) {
                            JSONObject root = JSONObject.fromObject(resultData);
                            if (root.containsKey("ticker")) {
                                JSONObject tick = root.getJSONObject("ticker");
                                SpotDetailUsdtOkex detail = new SpotDetailUsdtOkex();
                                setValue(detail, tick, sysTrade, tradingTime, symbol);

                                CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice=new CalSampleSpotSymbolWeightPrice();
                                calSampleSpotSymbolWeightPrice.setId(DrdsIDUtils.getID(DrdsTable.CAL));
                                calSampleSpotSymbolWeightPrice.setPrice(detail.getPrice());
                                calSampleSpotSymbolWeightPrice.setSymbolId(calSampleSpotSymbolWeight);
                                calSampleSpotSymbolWeightPrice.setYear(y);
                                calSampleSpotSymbolWeightPrice.setMonth(m);
                                calSampleSpotSymbolWeightPrice.setDay(d);
                                calSampleSpotSymbolWeightPrice.setRecordDate(tradingTime);
                                Utils.bind(calSampleSpotSymbolWeightPrice,"task");

                                spotDetailUsdtOkexStore.save(detail, Persistent.SAVE,calSampleSpotSymbolWeightPrice);

                                if (releaseEnvironment.equals("DIS")) {
                                    Utils.setWeightSymbolPrice(calSampleSpotSymbolWeight,detail.getPrice());
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void callSpotBtc(final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    SpotDetailBtcOkexStore spotDetailBtcOkexStore = hsfServiceFactory.consumer(SpotDetailBtcOkexStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://www.okex.com/api/v1/ticker.do";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("ticker")) {
                            JSONObject tick = root.getJSONObject("ticker");
                            SpotDetailBtcOkex detail = new SpotDetailBtcOkex();
                            setValue(detail, tick, sysTrade, tradingTime, symbol);
                            spotDetailBtcOkexStore.save(detail, Persistent.SAVE);
                            System.out.println("DetailTaskForOkexBtc --- " + symbol.getSymbol() + " save success.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void callSpotEth(final SpotSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    SpotDetailEthOkexStore spotDetailEthOkexStore = hsfServiceFactory.consumer(SpotDetailEthOkexStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    String action = "https://www.okex.com/api/v1/ticker.do";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("ticker")) {
                            JSONObject tick = root.getJSONObject("ticker");
                            SpotDetailEthOkex detail = new SpotDetailEthOkex();
                            setValue(detail, tick, sysTrade, tradingTime, symbol);
                            spotDetailEthOkexStore.save(detail, Persistent.SAVE);
                            System.out.println("DetailTaskForOkexEth --- " + symbol.getSymbol() + " save success.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void callFuturesUsdt(final FuturesSymbol symbol, final HSFServiceFactory hsfServiceFactory, final SysTrade sysTrade, final Date tradingTime) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    FuturesDetailUsdtOkexStore futuresDetailUsdtOkexStore = hsfServiceFactory.consumer(FuturesDetailUsdtOkexStore.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("symbol", symbol.getSymbol());
                    params.put("contract_type", symbol.getSymbolDesc());
                    String action = "https://www.okex.com/api/v1/future_ticker.do";
                    String resultData = OKHttpUtil.get(action, params);
                    if (StringUtils.isNotBlank(resultData)) {
                        JSONObject root = JSONObject.fromObject(resultData);
                        if (root.containsKey("ticker")) {
                            JSONObject tick = root.getJSONObject("ticker");
                            FuturesDetailUsdtOkex detail = new FuturesDetailUsdtOkex();
                            setValue(detail, tick, sysTrade, tradingTime, null);
                            detail.setSymbolId(symbol);
                            futuresDetailUsdtOkexStore.save(detail, Persistent.SAVE);
                            System.out.println("DetailTaskForOkexFuturesUsdt --- " + symbol.getSymbol() + " save success.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
