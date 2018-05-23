package com.mobius.providers.store.sys;


import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysTrade;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SysTradeStore {

    SysTrade getById(Long id, Selector... selectors) throws StoreException;

    SysTrade getBySign(String sign) throws StoreException;

    List<SysTrade> getList(List<Selector> selectorList) throws StoreException;

    void save(SysTrade sysTrade, Persistent persistent) throws StoreException;

    void save(List<SysTrade> sysTradeList, Persistent persistent) throws StoreException;

    void delete(SysTrade sysTrade) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
