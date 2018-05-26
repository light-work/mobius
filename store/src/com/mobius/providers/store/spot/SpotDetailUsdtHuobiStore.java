package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailUsdtHuobi;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailUsdtHuobiStore {

    SpotDetailUsdtHuobi getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailUsdtHuobi> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailUsdtHuobi spotDetailUsdtHuobi, Persistent persistent) throws StoreException;

    void save(List<SpotDetailUsdtHuobi> spotDetailUsdtHuobiList, Persistent persistent) throws StoreException;

    void delete(SpotDetailUsdtHuobi spotDetailUsdtHuobi) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
