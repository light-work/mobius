package com.mobius.service.sys;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.providers.store.sys.SysCapitalizationStore;
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
public class SysCapitalizationService extends HQuery implements SysCapitalizationStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SysCapitalization getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SysCapitalization.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SysCapitalization> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SysCapitalization.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Page<SysCapitalization> getPageList(int start, int limit, List<Selector> selectorList) throws StoreException {
        return $(selectorList).page(SysCapitalization.class, start, limit);    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SysCapitalization SysCapitalization, Persistent persistent) throws StoreException {
        $(SysCapitalization).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SysCapitalization> SysCapitalizationList, Persistent persistent) throws StoreException {
        $(SysCapitalizationList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SysCapitalization SysCapitalization) throws StoreException {
        $(SysCapitalization).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SysCapitalization.class);
    }
}
