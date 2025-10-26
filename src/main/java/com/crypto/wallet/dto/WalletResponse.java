package com.crypto.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalletResponse {

    private final Long id;
    private final String email;
    private final LocalDateTime createdAt;
    private final BigDecimal total;
    private final List<AssetView> assets;

    public WalletResponse(Long id, String email, LocalDateTime createdAt, BigDecimal total, List<AssetView> assets) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.total = total;
        this.assets = assets;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getTotal() { return total; }
    public List<AssetView> getAssets() { return assets; }
}
