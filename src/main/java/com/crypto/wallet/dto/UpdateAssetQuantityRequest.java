package com.crypto.wallet.dto;

import java.math.BigDecimal;

public class UpdateAssetQuantityRequest {
    private BigDecimal quantity;

    public UpdateAssetQuantityRequest() {}

    public UpdateAssetQuantityRequest(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
