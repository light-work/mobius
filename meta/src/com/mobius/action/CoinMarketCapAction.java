package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.sys.SysCoin;
import com.mobius.providers.store.sys.SysCoinStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
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
}