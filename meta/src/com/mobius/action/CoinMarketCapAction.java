package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysCoin;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysCoinStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;

import java.util.ArrayList;
import java.util.List;

@Action(name = "coin", namespace = "/coinmarketcap")
public class CoinMarketCapAction extends BaseAction {


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String execute() throws Exception {
        return null;
    }

    public String initCoin() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            if (sysCoinStore != null) {
                String resultStr = OKHttpUtil.get("https://api.coinmarketcap.com/v2/listings/", null);
                if (StringUtils.isNotBlank(resultStr)) {
                    JSONObject root = JSONObject.fromObject(resultStr);
                    if (!root.containsKey("data")) {
                        return null;
                    }
                    JSONArray symbols = root.getJSONArray("data");
                    if (!symbols.isEmpty()) {
                        List<SysCoin> sysCoinList = new ArrayList<>();
                        for (Object obj : symbols) {
                            JSONObject coin = JSONObject.fromObject(obj);
                            if (coin != null) {
                                SysCoin sysCoin = new SysCoin();
                                sysCoin.setId(coin.getLong("id"));
                                sysCoin.setName(coin.getString("name"));
                                sysCoin.setSymbol(coin.getString("symbol"));
                                sysCoin.setWebsiteSlug(coin.getString("website_slug"));
                                sysCoin.setUseYn("Y");
                                sysCoinList.add(sysCoin);
                            }
                        }
                        if (!sysCoinList.isEmpty()) {
                            sysCoinStore.save(sysCoinList, Persistent.SAVE);
                            result.put("result", "0");
                        }
                    }
                }
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public String spotSymbolCoin() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
            if (sysCoinStore != null && spotSymbolStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$alias("tradeId","tradeId"));
                selectorList.add(SelectorUtils.$eq("tradeId.id", 5l));
                List<SpotSymbol> spotSymbolList = spotSymbolStore.getList(selectorList);
                if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                    for (SpotSymbol spotSymbol : spotSymbolList) {
                        String symbol = spotSymbol.getSymbol();
                        String market = spotSymbol.getMarket();
                        if(spotSymbol.getTradeId().getId().longValue()==3){
                            market=market.toUpperCase();
                        }
                        if(spotSymbol.getTradeId().getId().longValue()==5){
                            if(market.equals("usdt")){
                                market="usd";
                            }
                        }
                        System.out.println(symbol);
                        String sym = symbol.replaceAll(market, "");
                        System.out.println(sym);
                        if (StringUtils.isNotBlank(sym)) {
                            SysCoin sysCoin = sysCoinStore.getBySymbol(sym.toUpperCase());
                            if (sysCoin != null) {
                                spotSymbol.setCoinId(sysCoin);
                            }
                        }
                    }
                    spotSymbolStore.save(spotSymbolList, Persistent.UPDATE);
                }
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public String futuresSymbolCoin() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            FuturesSymbolStore futuresSymbolStore = hsfServiceFactory.consumer(FuturesSymbolStore.class);
            if (sysCoinStore != null && futuresSymbolStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$eq("tradeId.id", 1l));
                List<FuturesSymbol> futuresSymbolList = futuresSymbolStore.getList(selectorList);
                if (futuresSymbolList != null && !futuresSymbolList.isEmpty()) {
                    for (FuturesSymbol spotSymbol : futuresSymbolList) {
                        String symbol = spotSymbol.getSymbol();
                        System.out.println(symbol);
                        String s[] = symbol.split("_");
                        System.out.println(s[0]);
                        if (StringUtils.isNotBlank(s[0])) {
                            SysCoin sysCoin = sysCoinStore.getBySymbol(s[0].toUpperCase());
                            if (sysCoin != null) {
                                spotSymbol.setCoinId(sysCoin);
                            }
                        }
                    }
                    futuresSymbolStore.save(futuresSymbolList, Persistent.UPDATE);
                }
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}