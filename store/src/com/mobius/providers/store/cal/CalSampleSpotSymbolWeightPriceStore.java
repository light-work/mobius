package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotSymbolWeightPriceStore {

    CalSampleSpotSymbolWeightPrice getById(Long id, Selector... selectors) throws StoreException;

    List<CalSampleSpotSymbolWeightPrice> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice, Persistent persistent) throws StoreException;

    void save(List<CalSampleSpotSymbolWeightPrice> calSampleSpotSymbolWeightPriceList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
