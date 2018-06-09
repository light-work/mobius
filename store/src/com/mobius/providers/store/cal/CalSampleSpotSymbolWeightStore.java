package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotSymbolWeightStore {

    CalSampleSpotSymbolWeight getById(Long id, Selector... selectors) throws StoreException;

    CalSampleSpotSymbolWeight getBySymbolIdYearMonth(Long symbolId, Integer year, Integer month) throws StoreException;

    List<CalSampleSpotSymbolWeight> getListByYearMonth(Integer year, Integer month) throws StoreException;

    List<CalSampleSpotSymbolWeight> getListByYearMonthTradeMarketServerNo(Integer year, Integer month,Long tradeId,String market,Integer serverNo) throws StoreException;

    List<CalSampleSpotSymbolWeight> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight, Persistent persistent) throws StoreException;

    void save(List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
