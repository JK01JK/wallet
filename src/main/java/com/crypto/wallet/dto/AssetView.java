package com.crypto.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssetView {

    private final String symbol;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final BigDecimal value;
    private final LocalDateTime lastUpdated;

    public AssetView(String symbol, BigDecimal quantity, BigDecimal price, BigDecimal value, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.value = value;
        this.lastUpdated = lastUpdated;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getValue() { return value; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}
