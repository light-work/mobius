package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyBtc;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDailyBtcStore {

    SpotDailyBtc getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDailyBtc> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDailyBtc spotDailyBtc, Persistent persistent) throws StoreException;

    void save(List<SpotDailyBtc> spotDailyBtcList, Persistent persistent) throws StoreException;

    void delete(SpotDailyBtc spotDailyBtc) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
