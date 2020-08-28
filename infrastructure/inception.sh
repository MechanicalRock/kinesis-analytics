#!/bin/bash

# set -e
# reset

# Update to use a different AWS profile
PROFILE=
STACK_NAME=stocktrading-analytics-pipeline-admin

echo "Create the initial CloudFormation Stack"
aws --profile ${PROFILE} cloudformation create-stack --stack-name ${STACK_NAME} --template-body file://pipeline_seed.yml --parameters file://pipeline_seed-cli-parameters.json --capabilities "CAPABILITY_NAMED_IAM"
echo "Waiting for the CloudFormation stack to finish being created."
aws --profile ${PROFILE} cloudformation wait stack-create-complete --stack-name ${STACK_NAME}
# Print out all the CloudFormation outputs.
aws --profile ${PROFILE} cloudformation describe-stacks --stack-name ${STACK_NAME} --output table --query "Stacks[0].Outputs"
