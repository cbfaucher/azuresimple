package com.ms.wmbanking.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class HelloWorldHandler  implements RequestHandler<Map<String, String>, AwsLambdaResponse> {

    private final Supplier<LocalDateTime> dtSupplier;

    public HelloWorldHandler() {
        this(LocalDateTime::now);
    }

    @Override
    public AwsLambdaResponse handleRequest(Map<String, String> map, Context context) {



        System.out.println("Map received:\n" + map.keySet()
                                                  .stream()
                                                  .map(k -> String.format("--> %s=%s", k, map.get(k)))
                                                  .collect(Collectors.joining("\n")));

        val firstName = map.getOrDefault("firstName", "Anonymous");

        return new AwsLambdaResponse().withBody(String.format("Hello %s, it is %s", firstName, dtSupplier.get().format(DateTimeFormatter.ISO_DATE_TIME)));
    }
}
