## Proof of concept

Java and Azure Functions: https://docs.microsoft.com/en-us/azure/azure-functions/functions-reference-java?tabs=bash%2Cconsumption

* (_DONE_) Use GSON instead of Jackson --> Works natively with func.exe for Functions return values.
* (_DONE_) See how to use Spring locally only to run and debug the Function code --> Use unit/integration tests
* (_DONE_) JSR-303 Validation? --> See issue below
* Avoid the ``HttpRequestMessage``, and use type directly?
* Security: Authentication and Authorization
* (_DONE_) <strike>Multi module with Maven and Functions</strike>
* (_DONE_) <strike>What is the Azure JSON mapper and how to configure it</strike>   -->  USE GOOGLE GSON!
* Continuous deployments
* (_DONE_) Azure Events:
   * Event Hubs (Streams?!) - DONE - ALL -> TxnManager
   * Queues (MQ-like?) - DONE - TxnManager -> Approval
   * Event Grid (classic pub/sub) - DONE - TxnManager -> Execution
   * Service Bus - DONE -> PaymentSvc -> TxnManager
   
## URLs:

* *PaymentSvc/Ping*: https://cfpaymentsvc.azurewebsites.net/api/ping?name=Christian [GET, POST] 
* *PaymentSvc/Initiate*: https://cfpaymentsvc.azurewebsites.net/api/paymentInitiate [POST]
<pre>
    {
          "amount" : 12.34,
      "from" : {
        "ownerName" : "Mom",
        "accountNumber" : "123456"
      },
      "to" : {
        "ownerName" : "Christian",
        "accountNumber" : "987654"
      }
    }
</pre> 
* *PaymentSvc/Fetch*: https://cfpaymentsvc.azurewebsites.net/api/paymentList [GET]     
   
## Issues found so far

* *Spring support is partial and misleading*.  All good when run in unit tests or through `Application` class.  However, in real world, the Function is run through a cmd line tool call `func.exe`, and this one doesn't seem to connect to JSON Mapper found in Spring, thus yielding a different JSON result when invoking the Function in real world.
   * *Spring Context loaded multiple times*.  In fact, for each Function and through tests.
   * *Spring Profiles not carried over from tests*.  Profiles not carried over from tests to Application, when running tests.  This has the bad sideffect that application's properties (for instance, DB configuration) cannot be overwritten from tests' properties (e.g. running H2 in tests instead of real database).
* <strike>*Forced to return String instead of Object*.  Due to point above, we cannot rely on default JSON mapper for Functions (which is neigher Google's GSon nor Jackson).  Based on numerous examples on the web, we are forced to return a `String` from Function's method, being the already marshalled result object, instead of the result object itself.  Not cool.
   * Update: seems Azure uses Google JSON: https://docs.microsoft.com/en-us/azure/azure-functions/functions-reference-java?tabs=bash%2Cconsumption#data-type-support
   * Will switch this project to use GSON.</strike> _Switching to use GSON annotations fixed the issue_.  Jackson to be avoided at all costs.
* *Poor API from Spring Cloud for Azure*.  The base classes provided for Azure by Spring Cloud are poorly written: 
   * Reloads contexts for each Function
   * No way of setting properly profiles
   * Profiles from testing not carried over
* *Java API libraries have conflicts in third-party versions*: Azure has extra libraries you can pull in to use _programmatic_ API to send/receive messages to Events Hubs/Grids, Queues.  Unfortunately, it seems their development is not well integrated, and I ended up with multiple version clashes, preventing running my sample Functions properly.    
* *JSR-303* (or HTTP validation in general): Found now way of having _automatic_ JSR-303 validation on HTTP Trigger, or any type of validation for that matter.  Validation can be made inline code, but it adds boilerplate that would be otherwise done by HTTP layer (in Spring Rest or CXF).
* *Security/Authentication/Authorization*: Done through the Azure portal, not within App.  Which can be a good and a bad thing, moving that layer to infrastructure, and out of code.
* *The PaymentSvc FuncationApp seems to fall into an unusable state overnight*.  I have to bounce it every morning to get back on its feet.   
* *Avoid having multiple Events tools (EventHub, EventGrid, ServiceBus, ...) within same FunctionApp*.  You'll quickly run into "AbstractMethodError" (and similar), because dependencies are not coherent between the various being pulled.      
* *IntelliJ Azure plugin buggy?*  Functions work fine when run from Maven plugin (`mvn azure-functions:run`) and when deployed from Maven as well to Azure Cloud.  But getting "Entity not mapped" errors when run from Intellij plugin. 
   
### Quick fixes

* Maven's Azure plugin fails with authentication/authorization error: run `az login` from command line.
* `local-settings.json` is **local**, as its name implies.  Configuration on Azure is done through `Configuration` section for Function App.
* Do *NOT* use Jackson - use Google GSon!  Important properties
<pre>
# New way of setting previous property
spring.mvc.converters.preferred-json-mapper=gson

# Use internally by Spring CLoud
spring.http.converters.preferred-json-mapper=${spring.mvc.converters.preferred-json-mapper}
</pre>    

## AZURE 

### Grid, Hub, Bus, WTF?

See https://docs.microsoft.com/en-us/azure/event-grid/compare-messaging-services
and for Queues: https://docs.microsoft.com/en-us/azure/storage/queues/storage-java-how-to-use-queue-storage?tabs=java

TL/dr:
Microsoft defines _Events_ and _Messages_ as follow (from MS docs):
* *Events* : 
> An event is a lightweight notification of a condition or a state change. The publisher of the event has no expectation about how the event is handled.
* *Messages* :
> A message is raw data produced by a service to be consumed or stored elsewhere. The message contains the data that triggered the message pipeline. The publisher of the message has an expectation about how the consumer handles the message. A contract exists between the two sides. For example, the publisher sends a message with the raw data, and expects the consumer to create a file from that data and send a response when the work is done.

Conceptually, _business content_ are *messages*, not Events.  This point is important to take into account when choosing the event/message system.

| Messaging<br>Service | Msg Type           | Msg Format | Similar                  | Pros                                                                                       | Cons                                                                                                                                                     |
|----------------------|--------------------|------------|--------------------------|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Queues               | Messages or Events | Any        | MQ's Queues with a twist | Easy to create/manage<br>Easy to use<br>Sequential in nature<br>High volume (batches)      | Not exactly a "MQ Queue"                                                                                                                                 |
| Event Grids          | Events             | Specifics  |                          | High volume                                                                                | Fairly complex setup<br>Mean for _Events_ (not _messages_)<br>Could not get it to receive msgs locally (requires subscriptions to a _deployed_ Function) |
| Event Hubs           | Events             | Any        | Kafka topics-ish         | High volume<br>Front to pipelines                                                          | Not exactly a Kafka Topic either<br>Meant for events, not _messages_                                                                                     |
| Service Bus          | Messages           | Any        | Kafka Topics/MQ Queues   | High volume<br>Messaging frontend to business services<br>Queues (n-1) or Topics (pub/sub) |                                                                                                                                                          |

### Common Parameters

* RESOURCE_GROUP=`cf-tutorial`
* EVENT_HUB_NAMESPACE=`cftutorial`
* EVENT_HUB_NAME=`myhub`
* EVENT_HUB_AUTHORIZATION_RULE=`cf-eventhub-auth`
* COSMOS_DB_ACCOUNT=<value>
* STORAGE_ACCOUNT=`cftutorialstorage`
* FUNCTION_APP=<value>
* LOCATION=``eastus``

### Logging in 
* Login: ``az login``

### EventHubs
* list namespace: `az eventhubs namespace list`
* Create a new hub: `az eventhubs eventhub create --resource-group "cf-tutorial" --name "myhub" --namespace-name "cftutorial" --message-retention 1`
* List hubs: `az eventhubs eventhub list --namespace cftutorial --resource-group "cf-tutorial"`
* Create authorization: `az eventhubs eventhub authorization-rule create --resource-group "cf-tutorial" --name "cf-eventhub-auth" --eventhub-name "myhub" --namespace-name "cftutorial" --rights Listen Send`
* Getting your EventHub Connection String: `az eventhubs eventhub authorization-rule keys list --resource-group "cf-tutorial" --name "cf-eventhub-auth" --eventhub-name "myhub" --namespace-name "cftutorial" --query primaryConnectionString --output tsv`

### WebJobs Storage
* Create a Storage Account: `az storage account create --resource-group "cf-tutorial" --name "cftutorialstorage" --sku Standard_LRS`
* Get your connection String (value for `AzureWebJobsStorage` in `local.settings.json`): `az storage account show-connection-string --name cftutorialstorage --query connectionString --output tsv`   
   
### Database

See https://docs.microsoft.com/en-us/azure/azure-sql/database/single-database-create-quickstart?tabs=azure-cli

* Create a server first: `az sql server create --name payments-$RANDOM --resource-group "cf-tutorial" --location eastus --admin-user cbfaucher --admin-password cfr681`
* Set a Firewall to server.  
   * This command allows all hosts within Azure: `az sql server firewall-rule create --resource-group "cf-tutorial" --server "payments-24961" -n myrule --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0`
   * To list firewalls on that server: ``az sql server firewall-rule list --resource-group "cf-tutorial" --server "payments-24961"``

* Create DB: `az sql db create --resource-group cf-tutorial --server payments-24961 --name payments --sample-name AdventureWorksLT --edition GeneralPurpose --compute-model Serverless --family Gen5 --capacity 2`    
* If required, download the ADO.NET JDBC driver for your database: `https://docs.microsoft.com/en-ca/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver15`
* To know your JDBC connection string:
   * Navigate to your SQL DZB on the Azure portal, and open it.
   * On left-hand side, select *Connection strings*
   * Select *JDBC* tab at the top, and copy the connection string.
* How to set the DB connection string:
   * On AZURE: on your Function App's *Configuration*, create a new *Application Settings*
   * LOCALLY: Add it to your Idea RunConfig properties   
   * See https://docs.microsoft.com/en-us/azure/azure-sql/database/connect-query-java to use in Java