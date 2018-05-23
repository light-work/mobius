package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
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
public class SpotDailyBtcService extends HQuery implements SpotDailyBtcStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDailyBtc getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDailyBtc.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDailyBtc> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDailyBtc.class);
    }


    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDailyBtc SpotDailyBtc, Persistent persistent) throws StoreException {
        $(SpotDailyBtc).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDailyBtc> SpotDailyBtcList, Persistent persistent) throws StoreException {
        $(SpotDailyBtcList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDailyBtc SpotDailyBtc) throws StoreException {
        $(SpotDailyBtc).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDailyBtc.class);
    }
}
