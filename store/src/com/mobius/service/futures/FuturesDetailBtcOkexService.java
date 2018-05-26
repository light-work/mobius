package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailBtcOkex;
import com.mobius.providers.store.futures.FuturesDetailBtcOkexStore;
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
public class FuturesDetailBtcOkexService extends HQuery implements FuturesDetailBtcOkexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailBtcOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailBtcOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailBtcOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailBtcOkex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailBtcOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailBtcOkex futuresDetailBtcOkex, Persistent persistent) throws StoreException {
        $(futuresDetailBtcOkex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailBtcOkex> futuresDetailBtcOkexList, Persistent persistent) throws StoreException {
        $(futuresDetailBtcOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailBtcOkex futuresDetailBtcOkex) throws StoreException {
        $(futuresDetailBtcOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailBtcOkex.class);
    }
}
