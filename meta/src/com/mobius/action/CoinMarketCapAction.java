package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.entity.sys.SysCoin;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String url = "https://coinmarketcap.com/currencies/" + websiteSlug + "/historical-data/?start=20130428&end=20180527";

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

}