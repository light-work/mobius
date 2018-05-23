package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyUsdt;
import com.mobius.providers.store.spot.SpotDailyUsdtStore;
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
public class SpotDailyUsdtService extends HQuery implements SpotDailyUsdtStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDailyUsdt getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDailyUsdt.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDailyUsdt> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDailyUsdt.class);
    }


    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDailyUsdt SpotDailyUsdt, Persistent persistent) throws StoreException {
        $(SpotDailyUsdt).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDailyUsdt> SpotDailyUsdtList, Persistent persistent) throws StoreException {
        $(SpotDailyUsdtList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDailyUsdt SpotDailyUsdt) throws StoreException {
        $(SpotDailyUsdt).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDailyUsdt.class);
    }
}
