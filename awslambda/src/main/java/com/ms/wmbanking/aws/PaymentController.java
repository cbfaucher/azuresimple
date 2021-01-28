package com.ms.wmbanking.aws;

import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController
@Profile("local")
public class PaymentController {

    @Autowired
    private Function<Payment, PaymentEvent> paymentHandler;

    @RequestMapping(value = "/payment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PaymentEvent addPayment(@RequestBody final Payment payment) {
        return paymentHandler.apply(payment);
    }
}
