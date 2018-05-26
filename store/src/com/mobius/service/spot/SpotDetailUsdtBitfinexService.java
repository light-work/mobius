package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailUsdtBitfinex;
import com.mobius.providers.store.spot.SpotDetailUsdtBitfinexStore;
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
public class SpotDetailUsdtBitfinexService extends HQuery implements SpotDetailUsdtBitfinexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailUsdtBitfinex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailUsdtBitfinex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailUsdtBitfinex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailUsdtBitfinex.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailUsdtBitfinex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailUsdtBitfinex SpotDetailUsdtBitfinex, Persistent persistent) throws StoreException {
        $(SpotDetailUsdtBitfinex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailUsdtBitfinex> SpotDetailUsdtBitfinexList, Persistent persistent) throws StoreException {
        $(SpotDetailUsdtBitfinexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailUsdtBitfinex SpotDetailUsdtBitfinex) throws StoreException {
        $(SpotDetailUsdtBitfinex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailUsdtBitfinex.class);
    }
}
