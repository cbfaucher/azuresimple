## Proof of concept

Java and Azure Functions: https://docs.microsoft.com/en-us/azure/azure-functions/functions-reference-java?tabs=bash%2Cconsumption

* (_DONE_) Use GSON instead of Jackson --> Works natively with func.exe for Functions return values.
* See how to use Spring locally only to run and debug the Function code
* JSR-303 Validation?
* Avoid the ``HttpRequestMessage``, and use type directly?
* Security: Authentication and Authorization
* <strike>Multi module with Maven and Functions</strike>
* <strike>What is the Azure JSON mapper and how to configure it</strike>   -->  USE GOOGLE GSON!
* Continuous deployments
* Azure Events:
   * Event Hubs (Streams?!) - DONE - ALL -> TxnManager
   * Queues (MQ-like?) - DONE - TxnManager -> Approval
   * Event Grid (classic pub/sub) - DONE - TxnManager -> Execution
   * Service Bus - TODO
   
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