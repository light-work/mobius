package com.mobius.service.cal;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightPriceStore;
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
public class CalSampleSpotSymbolWeightPriceService extends HQuery implements CalSampleSpotSymbolWeightPriceStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public CalSampleSpotSymbolWeightPrice getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(CalSampleSpotSymbolWeightPrice.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<CalSampleSpotSymbolWeightPrice> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(CalSampleSpotSymbolWeightPrice.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbolWeightPrice).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<CalSampleSpotSymbolWeightPrice> calSampleSpotSymbolWeightPriceList, Persistent persistent) throws StoreException {
        $(calSampleSpotSymbolWeightPriceList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        $(calSampleSpotSymbolWeightPrice).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(CalSampleSpotSymbolWeightPrice.class);
    }
}
