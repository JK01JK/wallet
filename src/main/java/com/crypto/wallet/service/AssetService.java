package com.crypto.wallet.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crypto.wallet.dto.AddAssetRequest;
import com.crypto.wallet.model.Asset;
import com.crypto.wallet.model.Wallet;
import com.crypto.wallet.repository.AssetRepository;
import com.crypto.wallet.repository.WalletRepository;

@Service
public class AssetService {

    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final CoinCapService coinCapService;

    public AssetService(WalletRepository walletRepository,
                        AssetRepository assetRepository,
                        CoinCapService coinCapService) {
        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
        this.coinCapService = coinCapService;
    }

    @Transactional
    public Long addAssetToWallet(Long walletId, AddAssetRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        BigDecimal price = coinCapService.getLatestPriceUsdBySymbol(request.getSymbol())
                .orElseThrow(() -> new IllegalArgumentException("Price not found for symbol: " + request.getSymbol()));

        Asset asset = new Asset();
        asset.setWallet(wallet);
        asset.setSymbol(request.getSymbol().toUpperCase());
        asset.setQuantity(request.getQuantity());
        asset.setPrice(price);
        asset.setValue(price.multiply(request.getQuantity()));
        asset.setLastUpdated(LocalDateTime.now());

        assetRepository.save(asset);
        return asset.getId();
    }

    @Transactional
    public void updateAssetQuantity(Long walletId, Long assetId, BigDecimal newQuantity) {
        if (newQuantity == null || newQuantity.signum() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        Asset asset = assetRepository.findByIdAndWalletId(assetId, walletId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for wallet"));

        asset.setQuantity(newQuantity);
        asset.setValue(asset.getPrice().multiply(newQuantity));
        asset.setLastUpdated(LocalDateTime.now());
        assetRepository.save(asset);
    }

    @Transactional
    public void removeAsset(Long walletId, Long assetId) {
        Asset asset = assetRepository.findByIdAndWalletId(assetId, walletId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found for wallet"));
        assetRepository.delete(asset);
    }

    @Transactional
    public int refreshPrices(Long walletId) {
        // Ensure wallet exists
        walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        List<Asset> assets = assetRepository.findByWalletId(walletId);
        int updated = 0;

        for (Asset a : assets) {
            var priceOpt = coinCapService.getLatestPriceUsdBySymbol(a.getSymbol());
            if (priceOpt.isPresent()) {
                BigDecimal p = priceOpt.get();
                a.setPrice(p);
                a.setValue(p.multiply(a.getQuantity()));
                a.setLastUpdated(LocalDateTime.now());
                updated++;
            }
        }
        if (updated > 0) assetRepository.saveAll(assets);
        return updated;
    }
}
