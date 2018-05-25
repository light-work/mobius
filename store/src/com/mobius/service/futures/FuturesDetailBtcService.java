package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailBtc;
import com.mobius.providers.store.futures.FuturesDetailBtcStore;
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
public class FuturesDetailBtcService extends HQuery implements FuturesDetailBtcStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesDetailBtc getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesDetailBtc.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesDetailBtc> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesDetailBtc.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(FuturesDetailBtc.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesDetailBtc futuresDetailBtc, Persistent persistent) throws StoreException {
        $(futuresDetailBtc).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesDetailBtc> futuresDetailBtcList, Persistent persistent) throws StoreException {
        $(futuresDetailBtcList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesDetailBtc futuresDetailBtc) throws StoreException {
        $(futuresDetailBtc).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesDetailBtc.class);
    }
}
