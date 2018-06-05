package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbol;
import com.mobius.providers.store.cal.CalSampleSpotSymbolStore;
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
public class CalSampleSpotSymbolService extends HQuery implements CalSampleSpotSymbolStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotSymbol getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotSymbol.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbol> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotSymbol.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotSymbol getBySymbolIdYearMonth(Long symbolId, Integer year, Integer month) throws StoreException {
        return $($eq("symbolId.id", symbolId), $eq("year", year), $eq("month", month)).get(CalSampleSpotSymbol.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbol> getListByYearMonth(Integer year, Integer month) throws StoreException {
        return $($eq("year", year), $eq("month", month)).list(CalSampleSpotSymbol.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotSymbol calSampleSpotSymbol, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbol).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotSymbol> calSampleSpotSymbolList, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbolList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotSymbol calSampleSpotSymbol) throws StoreException {
        $(calSampleSpotSymbol).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotSymbol.class);
    }
}
