package com.ms.wmbanking.azure.payment.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ms.wmbanking.azure.payment.jackson.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@With
@Getter
@EqualsAndHashCode
@ToString
public class PaymentEvent {

    final private String paymentId;

    final private Payment payment;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    final private LocalDateTime entryTimestamp;
}
