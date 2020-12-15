package com.ms.wmbanking.azure.model;

import com.google.gson.annotations.JsonAdapter;
import com.ms.wmbanking.azure.jackson.LocalDateTimeSerializer;
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

    @JsonAdapter(LocalDateTimeSerializer.class)
    final private LocalDateTime entryTimestamp;
}
