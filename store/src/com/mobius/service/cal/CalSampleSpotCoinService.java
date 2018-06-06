package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotCoin;
import com.mobius.providers.store.cal.CalSampleSpotCoinStore;
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
public class CalSampleSpotCoinService extends HQuery implements CalSampleSpotCoinStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotCoin getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotCoin.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotCoin> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotCoin.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotCoin getByCoinIdYearMonth(Long coinId, Integer year, Integer month) throws StoreException {
        return $($eq("coinId.id", coinId), $eq("year", year), $eq("month", month)).get(CalSampleSpotCoin.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotCoin> getListByYearMonth(Integer year, Integer month) throws StoreException {
        return $($eq("year", year), $eq("month", month)).list(CalSampleSpotCoin.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotCoin calSampleSpotCoin, Persistent persistent) throws StoreException {
        $(calSampleSpotCoin).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotCoin> calSampleSpotCoinList, Persistent persistent) throws StoreException {
        $(calSampleSpotCoinList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotCoin calSampleSpotCoin) throws StoreException {
        $(calSampleSpotCoin).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotCoin.class);
    }
}
