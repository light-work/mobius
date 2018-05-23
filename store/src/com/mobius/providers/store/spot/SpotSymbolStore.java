package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotSymbol;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SpotSymbolStore {

    SpotSymbol getById(Long id, Selector... selectors) throws StoreException;

    List<SpotSymbol> getListByTradeMarket(Long tradeId, String market) throws StoreException;

    List<SpotSymbol> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotSymbol spotSymbol, Persistent persistent) throws StoreException;

    void save(List<SpotSymbol> spotSymbolList, Persistent persistent) throws StoreException;

    void delete(SpotSymbol spotSymbol) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
