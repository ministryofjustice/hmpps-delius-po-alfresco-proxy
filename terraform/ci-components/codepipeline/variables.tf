variable "tags" {
  type    = map(string)
  default = {}
}

variable "region" {
  description = "The AWS region."
}

variable "remote_state_bucket_name" {
  description = "Terraform remote state bucket name"
}

variable "branch_name" {
  type = string
}
variable "container_name" {
  type = string
}

variable "image_name" {
  type = string
}

variable "aws_env" {
  type = string
}

variable "pre_apply_stack_commands" {
  type = string
}

variable "stack_post_build_commands" {
  type = string
}

variable "stack_pre_build_environment" {
  type = string
}

variable "target_app" {
  type = string
}


