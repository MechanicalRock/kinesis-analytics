#!/usr/bin/env python3

import sys
import json
import boto3
import random
import datetime
import time

kinesis = boto3.client('kinesis')

stocks = ['AMZN', 'GOOG', 'AZRE', 'ORCL', 'BABA']
stream = 'cloudtrading-ingest-stream'

def genData(cloudPlatform):
    data = {}
    now = datetime.datetime.now()
    str_now = now.isoformat()
    data['EVENT_TIME'] = str_now
    data['TICKER'] =  random.choice(stocks)
    price = random.random() * 100
    data['PRICE'] = round(price, 2)
    return data

tradingVolume = 0
while tradingVolume <= 50000:
    for cloudPlatform in stocks:
        data = json.dumps(genData(cloudPlatform))
        print(data)
        # kinesis.put_record(
        #     # StreamName=sys.argv[1],
        #     StreamName=stream,
        #     Data=data,
        #     PartitionKey="partitionkey")
    time.sleep(1)
    tradingVolume += 1


