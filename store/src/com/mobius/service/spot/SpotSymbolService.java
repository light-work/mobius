package com.mobius.service.spot;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotSymbol;
import com.mobius.providers.store.spot.SpotSymbolStore;
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
public class SpotSymbolService extends HQuery implements SpotSymbolStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public SpotSymbol getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotSymbol.class);
    }



    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotSymbol> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotSymbol.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotSymbol> getListByTradeMarket(Long tradeId, String market) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("market",market),$order("displayOrder")).list(SpotSymbol.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotSymbol SpotSymbol, Persistent persistent) throws StoreException {
        $(SpotSymbol).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotSymbol> SpotSymbolList, Persistent persistent) throws StoreException {
        $(SpotSymbolList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotSymbol SpotSymbol) throws StoreException {
        $(SpotSymbol).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotSymbol.class);
    }
}
