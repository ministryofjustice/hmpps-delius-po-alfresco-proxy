variable "environment_identifier" {}
variable "region" {}

variable "tags" {
  type = "map"
}

variable "role_arn" {}

variable "remote_state_bucket_name" {
  description = "Terraform remote state bucket name"
}

variable "bucket_identifier" {
  default = "spg-ci-build-test-results"
}