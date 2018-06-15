package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.providers.store.spot.SpotDailyEthStore;
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
public class SpotDailyEthService extends HQuery implements SpotDailyEthStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDailyEth getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDailyEth.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDailyEth> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDailyEth.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("symbolId.id",symbolId),
                $eq("tradingDay",tradingDay),$count("id")).value(SpotDailyEth.class,Integer.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDailyEth getTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("symbolId.id",symbolId),
                $eq("tradingDay",tradingDay)).get(SpotDailyEth.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDailyEth SpotDailyEth, Persistent persistent) throws StoreException {
        $(SpotDailyEth).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDailyEth> SpotDailyEthList, Persistent persistent) throws StoreException {
        $(SpotDailyEthList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDailyEth SpotDailyEth) throws StoreException {
        $(SpotDailyEth).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDailyEth.class);
    }
}
