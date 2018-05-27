package com.mobius.providers.store.sys;


import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCoin;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SysCoinStore {

    SysCoin getById(Long id, Selector... selectors) throws StoreException;

    SysCoin getBySymbol(String symbol) throws StoreException;

    List<SysCoin> getList(List<Selector> selectorList) throws StoreException;

    void save(SysCoin sysCoin, Persistent persistent) throws StoreException;

    void save(List<SysCoin> sysCoinList, Persistent persistent) throws StoreException;

    void delete(SysCoin sysCoin) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
