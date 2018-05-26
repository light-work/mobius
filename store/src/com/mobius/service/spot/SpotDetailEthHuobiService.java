package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthHuobi;
import com.mobius.providers.store.spot.SpotDetailEthHuobiStore;
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
public class SpotDetailEthHuobiService extends HQuery implements SpotDetailEthHuobiStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailEthHuobi getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailEthHuobi.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailEthHuobi> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailEthHuobi.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailEthHuobi.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailEthHuobi SpotDetailEthHuobi, Persistent persistent) throws StoreException {
        $(SpotDetailEthHuobi).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailEthHuobi> SpotDetailEthHuobiList, Persistent persistent) throws StoreException {
        $(SpotDetailEthHuobiList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailEthHuobi SpotDetailEthHuobi) throws StoreException {
        $(SpotDetailEthHuobi).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailEthHuobi.class);
    }
}
