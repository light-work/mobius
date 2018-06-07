package com.mobius.storeImpl.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotWeightHistory;
import com.mobius.providers.store.cal.CalSampleSpotWeightHistoryStore;
import com.mobius.service.cal.CalSampleSpotWeightHistoryService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotWeightHistoryStoreImpl implements CalSampleSpotWeightHistoryStore {

    @Inject
    private CalSampleSpotWeightHistoryService calSampleCoinService;

    @Override
    @ConnectManager
    public CalSampleSpotWeightHistory getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.calSampleCoinService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public CalSampleSpotWeightHistory getBySymbolIdDate(Long symbolId, Date recordDate) throws StoreException {
        try {
            return this.calSampleCoinService.getBySymbolIdDate(symbolId, recordDate);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotWeightHistory> getList(List<Selector> list) throws StoreException {
        try {
            return this.calSampleCoinService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(CalSampleSpotWeightHistory calSampleSpotWeightHistory, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(calSampleSpotWeightHistory, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<CalSampleSpotWeightHistory> list, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(CalSampleSpotWeightHistory calSampleSpotWeightHistory) throws StoreException {
        try {
            this.calSampleCoinService.delete(calSampleSpotWeightHistory);
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
