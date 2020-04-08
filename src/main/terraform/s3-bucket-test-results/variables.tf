variable "short_environment_identifier" {
  description = "short resource label or name"
}

variable "region" {
  description = "The AWS region."
}

variable "tags" {
  type = "map"
}

variable "remote_state_bucket_name" {
  description = "Terraform remote state bucket name"
}

variable "bucket_identifier" {
  default = "spg-test-results"
}

variable "eng_remote_state_bucket_name" {}

variable "eng_role_arn" {}