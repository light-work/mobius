package com.mobius.storeImpl.sys;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalizationLast;
import com.mobius.providers.store.sys.SysCapitalizationLastStore;
import com.mobius.service.sys.SysCapitalizationLastService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SysCapitalizationLastStoreImpl implements SysCapitalizationLastStore {

    @Inject
    private SysCapitalizationLastService sysCapitalizationLastService;

    @Override
    @ConnectManager
    public SysCapitalizationLast getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.sysCapitalizationLastService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public SysCapitalizationLast getByCoinId(Long coinId) throws StoreException {
        try {
            return this.sysCapitalizationLastService.getByCoinId(coinId);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SysCapitalizationLast> getList(List<Selector> list) throws StoreException {
        try {
            return this.sysCapitalizationLastService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SysCapitalizationLast SysCapitalizationLast, Persistent persistent) throws StoreException {
        try {
            this.sysCapitalizationLastService.save(SysCapitalizationLast, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SysCapitalizationLast> list, Persistent persistent) throws StoreException {
        try {
            this.sysCapitalizationLastService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SysCapitalizationLast SysCapitalizationLast) throws StoreException {
        try {
            this.sysCapitalizationLastService.delete(SysCapitalizationLast);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.sysCapitalizationLastService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
