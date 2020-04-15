import boto3
from botocore.vendored import requests
import json

def lambda_handler(event, context):
    print('init...')
    print('event is ' + str(event))
    print('context is '+ str(context))
    lambda_response = {
        'StackId': event['StackId'],
        'RequestId': event['RequestId'],
        'LogicalResourceId': event['LogicalResourceId'],
    }
    
    status = True
    reason = ''
    resource_id = None
    
    if 'PhysicalResourceId' in event:
        resource_id = event['PhysicalResourceId']
    
    try:
        status, resource_id = event_handler(event, context)
    except Exception as e:
        status = False
        reason = "Check CloudWatch Logs for errors"
    finally:
        lambda_response['Status'] = 'SUCCESS' if status else 'FAILED'
        lambda_response['Reason'] = reason
        
        if resource_id:
            lambda_response['PhysicalResourceId'] = resource_id
        
        reply_url = event['ResponseURL']
        
        if 'Test' not in event:
            print('lambda response is: for url '+reply_url)
            print(lambda_response)
            r=requests.put(reply_url, data=json.dumps(lambda_response))
            print('response is: ')
            print(str(r.json))
            
def event_handler(event, context):
    if event['RequestType'] in ['Create', 'Update']:
        properties = event['ResourceProperties']
        print('properties is: ')
        print(properties)
       
        network_configuration = properties['NetworkConfiguration']
        awsvpc_configuration = network_configuration['AwsvpcConfiguration']
  
        ecs = boto3.client('ecs')
        try:
            response = ecs.run_task(
             cluster=properties['ClusterId'],
             taskDefinition=properties['TaskDefinition'],
             launchType=properties['LaunchType'],
             networkConfiguration={
                'awsvpcConfiguration': {
                    'subnets': awsvpc_configuration['Subnets'],
                    'securityGroups': awsvpc_configuration['SecurityGroups'],
                    'assignPublicIp': awsvpc_configuration['AssignPublicIp'],
                  }
                },
             count=int(properties['Count'])
            )       
        except Exception as e:
            print("Unexpected error was found:" + str(e))
      
        
    return True, 'N/A'