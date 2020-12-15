package com.ms.wmbanking.azure.common.entities;

import com.ms.wmbanking.azure.common.model.Account;
import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "PAYMENTS", schema = "MMNG")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@With
@NamedQuery(name = "AllPaymentEntities",
            query = "SELECT T FROM PaymentEntity T")
public class PaymentEntity {

    static public PaymentEntity fromModel(final PaymentEvent paymentEvent) {
        if (paymentEvent == null) {
            return null;
        }

        return new PaymentEntity(paymentEvent.getPaymentId(),
                                 (float) paymentEvent.getPayment().getAmount(),
                                 Timestamp.valueOf(paymentEvent.getEntryTimestamp()),
                                 paymentEvent.getPayment().getFrom().getOwnerName(),
                                 paymentEvent.getPayment().getFrom().getAccountNumber(),
                                 paymentEvent.getPayment().getTo().getOwnerName(),
                                 paymentEvent.getPayment().getTo().getAccountNumber());
    }

    @Id
    @Column(name = "PAYMENT_ID", length = 8, nullable = false)
    private String paymentId;

    @Column(name = "AMOUNT")
    private Float amount;

    @Column(name = "ENTRY_DT", columnDefinition = "datetime")
    private Timestamp entryDT;

    @Column(name = "FROM_NAME", length = 100)
    private String fromName;

    @Column(name = "FROM_ACCOUNT_NB", length = 50)
    private String fromAccount;

    @Column(name = "TO_NAME", length = 100)
    private String toName;

    @Column(name = "TO_ACCOUNT_NB", length = 50)
    private String toAccount;

    public PaymentEvent toModel() {
        return new PaymentEvent(paymentId,
                                new Payment(amount,
                                            new Account(fromName, fromAccount),
                                            new Account(toName, toAccount)),
                                entryDT.toLocalDateTime());
    }
}
