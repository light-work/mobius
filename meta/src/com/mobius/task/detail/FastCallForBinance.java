package com.mobius.task.detail;


import com.mobius.OKHttpUtils;
import com.mobius.Utils;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailBtcBinance;
import com.mobius.entity.spot.SpotDetailEthBinance;
import com.mobius.entity.spot.SpotDetailUsdtBinance;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDetailBtcBinanceStore;
import com.mobius.providers.store.spot.SpotDetailEthBinanceStore;
import com.mobius.providers.store.spot.SpotDetailUsdtBinanceStore;
import net.sf.json.JSONObject;
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
public class FastCallForBinance {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void call(final CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,final SysTrade sysTrade, final SpotSymbol spotSymbol, final HSFServiceFactory hsfServiceFactory,
                            final Date tradingDate,final String releaseEnvironment) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    String market = spotSymbol.getMarket();
                    if (StringUtils.isNotBlank(market)) {
                        SpotDetailUsdtBinanceStore spotDetailUsdtBinanceStore = hsfServiceFactory.consumer(SpotDetailUsdtBinanceStore.class);
                        SpotDetailEthBinanceStore spotDetailEthBinanceStore = hsfServiceFactory.consumer(SpotDetailEthBinanceStore.class);
                        SpotDetailBtcBinanceStore spotDetailBtcBinanceStore = hsfServiceFactory.consumer(SpotDetailBtcBinanceStore.class);
                        Integer y=DateFormatUtil.getDayInYear(tradingDate);
                        Integer m=DateFormatUtil.getDayInMonth(tradingDate)+1;
                        Integer d=DateFormatUtil.getDayInDay(tradingDate);
                        if (spotDetailBtcBinanceStore != null && spotDetailUsdtBinanceStore != null && spotDetailEthBinanceStore != null) {
                            //todo 调用api 保存 都在这里写
                            Map<String, String> params = new HashMap<>();
                            params.put("symbol",spotSymbol.getSymbol());
                            try {
                                String resultStr =OKHttpUtils.get("https://api.binance.com/api/v1/ticker/24hr", params);
                                if (StringUtils.isNotBlank(resultStr)) {
                                    JSONObject jsonObject=JSONObject.fromObject(resultStr);
                                    if(jsonObject!=null){
                                        Double lastPrice=jsonObject.getDouble("lastPrice");
                                        Double bidPrice=jsonObject.getDouble("bidPrice");
                                        Double bidQty=jsonObject.getDouble("bidQty");
                                        Double askPrice=jsonObject.getDouble("askPrice");
                                        Double askQty=jsonObject.getDouble("askQty");
                                        Double volume=jsonObject.getDouble("volume");
                                        Double quoteVolume=jsonObject.getDouble("quoteVolume");
                                        if (market.equals("usdt")) {
                                            SpotDetailUsdtBinance spotDetailUsdtBinance = new SpotDetailUsdtBinance();
                                            spotDetailUsdtBinance.setTradeId(sysTrade);
                                            spotDetailUsdtBinance.setSymbolId(spotSymbol);
                                            spotDetailUsdtBinance.setTradingDay(tradingDate);
                                            spotDetailUsdtBinance.setTradingTime(tradingDate);
                                            spotDetailUsdtBinance.setPrice(lastPrice);
                                            spotDetailUsdtBinance.setVolume(volume);
                                            spotDetailUsdtBinance.setTurnover(quoteVolume);
                                            spotDetailUsdtBinance.setBidPrice(bidPrice);
                                            spotDetailUsdtBinance.setBidVolume(bidQty);
                                            spotDetailUsdtBinance.setAskPrice(askPrice);
                                            spotDetailUsdtBinance.setAskVolume(askQty);
                                            Utils.bind(spotDetailUsdtBinance,"task");


                                            CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice=new CalSampleSpotSymbolWeightPrice();
                                            calSampleSpotSymbolWeightPrice.setId(DrdsIDUtils.getID(DrdsTable.CAL));
                                            calSampleSpotSymbolWeightPrice.setPrice(lastPrice);
                                            calSampleSpotSymbolWeightPrice.setSymbolId(calSampleSpotSymbolWeight);
                                            calSampleSpotSymbolWeightPrice.setYear(y);
                                            calSampleSpotSymbolWeightPrice.setMonth(m);
                                            calSampleSpotSymbolWeightPrice.setDay(d);
                                            calSampleSpotSymbolWeightPrice.setRecordDate(tradingDate);
                                            Utils.bind(calSampleSpotSymbolWeightPrice,"task");

                                            spotDetailUsdtBinanceStore.save(spotDetailUsdtBinance,Persistent.SAVE,calSampleSpotSymbolWeightPrice);
                                            if (releaseEnvironment.equals("DIS")) {
                                                Utils.setWeightSymbolPrice(calSampleSpotSymbolWeight,lastPrice);
                                            }
                                            //save
                                        } else if (market.equals("btc")) {
                                            SpotDetailBtcBinance spotDetailBtcBinance = new SpotDetailBtcBinance();
                                            spotDetailBtcBinance.setTradeId(sysTrade);
                                            spotDetailBtcBinance.setSymbolId(spotSymbol);
                                            spotDetailBtcBinance.setTradingDay(tradingDate);
                                            spotDetailBtcBinance.setTradingTime(tradingDate);
                                            spotDetailBtcBinance.setPrice(lastPrice);
                                            spotDetailBtcBinance.setVolume(volume);
                                            spotDetailBtcBinance.setTurnover(quoteVolume);
                                            spotDetailBtcBinance.setBidPrice(bidPrice);
                                            spotDetailBtcBinance.setBidVolume(bidQty);
                                            spotDetailBtcBinance.setAskPrice(askPrice);
                                            spotDetailBtcBinance.setAskVolume(askQty);
                                            Utils.bind(spotDetailBtcBinance,"task");
                                            spotDetailBtcBinanceStore.save(spotDetailBtcBinance,Persistent.SAVE);
                                            //save
                                        } else if (market.equals("eth")) {
                                            //save
                                            SpotDetailEthBinance spotDetailBinanceEth = new SpotDetailEthBinance();
                                            spotDetailBinanceEth.setTradeId(sysTrade);
                                            spotDetailBinanceEth.setSymbolId(spotSymbol);
                                            spotDetailBinanceEth.setTradingDay(tradingDate);
                                            spotDetailBinanceEth.setTradingTime(tradingDate);
                                            spotDetailBinanceEth.setPrice(lastPrice);
                                            spotDetailBinanceEth.setVolume(volume);
                                            spotDetailBinanceEth.setTurnover(quoteVolume);
                                            spotDetailBinanceEth.setBidPrice(bidPrice);
                                            spotDetailBinanceEth.setBidVolume(bidQty);
                                            spotDetailBinanceEth.setAskPrice(askPrice);
                                            spotDetailBinanceEth.setAskVolume(askQty);
                                            Utils.bind(spotDetailBinanceEth,"task");
                                            spotDetailEthBinanceStore.save(spotDetailBinanceEth,Persistent.SAVE);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
