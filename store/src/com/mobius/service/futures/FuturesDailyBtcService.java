package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyBtc;
import com.mobius.providers.store.futures.FuturesDailyBtcStore;
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
public class FuturesDailyBtcService extends HQuery implements FuturesDailyBtcStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDailyBtc getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDailyBtc.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDailyBtc> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDailyBtc.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("symbolId.id",symbolId),
                $eq("tradingDay",tradingDay),$count("id")).value(FuturesDailyBtc.class,Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDailyBtc futuresDailyBtc, Persistent persistent) throws StoreException {
        $(futuresDailyBtc).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDailyBtc> futuresDailyBtcList, Persistent persistent) throws StoreException {
        $(futuresDailyBtcList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDailyBtc futuresDailyBtc) throws StoreException {
        $(futuresDailyBtc).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDailyBtc.class);
    }
}
