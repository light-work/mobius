package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcBitfinex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailBtcBitfinexStore {

    SpotDetailBtcBitfinex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailBtcBitfinex> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailBtcBitfinex spotDetailBtcBitfinex, Persistent persistent) throws StoreException;

    void save(List<SpotDetailBtcBitfinex> detailBtcBitfinexList, Persistent persistent) throws StoreException;

    void delete(SpotDetailBtcBitfinex spotDetailBtcBitfinex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
