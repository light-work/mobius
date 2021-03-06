package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcBitfinex;
import com.mobius.providers.store.spot.SpotDetailBtcBitfinexStore;
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
public class SpotDetailBtcBitfinexService extends HQuery implements SpotDetailBtcBitfinexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailBtcBitfinex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailBtcBitfinex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailBtcBitfinex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailBtcBitfinex.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailBtcBitfinex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailBtcBitfinex SpotDetailBtcBitfinex, Persistent persistent) throws StoreException {
        $(SpotDetailBtcBitfinex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailBtcBitfinex> SpotDetailBtcBitfinexList, Persistent persistent) throws StoreException {
        $(SpotDetailBtcBitfinexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailBtcBitfinex SpotDetailBtcBitfinex) throws StoreException {
        $(SpotDetailBtcBitfinex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailBtcBitfinex.class);
    }
}
