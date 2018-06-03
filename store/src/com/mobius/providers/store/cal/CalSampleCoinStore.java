package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleCoin;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleCoinStore {

    CalSampleCoin getById(Long id, Selector... selectors) throws StoreException;

    CalSampleCoin getByCoinTradeMarket(Long coinId, Integer year, Integer month) throws StoreException;

    List<CalSampleCoin> getListByYearMonth(Integer year, Integer month) throws StoreException;

    List<CalSampleCoin> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleCoin calSampleCoin, Persistent persistent) throws StoreException;

    void save(List<CalSampleCoin> calSampleCoinList, Persistent persistent) throws StoreException;

    void delete(CalSampleCoin calSampleCoin) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
