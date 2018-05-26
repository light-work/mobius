package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthHuobi;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailEthHuobiStore {

    SpotDetailEthHuobi getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailEthHuobi> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailEthHuobi spotDetailEthHuobi, Persistent persistent) throws StoreException;

    void save(List<SpotDetailEthHuobi> spotDetailEthHuobiList, Persistent persistent) throws StoreException;

    void delete(SpotDetailEthHuobi spotDetailEthHuobi) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
