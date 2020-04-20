# PO Alfresco Proxy Service
Deploys an ECS Task definition and service to the SPG shared ECS cluster.
The service listens on TCP/8080 by default and performs some validation then proxies requests to the alfresco service 

## IAM
The task runs under 2 IAM roles:

- Execution role = Executes the task, so needs to be able to pull the image, get SSM Parameters for secret env configs
- Task role = This is the role, with any policies, that apply to the running container

The shared ECS cluster is configured with the *ECS_AWSVPC_BLOCK_IMDS=true* config value to prevent tasks from reaching the host level metadata service under the host's own ec2 instance profile.

## Env Configs
Specific environment variables are set out in the service_config_map variable which is a map.
One or more of the KV pairs can be overridden in the env_configs/{ENVIRONMENT}/sub-projects/spg.tfvars file as necessary
Where no override is provided, the default value will be used.
This is a spring boot app and any spring config can be specified as environment variables. See https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html for reference.