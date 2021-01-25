package com.ms.azure.cosmosdb;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;
import lombok.val;

import java.util.List;

public class MongoDbListController extends AzureFunctionSpringHook<String, List<AccountUpdate>> {

    public MongoDbListController() {
        super(Application.class);
    }

    //  todo: Constants for Function Names!

    @FunctionName("mongoAccountNumberList")
    public List<AccountUpdate> list(@HttpTrigger(name = "list",
                                                 route = "list/{accountNumber}",
                                                 methods = HttpMethod.GET,
                                                 authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Void> request,
                                    final ExecutionContext executionContext) {

        val elements = request.getUri().getPath().split("/");
        System.out.println("PATH: " + String.join(", ", elements));

        val accountNumber = elements[3];

        return handleRequest(accountNumber, executionContext);
    }
}
