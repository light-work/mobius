package com.mobius.task.detail;


import com.mobius.entity.spot.SpotSymbol;
import com.mobius.providers.store.spot.SpotDetailBtcStore;
import com.mobius.providers.store.spot.SpotDetailEthStore;
import com.mobius.providers.store.spot.SpotDetailUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GoEasy
 */
public class FastCallForBinance {

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    public static void call(final SpotSymbol spotSymbol, final HSFServiceFactory hsfServiceFactory) {
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
                            OKHttpUtil.get("", null);
                            if (market.equals("usdt")) {
                                //save
                            } else if (market.equals("btc")) {
                                //save
                            } else if (market.equals("etc")) {
                                //save
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }


}
