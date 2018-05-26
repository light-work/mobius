package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailEthOkex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailEthOkexStore {

    FuturesDetailEthOkex getById(Long id, Selector... selectors) throws StoreException;


    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailEthOkex> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailEthOkex futuresDetailEthOkex, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailEthOkex> futuresDetailEthOkexList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailEthOkex futuresDetailEthOkex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
