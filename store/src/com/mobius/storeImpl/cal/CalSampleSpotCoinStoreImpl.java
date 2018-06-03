package com.mobius.storeImpl.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotCoin;
import com.mobius.providers.store.cal.CalSampleSpotCoinStore;
import com.mobius.service.cal.CalSampleSpotCoinService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotCoinStoreImpl implements CalSampleSpotCoinStore {

    @Inject
    private CalSampleSpotCoinService calSampleCoinService;

    @Override
    @ConnectManager
    public CalSampleSpotCoin getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.calSampleCoinService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public CalSampleSpotCoin getByCoinTradeMarket(Long coinId, Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getByCoinTradeMarket(coinId, year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotCoin> getListByYearMonth(Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getListByYearMonth(year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotCoin> getList(List<Selector> list) throws StoreException {
        try {
            return this.calSampleCoinService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(CalSampleSpotCoin calSampleSpotCoin, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(calSampleSpotCoin, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<CalSampleSpotCoin> list, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(CalSampleSpotCoin calSampleSpotCoin) throws StoreException {
        try {
            this.calSampleCoinService.delete(calSampleSpotCoin);
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
