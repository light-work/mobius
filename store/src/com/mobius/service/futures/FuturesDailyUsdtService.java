package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class FuturesDailyUsdtService extends HQuery implements FuturesDailyUsdtStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDailyUsdt getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDailyUsdt.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDailyUsdt> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDailyUsdt.class);
    }



    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("symbolId.id",symbolId),
                $eq("tradingDay",tradingDay),$count("id")).value(FuturesDailyUsdt.class,Integer.class);
    }
    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDailyUsdt futuresDailyUsdt, Persistent persistent) throws StoreException {
        $(futuresDailyUsdt).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDailyUsdt> futuresDailyUsdtList, Persistent persistent) throws StoreException {
        $(futuresDailyUsdtList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDailyUsdt futuresDailyUsdt) throws StoreException {
        $(futuresDailyUsdt).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDailyUsdt.class);
    }
}
