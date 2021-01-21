package com.ms.azure.cosmosdb;

import lombok.*;

@RequiredArgsConstructor
@Getter
@With
@EqualsAndHashCode
@ToString(doNotUseGetters = true)
public class AccountUpdate {
    final private String accountNumber;
    final private double amount;

    public AccountUpdate() {
        this(null, 0.0D);
    }
}
