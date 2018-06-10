package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import com.mobius.providers.store.cal.CalSampleSpotDailyPointStore;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotDailyPointService extends HQuery implements CalSampleSpotDailyPointStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotDailyPoint getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotDailyPoint.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotDailyPoint getByRecordDate(Date recordDate) throws StoreException {
        return $($eq("recordDate",recordDate)).get(CalSampleSpotDailyPoint.class);
    }

    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotDailyPoint> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotDailyPoint.class);
    }


    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotDailyPoint calSampleSpotDailyPoint, Persistent persistent) throws StoreException {
        $(calSampleSpotDailyPoint).save(persistent);
    }



    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotDailyPoint> calSampleSpotDailyPointList, Persistent persistent) throws StoreException {
        $(calSampleSpotDailyPointList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotDailyPoint calSampleSpotDailyPoint) throws StoreException {
        $(calSampleSpotDailyPoint).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotDailyPoint.class);
    }
}
