package com.mobius.providers.store.cal;


import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import com.mobius.entity.cal.CalSampleSpotPoint;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface CalSampleSpotPointStore {

    CalSampleSpotPoint getById(Long id, Selector... selectors) throws StoreException;

    List<CalSampleSpotPoint> getList(List<Selector> selectorList) throws StoreException;

    void save(CalSampleSpotPoint calSampleSpotPoint, Persistent persistent) throws StoreException;


    void saveAndDaily(CalSampleSpotPoint calSampleSpotPoint, Persistent persistent,
                      CalSampleSpotDailyPoint calSampleSpotDailyPoint,Persistent dailyPersistent) throws StoreException;

    void save(List<CalSampleSpotPoint> calSampleSpotPointList, Persistent persistent) throws StoreException;

    void delete(CalSampleSpotPoint calSampleSpotPoint) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
