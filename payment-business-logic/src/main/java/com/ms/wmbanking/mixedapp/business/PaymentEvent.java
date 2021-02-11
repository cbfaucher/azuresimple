package com.ms.wmbanking.mixedapp.business;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@With
@EqualsAndHashCode
@ToString
public class PaymentEvent {
    private String paymentId;
    private long amount;
}
