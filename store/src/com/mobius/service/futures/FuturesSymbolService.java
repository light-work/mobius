package com.mobius.service.futures;

import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.futures.FuturesSymbol;
import com.mobius.providers.store.futures.FuturesSymbolStore;
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
public class FuturesSymbolService extends HQuery implements FuturesSymbolStore {


    @Transactional(type = TransactionType.READ_ONLY)
    public FuturesSymbol getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(FuturesSymbol.class);
    }



    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesSymbol> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(FuturesSymbol.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesSymbol> getListByTradeMarket(Long tradeId, String market) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("market",market),$eq("useYn","Y"),
                $order("displayOrder")).list(FuturesSymbol.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<FuturesSymbol> getListByTradeMarketServer(Long tradeId, String market, Integer server) throws StoreException {
        return $($eq("tradeId.id",tradeId),$eq("market",market),$eq("server",server),$eq("useYn","Y"),
                $order("displayOrder")).list(FuturesSymbol.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(FuturesSymbol futuresSymbol, Persistent persistent) throws StoreException {
        $(futuresSymbol).save(persistent);
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<FuturesSymbol> futuresSymbolList, Persistent persistent) throws StoreException {
        $(futuresSymbolList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(FuturesSymbol futuresSymbol) throws StoreException {
        $(futuresSymbol).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(FuturesSymbol.class);
    }
}
