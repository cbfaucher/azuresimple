package com.ms.wmbanking.azure.common.model;

import com.google.gson.annotations.JsonAdapter;
import com.ms.wmbanking.azure.common.jackson.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@With
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PaymentEvent {

    public enum Status { Initiating, Approving, Approved, Executing, Executed };

    private String paymentId;

    private Payment payment;

    private Status status;

    @JsonAdapter(LocalDateTimeSerializer.class)
    private LocalDateTime entryTimestamp;
}
