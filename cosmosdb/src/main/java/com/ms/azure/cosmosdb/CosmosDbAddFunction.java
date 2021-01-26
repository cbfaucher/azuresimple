package com.ms.azure.cosmosdb;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.*;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CosmosDbAddFunction {

    @FunctionName("accountUpdateAdd")
    public void add(@HttpTrigger(name = "add",
                                 methods = HttpMethod.POST,
                                 authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<AccountUpdate> request,
                    @CosmosDBOutput(
                            name = "AccountUpdateOutput",
                            databaseName = "AccountsSql",
                            collectionName = "accounts",
                            connectionStringSetting = "CosmosDBConnectionString") final OutputBinding<AccountUpdate> bindings,
                    final ExecutionContext context) {

        val update = request.getBody();
        System.out.println("--> Got new update: " + update.toString());

        bindings.setValue(update);
    }

    @FunctionName("accountNumberList")
    public List<AccountUpdate> list(@HttpTrigger(name = "list",
                                                 route = "list/{accountNumber}",
                                                 methods = HttpMethod.GET,
                                                 authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Void> request,
                                    @CosmosDBInput( name = "AccountUpdateInput",
                                                    databaseName = "AccountsSql",
                                                    collectionName = "accounts",
                                                    connectionStringSetting = "CosmosDBConnectionString",
                                                    sqlQuery = "SELECT * FROM c where c.accountNumber = {accountNumber}")
                                    final Collection<AccountUpdate> items,
                                    final ExecutionContext executionContext) {

        val elements = request.getUri().getPath().split("/");
        System.out.println("PATH: " + String.join(", ", elements));

        val accountNumber = elements[3];

        System.out.printf("--> Looking for item for %s%n", accountNumber);

        return new ArrayList<>(items);
    }

}
