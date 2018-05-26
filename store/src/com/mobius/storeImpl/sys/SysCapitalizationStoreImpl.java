package com.mobius.storeImpl.sys;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysCapitalization;
import com.mobius.providers.store.sys.SysCapitalizationStore;
import com.mobius.service.sys.SysCapitalizationService;
import org.guiceside.commons.Page;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SysCapitalizationStoreImpl implements SysCapitalizationStore {

    @Inject
    private SysCapitalizationService sysCapitalizationService;

    @Override
    @ConnectManager
    public SysCapitalization getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.sysCapitalizationService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public Page<SysCapitalization> getPageList(int start, int limit, List<Selector> selectorList) throws StoreException {
        try {
            return this.sysCapitalizationService.getPageList(start, limit, selectorList);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }    }

    @Override
    @ConnectManager
    public List<SysCapitalization> getList(List<Selector> list) throws StoreException {
        try {
            return this.sysCapitalizationService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SysCapitalization SysCapitalization, Persistent persistent) throws StoreException {
        try {
            this.sysCapitalizationService.save(SysCapitalization, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SysCapitalization> list, Persistent persistent) throws StoreException {
        try {
            this.sysCapitalizationService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SysCapitalization SysCapitalization) throws StoreException {
        try {
            this.sysCapitalizationService.delete(SysCapitalization);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.sysCapitalizationService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
