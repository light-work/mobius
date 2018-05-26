package com.mobius.entity.sys;

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
@Table(name = "SYS_CAPITALIZATION")
public class SysCapitalization extends IdEntity implements Tracker {

    private static final long serialVersionUID = 1L;

    private Long id;

    private SysCoin coinId;

    private Double volume;

    private Double circulating;

    private Date recordDate;

    private Date recordTime;

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
    @JoinColumn(name = "COIN_ID")
    public SysCoin getCoinId() {
        return coinId;
    }

    public void setCoinId(SysCoin coinId) {
        this.coinId = coinId;
    }

    @Column(name = "VOLUME")
    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    @Column(name = "CIRCULATING")
    public Double getCirculating() {
        return circulating;
    }

    public void setCirculating(Double circulating) {
        this.circulating = circulating;
    }

    @Column(name = "RECORD_DATE")
    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Column(name = "RECORD_TIME")
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
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
}
