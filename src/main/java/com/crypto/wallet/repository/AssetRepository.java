package com.crypto.wallet.repository;

import com.crypto.wallet.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByIdAndWalletId(Long id, Long walletId);

    List<Asset> findByWalletId(Long walletId);
}
