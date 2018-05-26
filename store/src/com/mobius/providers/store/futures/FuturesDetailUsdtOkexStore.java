package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdtOkex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailUsdtOkexStore {

    FuturesDetailUsdtOkex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailUsdtOkex> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailUsdtOkex futuresDetailUsdtOkex, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailUsdtOkex> futuresDetailUsdtOkexList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailUsdtOkex futuresDetailUsdtOkex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
