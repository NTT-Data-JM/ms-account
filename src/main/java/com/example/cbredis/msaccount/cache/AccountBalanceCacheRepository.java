package com.example.cbredis.msaccount.cache;

import com.example.cbredis.msaccount.domain.AccountBalance;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository encapsulating Redis access for account balances (Cache Aside).
 */
@Repository
@RequiredArgsConstructor
public class AccountBalanceCacheRepository {

    private static final String KEY_PREFIX = "account:balance:";

    private final ReactiveRedisTemplate<String, AccountBalance> redisTemplate;

    @Value("${cache.account-balance.ttl}")
    private Duration ttl;

    public Mono<AccountBalance> findByAccountId(String accountId) {
        return redisTemplate.opsForValue().get(key(accountId));
    }

    public Mono<Boolean> save(AccountBalance balance) {
        return redisTemplate.opsForValue()
                .set(key(balance.accountId()), balance, ttl);
    }

    private String key(String accountId) {
        return KEY_PREFIX + accountId;
    }
}
