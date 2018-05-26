package com.mobius.providers.store.sys;


import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalization;
import org.guiceside.commons.Page;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SysCapitalizationStore {

    SysCapitalization getById(Long id, Selector... selectors) throws StoreException;


    Page<SysCapitalization> getPageList(int start,
                                        int limit, List<Selector> selectorList) throws StoreException;
    List<SysCapitalization> getList(List<Selector> selectorList) throws StoreException;

    void save(SysCapitalization sysCapitalization, Persistent persistent) throws StoreException;

    void save(List<SysCapitalization> sysCapitalizationList, Persistent persistent) throws StoreException;

    void delete(SysCapitalization sysCapitalization) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
