## Proof of concept

* See how to use Spring locally only to run and debug the Function code
* JSR-303 Validation?
* Avoid the ``HttpRequestMessage``, and use type directly?
* Security: Authentication and Authorization
* Multi module with Maven and Functions
* What is the Azure JSON mapper and how to configure it
* Continuous deployments
* Azure Events:
   * Event Hubs (Streams?!) - DONE
   * Event Grid (classic pub/sub)
   * Queues (MQ-like?)
   * Service Bus
   

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