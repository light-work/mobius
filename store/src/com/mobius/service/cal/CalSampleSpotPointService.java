package com.mobius.service.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotDailyPoint;
import com.mobius.entity.cal.CalSampleSpotPoint;
import com.mobius.providers.store.cal.CalSampleSpotPointStore;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotPointService extends HQuery implements CalSampleSpotPointStore {

    @Inject
    private CalSampleSpotDailyPointService calSampleSpotDailyPointService;

    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotPoint getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotPoint.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotPoint> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotPoint.class);
    }


    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotPoint calSampleSpotPoint, Persistent persistent) throws StoreException {
        $(calSampleSpotPoint).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotPoint> calSampleSpotPointList, Persistent persistent) throws StoreException {
        $(calSampleSpotPointList).save(persistent);
    }


    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void saveAndDaily(CalSampleSpotPoint calSampleSpotPoint, Persistent persistent, CalSampleSpotDailyPoint calSampleSpotDailyPoint, Persistent dailyPersistent) throws StoreException {
        $(calSampleSpotPoint).save(persistent);
        if(calSampleSpotDailyPoint!=null){
            calSampleSpotDailyPointService.save(calSampleSpotDailyPoint,dailyPersistent);
        }
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotPoint calSampleSpotPoint) throws StoreException {
        $(calSampleSpotPoint).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotPoint.class);
    }
}
