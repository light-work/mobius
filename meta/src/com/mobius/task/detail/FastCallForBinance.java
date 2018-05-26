package com.mobius.task.detail;


import com.mobius.Utils;
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
import com.mobius.providers.store.spot.SpotSymbolStore;
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
public class FastCallForBinance {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void call(final SysTrade sysTrade, final SpotSymbol spotSymbol, final HSFServiceFactory hsfServiceFactory,
                            final Date tradingDate) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    String market = spotSymbol.getMarket();
                    if (StringUtils.isNotBlank(market)) {
                        SpotDetailUsdtStore spotDetailUsdtStore = hsfServiceFactory.consumer(SpotDetailUsdtStore.class);
                        SpotDetailEthStore spotDetailEthStore = hsfServiceFactory.consumer(SpotDetailEthStore.class);
                        SpotDetailBtcStore spotDetailBtcStore = hsfServiceFactory.consumer(SpotDetailBtcStore.class);
                        if (spotDetailBtcStore != null && spotDetailUsdtStore != null && spotDetailEthStore != null) {
                            //todo 调用api 保存 都在这里写
                            Map<String, String> params = new HashMap<>();
                            params.put("symbol",spotSymbol.getSymbol());
                            try {
                                String resultStr =OKHttpUtil.get("https://api.binance.com/api/v1/ticker/24hr", params);
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
                                            SpotDetailUsdt spotDetailUsdt = new SpotDetailUsdt();
                                            spotDetailUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                            spotDetailUsdt.setTradeId(sysTrade);
                                            spotDetailUsdt.setSymbolId(spotSymbol);
                                            spotDetailUsdt.setTradingDay(tradingDate);
                                            spotDetailUsdt.setTradingTime(tradingDate);
                                            spotDetailUsdt.setPrice(lastPrice);
                                            spotDetailUsdt.setVolume(volume);
                                            spotDetailUsdt.setTurnover(quoteVolume);
                                            spotDetailUsdt.setBidPrice(bidPrice);
                                            spotDetailUsdt.setBidVolume(bidQty);
                                            spotDetailUsdt.setAskPrice(askPrice);
                                            spotDetailUsdt.setAskVolume(askQty);
                                            Utils.bind(spotDetailUsdt,"task");
                                            spotDetailUsdtStore.save(spotDetailUsdt,Persistent.SAVE);
                                            //save
                                        } else if (market.equals("btc")) {
                                            SpotDetailBtc spotDetailBtc = new SpotDetailBtc();
                                            spotDetailBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                            spotDetailBtc.setTradeId(sysTrade);
                                            spotDetailBtc.setSymbolId(spotSymbol);
                                            spotDetailBtc.setTradingDay(tradingDate);
                                            spotDetailBtc.setTradingTime(tradingDate);
                                            spotDetailBtc.setPrice(lastPrice);
                                            spotDetailBtc.setVolume(volume);
                                            spotDetailBtc.setTurnover(quoteVolume);
                                            spotDetailBtc.setBidPrice(bidPrice);
                                            spotDetailBtc.setBidVolume(bidQty);
                                            spotDetailBtc.setAskPrice(askPrice);
                                            spotDetailBtc.setAskVolume(askQty);
                                            Utils.bind(spotDetailBtc,"task");
                                            spotDetailBtcStore.save(spotDetailBtc,Persistent.SAVE);
                                            //save
                                        } else if (market.equals("eth")) {
                                            //save
                                            SpotDetailEth spotDetailEth = new SpotDetailEth();
                                            spotDetailEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                            spotDetailEth.setTradeId(sysTrade);
                                            spotDetailEth.setSymbolId(spotSymbol);
                                            spotDetailEth.setTradingDay(tradingDate);
                                            spotDetailEth.setTradingTime(tradingDate);
                                            spotDetailEth.setPrice(lastPrice);
                                            spotDetailEth.setVolume(volume);
                                            spotDetailEth.setTurnover(quoteVolume);
                                            spotDetailEth.setBidPrice(bidPrice);
                                            spotDetailEth.setBidVolume(bidQty);
                                            spotDetailEth.setAskPrice(askPrice);
                                            spotDetailEth.setAskVolume(askQty);
                                            Utils.bind(spotDetailEth,"task");
                                            spotDetailEthStore.save(spotDetailEth,Persistent.SAVE);
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }


}
