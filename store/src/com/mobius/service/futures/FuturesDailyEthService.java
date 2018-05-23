package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyEth;
import com.mobius.providers.store.futures.FuturesDailyEthStore;
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
public class FuturesDailyEthService extends HQuery implements FuturesDailyEthStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDailyEth getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDailyEth.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDailyEth> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDailyEth.class);
    }



    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("symbolId.id",symbolId),
                $eq("tradingDay",tradingDay),$count("id")).value(FuturesDailyEth.class,Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDailyEth futuresDailyEth, Persistent persistent) throws StoreException {
        $(futuresDailyEth).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDailyEth> futuresDailyEthList, Persistent persistent) throws StoreException {
        $(futuresDailyEthList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDailyEth futuresDailyEth) throws StoreException {
        $(futuresDailyEth).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDailyEth.class);
    }
}
