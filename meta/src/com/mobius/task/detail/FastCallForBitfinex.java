package com.mobius.task.detail;


import com.mobius.OKHttpUtils;
import com.mobius.Utils;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailBtcBitfinex;
import com.mobius.entity.spot.SpotDetailEthBitfinex;
import com.mobius.entity.spot.SpotDetailUsdtBitfinex;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDetailBtcBitfinexStore;
import com.mobius.providers.store.spot.SpotDetailEthBitfinexStore;
import com.mobius.providers.store.spot.SpotDetailUsdtBitfinexStore;
import net.sf.json.JSONArray;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastCallForBitfinex {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void call(final Map<String, CalSampleSpotSymbolWeight> sampleSpotSymbolWeightMap,
                            final Map<String, SpotSymbol> spotSymbolHashMap,
                            final SysTrade sysTrade, final List<SpotSymbol> spotSymbolList, final HSFServiceFactory hsfServiceFactory,
                            final Date tradingDate,final String releaseEnvironment) {
        executorService.execute(new Runnable() {
            public void run() {
                try {

                    SpotDetailUsdtBitfinexStore spotDetailUsdtBitfinexStore = hsfServiceFactory.consumer(SpotDetailUsdtBitfinexStore.class);
                    SpotDetailEthBitfinexStore spotDetailEthBitfinexStore = hsfServiceFactory.consumer(SpotDetailEthBitfinexStore.class);
                    SpotDetailBtcBitfinexStore spotDetailBtcBitfinexStore = hsfServiceFactory.consumer(SpotDetailBtcBitfinexStore.class);

                    Integer y=DateFormatUtil.getDayInYear(tradingDate);
                    Integer m=DateFormatUtil.getDayInMonth(tradingDate)+1;
                    Integer d=DateFormatUtil.getDayInDay(tradingDate);

                    if (spotDetailUsdtBitfinexStore != null && spotDetailEthBitfinexStore != null && spotDetailBtcBitfinexStore != null &&
                            spotSymbolList != null && !spotSymbolList.isEmpty() && sampleSpotSymbolWeightMap != null && !sampleSpotSymbolWeightMap.isEmpty()
                            && spotSymbolHashMap != null&& !spotSymbolHashMap.isEmpty()){
                        //todo 调用api 保存 都在这里写
                        String paramsStr = "";
                        for (SpotSymbol spotSymbol : spotSymbolList) {
                            paramsStr += "t" + spotSymbol.getSymbol().toUpperCase() + ",";
                        }
                        Map<String, String> params = new HashMap<>();
                        try {
                            String resultStr = OKHttpUtils.get("https://api.bitfinex.com/v2/tickers?symbols=" + paramsStr, params);
                            if (StringUtils.isNotBlank(resultStr)) {
                                JSONArray jsonArray = JSONArray.fromObject(resultStr);
                                if (jsonArray != null && !jsonArray.isEmpty()) {
                                    for (Object symbolObj : jsonArray) {
                                        JSONArray dayAttr = (JSONArray) symbolObj;
                                        if (dayAttr != null && !dayAttr.isEmpty()) {
                                            String key = dayAttr.getString(0);
                                            Double bidPrice = dayAttr.getDouble(1);
                                            Double bidQty = dayAttr.getDouble(2);
                                            Double askPrice = dayAttr.getDouble(3);
                                            Double askQty = dayAttr.getDouble(4);
                                            Double lastPrice = dayAttr.getDouble(7);
                                            Double volume = dayAttr.getDouble(8);
                                            Double quoteVolume = 0.00D;

                                            CalSampleSpotSymbolWeight calSampleSpotSymbolWeight = sampleSpotSymbolWeightMap.get(key);
                                            SpotSymbol spotSymbol = spotSymbolHashMap.get(key);
                                            if(calSampleSpotSymbolWeight!=null&&symbolObj!=null){
                                                String market = spotSymbol.getMarket();
                                                if (market.equals("usdt")) {
                                                    SpotDetailUsdtBitfinex spotDetailUsdtBitfinex = new SpotDetailUsdtBitfinex();
                                                    spotDetailUsdtBitfinex.setTradeId(sysTrade);
                                                    spotDetailUsdtBitfinex.setSymbolId(spotSymbol);
                                                    spotDetailUsdtBitfinex.setTradingDay(tradingDate);
                                                    spotDetailUsdtBitfinex.setTradingTime(tradingDate);
                                                    spotDetailUsdtBitfinex.setPrice(lastPrice);
                                                    spotDetailUsdtBitfinex.setVolume(volume);
                                                    spotDetailUsdtBitfinex.setTurnover(quoteVolume);
                                                    spotDetailUsdtBitfinex.setBidPrice(bidPrice);
                                                    spotDetailUsdtBitfinex.setBidVolume(bidQty);
                                                    spotDetailUsdtBitfinex.setAskPrice(askPrice);
                                                    spotDetailUsdtBitfinex.setAskVolume(askQty);
                                                    Utils.bind(spotDetailUsdtBitfinex, "task");


                                                    CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice=new CalSampleSpotSymbolWeightPrice();
                                                    calSampleSpotSymbolWeightPrice.setId(DrdsIDUtils.getID(DrdsTable.CAL));
                                                    calSampleSpotSymbolWeightPrice.setPrice(lastPrice);
                                                    calSampleSpotSymbolWeightPrice.setSymbolId(calSampleSpotSymbolWeight);
                                                    calSampleSpotSymbolWeightPrice.setYear(y);
                                                    calSampleSpotSymbolWeightPrice.setMonth(m);
                                                    calSampleSpotSymbolWeightPrice.setDay(d);
                                                    calSampleSpotSymbolWeightPrice.setRecordDate(tradingDate);
                                                    Utils.bind(calSampleSpotSymbolWeightPrice,"task");

                                                    spotDetailUsdtBitfinexStore.save(spotDetailUsdtBitfinex, Persistent.SAVE,calSampleSpotSymbolWeightPrice);
                                                    if (releaseEnvironment.equals("DIS")) {
                                                        Utils.setWeightSymbolPrice(calSampleSpotSymbolWeight,lastPrice);
                                                        Utils.setDailySymbolPrice(spotSymbol, lastPrice,tradingDate);

                                                    }
                                                    //save
                                                } else if (market.equals("btc")) {
                                                    SpotDetailBtcBitfinex spotDetailBtcBitfinex = new SpotDetailBtcBitfinex();
                                                    spotDetailBtcBitfinex.setTradeId(sysTrade);
                                                    spotDetailBtcBitfinex.setSymbolId(spotSymbol);
                                                    spotDetailBtcBitfinex.setTradingDay(tradingDate);
                                                    spotDetailBtcBitfinex.setTradingTime(tradingDate);
                                                    spotDetailBtcBitfinex.setPrice(lastPrice);
                                                    spotDetailBtcBitfinex.setVolume(volume);
                                                    spotDetailBtcBitfinex.setTurnover(quoteVolume);
                                                    spotDetailBtcBitfinex.setBidPrice(bidPrice);
                                                    spotDetailBtcBitfinex.setBidVolume(bidQty);
                                                    spotDetailBtcBitfinex.setAskPrice(askPrice);
                                                    spotDetailBtcBitfinex.setAskVolume(askQty);
                                                    Utils.bind(spotDetailBtcBitfinex, "task");
                                                    spotDetailBtcBitfinexStore.save(spotDetailBtcBitfinex, Persistent.SAVE);
                                                    //save
                                                } else if (market.equals("eth")) {
                                                    //save
                                                    SpotDetailEthBitfinex spotDetailBitfinexEth = new SpotDetailEthBitfinex();
                                                    spotDetailBitfinexEth.setTradeId(sysTrade);
                                                    spotDetailBitfinexEth.setSymbolId(spotSymbol);
                                                    spotDetailBitfinexEth.setTradingDay(tradingDate);
                                                    spotDetailBitfinexEth.setTradingTime(tradingDate);
                                                    spotDetailBitfinexEth.setPrice(lastPrice);
                                                    spotDetailBitfinexEth.setVolume(volume);
                                                    spotDetailBitfinexEth.setTurnover(quoteVolume);
                                                    spotDetailBitfinexEth.setBidPrice(bidPrice);
                                                    spotDetailBitfinexEth.setBidVolume(bidQty);
                                                    spotDetailBitfinexEth.setAskPrice(askPrice);
                                                    spotDetailBitfinexEth.setAskVolume(askQty);
                                                    Utils.bind(spotDetailBitfinexEth, "task");
                                                    spotDetailEthBitfinexStore.save(spotDetailBitfinexEth, Persistent.SAVE);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
