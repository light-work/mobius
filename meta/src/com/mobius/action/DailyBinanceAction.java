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
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

import java.util.*;

@Action(name = "binance", namespace = "/daily")
public class DailyBinanceAction extends BaseAction {

    private static String tradeSign="BINANCE";


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }

    public String initSymbol() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result","-1");
        try {
            if (StringUtils.isNotBlank(tradeSign)) {
                SysTradeStore sysTradeStore = hsfServiceFactory.consumer(SysTradeStore.class);
                if (sysTradeStore != null) {
                    SysTrade sysTrade = sysTradeStore.getBySign(tradeSign);
                    if (sysTrade != null) {
                        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
                        if (spotSymbolStore != null) {
                            String resultStr=OKHttpUtil.get("https://api.binance.com/api/v1/exchangeInfo",null);
                            if(StringUtils.isNotBlank(resultStr)){
                                JSONObject jsonObject=JSONObject.fromObject(resultStr);
                                if(jsonObject!=null){
                                    JSONArray symbols=jsonObject.getJSONArray("symbols");
                                    if(symbols!=null&&!symbols.isEmpty()){
                                        int displayOrder=1;
                                        List<SpotSymbol> spotSymbolList=new ArrayList<>();
                                        for(Object obj:symbols){
                                            JSONObject symbolObj=(JSONObject)obj;
                                            if(symbolObj!=null){
                                                String symbol=symbolObj.getString("symbol");
                                                String market=symbolObj.getString("quoteAsset");
                                                SpotSymbol spotSymbol=new SpotSymbol();
                                                spotSymbol.setTradeId(sysTrade);
                                                spotSymbol.setSymbol(symbol);
                                                spotSymbol.setMarket(market.toLowerCase());
                                                spotSymbol.setDisplayOrder(displayOrder);
                                                spotSymbol.setUseYn("Y");
                                                spotSymbolList.add(spotSymbol);
                                            }
                                        }
                                        if(spotSymbolList!=null&&!spotSymbolList.isEmpty()){
                                            spotSymbolStore.save(spotSymbolList,Persistent.SAVE);
                                            result.put("result","0");
                                        }
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


    private long usdt(SpotSymbol spotSymbol,long startTime,Map<String, String> params,SysTrade sysTrade,
                      SpotDailyUsdtStore spotDailyUsdtStore) throws Exception{
        if(startTime==0){
            startTime=DateFormatUtil.parse("2017-01-01 00:00:00",DateFormatUtil.YMDHMS_PATTERN).getTime();
        }
        params.put("symbol", spotSymbol.getSymbol());
        params.put("startTime",startTime+"");

        try {
            String resultStr = OKHttpUtil.get("https://api.binance.com/api/v1/klines", params);
            if (StringUtils.isNotBlank(resultStr)) {
                JSONArray klineArray = JSONArray.fromObject(resultStr);
                if (klineArray != null && !klineArray.isEmpty()) {
                    List<SpotDailyUsdt> dailyUsdtList=new ArrayList<>();
                    for (Object dayObj : klineArray) {
                        JSONArray dayAttr = (JSONArray) dayObj;
                        if (dayAttr != null && !dayAttr.isEmpty()) {
                            Long times = dayAttr.getLong(0);
                            Double lastPrice=dayAttr.getDouble(4);
                            Double volume=dayAttr.getDouble(5);
                            Date timeDate = new Date(times);
                            int hours = DateFormatUtil.getDayInHour(timeDate);
                            if (hours != 0) {
                                continue;
                            }
                            SpotDailyUsdt spotDailyUsdt=new SpotDailyUsdt();
                            spotDailyUsdt.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                            spotDailyUsdt.setTradeId(sysTrade);
                            spotDailyUsdt.setSymbolId(spotSymbol);
                            spotDailyUsdt.setTradingDay(timeDate);
                            spotDailyUsdt.setLastPrice(lastPrice);
                            spotDailyUsdt.setVolume(volume);
                            dailyUsdtList.add(spotDailyUsdt);
                        }
                    }
                    spotDailyUsdtStore.save(dailyUsdtList,Persistent.SAVE);
                    System.out.println(spotSymbol.getSymbol()+" save success "+ dailyUsdtList.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime=0l;
        return  endTime;
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
                                params.put("interval", "4h");

                                for (SpotSymbol spotSymbol : symbolList) {


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
}