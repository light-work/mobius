package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthBitfinex;
import com.mobius.providers.store.spot.SpotDetailEthBitfinexStore;
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
public class SpotDetailEthBitfinexService extends HQuery implements SpotDetailEthBitfinexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailEthBitfinex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailEthBitfinex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailEthBitfinex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailEthBitfinex.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailEthBitfinex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailEthBitfinex SpotDetailEthBitfinex, Persistent persistent) throws StoreException {
        $(SpotDetailEthBitfinex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailEthBitfinex> SpotDetailEthBitfinexList, Persistent persistent) throws StoreException {
        $(SpotDetailEthBitfinexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailEthBitfinex SpotDetailEthBitfinex) throws StoreException {
        $(SpotDetailEthBitfinex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailEthBitfinex.class);
    }
}
