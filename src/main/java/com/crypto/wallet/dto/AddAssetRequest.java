package com.crypto.wallet.dto;

import java.math.BigDecimal;

public class AddAssetRequest {

    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price; // fetch from CoinCap

    public AddAssetRequest() {}

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
