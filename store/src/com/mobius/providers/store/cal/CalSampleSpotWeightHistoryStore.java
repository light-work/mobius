package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotWeightHistory;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;


public interface CalSampleSpotWeightHistoryStore {

    CalSampleSpotWeightHistory getById(Long id, Selector... selectors) throws StoreException;

    CalSampleSpotWeightHistory getBySymbolIdDate(Long symbolId, Date recordDate) throws StoreException;

    List<CalSampleSpotWeightHistory> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotWeightHistory calSampleSpotWeightHistory, Persistent persistent) throws StoreException;

    void save(List<CalSampleSpotWeightHistory> calSampleSpotWeightHistoryList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotWeightHistory calSampleSpotWeightHistory) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
