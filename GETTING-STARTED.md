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
