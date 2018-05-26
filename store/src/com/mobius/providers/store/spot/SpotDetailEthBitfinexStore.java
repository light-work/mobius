package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthBitfinex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailEthBitfinexStore {

    SpotDetailEthBitfinex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailEthBitfinex> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailEthBitfinex spotDetailEthBitfinex, Persistent persistent) throws StoreException;

    void save(List<SpotDetailEthBitfinex> spotDetailEthBitfinexList, Persistent persistent) throws StoreException;

    void delete(SpotDetailEthBitfinex spotDetailEthBitfinex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
