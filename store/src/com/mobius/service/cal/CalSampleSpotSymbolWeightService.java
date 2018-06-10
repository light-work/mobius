package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotSymbolWeightService extends HQuery implements CalSampleSpotSymbolWeightStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotSymbolWeight getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotSymbolWeight.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbolWeight> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotSymbolWeight.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotSymbolWeight getBySymbolIdYearMonth(Long symbolId, Integer year, Integer month) throws StoreException {
        return $($alias("symbolId","symbolId"),$eq("symbolId.id", symbolId), $eq("year", year), $eq("month", month)).get(CalSampleSpotSymbolWeight.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbolWeight> getListByYearMonth(Integer year, Integer month) throws StoreException {
        return $($alias("symbolId","symbolId"),$eq("year", year), $eq("month", month)).list(CalSampleSpotSymbolWeight.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbolWeight> getListByYearMonthTradeMarketServerNo(Integer year, Integer month,Long tradeId,String market, Integer serverNo) throws StoreException {
        return $($alias("symbolId","symbolId"),$alias("symbolId.tradeId","tradeId"),$eq("tradeId.id",tradeId),
                $eq("symbolId.market",market),$eq("symbolId.server",serverNo),$eq("year", year), $eq("month", month)).list(CalSampleSpotSymbolWeight.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbolWeight).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotSymbolWeight> calSampleSpotSymbolWeightList, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbolWeightList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) throws StoreException {
        $(calSampleSpotSymbolWeight).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotSymbolWeight.class);
    }
}
