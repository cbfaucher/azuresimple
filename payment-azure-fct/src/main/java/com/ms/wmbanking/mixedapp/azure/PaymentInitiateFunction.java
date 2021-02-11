package com.ms.wmbanking.mixedapp.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.ServiceBusTopicOutput;
import com.ms.wmbanking.mixedapp.business.Payment;
import com.ms.wmbanking.mixedapp.business.PaymentBeans;
import com.ms.wmbanking.mixedapp.business.PaymentEvent;
import lombok.val;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.function.Function;


public class PaymentInitiateFunction {

    private ConfigurableApplicationContext springContext;

    public PaymentInitiateFunction() {
        try {
            System.out.println("--> Loading the Spring Context...");
            springContext = new AnnotationConfigApplicationContext(PaymentBeans.class);
            //springContext.refresh();

        } catch (Exception e) {
            System.err.println("--> Failure loading Spring Context!");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @FunctionName("initiate")
    @ServiceBusTopicOutput(name = "SBusOutput",
                           connection = "ServiceBusConnectionString",
                           topicName = "newpayment",
                           subscriptionName = "newpayments")
    public PaymentEvent initiateViaHttp(@HttpTrigger(name = "initiate", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Payment> request,
                                        final ExecutionContext context) {

        val log = context.getLogger();

        log.info(String.format("--> Entering: initiateViaHttp.  Fetching Bean '%s' now...", context.getFunctionName()));
        val handler = (Function<Payment, PaymentEvent>) springContext.getBean(context.getFunctionName());

        log.info("Invoking Bean Handler...");
        return handler.apply(request.getBody());
    }
}
