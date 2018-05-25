package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtc;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailBtcStore {

    SpotDetailBtc getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailBtc> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailBtc spotDetailBtc, Persistent persistent) throws StoreException;

    void save(List<SpotDetailBtc> detailBtcList, Persistent persistent) throws StoreException;

    void delete(SpotDetailBtc spotDetailBtc) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
