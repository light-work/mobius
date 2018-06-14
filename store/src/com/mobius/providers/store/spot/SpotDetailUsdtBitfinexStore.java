package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtBitfinex;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailUsdtBitfinexStore {

    SpotDetailUsdtBitfinex getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailUsdtBitfinex> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailUsdtBitfinex spotDetailUsdtBitfinex, Persistent persistent,
              CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException;

    void save(List<SpotDetailUsdtBitfinex> spotDetailUsdtBitfinexList, Persistent persistent) throws StoreException;

    void delete(SpotDetailUsdtBitfinex spotDetailUsdtBitfinex) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
