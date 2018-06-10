package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotDailyPointStore {

    CalSampleSpotDailyPoint getById(Long id, Selector... selectors) throws StoreException;

    List<CalSampleSpotDailyPoint> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotDailyPoint calSampleSpotDailyPoint, Persistent persistent) throws StoreException;

    void save(List<CalSampleSpotDailyPoint> calSampleSpotDailyPointList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotDailyPoint calSampleSpotDailyPoint) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
