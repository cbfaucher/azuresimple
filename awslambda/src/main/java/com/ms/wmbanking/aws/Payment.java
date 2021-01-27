package com.ms.wmbanking.aws;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@EqualsAndHashCode
@ToString(includeFieldNames = false)
public class Payment {
    private String paymentId;
    private double amount;
}
