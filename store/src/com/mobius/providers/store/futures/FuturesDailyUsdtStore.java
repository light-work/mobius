package com.mobius.providers.store.futures;


import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyUsdt;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface FuturesDailyUsdtStore {

    FuturesDailyUsdt getById(Long id, Selector... selectors) throws StoreException;


    List<FuturesDailyUsdt> getList(List<Selector> selectorList) throws StoreException;

    void save(FuturesDailyUsdt futuresDailyUsdt, Persistent persistent) throws StoreException;

    void save(List<FuturesDailyUsdt> futuresDailyUsdtList, Persistent persistent) throws StoreException;

    void delete(FuturesDailyUsdt futuresDailyUsdt) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
