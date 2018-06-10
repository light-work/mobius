package com.mobius.task.detail;


import com.mobius.OKHttpUtils;
import com.mobius.Utils;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
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
public class FastCallForBitfinex {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void call(final CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,final SysTrade sysTrade, final SpotSymbol spotSymbol, final HSFServiceFactory hsfServiceFactory,
                            final Date tradingDate) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    String market = spotSymbol.getMarket();
                    if (StringUtils.isNotBlank(market)) {
                        SpotDetailUsdtBitfinexStore spotDetailUsdtBitfinexStore = hsfServiceFactory.consumer(SpotDetailUsdtBitfinexStore.class);
                        SpotDetailEthBitfinexStore spotDetailEthBitfinexStore = hsfServiceFactory.consumer(SpotDetailEthBitfinexStore.class);
                        SpotDetailBtcBitfinexStore spotDetailBtcBitfinexStore = hsfServiceFactory.consumer(SpotDetailBtcBitfinexStore.class);
                        if (spotDetailUsdtBitfinexStore != null && spotDetailEthBitfinexStore != null && spotDetailBtcBitfinexStore != null) {
                            //todo 调用api 保存 都在这里写
                            Map<String, String> params = new HashMap<>();
                            try {
                                String resultStr =OKHttpUtils.get("https://api.bitfinex.com/v2/ticker/t"+spotSymbol.getSymbol().toUpperCase(), params);
                                if (StringUtils.isNotBlank(resultStr)) {
                                    JSONArray jsonArray=JSONArray.fromObject(resultStr);
                                    if(jsonArray!=null){
                                        Double bidPrice=jsonArray.getDouble(0);
                                        Double bidQty=jsonArray.getDouble(1);
                                        Double askPrice=jsonArray.getDouble(2);
                                        Double askQty=jsonArray.getDouble(3);
                                        Double lastPrice=jsonArray.getDouble(6);
                                        Double volume=jsonArray.getDouble(7);
                                        Double quoteVolume=0.00D;
                                        if (market.equals("usdt")) {
                                            SpotDetailUsdtBitfinex spotDetailUsdtBitfinex = new SpotDetailUsdtBitfinex();
                                            spotDetailUsdtBitfinex.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
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
                                            Utils.bind(spotDetailUsdtBitfinex,"task");

                                            calSampleSpotSymbolWeight.setLastPrice(lastPrice);
                                            Utils.bind(calSampleSpotSymbolWeight,"task");

                                            spotDetailUsdtBitfinexStore.save(spotDetailUsdtBitfinex,Persistent.SAVE,calSampleSpotSymbolWeight);
                                            //save
                                        } else if (market.equals("btc")) {
                                            SpotDetailBtcBitfinex spotDetailBtcBitfinex = new SpotDetailBtcBitfinex();
                                            spotDetailBtcBitfinex.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
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
                                            Utils.bind(spotDetailBtcBitfinex,"task");
                                            spotDetailBtcBitfinexStore.save(spotDetailBtcBitfinex,Persistent.SAVE);
                                            //save
                                        } else if (market.equals("eth")) {
                                            //save
                                            SpotDetailEthBitfinex spotDetailBitfinexEth = new SpotDetailEthBitfinex();
                                            spotDetailBitfinexEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
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
                                            Utils.bind(spotDetailBitfinexEth,"task");
                                            spotDetailEthBitfinexStore.save(spotDetailBitfinexEth,Persistent.SAVE);
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
