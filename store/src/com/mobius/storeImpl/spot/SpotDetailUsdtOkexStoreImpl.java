package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.spot.SpotDetailUsdtOkex;
import com.mobius.providers.store.spot.SpotDetailUsdtOkexStore;
import com.mobius.service.spot.SpotDetailUsdtOkexService;
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
public class SpotDetailUsdtOkexStoreImpl implements SpotDetailUsdtOkexStore {

    @Inject
    private SpotDetailUsdtOkexService spotDetailUsdtOkexService;

    @Override
    @ConnectManager
    public SpotDetailUsdtOkex getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.spotDetailUsdtOkexService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.spotDetailUsdtOkexService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SpotDetailUsdtOkex> getList(List<Selector> list) throws StoreException {
        try {
            return this.spotDetailUsdtOkexService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @ConnectManager
    public void save(SpotDetailUsdtOkex SpotDetailUsdtOkex, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtOkexService.save(SpotDetailUsdtOkex, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDetailUsdtOkex spotDetailUsdtOkex, Persistent persistent, CalSampleSpotSymbolWeight calSampleSpotSymbolWeight) throws StoreException {
        try {
            this.spotDetailUsdtOkexService.save(spotDetailUsdtOkex, persistent,calSampleSpotSymbolWeight);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDetailUsdtOkex> list, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtOkexService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDetailUsdtOkex spotDetailUsdtOkex) throws StoreException {
        try {
            this.spotDetailUsdtOkexService.delete(spotDetailUsdtOkex);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.spotDetailUsdtOkexService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
