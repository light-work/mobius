package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEth;
import com.mobius.providers.store.spot.SpotDetailEthStore;
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
public class SpotDetailEthService extends HQuery implements SpotDetailEthStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailEth getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailEth.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailEth> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailEth.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailEth.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailEth SpotDetailEth, Persistent persistent) throws StoreException {
        $(SpotDetailEth).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailEth> SpotDetailEthList, Persistent persistent) throws StoreException {
        $(SpotDetailEthList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailEth SpotDetailEth) throws StoreException {
        $(SpotDetailEth).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailEth.class);
    }
}
