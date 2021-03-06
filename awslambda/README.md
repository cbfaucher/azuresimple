# AWS

## Prerequisites

### Install command-line tool

See https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html, and install the AWS command-line utility for your environment.

Once installed, you can confirm if working properly by doing: ``aws --version``

### Create your Access Key

If not done already, generate your *Access Key*, so that the ``aws`` has access to your account:
* In [AWS Management Console](https://us-east-2.console.aws.amazon.com/console/home?region=us-east-2), open the dropdown menu near your userID (upper right)
* Select ``My Security Credentials``
* Expand ``Access keys (access key ID and secret access key)``
* Click the ``Create new Access Key`` button.
* Once requested, save the generated file somewhere safe (e.g. not on your ``public/`` directory...)

Inside that file, you will find the **AWSAccessKeyId** and **AWSSecretKey** values you will need later.

### Connect the ``aws`` utility to your AWS account

* Run ``aws configure`` and answer the prompts:
   * ``AWS Access Key ID``: _See Access Key file generated above_
   * ``AWS Secret Access Key``: _See Access Key file generated above_
   * ``Default region name``: Choose one of AWS deployment regions (see the dropdown menu, upper right, just after your UserId in [AWS Management Console](https://us-east-2.console.aws.amazon.com/console/home?region=us-east-2))
   * ``Default output format``: **table** or **text** (see [values](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-format))
   
This will generate the require files in your ``~/.aws`` directory.         

## My first AWS Lambda.

### Deploy to AWS

See [PaymentHandler]() class for example.

* Code your class, unit tests, etc.
* Run ``1-create-bucket.sh`` at least once (don't ask me what a _bucket_ is - no clue)
* Run ``2-deploy.sh`` to package your Lambda for AWS.  It runs ``mvn package``, and some ``aws`` commands

If previous command is successful, you should see your new Lambda on AWS Console. **Make sure you select the proper region! (upper right)** 

Refer to AWS documentation for the ``aws`` utility magic.

### Set the VPC

*WARNING*: This assumes you already have a VPC defined for your region!  If not, follow Amazon AWS tutorial to create one.

* Go to  [AWS Management Console](https://us-east-2.console.aws.amazon.com/console/home?region=us-east-2), and find your Function
* Under ``Configuration`` tab, find the ``VPC`` menu.
* If no VPC defined, click ``Edit`` button.  Select the *VPC*, *Subnets* and *Security Group* you want, and click ``Save`` button. 

### Add a Trigger

* Go to  [AWS Management Console](https://us-east-2.console.aws.amazon.com/console/home?region=us-east-2), and find your Function
* Get to your Lambda's Details
* Click the ``+ Add Trigger`` function, and fill the blanks.
   * Select **API Gateway** as trigger type.
   * Select **Create an API from dropdown
   * As ``API Type``, select **REST API** (this is a ReST service, not a web application)
   * ``Security``, select **Open** for now
   * In ``API Name``, give it a valid name, such as the function of your lambda, e.g. "Payment Add"
   * Click ``Add`` button at bottom

From trigger you can test your lambda.     
   