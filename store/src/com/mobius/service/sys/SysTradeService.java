package com.mobius.service.sys;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.sys.SysTrade;
import com.mobius.providers.store.sys.SysTradeStore;
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
public class SysTradeService extends HQuery implements SysTradeStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SysTrade getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SysTrade.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public SysTrade getBySign(String sign) throws StoreException {
        return $($eq("tradeSign", sign)).get(SysTrade.class);
    }

    @Transactional(type = TransactionType.READ_ONLY)
    public List<SysTrade> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SysTrade.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SysTrade sysTrade, Persistent persistent) throws StoreException {
        $(sysTrade).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SysTrade> sysTradeList, Persistent persistent) throws StoreException {
        $(sysTradeList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SysTrade sysTrade) throws StoreException {
        $(sysTrade).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SysTrade.class);
    }
}
