package com.crypto.wallet.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.wallet.dto.AddAssetRequest;
import com.crypto.wallet.dto.SimulationRequest;
import com.crypto.wallet.dto.SimulationResponse;
import com.crypto.wallet.dto.UpdateAssetQuantityRequest;
import com.crypto.wallet.dto.WalletDTO;
import com.crypto.wallet.service.AssetService;
import com.crypto.wallet.service.SimulationService;
import com.crypto.wallet.service.WalletService;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final AssetService assetService;
    private final SimulationService simulationService;

    public WalletController(WalletService walletService, AssetService assetService, SimulationService simulationService) {
        this.walletService = walletService;
        this.assetService = assetService;
		this.simulationService = simulationService;
    }

    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody WalletDTO request) {
        try {
            WalletDTO response = walletService.createWallet(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@PathVariable Long id) {
        try {
            var response = walletService.getWallet(id); // returns WalletResponse
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/assets")
    public ResponseEntity<?> addAsset(@PathVariable("id") Long walletId,
                                      @RequestBody AddAssetRequest request) {
        try {
            Long assetId = assetService.addAssetToWallet(walletId, request);
            return ResponseEntity.ok(Map.of(
                    "assetId", assetId,
                    "message", "Asset added successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{walletId}/assets/{assetId}")
    public ResponseEntity<?> updateAssetQuantity(@PathVariable Long walletId,
                                                 @PathVariable Long assetId,
                                                 @RequestBody UpdateAssetQuantityRequest request) {
        try {
            assetService.updateAssetQuantity(walletId, assetId, request.getQuantity());
            return ResponseEntity.ok(Map.of("message", "Quantity updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{walletId}/assets/{assetId}")
    public ResponseEntity<?> removeAsset(@PathVariable Long walletId,
                                         @PathVariable Long assetId) {
        try {
            assetService.removeAsset(walletId, assetId);
            return ResponseEntity.ok(Map.of("message", "Asset removed"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{walletId}/refresh-prices")
    public ResponseEntity<?> refreshPrices(@PathVariable Long walletId) {
        try {
            int updated = assetService.refreshPrices(walletId);
            return ResponseEntity.ok(Map.of(
                    "updated", updated,
                    "message", "Prices refreshed"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    

    @PostMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestBody SimulationRequest request) {
        try {
            SimulationResponse resp = simulationService.simulate(request);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
}
