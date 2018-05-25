package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailEth;
import com.mobius.providers.store.futures.FuturesDetailEthStore;
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
public class FuturesDetailEthService extends HQuery implements FuturesDetailEthStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailEth getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailEth.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailEth> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailEth.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailEth.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailEth futuresDetailEth, Persistent persistent) throws StoreException {
        $(futuresDetailEth).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailEth> futuresDetailEthList, Persistent persistent) throws StoreException {
        $(futuresDetailEthList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailEth futuresDetailEth) throws StoreException {
        $(futuresDetailEth).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailEth.class);
    }
}
