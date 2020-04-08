terraform {
  # The configuration for this backend will be filled in by Terragrunt
  backend "s3" {}
}

provider "aws" {
  region  = "${var.region}"
  version = ">= 2.55.0"
}

############################################
# S3 bucket
############################################

# #-------------------------------------------
# ### S3 bucket for ci build test results
# #-------------------------------------------

module "s3_spg_ci_build_test_results_bucket" {
  source         = "git::https://github.com/ministryofjustice/hmpps-terraform-modules.git?ref=master//modules//s3bucket//s3bucket_without_policy"
  s3_bucket_name = "${var.short_environment_identifier}-${var.bucket_identifier}"
  tags           = "${var.tags}"
}