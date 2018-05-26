package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdtOkex;
import com.mobius.providers.store.futures.FuturesDetailUsdtOkexStore;
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
public class FuturesDetailUsdtOkexService extends HQuery implements FuturesDetailUsdtOkexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailUsdtOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailUsdtOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailUsdtOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailUsdtOkex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailUsdtOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailUsdtOkex futuresDetailUsdtOkex, Persistent persistent) throws StoreException {
        $(futuresDetailUsdtOkex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailUsdtOkex> futuresDetailUsdtOkexList, Persistent persistent) throws StoreException {
        $(futuresDetailUsdtOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailUsdtOkex futuresDetailUsdtOkex) throws StoreException {
        $(futuresDetailUsdtOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailUsdtOkex.class);
    }
}
