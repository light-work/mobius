package com.mobius.action;

import com.google.inject.Inject;
import com.mobius.Utils;
import com.mobius.entity.cal.*;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.entity.sys.SysCoin;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.store.cal.*;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.spot.SpotSymbolStore;
import com.mobius.providers.store.sys.SysCapitalizationStore;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.HSFServiceFactory;
import org.guiceside.web.action.BaseAction;
import org.guiceside.web.annotation.Action;
import org.guiceside.web.annotation.ReqGet;

import java.util.*;

/**
 * Created by Lara Croft on 2018/6/3.
 */
@Action(name = "sample", namespace = "/cal")
public class CalSampleCoinAction extends BaseAction {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @ReqGet
    private Integer offset;

    @ReqGet
    private String date;

    private Integer month;

    private Integer useYear;

    private Integer userMonth;

    private Integer year;

    @ReqGet
    private String dateTime;

    @Override
    public String execute() throws Exception {
        return null;
    }

    private void getDate() throws Exception {
        if (offset == null) offset = 0;
        if (StringUtils.isNotBlank(date)) {
            Date _d = DateFormatUtil.parse(date + "-01");
            Calendar c = Calendar.getInstance();
            c.setTime(_d);
            useYear = c.get(Calendar.YEAR);
            userMonth = c.get(Calendar.MONTH) + 1;
            c.add(Calendar.MONTH, -1);
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);

        } else if (StringUtils.isNotBlank(dateTime)) {
            Date _d = DateFormatUtil.parse(dateTime);
            Calendar c = Calendar.getInstance();
            c.setTime(_d);
            useYear = c.get(Calendar.YEAR);
            userMonth = c.get(Calendar.MONTH) + 1;
            c.add(Calendar.MONTH, -1);
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);
        }
    }

    /**
     * 往前推三个月查找适合的样本
     *
     * @return 是否成功
     * @throws Exception
     */
    private boolean filterSymbols() throws Exception {
        getDate();
        if (month == null || year == null) {
            return false;
        }
        //get store
        CalSampleSpotSymbolStore calSampleSpotSymbolStore = hsfServiceFactory.consumer(CalSampleSpotSymbolStore.class);
        SpotSymbolStore spotSymbolStore = hsfServiceFactory.consumer(SpotSymbolStore.class);
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);// 每日一条

        if (spotSymbolStore != null && calSampleSpotSymbolStore != null && spotDailyUsdtStore != null) {

            //get symbol
            List<Selector> selectorList = new ArrayList<>();
            selectorList.add(SelectorUtils.$eq("useYn", "Y"));
            selectorList.add(SelectorUtils.$eq("market", "usdt"));

            List<SpotSymbol> spotSymbolList = spotSymbolStore.getList(selectorList);

            List<Selector> dailySelector = new ArrayList<>();

            //三个月前的数据 (offset)
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.add(Calendar.DAY_OF_MONTH, -(offset));
            Date endDate = calendar.getTime();

            calendar.add(Calendar.MONTH, -2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -(offset + 1));
            Date startDate = calendar.getTime();

            String startTime = DateFormatUtil.format(startDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            String endTime = DateFormatUtil.format(endDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            dailySelector.add(SelectorUtils.$le("tradingDay", endDate));
            dailySelector.add(SelectorUtils.$gt("tradingDay", startDate));

            System.out.println("--------calSimpleCoin---- startDate=" + startTime + "  endTime=" + endTime);

            try {

                if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                    Date date = new Date();
                    for (SpotSymbol symbol : spotSymbolList) {
                        List<CalSampleSpotSymbol> saveList = new ArrayList<>();
                        List<CalSampleSpotSymbol> updateList = new ArrayList<>();
                        if (dailySelector.size() == 3) {//replace symbol id
                            dailySelector.remove(2);
                        }
                        dailySelector.add(SelectorUtils.$eq("symbolId.id", symbol.getId()));
                        List<SpotDailyUsdt> spotDailyUsdts = spotDailyUsdtStore.getList(dailySelector);
                        System.out.println("spotDailyUsdts list is null ? " + (spotDailyUsdts == null || spotDailyUsdts.isEmpty()) + " symbol.getId()=" + symbol.getId());
                        if (spotDailyUsdts != null && !spotDailyUsdts.isEmpty()) {

                            Map<String, List<Double>> map = orderList(spotDailyUsdts, endTime);
                            System.out.print("symbol=" + symbol.getSymbol() + "  and map size=" + map.size());
                            if (!map.isEmpty()) {
                                List<Double> turnOverList = new ArrayList<>();
                                for (Map.Entry<String, List<Double>> entry : map.entrySet()) {
                                    turnOverList.add(Utils.median(entry.getValue()));
                                }
                                if (turnOverList.size() == 3) {
                                    Double min = Utils.min(turnOverList.get(0), turnOverList.get(1), turnOverList.get(2));
                                    if (min >= 5000000) {
                                        for (Map.Entry<String, List<Double>> entry : map.entrySet()) {
                                            Double median = Utils.median(entry.getValue());
                                            Integer _year = Integer.parseInt(entry.getKey().split("-")[0]);
                                            Integer _month = Integer.parseInt(entry.getKey().split("-")[1]);
                                            CalSampleSpotSymbol coin = calSampleSpotSymbolStore.getBySymbolIdYearMonthUse(symbol.getId(), useYear, userMonth, _year, _month);
                                            System.out.print("coin is null?" + (coin == null));
                                            if (coin == null) {//save
                                                coin = new CalSampleSpotSymbol();
                                                coin.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                                coin.setYear(_year);
                                                coin.setSymbolId(symbol);
                                                coin.setMedian(median);
                                                coin.setMonth(_month);
                                                coin.setUseMonth(userMonth);
                                                coin.setUseYear(useYear);
                                                coin.setCreated(date);
                                                coin.setCreatedBy("batch");
                                                coin.setUseYn("Y");
                                                saveList.add(coin);
                                            } else {//update
                                                coin.setUseMonth(userMonth);
                                                coin.setUseYear(useYear);
                                                coin.setYear(_year);
                                                coin.setMedian(median);
                                                coin.setMonth(_month);
                                                coin.setSymbolId(symbol);
                                                coin.setUpdated(date);
                                                coin.setUpdatedBy("batchUpdate");
                                                coin.setUseYn("Y");
                                                updateList.add(coin);
                                            }
                                        }
                                    } else {
                                        System.out.println(" symbol id=" + symbol.getId() + " and min=" + min);
                                    }
                                } else {
                                    System.out.println("turnOverList size =" + turnOverList.size() + " symbol id=" + symbol.getId());
                                }
                            }
                        }
                        if (!saveList.isEmpty()) {
                            calSampleSpotSymbolStore.save(saveList, Persistent.SAVE);
                        }
                        if (!updateList.isEmpty()) {
                            calSampleSpotSymbolStore.save(updateList, Persistent.UPDATE);
                        }
                    }
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    private Map<String, List<Double>> orderList(List<SpotDailyUsdt> list, String endTime) throws Exception {
        System.out.println(" list size =" + list.size());
        Map<String, List<Double>> map = new LinkedHashMap<>();
        if (endTime != null) {
            Date endDate = DateFormatUtil.parse(endTime + " 23:59:59", DateFormatUtil.YMDHMS_PATTERN);
            Long key1EndTime = endDate.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);

            if (offset == 0) {
                calendar.set(Calendar.DAY_OF_MONTH, 0);
            } else {
                calendar.add(Calendar.MONTH, -1);
            }

            Long key1StartTime = calendar.getTimeInMillis();
            String key2 = DateFormatUtil.format(calendar.getTime(), DateFormatUtil.YEAR_MONTH_DAY_PATTERN);

            if (offset == 0) {
                calendar.set(Calendar.DAY_OF_MONTH, 0);
            } else {
                calendar.add(Calendar.MONTH, -1);
            }
            Long key2StartTime = calendar.getTimeInMillis();
            String key3 = DateFormatUtil.format(calendar.getTime(), DateFormatUtil.YEAR_MONTH_DAY_PATTERN);


            if (offset == 0) {
                calendar.set(Calendar.DAY_OF_MONTH, 0);
            } else {
                calendar.add(Calendar.MONTH, -1);
            }
            Long key3StartTime = calendar.getTimeInMillis();
            System.out.println(" key1=" + endTime + " key2=" + key2 + " key3=" + key3);
            System.out.println(key1EndTime + " " + key1StartTime + " " + key2StartTime + " " + key3StartTime);

            for (SpotDailyUsdt usdt : list) {
                long time = usdt.getTradingDay().getTime();
                if (time <= key1EndTime && time > key1StartTime) {
                    List<Double> _group = map.get(endTime);
                    if (_group == null) {
                        _group = new ArrayList<>();
                    }
                    _group.add(usdt.getTurnover());
                    map.put(endTime, _group);

                } else if (time <= key1StartTime && time > key2StartTime) {
                    List<Double> _group = map.get(key2);
                    if (_group == null) {
                        _group = new ArrayList<>();
                    }
                    _group.add(usdt.getTurnover());
                    map.put(key2, _group);
                } else if (time <= key2StartTime && time > key3StartTime) {
                    List<Double> _group = map.get(key3);
                    if (_group == null) {
                        _group = new ArrayList<>();
                    }
                    _group.add(usdt.getTurnover());
                    map.put(key3, _group);
                }
            }
            if (!map.isEmpty()) {//filter
                List<String> remove = new ArrayList<>();
                for (Map.Entry<String, List<Double>> entry : map.entrySet()) {
                    List<Double> _group = entry.getValue();
                    if (_group.size() < 25) {
                        remove.add(entry.getKey());
                    }
                }
                if (!remove.isEmpty()) {
                    for (String key : remove) {
                        map.remove(key);
                    }
                }
            }

        }
        return map;
    }

    private List<CalSampleSpotSymbol> getLastMonthList(int y, int m, Long coinId) throws Exception {
        CalSampleSpotSymbolStore calSampleSpotSymbolStore = hsfServiceFactory.consumer(CalSampleSpotSymbolStore.class);
        List<CalSampleSpotSymbol> list = new ArrayList<>();
        if (calSampleSpotSymbolStore != null) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, y);
            c.set(Calendar.MONTH, m - 1);
            int year1 = c.get(Calendar.YEAR);
            int month1 = c.get(Calendar.MONTH) + 1;
            List<Selector> selector1 = new ArrayList<>();
            selector1.add(SelectorUtils.$alias("symbolId", "symbolId"));
            selector1.add(SelectorUtils.$alias("symbolId.coinId", "coinId"));
            selector1.add(SelectorUtils.$eq("useYn", "Y"));
            selector1.add(SelectorUtils.$eq("month", month1));
            selector1.add(SelectorUtils.$eq("year", year1));
            if (coinId != null) {
                selector1.add(SelectorUtils.$eq("coinId.id", coinId));
            }
            list = calSampleSpotSymbolStore.getList(selector1);
        }
        return list;
    }

    /**
     * 找出样本对应的币种,并计算覆盖率
     *
     * @return
     * @throws Exception
     */
    private boolean setCoverRate() throws Exception {
        getDate();
        if (month == null || year == null) {
            return false;
        }
        CalSampleSpotCoinStore calSampleSpotCoinStore = hsfServiceFactory.consumer(CalSampleSpotCoinStore.class);
        SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);

        if (sysCapitalizationStore != null && calSampleSpotCoinStore != null) {
            List<Selector> selectorList = new ArrayList<>();

            //获取匹配的symbol
            List<CalSampleSpotSymbol> calSampleSpotSymbolList = getLastMonthList(year, month, null);
            if (calSampleSpotSymbolList != null && !calSampleSpotSymbolList.isEmpty()) {
                System.out.println("-----calSampleSpotSymbolList size=" + calSampleSpotSymbolList.size());
                Set<SysCoin> coins = new LinkedHashSet<>();
                for (CalSampleSpotSymbol symbol : calSampleSpotSymbolList) {
                    if (symbol.getSymbolId() != null && symbol.getSymbolId().getCoinId() != null) {
                        coins.add(symbol.getSymbolId().getCoinId());
                    }
                }
                if (!coins.isEmpty()) {
                    System.out.println("-------coins size=" + coins.size());

                    //上个月市值(offset)
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.DAY_OF_MONTH, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.add(Calendar.DAY_OF_YEAR, -offset);
                    Date firstDayInMonth = calendar.getTime();
                    calendar.add(Calendar.MONTH, 2);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.add(Calendar.DAY_OF_YEAR, -offset - 1);
                    Date lastDayInMonth = calendar.getTime();

                    Map<SysCoin, Double> coinAvgMap = new HashMap<>();
                    Integer _year = calendar.get(Calendar.YEAR);
                    Integer _month = calendar.get(Calendar.MONTH) + 1;
                    Date date = new Date();
                    System.out.print("startTime=" + DateFormatUtil.format(firstDayInMonth, DateFormatUtil.YEAR_MONTH_DAY_PATTERN));
                    System.out.println("endTime=" + DateFormatUtil.format(lastDayInMonth, DateFormatUtil.YEAR_MONTH_DAY_PATTERN));

                    for (SysCoin sysCoin : coins) {
                        //币种上个月市值
                        List<Selector> selectors = new ArrayList<>();
                        selectors.add(SelectorUtils.$eq("coinId.id", sysCoin.getId()));
                        selectors.add(SelectorUtils.$gt("recordDate", firstDayInMonth));
                        selectors.add(SelectorUtils.$le("recordDate", lastDayInMonth));
                        List<SysCapitalization> capitalizationList = sysCapitalizationStore.getList(selectors);
                        if (capitalizationList != null && !capitalizationList.isEmpty()) {
                            Map<String, Double> capitalizationMap = new LinkedHashMap<>();
                            for (SysCapitalization capitalization : capitalizationList) {
                                String key = DateFormatUtil.format(capitalization.getRecordDate(), DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
                                capitalizationMap.put(key, capitalization.getVolume());
                            }
                            if (!capitalizationMap.isEmpty()) {
                                System.out.println("------capitalizationMap size=" + capitalizationMap.size() + " and coin id is " + sysCoin.getId());
                                Double total = 0d;
                                for (Map.Entry<String, Double> entry : capitalizationMap.entrySet()) {
                                    total += entry.getValue();
                                }
                                Double avg = total / capitalizationMap.size();
                                coinAvgMap.put(sysCoin, avg);
                            }
                        } else {
                            System.out.println("--------capitalizationList was null!");
                        }
                    }
                    if (!coinAvgMap.isEmpty()) {
                        //order by value desc
                        List<Map.Entry<SysCoin, Double>> entryArrayList = new ArrayList<>(coinAvgMap.entrySet());
                        Collections.sort(entryArrayList, new Comparator<Map.Entry<SysCoin, Double>>() {
                            @Override
                            public int compare(Map.Entry<SysCoin, Double> o1, Map.Entry<SysCoin, Double> o2) {
                                return o1.getValue() > o2.getValue() ? -1 : 0;
                            }
                        });
                        //calc total
                        Double total = 0d;
                        for (Map.Entry<SysCoin, Double> entry : entryArrayList) {
                            total += entry.getValue();
                            System.out.println(entry.getKey().getSymbol());
                        }
                        System.out.println("----total =" + total);
                        List<CalSampleSpotCoin> saveList = new ArrayList<>();
                        List<CalSampleSpotCoin> updateList = new ArrayList<>();
                        //calc percent
                        double basePercent = 0d;
                        System.out.println("coin sum=" + entryArrayList.size());
                        for (Map.Entry<SysCoin, Double> entry : entryArrayList) {

                            if (basePercent >= 95) {
                                break;
                            }
                            Double percent = entry.getValue() / total * 100;
                            basePercent += percent;
                            CalSampleSpotCoin coin = calSampleSpotCoinStore.getByCoinIdYearMonth(entry.getKey().getId(), _year, _month);
                            if (coin == null) {//save
                                coin = new CalSampleSpotCoin();
                                coin.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                coin.setCoinId(entry.getKey());
                                coin.setYear(_year);
                                coin.setAvgVolume(entry.getValue());
                                coin.setCoverRate(basePercent);
                                coin.setMonth(_month);
                                coin.setCreated(date);
                                coin.setCreatedBy("batch");
                                coin.setUseYn("Y");
                                saveList.add(coin);
                            } else {//update
                                coin.setCoinId(entry.getKey());
                                coin.setAvgVolume(entry.getValue());
                                coin.setCoverRate(basePercent);
                                coin.setYear(_year);
                                coin.setMonth(_month);
                                coin.setUpdated(date);
                                coin.setUpdatedBy("batchUpdate");
                                coin.setUseYn("Y");
                                updateList.add(coin);
                            }
                        }
                        if (!saveList.isEmpty()) {
                            calSampleSpotCoinStore.save(saveList, Persistent.SAVE);
                        }
                        if (!updateList.isEmpty()) {
                            calSampleSpotCoinStore.save(updateList, Persistent.UPDATE);
                        }
                        return true;
                    }
                } else {
                    System.out.println("--------coins was empty!");
                }
            } else {
                System.out.println("--------calSampleSpotSymbolList was empty!");
            }
        }
        return false;
    }

    /**
     * 设置币种权重
     *
     * @return
     * @throws Exception
     */
    private boolean setCoinWeight() throws Exception {
        getDate();
        if (month == null || year == null) {
            return false;
        }
        CalSampleSpotCoinStore calSampleSpotCoinStore = hsfServiceFactory.consumer(CalSampleSpotCoinStore.class);
        SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);
        if (calSampleSpotCoinStore != null && sysCapitalizationStore != null) {
            List<Selector> spotCoinSelectors = new ArrayList<>();
            spotCoinSelectors.add(SelectorUtils.$eq("year", year));
            spotCoinSelectors.add(SelectorUtils.$eq("month", month));
            spotCoinSelectors.add(SelectorUtils.$alias("coinId", "coinId"));

            List<CalSampleSpotCoin> spotCoinList = calSampleSpotCoinStore.getList(spotCoinSelectors);
            if (spotCoinList != null && !spotCoinList.isEmpty()) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.DAY_OF_MONTH, 0);
                calendar.set(Calendar.MONTH, month);
                calendar.add(Calendar.DAY_OF_YEAR, -(offset + 1));
                Date startDate = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date endDate = calendar.getTime();

                Map<SysCoin, Double> turnoverMap = new LinkedHashMap<>();
                Double total = 0d;
                for (CalSampleSpotCoin sampleSpotCoin : spotCoinList) {
                    //获取上月最后一天交易日市值
                    List<Selector> selectorList = new ArrayList<>();
                    selectorList.add(SelectorUtils.$gt("recordDate", startDate));
                    selectorList.add(SelectorUtils.$le("recordDate", endDate));
                    selectorList.add(SelectorUtils.$eq("coinId.id", sampleSpotCoin.getCoinId().getId()));
                    List<SysCapitalization> sysCapitalizationList = sysCapitalizationStore.getList(selectorList);
                    if (sysCapitalizationList != null && !sysCapitalizationList.isEmpty()) {
                        System.out.println(" sysCapitalizationList size=" + sysCapitalizationList.size() + " trading_day=" + DateFormatUtil.format(sysCapitalizationList.get(sysCapitalizationList.size() - 1).getRecordTime(), DateFormatUtil.YMDHMS_PATTERN));
                        Double volume = sysCapitalizationList.get(sysCapitalizationList.size() - 1).getVolume();
                        turnoverMap.put(sampleSpotCoin.getCoinId(), volume);
                        total += volume;
                    } else {
                        System.out.println("------sysCapitalizationList  is null");
                    }
                }
                if (!turnoverMap.isEmpty()) {
                    List<Double> weightList = new ArrayList<>();
                    for (Map.Entry<SysCoin, Double> entry : turnoverMap.entrySet()) {
                        Double percent = entry.getValue() / total * 100;
                        weightList.add(percent);
                    }
                    reWeight(weightList);

                    //replace
                    int index = 0;
                    for (Map.Entry<SysCoin, Double> entry : turnoverMap.entrySet()) {
                        turnoverMap.put(entry.getKey(), weightList.get(index++));
                        //System.out.println(entry.getKey().getSymbol()+" "+ weightList.get(index++));
                    }
                    List<CalSampleSpotCoin> updateList = new ArrayList<>();
                    for (CalSampleSpotCoin sampleSpotCoin : spotCoinList) {
                        sampleSpotCoin.setWeight(turnoverMap.get(sampleSpotCoin.getCoinId()));
                        sampleSpotCoin.setUpdatedBy("batchUpdate");
                        sampleSpotCoin.setUpdated(new Date());
                        updateList.add(sampleSpotCoin);
                    }
                    if (!updateList.isEmpty()) {
                        calSampleSpotCoinStore.save(updateList, Persistent.UPDATE);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 反向设置币种对应的样本的权重
     *
     * @return
     * @throws Exception
     */
    private boolean setSymbolsWeight() throws Exception {
        getDate();
        if (month == null || year == null) {
            return false;
        }
        JSONObject root = new JSONObject();
        root.put("result", -1);
        CalSampleSpotCoinStore calSampleSpotCoinStore = hsfServiceFactory.consumer(CalSampleSpotCoinStore.class);
        CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
        if (calSampleSpotCoinStore != null && calSampleSpotSymbolWeightStore != null) {


            List<Selector> spotCoinSelectors = new ArrayList<>();
            spotCoinSelectors.add(SelectorUtils.$eq("year", year));
            spotCoinSelectors.add(SelectorUtils.$eq("month", month));
            spotCoinSelectors.add(SelectorUtils.$alias("coinId", "coinId"));

            List<CalSampleSpotCoin> coinList = calSampleSpotCoinStore.getList(spotCoinSelectors);
            if (coinList != null && !coinList.isEmpty()) {
                Date date = new Date();
                List<CalSampleSpotSymbolWeight> saveList = new ArrayList<>();
                List<CalSampleSpotSymbolWeight> updateList = new ArrayList<>();
                for (CalSampleSpotCoin coin : coinList) {
                    List<CalSampleSpotSymbol> spotSymbolList = getLastMonthList(year, month, coin.getCoinId().getId());
                    if (spotSymbolList != null && !spotSymbolList.isEmpty()) {
                        Double total = 0d;
                        for (CalSampleSpotSymbol symbol : spotSymbolList) {
                            total += symbol.getMedian();
                        }
                        if (total != 0d) {
                            for (CalSampleSpotSymbol symbol : spotSymbolList) {
                                Double weight = symbol.getMedian() / total * (coin.getWeight() / 100);
                                CalSampleSpotSymbolWeight calSampleSpotSymbolWeight = calSampleSpotSymbolWeightStore.getBySymbolIdYearMonth(symbol.getSymbolId().getId(), year, month);
                                if (calSampleSpotSymbolWeight == null) {
                                    calSampleSpotSymbolWeight = new CalSampleSpotSymbolWeight();
                                    calSampleSpotSymbolWeight.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                                    calSampleSpotSymbolWeight.setCreated(date);
                                    calSampleSpotSymbolWeight.setCreatedBy("batch");
                                    calSampleSpotSymbolWeight.setUseYn("Y");
                                    calSampleSpotSymbolWeight.setSymbolId(symbol.getSymbolId());
                                    calSampleSpotSymbolWeight.setWeight(weight);
                                    calSampleSpotSymbolWeight.setYear(year);
                                    calSampleSpotSymbolWeight.setMonth(month);
                                    saveList.add(calSampleSpotSymbolWeight);
                                } else {
                                    calSampleSpotSymbolWeight.setUpdated(date);
                                    calSampleSpotSymbolWeight.setUpdatedBy("batchUpdate");
                                    calSampleSpotSymbolWeight.setSymbolId(symbol.getSymbolId());
                                    calSampleSpotSymbolWeight.setWeight(weight);
                                    calSampleSpotSymbolWeight.setYear(year);
                                    calSampleSpotSymbolWeight.setMonth(month);
                                    updateList.add(calSampleSpotSymbolWeight);
                                }
                            }
                        } else {
                            System.out.println("----total =0");
                        }
                    } else {
                        System.out.println("------spotSymbolList was null.");
                    }
                }
                if (!saveList.isEmpty()) {
                    calSampleSpotSymbolWeightStore.save(saveList, Persistent.SAVE);
                }
                if (!updateList.isEmpty()) {
                    calSampleSpotSymbolWeightStore.save(updateList, Persistent.UPDATE);
                }
                return true;
            } else {
                System.out.println("-----coinList was null.");
            }
        }

        return false;
    }

    private static void reWeight(List<Double> list) {
        if (list != null && !list.isEmpty()) {
            double overflow = -1;
            Double sum = null;
            Map<Integer, Double> replaceMap = new HashMap<>();
            System.out.println("");
            System.out.println(join(list));
            System.out.println("");
            for (int index = 0; index < list.size(); index++) {
                Double value = list.get(index);
                if (value > 30 && sum == null) {
                    replaceMap.put(index, 30d);
                    sum = sum(list.subList(index + 1, list.size()));
                    overflow = value - 30;
                } else if (overflow > -1) {
                    Double newWeight = value + (value / sum) * overflow;
                    System.out.println(newWeight + "=" + value + "+(" + value + "/" + sum + ")*" + overflow);
                    replaceMap.put(index, newWeight);
                }
            }
            if (!replaceMap.isEmpty()) {
                for (Map.Entry<Integer, Double> entry : replaceMap.entrySet()) {
                    list.set(entry.getKey(), entry.getValue());
                }
            }
            for (Double d : list) {
                if (d > 30) {
                    reWeight(list);
                }
            }

        }
    }

    private static Double sum(List<Double> list) {
        Double sum = null;
        if (list != null && !list.isEmpty()) {
            sum = 0d;
            for (Double d : list) {
                sum += d;
            }
        }
        return sum;
    }

    private static String join(List<Double> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Double d : list) {
            sb.append(d).append("d, ");
        }
        return sb.deleteCharAt(sb.toString().length() - 1).append("]").toString();
    }

    /**
     * 计算指数
     *
     * @param lastPriceFormDaily 实时价格是否来自历史数据,如果是false,者会从样本权重取lastPrice
     * @return
     * @throws Exception
     */
    private boolean calcPoint(boolean lastPriceFormDaily) throws Exception {
        getDate();
        if (month == null || year == null) {
            return false;
        }
        CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
        SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);
        SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
        CalSampleSpotPointStore calSampleSpotPointStore = hsfServiceFactory.consumer(CalSampleSpotPointStore.class);
        CalSampleSpotWeightHistoryStore calSampleSpotWeightHistoryStore = hsfServiceFactory.consumer(CalSampleSpotWeightHistoryStore.class);
        CalSampleSpotDailyPointStore calSampleSpotDailyPointStore = hsfServiceFactory.consumer(CalSampleSpotDailyPointStore.class);
        if (StringUtils.isNotBlank(dateTime) && calSampleSpotSymbolWeightStore != null
                && sysCapitalizationStore != null && spotDailyUsdtStore != null && calSampleSpotDailyPointStore!=null
                && calSampleSpotPointStore != null && calSampleSpotWeightHistoryStore != null) {
            //date
            Date date = DateFormatUtil.parse(dateTime + " 00:00:00", DateFormatUtil.YMDHMS_PATTERN);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayIndex = c.get(Calendar.DAY_OF_MONTH);

            //获取全部样本合约
            List<Selector> selectorList = new ArrayList<>();
            selectorList.add(SelectorUtils.$eq("year", year));
            selectorList.add(SelectorUtils.$eq("month", month));
            selectorList.add(SelectorUtils.$alias("symbolId", "symbolId"));
            List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList = calSampleSpotSymbolWeightStore.getList(selectorList);
            if (calSampleSpotSymbolWeightList != null && !calSampleSpotSymbolWeightList.isEmpty()) {
                //获取daily
                Map<Long, Double> todayLastPriceMap = new HashMap<>();
                Map<Long, Double> yesterdayDailyMap = new HashMap<>();
                Map<Long, Double> todayOpenWeightMap = new HashMap<>();
                Double yesterdayPoint = 0d;


                List<Long> symbolIds = new ArrayList<>();
                for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                    symbolIds.add(weight.getSymbolId().getId());
                    todayLastPriceMap.put(weight.getSymbolId().getId(), weight.getLastPrice());//实时价格
                }

                Date today = c.getTime();
                if (lastPriceFormDaily) {//form test
                    todayLastPriceMap = new HashMap<>();
                    //today 收盘价
                    List<Selector> todaySelectors = new ArrayList<>();
                    todaySelectors.add(SelectorUtils.$alias("symbolId", "symbolId"));
                    todaySelectors.add(SelectorUtils.$in("symbolId.id", symbolIds));
                    todaySelectors.add(SelectorUtils.$eq("tradingDay", c.getTime()));
                    List<SpotDailyUsdt> todayDailyList = spotDailyUsdtStore.getList(todaySelectors);
                    if (todayDailyList != null && !todayDailyList.isEmpty()) {
                        for (SpotDailyUsdt usdt : todayDailyList) {
                            todayLastPriceMap.put(usdt.getSymbolId().getId(), usdt.getLastPrice());
                        }
                    } else {
                        System.out.println("-------todayDailyList was null.");
                    }
                }


                //yesterday 收盘价
                c.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterdayDate = c.getTime();
                List<Selector> yesterdaySelectors = new ArrayList<>();
                yesterdaySelectors.add(SelectorUtils.$alias("symbolId", "symbolId"));
                yesterdaySelectors.add(SelectorUtils.$in("symbolId.id", symbolIds));
                yesterdaySelectors.add(SelectorUtils.$eq("tradingDay", c.getTime()));
                List<SpotDailyUsdt> yesterdayDailyList = spotDailyUsdtStore.getList(yesterdaySelectors);
                if (yesterdayDailyList != null && !yesterdayDailyList.isEmpty()) {
                    for (SpotDailyUsdt usdt : yesterdayDailyList) {
                        yesterdayDailyMap.put(usdt.getSymbolId().getId(), usdt.getLastPrice());
                    }
                } else {
                    System.out.println("-------yesterdayDailyList was null.");
                }

                // 昨日点位
                CalSampleSpotDailyPoint calSampleSpotDailyPoint = calSampleSpotDailyPointStore.getByRecordDate(yesterdayDate);
                if (calSampleSpotDailyPoint != null) {
                    yesterdayPoint = calSampleSpotDailyPoint.getPoint();
                }

                if (dayIndex == 1) {// 月初第一天
                    if (yesterdayPoint == null || yesterdayPoint == 0d) {
                        yesterdayPoint = 1000d;
                    }
                    for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                        todayOpenWeightMap.put(weight.getSymbolId().getId(), weight.getWeight());
                    }
                } else {

                    Map<Long, Double> dayBeforeYesterdayDailyMap = new HashMap<>();
                    Map<Long, Double> yesterdayWeightMap = new HashMap<>();

                    //the day before yesterday 收盘价
                    c.add(Calendar.DAY_OF_YEAR, -1);
                    List<Selector> beforeYesterdaySelectors = new ArrayList<>();
                    beforeYesterdaySelectors.add(SelectorUtils.$alias("symbolId", "symbolId"));
                    beforeYesterdaySelectors.add(SelectorUtils.$in("symbolId.id", symbolIds));
                    beforeYesterdaySelectors.add(SelectorUtils.$eq("tradingDay", c.getTime()));
                    List<SpotDailyUsdt> dayBeforeYesterdayDailyList = spotDailyUsdtStore.getList(beforeYesterdaySelectors);
                    if (dayBeforeYesterdayDailyList != null && !dayBeforeYesterdayDailyList.isEmpty()) {
                        for (SpotDailyUsdt usdt : dayBeforeYesterdayDailyList) {
                            dayBeforeYesterdayDailyMap.put(usdt.getSymbolId().getId(), usdt.getLastPrice());
                        }
                    } else {
                        System.out.println("-------dayBeforeYesterdayDailyList was null.");
                    }


                    List<Selector> yesterdayWeightSelector = new ArrayList<>();
                    yesterdayWeightSelector.add(SelectorUtils.$alias("symbolId", "symbolId"));
                    yesterdayWeightSelector.add(SelectorUtils.$in("symbolId.id", symbolIds));
                    yesterdayWeightSelector.add(SelectorUtils.$eq("recordDate", yesterdayDate));
                    List<CalSampleSpotWeightHistory> yesterdayWeightList = calSampleSpotWeightHistoryStore.getList(yesterdayWeightSelector);
                    if (yesterdayWeightList != null && !yesterdayWeightList.isEmpty()) {
                        for (CalSampleSpotWeightHistory obj : yesterdayWeightList) {
                            yesterdayWeightMap.put(obj.getSymbolId().getId(), obj.getWeight());
                        }
                    } else {
                        System.out.println("yesterdayWeightList was null");
                    }

                    //样本权重 map sum(样本昨日收市价/样本前日收市价*样本昨日开市权重)
                    Map<Long, Double> numeratorMap = new HashMap<>();
                    Double denominator = 0d;
                    for (Long symbolId : symbolIds) {
                        Double yesterdayClosePrice = yesterdayDailyMap.get(symbolId);
                        Double dayBeforeYeseterdayClosePrice = dayBeforeYesterdayDailyMap.get(symbolId);
                        Double yesterdayWeight = yesterdayWeightMap.get(symbolId);
                        if (yesterdayWeight == null || dayBeforeYeseterdayClosePrice == null || yesterdayClosePrice == null) {
                            System.out.println("yesterdayWeight=" + yesterdayWeight + "  yesterdayDate=" +
                                    DateFormatUtil.format(yesterdayDate, DateFormatUtil.YEAR_MONTH_DAY_PATTERN)
                                    + " and today=" + DateFormatUtil.format(today, DateFormatUtil.YEAR_MONTH_DAY_PATTERN));
                            System.out.println("stmbolId=" + symbolId);
                            System.out.println(" dayBeforeYeseterdayClosePrice=" +
                                    dayBeforeYeseterdayClosePrice + " yesterdayClosePrice =" + yesterdayClosePrice);
                            return false;
                        }
                        Double numerator = yesterdayClosePrice / dayBeforeYeseterdayClosePrice * yesterdayWeight;
                        numeratorMap.put(symbolId, numerator);
                        denominator += numerator;
                    }
                    for (Long symbolId : symbolIds) {
                        if (denominator != 0d && numeratorMap.get(symbolId) != null && numeratorMap.get(symbolId) != 0d) {
                            Double yesterdayCloseWeight = numeratorMap.get(symbolId) / denominator;
                            todayOpenWeightMap.put(symbolId, yesterdayCloseWeight);
                        } else {
                            System.out.println("---denominator==0? " + (denominator == 0d) + " numeratorMap.get(" + symbolId + ")=" + numeratorMap.get(symbolId));
                        }
                    }

                }

                //计算当日实时成交价/昨日收市价*样本权重 & sum
                Map<Long, Double> map1 = new HashMap<>();
                Double total = 0d;
                for (Long symbolId : symbolIds) {
                    Double todayDealPrice = todayLastPriceMap.get(symbolId);
                    Double yesterdayClosePrice = yesterdayDailyMap.get(symbolId);
                    Double todayOpenWeight = todayOpenWeightMap.get(symbolId);
                    if (todayOpenWeight != null && todayOpenWeight != 0d &&
                            todayDealPrice != null && todayDealPrice != 0d &&
                            yesterdayClosePrice != null && yesterdayClosePrice != 0d) {
                        Double v = todayDealPrice / yesterdayClosePrice * todayOpenWeight;
                        map1.put(symbolId, v);
                        total += v;
                    } else {
                        System.out.println("symbolId=" + symbolId + " currentDealPrice=" + todayDealPrice +
                                "  yesterdayClosePrice=" + yesterdayClosePrice + "  todayOpenWeight" + todayOpenWeight);
                    }
                }
                System.out.println("total=" + total);
                List<CalSampleSpotWeightHistory> saveList = new ArrayList<>();
                for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                    CalSampleSpotWeightHistory history = calSampleSpotWeightHistoryStore.getBySymbolIdDate(weight.getSymbolId().getId(), today);
                    if (history == null) {
                        history = new CalSampleSpotWeightHistory();
                        history.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                        history.setSymbolId(weight.getSymbolId());
                        history.setRecordDate(today);
                        history.setWeight(todayOpenWeightMap.get(weight.getSymbolId().getId()));
                        history.setCreated(new Date());
                        history.setCreatedBy("batch");
                        history.setUseYn("Y");
                        saveList.add(history);
                    } else {
                        Double _w = todayOpenWeightMap.get(weight.getSymbolId().getId());
                        if (history.getWeight() != null && _w != null && !history.getWeight().equals(_w)) {
                            System.out.println("symbolId=" + weight.getSymbolId().getId() + "history weight=" + history.getWeight() + " new =" + _w);
                        }
                    }
                    todayOpenWeightMap.put(weight.getSymbolId().getId(), weight.getWeight());
                }
                if (!saveList.isEmpty()) {
                    calSampleSpotWeightHistoryStore.save(saveList, Persistent.SAVE);
                }
                //计算实时点位
                if (yesterdayPoint != null && yesterdayPoint != 0d) {
                    Double currentPoint = yesterdayPoint * total;

                    Date created = new Date();
                    Persistent status = Persistent.UPDATE;
                    CalSampleSpotDailyPoint todayPoint = calSampleSpotDailyPointStore.getByRecordDate(today);
                    if (todayPoint == null) {
                        status = Persistent.SAVE;
                        todayPoint = new CalSampleSpotDailyPoint();
                        todayPoint.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                        todayPoint.setCreated(created);
                        todayPoint.setCreatedBy("batch");
                        todayPoint.setUpdatedBy("batch");
                        todayPoint.setUpdated(created);
                        todayPoint.setRecordDate(today);
                        todayPoint.setUseYn("Y");
                    }
                    todayPoint.setPoint(currentPoint);

                    CalSampleSpotPoint point = new CalSampleSpotPoint();
                    point.setRecordDate(today);
                    point.setId(DrdsIDUtils.getID(DrdsTable.SPOT));
                    point.setPoint(currentPoint);
                    point.setCreated(created);
                    point.setCreatedBy("batch");
                    point.setUseYn("Y");
                    calSampleSpotPointStore.saveAndDaily(point, Persistent.SAVE, todayPoint, status);
                    return true;
                } else {
                    System.out.println("yesterday point was null and date is " + DateFormatUtil.format(today, DateFormatUtil.YEAR_MONTH_DAY_PATTERN_SHORT));
                }
            } else {
                System.out.println("---calSampleSpotSymbolWeightList was null");
            }
        }
        return false;
    }

    public String batchUpdate() throws Exception {
        String starDate = "2018-1-1 00:00:00";
        String endDate = "2018-6-9 00:00:00";
        new Date().getTime();
        long startTime = DateFormatUtil.parse(starDate, DateFormatUtil.YMDHMS_PATTERN).getTime();
        long endTime = DateFormatUtil.parse(endDate, DateFormatUtil.YMDHMS_PATTERN).getTime();
        for (; startTime <= endTime; ) {
            this.dateTime = DateFormatUtil.format(new Date(startTime), DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            this.calcPoint(true);
            startTime += 24 * 60 * 60 * 1000;
        }

        return null;
    }

    public String calcWeight() throws Exception {
        JSONObject root = new JSONObject();
        root.put("result", -1);
        try {
            boolean isSuccessStep1 = filterSymbols();//过滤样本
            boolean isSuccessStep2 = setCoverRate();//过滤样本对应币种覆盖率
            boolean isSuccessStep3 = setCoinWeight();//计算样本对应币种权重
            boolean isSuccessStep4 = setSymbolsWeight();//反向设置样本的权重
            root.put("filterSymbols", isSuccessStep1 ? 0 : -1);
            root.put("setCoverRate", isSuccessStep2 ? 0 : -1);
            root.put("setCoinWeight", isSuccessStep3 ? 0 : -1);
            root.put("setSymbolsWeight", isSuccessStep4 ? 0 : -1);
            root.put("result", 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        writeJsonByAction(root.toString());
        return null;
    }

    public String calPoint() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", -1);
        Boolean isSuccess = calcPoint(true);
        if (isSuccess) {
            result.put("result", 0);
        }
        writeJsonByAction(result.toString());
        return null;
    }
}
