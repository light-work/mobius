package com.mobius.entity.cal;

import com.mobius.entity.spot.SpotSymbol;
import com.mobius.entity.sys.SysCoin;
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
@Table(name = "CAL_SAMPLE_SPOT_SYMBOL")
public class CalSampleSpotSymbol extends IdEntity implements Tracker {

    private static final long serialVersionUID = 1L;

    private Long id;

    private SpotSymbol symbolId;

    private Integer useYear;

    private Integer useMonth;

    private Integer year;

    private Integer month;

    private Double median;

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
    @JoinColumn(name = "SYMBOL_ID")
    public SpotSymbol getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(SpotSymbol symbolId) {
        this.symbolId = symbolId;
    }


    @Column(name = "USE_YEAR")
    public Integer getUseYear() {
        return useYear;
    }

    public void setUseYear(Integer useYear) {
        this.useYear = useYear;
    }

    @Column(name = "USE_MONTH")
    public Integer getUseMonth() {
        return useMonth;
    }

    public void setUseMonth(Integer useMonth) {
        this.useMonth = useMonth;
    }

    @Column(name = "YEAR")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "MONTH")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "MEDIAN")
    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
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
