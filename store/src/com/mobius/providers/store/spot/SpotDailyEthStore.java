package com.mobius.providers.store.spot;


import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyEth;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SpotDailyEthStore {

    SpotDailyEth getById(Long id, Selector... selectors) throws StoreException;


    List<SpotDailyEth> getList(List<Selector> selectorList) throws StoreException;

    void save(SpotDailyEth spotDailyEth, Persistent persistent) throws StoreException;

    void save(List<SpotDailyEth> spotDailyEthList, Persistent persistent) throws StoreException;

    void delete(SpotDailyEth spotDailyEth) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
