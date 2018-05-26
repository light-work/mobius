package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcHuobi;
import com.mobius.providers.store.spot.SpotDetailBtcHuobiStore;
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
public class SpotDetailBtcHuobiService extends HQuery implements SpotDetailBtcHuobiStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailBtcHuobi getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailBtcHuobi.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailBtcHuobi> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailBtcHuobi.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailBtcHuobi.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailBtcHuobi SpotDetailBtcHuobi, Persistent persistent) throws StoreException {
        $(SpotDetailBtcHuobi).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailBtcHuobi> SpotDetailBtcHuobiList, Persistent persistent) throws StoreException {
        $(SpotDetailBtcHuobiList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailBtcHuobi SpotDetailBtcHuobi) throws StoreException {
        $(SpotDetailBtcHuobi).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailBtcHuobi.class);
    }
}
