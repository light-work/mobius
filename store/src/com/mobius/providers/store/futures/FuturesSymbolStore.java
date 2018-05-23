package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesSymbol;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface FuturesSymbolStore {

    FuturesSymbol getById(Long id, Selector... selectors) throws StoreException;

    List<FuturesSymbol> getListByTradeMarket(Long tradeId,String market) throws StoreException;

    List<FuturesSymbol> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesSymbol futuresSymbol, Persistent persistent) throws StoreException;

    void save(List<FuturesSymbol> futuresSymbolList, Persistent persistent) throws StoreException;

    void delete(FuturesSymbol futuresSymbol) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
