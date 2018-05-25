package com.mobius.storeImpl.futures;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDetailBtc;
import com.mobius.providers.store.futures.FuturesDetailBtcStore;
import com.mobius.service.futures.FuturesDetailBtcService;
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
public class FuturesDetailBtcStoreImpl implements FuturesDetailBtcStore {

    @Inject
    private FuturesDetailBtcService futuresDetailBtcService;

    @Override
    @ConnectManager
    public FuturesDetailBtc getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.futuresDetailBtcService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.futuresDetailBtcService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<FuturesDetailBtc> getList(List<Selector> list) throws StoreException {
        try {
            return this.futuresDetailBtcService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(FuturesDetailBtc futuresDetailBtc, Persistent persistent) throws StoreException {
        try {
            this.futuresDetailBtcService.save(futuresDetailBtc, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<FuturesDetailBtc> list, Persistent persistent) throws StoreException {
        try {
            this.futuresDetailBtcService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(FuturesDetailBtc futuresDetailBtc) throws StoreException {
        try {
            this.futuresDetailBtcService.delete(futuresDetailBtc);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.futuresDetailBtcService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
