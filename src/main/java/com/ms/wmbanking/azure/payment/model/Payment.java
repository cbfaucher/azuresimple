package com.ms.wmbanking.azure.payment.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@With
@Getter
@EqualsAndHashCode
@ToString
public class Payment {
    private double amount;

    private Account from;
    private Account to;
}
