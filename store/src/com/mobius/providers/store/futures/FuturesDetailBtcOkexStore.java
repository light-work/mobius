package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailBtcOkex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailBtcOkexStore {

    FuturesDetailBtcOkex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailBtcOkex> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailBtcOkex futuresDetailBtcOkex, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailBtcOkex> futuresDetailBtcOkexList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailBtcOkex futuresDetailBtcOkex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
