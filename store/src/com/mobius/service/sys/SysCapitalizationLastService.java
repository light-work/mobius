package com.mobius.service.sys;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalizationLast;
import com.mobius.providers.store.sys.SysCapitalizationLastStore;
import org.guiceside.commons.Page;
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
public class SysCapitalizationLastService extends HQuery implements SysCapitalizationLastStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SysCapitalizationLast getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SysCapitalizationLast.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SysCapitalizationLast> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SysCapitalizationLast.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public SysCapitalizationLast getByCoinId(Long coinId) throws StoreException {
        return $($eq("coinId.id", coinId)).get(SysCapitalizationLast.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SysCapitalizationLast SysCapitalizationLast, Persistent persistent) throws StoreException {
        $(SysCapitalizationLast).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SysCapitalizationLast> SysCapitalizationLastList, Persistent persistent) throws StoreException {
        $(SysCapitalizationLastList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SysCapitalizationLast SysCapitalizationLast) throws StoreException {
        $(SysCapitalizationLast).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SysCapitalizationLast.class);
    }
}
