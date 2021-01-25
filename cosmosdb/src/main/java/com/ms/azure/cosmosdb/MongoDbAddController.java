package com.ms.azure.cosmosdb;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;
import lombok.val;

public class MongoDbAddController extends AzureFunctionSpringHook<AccountUpdate, AccountUpdate> {

    public MongoDbAddController() {
        super(Application.class);
    }

    @FunctionName("mongoAccountUpdateAdd")
    public void add(@HttpTrigger(name = "add",
                                 methods = HttpMethod.POST,
                                 authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<AccountUpdate> request,
            final ExecutionContext context
            /*,
                    @CosmosDBOutput(
                            name = "Accounts",
                            databaseName = "TelemetryDb",
                            collectionName = "TelemetryInfo",
                            connectionStringSetting = "CosmosDBConnectionString") final OutputBinding<AccountUpdate> bindings*/) {

        val update = request.getBody();
        System.out.println("--> Got new update: " + update.toString());
        handleRequest(update, context);
    }
}
