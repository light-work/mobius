package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdt;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface FuturesDetailUsdtStore {

    FuturesDetailUsdt getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<FuturesDetailUsdt> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDetailUsdt futuresDetailUsdt, Persistent persistent) throws StoreException;

    void save(List<FuturesDetailUsdt> futuresDetailUsdtList, Persistent persistent) throws StoreException;

    void delete(FuturesDetailUsdt futuresDetailUsdt) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
