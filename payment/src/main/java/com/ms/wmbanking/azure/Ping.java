package com.ms.wmbanking.azure;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.common.model.Response;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;
import lombok.val;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Ping extends AzureFunctionSpringHook<String, Response> {

    public Ping() {
        super(Application.class);
    }

    /**
     * This function listens at endpoint "/api/ping". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/ping
     * 2. curl {your host}/api/ping?name=HTTP%20Query
     */
    @FunctionName("ping")
    public HttpResponseMessage ping(
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
