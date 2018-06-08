package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbol;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotSymbolStore {

    CalSampleSpotSymbol getById(Long id, Selector... selectors) throws StoreException;

    CalSampleSpotSymbol getBySymbolIdYearMonthUse(Long symbolId,Integer useYear, Integer useMonth, Integer year, Integer month) throws StoreException;

    List<CalSampleSpotSymbol> getListByYearMonthUse(Integer useYear, Integer useMonth) throws StoreException;

    List<CalSampleSpotSymbol> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotSymbol calSampleSpotSymbol, Persistent persistent) throws StoreException;

    void save(List<CalSampleSpotSymbol> calSampleSpotSymbolList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotSymbol calSampleSpotSymbol) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
