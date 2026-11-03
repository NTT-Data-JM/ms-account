package com.example.cbredis.msaccount.fee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeeStrategySelector {

    private final PercentageFeeStrategy percentageFeeStrategy;
    private final FixedFeeStrategy fixedFeeStrategy;

    /**
     * PREMIUM accounts use percentage fee (5%); otherwise fixed fee.
     */
    public FeeStrategy select(String accountType) {
        if ("PREMIUM".equalsIgnoreCase(accountType)) {
            return percentageFeeStrategy;
        }
        return fixedFeeStrategy;
    }
}
