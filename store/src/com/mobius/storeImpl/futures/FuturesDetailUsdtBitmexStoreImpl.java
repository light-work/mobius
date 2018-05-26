package com.mobius.storeImpl.futures;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailUsdtBitmex;
import com.mobius.providers.store.futures.FuturesDetailUsdtBitmexStore;
import com.mobius.service.futures.FuturesDetailUsdtBitmexService;
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
public class FuturesDetailUsdtBitmexStoreImpl implements FuturesDetailUsdtBitmexStore {

    @Inject
    private FuturesDetailUsdtBitmexService futuresDetailUsdtBitmexService;

    @Override
    @ConnectManager
    public FuturesDetailUsdtBitmex getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.futuresDetailUsdtBitmexService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.futuresDetailUsdtBitmexService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public List<FuturesDetailUsdtBitmex> getList(List<Selector> list) throws StoreException {
        try {
            return this.futuresDetailUsdtBitmexService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex, Persistent persistent) throws StoreException {
        try {
            this.futuresDetailUsdtBitmexService.save(futuresDetailUsdtBitmex, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<FuturesDetailUsdtBitmex> list, Persistent persistent) throws StoreException {
        try {
            this.futuresDetailUsdtBitmexService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(FuturesDetailUsdtBitmex futuresDetailUsdtBitmex) throws StoreException {
        try {
            this.futuresDetailUsdtBitmexService.delete(futuresDetailUsdtBitmex);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.futuresDetailUsdtBitmexService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
