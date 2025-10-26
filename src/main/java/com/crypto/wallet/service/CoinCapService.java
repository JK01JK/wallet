package com.crypto.wallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;


@Service
public class CoinCapService {

    private static final Logger log = LoggerFactory.getLogger(CoinCapService.class);

    private final WebClient webClient;
    private final String apiKey;

    public CoinCapService(
            @Value("${coincap.base-url}") String baseUrl,
            @Value("${coincap.api.key}") String apiKey,
            WebClient.Builder webClientBuilder
    ) {
        this.apiKey = Objects.requireNonNull(apiKey, "coincap.api.key must be set");
        this.webClient = webClientBuilder
                .baseUrl(Objects.requireNonNull(baseUrl, "coincap.base-url must be set"))
                .build();
    }

    /* latest USD price by symbol (e.g., BTC). Returns empty if not found. */
    public Optional<BigDecimal> getLatestPriceUsdBySymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) return Optional.empty();
        String upper = symbol.toUpperCase(Locale.ROOT);

        try {
            PriceBySymbolResponse resp = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/price/bysymbol/{symbol}")
                            .build(upper))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(PriceBySymbolResponse.class)
                    .onErrorResume(ex -> {
                        log.error("CoinCap call failed for {}: {}", upper, ex.getMessage(), ex);
                        return Mono.empty();
                    })
                    .block();

            if (resp == null || resp.data == null || resp.data.isEmpty()) {
                log.warn("CoinCap price not found for symbol {}", upper);
                return Optional.empty();
            }

            return Optional.of(new BigDecimal(resp.data.get(0)));
        } catch (Exception e) {
            log.error("CoinCap unexpected error for {}: {}", upper, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /* Batch prices */
    public Optional<List<BigDecimal>> getLatestPricesUsdBySymbols(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) return Optional.empty();

        String joined = String.join(",", symbols.stream()
                .map(s -> s.toUpperCase(Locale.ROOT)).toList());

        try {
            PriceBySymbolResponse resp = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/price/bysymbol/{symbols}")
                            .build(joined))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(PriceBySymbolResponse.class)
                    .onErrorResume(ex -> {
                        log.error("CoinCap batch call failed for {}: {}", joined, ex.getMessage(), ex);
                        return Mono.empty();
                    })
                    .block();

            if (resp == null || resp.data == null || resp.data.isEmpty()) {
                log.warn("CoinCap prices not found for symbols {}", joined);
                return Optional.empty();
            }

            return Optional.of(resp.data.stream().map(BigDecimal::new).toList());
        } catch (Exception e) {
            log.error("CoinCap batch unexpected error for {}: {}", joined, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /*  CoinCap slug (e.g. BTC -> "bitcoin") via /v3/assets?search= */
    public Optional<String> findSlugBySymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) return Optional.empty();

        try {
            AssetsSearchResponse resp = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/assets")
                            .queryParam("search", symbol)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(AssetsSearchResponse.class)
                    .onErrorResume(ex -> {
                        log.error("CoinCap assets search failed for {}: {}", symbol, ex.getMessage(), ex);
                        return Mono.empty();
                    })
                    .block();

            if (resp == null || resp.data == null || resp.data.isEmpty()) return Optional.empty();

            return resp.data.stream()
                    .filter(a -> symbol.equalsIgnoreCase(a.symbol))
                    .map(a -> a.slug != null && !a.slug.isBlank() ? a.slug : a.id)
                    .findFirst();

        } catch (Exception e) {
            log.error("CoinCap assets search unexpected error for {}: {}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /* Get the daily USD price for a given slug on a specific UTC date. */
    public Optional<BigDecimal> getDailyPriceUsd(String slug, LocalDate date) {
        if (slug == null || slug.isBlank() || date == null) return Optional.empty();

        Instant start = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        try {
            HistoryResponse resp = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/assets/{slug}/history")
                            .queryParam("interval", "d1")
                            .queryParam("start", start.toEpochMilli())
                            .queryParam("end", end.toEpochMilli())
                            .build(slug))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(HistoryResponse.class)
                    .onErrorResume(ex -> {
                        log.error("CoinCap history failed for {} on {}: {}",
                                slug, date.format(DateTimeFormatter.ISO_DATE), ex.getMessage(), ex);
                        return Mono.empty();
                    })
                    .block();

            if (resp == null || resp.data == null || resp.data.isEmpty()) {
                log.warn("CoinCap history empty for {} on {}", slug, date);
                return Optional.empty();
            }

            return Optional.of(new BigDecimal(resp.data.get(0).priceUsd));
        } catch (Exception e) {
            log.error("CoinCap history unexpected error for {} on {}: {}",
                    slug, date, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /* /v3/price/bysymbol response */
    public static final class PriceBySymbolResponse {
        public long timestamp;
        public List<String> data;
        public PriceBySymbolResponse() {}
    }

    /* /v3/assets?search= response */
    public static final class AssetsSearchResponse {
        public List<AssetRow> data;
        public AssetsSearchResponse() {}
    }
    public static final class AssetRow {
        public String id;      // sometimes used as slug
        public String slug;    // if present, prefer this
        public String symbol;  // e.g., "BTC"
        public String name;
        public AssetRow() {}
    }

    /* /v3/assets/{slug}/history response */
    public static final class HistoryResponse {
        public List<HistoryRow> data;
        public HistoryResponse() {}
    }
    public static final class HistoryRow {
        public String priceUsd;
        public long time;
        public String date;
        public HistoryRow() {}
    }
}
