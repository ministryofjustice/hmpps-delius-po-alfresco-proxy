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

resource "aws_s3_bucket_object" "releases_folder" {
  bucket       = "${data.terraform_remote_state.s3_ci_test_results_bucket}"
  acl          = "private"
  key          = "releases/"
  source       = "/dev/null"
  content_type = "application/x-directory"
}