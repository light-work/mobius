package com.mobius.providers.store.sys;


import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalizationLast;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SysCapitalizationLastStore {

    SysCapitalizationLast getById(Long id, Selector... selectors) throws StoreException;

    SysCapitalizationLast getByCoinId(Long coinId) throws StoreException;

    List<SysCapitalizationLast> getList(List<Selector> selectorList) throws StoreException;

    void save(SysCapitalizationLast sysCapitalizationLast, Persistent persistent) throws StoreException;

    void save(List<SysCapitalizationLast> sysCapitalizationLastList, Persistent persistent) throws StoreException;

    void delete(SysCapitalizationLast sysCapitalizationLast) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
