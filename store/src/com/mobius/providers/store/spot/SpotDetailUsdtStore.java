package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailUsdt;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailUsdtStore {

    SpotDetailUsdt getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailUsdt> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailUsdt spotDetailUsdt, Persistent persistent) throws StoreException;

    void save(List<SpotDetailUsdt> spotDetailUsdtList, Persistent persistent) throws StoreException;

    void delete(SpotDetailUsdt spotDetailUsdt) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
