package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyEth;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDailyEthStore {

    FuturesDailyEth getById(Long id, Selector... selectors) throws StoreException;


    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDailyEth> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDailyEth futuresDailyEth, Persistent persistent) throws StoreException;

    void save(List<FuturesDailyEth> futuresDailyEthList, Persistent persistent) throws StoreException;

    void delete(FuturesDailyEth futuresDailyEth) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
