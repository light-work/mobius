package com.mobius.storeImpl.futures;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.providers.store.futures.FuturesSymbolStore;
import com.mobius.service.futures.FuturesSymbolService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class FuturesSymbolStoreImpl implements FuturesSymbolStore {

    @Inject
    private FuturesSymbolService FuturesSymbolService;

    @Override
    @ConnectManager
    public FuturesSymbol getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.FuturesSymbolService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public List<FuturesSymbol> getListByTradeMarket(Long tradeId, String market) throws StoreException {
        try {
            return this.FuturesSymbolService.getListByTradeMarket(tradeId, market);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<FuturesSymbol> getList(List<Selector> list) throws StoreException {
        try {
            return this.FuturesSymbolService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(FuturesSymbol futuresSymbol, Persistent persistent) throws StoreException {
        try {
            this.FuturesSymbolService.save(futuresSymbol, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<FuturesSymbol> list, Persistent persistent) throws StoreException {
        try {
            this.FuturesSymbolService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(FuturesSymbol futuresSymbol) throws StoreException {
        try {
            this.FuturesSymbolService.delete(futuresSymbol);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.FuturesSymbolService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
