package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailBtcBinance;
import com.mobius.providers.store.spot.SpotDetailBtcBinanceStore;
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
public class SpotDetailBtcBinanceService extends HQuery implements SpotDetailBtcBinanceStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailBtcBinance getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailBtcBinance.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailBtcBinance> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailBtcBinance.class);
    }


    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailBtcBinance.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailBtcBinance SpotDetailBtcBinance, Persistent persistent) throws StoreException {
        $(SpotDetailBtcBinance).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailBtcBinance> SpotDetailBtcBinanceList, Persistent persistent) throws StoreException {
        $(SpotDetailBtcBinanceList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailBtcBinance SpotDetailBtcBinance) throws StoreException {
        $(SpotDetailBtcBinance).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailBtcBinance.class);
    }
}
