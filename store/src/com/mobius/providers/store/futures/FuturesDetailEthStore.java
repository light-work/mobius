package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailEth;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailEthStore {

    FuturesDetailEth getById(Long id, Selector... selectors) throws StoreException;


    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailEth> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailEth futuresDetailEth, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailEth> futuresDetailEthList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailEth futuresDetailEth) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
