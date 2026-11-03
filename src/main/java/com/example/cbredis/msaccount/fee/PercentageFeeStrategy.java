package com.example.cbredis.msaccount.fee;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("percentageFeeStrategy")
public class PercentageFeeStrategy implements FeeStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.05");

    @Override
    public BigDecimal calculateFee(BigDecimal balance) {
        return balance.multiply(RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
