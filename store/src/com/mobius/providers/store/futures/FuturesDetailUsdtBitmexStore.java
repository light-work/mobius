package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdtBitmex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailUsdtBitmexStore {

    FuturesDetailUsdtBitmex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailUsdtBitmex> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailUsdtBitmex> futuresDetailUsdtBitmexList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
