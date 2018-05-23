package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyUsdt;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SpotDailyUsdtStore {

    SpotDailyUsdt getById(Long id, Selector... selectors) throws StoreException;


    List<SpotDailyUsdt> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDailyUsdt spotDailyUsdt, Persistent persistent) throws StoreException;

    void save(List<SpotDailyUsdt> spotDailyUsdtList, Persistent persistent) throws StoreException;

    void delete(SpotDailyUsdt spotDailyUsdt) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
