package com.mobius.service.sys;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCoin;
import com.mobius.providers.store.sys.SysCoinStore;
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
public class SysCoinService extends HQuery implements SysCoinStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SysCoin getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SysCoin.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SysCoin> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SysCoin.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SysCoin SysCoin, Persistent persistent) throws StoreException {
        $(SysCoin).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SysCoin> SysCoinList, Persistent persistent) throws StoreException {
        $(SysCoinList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SysCoin SysCoin) throws StoreException {
        $(SysCoin).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SysCoin.class);
    }
}
