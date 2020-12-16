package com.ms.wmbanking.azure.common.model;

import com.google.gson.annotations.JsonAdapter;
import com.ms.wmbanking.azure.common.jackson.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@With
@Getter
@EqualsAndHashCode
@ToString
public class PaymentEvent {

    public enum Status { Initiating, Approving, Approved, Executing, Executed };

    final private String paymentId;

    final private Payment payment;

    final private Status status;

    @JsonAdapter(LocalDateTimeSerializer.class)
    final private LocalDateTime entryTimestamp;
}
