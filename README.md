**NOTE** This repository is not maintained.  It is unsuitable for production use and probably has known security vulnerabilities.


# Kinesis Analytics
AWS Kinesis Data Analytics - Done the DevOps way

The CI/CD environment has been configured with a Build/Tooling account and Sandbox account(which is where the kinesis application and resources are deployed to). A good practice when setting up your AWS account structure as a minimum, essentially separating your workload accounts from the tooling/build account.

Codepipeline performs cross account deployments. The account-iam-management folder contains the cloudformation templates which creates the cross-acount assume and deploy roles required to deploy resources to the Sandbox account. This step should be done before deploying codepipeline. 

To deploy your codepipeline to aws, you first run the inception.sh script which creates the pipeline and also updates itself.  Note that the inception.sh script references the pipeline_seed-cli-parameters.json for the first time, after that codepipeline will reference the pipeline_seed.json for parameter definitions, these 2 json parameter files should be kept in sync.

The CodeBuild Spec file lives with the application source, in the /src folder of this repo.  Application Jar artifacts are deployed to an s3 bucket for Kinesis Analytics to deploy. 

<center><img src="KinesisAnalytics-CICD.png" /></center><br/>

## Application Example: Performing analytics on streaming data of Stock being traded
We will be ingesting stock trades into a kinesis data stream which an AWS Kinesis Data Analytics application will use as a source to perform calculations near real-time.  We will be scripting our own kinesis data producer in python to simulate the streaming data of stock trade prices.

The data processing application will be using the Kinesis Analytics Apache Flink runtime.  For this basic example we will make use of the Apache Flink 'max' operator over a sliding time window, to work out the max price of each stock over a 1 minute window and output to a kinesis data streams sink.

Under the data folder there is a shell script which can test consuming the processed data from the output kinesis data stream. 

