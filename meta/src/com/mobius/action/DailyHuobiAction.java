package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysTrade;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysTradeStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lara Croft on 2018/5/24.
 */
@Action(name = "huobi", namespace = "/daily")
public class DailyHuobiAction extends BaseAction {

    private static String tradeSign = "HUOBIPRO";


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }
    public String initSymbol() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        if (spotSymbolStore != null) {
                            String resultStr = OKHttpUtil.get("https://api.huobi.pro/v1/common/symbols", null);
                            if (StringUtils.isNotBlank(resultStr)) {
                                JSONObject root=JSONObject.fromObject(resultStr);
                                if(!root.containsKey("data")){
                                    return null;
                                }
                                JSONArray symbols = root.getJSONArray("data");
                                System.out.print(symbols.size());
                                if (!symbols.isEmpty()) {
                                    int displayOrder = 1;
                                    List<SpotSymbol> spotSymbolList = new ArrayList<>();
                                    for (Object obj : symbols) {
                                        JSONObject coin = JSONObject.fromObject(obj);
                                        if (coin!=null) {
                                            String baseCurrency=coin.getString("base-currency");
                                            String quoteCurrency=coin.getString("quote-currency");
                                            SpotSymbol symbol = new SpotSymbol();
                                            symbol.setTradeId(sysTrade);
                                            symbol.setSymbol(baseCurrency+quoteCurrency);
                                            String market = null;
                                            if (quoteCurrency.equals("usdt")) {
                                                market = "usdt";
                                            } else if (quoteCurrency.equals("btc")) {
                                                market = "btc";
                                            } else if (quoteCurrency.equals("eth")) {
                                                market = "eth";
                                            }
                                            if(StringUtils.isNotBlank(market)){
                                                symbol.setDisplayOrder(displayOrder);
                                                symbol.setMarket(market);
                                                displayOrder++;
                                                symbol.setUseYn("Y");
                                                spotSymbolList.add(symbol);
                                            }
                                        }
                                    }
                                    if ( !spotSymbolList.isEmpty()) {
                                        spotSymbolStore.save(spotSymbolList, Persistent.SAVE);
                                        result.put("result", "0");
                                    }
                                }
                            }
                            writeJsonByAction(result.toString());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String buildSpotUsdt() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                        if (spotSymbolStore != null&&spotDailyUsdtStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "usdt");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("period", "1day");
                                params.put("size", "1000");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        String resultStr = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                        if (StringUtils.isNotBlank(resultStr) ) {
                                            JSONObject obj=JSONObject.fromObject(resultStr);
                                            if(!obj.containsKey("data")){
                                                System.out.print("huobi  kline data was null!");
                                                return null;
                                            }
                                            JSONArray klineArray = obj.getJSONArray("data");
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyUsdt> dailyUsdtList=new ArrayList<>();
                                                int loop=0;
                                                for (Object _obj:klineArray) {
                                                    JSONObject jsonObj =  JSONObject.fromObject(_obj);
                                                    if (jsonObj != null ) {
                                                        if (loop == 0) { //today
                                                            loop++;
                                                            continue;
                                                        }
                                                        SpotDailyUsdt spotDailyUsdt=new SpotDailyUsdt();
                                                        spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyUsdt.setTradeId(sysTrade);
                                                        spotDailyUsdt.setSymbolId(spotSymbol);
                                                        spotDailyUsdt.setTradingDay(new Date(jsonObj.getLong("id")*1000));
                                                        spotDailyUsdt.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            spotDailyUsdt.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            spotDailyUsdt.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        dailyUsdtList.add(spotDailyUsdt);
                                                    }
                                                }
                                                spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(1);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String buildSpotBtc() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyBtcStore dailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
                        if (spotSymbolStore != null&&dailyBtcStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "btc");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("period", "1day");
                                params.put("size", "1000");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        String resultStr = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                        if (StringUtils.isNotBlank(resultStr) ) {
                                            JSONObject obj=JSONObject.fromObject(resultStr);
                                            if(!obj.containsKey("data")){
                                                System.out.print("huobi  kline data was null!");
                                                return null;
                                            }
                                            JSONArray klineArray = obj.getJSONArray("data");
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyBtc> saveList=new ArrayList<>();
                                                int loop=0;
                                                for (Object _obj:klineArray) {
                                                    JSONObject jsonObj =  JSONObject.fromObject(_obj);
                                                    if (jsonObj != null ) {
                                                        if (loop == 0) { //today
                                                            loop++;
                                                            continue;
                                                        }
                                                        SpotDailyBtc spotDailyBtc=new SpotDailyBtc();
                                                        spotDailyBtc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyBtc.setTradeId(sysTrade);
                                                        spotDailyBtc.setSymbolId(spotSymbol);
                                                        spotDailyBtc.setTradingDay(new Date(jsonObj.getLong("id")*1000));
                                                        spotDailyBtc.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            spotDailyBtc.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            spotDailyBtc.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        saveList.add(spotDailyBtc);
                                                    }
                                                }
                                                dailyBtcStore.save(saveList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(1);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ saveList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }



    public String buildSpotEth() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");

        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        SpotDailyEthStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
                        if (spotSymbolStore != null&&spotDailyUsdtStore!=null) {
                            List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), "eth");
                            if (symbolList != null && !symbolList.isEmpty()) {
                                Map<String, String> params = new HashMap<>();
                                params.put("period", "1day");
                                params.put("size", "1000");

                                for (SpotSymbol spotSymbol : symbolList) {
                                    params.put("symbol", spotSymbol.getSymbol());

                                    try {
                                        String resultStr = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                        if (StringUtils.isNotBlank(resultStr) ) {
                                            JSONObject obj=JSONObject.fromObject(resultStr);
                                            if(!obj.containsKey("data")){
                                                System.out.print("huobi  kline data was null!");
                                                return null;
                                            }
                                            JSONArray klineArray = obj.getJSONArray("data");
                                            if (klineArray != null && !klineArray.isEmpty()) {
                                                List<SpotDailyEth> dailyUsdtList=new ArrayList<>();
                                                int loop=0;
                                                for (Object _obj:klineArray) {
                                                    JSONObject jsonObj =  JSONObject.fromObject(_obj);
                                                    if (jsonObj != null ) {
                                                        if (loop == 0) { //today
                                                            loop++;
                                                            continue;
                                                        }
                                                        SpotDailyEth spotDailyEth=new SpotDailyEth();
                                                        spotDailyEth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        spotDailyEth.setTradeId(sysTrade);
                                                        spotDailyEth.setSymbolId(spotSymbol);
                                                        spotDailyEth.setTradingDay(new Date(jsonObj.getLong("id")*1000));
                                                        spotDailyEth.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            spotDailyEth.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            spotDailyEth.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        dailyUsdtList.add(spotDailyEth);
                                                    }
                                                }
                                                spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(1);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                result.put("result","0");
                                writeJsonByAction(result.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  null;
    }


}
