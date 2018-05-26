package com.mobius.entity.futures;

import com.mobius.entity.sys.SysTrade;
import org.guiceside.persistence.entity.IdEntity;
import org.guiceside.persistence.entity.Tracker;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-8-15
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "FUTURES_DETAIL_ETH_OKEX")
public class FuturesDetailEthOkex extends IdEntity implements Tracker {

    private static final long serialVersionUID = 1L;

    private Long id;

    private SysTrade tradeId;

    private FuturesSymbol symbolId;

    private Date tradingDay;

    private Date tradingTime;

    private Double price;

    private Double bidPrice;

    private Double bidVolume;

    private Double askPrice;

    private Double askVolume;

    private Double volume;

    private Double turnover;

    private Date created;

    private String createdBy;

    private Date updated;

    private String updatedBy;

    private String useYn;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADE_ID")
    public SysTrade getTradeId() {
        return tradeId;
    }

    public void setTradeId(SysTrade tradeId) {
        this.tradeId = tradeId;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SYMBOL_ID")
    public FuturesSymbol getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(FuturesSymbol symbolId) {
        this.symbolId = symbolId;
    }

    @Column(name = "TRADING_DAY")
    public Date getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(Date tradingDay) {
        this.tradingDay = tradingDay;
    }

    @Column(name = "VOLUME")
    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    @Column(name = "TURNOVER")
    public Double getTurnover() {
        return turnover;
    }

    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }

    @Column(name = "CREATED", updatable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "CREATED_BY")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED")
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Column(name = "UPDATED_BY")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "USE_YN")
    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    @Column(name = "TRADING_TIME")
    public Date getTradingTime() {
        return tradingTime;
    }

    public void setTradingTime(Date tradingTime) {
        this.tradingTime = tradingTime;
    }

    @Column(name = "PRICE")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "BID_PRICE")
    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    @Column(name = "BID_VOLUME")
    public Double getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(Double bidVolume) {
        this.bidVolume = bidVolume;
    }

    @Column(name = "ASK_PRICE")
    public Double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(Double askPrice) {
        this.askPrice = askPrice;
    }

    @Column(name = "ASK_VOLUME")
    public Double getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(Double askVolume) {
        this.askVolume = askVolume;
    }
}
