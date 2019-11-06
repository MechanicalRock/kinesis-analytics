# kinesis-analytics-racing
AWS Kinesis Data Analytics to extract Max Speeds from racing cars

The CI/CD platformn has been configured with the Build and tooling in 1 aws account and the deployment of kinesis resources to a Sandbox account. 

Pipeline Pete's Inception pipeline was adopted for this example. 

Codepipeline performs cross account deployments. 

The account-management folder contains a cloudformation template which creates the roles required to deploy resources to the Sandbox account. 
