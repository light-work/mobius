package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtBitfinex;
import com.mobius.providers.store.spot.SpotDetailUsdtBitfinexStore;
import com.mobius.service.spot.SpotDetailUsdtBitfinexService;
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
public class SpotDetailUsdtBitfinexStoreImpl implements SpotDetailUsdtBitfinexStore {

    @Inject
    private SpotDetailUsdtBitfinexService spotDetailUsdtBitfinexService;

    @Override
    @ConnectManager
    public SpotDetailUsdtBitfinex getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.spotDetailUsdtBitfinexService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.spotDetailUsdtBitfinexService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SpotDetailUsdtBitfinex> getList(List<Selector> list) throws StoreException {
        try {
            return this.spotDetailUsdtBitfinexService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @ConnectManager
    public void save(SpotDetailUsdtBitfinex SpotDetailUsdtBitfinex, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtBitfinexService.save(SpotDetailUsdtBitfinex, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDetailUsdtBitfinex spotDetailUsdtBitfinex, Persistent persistent,
                     CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        try {
            this.spotDetailUsdtBitfinexService.save(spotDetailUsdtBitfinex, persistent,
                    calSampleSpotSymbolWeightPrice);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDetailUsdtBitfinex> list, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtBitfinexService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDetailUsdtBitfinex spotDetailUsdtBitfinex) throws StoreException {
        try {
            this.spotDetailUsdtBitfinexService.delete(spotDetailUsdtBitfinex);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.spotDetailUsdtBitfinexService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
