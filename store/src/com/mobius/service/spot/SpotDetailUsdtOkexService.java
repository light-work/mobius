package com.mobius.service.spot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobius.common.StoreException;
import com.mobius.entity.cal.CalSampleSpotSymbolWeight;
import com.mobius.entity.cal.CalSampleSpotSymbolWeightPrice;
import com.mobius.entity.spot.SpotDetailUsdtOkex;
import com.mobius.providers.store.spot.SpotDetailUsdtOkexStore;
import com.mobius.service.cal.CalSampleSpotSymbolWeightPriceService;
import com.mobius.service.cal.CalSampleSpotSymbolWeightService;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.Date;
import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class SpotDetailUsdtOkexService extends HQuery implements SpotDetailUsdtOkexStore {

    @Inject
    private CalSampleSpotSymbolWeightService calSampleSpotSymbolWeightService;


    @Inject
    private CalSampleSpotSymbolWeightPriceService calSampleSpotSymbolWeightPriceService;

    @Transactional(type = TransactionType.READ_ONLY)
    public SpotDetailUsdtOkex getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(SpotDetailUsdtOkex.class);
    }


    @Transactional(type = TransactionType.READ_ONLY)
    public List<SpotDetailUsdtOkex> getList(List<Selector> selectorList) throws StoreException {
        return $(selectorList).list(SpotDetailUsdtOkex.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Integer getCountTradeSymbolDay(Long tradeId, Long symbolId, Date tradingDay) throws StoreException {
        return $($eq("tradeId.id", tradeId), $eq("symbolId.id", symbolId),
                $eq("tradingDay", tradingDay), $count("id")).value(SpotDetailUsdtOkex.class, Integer.class);
    }

    /**
     * 保存对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailUsdtOkex SpotDetailUsdtOkex, Persistent persistent) throws StoreException {
        $(SpotDetailUsdtOkex).save(persistent);
    }


    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(SpotDetailUsdtOkex spotDetailUsdtOkex, Persistent persistent, CalSampleSpotSymbolWeight calSampleSpotSymbolWeight,
                     CalSampleSpotSymbolWeightPrice calSampleSpotSymbolWeightPrice) throws StoreException {
        $(spotDetailUsdtOkex).save(persistent);
        if(calSampleSpotSymbolWeight!=null){
            calSampleSpotSymbolWeightService.save(calSampleSpotSymbolWeight,Persistent.UPDATE);
        }
        if(calSampleSpotSymbolWeightPrice!=null){
            calSampleSpotSymbolWeightPriceService.save(calSampleSpotSymbolWeightPrice,Persistent.SAVE);
        }
    }

    @Transactional(type = TransactionType.READ_WRITE)
    public void save(List<SpotDetailUsdtOkex> SpotDetailUsdtOkexList, Persistent persistent) throws StoreException {
        $(SpotDetailUsdtOkexList).save(persistent);
    }

    /**
     * 删除对象
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(SpotDetailUsdtOkex SpotDetailUsdtOkex) throws StoreException {
        $(SpotDetailUsdtOkex).delete();
    }


    /**
     * 根据id 删除对象
     *
     * @param id
     */
    @Transactional(type = TransactionType.READ_WRITE)
    public void deleteById(Long id) throws StoreException {
        $(id).delete(SpotDetailUsdtOkex.class);
    }
}
