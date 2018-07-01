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
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lara Croft on 2018/5/24.
 */
@Action(name = "huobi", namespace = "/daily")
public class DailyHuobiAction extends BaseAction {

    private static String tradeSign = "HUOBIPRO";

    @ReqGet
    private Integer size;

    @ReqGet
    private String since;

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
                                        Set<String> dateSet = new HashSet<>();
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
                                                        String happenDay = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                                DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
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
                                                        spotDailyUsdt.setCreated(new Date());
                                                        spotDailyUsdt.setCreatedBy("batch");
                                                        dailyUsdtList.add(spotDailyUsdt);
                                                    }
                                                }
                                                spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------Huobi buildSpotUsdt  running over----");
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
                                        Set<String> dateSet = new HashSet<>();
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
                                                        String happenDay = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                                DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
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
                                                        spotDailyBtc.setCreated(new Date());
                                                        spotDailyBtc.setCreatedBy("batch");
                                                        saveList.add(spotDailyBtc);
                                                    }
                                                }
                                                dailyBtcStore.save(saveList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ saveList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------Huobi buildSpotBtc  running over----");
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
                                        Set<String> dateSet = new HashSet<>();
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
                                                        String happenDay = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                                DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                        if (dateSet.contains(happenDay)) {
                                                            continue;
                                                        }
                                                        dateSet.add(happenDay);
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
                                                        spotDailyEth.setCreated(new Date());
                                                        spotDailyEth.setCreatedBy("batch");
                                                        dailyUsdtList.add(spotDailyEth);
                                                    }
                                                }
                                                spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                                                TimeUnit.SECONDS.sleep(2);//秒
                                                System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("-------Huobi buildSpotEth  running over----");
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

    public String dailyTask() throws Exception {
        JSONObject root = new JSONObject();
        root.put("result", "-1");
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                List<String> marketList = new ArrayList<>();
                marketList.add("usdt");
                marketList.add("btc");
                marketList.add("eth");

                SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
                SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
                if (spotSymbolStore != null && spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
                    for (String market : marketList) {
                        List<SpotSymbol> symbolList = spotSymbolStore.getListByTradeMarket(sysTrade.getId(), market);
                        if (symbolList != null && !symbolList.isEmpty()) {
                            Map<String, String> params = new HashMap<>();
                            params.put("period", "1day");
                            if (size != null && size != 0) {
                                params.put("size", size + "");
                            } else {
                                params.put("size", "2");
                            }
                            for (SpotSymbol spotSymbol : symbolList) {
                                params.put("symbol", spotSymbol.getSymbol());
                                try {
                                    Set<String> dateSet = new HashSet<>();
                                    String result = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                    if (StringUtils.isNotBlank(result)) {
                                        JSONObject obj = JSONObject.fromObject(result);
                                        if (!obj.containsKey("data")) {
                                            System.out.print("huobi  kline data was null!");
                                            return null;
                                        }
                                        JSONArray klineArray = obj.getJSONArray("data");
                                        if (klineArray != null && !klineArray.isEmpty()) {
                                            List<SpotDailyUsdt> usdtList = new ArrayList<>();
                                            List<SpotDailyBtc> btcList = new ArrayList<>();
                                            List<SpotDailyEth> ethList = new ArrayList<>();
                                            for (int x = 1; x < klineArray.size(); x++) {// 3 2 1
                                                JSONObject jsonObj = klineArray.getJSONObject(x);
                                                String dateStr = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                        DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                                if (dateSet.contains(dateStr)) {
                                                    continue;
                                                }
                                                dateSet.add(dateStr);
                                                if (market.equals("usdt")) {
                                                    Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == 1) {
                                                        System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    } else if (count == 0) {
                                                        SpotDailyUsdt usdt = new SpotDailyUsdt();
                                                        usdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        usdt.setTradeId(sysTrade);
                                                        usdt.setSymbolId(spotSymbol);
                                                        usdt.setTradingDay(tradingDate);
                                                        usdt.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            usdt.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            usdt.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        usdt.setCreatedBy("bySave");
                                                        usdt.setCreated(new Date());
                                                        usdtList.add(usdt);
                                                    }
                                                } else if (market.equals("btc")) {
                                                    Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == 1) {
                                                        System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    } else if (count == 0) {
                                                        SpotDailyBtc btc = new SpotDailyBtc();
                                                        btc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        btc.setTradeId(sysTrade);
                                                        btc.setSymbolId(spotSymbol);
                                                        btc.setTradingDay(tradingDate);
                                                        btc.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            btc.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            btc.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        btc.setCreatedBy("bySave");
                                                        btc.setCreated(new Date());
                                                        btcList.add(btc);
                                                    }
                                                } else if (market.equals("eth")) {
                                                    Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                            spotSymbol.getId(), tradingDate);
                                                    if (count == 1) {
                                                        System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                    } else if (count == 0) {
                                                        SpotDailyEth eth = new SpotDailyEth();
                                                        eth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                        eth.setTradeId(sysTrade);
                                                        eth.setSymbolId(spotSymbol);
                                                        eth.setTradingDay(tradingDate);
                                                        eth.setLastPrice(jsonObj.getDouble("close"));
                                                        if (jsonObj.containsKey("amount")) {
                                                            eth.setVolume(jsonObj.getDouble("amount"));
                                                        }
                                                        if (jsonObj.containsKey("vol")) {
                                                            eth.setTurnover(jsonObj.getDouble("vol"));
                                                        }
                                                        eth.setCreatedBy("bySave");
                                                        eth.setCreated(new Date());
                                                        ethList.add(eth);
                                                    }
                                                }
                                            }
                                            if (!usdtList.isEmpty()) {
                                                spotDailyUsdtStore.save(usdtList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + usdtList.size());
                                            }
                                            if (!btcList.isEmpty()) {
                                                spotDailyBtcStore.save(btcList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + btcList.size());
                                            }
                                            if (!ethList.isEmpty()) {
                                                spotDailyEthStore.save(ethList, Persistent.SAVE);
                                                System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + ethList.size());
                                            }
                                            TimeUnit.SECONDS.sleep(2);//秒
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    System.out.println("huobi daily task over---------");
                    root.put("result", "0");
                    writeJsonByAction(root.toString());
                }
            }
        }
        return null;
    }


    //supplement

    private List<SpotSymbol> getMissList(SpotSymbolStore spotSymbolStore, SpotDailyUsdtStore spotDailyUsdtStore,
                                         SpotDailyBtcStore spotDailyBtcStore, SpotDailyEthStore spotDailyEthStore) throws Exception {
        List<SpotSymbol> list = new ArrayList<>();
        if (spotSymbolStore != null && spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
            Date date = DateFormatUtil.parse(since + " 00:00:00", DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            long tradeId = 2;
            List<Selector> selectorList = new ArrayList<>();
            selectorList.add(SelectorUtils.$eq("tradingDay", date));
            selectorList.add(SelectorUtils.$alias("symbolId", "symbolId"));
            selectorList.add(SelectorUtils.$eq("tradeId.id", tradeId));

            //total symbol
            List<SpotSymbol> allBtcSymbolList = spotSymbolStore.getListByTradeMarket(tradeId, "btc");
            List<SpotSymbol> allUsdtSymbolList = spotSymbolStore.getListByTradeMarket(tradeId, "usdt");
            List<SpotSymbol> allEthSymbolList = spotSymbolStore.getListByTradeMarket(tradeId, "eth");

            //saved symbol
            List<SpotDailyBtc> savedBtcSymbolList = spotDailyBtcStore.getList(selectorList);
            List<SpotDailyUsdt> savedUsdtSymbolList = spotDailyUsdtStore.getList(selectorList);
            List<SpotDailyEth> savedEthSymbolList = spotDailyEthStore.getList(selectorList);

            //btc
            if (savedBtcSymbolList != null && !savedBtcSymbolList.isEmpty()) {
                if (allBtcSymbolList != null && !allBtcSymbolList.isEmpty()) {
                    List<SpotSymbol> existList = new ArrayList<>();
                    for (SpotSymbol s1 : allBtcSymbolList) {
                        for (SpotDailyBtc dailyBtc : savedBtcSymbolList) {
                            if (s1.getSymbol().equals(dailyBtc.getSymbolId().getSymbol())) {
                                existList.add(s1);
                            }
                        }
                    }
                    System.out.println("-----allBtcSymbolList remove before size =" + (allBtcSymbolList.size()) + " and btcList = " + (existList.size()));
                    allBtcSymbolList.removeAll(existList);
                    System.out.println("-----allBtcSymbolList remove after size =" + (allBtcSymbolList.size()) + " and list size= " + (list.size()));
                    list.addAll(allBtcSymbolList);
                    System.out.println("----- after addAll list size = " + (list.size()));
                } else {
                    System.out.println("-------allBtcSymbolList was null------");
                }
            } else {
                if (allBtcSymbolList != null && !allBtcSymbolList.isEmpty()) {
                    list.addAll(allBtcSymbolList);
                }
                System.out.println("---savedBtcSymbolList was null----");
            }

            //usdt
            if (savedUsdtSymbolList != null && !savedUsdtSymbolList.isEmpty()) {
                if (allUsdtSymbolList != null && !allUsdtSymbolList.isEmpty()) {
                    List<SpotSymbol> existList = new ArrayList<>();
                    for (SpotSymbol s1 : allUsdtSymbolList) {
                        for (SpotDailyUsdt usdt : savedUsdtSymbolList) {
                            if (s1.getSymbol().equals(usdt.getSymbolId().getSymbol())) {
                                existList.add(s1);
                            }
                        }
                    }
                    System.out.println("-----allUsdtSymbolList remove before size =" + (allUsdtSymbolList.size()) + " and usdtList = " + (existList.size()));
                    allUsdtSymbolList.removeAll(existList);
                    System.out.println("-----allUsdtSymbolList remove after size =" + (allUsdtSymbolList.size()) + " and list size= " + (list.size()));
                    list.addAll(allUsdtSymbolList);
                    System.out.println("----- after addAll list size = " + (list.size()));
                } else {
                    System.out.println("-------allUsdtSymbolList was null------");
                }
            } else {
                if (allUsdtSymbolList != null && !allUsdtSymbolList.isEmpty()) {
                    list.addAll(allUsdtSymbolList);
                }
                System.out.println("---savedUsdtSymbolList was null----");
            }

            //eth
            if (savedEthSymbolList != null && !savedEthSymbolList.isEmpty()) {
                if (allEthSymbolList != null && !allEthSymbolList.isEmpty()) {
                    List<SpotSymbol> existList = new ArrayList<>();
                    for (SpotSymbol s1 : allEthSymbolList) {
                        for (SpotDailyEth usdt : savedEthSymbolList) {
                            if (s1.getSymbol().equals(usdt.getSymbolId().getSymbol())) {
                                existList.add(s1);
                            }
                        }
                    }
                    System.out.println("-----allEthSymbolList remove before size =" + (allEthSymbolList.size()) + " and ethList = " + (existList.size()));
                    allEthSymbolList.removeAll(existList);
                    System.out.println("-----allEthSymbolList remove after size =" + (allEthSymbolList.size()) + " and list size= " + (list.size()));
                    list.addAll(allEthSymbolList);
                    System.out.println("----- after addAll list size = " + (list.size()));
                } else {
                    System.out.println("-------allEthSymbolList was null------");
                }
            } else {
                if (allEthSymbolList != null && !allEthSymbolList.isEmpty()) {
                    list.addAll(allEthSymbolList);
                }
                System.out.println("---savedEthSymbolList was null----");
            }
        }
        return list;
    }

    private static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public String spotDailySupplementTask() throws Exception {
        JSONObject root = new JSONObject();
        root.put("result", "-1");
        SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
        if (sysTradeStore != null) {
            SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
            if (sysTrade != null) {
                SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
                SpotDailyBtcStore spotDailyBtcStore = hsfServiceFactory.consumer(SpotDailyBtcStore.class);
                SpotDailyEthStore spotDailyEthStore = hsfServiceFactory.consumer(SpotDailyEthStore.class);
                if (spotSymbolStore != null && spotDailyUsdtStore != null && spotDailyBtcStore != null && spotDailyEthStore != null) {
                    List<SpotSymbol> symbolList = getMissList(spotSymbolStore, spotDailyUsdtStore, spotDailyBtcStore, spotDailyEthStore);
                    if (symbolList != null && !symbolList.isEmpty()) {
                        Date date = DateFormatUtil.parse(since + " 00:00:00", DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                        Map<String, String> params = new HashMap<>();
                        params.put("period", "1day");
                        params.put("size", (getGapCount(date, new Date()) + 1) + "");
                        for (SpotSymbol spotSymbol : symbolList) {
                            params.put("symbol", spotSymbol.getSymbol());
                            try {
                                Set<String> dateSet = new HashSet<>();
                                String result = OKHttpUtil.get("https://api.huobi.pro/market/history/kline", params);
                                if (StringUtils.isNotBlank(result)) {
                                    JSONObject obj = JSONObject.fromObject(result);
                                    if (!obj.containsKey("data")) {
                                        System.out.print("huobi  kline data was null!");
                                        return null;
                                    }
                                    JSONArray klineArray = obj.getJSONArray("data");
                                    if (klineArray != null && !klineArray.isEmpty()) {
                                        List<SpotDailyUsdt> usdtList = new ArrayList<>();
                                        List<SpotDailyBtc> btcList = new ArrayList<>();
                                        List<SpotDailyEth> ethList = new ArrayList<>();
                                        for (int x = klineArray.size() - 1; x < klineArray.size(); x++) {// 3 2 1
                                            JSONObject jsonObj = klineArray.getJSONObject(x);
                                            String dateStr = DateFormatUtil.format(new Date(jsonObj.getLong("id") * 1000),
                                                    DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                            Date tradingDate = DateFormatUtil.parse(dateStr, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                            if (dateSet.contains(dateStr)) {
                                                continue;
                                            }
                                            dateSet.add(dateStr);
                                            if (spotSymbol.getMarket().equals("usdt")) {
                                                Integer count = spotDailyUsdtStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                        spotSymbol.getId(), tradingDate);
                                                if (count == 1) {
                                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                } else if (count == 0) {
                                                    SpotDailyUsdt usdt = new SpotDailyUsdt();
                                                    usdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                    usdt.setTradeId(sysTrade);
                                                    usdt.setSymbolId(spotSymbol);
                                                    usdt.setTradingDay(tradingDate);
                                                    usdt.setLastPrice(jsonObj.getDouble("close"));
                                                    if (jsonObj.containsKey("amount")) {
                                                        usdt.setVolume(jsonObj.getDouble("amount"));
                                                    }
                                                    if (jsonObj.containsKey("vol")) {
                                                        usdt.setTurnover(jsonObj.getDouble("vol"));
                                                    }
                                                    usdt.setCreatedBy("bySave");
                                                    usdt.setCreated(new Date());
                                                    usdtList.add(usdt);
                                                }
                                            } else if (spotSymbol.getMarket().equals("btc")) {
                                                Integer count = spotDailyBtcStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                        spotSymbol.getId(), tradingDate);
                                                if (count == 1) {
                                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                } else if (count == 0) {
                                                    SpotDailyBtc btc = new SpotDailyBtc();
                                                    btc.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                    btc.setTradeId(sysTrade);
                                                    btc.setSymbolId(spotSymbol);
                                                    btc.setTradingDay(tradingDate);
                                                    btc.setLastPrice(jsonObj.getDouble("close"));
                                                    if (jsonObj.containsKey("amount")) {
                                                        btc.setVolume(jsonObj.getDouble("amount"));
                                                    }
                                                    if (jsonObj.containsKey("vol")) {
                                                        btc.setTurnover(jsonObj.getDouble("vol"));
                                                    }
                                                    btc.setCreatedBy("bySave");
                                                    btc.setCreated(new Date());
                                                    btcList.add(btc);
                                                }
                                            } else if (spotSymbol.getMarket().equals("eth")) {
                                                Integer count = spotDailyEthStore.getCountTradeSymbolDay(sysTrade.getId(),
                                                        spotSymbol.getId(), tradingDate);
                                                if (count == 1) {
                                                    System.out.println("huobi daily task---" + dateStr + " " + spotSymbol.getSymbol() + " count >1");
                                                } else if (count == 0) {
                                                    SpotDailyEth eth = new SpotDailyEth();
                                                    eth.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                    eth.setTradeId(sysTrade);
                                                    eth.setSymbolId(spotSymbol);
                                                    eth.setTradingDay(tradingDate);
                                                    eth.setLastPrice(jsonObj.getDouble("close"));
                                                    if (jsonObj.containsKey("amount")) {
                                                        eth.setVolume(jsonObj.getDouble("amount"));
                                                    }
                                                    if (jsonObj.containsKey("vol")) {
                                                        eth.setTurnover(jsonObj.getDouble("vol"));
                                                    }
                                                    eth.setCreatedBy("bySave");
                                                    eth.setCreated(new Date());
                                                    ethList.add(eth);
                                                }
                                            }
                                        }
                                        if (!usdtList.isEmpty()) {
                                            spotDailyUsdtStore.save(usdtList, Persistent.SAVE);
                                            System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + usdtList.size());
                                        }
                                        if (!btcList.isEmpty()) {
                                            spotDailyBtcStore.save(btcList, Persistent.SAVE);
                                            System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + btcList.size());
                                        }
                                        if (!ethList.isEmpty()) {
                                            spotDailyEthStore.save(ethList, Persistent.SAVE);
                                            System.out.println("huobi daily task---" + spotSymbol.getSymbol() + " save success " + ethList.size());
                                        }
                                        TimeUnit.SECONDS.sleep(2);//秒
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("huobi daily task over---------");
                    root.put("result", "0");
                    writeJsonByAction(root.toString());
                }
            }
        }
        return null;
    }
}
