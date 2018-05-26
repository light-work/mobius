package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthOkex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailEthOkexStore {

    SpotDetailEthOkex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailEthOkex> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailEthOkex spotDetailEthOkex, Persistent persistent) throws StoreException;

    void save(List<SpotDetailEthOkex> spotDetailEthOkexList, Persistent persistent) throws StoreException;

    void delete(SpotDetailEthOkex spotDetailEthOkex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
