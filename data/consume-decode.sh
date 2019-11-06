#!/bin/bash

#AWS_PROFILE=
OUTPUT_STREAM_NAME='race-analytics-outputStream-sandbox'
# OUTPUT_STREAM_NAME='racingdata-ingest-stream'
JSON_OUTPUT=racing_stats.json

SHARD_ID=`aws --profile $AWS_PROFILE kinesis list-shards --stream-name $OUTPUT_STREAM_NAME | jq -r '.Shards[].ShardId'`
echo "Shard Id: " $SHARD_ID
echo ""

SHARD_ITERATOR=`aws --profile $AWS_PROFILE kinesis get-shard-iterator --stream-name $OUTPUT_STREAM_NAME --shard-id $SHARD_ID --shard-iterator-type LATEST | jq -r '.ShardIterator'`
echo "Shard Iterator: " $SHARD_ITERATOR
echo ""

echo "*** Consuming data from Kinesis Data Stream ${OUTPUT_STREAM_NAME} with Base64 decode ... ***"
aws --profile $AWS_PROFILE kinesis get-records --shard-iterator $SHARD_ITERATOR >> $JSON_OUTPUT

for data in `cat ${JSON_OUTPUT} | jq -r '.Records[].Data'` 
  do
    echo $data | base64 --decode
  done

rm $JSON_OUTPUT