package com.crypto.wallet.dto;

import java.math.BigDecimal;

public class SimulationResponse {
    private BigDecimal total;              // total current USD worth
    private String bestAsset;              // symbol of best performer
    private BigDecimal bestPerformance;    // % up (e.g., 35.35)
    private String worstAsset;             // symbol of worst performer
    private BigDecimal worstPerformance;   // % up/down (positive means up, negative down)

    public SimulationResponse() {}

    public SimulationResponse(BigDecimal total,
                              String bestAsset, BigDecimal bestPerformance,
                              String worstAsset, BigDecimal worstPerformance) {
        this.total = total;
        this.bestAsset = bestAsset;
        this.bestPerformance = bestPerformance;
        this.worstAsset = worstAsset;
        this.worstPerformance = worstPerformance;
    }

    public BigDecimal getTotal() { return total; }
    public String getBestAsset() { return bestAsset; }
    public BigDecimal getBestPerformance() { return bestPerformance; }
    public String getWorstAsset() { return worstAsset; }
    public BigDecimal getWorstPerformance() { return worstPerformance; }

    public void setTotal(BigDecimal total) { this.total = total; }
    public void setBestAsset(String bestAsset) { this.bestAsset = bestAsset; }
    public void setBestPerformance(BigDecimal bestPerformance) { this.bestPerformance = bestPerformance; }
    public void setWorstAsset(String worstAsset) { this.worstAsset = worstAsset; }
    public void setWorstPerformance(BigDecimal worstPerformance) { this.worstPerformance = worstPerformance; }
}
