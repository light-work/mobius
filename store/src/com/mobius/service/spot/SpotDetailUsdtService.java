package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailUsdt;
import com.mobius.providers.store.spot.SpotDetailUsdtStore;
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
public class SpotDetailUsdtService extends HQuery implements SpotDetailUsdtStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailUsdt getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailUsdt.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailUsdt> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailUsdt.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailUsdt.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailUsdt SpotDetailUsdt, Persistent persistent) throws StoreException {
        $(SpotDetailUsdt).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailUsdt> SpotDetailUsdtList, Persistent persistent) throws StoreException {
        $(SpotDetailUsdtList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailUsdt SpotDetailUsdt) throws StoreException {
        $(SpotDetailUsdt).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailUsdt.class);
    }
}
