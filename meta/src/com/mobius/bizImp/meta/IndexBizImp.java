package com.mobius.bizImp.meta;

import com.google.inject.Inject;
import com.mobius.IndexPoint;
import com.mobius.Utils;
import com.mobius.common.BizException;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import com.mobius.entity.cal.CalSampleSpotPoint;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotWeightHistory;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.entity.utils.DrdsIDUtils;
import com.mobius.entity.utils.DrdsTable;
import com.mobius.providers.biz.meta.IndexBiz;
import com.mobius.providers.store.cal.CalSampleSpotDailyPointStore;
import com.mobius.providers.store.cal.CalSampleSpotPointStore;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import com.mobius.providers.store.cal.CalSampleSpotWeightHistoryStore;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
import com.mobius.providers.store.sys.SysCapitalizationStore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.Page;
import org.guiceside.commons.lang.DateFormatUtil;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.*;

/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class IndexBizImp extends BaseBiz implements IndexBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String index() throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            System.out.println(IndexPoint.getInstance().getIndex() + "##");
            resultObj.put("index", IndexPoint.getInstance().getIndex());
            resultObj.put("result", "0");
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String historyIndex() throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            CalSampleSpotDailyPointStore calSampleSpotDailyPointStore = hsfServiceFactory.consumer(CalSampleSpotDailyPointStore.class);
            if (calSampleSpotDailyPointStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$order("id", false));
                Page<CalSampleSpotDailyPoint> calSampleSpotDailyPointPage = calSampleSpotDailyPointStore.getPageList(0, 90, selectorList);
                if (calSampleSpotDailyPointPage != null) {
                    List<CalSampleSpotDailyPoint> sampleSpotDailyPointList = calSampleSpotDailyPointPage.getResultList();
                    if (sampleSpotDailyPointList != null && !sampleSpotDailyPointList.isEmpty()) {
                        JSONArray jsonArray = new JSONArray();
                        for (CalSampleSpotDailyPoint calSampleSpotDailyPoint : sampleSpotDailyPointList) {
                            JSONObject daily = new JSONObject();
                            daily.put("index", calSampleSpotDailyPoint.getPoint());
                            daily.put("time", calSampleSpotDailyPoint.getRecordDate().getTime());
                            jsonArray.add(daily);
                        }
                        resultObj.put("indexs", jsonArray);
                        resultObj.put("result", "0");
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String buildIndex(String releaseEnvironment) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", "-1");
        try {
            Calendar now = Calendar.getInstance();
            String dateTime = DateFormatUtil.format(now.getTime(), DateFormatUtil.YEAR_MONTH_DAY_PATTERN);
            now.add(Calendar.MONTH, -1);
            Integer month = now.get(Calendar.MONTH) + 1;
            Integer year = now.get(Calendar.YEAR);

            CalSampleSpotSymbolWeightStore calSampleSpotSymbolWeightStore = hsfServiceFactory.consumer(CalSampleSpotSymbolWeightStore.class);
            SysCapitalizationStore sysCapitalizationStore = hsfServiceFactory.consumer(SysCapitalizationStore.class);
            SpotDailyUsdtStore spotDailyUsdtStore = hsfServiceFactory.consumer(SpotDailyUsdtStore.class);
            CalSampleSpotPointStore calSampleSpotPointStore = hsfServiceFactory.consumer(CalSampleSpotPointStore.class);
            CalSampleSpotWeightHistoryStore calSampleSpotWeightHistoryStore = hsfServiceFactory.consumer(CalSampleSpotWeightHistoryStore.class);
            CalSampleSpotDailyPointStore calSampleSpotDailyPointStore = hsfServiceFactory.consumer(CalSampleSpotDailyPointStore.class);
            if (StringUtils.isNotBlank(dateTime) && calSampleSpotSymbolWeightStore != null
                    && sysCapitalizationStore != null && spotDailyUsdtStore != null && calSampleSpotDailyPointStore != null
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

                    //实时价格
                    List<Long> symbolIds = new ArrayList<>();
                    for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                        symbolIds.add(weight.getSymbolId().getId());
                        if (releaseEnvironment.equals("DIS")) {//from redis
                            Double price = Utils.getWeightSymbolPrice(weight);
                            if (price != 0d) {
                                todayLastPriceMap.put(weight.getSymbolId().getId(), price);
                            }
                        } else {
                            todayLastPriceMap.put(weight.getSymbolId().getId(), weight.getLastPrice());
                        }
                    }

                    Date today = c.getTime();

                    //yesterday 收盘价
                    c.add(Calendar.DAY_OF_YEAR, -1);
                    Date yesterdayDate = c.getTime();
                    if (releaseEnvironment.equals("DIS")) {//from redis
                        for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                            Double closePrice = Utils.getDailySymbolPrice(weight.getSymbolId(),c.getTime());
                            if (closePrice != 0d) {
                                yesterdayDailyMap.put(weight.getSymbolId().getId(), closePrice);
                            }
                        }
                    } else {
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
                    }

                    // 昨日指数
                    CalSampleSpotDailyPoint calSampleSpotDailyPoint = null;
                    if (releaseEnvironment.equals("DIS")) {//from redis
                        Double point = Utils.getPointByDate(yesterdayDate);
                        if (point != 0d) {
                            yesterdayPoint = point;
                        }else{
                            calSampleSpotDailyPoint = calSampleSpotDailyPointStore.getByRecordDate(yesterdayDate);
                            if (calSampleSpotDailyPoint != null) {
                                yesterdayPoint = calSampleSpotDailyPoint.getPoint();
                                Utils.setPointByDate(yesterdayDate,point);
                            }
                        }
                    } else {
                        calSampleSpotDailyPoint = calSampleSpotDailyPointStore.getByRecordDate(yesterdayDate);
                        if (calSampleSpotDailyPoint != null) {
                            yesterdayPoint = calSampleSpotDailyPoint.getPoint();
                        }
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
                        if (releaseEnvironment.equals("DIS")) {//from redis
                            for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                                Double beforeYesterdayClosePice = Utils.getDailySymbolPrice(weight.getSymbolId(), c.getTime());
                                if (beforeYesterdayClosePice != 0d) {
                                    dayBeforeYesterdayDailyMap.put(weight.getSymbolId().getId(), beforeYesterdayClosePice);
                                }
                            }
                        } else {
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
                        }

                        //获取昨日开市权重
                        if (releaseEnvironment.equals("DIS")) {//from redis
                            for (CalSampleSpotSymbolWeight weight : calSampleSpotSymbolWeightList) {
                                Double _d = Utils.getSymbolWeight(weight.getSymbolId(), yesterdayDate);
                                if (_d != 0d) {
                                    yesterdayWeightMap.put(weight.getSymbolId().getId(), _d);
                                }
                            }

                        } else {
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
                                throw new Exception(" -----price was null ------ !");
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
                            Utils.setSymbolWeight(weight.getSymbolId(), today, todayOpenWeightMap.get(weight.getSymbolId().getId()));//设置今日开市权重
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
                        if (releaseEnvironment.equals("DIS")) {
                            Utils.setPointByDate(today, currentPoint);//设置今日点数
                            IndexPoint.getInstance().setIndex(point.getPoint());
                        }
                        resultObj.put("result", "0");
                    } else {
                        System.out.println("yesterday point was null and date is " + DateFormatUtil.format(today, DateFormatUtil.YEAR_MONTH_DAY_PATTERN_SHORT));
                    }
                } else {
                    System.out.println("---calSampleSpotSymbolWeightList was null");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }
}
