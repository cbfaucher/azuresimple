

# AZURE

## Parameters
* RESOURCE_GROUP=`cf-tutorial`
* EVENT_HUB_NAMESPACE=`cftutoiral`
* EVENT_HUB_NAME=`myhub`
* EVENT_HUB_AUTHORIZATION_RULE=`cf-eventhub-auth`
* COSMOS_DB_ACCOUNT=<value>
* STORAGE_ACCOUNT=`cftutorialstorage`
* FUNCTION_APP=<value>
* LOCATION=<value>

## Useful commands

* Login: ``az login``
* EventHubs:
   * list namespace: `az eventhubs namespace list`
   * Create a new hub: `az eventhubs eventhub create --resource-group "cf-tutorial" --name "myhub" --namespace-name "cftutorial" --message-retention 1`
   * List hubs: `az eventhubs eventhub list --namespace cftutorial --resource-group "cf-tutorial"`
   * Create authorization: `az eventhubs eventhub authorization-rule create --resource-group "cf-tutorial" --name "cf-eventhub-auth" --eventhub-name "myhub" --namespace-name "cftutorial" --rights Listen Send`
   * Getting your EventHub Connection String: `az eventhubs eventhub authorization-rule keys list --resource-group "cf-tutorial" --name "cf-eventhub-auth" --eventhub-name "myhub" --namespace-name "cftutorial" --query primaryConnectionString --output tsv`
* WebJobs Storage
   * Create a Storage Account: `az storage account create --resource-group "cf-tutorial" --name "cftutorialstorage" --sku Standard_LRS`
   * Get your connection String (value for `AzureWebJobsStorage` in `local.settings.json`): `az storage account show-connection-string --name cftutorialstorage --query connectionString --output tsv`   
   
   
