package com.example.cbredis.msaccount.core;

import com.example.cbredis.msaccount.domain.AccountBalance;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientCoreBankingClient implements CoreBankingClient {

    // core-banking-service - GET http://core-banking-service/accounts/core/{accountId}/balance
    private final WebClient webClient;

    public WebClientCoreBankingClient(
            WebClient.Builder builder, @Value("${core-banking.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "coreBanking", fallbackMethod = "fallbackBalance")
    public Mono<AccountBalance> getAccountBalance(String accountId) {
        return callCoreBanking(accountId);
    }

    @Override
    @CircuitBreaker(name = "coreBanking", fallbackMethod = "fallbackBalance")
    @Retry(name = "coreBanking")
    public Mono<AccountBalance> getAccountBalanceCbRetry(String accountId) {
        return callCoreBanking(accountId);
    }

    @Override
    @RateLimiter(name = "backendService", fallbackMethod = "fallbackRateLimiter")
    public Mono<AccountBalance> getAccountBalanceRl(String accountId) {
        return callCoreBanking(accountId);
    }

    @Override
    @TimeLimiter(name = "coreBankingTl", fallbackMethod = "fallbackBalanceTl")
    public Mono<AccountBalance> getAccountBalanceTl(String accountId) {
        // TimeLimiter annotations target CompletionStage; enforce timeout on Mono too
        return callCoreBanking(accountId).timeout(Duration.ofSeconds(1));
    }

    private Mono<AccountBalance> callCoreBanking(String accountId) {
        System.out.println(" -> Llamando a Core Bancario");
        return webClient
                .get()
                .uri("/accounts/core/{accountId}/balance", accountId)
                .retrieve()
                .bodyToMono(CoreBankingBalanceResponse.class)
                .map(r -> new AccountBalance(
                        r.accountId(), r.balance(), r.currency(), r.accountType()));
    }

    private Mono<AccountBalance> fallbackBalance(String accountId, Throwable throwable) {
        System.out.println("Ingreso a Fallback CircuitBreaker/Retry: " + throwable.toString());
        return Mono.just(new AccountBalance(accountId, BigDecimal.ZERO, "ERROR", "ERROR"));
    }

    private Mono<AccountBalance> fallbackBalanceTl(String accountId, Throwable throwable) {
        System.out.println("Ingreso a Fallback TimeLimiter: " + throwable.toString());
        return Mono.just(
                new AccountBalance(accountId, BigDecimal.ZERO, "ERROR TIMELIMITER", "ERROR TIMELIMITER"));
    }

    private Mono<AccountBalance> fallbackRateLimiter(String accountId, Throwable throwable) {
        System.out.println("Ingreso a Fallback RateLimiter");
        return Mono.just(
                new AccountBalance(accountId, BigDecimal.ZERO, "ERROR RATELIMITER", "ERROR RATELIMITER"));
    }
}
