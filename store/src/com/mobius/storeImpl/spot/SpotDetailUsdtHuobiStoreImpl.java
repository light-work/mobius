package com.mobius.storeImpl.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtHuobi;
import com.mobius.providers.store.spot.SpotDetailUsdtHuobiStore;
import com.mobius.service.spot.SpotDetailUsdtHuobiService;
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
public class SpotDetailUsdtHuobiStoreImpl implements SpotDetailUsdtHuobiStore {

    @Inject
    private SpotDetailUsdtHuobiService spotDetailUsdtHuobiService;

    @Override
    @ConnectManager
    public SpotDetailUsdtHuobi getById(Long id, Selector... selectors) throws StoreException {
        try {
            return this.spotDetailUsdtHuobiService.getById(id, selectors);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }


    @Override
    @ConnectManager
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        try {
            return this.spotDetailUsdtHuobiService.getCountTradeSymbolDay(tradeId, symbolId, tradingDay);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public List<SpotDetailUsdtHuobi> getList(List<Selector> list) throws StoreException {
        try {
            return this.spotDetailUsdtHuobiService.getList(list);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @ConnectManager
    public void save(SpotDetailUsdtHuobi SpotDetailUsdtHuobi, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtHuobiService.save(SpotDetailUsdtHuobi, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(SpotDetailUsdtHuobi spotDetailUsdtHuobi, Persistent persistent,
                     CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        try {
            this.spotDetailUsdtHuobiService.save(spotDetailUsdtHuobi, persistent,calSampleSpotSymbolWeightPrice);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void save(List<SpotDetailUsdtHuobi> list, Persistent persistent) throws StoreException {
        try {
            this.spotDetailUsdtHuobiService.save(list, persistent);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void delete(SpotDetailUsdtHuobi spotDetailUsdtHuobi) throws StoreException {
        try {
            this.spotDetailUsdtHuobiService.delete(spotDetailUsdtHuobi);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }

    @Override
    @ConnectManager
    public void deleteById(Long aLong) throws StoreException {
        try {
            this.spotDetailUsdtHuobiService.deleteById(aLong);
        } catch (HibernateException e) {
            Throwable throwable = e.getCause() != null ? e.getCause() : e;
            throw new StoreException(throwable.getLocalizedMessage(), e.fillInStackTrace());
        }
    }
}
