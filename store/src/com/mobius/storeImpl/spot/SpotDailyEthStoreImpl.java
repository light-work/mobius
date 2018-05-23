package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyEth;
import com.mobius.providers.store.spot.SpotDailyEthStore;
import com.mobius.service.spot.SpotDailyEthService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SpotDailyEthStoreImpl implements SpotDailyEthStore {

    @Inject
    private SpotDailyEthService SpotDailyEthService;

    @Override
    @ConnectManager
    public SpotDailyEth getById(Long id,Selector... selectors) throws StoreException {
        try {
            return this.SpotDailyEthService.getById(id,selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }



    @Override
    @ConnectManager
    public List<SpotDailyEth> getList(List<Selector> list) throws StoreException {
        try {
            return this.SpotDailyEthService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDailyEth SpotDailyEth,Persistent persistent) throws StoreException {
        try {
            this.SpotDailyEthService.save(SpotDailyEth,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDailyEth> list,Persistent persistent) throws StoreException {
        try {
            this.SpotDailyEthService.save(list,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDailyEth SpotDailyEth) throws StoreException {
        try {
            this.SpotDailyEthService.delete(SpotDailyEth);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.SpotDailyEthService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
