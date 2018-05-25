package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEth;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailEthStore {

    SpotDetailEth getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailEth> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailEth spotDetailEth, Persistent persistent) throws StoreException;

    void save(List<SpotDetailEth> spotDetailEthList, Persistent persistent) throws StoreException;

    void delete(SpotDetailEth spotDetailEth) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
