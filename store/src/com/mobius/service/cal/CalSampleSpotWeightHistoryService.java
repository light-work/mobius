package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotWeightHistory;
import com.mobius.providers.store.cal.CalSampleSpotWeightHistoryStore;
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
public class CalSampleSpotWeightHistoryService extends HQuery implements CalSampleSpotWeightHistoryStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotWeightHistory getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotWeightHistory.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotWeightHistory> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotWeightHistory.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotWeightHistory getBySymbolIdDate(Long symbolId, Date recordDate) throws StoreException {
        return $($eq("symbolId.id", symbolId), $eq("recordDate", recordDate)).get(CalSampleSpotWeightHistory.class);
    }


    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotWeightHistory calSampleSpotWeightHistory, Persistent persistent) throws StoreException {
        $(calSampleSpotWeightHistory).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotWeightHistory> calSampleSpotWeightHistoryList, Persistent persistent) throws StoreException {
        $(calSampleSpotWeightHistoryList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotWeightHistory calSampleSpotWeightHistory) throws StoreException {
        $(calSampleSpotWeightHistory).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotWeightHistory.class);
    }
}
