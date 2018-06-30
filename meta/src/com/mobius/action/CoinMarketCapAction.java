package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.entity.sys.SysCoin;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysCapitalizationStore;
import com.mobius.providers.store.sys.SysCoinStore;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Action(name = "coin", namespace = "/coinmarketcap")
public class CoinMarketCapAction extends BaseAction {


    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @ReqGet
    private String startDate;

    @ReqGet
    private String endDate;

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

    public String getHistoryData() throws Exception {
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("Jan", "01");
        monthMap.put("Feb", "02");
        monthMap.put("Mar", "03");
        monthMap.put("Apr", "04");
        monthMap.put("May", "05");
        monthMap.put("Jun", "06");
        monthMap.put("Jul", "07");
        monthMap.put("Aug", "08");
        monthMap.put("Sep", "09");
        monthMap.put("Oct", "10");
        monthMap.put("Nov", "11");
        monthMap.put("Dec", "12");

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);
            if (sysCoinStore != null && sysCapitalizationStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                List<SysCoin> sysCoinList = sysCoinStore.getList(selectorList);
                if (!sysCoinList.isEmpty()) {
                    for (SysCoin coin : sysCoinList) {
                        List<SysCapitalization> list = this.fetchData(monthMap, coin.getWebsiteSlug(), coin);
                        if (list != null && !list.isEmpty()) {
                            System.out.println("-------coin market cap  symbol=" + coin.getName() + " start....");
                            sysCapitalizationStore.save(list, Persistent.SAVE);
                            System.out.println("-------coin market cap  symbol=" + coin.getName() + " end.");

                        }
                    }
                }
                result.put("result", "0");
                writeJsonByAction(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<SysCapitalization> fetchData(Map<String, String> monthMap, String websiteSlug, SysCoin sysCoin) throws Exception {
        if (StringUtils.isBlank(startDate)) {
            startDate = "20130428";
        }
        if (StringUtils.isBlank(endDate)) {
            Date yesterday = DateFormatUtil.addDay(new Date(), -1);
            endDate = DateFormatUtil.format(yesterday, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
        }

        String url = "https://coinmarketcap.com/currencies/" + websiteSlug + "/historical-data/?start=" +
                startDate + "&end=" + endDate;

        List<SysCapitalization> list = new ArrayList<>();

        try {
            String message = OKHttpUtil.get(url, null);
            if (StringUtils.isNotBlank(message)) {
                String p = "(<td[^>]*>[^<]*</td>)";
                Pattern pt = Pattern.compile(p);
                Matcher m = pt.matcher(message);
                List<String> tdList = new ArrayList<>();
                while (m.find()) {
                    tdList.add(getContext(m.group(1)));
                }
                if (!tdList.isEmpty()) {
                    Date create = Calendar.getInstance().getTime();
                    for (int x = 0; x < tdList.size(); x += 7) {
                        SysCapitalization sysCapitalization = new SysCapitalization();
                        sysCapitalization.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                        sysCapitalization.setCoinId(sysCoin);
                        sysCapitalization.setCirculating(0d);
                        sysCapitalization.setVolume(0d);
                        sysCapitalization.setCreated(create);
                        sysCapitalization.setUseYn("Y");
                        sysCapitalization.setCreatedBy("batch");
                        sysCapitalization.setCreated(create);
                        String date = tdList.get(x);
                        if (StringUtils.isNotBlank(date)) {
                            String[] dateArray = date.split(" ");
                            if (dateArray.length == 3) {
                                String _s = dateArray[2] + "-" + monthMap.get(dateArray[0]) + "-" + dateArray[1] + " 00:00:00";
                                Date _d = DateFormatUtil.parse(_s, DateFormatUtil.YMDHMS_PATTERN);
                                sysCapitalization.setRecordDate(_d);
                                sysCapitalization.setRecordTime(_d);
                            }
                        }
                        String marketCap = tdList.get(x + 6);
                        if (StringUtils.isNotBlank(marketCap) && !marketCap.equals("null") && !marketCap.equals("-")) {
                            sysCapitalization.setVolume(Double.parseDouble(marketCap));
                        }
                        list.add(sysCapitalization);
                    }
                } else {
                    System.out.println("------------coinmarketcap fetch data null and symole is" + sysCoin.getName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }


    private String getContext(String html) {
        List<String> resultList = new ArrayList<>();
        Pattern p = Pattern.compile(">([^</]+)</");
        Matcher m = p.matcher(html);
        while (m.find()) {
            resultList.add(m.group(1).replace(",", ""));
        }
        return resultList.isEmpty() ? null : resultList.get(0);
    }


    public String spotSymbolCoin() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
            if (sysCoinStore != null && spotSymbolStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$eq("tradeId.id", 1l));
                List<SpotSymbol> spotSymbolList = spotSymbolStore.getList(selectorList);
                if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                    for (SpotSymbol spotSymbol : spotSymbolList) {
                        String symbol = spotSymbol.getSymbol();
                        System.out.println(symbol);
                        String s[] = symbol.split("_");
                        System.out.println(s[0]);
                        if (StringUtils.isNotBlank(s[0])) {
                            List<Selector> selectors = new ArrayList<>();
                            selectors.add(SelectorUtils.$eq("symbol", s[0].toUpperCase()));
                            List<SysCoin> sysCoinList = sysCoinStore.getList(selectors);
                            if (sysCoinList != null && sysCoinList.size() == 1) {
                                SysCoin sysCoin = sysCoinList.get(0);
                                if (sysCoin != null) {
                                    spotSymbol.setCoinId(sysCoin);
                                }
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


    public String spotSymbolCoin1() throws Exception {

        JSONObject result = new JSONObject();
        result.put("result", "-1");
        try {
            SysCoinStore sysCoinStore = hsfServiceFactory.consumer(SysCoinStore.class);
            SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
            if (sysCoinStore != null && spotSymbolStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$alias("tradeId", "tradeId"));
                selectorList.add(SelectorUtils.$not(SelectorUtils.$eq("tradeId.id", 1l)));
                List<SpotSymbol> spotSymbolList = spotSymbolStore.getList(selectorList);
                if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                    for (SpotSymbol spotSymbol : spotSymbolList) {
                        String symbol = spotSymbol.getSymbol();
                        String market = spotSymbol.getMarket();
                        if (spotSymbol.getTradeId().getId().longValue() == 5) {
                            if (market.equals("usdt")) {
                                market = "usd";
                            }
                        }
                        if (spotSymbol.getTradeId().getId().longValue() == 3) {
                            market = market.toUpperCase();
                        }
                        System.out.println(symbol);
                        symbol = symbol.replaceAll(market, "");
                        System.out.println(symbol);
                        if (StringUtils.isNotBlank(symbol)) {
                            List<Selector> selectors = new ArrayList<>();
                            selectors.add(SelectorUtils.$eq("symbol", symbol.toUpperCase()));
                            List<SysCoin> sysCoinList = sysCoinStore.getList(selectors);
                            if (sysCoinList != null && sysCoinList.size() == 1) {
                                SysCoin sysCoin = sysCoinList.get(0);
                                if (sysCoin != null) {
                                    spotSymbol.setCoinId(sysCoin);
                                }
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
                            List<Selector> selectors = new ArrayList<>();
                            selectors.add(SelectorUtils.$eq("symbol", s[0].toUpperCase()));
                            List<SysCoin> sysCoinList = sysCoinStore.getList(selectors);
                            if (sysCoinList != null && sysCoinList.size() == 1) {
                                SysCoin sysCoin = sysCoinList.get(0);
                                if (sysCoin != null) {
                                    spotSymbol.setCoinId(sysCoin);
                                }
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