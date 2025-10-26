package com.crypto.wallet.repository;

import com.crypto.wallet.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> { }
