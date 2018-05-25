package com.mobius.service.sys;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysIpServer;
import com.mobius.providers.store.sys.SysIpServerStore;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SysIpServerService extends HQuery implements SysIpServerStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SysIpServer getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SysIpServer.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public SysIpServer getByIpServerMarket(String ipServer,String market) throws StoreException {
        return $($eq("ipAddress", ipServer),$eq("market", market)).get(SysIpServer.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SysIpServer> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SysIpServer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SysIpServer sysIpServer, Persistent persistent) throws StoreException {
        $(sysIpServer).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SysIpServer> sysIpServerList, Persistent persistent) throws StoreException {
        $(sysIpServerList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SysIpServer sysIpServer) throws StoreException {
        $(sysIpServer).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SysIpServer.class);
    }
}
