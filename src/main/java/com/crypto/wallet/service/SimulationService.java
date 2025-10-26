package com.crypto.wallet.service;

import com.crypto.wallet.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

@Service
public class SimulationService {

    private final CoinCapService coinCapService;

    public SimulationService(CoinCapService coinCapService) {
        this.coinCapService = coinCapService;
    }


    public SimulationResponse simulate(SimulationRequest req) {
        if (req == null || req.getDate() == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("date and items are required.");
        }
        LocalDate date = req.getDate();

        BigDecimal totalNow = BigDecimal.ZERO;
        String bestAsset = null, worstAsset = null;
        BigDecimal bestPerf = null, worstPerf = null;

        for (SimulationItem it : req.getItems()) {
            String symbol = it.getSymbol();
            BigDecimal qty = it.getQuantity();
            if (symbol == null || qty == null) continue;

            // current price by symbol
            Optional<BigDecimal> nowOpt = coinCapService.getLatestPriceUsdBySymbol(symbol);
            if (nowOpt.isEmpty()) continue;
            BigDecimal now = nowOpt.get();

            // slug for history
            Optional<String> slugOpt = coinCapService.findSlugBySymbol(symbol);
            if (slugOpt.isEmpty()) continue;

            // past price on the given date
            Optional<BigDecimal> pastOpt = coinCapService.getDailyPriceUsd(slugOpt.get(), date);
            if (pastOpt.isEmpty()) continue;
            BigDecimal past = pastOpt.get();

            // accumulate total now
            totalNow = totalNow.add(now.multiply(qty));

            // performance %
            if (past.compareTo(BigDecimal.ZERO) == 0) continue; // avoid division by zero
            BigDecimal perfPct = now.subtract(past)
                    .divide(past, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            // track best/worst
            if (bestPerf == null || perfPct.compareTo(bestPerf) > 0) {
                bestPerf = perfPct;
                bestAsset = symbol.toUpperCase();
            }
            if (worstPerf == null || perfPct.compareTo(worstPerf) < 0) {
                worstPerf = perfPct;
                worstAsset = symbol.toUpperCase();
            }
        }

        // Default to zeroes if nothing was processed
        if (bestPerf == null) {
            bestPerf = BigDecimal.ZERO;
            worstPerf = BigDecimal.ZERO;
            bestAsset = "N/A";
            worstAsset = "N/A";
        }

        return new SimulationResponse(
                totalNow.setScale(2, RoundingMode.HALF_UP),
                bestAsset, bestPerf,
                worstAsset, worstPerf
        );
    }
}
