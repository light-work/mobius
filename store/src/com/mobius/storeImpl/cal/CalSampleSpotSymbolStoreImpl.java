package com.mobius.storeImpl.cal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbol;
import com.mobius.providers.store.cal.CalSampleSpotSymbolStore;
import com.mobius.service.cal.CalSampleSpotSymbolService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class CalSampleSpotSymbolStoreImpl implements CalSampleSpotSymbolStore {

    @Inject
    private CalSampleSpotSymbolService calSampleCoinService;

    @Override
    @ConnectManager
    public CalSampleSpotSymbol getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.calSampleCoinService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public CalSampleSpotSymbol getBySymbolTradeMarket(Long symbolId, Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getBySymbolTradeMarket(symbolId, year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotSymbol> getListByYearMonth(Integer year, Integer month) throws StoreException {
        try {
            return this.calSampleCoinService.getListByYearMonth(year, month);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<CalSampleSpotSymbol> getList(List<Selector> list) throws StoreException {
        try {
            return this.calSampleCoinService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(CalSampleSpotSymbol calSampleSpotSymbol, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(calSampleSpotSymbol, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<CalSampleSpotSymbol> list, Persistent persistent) throws StoreException {
        try {
            this.calSampleCoinService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(CalSampleSpotSymbol calSampleSpotSymbol) throws StoreException {
        try {
            this.calSampleCoinService.delete(calSampleSpotSymbol);
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
