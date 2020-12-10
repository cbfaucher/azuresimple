package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.val;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Ping extends AzureSpringBootRequestHandler<String, Response> {
    /**
     * This function listens at endpoint "/api/ping". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/ping
     * 2. curl {your host}/api/ping?name=HTTP%20Query
     */
    @FunctionName("ping")
    public HttpResponseMessage run(
            @HttpTrigger(name = "ping", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        val query = request.getQueryParameters().getOrDefault("name", "");
        val name = request.getBody().orElse(query);

        System.out.printf("--> Calling handleRequest(\"%s\", context)%n", name);
        val results = handleRequest(name, context);
        System.out.printf("--> Results is %s%n", results != null ? results.toString() : "NULL");

        return request.createResponseBuilder(HttpStatus.valueOf(results.status.value()))
                      .body(results.msg)
                      .build();
    }
}
