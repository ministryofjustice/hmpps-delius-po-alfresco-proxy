variable "ecs_instance_type" {
  description = "EC2 instance type for ECS Hosts"
  default     = "t2.medium"
}

variable "environment_name" {
  type = string
}

variable "short_environment_name" {
  type = string
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

variable "network_and_legacy_spg_remote_state_bucket_name" {
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

variable "service_config_cpu" {
  type    = string
  default = "1024"
}

variable "service_config_memory" {
  type    = string
  default = "512"
}

variable "service_config_deployment_minimum_healthy_percent" {
  type    = number
  default = 30
}

variable "service_config_ecs_target_cpu" {
  type    = string
  default = "60"
}

variable "service_config_service_port" {
  type    = number
  default = 8080
}

# ECS Task App Autoscaling min and max thresholds
variable "ecs_scaling_min_capacity" {
  default = 1
}

variable "ecs_scaling_max_capacity" {
  default = 5
}

variable "task_desired_count" {
  default = 1
}

variable "docker_image" {
  default = "895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/spgw-alfresco-proxy"
}

variable "image_version" {}

variable "internal_health_command" {
  default = "curl -s http://localhost:8080/actuator/health"
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
  default     = false
}

variable "bastion_inventory" {}