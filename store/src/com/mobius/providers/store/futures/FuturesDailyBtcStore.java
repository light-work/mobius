package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyBtc;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDailyBtcStore {

    FuturesDailyBtc getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDailyBtc> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDailyBtc futuresDailyBtc, Persistent persistent) throws StoreException;

    void save(List<FuturesDailyBtc> futuresDailyBtcList, Persistent persistent) throws StoreException;

    void delete(FuturesDailyBtc futuresDailyBtc) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
