# Kinesis Analytics
AWS Kinesis Data Analytics - Done the DevOps way

The CI/CD platformn has been configured with the Build and tooling in 1 aws account and the deployment of kinesis resources to a Sandbox account. 

Codepipeline performs cross account deployments. 

The account-management folder contains a cloudformation template which creates the roles required to deploy resources to the Sandbox account. 

## Applicatio Example: Stock Trades 
We will be ingesting stock trades into a kinesis data stream which a Amazon Kinesis Data Analytics application will use a source to perform calculations near-realtime.  We will be writing our own kinesis data producer to randomly generate stock trade prices.

The data processing application will be using Apache Flink under the Kinesis Analytics Serverless framework.
