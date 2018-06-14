package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtBinance;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface SpotDetailUsdtBinanceStore {

    SpotDetailUsdtBinance getById(Long id, Selector... selectors) throws StoreException;

    Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException;


    List<SpotDetailUsdtBinance> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDetailUsdtBinance spotDetailUsdtBinance, Persistent persistent) throws StoreException;

    void save(SpotDetailUsdtBinance spotDetailUsdtBinance, Persistent persistent,  CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException;


    void save(List<SpotDetailUsdtBinance> spotDetailUsdtBinanceList, Persistent persistent) throws StoreException;

    void delete(SpotDetailUsdtBinance spotDetailUsdtBinance) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
