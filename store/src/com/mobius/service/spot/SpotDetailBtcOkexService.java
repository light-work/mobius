package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcOkex;
import com.mobius.providers.store.spot.SpotDetailBtcOkexStore;
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
public class SpotDetailBtcOkexService extends HQuery implements SpotDetailBtcOkexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailBtcOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailBtcOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailBtcOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailBtcOkex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailBtcOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailBtcOkex SpotDetailBtcOkex, Persistent persistent) throws StoreException {
        $(SpotDetailBtcOkex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailBtcOkex> SpotDetailBtcOkexList, Persistent persistent) throws StoreException {
        $(SpotDetailBtcOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailBtcOkex SpotDetailBtcOkex) throws StoreException {
        $(SpotDetailBtcOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailBtcOkex.class);
    }
}
