package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotDetailUsdtOkex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailUsdtOkexStore {

    SpotDetailUsdtOkex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailUsdtOkex> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailUsdtOkex spotDetailUsdtOkex, Persistent persistent,CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) throws StoreException;

    void save(List<SpotDetailUsdtOkex> spotDetailUsdtOkexList, Persistent persistent) throws StoreException;

    void delete(SpotDetailUsdtOkex spotDetailUsdtOkex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
