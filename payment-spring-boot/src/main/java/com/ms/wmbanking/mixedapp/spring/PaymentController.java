package com.ms.wmbanking.mixedapp.spring;

import com.ms.wmbanking.mixedapp.business.Payment;
import com.ms.wmbanking.mixedapp.business.PaymentEvent;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@Api
@SwaggerDefinition(
        info = @Info(
                title = "Payment Controller",
                description = "A Spring/SpringBoot Controller facing shared code with Azure Functions",
                version = "1.0"
        )
)
public class PaymentController {

    @Autowired
    private Function<Payment, PaymentEvent> initiateHandler;

    @RequestMapping(method = RequestMethod.POST, path = "payment")
    public PaymentEvent initiate(@RequestBody final Payment payment) {
        return initiateHandler.apply(payment);
    }
}
