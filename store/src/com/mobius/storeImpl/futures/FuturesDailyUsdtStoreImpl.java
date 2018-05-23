package com.mobius.storeImpl.futures;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyUsdt;
import com.mobius.providers.store.futures.FuturesDailyUsdtStore;
import com.mobius.service.futures.FuturesDailyUsdtService;
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
public class FuturesDailyUsdtStoreImpl implements FuturesDailyUsdtStore {

    @Inject
    private FuturesDailyUsdtService FuturesDailyUsdtService;

    @Override
    @ConnectManager
    public FuturesDailyUsdt getById(Long id,Selector... selectors) throws StoreException {
        try {
            return this.FuturesDailyUsdtService.getById(id,selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.FuturesDailyUsdtService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }



    @Override
    @ConnectManager
    public List<FuturesDailyUsdt> getList(List<Selector> list) throws StoreException {
        try {
            return this.FuturesDailyUsdtService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(FuturesDailyUsdt futuresDailyUsdt,Persistent persistent) throws StoreException {
        try {
            this.FuturesDailyUsdtService.save(futuresDailyUsdt,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<FuturesDailyUsdt> list,Persistent persistent) throws StoreException {
        try {
            this.FuturesDailyUsdtService.save(list,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(FuturesDailyUsdt futuresDailyUsdt) throws StoreException {
        try {
            this.FuturesDailyUsdtService.delete(futuresDailyUsdt);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.FuturesDailyUsdtService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
