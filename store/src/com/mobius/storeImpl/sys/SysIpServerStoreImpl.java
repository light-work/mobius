package com.mobius.storeImpl.sys;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.providers.store.sys.SysIpServerStore;
import com.mobius.service.sys.SysIpServerService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SysIpServerStoreImpl implements SysIpServerStore {

    @Inject
    private SysIpServerService sysIpServerService;

    @Override
    @ConnectManager
    public SysIpServer getById(Long id,Selector... selectors) throws StoreException {
        try {
            return this.sysIpServerService.getById(id,selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public SysIpServer getByIpServerMarket(String ipServer,String market) throws StoreException {
        try {
            return this.sysIpServerService.getByIpServerMarket(ipServer,market);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SysIpServer sysIpServer,Persistent persistent) throws StoreException {
        try {
            this.sysIpServerService.save(sysIpServer,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SysIpServer> list,Persistent persistent) throws StoreException {
        try {
            this.sysIpServerService.save(list,persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SysIpServer sysIpServer) throws StoreException {
        try {
            this.sysIpServerService.delete(sysIpServer);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.sysIpServerService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
