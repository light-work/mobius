package com.mobius.storeImpl.futures;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesDailyBtc;
import com.mobius.providers.store.futures.FuturesDailyBtcStore;
import com.mobius.service.futures.FuturesDailyBtcService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class FuturesDailyBtcStoreImpl implements FuturesDailyBtcStore {

    @Inject
    private FuturesDailyBtcService FuturesDailyBtcService;

    @Override
    @ConnectManager
    public FuturesDailyBtc getById(Long id,Selector... selectors) throws StoreException {
        try {
            return this.FuturesDailyBtcService.getById(id,selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    
    @Override
    @ConnectManager
    public List<FuturesDailyBtc> getList(List<Selector> list) throws StoreException {
        try {
            return this.FuturesDailyBtcService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(FuturesDailyBtc futuresDailyBtc,Persistent persistent) throws StoreException {
        try {
            this.FuturesDailyBtcService.save(futuresDailyBtc,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<FuturesDailyBtc> list,Persistent persistent) throws StoreException {
        try {
            this.FuturesDailyBtcService.save(list,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(FuturesDailyBtc futuresDailyBtc) throws StoreException {
        try {
            this.FuturesDailyBtcService.delete(futuresDailyBtc);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.FuturesDailyBtcService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
