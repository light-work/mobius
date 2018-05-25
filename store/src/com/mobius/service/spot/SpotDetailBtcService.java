package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtc;
import com.mobius.providers.store.spot.SpotDetailBtcStore;
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
public class SpotDetailBtcService extends HQuery implements SpotDetailBtcStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailBtc getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailBtc.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailBtc> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailBtc.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailBtc.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailBtc SpotDetailBtc, Persistent persistent) throws StoreException {
        $(SpotDetailBtc).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailBtc> SpotDetailBtcList, Persistent persistent) throws StoreException {
        $(SpotDetailBtcList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailBtc SpotDetailBtc) throws StoreException {
        $(SpotDetailBtc).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailBtc.class);
    }
}
