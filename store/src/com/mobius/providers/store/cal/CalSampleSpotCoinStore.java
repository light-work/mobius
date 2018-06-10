package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotCoin;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotCoinStore {

    CalSampleSpotCoin getById(Long id, Selector... selectors) throws StoreException;

    CalSampleSpotCoin getByCoinIdYearMonth(Long coinId, Integer year, Integer month) throws StoreException;

    List<CalSampleSpotCoin> getListByYearMonth(Integer year, Integer month) throws StoreException;

    List<CalSampleSpotCoin> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotCoin calSampleSpotCoin, Persistent persistent) throws StoreException;



    void save(List<CalSampleSpotCoin> calSampleSpotCoinList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotCoin calSampleSpotCoin) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
