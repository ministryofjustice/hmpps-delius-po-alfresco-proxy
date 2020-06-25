variable "environment_name" {
  type = "string"
}

variable "short_environment_name" {
  type = "string"
}

variable "project_name" {
  description = "The project name - eg delius (and delius-core)"
}

variable "project_name_abbreviated" {
  description = "The abbreviated project name, e.g. dat-> delius auto test"
}

variable "environment_type" {
  description = "The environment type - e.g. dev"
}

variable "region" {
  description = "The AWS region."
}

variable "remote_state_bucket_name" {
  description = "Terraform remote state bucket name"
}

variable "environment_identifier" {
  description = "resource label or name"
}

variable "short_environment_identifier" {
  description = "shortend resource label or name"
}

variable "dependencies_bucket_arn" {
  description = "S3 bucket arn for dependencies"
}

variable "tags" {
  type = "map"
}

#load balancer account id - common accross all services within an environment
variable "lb_account_id" {}

variable "service_config_map" {
  description = "Config map for the service"
  type        = "map"

  default = {
    #standard ECS task vars
    cpu           = "1024"
    memory        = "512"

    deployment_minimum_healthy_percent = 30

    # ECS Task App Autoscaling min and max thresholds
    ecs_scaling_min_capacity = 1
    ecs_scaling_max_capacity = 5

    # ECS Task App AutoScaling will kick in above avg cpu util set here
    ecs_target_cpu = "60"

    # Task Def Env Vars
    env_service_port               = 8080
   }
}

variable "docker_image" {
  default = "895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/spgw-alfresco-proxy"
}

variable "image_version" {}

variable "internal_health_endpoint" {
  default = "http://localhost:8080/actuator/health"
}

variable "application_name" {}
variable "alfresco_health_endpoint" {}
variable "alfresco_base_url" {}

variable "cloudwatch_log_retention" {
  description = "Cloudwatch logs data retention in days"
  default     = "14"
}

variable "eng_remote_state_bucket_name" {}

variable "eng_role_arn" {}

variable "is_wiremock" {
  description = "indicator to show if an environment contains official data (prod,preprod etc)"
  default = false
}