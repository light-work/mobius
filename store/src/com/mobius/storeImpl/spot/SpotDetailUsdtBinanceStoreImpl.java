package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtBinance;
import com.mobius.providers.store.spot.SpotDetailUsdtBinanceStore;
import com.mobius.service.spot.SpotDetailUsdtBinanceService;
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
public class SpotDetailUsdtBinanceStoreImpl implements SpotDetailUsdtBinanceStore {

    @Inject
    private SpotDetailUsdtBinanceService spotDetailUsdtBinanceService;

    @Override
    @ConnectManager
    public SpotDetailUsdtBinance getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.spotDetailUsdtBinanceService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.spotDetailUsdtBinanceService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SpotDetailUsdtBinance> getList(List<Selector> list) throws StoreException {
        try {
            return this.spotDetailUsdtBinanceService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDetailUsdtBinance SpotDetailUsdtBinance, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtBinanceService.save(SpotDetailUsdtBinance, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public void save(SpotDetailUsdtBinance spotDetailUsdtBinance, Persistent persistent, CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,
                     CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        try {
            this.spotDetailUsdtBinanceService.save(spotDetailUsdtBinance, persistent,calSampleSpotSymbolWeight,calSampleSpotSymbolWeightPrice);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDetailUsdtBinance> list, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtBinanceService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDetailUsdtBinance spotDetailUsdtBinance) throws StoreException {
        try {
            this.spotDetailUsdtBinanceService.delete(spotDetailUsdtBinance);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.spotDetailUsdtBinanceService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
