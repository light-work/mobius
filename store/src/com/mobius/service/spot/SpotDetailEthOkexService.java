package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthOkex;
import com.mobius.providers.store.spot.SpotDetailEthOkexStore;
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
public class SpotDetailEthOkexService extends HQuery implements SpotDetailEthOkexStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailEthOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailEthOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailEthOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailEthOkex.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailEthOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailEthOkex SpotDetailEthOkex, Persistent persistent) throws StoreException {
        $(SpotDetailEthOkex).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailEthOkex> SpotDetailEthOkexList, Persistent persistent) throws StoreException {
        $(SpotDetailEthOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailEthOkex SpotDetailEthOkex) throws StoreException {
        $(SpotDetailEthOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailEthOkex.class);
    }
}
