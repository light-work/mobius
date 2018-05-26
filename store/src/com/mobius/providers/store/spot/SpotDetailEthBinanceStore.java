package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthBinance;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailEthBinanceStore {

    SpotDetailEthBinance getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailEthBinance> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailEthBinance spotDetailEthBinance, Persistent persistent) throws StoreException;

    void save(List<SpotDetailEthBinance> spotDetailEthBinanceList, Persistent persistent) throws StoreException;

    void delete(SpotDetailEthBinance spotDetailEthBinance) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
