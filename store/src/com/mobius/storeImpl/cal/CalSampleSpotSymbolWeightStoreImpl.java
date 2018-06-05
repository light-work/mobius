package com.mobius.storeImpl.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.providers.store.cal.CalSampleSpotSymbolWeightStore;
import com.mobius.service.cal.CalSampleSpotSymbolWeightService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotSymbolWeightStoreImpl implements CalSampleSpotSymbolWeightStore {

    @Inject
    private CalSampleSpotSymbolWeightService calSampleCoinService;

    @Override
    @ConnectManager
    public CalSampleSpotSymbolWeight getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.calSampleCoinService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public CalSampleSpotSymbolWeight getBySymbolIdYearMonth(Long symbolId, Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getBySymbolIdYearMonth(symbolId, year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotSymbolWeight> getListByYearMonth(Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getListByYearMonth(year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotSymbolWeight> getList(List<Selector> list) throws StoreException {
        try {
            return this.calSampleCoinService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(calSampleSpotSymbolWeight, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<CalSampleSpotSymbolWeight> list, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) throws StoreException {
        try {
            this.calSampleCoinService.delete(calSampleSpotSymbolWeight);
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
