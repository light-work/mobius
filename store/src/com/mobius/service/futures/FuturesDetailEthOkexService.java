package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailEthOkex;
import com.mobius.providers.store.futures.FuturesDetailEthOkexStore;
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
public class FuturesDetailEthOkexService extends HQuery implements FuturesDetailEthOkexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailEthOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailEthOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailEthOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailEthOkex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailEthOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailEthOkex futuresDetailEthOkex, Persistent persistent) throws StoreException {
        $(futuresDetailEthOkex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailEthOkex> futuresDetailEthOkexList, Persistent persistent) throws StoreException {
        $(futuresDetailEthOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailEthOkex futuresDetailEthOkex) throws StoreException {
        $(futuresDetailEthOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailEthOkex.class);
    }
}
