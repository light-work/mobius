package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailBtc;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailBtcStore {

    FuturesDetailBtc getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailBtc> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailBtc futuresDetailBtc, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailBtc> futuresDetailBtcList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailBtc futuresDetailBtc) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
