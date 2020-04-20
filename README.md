# Distributed Gatling testing using Docker on AWS

This project is a template for loadtesting dristributed using aws ecs and docker

## Running  
To run the test you need the following 3 steps:
* Building the Docker image  
* Running the load test on AWS  
* Creating the HTML report  

### Building Docker image and push in ECR
* `aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 296553184312.dkr.ecr.us-east-1.amazonaws.com/gatling-images`
* `docker build -t <image-name> .`
* `docker tag <image-name>:latest 296553184312.dkr.ecr.us-east-1.amazonaws.com/<image-name>:latest`


### Create ECS infrastructure
you can create the infrastructure with cloudformation using the template cloud-formation-load-infra.json or manually you need to create the following resources.

* ECR repository for Gatling Docker image  
* S3 bucket for Gatling logs  
* ECS cluster for running Docker containers
* ECS task Definition

with cloudformation you neet put the following parameters:
 * PrefixName -> difference between stacks
 * DockerImage -> image of task with load test, where you push the image in ecr
 * SimulationPath -> the simulation path 
 * CidrBlockVpc -> optional have default values
 * CidrBlockSubnetA -> optional have default values
 * CidrBlockSubnetB -> optional have default values
 
### Running load test on AWS
you run the test in distributed mode using cloudformation with cloud-formation-custom-task.json, this creates a stack with custom resource for run the task and has the following inputs.
  * ServiceToken: ARN of lambda with the logic of custom task,
  * GatlingTaskDefinition: OutPut cloud-formation-load-infra.json
  * GatlingECSCluster:  OutPut cloud-formation-load-infra.json
  * CountInstance: number of instance (distributed load)
  * DefaultSecurityGroup:  OutPut cloud-formation-load-infra.json
  * PublicSubnetA:  OutPut cloud-formation-load-infra.json
  * PublicSubnetB:  OutPut cloud-formation-load-infra.json.
  
  you can run the task manually using console taskdefinition in aws.

### Creating HTML report
Generate final HTML report:
` sh generateHTMLReport.sh -r <buecket-name> -p <profile-aws-local>`
