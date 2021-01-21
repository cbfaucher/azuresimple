package com.ms.azure.cosmosdb;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.val;

public class CosmosController {

    @FunctionName("accountUpdate")
    public void add(@HttpTrigger(name = "add", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS)
                        final HttpRequestMessage<AccountUpdate> request) {

        val update = request.getBody();
        System.out.println("--> Got new update: " + update.toString());
    }
}
