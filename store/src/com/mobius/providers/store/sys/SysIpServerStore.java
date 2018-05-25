package com.mobius.providers.store.sys;


import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysIpServer;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface SysIpServerStore {

    SysIpServer getById(Long id, Selector... selectors) throws StoreException;

    SysIpServer getByIpServerMarket(String ipServer,String market) throws StoreException;

    void save(SysIpServer sysIpServer, Persistent persistent) throws StoreException;

    void save(List<SysIpServer> sysIpServerList, Persistent persistent) throws StoreException;

    void delete(SysIpServer sysIpServer) throws StoreException;

    void deleteById(Long id) throws StoreException;
}
