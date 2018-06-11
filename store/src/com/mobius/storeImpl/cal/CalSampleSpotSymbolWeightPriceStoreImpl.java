package com.mobius.storeImpl.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightPriceStore;
import com.mobius.service.cal.CalSampleSpotSymbolWeightPriceService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotSymbolWeightPriceStoreImpl implements CalSampleSpotSymbolWeightPriceStore {

    @Inject
    private CalSampleSpotSymbolWeightPriceService calSampleCoinService;

    @Override
    @ConnectManager
    public CalSampleSpotSymbolWeightPrice getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.calSampleCoinService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }



    @Override
    @ConnectManager
    public List<CalSampleSpotSymbolWeightPrice> getList(List<Selector> list) throws StoreException {
        try {
            return this.calSampleCoinService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(calSampleSpotSymbolWeightPrice, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<CalSampleSpotSymbolWeightPrice> list, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        try {
            this.calSampleCoinService.delete(calSampleSpotSymbolWeightPrice);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.calSampleCoinService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
