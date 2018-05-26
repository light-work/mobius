package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDetailEthBinance;
import com.mobius.providers.store.spot.SpotDetailEthBinanceStore;
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
public class SpotDetailEthBinanceService extends HQuery implements SpotDetailEthBinanceStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailEthBinance getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailEthBinance.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailEthBinance> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailEthBinance.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailEthBinance.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailEthBinance SpotDetailEthBinance, Persistent persistent) throws StoreException {
        $(SpotDetailEthBinance).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailEthBinance> SpotDetailEthBinanceList, Persistent persistent) throws StoreException {
        $(SpotDetailEthBinanceList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailEthBinance SpotDetailEthBinance) throws StoreException {
        $(SpotDetailEthBinance).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailEthBinance.class);
    }
}
