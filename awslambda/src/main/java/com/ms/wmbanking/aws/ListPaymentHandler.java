package com.ms.wmbanking.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.ServerlessSpringHook;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListPaymentHandler extends ServerlessSpringHook<String, List<PaymentEvent>>
        implements RequestHandler<Map<String, String>, AwsLambdaResponse> {

    public ListPaymentHandler() {
        super(Application.class);
    }

    @Override
    public AwsLambdaResponse handleRequest(Map<String, String> map, Context context) {

        context.getLogger().log("Entering ListPaymentHandler::handleRequest(map, ...)");
        val list = handleRequest(null, new Slf4j2AwsContextLoggerBridge(context.getLogger()), "listPayments");

        System.out.println("Map received:\n" + map.keySet()
                                                  .stream()
                                                  .map(k -> String.format("--> %s=%s", k, map.get(k)))
                                                  .collect(Collectors.joining("\n")));

        return new AwsLambdaResponse().withBody(list)
                                      .withHeader("Content-Type", "application/json");
    }
}
