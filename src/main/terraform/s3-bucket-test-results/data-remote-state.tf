#-------------------------------------------------------------
### Getting the s3bucket
#-------------------------------------------------------------
data "terraform_remote_state" "s3_ci_test_results_bucket" {
  backend = "s3"
  config {
    bucket = "${var.eng_remote_state_bucket_name}"
    key    = "s3bucket-test-results/terraform.tfstate"
    region = "${var.region}"
    role_arn = "${var.eng_role_arn}"
  }
}