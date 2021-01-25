package com.ms.azure.cosmosdb;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@With
@EqualsAndHashCode(exclude = "_id")
@ToString(doNotUseGetters = true)
public class AccountUpdate {

    private String _id;
    private String accountNumber;
    private double amount;

    public AccountUpdate() {
        this(UUID.randomUUID().toString(), null, 0.0D);
    }

    public AccountUpdate(final String accountNumber, final double amount) {
        this(UUID.randomUUID().toString(), accountNumber, amount);
    }
}
