package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.spot.SpotDailyBtc;
import com.mobius.providers.store.spot.SpotDailyBtcStore;
import com.mobius.service.spot.SpotDailyBtcService;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.ConnectManager;
import org.hibernate.HibernateException;

import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SpotDailyBtcStoreImpl implements SpotDailyBtcStore {

    @Inject
    private SpotDailyBtcService SpotDailyBtcService;

    @Override
    @ConnectManager
    public SpotDailyBtc getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.SpotDailyBtcService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.SpotDailyBtcService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public SpotDailyBtc getTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.SpotDailyBtcService.getTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SpotDailyBtc> getList(List<Selector> list) throws StoreException {
        try {
            return this.SpotDailyBtcService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDailyBtc SpotDailyBtc, Persistent persistent) throws StoreException {
        try {
            this.SpotDailyBtcService.save(SpotDailyBtc, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDailyBtc> list, Persistent persistent) throws StoreException {
        try {
            this.SpotDailyBtcService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDailyBtc SpotDailyBtc) throws StoreException {
        try {
            this.SpotDailyBtcService.delete(SpotDailyBtc);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.SpotDailyBtcService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
