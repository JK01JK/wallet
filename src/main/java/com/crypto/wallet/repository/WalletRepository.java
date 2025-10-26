package com.crypto.wallet.repository;

import com.crypto.wallet.model.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    //Eagerly loads assets to avoid N+1 and LazyInitialization issues in controllers
    @EntityGraph(attributePaths = "assets")
    Optional<Wallet> findById(Long id);
}
