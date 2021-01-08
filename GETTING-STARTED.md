<h1 align="center"> :grey_exclamation: Getting Started :grey_exclamation: </h1>

----
## Software
 * We have been using Intellij for all of our development so this will assume you have intellij (and a recent version). <small>(If you don't you can grab it from [JetBrains](https://www.jetbrains.com/idea/) )</small>
   * Intellij also has an Azure plugin which you can install, but I haven't tried it out too extensively yet
 * Next you will want to install the Azure CLI and Functions CLI
   * [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-windows?tabs=azure-cli)
   * [Functions CLI](https://aka.ms/azfunc-install)
 * You will also want to have the [.Net SDK](https://dotnet.microsoft.com/download) installed
 * For dependency management you will want a local installation of [Maven](https://maven.apache.org/download.cgi)

## Azure
 * You will need an active Azure subscription on which you can create resources

## Setting up your workspace
 * Create a fork of repo and clone your fork to a local location
 * In Intellij browse to the pom.xml in the root folder and open it
    * Intellij will ask you if you want to open the project or just the file - choose to open the whole project
 * The first time it opens, Intellij will need to import all the settings which could take a bit of time
 * Once everything is done, ensure that the maven dependencies have been loaded
    * If they haven't been, usually you won't see the java files in the project explorer panel
      * In this case, manually tell intellij to reload the dependencies using the "refresh" button from the Maven panel
 * You should also be able to see the run configurations in the toolbar
    * If you dont have a toolbar: `View` :arrow_right: `Apperance` :arrow_right: `Toolbar`
    * If you dont see the run configurations, chances are you are using an older version on intellij
   
## Setting up the Azure Resources
 *The examples here are all going to be using the CLI but all the same actions can also be performed via the Azure Portal UI*

### Login
 * First we will need to log into Azure CLI. Bring up a terminal and type `az login`
   * This will launch a browser which allows you to log in with your Azure account 
   * Upon success, it will return a list of all the subscriptions your account has access to
   
### Resource Group
 * Now we need to create a resource group. We are going to do this by running the command `az group create -l eastus -n MyResourceGroup`
   * Adjust the values as needed
     * `eastus` is the location of the group
     * `MyResourceGroup` is the name of the resource group
   * If you have multiple subscriptions under your account you can use the `--subscription` flag to specify which one to create it under.
   * Upon success, it will return the details of the resource group. If you have multiple subscriptions, ensure the resource group was created under the correct one.
   * Record the name of the resource group for use later on
   
### Database
 * Now we will create the database for our app
 * First we create a database server
   
   ```shell
   az sql server create --name myservername --resource-group MyResourceGroup --location eastus --admin-user alex --admin-password P@$$W0rd123
   ```
   * Adjust the values as needed
      * `myservername` is the name of the SQL server we are creating, it needs to be unique (ex. payments-1234)  
      * `MyResourceGroup` is the name of the resource group
      * `eastus` is the location of the server
      * `alex` is the username of the SQL admin
      * `P@$$W0rd123` is the password of the SQL admin
   * Make a note of the user name and password as we will need it later
 * Next we will create a firewall rule to allow all internal Azure machines to access our DB (In a real scenario this would be more locked down)

   ```shell
   az sql server firewall-rule create --resource-group MyResourceGroup --server myservername -n myrule --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
   ```
    * Adjust the values as needed
      * `MyResourceGroup` is the name of the resource group
      * `myservername` is the name of the SQL server we created
      * `myrule` is the name of the Rule we are creating
 * To make sure it worked we can run
    
   ```shell
   az sql server firewall-rule list --resource-group "MyResourceGroup" --server "myservername"
   ```
    * Adjust the values as needed
      * `MyResourceGroup` is the name of the resource group
      * `myservername` is the name of the SQL server we created
 * Next we create the actual Database
   
   ```shell
   az sql db create --resource-group MyResourceGroup --server myservername --name payments --edition GeneralPurpose --compute-model Serverless --family Gen5 --capacity 2
   ```
   * Adjust the values as needed
     * `MyResourceGroup` is the name of the resource group
     * `myservername` is the name of the SQL server we created
     * `payments` is the name of the DB we are creating
 * To get your DB connection string

   ```shell
   az sql db show-connection-string -s myservername -n payments -c jdbc --output tsv
   ```
    * Adjust the values as needed
      * `myservername` is the name of the SQL server we created
      * `payments` is the name of the DB we created
 * In the connection string you will want to update the `<username>` and `<password>` values with those that you saved earlier. Save this connection string for later use.

### Event Hub
 * Now we create the eventhub (We're almost done with the infra)
 * First we create the namespace

   ```shell
   az eventhubs namespace create --resource-group MyResourceGroup --name mynamespace --location eastus
   ```
   * Adjust the values as needed
     * `MyResourceGroup` is the name of the resource group
     * `mynamespace` is the name of the namespace we are creating, must be unique
     * `eastus` is the location of the namespace
 * To verify the namespace was created we can run

   ```shell
   az eventhubs namespace list
   ```
 * Now we create the event hub

   ```shell
   az eventhubs eventhub create --resource-group "MyResourceGroup" --name "newpayment" --namespace-name "mynamespace" --message-retention 1
   ```
   * Adjust the values as needed
     * `MyResourceGroup` is the name of the resource group
     * `newpayment` is the name of the event hub (use the given name for the sample to work)
     * `mynamespace` is the name of the namespace we are creating, must be unique
 * To verify the event hub was created

   ```shell
   az eventhubs eventhub list --namespace mynamespace --resource-group "MyResourceGroup"
   ```
 * Next we need an authorization rule

   ```shell
   az eventhubs eventhub authorization-rule create --resource-group "MyResourceGroup" --name "my-eventhub-auth" --eventhub-name "newpayment" --namespace-name "mynamespace" --rights Listen Send
   ```
   * Adjust the values as needed
     * `MyResourceGroup` is the name of the resource group
     * `my-eventhub-auth` is the name of the auth key we are creating  
     * `newpayment` is the name of the event hub
     * `mynamespace` is the name of the namespace
 * To get your Event Hub connection string

   ```shell
   az eventhubs eventhub authorization-rule keys list --resource-group "MyResourceGroup" --name "my-eventhub-auth" --eventhub-name "newpayment" --namespace-name "mynamespace" --query primaryConnectionString --output tsv
   ```
 * In the connection string, remove the part at then end which says `EntityPath=newpayment`. Save this connection string for later use.

### Update Config
 * We're finally done with infra :balloon: Now on to updating the config. We're going to update the config for the payment service, but the same process applies to the other modules.
 * Open the `pom.xml` in the payment module
   * Find the properties `functionResourceGroup`, `functionAppName` and `functionAppRegion` and update them with the appropriate values
     * `functionResourceGroup` is the name of our resource group (what we previously called `MyResourceGroup`)
     * `functionAppName` is the name of the new function app, needs to be unique  (ex. arpaymentsvc)
     * `functionAppRegion` is the region the app will run in (ex. eastus)
 * Open the root `pom.xml`
     * Find the property `url` update it to `https://functionAppName.azurewebsites.net` (ex. https://arpaymentsvc.azurewebsites.net)
 * Under `payments/src/main/resources` create a properties file with the name `application-username.properties` where `username` is your windows username
     * Create and fill in the values for the following properties 
       * `spring.datasource.url` to the SQL connection string we saved before
       * `spring.datasource.username` to the SQL connection username we saved before
       * `spring.datasource.password` to the SQL connection password we saved before
 * Under `payments/src/main/azure` create a file called `local.setting.json`
   * Put the following into the file:
    
   ```json
    {
        "IsEncrypted": false,
        "Values": {
            "MAIN_CLASS": "com.ms.wmbanking.azure.Application",
            "FUNCTIONS_WORKER_RUNTIME": "java",
            "IsEncrypted": "false",
            "spring.profiles.active": "${user.name}"
        }
    }
    ```

### Compile and Run
 * To compile and package the project run `mvn clean package` (this uses your local maven installation)
 * Once the application is built, you can run it locally using the Azure Function Maven plug-in
   
   ```shell
   cd payment
   mvn azure-functions:run
   ```
 * Once the startup completes it should print out your local urls. You can try to hit the endpoint to ensure they work. (ex. `http://localhost:7071/api/ping?name=Alex`)
 * To stop the function you can use <kbd>CTRL</kbd> + <kbd>C</kbd>, then confirm at the prompt

### Deploy to Azure
 * To deploy the project to Azure, `mvn azure-functions:deploy`
 * The first time you d this it will create the App which can be a little slow. On future invocations it updates the existing app.
 * nce complete it should print out your urls which yuo can then invoke t test the function (ex. `https://arpaymentsvc.azurewebsites.net/api/ping?name=Alex`)