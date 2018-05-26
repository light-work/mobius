package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcBinance;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailBtcBinanceStore {

    SpotDetailBtcBinance getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailBtcBinance> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailBtcBinance spotDetailBtcBinance, Persistent persistent) throws StoreException;

    void save(List<SpotDetailBtcBinance> detailBtcBinanceList, Persistent persistent) throws StoreException;

    void delete(SpotDetailBtcBinance spotDetailBtcBinance) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
