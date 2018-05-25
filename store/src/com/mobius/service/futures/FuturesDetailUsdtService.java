package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdt;
import com.mobius.providers.store.futures.FuturesDetailUsdtStore;
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
public class FuturesDetailUsdtService extends HQuery implements FuturesDetailUsdtStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailUsdt getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailUsdt.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailUsdt> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailUsdt.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailUsdt.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailUsdt futuresDetailUsdt, Persistent persistent) throws StoreException {
        $(futuresDetailUsdt).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailUsdt> futuresDetailUsdtList, Persistent persistent) throws StoreException {
        $(futuresDetailUsdtList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailUsdt futuresDetailUsdt) throws StoreException {
        $(futuresDetailUsdt).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailUsdt.class);
    }
}
