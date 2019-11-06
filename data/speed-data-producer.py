#!/usr/bin/env python3

import sys
import json
import boto3
import random
import datetime
import time

kinesis = boto3.client('kinesis')

cars = ['EVO', 'DB9', 'STI', 'TORANA', 'SCOOBY']
stream = 'racingdata-ingest-stream'

def genData(vehicle):
    data = {}
    now = datetime.datetime.now()
    str_now = now.isoformat()
    data['EVENT_TIME'] = str_now
    data['LAP_NUMBER'] =  laps + 1
    data['CAR'] = vehicle
    speed = random.random() * 100
    data['VELOCITY'] = round(speed, 2)
    return data

laps = 0
while laps <= 50000:
        for vehicle in cars:
            data = json.dumps(genData(vehicle))
            print(data)
            kinesis.put_record(
                # StreamName=sys.argv[1],
                StreamName=stream,
                Data=data,
                PartitionKey="partitionkey")
        time.sleep(1)
        laps += 1


