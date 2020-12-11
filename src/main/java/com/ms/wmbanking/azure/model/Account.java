package com.ms.wmbanking.azure.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@With
@Getter
@EqualsAndHashCode
@ToString
public class Account {
    private String ownerName;
    private String accountNumber;
}
