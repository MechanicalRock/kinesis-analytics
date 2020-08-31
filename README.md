# Kinesis Analytics
AWS Kinesis Data Analytics - Done the DevOps way

The CI/CD environment has been configured with a Github as a source action, so you will have to setup a webhook for push, otherwise enable polling.  The codebuild buildspec yaml is living in the /src folder of this repo, together with the application source. 

<center><img src="KinesisAnalytics-CICD.png" /></center><br/>

## Application Example: Performing analytics on streaming data of Stock being traded
We will be ingesting stock trades into a kinesis data stream which an Amazon Kinesis Data Analytics application will use a source to perform calculations near-realtime.  We will be writing our own kinesis data producer to simulate the streaming stock trade prices.

The data processing application will be using Apache Flink under the Kinesis Analytics Serverless framework.
