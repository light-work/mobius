package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcHuobi;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailBtcHuobiStore {

    SpotDetailBtcHuobi getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailBtcHuobi> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailBtcHuobi spotDetailBtcHuobi, Persistent persistent) throws StoreException;

    void save(List<SpotDetailBtcHuobi> detailBtcHuobiList, Persistent persistent) throws StoreException;

    void delete(SpotDetailBtcHuobi spotDetailBtcHuobi) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
