package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdtBitmex;
import com.mobius.providers.store.futures.FuturesDetailUsdtBitmexStore;
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
public class FuturesDetailUsdtBitmexService extends HQuery implements FuturesDetailUsdtBitmexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailUsdtBitmex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailUsdtBitmex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailUsdtBitmex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailUsdtBitmex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailUsdtBitmex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex, Persistent persistent) throws StoreException {
        $(futuresDetailUsdtBitmex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailUsdtBitmex> futuresDetailUsdtBitmexList, Persistent persistent) throws StoreException {
        $(futuresDetailUsdtBitmexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex) throws StoreException {
        $(futuresDetailUsdtBitmex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailUsdtBitmex.class);
    }
}
