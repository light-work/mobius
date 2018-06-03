package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleCoin;
import com.mobius.providers.store.cal.CalSampleCoinStore;
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
public class CalSampleCoinService extends HQuery implements CalSampleCoinStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleCoin getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleCoin.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleCoin> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleCoin.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleCoin getByCoinTradeMarket(Long coinId, Integer year, Integer month) throws StoreException {
        return $($eq("coinId.id", coinId), $eq("year", year), $eq("month", month)).get(CalSampleCoin.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleCoin> getListByYearMonth(Integer year, Integer month) throws StoreException {
        return $($eq("year", year), $eq("month", month)).list(CalSampleCoin.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleCoin calSampleCoin, Persistent persistent) throws StoreException {
        $(calSampleCoin).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleCoin> calSampleCoinList, Persistent persistent) throws StoreException {
        $(calSampleCoinList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleCoin calSampleCoin) throws StoreException {
        $(calSampleCoin).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleCoin.class);
    }
}
